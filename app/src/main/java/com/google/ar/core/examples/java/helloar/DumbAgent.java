package com.google.ar.core.examples.java.helloar;

import android.util.Log;

import java.util.LinkedList;
import java.util.List;

public class DumbAgent {

    public List<WrappedAnchor> state ;
    public List<String> previous = new LinkedList<>() ;

    public DumbAgent() { }

    public DumbAgent attachState(List<WrappedAnchor>  state) {
        this.state = state ;
        previous = getIds() ;
        return this ;
    }

    List<String> getIds() {
        List<String> ids = new LinkedList<>() ;
        for(WrappedAnchor a : state) {
            ids.add(a.id) ;
        }
        return ids ;
    }

    public void update() {
        if(state.size() > 4) {
            Log.d("COBA", "VIOLATION of inv: more anchors than allowed!") ;
        }
        List<String> newAnchors = getIds() ;
        for(String a : previous) {
            if (!newAnchors.contains(a)) {
                Log.d("COBA", "Anchor removed: " + a) ;
            }
        }
        for (String a : newAnchors) {
            if (! previous.contains(a)) {
                Log.d("COBA", "Anchor added: " + a) ;
            }
        }
        previous = newAnchors ;
    }
/*
    public void runTestSequence(Frame frame, Session session) {
        List<String> ids = getIds();
        test.TestActivity.testSequence(state, ids, previous, frame, session);
    }
*/
}
