package helloAr.testAgentSupport;

import static nl.uu.cs.aplib.AplibEDSL.*;

import eu.iv4xr.framework.mainConcepts.TestAgent;
import eu.iv4xr.framework.mainConcepts.WorldModel;
import nl.uu.cs.aplib.mainConcepts.Action;
import nl.uu.cs.aplib.mainConcepts.GoalStructure;

public class GoalLib {

    public GoalStructure tapScreenG(TestAgent agent, int x, int y, int sleep) {
        Action tap = action("tapping screen")
                .do1((MyAgentState S) -> {
                    WorldModel obs = null;
                    try {
                        obs = S.env().tapScreen(agent.getId(),x,y,sleep);
                    } catch (InterruptedException e) {
                        return null ;
                    }
                    return obs ;
                })
                ;
        return lift("Screen @(" + x + "," + y + ") tapped", tap) ;
    }

    //New: More goals added
    public GoalStructure clickButtonG(TestAgent agent, String buttonName, int sleep) throws InterruptedException {
        Action click = action("clicking button")
                .do1((MyAgentState S) -> {
                    WorldModel obs = null;
                    try {
                        obs = S.env().clickButton(agent.getId(), buttonName, sleep);
                    } catch (InterruptedException e) {
                        return null ;
                    }
                    return obs ;
                })
                ;
        return lift("'" + buttonName + "' button clicked", click) ;
    }

    public GoalStructure selectVideoG(TestAgent agent, int videoPosition, int sleep) throws InterruptedException {
        Action selectVideo = action("clicking button")
                .do1((MyAgentState S) -> {
                    WorldModel obs = null;
                    try {
                        obs = S.env().selectVideo(agent.getId(), videoPosition, sleep);
                    } catch (InterruptedException e) {
                        return null ;
                    }
                    return obs ;
                })
                ;
        return lift("Video " + videoPosition + " selected", selectVideo) ;
    }
}
