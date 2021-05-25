package com.example.wearinghelmetapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;














public class MainActivity extends AppCompatActivity {
    
    private static final int REQUEST_ENABLE_BT = 10; // 블루투스 활성화 상태
    private BluetoothAdapter bluetoothAdapter; // 블루투스 어댑터
    private OutputStream outputStream = null; // 블루투스에 데이터를 출력하기 위한 출력 스트림
    private InputStream inputStream = null; // 블루투스에 데이터를 입력하기 위한 입력 스트림

    private BluetoothDevice bluetoothDevice; // 블루투스 디바이스
    private BluetoothSocket bluetoothSocket = null; // 블루투스 소켓

    
    Set<BluetoothDevice> devices;  //블루투스 디바이스 데이터셋
    ArrayAdapter<String> btArrayAdapter;
    ArrayList<String> deviceAddressArray;   `

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); // 블루투스 어댑터를 디폴트 어댑터로 설정
        btArrayAdapter= new ArrayAdapter<>(this,android.R.layout.simple_list_item_1);
        deviceAddressArray= new ArrayList<>();
        //listView.setAdapter(btArrayAdapter);


        if(bluetoothAdapter.isEnabled()) { // 블루투스가 활성화 상태 (기기에 블루투스가 켜져있음)

            connectBluetoothDevice("블루투스 모듈명 필요"); // 블루투스 디바이스 연결함수 호출             
        }

        else { // 블루투스가 비 활성화 상태이면

            // 블루투스를 활성화 하기 위한 다이얼로그 출력
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE); 
            // 선택한 값이 onActivityResult 함수에서 콜백된다.

            startActivityForResult(intent, REQUEST_ENABLE_BT); 

        }


    
    }

    @Override   //onAcitivityResul함수 오버라이드
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode != ACTIVITY.RESULT_OK) {
                return;
            } 
            connectBluetoothDevice("블루투스모듈명 필요");  //블루투스 디바이스 선택함수 호출
        } 
 
    }

    public void connectBluetoothDevice(String deviceName)  //인자로 디바이스명 건네주기
    {
        btArrayAdapter.clear();
        if(deviceAddressArray!=null && !deviceAddressArray.isEmpty()){ deviceAddressArray.clear(); }
        devices = btAdapter.getBondedDevices();   //디바이스 목록들 불러오기..
        if (devices.size() > 0) {
            for (BluetoothDevice device : devices) {     
                String deviceName = device.getName();               //이름 저장
                String deviceHardwareAddress = device.getAddress(); // MAC address 저장

                btArrayAdapter.add(deviceName);                  //각각 추가하기
                deviceAddressArray.add(deviceHardwareAddress);
            }
        }

        for (BluetoothDevice tempDevice : devices){     //device목록에 들어가 블루투스 중에 deviceName과 일치하는 블루투스가 있다면 알아내기
            if (deviceName.equals(tempDevice.getName()))
            {
                bludtoothDevice=tempDevice;
                break;
            }
        }
       

        //연결해주기
        UUID uuid = java.util.UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    
        try {

            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
    
            bluetoothSocket.connect();
    
            // 데이터 송,수신 스트림을 얻어옵니다.
    
            outputStream = bluetoothSocket.getOutputStream();
    
            inputStream = bluetoothSocket.getInputStream();
    
            // 데이터 송/수신 함수 호출
    
            receiveData();
            sednData();
    
        } catch (IOException e) {
    
            e.printStackTrace();
    
        }

        
    
    }
   

    
}





출처: https://yeolco.tistory.com/80 [열코의 프로그래밍 일기]


    }
   
        


출처: https://yeolco.tistory.com/80 [열코의 프로그래밍 일기]
        devices = bluetoothAdapter.getBondedDevices();
        pariedDeviceCount = devices.size();
        // 페어링 되어있는 장치가 없는 경우

        if(pariedDeviceCount == 0) {

        // 페어링을 하기위한 함수 호출

        }

        // 페어링 되어있는 장치가 있는 경우

        else {

            // 디바이스를 선택하기 위한 다이얼로그 생성

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("페어링 되어있는 블루투스 디바이스 목록");

            // 페어링 된 각각의 디바이스의 이름과 주소를 저장

            List<String> list = new ArrayList<>();

            // 모든 디바이스의 이름을 리스트에 추가

            for(BluetoothDevice bluetoothDevice : devices) {

                list.add(bluetoothDevice.getName());

            }

            list.add("취소");


            // List를 CharSequence 배열로 변경

            final CharSequence[] charSequences = list.toArray(new CharSequence[list.size()]);

            list.toArray(new CharSequence[list.size()]);
            connectDevice(charSequences[which].toString());



    }

}


    



