package com.google.ar.core.examples.java.helloar;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertTrue;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.UiDevice;

import com.google.ar.core.Anchor;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;


@LargeTest
@RunWith(AndroidJUnit4.class)
public class HelloArActivityTest {

    @Rule
    public ActivityTestRule<HelloArActivity> mActivityTestRule = new ActivityTestRule<>(HelloArActivity.class);
    //public IntentsTestRule<HelloArActivity> mActivityTestRule = new IntentsTestRule<>(HelloArActivity.class);
    //public ActivityScenarioRule<HelloArActivity> mActivityTestRule = new ActivityScenarioRule <>(HelloArActivity.class);

    /*
    private HelloArActivity launchedActivity;

    @Before
    public void setUp() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        //this is the key part
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //this is the key part
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        launchedActivity = mActivityTestRule.launchActivity(intent);
    }
     */

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
        Thread.sleep(2000);

        // Click on the recorded video (in Downloads file)
        // Long tap (select) on a video in the gallery
        //First video OK
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).click(200, 1000);
        //Second video OK
//        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).click(800, 1000);
        //Third video OK
//        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).click(200, 1500);
        //Forth video OK
//        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).click(800, 1500);

        // Tap on SELECT
        /*Thread.sleep(1000);
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).click(900, 300);*/

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
        Thread.sleep(5000);

        //Validate properties of mActivityTestRule

        /*ActivityScenario scenario = mActivityTestRule.getScenario();
        mActivityTestRule.getScenario().getResult().getResultData().get
        scenario.getResult().getResultData();
        Activity activityInstance = getActivityInstance();*/
        /*assertThat(activity.getSomething()).isEqualTo("something");
        assertEquals(datosRecibidos.extras!!.getString("nombre"), "Julio")
        scenario.onActivity(activity -> {
            assertThat(activity.getSomething()).isEqualTo("something");
            assertThat(activity.activityResult.getResultData()).extras().containsKey("key");
        });*/

        String TAG = "MyActivity";
        List<WrappedAnchor> anchorsList = mActivityTestRule.getActivity().getAnchors();
        Log.d(TAG, "Number of anchors: " + anchorsList.size());
        for(WrappedAnchor wAnchor : anchorsList) {
            Anchor anchor = wAnchor.getAnchor();
            float qx = anchor.getPose().qx();
            float qy = anchor.getPose().qy();
            float qz = anchor.getPose().qz();
            float qw = anchor.getPose().qw();

            float tx = anchor.getPose().tx();
            float ty = anchor.getPose().ty();
            float tz = anchor.getPose().tz();

            float[] xAxis = anchor.getPose().getXAxis();
            float[] yAxis = anchor.getPose().getYAxis();
            float[] zAxis = anchor.getPose().getZAxis();

            Log.d(TAG, "qx = " + qx);
            Log.d(TAG, "qy = " + qy);
            Log.d(TAG, "qz = " + qz);
            Log.d(TAG, "qw = " + qw);
            Log.d(TAG, "tx = " + tx);
            Log.d(TAG, "ty = " + ty);
            Log.d(TAG, "tz = " + tz);

            Log.d(TAG, "xAxis:");
            for(float i : xAxis){
                Log.d(TAG, "" + i);
            }
            Log.d(TAG, "yAxis:");
            for(float i : yAxis){
                Log.d(TAG, "" + i);
            }
            Log.d(TAG, "zAxis:");
            for(float i : zAxis){
                Log.d(TAG, "" + i);
            }

            //TEST CASE 1: The base of the item is well placed in a surface
            //It should only be able to be rotated to left/right (qy)
            assertTrue(qx == 0.0);
            assertTrue(qz == 0.0);
        }

        Thread.sleep(10000);


    }

    public static Matcher<View> childAtPosition(
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

    /*public static ViewAction clickXY(final int x, final int y){
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
    }*/

    /*public Activity getActivityInstance() {
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
    }*/
}
