package com.example.wearinghelmetapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;

import com.example.wearinghelmetapp.ui.FailReturnFragment;

public class ReturnActivity extends AppCompatActivity {
    FragmentManager fragmentManager;
    FailReturnFragment failReturnFragment;
    FragmentTransaction fragmentTransaction;

    TextView textView;
    CountDownTimer myTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return);
        textView = findViewById(R.id.tv_timer);

        fragmentManager = getSupportFragmentManager();
        failReturnFragment = new FailReturnFragment();

        setMyTimer();
        myTimer.start();
    }

    private void setMyTimer() {
        //1000 --> 1초
        myTimer = new CountDownTimer(9000, 1000) {
            @Override
            public void onTick(long l) {
                textView.setText("⏳" + l / 1000 + "초 남았습니다⏳");
            }

            @Override
            public void onFinish() {
                textView.setText("⏳" + 0 + "초 남았습니다⏳");
                fragmentManager.beginTransaction().replace(R.id.cl_parent, failReturnFragment).commit();
            }
        };
    }


}