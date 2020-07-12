package com.example.aquaticanimals.markerlessAR;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.PixelCopy;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;

import com.example.aquaticanimals.R;
import com.example.aquaticanimals.utils.Animal;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Trackable;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ViewAnimals extends AppCompatActivity {
    private static final String TAG = ViewAnimals.class.getSimpleName();
    private static final double MIN_OPENGL_VERSION = 3.0;

    private ArFragment arFragment;
    private ModelRenderable modelRenderable;
    private ViewRenderable testViewRenderable;
    private ModelLoader modelLoader;
    private String nodeName, animalClicked;
    private ConstraintLayout mainLayout;
    private FloatingActionButton infoBtn;

    @Override
    @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!checkIsSupportedDeviceOrFinish(this)) {
            return;
        }

        setContentView(R.layout.activity_view_animals);
        mainLayout = (ConstraintLayout) findViewById(R.id.activity_view_animals);

        // lock the screen to be in landscape orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
        modelLoader = new ModelLoader(new WeakReference<>(this));

        setUpFloatingButton();
        initializeGallery();
        buildModel(Uri.parse("Mesh_Penguin.sfb"));
        nodeName = "Penguin";

        // build the renderable for Text Dialog
        /*ViewRenderable.builder()
                .setView(this, R.layout.test_view)
                .build()
                .thenAccept(renderable -> testViewRenderable = renderable);*/

        arFragment.setOnTapArPlaneListener(
                (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
                    if (modelRenderable == null) {
                        return;
                    }

                    // Create the Anchor.
                    Anchor anchor = hitResult.createAnchor();
                    AnchorNode anchorNode = new AnchorNode(anchor);
                    anchorNode.setParent(arFragment.getArSceneView().getScene());

                    // Create the transformable node and add it to the anchor.
                    TransformableNode node = new TransformableNode(arFragment.getTransformationSystem());
                    node.setParent(anchorNode);
                    node.setRenderable(modelRenderable);
                    node.setName(nodeName);
                    node.select();
                    animalClicked = node.getName();
                    infoBtn.show();

                    /*TransformableNode node2 = new TransformableNode(arFragment.getTransformationSystem());
                    node2.setParent(node);
                    node2.setRenderable(testViewRenderable);*/
                    //startAnimation(node, modelRenderable);

                    modelClicked(node, modelRenderable);
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //arFragment.getArSceneView().destroy();
        arFragment.getArSceneView().getSession().close();
    }

    private void initializeGallery() {
        LinearLayout gallery = findViewById(R.id.gallery_layout);

        ImageView penguin = new ImageView(this);
        penguin.setImageResource(R.drawable.penguin_tn);
        penguin.setContentDescription("Penguin");
        penguin.setOnClickListener(view ->{
            modelLoader.loadModel(Uri.parse("Mesh_Penguin.sfb"));
            nodeName = "Penguin";
        });
        gallery.addView(penguin);

        ImageView seahorse = new ImageView(this);
        seahorse.setImageResource(R.drawable.seahorse_tn);
        seahorse.setContentDescription("Seahorse");
        seahorse.setOnClickListener(view ->{
            modelLoader.loadModel(Uri.parse("seahorse.sfb"));
            nodeName = "Seahorse";
        });
        gallery.addView(seahorse);

        ImageView dolphin = new ImageView(this);
        dolphin.setImageResource(R.drawable.dolphin_tn);
        dolphin.setContentDescription("Dolphin");
        dolphin.setOnClickListener(view ->{
            modelLoader.loadModel(Uri.parse("dolphin.sfb"));
            nodeName = "Dolphin";
        });
        gallery.addView(dolphin);

        ImageView turtle = new ImageView(this);
        turtle.setImageResource(R.drawable.turtle_tn);
        turtle.setContentDescription("Turtle");
        turtle.setOnClickListener(view ->{
            modelLoader.loadModel(Uri.parse("turtle.sfb"));
            nodeName = "Turtle";
        });
        gallery.addView(turtle);
    }

    public class ModelLoader {
        private final WeakReference<ViewAnimals> owner;
        private static final String TAG = "ModelLoader";

        ModelLoader(WeakReference<ViewAnimals> owner) {
            this.owner = owner;
        }

        void loadModel(Uri uri) {
            if (owner.get() == null) {
                Log.d(TAG, "Activity is null.  Cannot load model.");
                return;
            }
            // "ModelRenderable.builder()" is the code to build the selected model.
            // this line "modelRenderable = renderable" set the selected as the model to be rendered.
            buildModel(uri);

            return;
        }
    }

    /* Returns false and displays an error message if Sceneform can not run, true if Sceneform can run
     * on this device. */
    public static boolean checkIsSupportedDeviceOrFinish(final Activity activity) {
        if (Build.VERSION.SDK_INT < VERSION_CODES.N) {
            Log.e(TAG, "Sceneform requires Android N or later");
            Toast.makeText(activity, "Sceneform requires Android N or later", Toast.LENGTH_LONG).show();
            activity.finish();
            return false;
        }
        String openGlVersionString =
                ((ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE))
                        .getDeviceConfigurationInfo()
                        .getGlEsVersion();
        if (Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
            Log.e(TAG, "Sceneform requires OpenGL ES 3.0 later");
            Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
                    .show();
            activity.finish();
            return false;
        }
        return true;
    }

    // "ModelRenderable.builder()" is the code to build the selected model.
    // this line "modelRenderable = renderable" set the selected as the model to be rendered.
    private void buildModel(Uri uri) {
        ModelRenderable.builder()
                .setSource(ViewAnimals.this, uri)
                .build()
                .thenAccept(renderable -> modelRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast =
                                    Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });
    }

    private void modelClicked(TransformableNode node, ModelRenderable renderable) {
        if(renderable==null) {
            return;
        }

        node.setOnTapListener(
                (hitTestResult, motionEvent) -> {
                    Toast.makeText(ViewAnimals.this, node.getName(), Toast.LENGTH_SHORT).show();
                    animalClicked = node.getName();

                    /*if (node.getName().equals("Penguin")) {
                        Toast.makeText(ViewAnimals.this, node.getName(), Toast.LENGTH_SHORT).show();
                    } else if (node.getName().equals("Seahorse")) {
                        Toast.makeText(ViewAnimals.this, node.getName(), Toast.LENGTH_SHORT).show();
                    } else if (node.getName().equals("Dolphin")) {
                        Toast.makeText(ViewAnimals.this, node.getName(), Toast.LENGTH_SHORT).show();
                    } else if (node.getName().equals("Turtle")) {
                        Toast.makeText(ViewAnimals.this, node.getName(), Toast.LENGTH_SHORT).show();
                    }*/
                });
    }

    public void setUpFloatingButton() {
        FloatingActionButton fab = findViewById(R.id.fab);
        infoBtn = findViewById(R.id.infoBtn);
        infoBtn.hide();

        fab.setOnClickListener(view -> {
            takePhoto();
        });

        infoBtn.setOnClickListener(view -> {
            LayoutInflater inflater = ViewAnimals.this.getLayoutInflater();
            View animalInfo = inflater.inflate(R.layout.animal_info, mainLayout, false);
            mainLayout.addView(animalInfo);

            FloatingActionButton exitBtn = animalInfo.findViewById(R.id.exitBtn);
            TextView title = animalInfo.findViewById(R.id.title);
            TextView field1 = animalInfo.findViewById(R.id.field1);
            TextView field2 = animalInfo.findViewById(R.id.field2);
            TextView field3 = animalInfo.findViewById(R.id.field3);
            TextView field4 = animalInfo.findViewById(R.id.field4);
            TextView field5 = animalInfo.findViewById(R.id.field5);
            TextView field6 = animalInfo.findViewById(R.id.field6);
            TextView field7 = animalInfo.findViewById(R.id.field7);

            if (animalClicked.equals("Penguin")) {
                title.setText("Penguin");
                field1.setText(Animal.PENGUIN_ANIMAL_GROUP);
                field2.setText(Animal.PENGUIN_SIZE);
                field3.setText(Animal.PENGUIN_WEIGHT);
                field4.setText(Animal.PENGUIN_LIFESPAN);
                field5.setText(Animal.PENGUIN_DIET);
                field6.setText(Animal.PENGUIN_HABITAT);
                field7.setText(Animal.PENGUIN_STATUS);
            } else if (animalClicked.equals("Seahorse")) {
                title.setText("Seahorse");
                field1.setText(Animal.SEAHORSE_ANIMAL_GROUP);
                field2.setText(Animal.SEAHORSE_SIZE);
                field3.setText(Animal.SEAHORSE_WEIGHT);
                field4.setText(Animal.SEAHORSE_LIFESPAN);
                field5.setText(Animal.SEAHORSE_DIET);
                field6.setText(Animal.SEAHORSE_HABITAT);
                field7.setText(Animal.SEAHORSE_STATUS);
            } else if (animalClicked.equals("Dolphin")) {
                title.setText("Dolphin");
                field1.setText(Animal.DOLPHIN_ANIMAL_GROUP);
                field2.setText(Animal.DOLPHIN_SIZE);
                field3.setText(Animal.DOLPHIN_WEIGHT);
                field4.setText(Animal.DOLPHIN_LIFESPAN);
                field5.setText(Animal.DOLPHIN_DIET);
                field6.setText(Animal.DOLPHIN_HABITAT);
                field7.setText(Animal.DOLPHIN_STATUS);
            } else if (animalClicked.equals("Turtle")) {
                title.setText("Turtle");
                field1.setText(Animal.TURTLE_ANIMAL_GROUP);
                field2.setText(Animal.TURTLE_SIZE);
                field3.setText(Animal.TURTLE_WEIGHT);
                field4.setText(Animal.TURTLE_LIFESPAN);
                field5.setText(Animal.TURTLE_DIET);
                field6.setText(Animal.TURTLE_HABITAT);
                field7.setText(Animal.TURTLE_STATUS);
            }

            exitBtn.setOnClickListener(view2 -> {
                mainLayout.removeView(animalInfo);
                fab.show();
                infoBtn.show();
            });

            fab.hide();
            infoBtn.hide();
        });
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
                    Toast toast = Toast.makeText(ViewAnimals.this, e.toString(),
                            Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }
                //Toast.makeText(ViewAnimals.this, "Photo saved", Toast.LENGTH_LONG).show();
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
                Toast toast = Toast.makeText(ViewAnimals.this,
                        "Failed to copyPixels: " + copyResult, Toast.LENGTH_LONG);
                toast.show();
            }
            handlerThread.quitSafely();
        }, new Handler(handlerThread.getLooper()));
    }

    /*private void startAnimation(TransformableNode node, ModelRenderable renderable){
        if(renderable==null || renderable.getAnimationDataCount() == 0) {
            return;
        }
        for(int i = 0;i < renderable.getAnimationDataCount();i++){
            AnimationData animationData = renderable.getAnimationData(i);
        }
        ModelAnimator animator = new ModelAnimator(renderable.getAnimationData(0), renderable);
        animator.start();
        node.setOnTapListener(
                (hitTestResult, motionEvent) -> {
                    togglePauseAndResume(animator);
                });
    }

    public void togglePauseAndResume(ModelAnimator animator) {
        if (animator.isPaused()) {
            animator.resume();
        } else if (animator.isStarted()) {
            animator.pause();
        } else {
            animator.start();
        }
    }*/
}
