package com.example.easylearn;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.ar.core.Anchor;
import com.google.ar.core.Config;
import com.google.ar.core.Config.InstantPlacementMode;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.PlaybackStatus;
import com.google.ar.core.RecordingConfig;
import com.google.ar.core.RecordingStatus;
import com.google.ar.core.Session;
import common.helpers.TapHelper;
import common.helpers.InstantPlacementSettings;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.PlaybackFailedException;
import com.google.ar.core.exceptions.RecordingFailedException;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.BaseArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;



public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ArFragment arFragment;
    private ModelRenderable modelRenderable, xRenderable, oRenderable;
    ImageView x, o;
    View arrayView[];
    int numberOfTaps = 0;
    boolean lastWasCircle = true;
    int selected = 1;
    private GLSurfaceView surfaceView;
    private TapHelper tapHelper;

    public enum AppState {
        Idle,
        Recording,
        Playingback
    }
    private AppState appState = AppState.Idle;

    private final String MP4_VIDEO_MIME_TYPE = "video/mp4";
    private int REQUEST_MP4_SELECTOR = 1;
    private boolean hasSetTextureNames = false;

    private final InstantPlacementSettings instantPlacementSettings = new InstantPlacementSettings();

    private static final String TAG = MainActivity.class.getSimpleName();

    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        surfaceView = findViewById(R.id.surfaceview);

        tapHelper = new TapHelper(/*context=*/ this);
        surfaceView.setOnTouchListener(tapHelper);

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


    //ARCore default code related to "Record" and "Playback buttons":

    /** Configures the session with feature settings. */
    private void configureSession() {
        Config config = session.getConfig();
        config.setLightEstimationMode(Config.LightEstimationMode.ENVIRONMENTAL_HDR);
        if (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
            config.setDepthMode(Config.DepthMode.AUTOMATIC);
        } else {
            config.setDepthMode(Config.DepthMode.DISABLED);
        }
        if (instantPlacementSettings.isInstantPlacementEnabled()) {
            config.setInstantPlacementMode(InstantPlacementMode.LOCAL_Y_UP);
        } else {
            config.setInstantPlacementMode(InstantPlacementMode.DISABLED);
        }

        session.configure(config);
    }

    // Update the "Record" button based on app's internal state.
  private void updateRecordButton() {
    View buttonView = findViewById(R.id.record_button);
    Button button = (Button) buttonView;

    switch (appState) {
      // The app is neither recording nor playing back. The "Record" button is visible.
      case Idle:
        button.setText("Record");
        button.setVisibility(View.VISIBLE);
        break;

      // While recording, the "Record" button is visible and says "Stop".
      case Recording:
        button.setText("Stop");
        button.setVisibility(View.VISIBLE);
        break;

      // During playback, the "Record" button is not visible.
      case Playingback:
        button.setVisibility(View.INVISIBLE);
        break;
    }
  }

  // Handle the "Record" button click event.
  public void onClickRecord(View view) {
    Log.d(TAG, "onClickRecord");

    // Check the app's internal state and switch to the new state if needed.
    switch (appState) {
      // If the app is not recording, begin recording.
      case Idle: {
        boolean hasStarted = startRecording();
        Log.d(TAG, String.format("onClickRecord start: hasStarted %b", hasStarted));

        if (hasStarted)
          appState = AppState.Recording;

        break;
      }

      // If the app is recording, stop recording.
      case Recording: {
        boolean hasStopped = stopRecording();
        Log.d(TAG, String.format("onClickRecord stop: hasStopped %b", hasStopped));

        if (hasStopped)
          appState = AppState.Idle;

        break;
      }

      default:
        // Do nothing.
        break;
    }

    updateRecordButton();
    updatePlaybackButton();
  }

  private boolean startRecording() {
    Uri mp4FileUri = createMp4File();
    if (mp4FileUri == null)
      return false;

    Log.d(TAG, "startRecording at: " + mp4FileUri);

    pauseARCoreSession();

    // Configure the ARCore session to start recording.
    RecordingConfig recordingConfig = new RecordingConfig(session)
            .setMp4DatasetUri(mp4FileUri)
            .setAutoStopOnPause(true);

    try {
      // Prepare the session for recording, but do not start recording yet.
      session.startRecording(recordingConfig);
    } catch (RecordingFailedException e) {
      Log.e(TAG, "startRecording - Failed to prepare to start recording", e);
      return false;
    }

    boolean canResume = resumeARCoreSession();
    if (!canResume)
      return false;

    // Correctness checking: check the ARCore session's RecordingState.
    RecordingStatus recordingStatus = session.getRecordingStatus();
    Log.d(TAG, String.format("startRecording - recordingStatus %s", recordingStatus));
    return recordingStatus == RecordingStatus.OK;
  }

  private void pauseARCoreSession() {
    // Pause the GLSurfaceView so that it doesn't update the ARCore session.
    // Pause the ARCore session so that we can update its configuration.
    // If the GLSurfaceView is not paused,
    //   onDrawFrame() will try to update the ARCore session
    //   while it's paused, resulting in a crash.
    surfaceView.onPause();
    session.pause();
  }

  private boolean resumeARCoreSession() {
    // We must resume the ARCore session before the GLSurfaceView.
    // Otherwise, the GLSurfaceView will try to update the ARCore session.
    try {
      session.resume();
    } catch (CameraNotAvailableException e) {
      Log.e(TAG, "CameraNotAvailableException in resumeARCoreSession", e);
      return false;
    }

    surfaceView.onResume();
    return true;
  }

  private boolean stopRecording() {
    try {
      session.stopRecording();
    } catch (RecordingFailedException e) {
      Log.e(TAG, "stopRecording - Failed to stop recording", e);
      return false;
    }

    // Correctness checking: check if the session stopped recording.
    return session.getRecordingStatus() == RecordingStatus.NONE;
  }

  private Uri createMp4File() {
    // Since we use legacy external storage for Android 10,
    // we still need to request for storage permission on Android 10.
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
      if (!checkAndRequestStoragePermission()) {
        Log.i(TAG, String.format(
                "Didn't createMp4File. No storage permission, API Level = %d",
                Build.VERSION.SDK_INT));
        return null;
      }
    }

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
    String mp4FileName = "arcore-" + dateFormat.format(new Date()) + ".mp4";

    ContentResolver resolver = this.getContentResolver();

    Uri videoCollection = null;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      videoCollection = MediaStore.Video.Media.getContentUri(
              MediaStore.VOLUME_EXTERNAL_PRIMARY);
    } else {
      videoCollection = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
    }

    // Create a new Media file record.
    ContentValues newMp4FileDetails = new ContentValues();
    newMp4FileDetails.put(MediaStore.Video.Media.DISPLAY_NAME, mp4FileName);
    newMp4FileDetails.put(MediaStore.Video.Media.MIME_TYPE, MP4_VIDEO_MIME_TYPE);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      // The Relative_Path column is only available since API Level 29.
      newMp4FileDetails.put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES);
    } else {
      // Use the Data column to set path for API Level <= 28.
      File mp4FileDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
      String absoluteMp4FilePath = new File(mp4FileDir, mp4FileName).getAbsolutePath();
      newMp4FileDetails.put(MediaStore.Video.Media.DATA, absoluteMp4FilePath);
    }

    Uri newMp4FileUri = resolver.insert(videoCollection, newMp4FileDetails);

    // Ensure that this file exists and can be written.
    if (newMp4FileUri == null) {
      Log.e(TAG, String.format("Failed to insert Video entity in MediaStore. API Level = %d", Build.VERSION.SDK_INT));
      return null;
    }

    // This call ensures the file exist before we pass it to the ARCore API.
    if (!testFileWriteAccess(newMp4FileUri)) {
      return null;
    }

    Log.d(TAG, String.format("createMp4File = %s, API Level = %d", newMp4FileUri, Build.VERSION.SDK_INT));

    return newMp4FileUri;
  }

  // Test if the file represented by the content Uri can be open with write access.
  private boolean testFileWriteAccess(Uri contentUri) {
    try (java.io.OutputStream mp4File = this.getContentResolver().openOutputStream(contentUri)) {
      Log.d(TAG, String.format("Success in testFileWriteAccess %s", contentUri.toString()));
      return true;
    } catch (java.io.FileNotFoundException e) {
      Log.e(TAG, String.format("FileNotFoundException in testFileWriteAccess %s", contentUri.toString()), e);
    } catch (java.io.IOException e) {
      Log.e(TAG, String.format("IOException in testFileWriteAccess %s", contentUri.toString()), e);
    }

    return false;
  }

  private final int REQUEST_WRITE_EXTERNAL_STORAGE = 1;
  public boolean checkAndRequestStoragePermission() {
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(this,
              new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
              REQUEST_WRITE_EXTERNAL_STORAGE);
      return false;
    }

    return true;
  }

  // Update the "Playback" button based on app's internal state.
  private void updatePlaybackButton() {
    View buttonView = findViewById(R.id.playback_button);
    Button button = (Button)buttonView;

    switch (appState) {

      // The app is neither recording nor playing back. The "Playback" button is visible.
      case Idle:
        button.setText("Playback");
        button.setVisibility(View.VISIBLE);
        break;

      // While playing back, the "Playback" button is visible and says "Stop".
      case Playingback:
        button.setText("Stop");
        button.setVisibility(View.VISIBLE);
        break;

      // During recording, the "Playback" button is not visible.
      case Recording:
        button.setVisibility(View.INVISIBLE);
        break;
    }
  }

  // Handle the click event of the "Playback" button.
  public void onClickPlayback(View view) {
    Log.d(TAG, "onClickPlayback");

    switch (appState) {

      // If the app is not playing back, open the file picker.
      case Idle: {
        boolean hasStarted = selectFileToPlayback();
        Log.d(TAG, String.format("onClickPlayback start: selectFileToPlayback %b", hasStarted));
        break;
      }

      // If the app is playing back, stop playing back.
      case Playingback: {
        boolean hasStopped = stopPlayingback();
        Log.d(TAG, String.format("onClickPlayback stop: hasStopped %b", hasStopped));
        break;
      }

      default:
        // Recording - do nothing.
        break;
    }

    // Update the UI for the "Record" and "Playback" buttons.
    updateRecordButton();
    updatePlaybackButton();
  }

  private boolean selectFileToPlayback() {
    // Start file selection from Movies directory.
    // Android 10 and above requires VOLUME_EXTERNAL_PRIMARY to write to MediaStore.
    Uri videoCollection;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      videoCollection = MediaStore.Video.Media.getContentUri(
              MediaStore.VOLUME_EXTERNAL_PRIMARY);
    } else {
      videoCollection = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
    }

    // Create an Intent to select a file.
    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

    // Add file filters such as the MIME type, the default directory and the file category.
    intent.setType(MP4_VIDEO_MIME_TYPE); // Only select *.mp4 files
    intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, videoCollection); // Set default directory
    intent.addCategory(Intent.CATEGORY_OPENABLE); // Must be files that can be opened

    this.startActivityForResult(intent, REQUEST_MP4_SELECTOR);

    return true;
  }

  // Begin playback once the user has selected the file.
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    // Check request status. Log an error if the selection fails.
    if (resultCode != android.app.Activity.RESULT_OK || requestCode != REQUEST_MP4_SELECTOR) {
      Log.e(TAG, "onActivityResult select file failed");
      return;
    }

    Uri mp4FileUri = data.getData();
    Log.d(TAG, String.format("onActivityResult result is %s", mp4FileUri));

    // Begin playback.
    startPlayingback(mp4FileUri);
  }

  private boolean startPlayingback(Uri mp4FileUri) {
    if (mp4FileUri == null)
      return false;

    Log.d(TAG, "startPlayingback at:" + mp4FileUri);

    pauseARCoreSession();

    try {
      session.setPlaybackDatasetUri(mp4FileUri);
    } catch (PlaybackFailedException e) {
      Log.e(TAG, "startPlayingback - setPlaybackDataset failed", e);
    }

    // The session's camera texture name becomes invalid when the
    // ARCore session is set to play back.
    // Workaround: Reset the Texture to start Playback
    // so it doesn't crashes with AR_ERROR_TEXTURE_NOT_SET.
    hasSetTextureNames = false;

    boolean canResume = resumeARCoreSession();
    if (!canResume)
      return false;

    PlaybackStatus playbackStatus = session.getPlaybackStatus();
    Log.d(TAG, String.format("startPlayingback - playbackStatus %s", playbackStatus));

    if (playbackStatus != PlaybackStatus.OK) { // Correctness check
      return false;
    }

    appState = AppState.Playingback;
    updateRecordButton();
    updatePlaybackButton();

    return true;
  }

  // Stop the current playback, and restore app status to Idle.
  private boolean stopPlayingback() {
    // Correctness check, only stop playing back when the app is playing back.
    if (appState != AppState.Playingback)
      return false;

    pauseARCoreSession();

    // Close the current session and create a new session.
    session.close();
    try {
      session = new Session(this);
    } catch (UnavailableArcoreNotInstalledException
            | UnavailableApkTooOldException
            | UnavailableSdkTooOldException
            | UnavailableDeviceNotCompatibleException e) {
      Log.e(TAG, "Error in return to Idle state. Cannot create new ARCore session", e);
      return false;
    }
    configureSession();

    boolean canResume = resumeARCoreSession();
    if (!canResume)
      return false;

    // A new session will not have a camera texture name.
    // Manually set hasSetTextureNames to false to trigger a reset.
    hasSetTextureNames = false;

    // Reset appState to Idle, and update the "Record" and "Playback" buttons.
    appState = AppState.Idle;
    updateRecordButton();
    updatePlaybackButton();

    return true;
  }
