package com.example.aquaticanimals.markerAR;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.example.aquaticanimals.R;
import com.example.aquaticanimals.utils.SnackbarHelper;
import com.google.ar.core.AugmentedImage;
import com.google.ar.core.Frame;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.ux.ArFragment;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * This application demonstrates using augmented images to place anchor nodes. app to include image
 * tracking functionality.
 *
 * <p>In this example, we assume all images are static or moving slowly with a large occupation of
 * the screen. If the target is actively moving, we recommend to check
 * ArAugmentedImage_getTrackingMethod() and render only when the tracking method equals to
 * AR_AUGMENTED_IMAGE_TRACKING_METHOD_FULL_TRACKING. See details in <a
 * href="https://developers.google.com/ar/develop/c/augmented-images/">Recognize and Augment
 * Images</a>.
 */
public class AugmentedImageActivity extends AppCompatActivity {

  private ArFragment arFragment;
  private ImageView fitToScanView;

  // Augmented image and its associated center pose anchor, keyed by the augmented image in
  // the database.
  private final Map<AugmentedImage, AugmentedImageNode> augmentedImageMap = new HashMap<>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_augmented_image);

    arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
    fitToScanView = findViewById(R.id.image_view_fit_to_scan);

    arFragment.getArSceneView().getScene().addOnUpdateListener(this::onUpdateFrame);
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (augmentedImageMap.isEmpty()) {
      fitToScanView.setVisibility(View.VISIBLE);
    }
  }

  /**
   * Registered with the Sceneform Scene object, this method is called at the start of each frame.
   *
   * @param frameTime - time since last frame.
   */
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
          String text = "Detected Image " + augmentedImage.getIndex();
          SnackbarHelper.getInstance().showMessage(this, text);
          break;

        case TRACKING:
          // Have to switch to UI Thread to update View.
          fitToScanView.setVisibility(View.GONE);

          // Create a new anchor for newly found images.
          if (!augmentedImageMap.containsKey(augmentedImage)) {
            //text = "Detected Image " + augmentedImage.getName();
            //SnackbarHelper.getInstance().showMessage(this, text);
            AugmentedImageNode node = new AugmentedImageNode(this, augmentedImage.getName());
            node.setImage(augmentedImage);
            augmentedImageMap.put(augmentedImage, node);
            arFragment.getArSceneView().getScene().addChild(node);
          }
          break;

        case STOPPED:
          augmentedImageMap.remove(augmentedImage);
          break;
      }
    }
    /*for (AugmentedImage img : updatedAugmentedImages) {
      // Developers can:
      // 1. Check tracking state.
      // 2. Render something based on the pose, or attach an anchor.
      if (img.getTrackingState() == TrackingState.TRACKING) {
        // Use getTrackingMethod() to determine whether the image is currently
        // being tracked by the camera.
        if (img.getTrackingMethod() == AugmentedImage.TrackingMethod.LAST_KNOWN_POSE) {
          // The planar target is currently being tracked based on its last
          // known pose.
        } else    // (getTrackingMethod() == TrackingMethod.FULL_TRACKING)
        {
          // The planar target is being tracked using the current camera image.
        }
        // You can also check which image this is based on getName().
        if (img.getName().equals("penguin")) {
          String name = "penguin";
          AugmentedImageNode node = new AugmentedImageNode(this, name);
          node.setImage(img);
          augmentedImageMap.put(img, node);
          arFragment.getArSceneView().getScene().addChild(node);
        }
        else if (img.getName().equals("dolphin")) {
          String name = "dolphin";
          AugmentedImageNode node = new AugmentedImageNode(this, name);
          node.setImage(img);
          augmentedImageMap.put(img, node);
          arFragment.getArSceneView().getScene().addChild(node);
        }
        else if (img.getName().equals("seahorse")) {
          String name = "seahorse";
          AugmentedImageNode node = new AugmentedImageNode(this, name);
          node.setImage(img);
          augmentedImageMap.put(img, node);
          arFragment.getArSceneView().getScene().addChild(node);
        }
        else if (img.getName().equals("turtle")) {
          String name = "turtle";
          AugmentedImageNode node = new AugmentedImageNode(this, name);
          node.setImage(img);
          augmentedImageMap.put(img, node);
          arFragment.getArSceneView().getScene().addChild(node);
        }
      }
    }*/

    /*for (AugmentedImage augmentedImage : updatedAugmentedImages) {
      switch (augmentedImage.getTrackingState()) {
        case PAUSED:
          // When an image is in PAUSED state, but the camera is not PAUSED, it has been detected,
          // but not yet tracked.
          String text = "Detected Image " + augmentedImage.getIndex();
          SnackbarHelper.getInstance().showMessage(this, text);
          break;

        case TRACKING:
          // Have to switch to UI Thread to update View.
          fitToScanView.setVisibility(View.GONE);

          // Create a new anchor for newly found images.
          if (!augmentedImageMap.containsKey(augmentedImage)) {
            AugmentedImageNode node = new AugmentedImageNode(this);
            node.setImage(augmentedImage);
            augmentedImageMap.put(augmentedImage, node);
            arFragment.getArSceneView().getScene().addChild(node);
          }
          break;

        case STOPPED:
          augmentedImageMap.remove(augmentedImage);
          break;
      }
    }*/
  }
}
