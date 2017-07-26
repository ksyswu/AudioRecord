package com.example.pc.audiorecord;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static String RECORD_FILE;

    private MediaPlayer player; //재생버튼클릭시 사용 클래스
    private MediaRecorder recorder; // 녹음시 사용 클래스


   // Toast tst = Toast.makeText(MainActivity.this, " ", Toast.LENGTH_SHORT);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //서비스실행
        Intent myService = new Intent(this, MyService.class);
       // startService(myService);

        findViewById(R.id.btnRec).setOnClickListener(btnClick); // 클릭만할경우 findView의 셋온클릭리스러를 사용하여 버튼 클릭시 찾아서 사용하라는것
        findViewById(R.id.btnRecStop).setOnClickListener(btnClick);
        findViewById(R.id.btnPlay).setOnClickListener(btnClick);
        findViewById(R.id.btnPlayStop).setOnClickListener(btnClick);

        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                }, 1);


        //SD카드에 저장되는 이름 지정
        File sdcard = Environment.getExternalStorageDirectory();
        File file = new File(sdcard, "recorded.mp4");
        RECORD_FILE = file.getAbsolutePath();  // 이 파일을 실행하기 위해서는 sdcard가 있어야 함
    }

    private View.OnClickListener btnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btnRec: //녹음

                    //녹음은 용량이 커서 앱내부에 저장하지 못함 따라서 sd카드에 저장 해야 함

                    if (recorder != null){
                        //메모리 해제 작업 -> 안하면 앱이 죽음 ,특히 이미지
                        recorder.stop();
                        recorder.release();
                        recorder = null;
                    }

                    recorder = new MediaRecorder();

                    recorder.setAudioSource(MediaRecorder.AudioSource.MIC); //레코드시 마이크 사용하겠다
                    recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4); //엠피4로 뽑아 내겠다
                    recorder.setAudioEncoder(MediaRecorder.AudioSource.DEFAULT);

                    recorder.setOutputFile(RECORD_FILE);

                    try{
                        //Toast tst = Toast.makeText(MainActivity.this, "녹음을 시작합니다.", Toast.LENGTH_SHORT);
                        //tst.show();
                        ToastUtil.showToast(MainActivity.this, "녹음을 시작합니다.");


                        recorder.prepare();
                        recorder.start();

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;

                case R.id.btnRecStop:
                    if (recorder == null) return;

                    recorder.stop();
                    recorder.release();
                    recorder = null;
                    break;

                //녹음 파일 재생 버튼
                case R.id.btnPlay:
                    if (player != null){
                        player.stop();
                        player.release();
                        player = null;
                    }

                    //Toast tst2 = Toast.makeText(MainActivity.this, "녹음된 파일을 재생합니다.", Toast.LENGTH_SHORT);
                    //tst2.show();
                    ToastUtil.showToast(MainActivity.this, "녹음된 파일을 재생합니다.");

                    try{
                        player = new MediaPlayer();
                        player.setDataSource(RECORD_FILE);
                        player.prepare();
                        player.start();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
            }
        }

    };
}
