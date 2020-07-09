package com.example.cp_cop_0621;
import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.WindowManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class WeatherService extends IntentService {
    final String WEATHER_KEY = "6c9f31537ffb86c0976ebee100f53d19";

    private int weather_id; // 날씨
    private int humidity; // 습도

    public static int count=0;

    public WeatherService() {
        super("WeatherService");
    }

    @Override
    protected void onHandleIntent(final Intent intent) {

        Thread t = new Thread(){
            @Override
            public void run() {
                while (isAlive()){
                    if(count%120 == 0) {
                        new getWeatherDataTask(intent).execute();
                        count=0;
                    }else {

                        Intent send = new Intent();
                        send.setAction("GET_WEATHER_DATA");
                        send.putExtra("weather_id", weather_id);
                        send.putExtra("humidity", humidity);

                        sendBroadcast(send);
                    }
                    try {
                        sleep(500);
                        count++;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        t.start();
    }

    private class getWeatherDataTask extends AsyncTask<String, Void, String> {

        private Intent intentRecv;
        private Intent intentSend;

        public getWeatherDataTask(Intent intent){
            this.intentRecv = intent;
            this.intentSend = new Intent();
            intentSend.setAction("GET_WEATHER_DATA");
        }

        @Override
        protected String doInBackground(String... strings) {
            URL url;
            String buf = "";
            String msg="";
            try {
                url = new URL("http://api.openweathermap.org/data/2.5/weather?q=Ansan,KR,&appid="+WEATHER_KEY);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                if(conn.getResponseCode() == conn.HTTP_OK){
                    InputStreamReader isr = new InputStreamReader(conn.getInputStream());
                    BufferedReader br = new BufferedReader(isr);
                    StringBuffer sb = new StringBuffer();

                    while((buf = br.readLine()) != null){
                        sb.append(buf);
                    }
                    msg = sb.toString();
                    br.close();

                }else{
                    Log.e("URLConnectionError", conn.getResponseCode()+" Error");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return msg;
        }

        @Override
        protected void onPostExecute(String json) {
            try {
                JSONObject jo = new JSONObject(json);
                JSONObject weather = jo.getJSONArray("weather").getJSONObject(0);

                setWeatherID(weather.optInt("id", 800));

                intentSend.putExtra("weather_id", weather_id);
                JSONObject main = jo.getJSONObject("main");

                setHumidity(main.optInt("humidity", 0));
                intentSend.putExtra("humidity", humidity);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            sendBroadcast(intentSend);
            super.onPostExecute(json);
        }
    }

    public void setWeatherID(int val) {
        this.weather_id = val;
    }
    public void setHumidity(int val) {
        this.humidity = val;
    }
}

