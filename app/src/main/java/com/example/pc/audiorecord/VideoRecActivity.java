package com.example.pc.audiorecord;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;


public class VideoRecActivity extends AppCompatActivity {

    private String EXTERAL_STORAGE_PATH = " ";
    private String RECORED_FILE = "video_recored";
    private static int fileIndex = 0;
    private String fileName = " ";

    private MediaPlayer player;
    private MediaRecorder recorder;

    private Camera camera = null;
    private SurfaceHolder holder; //서페이스뷰는 찍은 사진을 바로바로 빠르게 보여주는것인데 이때 홀더를 이용하여 뿌리는 작업을 함

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_rec);

        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                }, 1);

        findViewById(R.id.btnRec).setOnClickListener(btnClick); // 클릭만할경우 findView의 셋온클릭리스러를 사용하여 버튼 클릭시 찾아서 사용하라는것
        findViewById(R.id.btnRecStop).setOnClickListener(btnClick);
        findViewById(R.id.btnPlay).setOnClickListener(btnClick);
        findViewById(R.id.btnPlayStop).setOnClickListener(btnClick);

        //sd카드가 있는지 없는지를 체크한다.
        String state = Environment.getExternalStorageState();
        if (!state.equals(Environment.MEDIA_MOUNTED)){
            Toast.makeText(VideoRecActivity.this, "SD카드가 없습니다!!", Toast.LENGTH_SHORT).show();
        }else{
            EXTERAL_STORAGE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
        }

        //surfaceView를 만든다.
        SurfaceView surface = new SurfaceView(this); //서페이스 뷰를 생성
        holder = surface.getHolder(); //뷰에서 홀더를 가져옴
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        FrameLayout frame = (FrameLayout)findViewById(R.id.videoLayout);
        frame.addView(surface);
    }

    private View.OnClickListener btnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btnRec: //녹화

                    if(recorder == null){
                        recorder = new MediaRecorder();
                    }

                    recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
                    recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                    recorder.setAudioEncoder(MediaRecorder.VideoEncoder.DEFAULT);
                    recorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);

                    fileName = createFileName();

                    recorder.setOutputFile(fileName);

                    //레코더가 카메라로 찍은 영상을 surfaceView에 뿌려준다.
                    recorder.setPreviewDisplay(holder.getSurface());
                    try {
                        recorder.prepare();
                        recorder.start();
                    }catch (Exception e){
                        e.printStackTrace();
                        recorder.release();
                        recorder = null;
                    }

                    break;

                case R.id.btnRecStop:
                    if(recorder == null) return;

                    recorder.stop();
                    recorder.reset();
                    recorder.release();
                    recorder = null;

                    ContentValues values = new ContentValues(10); //provider 앨범에서 가져오는 것
                    values.put(MediaStore.MediaColumns.TITLE, "RecordedVideo");
                    values.put(MediaStore.Video.Media.ALBUM, "Vedio Alibum");
                    values.put(MediaStore.Audio.Media.ARTIST, "SUYOUNG");
                    values.put(MediaStore.Audio.Media.DISPLAY_NAME, "Recorded Vedio");
                    values.put(MediaStore.MediaColumns.DATE_ADDED, System.currentTimeMillis() /1000);
                    values.put(MediaStore.MediaColumns.MIME_TYPE, "Vedio Alibum");
                    values.put(MediaStore.Audio.Media.DATA, fileName);

                    Uri videoUri = getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
                    if (videoUri == null){
                        Toast.makeText(VideoRecActivity.this, "비디오 저장 실패!!!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, videoUri)); //안전하게 브로드캐스트로

                    break;

                //녹음 파일 재생 버튼
                case R.id.btnPlay:
                    if(player == null){
                        player = new MediaPlayer();
                    }
                    try{
                        player.setDataSource(fileName);
                        player.setDisplay(holder);

                        player.prepare();
                        player.start();
                    }catch (Exception e){
                        e.printStackTrace();
                        //Log.e(TAG, "Video play failed", e);
                    }

                    break;
            }
        }
    }; //end btnClick

    //접근제어자(modifier) => private : 저장하는 경로+ 파일이름을 만들어 주는 메소드
    private String createFileName(){
        fileIndex++;

        String newFileName = " ";
        if (EXTERAL_STORAGE_PATH == null || EXTERAL_STORAGE_PATH.equals("")) {
            //use internal memory
            newFileName = RECORED_FILE + fileIndex + ".mp4";
        }else{
            newFileName = EXTERAL_STORAGE_PATH + "/" + RECORED_FILE +fileIndex + ".mp4";
        }
        return newFileName;
    }
}
