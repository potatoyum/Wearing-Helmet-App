package com.example.wearinghelmetapp.BluetoothConnect;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.wearinghelmetapp.R;
import com.google.android.material.button.MaterialButton;

import java.util.IllegalFormatException;

public class BluetoothConnectActivity extends AppCompatActivity {
    public static final String MODULE_NAME="MODULE_NAME",REQUEST_TYPE="REQUEST_TYPE";
    public static final int OPEN_CASE=0,CHECK_LOCK=1, BLUETOOTH_REQUEST_CODE=1002;
    private MaterialButton bluetoothConnectBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) throws IllegalFormatException {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_connect);
        //set button listener
        bluetoothConnectBtn=(MaterialButton)findViewById(R.id.bluetooth_connect_button);
        bluetoothConnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get intent and request
                Intent intent=getIntent();
                String moduleName=intent.getStringExtra(MODULE_NAME);
                int request=intent.getIntExtra(REQUEST_TYPE,-1);
                int result=-1;
                if (request==OPEN_CASE){
                    result=requestOpenCase(moduleName);
                }
                else if(request==CHECK_LOCK){
                    result=requestLockStatus(moduleName);
                }
                else{
                    throw new IllegalArgumentException("request type is not set");
                }
                setResult(result);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    /**
     * 블루투스로 보관함 연결 후 연결 해제 요청을 하는 메소드를 작성해주세요
     * 에러가 발생한 경우는 OPEN_ERROR를 리턴하면 됩니다.
     */
    public static final int OPEN_SUCCESS=1,OPEN_ERROR=0;
    protected int requestOpenCase(String moduleName){

        return OPEN_SUCCESS;
    }
    /**
     * 블루투스로 보관함 연결 후 잠금 확인을 요청을 하는 메소드를 작성해주세요
     * 에러가 발생한 경우는 LOCK_ERROR를 리턴하면 됩니다.
     */
    public static final int LOCK_OK=1,LOCK_ERROR=0;
    protected int requestLockStatus(String moduleName){
        //TODO requestLockStatus 작성(김미주)

        return LOCK_OK;
    }
}