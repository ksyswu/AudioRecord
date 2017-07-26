package com.example.pc.audiorecord;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

public class CaptureActivity extends AppCompatActivity {

    public  static  final  int REQ_IMG_CAPTURE = 1001;

    private File file = null;
    private ImageView mImgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);

        try{
            String imageFileName = "test.jpg";
            File storageDir = Environment.getExternalStorageDirectory();
            file = new File(storageDir, imageFileName);
        }catch (Exception e){
            e.printStackTrace();
        }

        mImgView = (ImageView)findViewById(R.id.imageView);
        findViewById(R.id.btnCapture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //사진찍기 버튼 클릭시
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //인텐트 값을 바꿔주면 값이 바뀜...?
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                if(intent.resolveActivity(getPackageManager()) != null ){ //전체 패키지에서 캡쳐할수 있는 애를 찾아서 널이 아니면 캡쳐해오는 것
                    startActivityForResult(intent, REQ_IMG_CAPTURE); // 스타트를 통해 카메라 실행후 피니쉬되면 결과값이 넣오는데 이 번호가 결과값의 번호 여기서는 1001로 설정해 놓음
                }

            }
        }); //end onClickListener
    } //end onCreate()

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { //호출한 1001번을 통해 콜백이 들어옴
        if(requestCode == REQ_IMG_CAPTURE && resultCode == RESULT_OK){
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8; //8분의 1사이즈로 만들어 저장
            if (file != null){
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
                mImgView.setImageBitmap(bitmap);
            }else{
                Toast.makeText(getApplicationContext(), "캡쳐 실패", Toast.LENGTH_SHORT).show();
            }
        }//end if
    }//end method
}//end class Activity
