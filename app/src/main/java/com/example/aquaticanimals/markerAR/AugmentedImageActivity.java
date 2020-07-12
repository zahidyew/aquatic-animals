package com.example.aquaticanimals.markerAR;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.LayoutInflater;
import android.view.PixelCopy;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aquaticanimals.R;
import com.example.aquaticanimals.utils.Animal;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.ar.core.AugmentedImage;
import com.google.ar.core.Frame;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.ux.ArFragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/* This application demonstrates using augmented images to place anchor nodes. app to include image
 * tracking functionality.
 *
 * Here, we assume all images are static or moving slowly with a large occupation of
 * the screen. If the target is actively moving, we recommend to check
 * ArAugmentedImage_getTrackingMethod() and render only when the tracking method equals to
 * AR_AUGMENTED_IMAGE_TRACKING_METHOD_FULL_TRACKING. . */
public class AugmentedImageActivity extends AppCompatActivity {

  private ArFragment arFragment;
  private ImageView fitToScanView;
  private AugmentedImageNode node;
  private ConstraintLayout mainLayout;
  private FloatingActionButton infoBtn;
  private String scannedAnimal;

  // Augmented image and its associated center pose anchor, keyed by the augmented image in the database.
  private final Map<AugmentedImage, AugmentedImageNode> augmentedImageMap = new HashMap<>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_augmented_image);
    mainLayout = findViewById(R.id.activity_augmented_image);
    arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
    fitToScanView = findViewById(R.id.image_view_fit_to_scan);
    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

    arFragment.getArSceneView().getScene().addOnUpdateListener(this::onUpdateFrame);
    setUpFloatingButton();
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (augmentedImageMap.isEmpty()) {
      fitToScanView.setVisibility(View.VISIBLE);
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    arFragment.getArSceneView().getSession().close();
  }

  /* Registered with the Sceneform Scene object, this method is called at the start of each frame.
     @param frameTime - time since last frame. */
  private void onUpdateFrame(FrameTime frameTime) {
    Frame frame = arFragment.getArSceneView().getArFrame();

    // If there is no frame, just return.
    if (frame == null) {
      return;
    }

    Collection<AugmentedImage> updatedAugmentedImages =
        frame.getUpdatedTrackables(AugmentedImage.class);

    for (AugmentedImage augmentedImage : updatedAugmentedImages) {
      switch (augmentedImage.getTrackingState()) {
        case PAUSED:
          // When an image is in PAUSED state, but the camera is not PAUSED, it has been detected,
          // but not yet tracked.
          String text = "Detected Image: " + augmentedImage.getName();
          Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
          //SnackbarHelper.getInstance().showMessage(this, text);
          break;

        case TRACKING:
          // Have to switch to UI Thread to update View.
          fitToScanView.setVisibility(View.GONE);

          // Create a new anchor for newly found images.
          if (!augmentedImageMap.containsKey(augmentedImage)) {
            node = new AugmentedImageNode(this, augmentedImage.getName());
            Toast.makeText(getApplicationContext(), augmentedImage.getName(), Toast.LENGTH_SHORT).show();
            node.setImage(augmentedImage);
            augmentedImageMap.put(augmentedImage, node);
            arFragment.getArSceneView().getScene().addChild(node);

            scannedAnimal = augmentedImage.getName();
            infoBtn.setVisibility(View.VISIBLE);
          }
          break;

        case STOPPED:
          augmentedImageMap.remove(augmentedImage);
          break;
      }
    }
  }

  public void setUpFloatingButton() {
    FloatingActionButton fab = findViewById(R.id.fab);
    infoBtn = findViewById(R.id.infoBtn);
    infoBtn.setVisibility(View.INVISIBLE);

    fab.setOnClickListener(view -> {
      takePhoto();
    });

    infoBtn.setOnClickListener(view -> {
      LayoutInflater inflater = AugmentedImageActivity.this.getLayoutInflater();
      View animalFacts = inflater.inflate(R.layout.animal_facts, mainLayout, false);
      mainLayout.addView(animalFacts);

      FloatingActionButton exitBtn = animalFacts.findViewById(R.id.exitBtn);
      setFacts(animalFacts);

      exitBtn.setOnClickListener(view2 -> {
        mainLayout.removeView(animalFacts);

        infoBtn.setVisibility(View.VISIBLE);
        fab.setVisibility(View.VISIBLE);
      });

      fab.setVisibility(View.INVISIBLE);
      infoBtn.setVisibility(View.INVISIBLE);
    });
  }

  private void setFacts(View animalFacts) {
    TextView title = animalFacts.findViewById(R.id.title);
    TextView facts = animalFacts.findViewById(R.id.facts);

    if(scannedAnimal.equals("penguin")) {
      title.setText("Penguin");
      facts.setText(Animal.PENGUIN_FACTS);
    } else if(scannedAnimal.equals("seahorse")) {
      title.setText("Seahorse");
      facts.setText(Animal.SEAHORSE_FACTS);
    } else if(scannedAnimal.equals("dolphin")) {
      title.setText("Dolphin");
      facts.setText(Animal.DOLPHIN_FACTS);
    } else if(scannedAnimal.equals("turtle")) {
      title.setText("Turtle");
      facts.setText(Animal.TURTLE_FACTS);
    }
  }

  private String generateFilename() {
    String date =
            new SimpleDateFormat("yyyyMMddHHmmss", java.util.Locale.getDefault()).format(new Date());
    return Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES) + File.separator + "Sceneform/" + date + "_screenshot.jpg";
  }

  private void saveBitmapToDisk(Bitmap bitmap, String filename) throws IOException {

    File out = new File(filename);
    if (!out.getParentFile().exists()) {
      out.getParentFile().mkdirs();
    }
    try (FileOutputStream outputStream = new FileOutputStream(filename);
         ByteArrayOutputStream outputData = new ByteArrayOutputStream()) {
      bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputData);
      outputData.writeTo(outputStream);
      outputStream.flush();
      outputStream.close();
    } catch (IOException ex) {
      throw new IOException("Failed to save bitmap to disk", ex);
    }
  }

  private void takePhoto() {
    final String filename = generateFilename();
    ArSceneView view = arFragment.getArSceneView();

    // Create a bitmap the size of the scene view.
    final Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),
            Bitmap.Config.ARGB_8888);

    // Create a handler thread to offload the processing of the image.
    final HandlerThread handlerThread = new HandlerThread("PixelCopier");
    handlerThread.start();
    // Make the request to copy.
    PixelCopy.request(view, bitmap, (copyResult) -> {
      if (copyResult == PixelCopy.SUCCESS) {
        try {
          saveBitmapToDisk(bitmap, filename);
        } catch (IOException e) {
          Toast toast = Toast.makeText(AugmentedImageActivity.this, e.toString(),
                  Toast.LENGTH_LONG);
          toast.show();
          return;
        }
        //Toast.makeText(AugmentedImageActivity.this, "Photo saved", Toast.LENGTH_LONG).show();
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                "Photo saved", Snackbar.LENGTH_LONG);
                /*snackbar.setAction("Open in Photos", v -> {
                    File photoFile = new File(filename);

                    Uri photoURI = FileProvider.getUriForFile(ViewAnimals.this,
                            ViewAnimals.this.getPackageName(),
                            photoFile);
                    Intent intent = new Intent(Intent.ACTION_VIEW, photoURI);
                    intent.setDataAndType(photoURI, "image/*");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);

                });*/
        snackbar.show();
      } else {
        Toast toast = Toast.makeText(AugmentedImageActivity.this,
                "Failed to copyPixels: " + copyResult, Toast.LENGTH_LONG);
        toast.show();
      }
      handlerThread.quitSafely();
    }, new Handler(handlerThread.getLooper()));
  }
}
