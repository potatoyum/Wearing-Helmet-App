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
import android.content.Context;
import android.content.Intent;
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
    private String bleCommand;

    private static final int REQUEST_ENABLE_BT = 1;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 50000;
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS",BLE_COMMAND="BLE_COMMAND";
    private final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        //getActionBar().setTitle(R.string.title_devices);
        mHandler = new Handler(Looper.myLooper());

        final Intent intent = getIntent();
        mDeviceAddr = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS); //qr 인식한 mac 주소
        bleCommand=intent.getStringExtra(BLE_COMMAND);
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

        //filters = new ArrayList<ScanFilter>();
        //ScanFilter filter = new ScanFilter.Builder().setDeviceName("device명");

        //스캔 시작
        scanLeDevice(true);
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        if (!mScanning) {
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(true);
            menu.findItem(R.id.menu_refresh).setActionView(null);
        } else {
            menu.findItem(R.id.menu_stop).setVisible(true);
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(
                    R.layout.actionbar_indeterminate_progress);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_scan:
                mLeDeviceListAdapter.clear();
                scanLeDevice(true);
                break;
            case R.id.menu_stop:
                scanLeDevice(false);
                break;
        }
        return true;
    }
*/
    @Override
    protected void onResume() {
        super.onResume();

        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        // Initializes list view adapter.
        //mLeDeviceListAdapter = new LeDeviceListAdapter();
       // setListAdapter(mLeDeviceListAdapter);
        //scanLeDevice(true);
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
        //scanLeDevice(false);
        //mLeDeviceListAdapter.clear();
    }
/*
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        final BluetoothDevice device = mLeDeviceListAdapter.getDevice(position);
        if (device == null) return;
        final Intent intent = new Intent(this, DeviceControlActivity.class);
        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, device.getName());
        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
        if (mScanning) {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            mScanning = false;
        }
        startActivity(intent);
    }
*/

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

    /*
    // Adapter for holding devices found through scanning.
    private class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<BluetoothDevice> mLeDevices;
        private LayoutInflater mInflator;

        public LeDeviceListAdapter() {
            super();
            mLeDevices = new ArrayList<BluetoothDevice>();
            mInflator = DeviceScanActivity.this.getLayoutInflater();
        }

        public void addDevice(BluetoothDevice device) {
            if(!mLeDevices.contains(device)) {
                mLeDevices.add(device);
            }
        }

        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            // General ListView optimization code.
            if (view == null) {
                view = mInflator.inflate(R.layout.listitem_device, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            BluetoothDevice device = mLeDevices.get(i);
            final String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0)
                viewHolder.deviceName.setText(deviceName);
            else
                viewHolder.deviceName.setText(R.string.unknown_device);
            viewHolder.deviceAddress.setText(device.getAddress());

            return view;
        }
    }
*/

    private boolean alreadySend;
    private void sendCommandToBLE(BluetoothDevice device,String bleCommand){
        if(!alreadySend){
            alreadySend=true;
            BluetoothGatt bluetoothGatt=device.connectGatt(this,true,bluetoothGattCallback);

        }
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
                            }

                        }
                    });
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
                BluetoothGattCharacteristic bluetoothGattCharacteristic=bluetoothGattService.getCharacteristic(UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb"));
                Log.i(TAG, "onServicesDiscovered character: "+bluetoothGattCharacteristic.toString());
                bluetoothGattCharacteristic.setValue("bleCommand".getBytes());
                gatt.readCharacteristic(bluetoothGattCharacteristic);

            } else {
                Log.i(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            Log.i(TAG, "onCharacteristicRead : " +  characteristic.getValue());
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Log.i(TAG, "onCharacteristicWrite : " + characteristic.getStringValue(0));
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            Log.i(TAG, "onCharacteristicChanged : " );
        }
    };

    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }

}
