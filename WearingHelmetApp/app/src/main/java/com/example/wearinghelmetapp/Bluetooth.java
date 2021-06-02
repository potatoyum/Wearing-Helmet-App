package com.example.wearinghelmetapp;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class Bluetooth {
    
    public static final int REQUEST_ENABLE_BT = 10; // 블루투스 활성화 상태
    private BluetoothAdapter bluetoothAdapter; // 블루투스 어댑터
    private OutputStream outputStream = null; // 블루투스에 데이터를 출력하기 위한 출력 스트림
    private InputStream inputStream = null; // 블루투스에 데이터를 입력하기 위한 입력 스트림

    private BluetoothDevice bluetoothDevice; // 블루투스 디바이스
    private BluetoothSocket bluetoothSocket = null; // 블루투스 소켓

    
    Set<BluetoothDevice> devices;  //블루투스 디바이스 데이터셋
    ArrayAdapter<String> btArrayAdapter;
    ArrayList<String> deviceAddressArray;

    public String devicename;

    public Bluetooth(String name)
    {
        devicename=name;

        
    }

    public boolean activate()
    {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); // 블루투스 어댑터를 디폴트 어댑터로 설정

        //btArrayAdapter= new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        //deviceAddressArray= new ArrayList<>();
        //listView.setAdapter(btArrayAdapter);

        if(bluetoothAdapter.isEnabled()) { // 블루투스가 활성화 상태 (기기에 블루투스가 켜져있음)
            bluetoothAdapter.startDiscovery();
            return true;
        }

        else { // 블루투스가 비 활성화 상태이면
            return false;


        }

    }



    //블루투스 검색결과 BroadcastReceiver



    public void connectBluetoothDevice(BluetoothDevice dv) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException  //인자로 디바이스명 건네주기
    {
        bluetoothDevice=dv;
        if (bluetoothDevice.getName().equals(devicename)){
            Method method = bluetoothDevice.getClass().getMethod("createBond", (Class[]) null);
            method.invoke(bluetoothDevice, (Object[]) null);

            Log.d("mTag", "connected00");
        }






    /*
        //연결해주기
        UUID uuid = java.util.UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    
        try {

            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
    
            bluetoothSocket.connect();
            Log.d("mTag", "connected");
    
            // 데이터 송,수신 스트림을 얻어옵니다.
    
            outputStream = bluetoothSocket.getOutputStream();
    
            inputStream = bluetoothSocket.getInputStream();
    
            // 데이터 송/수신 함수 호출
    
            //receiveData();
            //sednData();
    
        } catch (IOException e) {
    
            e.printStackTrace();
    
        }
        */
        
    
    }
   

    
}








    



