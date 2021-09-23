package com.example.wearinghelmetapp.BluetoothConnect;


import android.Manifest;
import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.wearinghelmetapp.R;
import com.google.android.material.button.MaterialButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 */
public class DeviceScanActivity extends AppCompatActivity {

    //private LeDeviceListAdapter mLeDeviceListAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLEScanner;
    private ScanSettings settings;
    private boolean mScanning;
    private Handler mHandler;
    private String mDeviceAddr; //스캔한 MAC 주소
    private String bleCommand,bleReceive;

    private static final int REQUEST_ENABLE_BT = 1;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 50000;
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS",BLE_COMMAND="BLE_COMMAND",BLE_RECEIVE="BLE_RECEIVE_COMMAND";
    private final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
    private MaterialButton kickboardButton;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        kickboardButton =((MaterialButton)findViewById(R.id.kickboard_button));
        setContentView(R.layout.activity_connect);
        //getActionBar().setTitle(R.string.title_devices);
        mHandler = new Handler(Looper.myLooper());

        final Intent intent = getIntent();
        mDeviceAddr = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS); //qr 인식한 mac 주소
        bleCommand=intent.getStringExtra(BLE_COMMAND);
        bleReceive=intent.getStringExtra(BLE_RECEIVE);
        Log.d("aaa",mDeviceAddr.toString());


        ActivityCompat.requestPermissions(this, //위치 퍼미션 허용
               new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);

        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "R.string.ble_not_supported", Toast.LENGTH_SHORT).show();
            finish();
        }

        // 블루투스매니저 인스턴스 반환 후 블루투스어댑터 get
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        Log.d("aaa",mBluetoothAdapter.toString());
        // 블루투스 지원 안되면 리턴
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            return;
        }


        settings = new ScanSettings.Builder().setScanMode(
                ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setReportDelay(0)
                .build();


        //스캔 시작
        scanLeDevice(true);
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    //스캔
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            Log.d("aaa","scan_start");
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d("aaa","stop");
                    mScanning = false;
                    mBluetoothAdapter.getBluetoothLeScanner().stopScan(mLeScanCallback);
                    invalidateOptionsMenu();
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.getBluetoothLeScanner().startScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.getBluetoothLeScanner().stopScan(mLeScanCallback);
        }
        invalidateOptionsMenu();
    }
    // Device scan callback.
    private ScanCallback mLeScanCallback =
            new ScanCallback() {
                @Override public void onScanResult ( int callbackType, ScanResult result) {
                    processResult (result);
                }

                @Override public void onBatchScanResults (List<ScanResult> results) { for (ScanResult result : results) {

                    processResult (result);
                }
                }

                @Override public void onScanFailed ( int errorCode) {
                    Log.d("aaa", String.valueOf(errorCode));
                }


                private void processResult ( final ScanResult result) {
                    runOnUiThread ( new Runnable () {
                        @Override public void run () {

                            if(result.getDevice().getName() != null){
                                Log.d("aaa",result.getDevice().getName().toString());
                            }
                            Log.d("processResult",result.getDevice().getName() + " "+result.getDevice().getAddress());
                            if(result.getDevice().getAddress().toString().equals(mDeviceAddr)){
                                sendCommandToBLE(result.getDevice(),bleCommand);
                                scanLeDevice(false);
                            }

                        }
                    });
                }
    };
    /**
     * @author JAESEONG LEE, lee01042000@gmail.com
     */
    private boolean alreadySend;
    private void sendCommandToBLE(BluetoothDevice device,String bleCommand){
        if(!alreadySend){
            alreadySend=true;
            //일일이 핀넘버 넣는게 귀찮아서 broadcastReceiver로 페어링 요청 발생할때 미리 잡아서 코드로 비밀번호 넣도록 처리했습니다
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_PAIRING_REQUEST);
            registerReceiver(mPairingRequestReceiver, filter);
            //connectGatt를 실행하기전 페어링을 진행해야 데이터 전송이 원활함
            if(pairDevice(device)){// 페어링 성공시
                Log.d(TAG, "Connect gatt start.");
                device.connectGatt(this,true,bluetoothGattCallback);
            }

        }
    }
    private boolean pairDevice(BluetoothDevice device) {
        try {
            Log.i(TAG, "Start Pairing... with: " + device.getName());
            device.createBond();
            Log.i(TAG, "Pairing finished.");
            return true;
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
            return false;
        }
    }
    //페어링요청 발생시 자동 페어링하는 브로드캐스트 리시버
    private final BroadcastReceiver mPairingRequestReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothDevice.ACTION_PAIRING_REQUEST)) {
                try {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    String pin="000000";
                    //the pin in case you need to accept for an specific pin
                    Log.d(TAG, "Start Auto Pairing. PIN = " + pin);
                    byte[] pinBytes;
                    pinBytes = pin.getBytes("UTF-8");
                    device.setPin(pinBytes);
                    //device.setPairingConfirmation(true);
                } catch (Exception e) {
                    Log.e(TAG, "Error occurs when trying to auto pair");
                    e.printStackTrace();
                }
            }
        }
    };
    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";
    private final static String TAG = "BLE_GATT";
    private BluetoothGattCharacteristic writeCharacteristic;
    private BluetoothGattCharacteristic readCharacteristic;
    private BluetoothGattCallback bluetoothGattCallback=new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                Log.i(TAG, "onConnectionStateChange : Connected to GATT server.");
                gatt.discoverServices();

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i(TAG, "onConnectionStateChange : Disconnected from GATT server.");
            }
        }
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i(TAG, "onServicesDiscovered : Connected to GATT server."+" Service Size = "+gatt.getServices().size());
                for(BluetoothGattService gattService: gatt.getServices()){
                    Log.i(TAG, "onServicesDiscovered : Service = "+gattService.getUuid());
//                    if(gattService.getUuid().equals("0000ffe0-0000-1000-8000-00805f9b34fb")){
//                        BluetoothGattCharacteristic bluetoothGattCharacteristic=gattService.getCharacteristic(UUID.fromString("0000ff01-0000-1000-8000-00805f9b34fb"));
//                        bluetoothGattCharacteristic.setValue(bleCommand.getBytes());
//                    }
                    for(BluetoothGattCharacteristic bluetoothGattCharacteristic: gattService.getCharacteristics()){
                        Log.i(TAG, "onServicesDiscovered : Characteristic = "+bluetoothGattCharacteristic.getUuid() + " " + bluetoothGattCharacteristic.getDescriptors().toString());
                    }
                }
                BluetoothGattService bluetoothGattService=gatt.getService(UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb"));
                writeCharacteristic=bluetoothGattService.getCharacteristic(UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb"));
                readCharacteristic=bluetoothGattService.getCharacteristic(UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb"));
                if((writeCharacteristic.getProperties() & (BluetoothGattCharacteristic.PROPERTY_WRITE +
                        BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)) ==0) {
                    Log.i(TAG, "onServicesDiscovered properties: cannot read and write");
                }
                else{
                    Log.i(TAG, "onServicesDiscovered properties: can read and write");
                }
//                try {
//                    Thread.sleep(3000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                setButton(gatt);
                gatt.readCharacteristic(readCharacteristic);
                gatt.setCharacteristicNotification(readCharacteristic,true);

            } else {
                Log.i(TAG, "onServicesDiscovered received: " + status);
            }
        }
        private void setButton(BluetoothGatt gatt){
            ((MaterialButton)findViewById(R.id.kickboard_button)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    writeCharacteristic.setValue(bleCommand.getBytes());
                    if(!gatt.writeCharacteristic(writeCharacteristic)){
                        Log.i(TAG,"write fail");
                    }
                    else{
                        Log.i(TAG,"write started, len="+bleCommand.getBytes().length);
//                        Toast.makeText(getApplicationContext(),"onCharacteristicWrite : " +bleCommand.getBytes().length
//                                ,Toast.LENGTH_SHORT).show();
                    }
                }
            });
            new Thread(
                    new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ((MaterialButton)findViewById(R.id.kickboard_button)).setVisibility(View.VISIBLE);
                                    ((MaterialButton)findViewById(R.id.kickboard_button)).invalidate();
                                }
                            });
                        }
                    }
            ).start();
        }
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            Log.i(TAG, "onCharacteristicRead : " +  characteristic.getValue());
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            if(status != BluetoothGatt.GATT_SUCCESS) {
                Log.i(TAG, "onCharacteristicWrite : write fail");
            }
            Log.i(TAG, "onCharacteristicWrite : " + new String(characteristic.getValue()));
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            String receivedMsg=new String(characteristic.getValue());
            Log.i(TAG, "onCharacteristicChanged : " +receivedMsg);
            if(receivedMsg.equals(bleReceive)){
                setResult(RESULT_OK);
                finish();
            }
            Toast.makeText(getApplicationContext(),"received : "+characteristic.getValue()[0],Toast.LENGTH_SHORT).show();
        }
    };


    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }

}
