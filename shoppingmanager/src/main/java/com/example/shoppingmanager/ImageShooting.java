package com.example.shoppingmanager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.File;
import java.io.FileOutputStream;

public class ImageShooting extends AppCompatActivity{
    Button mShutter;
    MyCameraSurface mSurface;
    String mRootPath;
    static final String PICFOLDER = "CameraTest";
    String barcodeNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_shooting);


        mSurface = (MyCameraSurface)findViewById(R.id.previewFrame);
        mShutter = (Button)findViewById(R.id.button_capture);

        Button buttonExit = (Button) findViewById(R.id.button_exit);

        buttonExit.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }
        );

//        mShutter.setOnClickListener(new Button.OnClickListener() {
//            public void onClick(View v) {
//                // 사진을 촬영
//
//            }
//        });

        //저장할 공간 /mnt/sdcard/CameraTest 이렇게 폴더 안에 파일이 생성된다
        mRootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + PICFOLDER;
        File fRoot = new File(mRootPath);

        if (fRoot.exists() == false) {
            if (fRoot.mkdir() == false) {
                Toast.makeText(this, "사진을 저장할 폴더가 없습니다.", Toast.LENGTH_LONG).show();
//                finish();

                return;
            }
        }
    }

    public void onClickcapture(View v) {
        mSurface.mCamera.autoFocus(mAutoFocus);
    }

    // 포커싱 성공하면 촬영 허가
    Camera.AutoFocusCallback mAutoFocus = new Camera.AutoFocusCallback() {

        public void onAutoFocus(boolean success, Camera camera) {
//            mShutter.setEnabled(true);
            mSurface.mCamera.takePicture(null, null, mPicture);
        }
    };

    // 사진 저장.
    Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        public void onPictureTaken(byte[] data, Camera camera) {
            String FileName = "save.jpg";
            String path = mRootPath + "/" + FileName;

            File file = new File(path);
            try {
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(data);
                fos.flush();
                fos.close();
            } catch (Exception e) {

                return;
            }

            //파일을 갤러리에 저장
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri = Uri.parse("file://" + path);
            intent.setData(uri);
            sendBroadcast(intent);

            Toast.makeText(getApplicationContext(), "사진이 저장 되었습니다"+file.getAbsolutePath(), Toast.LENGTH_LONG).show();
            camera.startPreview();

            barcodeNumber = DecodeBarcode(file.getAbsolutePath());

            if(barcodeNumber != null) {
//                Intent intentBarcode = new Intent(getApplicationContext(), BarcodeActivity.class);
//                intentBarcode.putExtra("barcodeNumber", barcodeNumber);
//                startActivity(intentBarcode);
            }
            else {
                Toast.makeText(getApplicationContext(), "다시 촬영", Toast.LENGTH_LONG).show();
            }
        }

    };

    // 저장된 이미지경로에서 이미지 읽어옴.
    public String DecodeBarcode(String imagePath) {

        Bitmap myBitmap = BitmapFactory.decodeFile(imagePath);
//        myImageView.setImageBitmap(myBitmap);
        BarcodeDetector detector =
                new BarcodeDetector.Builder(getApplicationContext())
                        .setBarcodeFormats(Barcode.QR_CODE | Barcode.EAN_13 | Barcode.EAN_8 | Barcode.CODE_39 | Barcode.CODE_93 | Barcode.CODE_128)
                        .build();
        if (!detector.isOperational()) {
//            txtView.setText("Could not set up the detector!");
            return null;
        }

        Frame frame = new Frame.Builder().setBitmap(myBitmap).build();
        SparseArray<Barcode> barcodes = detector.detect(frame);

        try {
            Barcode thisCode = barcodes.valueAt(0);
            return thisCode.rawValue;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
