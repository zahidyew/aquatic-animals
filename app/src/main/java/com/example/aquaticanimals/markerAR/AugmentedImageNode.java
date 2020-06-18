package com.example.aquaticanimals.markerAR;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.aquaticanimals.utils.SnackbarHelper;
import com.google.ar.core.AugmentedImage;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import java.util.concurrent.CompletableFuture;

/**
 * Node for rendering an augmented image. The image is framed by placing the virtual picture frame
 * at the corners of the augmented image trackable.
 */
@SuppressWarnings({"AndroidApiChecker"})
public class AugmentedImageNode extends AnchorNode {

  private static final String TAG = "AugmentedImageNode";

  // The augmented image represented by this node.
  private AugmentedImage image;

  // Add a member variable to hold the maze model.
  private Node mazeNode;

  // Add a variable called mazeRenderable for use with loading
  // GreenMaze.sfb.
  private static CompletableFuture<ModelRenderable> mazeRenderable;

  // loads GreenMaze.sfb into mazeRenderable.
  public AugmentedImageNode(Context context) {
    mazeRenderable =
            ModelRenderable.builder()
                    .setSource(context, Uri.parse("turtle.sfb"))
                    .build();
  }


  public AugmentedImageNode(Context context, String markerName) {

    //String text = "Detected Image " + markerName;
    //SnackbarHelper.getInstance().showMessage(context, text);

    String modelName = "";
    if(markerName.equals("penguin")) {
      modelName = "Mesh_Penguin.sfb";
    } else if(markerName.equals("seahorse")) {
      modelName = "seahorse.sfb";
    } else if(markerName.equals("dolphin")) {
      modelName = "dolphin.sfb";
    } else if(markerName.equals("turtle")) {
      modelName = "turtle.sfb";
    }

    if (mazeRenderable == null) {
      mazeRenderable =
              ModelRenderable.builder()
                      .setSource(context, Uri.parse(modelName))
                      .build();
    }
  }

  /**
   * Called when the AugmentedImage is detected and should be rendered. A Sceneform node tree is
   * created based on an Anchor created from the image. The corners are then positioned based on the
   * extents of the image. There is no need to worry about world coordinates since everything is
   * relative to the center of the image, which is the parent node of the corners.
   */
  @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
  // Replace the definition of the setImage function with the following
  // code, which checks if mazeRenderable has completed loading.

  public void setImage(AugmentedImage image) {
    this.image = image;

    // Initialize mazeNode and set its parents and the Renderable.
    // If any of the models are not loaded, process this function
    // until they all are loaded.
    if (!mazeRenderable.isDone()) {
      CompletableFuture.allOf(mazeRenderable)
              .thenAccept((Void aVoid) -> setImage(image))
              .exceptionally(
                      throwable -> {
                        Log.e(TAG, "Exception loading", throwable);
                        return null;
                      });
    }
    // Set the anchor based on the center of the image.
    setAnchor(image.createAnchor(image.getCenterPose()));

    mazeNode = new Node();
    mazeNode.setParent(this);
    mazeNode.setRenderable(mazeRenderable.getNow(null));
  }

  public AugmentedImage getImage() {
    return image;
  }
}
