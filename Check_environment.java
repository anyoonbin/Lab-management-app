package com.example.cp_cop_0621;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.StrictMode;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

public class Check_environment extends AppCompatActivity {

    private BluetoothSPP bt;
    TextView text;
    private TextView tv_weather;
    private TextView tv_humidity;
    int hit = 0;
    int count = 0;


    public String getWeatherName(int id){
        if(id == 800){
            return "맑음";
        }
        else if(id >= 803){ // 흐림
            return "흐림";
        }
        else if(id == 741){ // 흐림
            return "안개";
        }
        else if(id >= 700){ // 흐림
            return "흐림";
        }
        else if(id >= 600){ // 눈
            return "눈";
        }
        else if(id >= 300){ // 비
            return "비";
        }
        else if(id >= 200){ // 비/번개
            return "비";
        }
        else{
            return "맑음";
        }
    }
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("GET_WEATHER_DATA")){
                setWeatherID(intent.getIntExtra("weather_id", 800));
                setHumidity(intent.getIntExtra("humidity", 0));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_environment);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        bt = new BluetoothSPP(this); //Initializing
        text = (TextView)findViewById(R.id.temp);
        tv_weather = findViewById(R.id.weather);
        tv_humidity = findViewById(R.id.humid);



        if (!bt.isBluetoothAvailable()) { // 블루투스 사용 불가
            Toast.makeText(getApplicationContext(), "Bluetooth is not available", Toast.LENGTH_SHORT).show();
            finish();
        }

        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() { // 데이터 수신
            public void onDataReceived(byte[] data, String message) {
                text.setText(message);
                createNotification(message);
                hit++;
            }
        });

        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() { // 연결 됐을 때
            public void onDeviceConnected(String name, String address) {
                Toast.makeText(getApplicationContext(), "Connected to " + name + "\n" + address, Toast.LENGTH_SHORT).show();
            }

            public void onDeviceDisconnected() { // 연결 해제
                Toast.makeText(getApplicationContext(), "Connection lost", Toast.LENGTH_SHORT).show();
            }

            public void onDeviceConnectionFailed() { // 연결 실패
                Toast.makeText(getApplicationContext(), "Unable to connect", Toast.LENGTH_SHORT).show();
            }
        });

        Button btnConnect = findViewById(R.id.btn_connect); // 연결 시도
        btnConnect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (bt.getServiceState() == BluetoothState.STATE_CONNECTED) {
                    bt.disconnect();
                } else {
                    Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                    startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
                }
            }
        });


    }

    //알림 생성
    public void createNotification(String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");

        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("-- 주의 --");
        builder.setContentText("실험실 온도가 높으니 화학약품 취급 시 주의하세요!");

        builder.setColor(Color.RED);
        // 사용자가 탭을 클릭하면 자동 제거
        builder.setAutoCancel(true);

        // 알림 표시
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_DEFAULT));
        }

        if (Float.valueOf(message) > 30 && count == 0) {
            // id값은 정의해야하는 각 알림의 고유한 int값
            notificationManager.notify(1, builder.build());
            count = 1;

        }
        if(hit == 20){
            count = 0;
            hit =  0;
        }
    }

    public void setWeatherID(int val) {
        tv_weather.setText(getWeatherName(val));
    }
    public void setHumidity(int val) {
        tv_humidity.setText(val+" %");
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("GET_WEATHER_DATA");
        registerReceiver(receiver, filter);
    }

    public void onStart() {
        super.onStart();
        if (!bt.isBluetoothEnabled()) { //
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            if (!bt.isServiceAvailable()) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER); //DEVICE_ANDROID는 안드로이드 기기 끼리
                setup();
            }
        }
    }

    public void onDestroy() {
        super.onDestroy();
        bt.stopService(); //블루투스 중지
        unregisterReceiver(receiver);
    }

    public void setup() {
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK)
                bt.connect(data);
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
                setup();
            } else {
                Toast.makeText(getApplicationContext()
                        , "Bluetooth was not enabled."
                        , Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

}
