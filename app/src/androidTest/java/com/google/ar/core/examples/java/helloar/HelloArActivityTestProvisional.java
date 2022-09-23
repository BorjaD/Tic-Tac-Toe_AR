package com.google.ar.core.examples.java.helloar;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.PixelCopy;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.CoordinatesProvider;
import androidx.test.espresso.action.GeneralClickAction;
import androidx.test.espresso.action.Press;
import androidx.test.espresso.action.Tap;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.runner.lifecycle.Stage;
import androidx.test.uiautomator.UiDevice;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.IntBuffer;
import java.util.Collection;

import javax.microedition.khronos.opengles.GL10;
import javax.security.auth.callback.Callback;

//

@LargeTest
@RunWith(AndroidJUnit4.class)
public class HelloArActivityTestProvisional {

    @Rule
    public ActivityTestRule<HelloArActivity> mActivityTestRule = new ActivityTestRule<>(HelloArActivity.class);

    @Test
    public void helloArActivityTest() throws InterruptedException {
        //wait(2000);
        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.playback_button), withText("Playback"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        appCompatButton.perform(click());

        // Click on the recorded video
        // Long tap (select) on a video in the gallery
        //First video
        /*UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).click(100, 1500);
        Thread.sleep(1000);*/
        //Second video
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).click(800, 1500);
        Thread.sleep(1000);
        // Tap on SELECT
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).click(900, 300);
        Thread.sleep(40000);

        //Tap the screen to place an item
        //onView(withId(R.id.surfaceview)).perform(touchDownAndUp(50, 50));
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).click(300, 1500);
        Thread.sleep(3000);
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).click(600, 1500);
        Thread.sleep(3000);
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).click(400, 1000);
        Thread.sleep(3000);
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).click(500, 1000);
        Thread.sleep(2000);

        //TEST CASE 2: The item remains in place after moving the camera
        File screenshot1 = takeScreenshot();
        Thread.sleep(5000);
        File screenshot2 = takeScreenshot();

        float diffPxProportion = getDiffPxProportion(screenshot1, screenshot2);
        boolean wholeViewChanged = wholeViewHasChanged(diffPxProportion);
        //assertTrue(wholeViewChanged);
        //assertFalse(wholeViewChanged);      //Imágenes en negro. Solo para Debug

        Thread.sleep(5000);


    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

    public static ViewAction clickXY(final int x, final int y){
        return new GeneralClickAction(
                Tap.SINGLE,
                new CoordinatesProvider() {
                    @Override
                    public float[] calculateCoordinates(View view) {

                        final int[] screenPos = new int[2];
                        view.getLocationOnScreen(screenPos);

                        final float screenX = screenPos[0] + x;
                        final float screenY = screenPos[1] + y;
                        float[] coordinates = {screenX, screenY};

                        return coordinates;
                    }
                },
                Press.FINGER);
    }

    public Activity getActivityInstance() {
        final Activity[] activity = new Activity[1];
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable( ) {
            public void run() {
                Activity currentActivity = null;
                Collection resumedActivities = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED);
                if (resumedActivities.iterator().hasNext()){
                    currentActivity = (Activity) resumedActivities.iterator().next();
                    activity[0] = currentActivity;
                }
            }
        });

        return activity[0];
    }

    private boolean wholeViewHasChanged(float diffPxProportion) {
        if(diffPxProportion == 1) {
            return true;
        } else {
            return false;
        }
    }

    private float getDiffPxProportion(File imageFile1, File imageFile2) {
        /*int differentPixels = 0;
        int totalPixels = 0;
        float diffxPxProportion = 0f;

        //DEBUG
        String TAG = "MyActivity";
        String imageFile1null = "No";
        if(imageFile1 == null) imageFile1null = "Yes";
        Log.d(TAG, "CP3: \"imageFile1\" is null? = " + imageFile1null);

        String fileName1 = imageFile1.getAbsolutePath();
        BitmapFactory.Options opts1 = new BitmapFactory.Options();
        Bitmap bm1;
        opts1.inJustDecodeBounds = false;
        bm1 = BitmapFactory.decodeFile(fileName1, opts1);*/

        /*ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
        bm1.compress(Bitmap.CompressFormat.JPEG, 100, stream1);  // 100 = max quality
        byte[] byteArray1 = stream1.toByteArray();*/

        /*String fileName2 = imageFile1.getAbsolutePath();
        BitmapFactory.Options opts2 = new BitmapFactory.Options();
        Bitmap bm2;
        opts1.inJustDecodeBounds = false;
        bm2 = BitmapFactory.decodeFile(fileName2, opts1);*/

        /*ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
        bm2.compress(Bitmap.CompressFormat.JPEG, 100, stream2);  // 100 = max quality
        byte[] byteArray2 = stream2.toByteArray();*/

        /*for(int x = 0; x < bm1.getWidth(); x++) {
            for(int y = 0; y < bm1.getHeight(); y++) {
                int pixelColor1 = bm1.getPixel(x,y);
                int pixelColor2 = bm2.getPixel(x,y);

                if(pixelColor1 != pixelColor2) {
                    differentPixels += 1;
                }

                totalPixels ++;
            }
        }

        diffxPxProportion = differentPixels / totalPixels;

        return diffxPxProportion;*/
        return 1;
    }

    public File takeScreenshot(){
        //View view1 = mActivityTestRule.getActivity().getWindow().getDecorView().getRootView();
        View view1 = mActivityTestRule.getActivity().getWindow().getDecorView();
        view1.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(view1.getDrawingCache());
        view1.setDrawingCacheEnabled(false);

        //String filePath = Environment.getExternalStorageDirectory()+"/Download/"+ Calendar.getInstance().getTime().toString()+".jpg";
        String millis = System.currentTimeMillis() + "";
        String fileName = millis.replaceAll(":", ".") + ".jpg";
        String filePath = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES) + "/" + fileName;


        File fileScreenshot = new File(filePath);
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(fileScreenshot);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(fileScreenshot);
        intent.setDataAndType(uri,"image/jpeg");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mActivityTestRule.getActivity().startActivity(intent);*/

        return fileScreenshot;
    }

    public static void getBitmapFormView(View view, Activity activity, Callback callback) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);

        int[] locations = new int[2];
        view.getLocationInWindow(locations);
        Rect rect = new Rect(locations[0], locations[1], locations[0] + view.getWidth(), locations[1] + view.getHeight());

        PixelCopy.request(activity.getWindow(), rect, bitmap, copyResult -> {
            if (copyResult == PixelCopy.SUCCESS) {
                //callback.onResult(bitmap);
            }
        }, new Handler(Looper.getMainLooper()));
    }

    private File takeScreenshot2() {
        File file = null;
        return file;
    }

    private File takeScreenshot3() {
        /*Date now = new Date();
        DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);
        String nowStr = now.toString();
        nowStr = nowStr.replaceAll(" ", "_");
        nowStr = nowStr.replaceAll(":", "-");*/


        try {
            /*File imgFile = new File(Environment.getExternalStorageDirectory () + "/somefolder/" + now + ".jpg");
            String fileName = imgFile.getAbsolutePath();
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(fileName)));
            //startActivityForResult(intent, REQUEST_FROM_CAMERA);*/

            // image naming and path  to include sd card  appending name you choose for file
            //String mPath = Environment.getExternalStorageDirectory().toString() + "/" + nowStr + ".jpg";
            //String fileName = System.currentTimeMillis().toString().replaceAll(":", ".") + ".jpg";

            //DEBUG
            String TAG = "MyActivity";
            String imageFile1null = "No";
            //Log.d(TAG, "CP0: mPath = " + mPath);

            // create bitmap screen capture
            //View v1 = mActivityTestRule.getActivity().getWindow().getDecorView().getRootView();
            //v1.setDrawingCacheEnabled(true);
            //Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            Bitmap bitmap = Bitmap.createBitmap(mActivityTestRule.getActivity().getWindow().getDecorView().getRootView().getWidth(), mActivityTestRule.getActivity().getWindow().getDecorView().getRootView().getHeight(), Bitmap.Config.ARGB_8888);
            //v1.setDrawingCacheEnabled(false);

            //Debug
            //Bitmap bitmap2 = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.RGB_565);
            //Bitmap bitmap3 = SavePixels(0, 0, bitmap.getWidth(), bitmap.getHeight(), GL_RGBA);
            //PixelCopy.request(mActivityTestRule.getActivity().getWindow(), bitmap, {}, Handler(Looper.getMainLooper()))

            //Debug
            Canvas canvas = new Canvas();
            //canvas.setBitmap(bitmap);
            canvas.setBitmap(bitmap);
            Drawable drawBG = mActivityTestRule.getActivity().getWindow().getDecorView().getRootView().getBackground();
            if (drawBG != null) drawBG.draw(canvas);
            else canvas.drawColor(Color.BLACK);
            mActivityTestRule.getActivity().getWindow().getDecorView().getRootView().draw(canvas);
            //mActivityTestRule.getActivity().getWindow().getDecorView().draw(canvas);

            /*String mPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/screenshots";
            File dir = new File(mPath);
            dir.mkdir();*/
            //
            File path = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES);

            String milis = System.currentTimeMillis() + "";
            String fileName = milis.replaceAll(":", ".") + ".jpg";
            File imageFile = new File(path, fileName);

            //
            //path.mkdirs();
            //imageFile=File.createTempFile(fileName,".jpg",path);

            //DEBUG
            if(imageFile == null) imageFile1null = "Yes";
            Log.d(TAG, "CP1: \"imageFile1\" is null? = " + imageFile1null);

            FileOutputStream outputStream = new FileOutputStream(imageFile);

            //DEBUG
            if(imageFile == null) imageFile1null = "Yes";
            Log.d(TAG, "CP1,1: \"imageFile1\" is null? = " + imageFile1null);

            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            //bitmap3.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);

            //DEBUG
            if(imageFile == null) imageFile1null = "Yes";
            Log.d(TAG, "CP1.2: \"imageFile1\" is null? = " + imageFile1null);

            //outputStream.flush();

            //DEBUG
            if(imageFile == null) imageFile1null = "Yes";
            Log.d(TAG, "CP1,3: \"imageFile1\" is null? = " + imageFile1null);

            //wait(1000);

            outputStream.close();

            //openScreenshot(imageFile);

            //DEBUG
            if(imageFile == null) imageFile1null = "Yes";
            Log.d(TAG, "CP2: \"imageFile1\" is null? = " + imageFile1null);

            return imageFile;
        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
            return null;
        }
    }

    private void openScreenshot(File imageFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(imageFile);
        intent.setDataAndType(uri, "image/*");
        mActivityTestRule.getActivity().startActivity(intent);
    }

    public static Bitmap SavePixels(int x, int y, int w, int h, GL10 gl)
    {
        int b[]=new int[w*(y+h)];
        int bt[]=new int[w*h];
        IntBuffer ib=IntBuffer.wrap(b);
        ib.position(0);
        gl.glReadPixels(x, 0, w, y+h, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, ib);

        for(int i=0, k=0; i<h; i++, k++)
        {//remember, that OpenGL bitmap is incompatible with Android bitmap
            //and so, some correction need.
            for(int j=0; j<w; j++)
            {
                int pix=b[i*w+j];
                int pb=(pix>>16)&0xff;
                int pr=(pix<<16)&0x00ff0000;
                int pix1=(pix&0xff00ff00) | pr | pb;
                bt[(h-k-1)*w+j]=pix1;
            }
        }


        Bitmap sb=Bitmap.createBitmap(bt, w, h, Bitmap.Config.ARGB_8888);
        return sb;
    }

}
