package com.example.wearinghelmetapp;

import androidx.appcompat.app.AppCompatActivity;

import com.bluetooth;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    
  

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new bluetooth b1=bluetooth();
        b1.activate();
        b1.connectBluetoothDevice("이름 넣어줘야됨");



       

    
    }

   

    
}








    



