package com.example.wearinghelmetapp;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;

public class ConnectActivity extends AppCompatActivity {
    public Bluetooth b1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);


        b1=new Bluetooth("TEST");

        if (b1.activate())
        {
            b1.connectBluetoothDevice(); // 블루투스 디바이스 연결함수 호출
        }
        else
        {
            // 블루투스를 활성화 하기 위한 다이얼로그 출력
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            // 선택한 값이 onActivityResult 함수에서 콜백된다.

            startActivityForResult(intent, Bluetooth.REQUEST_ENABLE_BT);
        }


    }


    //onAcitivityResul함수 오버라이드
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Bluetooth.REQUEST_ENABLE_BT) {
            if (resultCode != RESULT_OK) {
                return;
            }
            b1.connectBluetoothDevice();  //블루투스 디바이스 선택함수 호출
        }

    }
}