/*
  //NEWCODE
  @RequiresApi(api = Build.VERSION_CODES.N)
  private void setUpModel() {
    ModelRenderable.builder()
            .setSource(this, R.raw.board)
            .build()
            .thenAccept(renderable -> modelRenderable = renderable)
            .exceptionally(throwable -> {
              Toast.makeText(HelloArActivity.this,"Model can't be Loaded", Toast.LENGTH_SHORT).show();
              return null;
            });

    ModelRenderable.builder()
            .setSource(this, R.raw.x)
            .build()
            .thenAccept(renderable -> xRenderable = renderable)
            .exceptionally(throwable -> {
              Toast.makeText(HelloArActivity.this,"Model can't be Loaded", Toast.LENGTH_SHORT).show();
              return null;
            });

    ModelRenderable.builder()
            .setSource(this, R.raw.o)
            .build()
            .thenAccept(renderable -> oRenderable = renderable)
            .exceptionally(throwable -> {
              Toast.makeText(HelloArActivity.this,"Model can't be Loaded", Toast.LENGTH_SHORT).show();
              return null;
            });
  }

  private void createModel(AnchorNode anchorNode, int selected) {
    //if (numberOfTaps == 0) {
    /*
      TransformableNode node = new TransformableNode(arFragment.getTransformationSystem());
      node.setParent(anchorNode);
      node.setRenderable(modelRenderable);
      node.select();

     */

    //numberOfTaps++;
    //arrayView[0].setBackgroundColor(Color.parseColor("#C3FF99"));
    /*} else {
      if (lastWasCircle) {
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

      numberOfTaps++;
    }*/
}
