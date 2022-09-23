package helloAr.testAgentSupport;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static com.google.ar.core.examples.java.helloar.HelloArActivityTest.childAtPosition;
import static org.hamcrest.Matchers.allOf;

import androidx.test.espresso.ViewInteraction;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;

import com.google.ar.core.Anchor;
import com.google.ar.core.examples.java.helloar.HelloArActivity;
import com.google.ar.core.examples.java.helloar.R;
import com.google.ar.core.examples.java.helloar.WrappedAnchor;

import java.util.LinkedList;
import java.util.List;

import eu.iv4xr.framework.mainConcepts.Iv4xrEnvironment;
import eu.iv4xr.framework.mainConcepts.WorldEntity;
import eu.iv4xr.framework.mainConcepts.WorldModel;

public class MyAgentEnv extends Iv4xrEnvironment {

    public HelloArActivity mainActivity;

    List<WrappedAnchor> seenAnchors = new LinkedList<>();

    List<String> lastSeen = new LinkedList<>() ;

    int freshId = 0 ;

    int timeStamp = 0 ;


    public MyAgentEnv(HelloArActivity activity) {
        this.mainActivity = activity;
    }

    int getId(WrappedAnchor a) {
        for(int k = 0 ; k<seenAnchors.size(); k++) {
            if (a == seenAnchors.get(k))
                return k ;
        }
        return -1 ;
    }


    public WorldModel observe(String agentId) {

        WorldModel wom = new WorldModel() ;
        wom.agentId = agentId ;
        wom.timestamp = timeStamp ;

        List<WrappedAnchor> anchorsList = mainActivity.getAnchors();
        lastSeen.clear();
        for(WrappedAnchor a : anchorsList) {

            int id_ = getId(a) ;
            if(id_ < 0)  {
                id_ = freshId ;
                freshId++ ;
            }

            Anchor anchor = a.getAnchor() ;

            String id = "A" + id_ ;
            lastSeen.add(id) ;
            WorldEntity e = new WorldEntity(id,"3DObj", true) ;
            e.timestamp = wom.timestamp ;
            //New: Completed attributes
            e.properties.put("pose", anchor.getPose().toString()) ;

            e.properties.put("qx", anchor.getPose().qx()) ;
            e.properties.put("qy", anchor.getPose().qy()) ;
            e.properties.put("qz", anchor.getPose().qz()) ;
            e.properties.put("qw", anchor.getPose().qw()) ;
            e.properties.put("rotationQuaternion", anchor.getPose().getRotationQuaternion()) ;

            e.properties.put("tx", anchor.getPose().tx()) ;
            e.properties.put("ty", anchor.getPose().ty()) ;
            e.properties.put("tz", anchor.getPose().tz()) ;
            e.properties.put("translation", anchor.getPose().getTranslation()) ;

            e.properties.put("xAxis", anchor.getPose().getXAxis()) ;
            e.properties.put("yAxis", anchor.getPose().getYAxis()) ;
            e.properties.put("zAxis", anchor.getPose().getZAxis()) ;

            wom.elements.put(e.id,e) ;
        }
        this.timeStamp++ ;
        return wom ;
    }

    public WorldModel tapScreen(String agentId, int x, int y, int sleep) throws InterruptedException {
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).click(x, y);
        Thread.sleep(sleep);
        return observe(agentId);
    }

    //New: More actions added
    public WorldModel clickButton(String agentId, String buttonName, int sleep) throws InterruptedException {
        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.playback_button), withText(buttonName),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        appCompatButton.perform(click());

        Thread.sleep(sleep);
        return observe(agentId);
    }

    public WorldModel selectVideo(String agentId, int videoPosition, int sleep) throws InterruptedException {
        switch (videoPosition) {        //coordinates in Downloads folder
            case 1:
                UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).click(200, 1000);
            case 2:
                UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).click(800, 1000);
            case 3:
                UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).click(200, 1500);
            case 4:
                UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).click(800, 1500);
        }

        Thread.sleep(sleep);
        return observe(agentId);
    }
}
