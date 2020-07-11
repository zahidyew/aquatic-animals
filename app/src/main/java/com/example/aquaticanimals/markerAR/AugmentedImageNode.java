package com.example.aquaticanimals.markerAR;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.TextView;

import com.example.aquaticanimals.R;
import com.google.ar.core.AugmentedImage;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;

import java.util.concurrent.CompletableFuture;

 /* Node for rendering an augmented image. The image is framed by placing the virtual picture frame
 * at the corners of the augmented image trackable. */
@SuppressWarnings({"AndroidApiChecker"})
public class AugmentedImageNode extends AnchorNode {

  private static final String TAG = "AugmentedImageNode";
  private AugmentedImage image;
  private Node animalNode;
  private String modelName, animalName;
  private Context context;
  private float yPos;
  // Add a variable called animalRenderable for use to load the sfb files
  private static CompletableFuture<ModelRenderable> animalRenderable;

  public AugmentedImageNode(Context context, String markerName) {
    this.context = context;

    if(markerName.equals("penguin")) {
      modelName = "Mesh_Penguin.sfb";
      animalName = "Penguin";
      yPos = 0.15f;
    } else if(markerName.equals("seahorse")) {
      modelName = "seahorse.sfb";
      animalName = "Seahorse";
      yPos = 0.15f;
    } else if(markerName.equals("dolphin")) {
      modelName = "dolphin.sfb";
      animalName = "Dolphin";
      yPos = 0.2f;
    } else if(markerName.equals("turtle")) {
      modelName = "turtle.sfb";
      animalName = "Turtle";
      yPos = 0.10f;
    }

    animalRenderable = ModelRenderable.builder()
                      .setSource(context, Uri.parse(modelName))
                      .build();
  }


  @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
  // Initialize animalNode and set its parents and the Renderable.
  // If any of the models are not loaded, process this function until they all are loaded.
  public void setImage(AugmentedImage image) {
    this.image = image;

    if (!animalRenderable.isDone()) {
      CompletableFuture.allOf(animalRenderable)
              .thenAccept((Void aVoid) -> setImage(image))
              .exceptionally(
                      throwable -> {
                        Log.e(TAG, "Exception loading", throwable);
                        return null;
                      });
    }
    // Set the anchor based on the center of the image.
    setAnchor(image.createAnchor(image.getCenterPose()));

    animalNode = new Node();
    animalNode.setParent(this);
    animalNode.setName(animalName);
    animalNode.setRenderable(animalRenderable.getNow(null));

    Node textNode = new Node();
    textNode.setParent(animalNode);
    textNode.setLocalPosition(new Vector3(0.0f, yPos, 0.0f));
    ViewRenderable.builder()
            .setView(context, R.layout.test_view)
            .build()
            .thenAccept(renderable -> {
                textNode.setRenderable(renderable);
                TextView textView = (TextView) renderable.getView();
                textView.setText(animalNode.getName());
            });
  }

  public AugmentedImage getImage() {
    return image;
  }
}
