package test;

import android.os.Handler;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;

public class automaticAction {
    public void tapScreen(View view) {
        float x = 0.0f;
        float y = 0.0f;
        for (int i = 0; i < 4; i++) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    // yourMethod();
                }
            }, 5000);   //5 seconds

            // Obtain MotionEvent object
            long downTime = SystemClock.uptimeMillis();
            long eventTime = SystemClock.uptimeMillis() + 100;
            x = x++;
            y = y++;
            int metaState = 0;
            MotionEvent motionEvent = MotionEvent.obtain(
                    downTime,
                    eventTime,
                    MotionEvent.ACTION_UP,
                    x,
                    y,
                    metaState
            );

            // Dispatch touch event to view
            view.dispatchTouchEvent(motionEvent);
        }
    }


    /*
        view.setOnTouchListener(new View.OnTouchListener()
        {
            public boolean onTouch(View v, MotionEvent event)
            {
                Toast toast = Toast.makeText(
                        getApplicationContext(),
                        "View touched",
                        Toast.LENGTH_LONG
                );
                toast.show();

                return true;
            }

            private Context getApplicationContext() {
            }
        });
        */

}
