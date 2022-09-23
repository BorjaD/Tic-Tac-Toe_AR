package test;

public class TestActivity {/*
    private static List<WrappedAnchor> wrappedAnchors = new ArrayList<>();

    public static void testSequence(List<WrappedAnchor> state, List<String> ids, List<String> previous, Frame frame, Session session) {
        tapScreen(state, frame, session);
        update(state, ids, previous);
    }

    private static void tapScreen(List<WrappedAnchor> state, Frame frame, Session s) {
        //Add an Anchor and a renderable in front of the camera
        //Fragment arFragment = null;
        /*Session session = s;
        float[] pos = {(float) 0.002, (float) -0.652, (float) -0.740};
        float[] rotation = {(float) -0.00, (float) 0.03, (float) 0.02, (float) 1.00};
        Anchor anchor = session.createAnchor(new Pose(pos, rotation));
        WrappedAnchor wanchor = new WrappedAnchor(anchor);
        state.add(wanchor);
    }

    public static List<String> update(List<WrappedAnchor> state, List<String> ids, List<String> previous) {
        if(state.size() > 4) {
            Log.d("COBA", "VIOLATION of inv: more anchors than allowed!") ;
        }
        List<String> newAnchors = ids ;
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
        return previous;
    }*/
}