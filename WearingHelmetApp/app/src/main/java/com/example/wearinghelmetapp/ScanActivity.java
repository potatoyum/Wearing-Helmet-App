package com.example.wearinghelmetapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.content.pm.PackageManager;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.wearinghelmetapp.Handler.BackProgressCloseHandler;
import com.google.android.material.button.MaterialButton;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

public class ScanActivity extends Activity implements DecoratedBarcodeView.TorchListener {
    private CaptureManager captureManager;
    private DecoratedBarcodeView barcodeScannerView;
    private BackProgressCloseHandler backProgressCloseHandler;
    private Boolean switchFlashlightButtonCheck;
    private ImageButton switchFlashlightBtn;
    private MaterialButton ownHelmetButton;

    public static final int ALREADY_HAS=9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        switchFlashlightButtonCheck = true;

        backProgressCloseHandler = new BackProgressCloseHandler(this);

        Log.d("onActivityResult", "시작2");

        switchFlashlightBtn = (ImageButton)findViewById(R.id.flashlight_btn);
        ownHelmetButton=(MaterialButton)findViewById(R.id.OwnHelmetBtn);
        ownHelmetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("onActivityResult", "플래시");
                setResult(ALREADY_HAS);
                finish();
            }
        });

        if (!hasFlash()) { //라이트가 없는 경우 버튼 안 보이게
            switchFlashlightBtn.setVisibility(View.GONE);
        }

        barcodeScannerView = (DecoratedBarcodeView)findViewById(R.id.zxing_barcode_scanner);
        barcodeScannerView.setTorchListener(this);

        captureManager = new CaptureManager(this, barcodeScannerView);
        captureManager.initializeFromIntent(getIntent(), savedInstanceState);
        captureManager.decode();
    }

    @Override
    protected void onResume() {
        super.onResume();
        captureManager.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        captureManager.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        captureManager.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        captureManager.onSaveInstanceState(outState);
    }

    public void onBackPressed() {
        backProgressCloseHandler.onBackPressed();
    }

    public void switchFlashlight(View view) {
        if (switchFlashlightButtonCheck) {
            barcodeScannerView.setTorchOn();
        } else {
            barcodeScannerView.setTorchOff();
        }
    }

    private boolean hasFlash() {
        return getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }


    @Override
    public void onTorchOn() {
        switchFlashlightBtn.setImageResource(R.drawable.flash_on);
        switchFlashlightButtonCheck = false;
    }

    @Override
    public void onTorchOff() {
        switchFlashlightBtn.setImageResource(R.drawable.flash_off);
        switchFlashlightButtonCheck = true;
    }
}