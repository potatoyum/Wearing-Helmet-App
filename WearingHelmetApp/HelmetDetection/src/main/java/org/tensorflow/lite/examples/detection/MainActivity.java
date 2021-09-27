package org.tensorflow.lite.examples.detection;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.wearinghelmetapp.BluetoothConnect.BluetoothConnectActivity;
import com.example.wearinghelmetapp.BluetoothConnect.DeviceScanActivity;
import com.example.wearinghelmetapp.ScanActivity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.tensorflow.lite.examples.detection.tflite.Detector;

import java.util.IllegalFormatFlagsException;

/**
 * 어플리케이션 실행시 가장 먼저 실행되는 액티비티입니다.
 * 전체 구조를 변경하고 싶을 때는 여기부터 참고 하시면 됩니다.
 */
public class MainActivity extends AppCompatActivity {
    public static final int QR_REQUEST_CODE=1;
    private final int helmet_detector_waiting_time_ms=60000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //QR코드 인식 액티비티 실행
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(ScanActivity.class);
        integrator.initiateScan();
    }
    private boolean isOpenRequest=false;
    private String moduleName;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode,resultCode,intent);
        Log.d("onActivityResult", "onActivityResult: .");
        //QR코드 인식 결과 읽은 메시지를 포함한 결괏값 리턴
        if (requestCode==IntentIntegrator.REQUEST_CODE){
            Log.d("onActivityResult","IntentIntegrator finished");
            if (resultCode == Activity.RESULT_OK) {

                IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
                moduleName = scanResult.getContents();
                Log.d("onActivityResult", "onActivityResult: ." + moduleName);
                Toast.makeText(this, moduleName, Toast.LENGTH_LONG).show();

                // QR코드를 통해서 받은 블루투스 모듈명으로 보관함에 해제 요청
                isOpenRequest=true;
                Intent scanIntent = new Intent(getApplicationContext(), DeviceScanActivity.class);
                scanIntent.putExtra(DeviceScanActivity.EXTRAS_DEVICE_ADDRESS, moduleName); // 인텐트로 스캔액티비티로 값 넘김
                scanIntent.putExtra(DeviceScanActivity.BLE_COMMAND, "^");
                scanIntent.putExtra(DeviceScanActivity.BLE_RECEIVE,"get\n");
                startActivityForResult(scanIntent,BluetoothConnectActivity.BLUETOOTH_REQUEST_CODE);

            }
            else if(resultCode == ScanActivity.ALREADY_HAS){
                //이미 보관함을 가진 경우 바로 안전모 감지 액티비티로 이동
                Intent detectorIntent=new Intent(this,DetectorActivity.class);
                detectorIntent.putExtra(DetectorActivity.TIME,helmet_detector_waiting_time_ms);
                startActivityForResult(detectorIntent,DetectorActivity.REQUEST_CODE);
            }
            else{
                throw new IllegalStateException("wrong result code is returned");
            }
        }
        //블루투스 통신의 결괏값
        if(requestCode==BluetoothConnectActivity.BLUETOOTH_REQUEST_CODE){
            Log.d("onActivityResult","BluetoothConnectActivity finished");
            if(resultCode==RESULT_OK){
                // 해제 성공 신호를 받은 경우 안전모 감지 액티비티로 이동
                if(isOpenRequest){
                    Intent detectorIntent=new Intent(this,DetectorActivity.class);
                    detectorIntent.putExtra(DetectorActivity.TIME,helmet_detector_waiting_time_ms);
                    startActivityForResult(detectorIntent,DetectorActivity.REQUEST_CODE);
                }
                else{
                    //TODO return request

                }
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
            Log.d("onActivityResult","DetectorActivity finished");
            if(isOpenRequest){
                Intent scanIntent = new Intent(getApplicationContext(), DeviceScanActivity.class);
                scanIntent.putExtra(DeviceScanActivity.EXTRAS_DEVICE_ADDRESS, moduleName); // 인텐트로 스캔액티비티로 값 넘김
                scanIntent.putExtra(DeviceScanActivity.BLE_COMMAND, "^");
                scanIntent.putExtra(DeviceScanActivity.BLE_RECEIVE,"end");
                startActivityForResult(scanIntent,BluetoothConnectActivity.BLUETOOTH_REQUEST_CODE);
                isOpenRequest=false;
            }
        }
    }
}