package com.example.wearinghelmetapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.wearinghelmetapp.BluetoothConnect.DeviceScanActivity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ScanMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("onActivityResult", "시작");
    }

    protected void onResume(){
        super.onResume();


        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(ScanActivity.class);
        integrator.initiateScan();


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode,resultCode, intent);
        if (resultCode == Activity.RESULT_OK) {
            IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
            String re = scanResult.getContents(); //스캔한 값
            //String message = re;
            Log.d("onActivityResult", "onActivityResult: ." + re);
            Log.d("aaa",re);
            Toast.makeText(this, re, Toast.LENGTH_LONG).show();

            Intent scanIntent = new Intent(getApplicationContext(), DeviceScanActivity.class);

            scanIntent.putExtra(DeviceScanActivity.EXTRAS_DEVICE_ADDRESS, re); // 인텐트로 스캔액티비티로 값 넘김

            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}