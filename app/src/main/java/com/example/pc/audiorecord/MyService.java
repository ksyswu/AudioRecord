package com.example.pc.audiorecord;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

//서비스란 화면이없는 백그라운드에서 실행되는 액티비티

public class MyService extends Service {

    private int count = 0;

    public MyService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Thread thread = new Thread(run);
        thread.start();
    }

    private Runnable run = new Runnable() {
        @Override
        public void run() {

            try{
                while(true){
                    Log.i("TEST", "My Sercive Called #" + count);
                    count++;
                    Thread.sleep(2000);
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    };

    @Override
    public IBinder onBind(Intent intent) {

        throw new UnsupportedOperationException("Not yet implemented");

    }
}
