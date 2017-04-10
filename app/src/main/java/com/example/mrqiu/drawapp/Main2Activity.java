package com.example.mrqiu.drawapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mrqiu.drawapp.frame.FunctionFrame;
import com.example.mrqiu.drawapp.frame.PenColorFrame;
import com.example.mrqiu.drawapp.frame.WidthFrame;
import com.example.mrqiu.drawapp.utils.PhotoUtil;
import com.example.mrqiu.drawapp.widget.ZoomDrawView;

public class Main2Activity extends AppCompatActivity {
    private static final int MODE_PEN = 0x1;
    private static final int MODE_COLOR_PEN = 0x12;
    private static final int CHOOSE_PHOTO = 0x132;

    private ZoomDrawView zdv;
    private FragmentManager mFragmentManager;

    private WidthFrame mPenWidthFrame;
    private WidthFrame mEraserWidthFrame;
    private PenColorFrame mPenColorFrame;
    private FunctionFrame mFunctionFrame;
    private int alpha;
    private int red;
    private int green;
    private int blue;

    private int mMode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        mMode = MODE_PEN;
        zdv = (ZoomDrawView) findViewById(R.id.zdv);
        mFragmentManager = getFragmentManager();

        mFunctionFrame = FunctionFrame.newInstance(new OnFunctionClickListener() {
            @Override
            public void clickPen() {
                mMode = MODE_PEN;
                zdv.setPenColor(255, red, green, blue);
                zdv.setMode(ZoomDrawView.MODE_PEN);
            }

            @Override
            public void clickEraser() {
                zdv.setMode(ZoomDrawView.MODE_ERASER);
            }

            @Override
            public void clickPenColor() {
                mFragmentManager.beginTransaction().replace(R.id.fl_content, mPenColorFrame).commit();
            }

            @Override
            public void clickColorPen() {
                mMode = MODE_COLOR_PEN;
                zdv.setMode(ZoomDrawView.MODE_PEN);
                zdv.setPenColor(125, red, green, blue);

            }

            @Override
            public void clickPenWidth() {
                mFragmentManager.beginTransaction().replace(R.id.fl_content, mPenWidthFrame).commit();
            }

            @Override
            public void clickEraserWidth() {
                mFragmentManager.beginTransaction().replace(R.id.fl_content, mEraserWidthFrame).commit();
            }


            @Override
            public void clickSaveImage() {
                Bitmap bitmap = zdv.outBitmap();
                String savePath = PhotoUtil.saveToLocal(bitmap);
                Toast.makeText(Main2Activity.this, savePath, Toast.LENGTH_SHORT).show();
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,Uri.parse("file://"+savePath)));


            }

            @Override
            public void clickSrcImage() {
                if(ContextCompat.checkSelfPermission(Main2Activity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(Main2Activity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }else {
                    openAlbum();
                }

            }
        });

        mPenColorFrame = PenColorFrame.newInstance(new OnChangePenColorListener() {

            @Override
            public void onChangePenColor(int alpha, int red, int green, int blue) {
                Main2Activity.this.red = red;
                Main2Activity.this.green = green;
                Main2Activity.this.blue = blue;
                switch (mMode) {
                    case MODE_COLOR_PEN:
                        Main2Activity.this.alpha = 125;
                        break;
                    case MODE_PEN:
                        Main2Activity.this.alpha = 255;
                        break;
                }
                zdv.setPenColor(Main2Activity.this.alpha, red, green, blue);
            }
        }, new OnFrameBackListener() {
            @Override
            public void onBack() {
                mFragmentManager.beginTransaction().replace(R.id.fl_content, mFunctionFrame).commit();
            }
        });
        mPenWidthFrame = WidthFrame.newInstance(zdv.getPenWidth(), new OnChangeWidthListener() {
            @Override
            public void onChangeWidth(int width) {
                zdv.setPenWidth(width);

            }
        }, new OnFrameBackListener() {
            @Override
            public void onBack() {
                mFragmentManager.beginTransaction().replace(R.id.fl_content, mFunctionFrame).commit();
            }
        });


        mEraserWidthFrame = WidthFrame.newInstance(zdv.getEraserWidth(), new OnChangeWidthListener() {
            @Override
            public void onChangeWidth(int width) {
                zdv.setEraserWidth(width);
            }
        }, new OnFrameBackListener() {
            @Override
            public void onBack() {
                mFragmentManager.beginTransaction().replace(R.id.fl_content, mFunctionFrame).commit();
            }
        });


        mFragmentManager.beginTransaction().replace(R.id.fl_content, mFunctionFrame).commit();

    }


    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CHOOSE_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    if (Build.VERSION.SDK_INT >= 19) {
                        handleImageOnKiKat(data);
                    } else {
                        handleImageBeforeKiKAt(data);
                    }
                }
        }
    }

    private void handleImageBeforeKiKAt(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);

    }

    @TargetApi(19)
    private void handleImageOnKiKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];//解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            //如果时content类型的URI 则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            //如果是file类型的uri ，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        //通过Uri 和selection 累获取真是的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath) {
        zdv.setSrcImage(imagePath);
    }


}
