package com.google.ar.core.examples.java.helloar;

import static org.junit.Assert.assertTrue;
import static nl.uu.cs.aplib.AplibEDSL.SEQ;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.runner.RunWith;

import eu.iv4xr.framework.mainConcepts.TestAgent;
import eu.iv4xr.framework.mainConcepts.WorldEntity;
import helloAr.testAgentSupport.GoalLib;
import helloAr.testAgentSupport.MyAgentEnv;
import helloAr.testAgentSupport.MyAgentState;
import nl.uu.cs.aplib.mainConcepts.GoalStructure;


@LargeTest
@RunWith(AndroidJUnit4.class)
public class HelloArActivityTest_WithAgent {

    @Rule
    public ActivityTestRule<HelloArActivity> mActivityTestRule = new ActivityTestRule<>(HelloArActivity.class);
    //public IntentsTestRule<HelloArActivity> mActivityTestRule = new IntentsTestRule<>(HelloArActivity.class);
    //public ActivityScenarioRule<HelloArActivity> mActivityTestRule = new ActivityScenarioRule <>(HelloArActivity.class);



    //@Test
    public void helloArActivityTest() throws InterruptedException {

        TestAgent agent = new TestAgent("agentSmith","tester") ;
        MyAgentState state = new MyAgentState() ;

        agent.attachState(state)
             .attachEnvironment(new MyAgentEnv(mActivityTestRule.getActivity())) ;
        GoalLib goalLib = new GoalLib() ;
        GoalStructure G = SEQ(
                goalLib.clickButtonG(agent, "Playback", 2000),
                goalLib.selectVideoG(agent, 1, 1000),
                goalLib.tapScreenG(agent,300,1500,3000),
                goalLib.tapScreenG(agent,600,1500,3000),
                goalLib.tapScreenG(agent,400,1000,3000),
                goalLib.tapScreenG(agent,500,1000,5000)
        ) ;
        agent.setGoal(G) ;



        int k=0 ;
        while(G.getStatus().inProgress() && k < 20) {
            System.out.println(">>> k="+k) ;
            agent.update() ;
            int numberOfAnchorsDisplayed = 0;

            //Specific assertions:
            //  TEST CASE 1: The base of the item is well placed in a surface
            //  It should only be able to be rotated to left/right (qy)
            for(WorldEntity a : state.worldmodel().elements.values()) {
                if (a.type.equals("3DObj")) {
                    assertTrue((int) a.properties.get("qx") == 0.0) ;
                    assertTrue((int) a.properties.get("qz") == 0.0) ;
                }

                numberOfAnchorsDisplayed ++;
            }

            //There are a maximum of 4 anchors displayed
            assertTrue(numberOfAnchorsDisplayed <= 4);
        }

        assertTrue(G.getStatus().success());

    }

}
