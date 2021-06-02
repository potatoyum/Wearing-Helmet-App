package org.tensorflow.lite.examples.detection;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.wearinghelmetapp.BluetoothConnect.BluetoothConnectActivity;
import com.example.wearinghelmetapp.ScanActivity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.IllegalFormatFlagsException;

/**
 * 어플리케이션 실행시 가장 먼저 실행되는 액티비티입니다.
 * 전체 구조를 변경하고 싶을 때는 여기부터 참고 하시면 됩니다.
 */
public class MainActivity extends AppCompatActivity {
    public static final int QR_REQUEST_CODE=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //QR코드 인식 액티비티 실행
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(ScanActivity.class);
        integrator.initiateScan();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode,resultCode,intent);
        Log.d("onActivityResult", "onActivityResult: .");
        //QR코드 인식 결과 읽은 메시지를 포함한 결괏값 리턴
        if (requestCode==IntentIntegrator.REQUEST_CODE){
            if (resultCode == Activity.RESULT_OK) {

                IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
                String moduleName = scanResult.getContents();
                Log.d("onActivityResult", "onActivityResult: ." + moduleName);
                Toast.makeText(this, moduleName, Toast.LENGTH_LONG).show();

                // QR코드를 통해서 받은 블루투스 모듈명으로 보관함에 해제 요청
                Intent bluetoothIntent=new Intent(this, BluetoothConnectActivity.class);
                bluetoothIntent.putExtra(BluetoothConnectActivity.MODULE_NAME,moduleName);
                bluetoothIntent.putExtra(BluetoothConnectActivity.REQUEST_TYPE,BluetoothConnectActivity.OPEN_CASE);
                startActivityForResult(bluetoothIntent,BluetoothConnectActivity.BLUETOOTH_REQUEST_CODE);

            }
            else if(resultCode == ScanActivity.ALREADY_HAS){
                //이미 보관함을 가진 경우 바로 안전모 감지 액티비티로 이동
                Intent detectorIntent=new Intent(this,DetectorActivity.class);
                startActivityForResult(detectorIntent,DetectorActivity.REQUEST_CODE);
            }
            else{
                throw new IllegalStateException("wrong result code is returned");
            }
        }
        //블루투스 통신의 결괏값
        if(requestCode==BluetoothConnectActivity.BLUETOOTH_REQUEST_CODE){
            if(resultCode==BluetoothConnectActivity.OPEN_SUCCESS){
                // 해제 성공 신호를 받은 경우 안전모 감지 액티비티로 이동
                Intent detectorIntent=new Intent(this,DetectorActivity.class);
                startActivityForResult(detectorIntent,DetectorActivity.REQUEST_CODE);
            }
            else if(resultCode==BluetoothConnectActivity.OPEN_ERROR){
                //TODO 에러처리
            }
            else if(resultCode==BluetoothConnectActivity.LOCK_OK){
                //TODO 반납확인 신호를 받은 경우 반납 완료 뷰로 전환
            }
            else if(resultCode==BluetoothConnectActivity.LOCK_ERROR){
                //TODO 에러처리
            }
            else{
                throw new IllegalStateException("wrong result code is returned");
            }
        }
        //안전모 감지 액티비티의 결괏값,
        if(requestCode==DetectorActivity.REQUEST_CODE){
            if(requestCode==DetectorActivity.DETECTED){
                //안전모가 감지된 경우 결괏값 리턴 후 종료
                finishActivity(DetectorActivity.DETECTED);
            }
            else if(requestCode==DetectorActivity.TIME_OUT){
                //안전모가 시간안에 감지되지 않은 경우
                finishActivity(DetectorActivity.TIME_OUT);
            }
            else{
                throw new IllegalStateException("wrong result code is returned");
            }
        }
    }
}