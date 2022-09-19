package com.example.easylearn;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.BaseArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ArFragment arFragment;
    private ModelRenderable modelRenderable, xRenderable, oRenderable;
    ImageView x, o;
    View arrayView[];
    int numberOfTaps = 0;
    boolean lastWasCircle = true;

    int selected = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);

        //board = (ImageView)findViewById(R.id.board);
        x = (ImageView)findViewById(R.id.x);
        o = (ImageView)findViewById(R.id.o);

        setArrayView();
        setClickListener();

        arFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) ->
            {
                Anchor anchor = hitResult.createAnchor();
                AnchorNode anchorNode = new AnchorNode(anchor);
                anchorNode.setParent(arFragment.getArSceneView().getScene());

                createModel(anchorNode, selected);
            }
        );

        setUpPlane();

        setUpModel();

    }

    private void setClickListener() {
        for(int i = 0; i<arrayView.length; i++){
            arrayView[i].setOnClickListener(this);
        }
    }

    private void setArrayView() {
        arrayView = new View[]{
          x, o
        };
    }


    private void setUpModel() {
        ModelRenderable.builder()
                .setSource(this, R.raw.board)
                .build()
                .thenAccept(renderable -> modelRenderable = renderable)
                .exceptionally(throwable -> {
                    Toast.makeText(MainActivity.this,"Model can't be Loaded", Toast.LENGTH_SHORT).show();
                    return null;
                });

        ModelRenderable.builder()
                .setSource(this, R.raw.x)
                .build()
                .thenAccept(renderable -> xRenderable = renderable)
                .exceptionally(throwable -> {
                    Toast.makeText(MainActivity.this,"Model can't be Loaded", Toast.LENGTH_SHORT).show();
                    return null;
                });

        ModelRenderable.builder()
                .setSource(this, R.raw.o)
                .build()
                .thenAccept(renderable -> oRenderable = renderable)
                .exceptionally(throwable -> {
                    Toast.makeText(MainActivity.this,"Model can't be Loaded", Toast.LENGTH_SHORT).show();
                    return null;
                });
    }

    private void setUpPlane(){
        arFragment.setOnTapArPlaneListener(new BaseArFragment.OnTapArPlaneListener() {
            @Override
            public void onTapPlane(HitResult hitResult, Plane plane, MotionEvent motionEvent) {
                Anchor anchor = hitResult.createAnchor();
                AnchorNode anchorNode = new AnchorNode(anchor);
                anchorNode.setParent(arFragment.getArSceneView().getScene());

                createModel(anchorNode, selected);
            }


        });
    }

    private void createModel(AnchorNode anchorNode, int selected){
        if(numberOfTaps == 0) {
            TransformableNode node = new TransformableNode(arFragment.getTransformationSystem());
            node.setParent(anchorNode);
            node.setRenderable(modelRenderable);
            node.select();

            numberOfTaps ++;
            arrayView[0].setBackgroundColor(Color.parseColor("#C3FF99"));
        } else {
            if(lastWasCircle) {
                TransformableNode node = new TransformableNode(arFragment.getTransformationSystem());
                node.setParent(anchorNode);
                node.setRenderable(xRenderable);
                node.select();

                lastWasCircle = false;
                arrayView[0].setBackgroundColor(Color.TRANSPARENT);
                arrayView[1].setBackgroundColor(Color.parseColor("#C3FF99"));
            } else {
                TransformableNode node = new TransformableNode(arFragment.getTransformationSystem());
                node.setParent(anchorNode);
                node.setRenderable(oRenderable);
                node.select();

                lastWasCircle = true;
                arrayView[1].setBackgroundColor(Color.TRANSPARENT);
                arrayView[0].setBackgroundColor(Color.parseColor("#C3FF99"));
            }

            numberOfTaps ++;
        }




        /*if(selected == 1) {
            TransformableNode node = new TransformableNode(arFragment.getTransformationSystem());
            node.setParent(anchorNode);
            node.setRenderable(xRenderable);
            node.select();
        }
        if(selected == 2) {
            TransformableNode node = new TransformableNode(arFragment.getTransformationSystem());
            node.setParent(anchorNode);
            node.setRenderable(oRenderable);
            node.select();
        }*/

    }

    @Override
    public void onClick(View view) {
        /*if(view.getId() == R.id.x) {
            selected = 1;
            setBackground(view.getId());
        } else if(view.getId() == R.id.o) {
            selected = 2;
            setBackground(view.getId());
        }*/
    }

    private void setBackground(int id) {
        for(int i = 0; i < arrayView.length; i++) {
            if(arrayView[i].getId() == id)
                arrayView[i].setBackgroundColor(Color.parseColor("#C3FF99"));
                //arrayView[i].setBackgroundColor(Color.parseColor("#0C3000"));
            else
                arrayView[i].setBackgroundColor(Color.TRANSPARENT);
                //arrayView[i].setBackgroundColor(Color.parseColor("#004561"));
        }
    }
}
