package org.tensorflow.lite.examples.detection;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.wearinghelmetapp.ScanActivity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

/**
 * 어플리케이션 실행시 가장 먼저 실행되는 액티비티입니다.
 * 전체 구조를 변경하고 싶을 때는 여기를 참고 하시면 됩니다.
 */
public class MainActivity extends AppCompatActivity {
    public static final int QR_REQUEST_CODE=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    protected void onResume(){
        super.onResume();
        /**
         * QR코드 인식 액티비티 실행
         */
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(ScanActivity.class);
        integrator.initiateScan();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode,resultCode,intent);
        Log.d("onActivityResult", "onActivityResult: .");
        /**
         * QR코드 인식 결과 읽은 메시지를 포함한 결괏값 리턴
         */
        if (requestCode==IntentIntegrator.REQUEST_CODE){
            if (resultCode == Activity.RESULT_OK) {
                IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
                String re = scanResult.getContents();
                String message = re;
                Log.d("onActivityResult", "onActivityResult: ." + re);
                Toast.makeText(this, re, Toast.LENGTH_LONG).show();
                //TODO 1. QR코드를 통해서 받은 블루투스 모듈명으로 보관함에 해제 요청을 해야함
                //TODO 1-1. 해제 성공 신호를 받은 경우 안전모 감지 액티비티로 이동
            }
            if(resultCode == ScanActivity.ALREADY_HAS){
                //TODO 2. 이미 보관함을 가진 경우 바로 안전모 감지 액티비티로 이동
            }
        }
    }
}