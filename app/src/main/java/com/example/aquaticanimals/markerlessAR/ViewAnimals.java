package com.example.aquaticanimals.markerlessAR;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.aquaticanimals.R;
import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Trackable;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.lang.ref.WeakReference;
import java.util.List;

public class ViewAnimals extends AppCompatActivity {
    private static final String TAG = ViewAnimals.class.getSimpleName();
    private static final double MIN_OPENGL_VERSION = 3.0;

    private ArFragment arFragment;
    private ModelRenderable modelRenderable;
    private ModelLoader modelLoader;

    @Override
    @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!checkIsSupportedDeviceOrFinish(this)) {
            return;
        }

        setContentView(R.layout.activity_view_animals);
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
        modelLoader = new ModelLoader(new WeakReference<>(this));

        initializeGallery();
        buildModel(Uri.parse("Mesh_Penguin.sfb"));

        arFragment.setOnTapArPlaneListener(
                (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
                    if (modelRenderable == null) {
                        return;
                    }

                    // Create the Anchor.
                    Anchor anchor = hitResult.createAnchor();
                    AnchorNode anchorNode = new AnchorNode(anchor);
                    anchorNode.setParent(arFragment.getArSceneView().getScene());

                    // Create the transformable andy and add it to the anchor.
                    TransformableNode andy = new TransformableNode(arFragment.getTransformationSystem());
                    andy.setParent(anchorNode);
                    andy.setRenderable(modelRenderable);
                    andy.select();
                });
    }

    private void initializeGallery() {
        LinearLayout gallery = findViewById(R.id.gallery_layout);

        ImageView penguin = new ImageView(this);
        penguin.setImageResource(R.drawable.penguin_tn);
        penguin.setContentDescription("Penguin");
        penguin.setOnClickListener(view ->{modelLoader.loadModel(Uri.parse("Mesh_Penguin.sfb"));});
        gallery.addView(penguin);

        ImageView seahorse = new ImageView(this);
        seahorse.setImageResource(R.drawable.seahorse_tn);
        seahorse.setContentDescription("Seahorse");
        seahorse.setOnClickListener(view ->{modelLoader.loadModel(Uri.parse("seahorse.sfb"));});
        gallery.addView(seahorse);

        ImageView dolphin = new ImageView(this);
        dolphin.setImageResource(R.drawable.dolphin_tn);
        dolphin.setContentDescription("Dolphin");
        dolphin.setOnClickListener(view ->{modelLoader.loadModel(Uri.parse("dolphin.sfb"));});
        gallery.addView(dolphin);

        ImageView turtle = new ImageView(this);
        turtle.setImageResource(R.drawable.turtle_tn);
        turtle.setContentDescription("Turtle");
        turtle.setOnClickListener(view ->{modelLoader.loadModel(Uri.parse("turtle.sfb"));});
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
}
