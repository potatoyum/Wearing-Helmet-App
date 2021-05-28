package com.example.wearinghelmetapp.Handler;

import android.app.Activity;
import android.widget.Toast;

public class BackProgressCloseHandler {

    private long backKeyPressedTime = 0;
    private Toast toast;
    private Activity activity;

    public BackProgressCloseHandler(Activity context) {
        this.activity = context;
    }

    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            activity.finishAffinity();
            toast.cancel();
        }
    }
    public void showGuide() {
        toast = Toast.makeText(activity, "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT); toast.show();
    }

}
