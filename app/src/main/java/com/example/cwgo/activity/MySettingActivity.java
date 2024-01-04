package com.example.cwgo.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cwgo.R;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MySettingActivity extends AppCompatActivity {
    private ImageView back,logo;
    private EditText username, userid, userpassword, email, usersign;
    private Button regist;
    protected static Uri temUri;
    private Bitmap photo;



//    Handler handlerPra = new Handler(){
//        @Override
//        public void handleMessage(Message msg){
//            MyInfo = (ContentInfo) msg.obj;
//
//            user_image.setImageURL(" /uploadavatar/"+MyInfo.getAvatar());
//            user_name.setText(MyInfo.getNick());
//            user_sign.setText(MyInfo.getSign());
//        }
//    };

    //这里引入有问题啊啊啊
    //private List<LocalMedia> selectList = new ArrayList<>();
    String path="";
    //private GridImageAdapter adapter;

//    Handler handlerPra = new Handler(){
//        @Override
//        public void handleMessage(Message msg){
//            MyInfo = (ContentInfo) msg.obj;
//
//            user_image.setImageURL(" /uploadavatar/"+MyInfo.getAvatar());
//            user_name.setText(MyInfo.getNick());
//            user_sign.setText(MyInfo.getSign());
//        }
//    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_mysetting);
        logo = (ImageView) findViewById(R.id.r_logo);
        username =(EditText) findViewById(R.id.setting_username);
        userid =(EditText) findViewById(R.id.setting_id);
        userpassword =(EditText) findViewById(R.id.setting_password);
        email =(EditText) findViewById(R.id.setting_email);
        regist =(Button) findViewById(R.id.regist);
        back = (ImageView) findViewById(R.id.back);

//        logo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showDialogBox();
//            }
//        });

        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent nextIntent = new Intent(MySettingActivity.this, MainActivity.class);
                startActivity(nextIntent);
                finish();
            }
        });

        regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }


//    protected void showDialogBox(){
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        setTitle("设置头像");
//        String[] items = {"拍照","选择本地照片"};
//        builder.setNegativeButton("取消",null);
//        builder.setItems(items, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                switch (i){
//                    case 0://拍照
//                        /*if(ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
//                            ActivityCompat.requestPermissions(RegisterActivity.this,new String[]{Manifest.permission.CAMERA},0);
//                        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                            File uriFile = new File(AlbumUtil.getPath(RegisterActivity.this, temUri));
//                            temUri = FileProvider.getUriForFile(RegisterActivity.this, getPackageName(),uriFile);
//                        } else
//                            temUri = Uri.fromFile(new File(Environment.getDownloadCacheDirectory(),"image.jpg"));
//                        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,temUri);
//                        startActivityForResult(openCameraIntent,0);*/
//                        PictureSelector.create(RegisterActivity.this)
//                                .openCamera(PictureMimeType.ofImage())
//                                .forResult(PictureConfig.CHOOSE_REQUEST);
//                        break;
//                    case 1://选择本地图片
//                        /*if(ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
//                            ActivityCompat.requestPermissions(RegisterActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},0);
//                        Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
//                        openAlbumIntent.setType("image/*");
//                        startActivityForResult(openAlbumIntent,1);*/
//                        PictureSelector.create(RegisterActivity.this)
//                                .openGallery(PictureMimeType.ofImage())
//                                .maxSelectNum(1)
//                                .minSelectNum(1)
//                                .imageSpanCount(4)
//                                .selectionMode(PictureConfig.MULTIPLE)
//                                .enableCrop(true)
//                                .withAspectRatio(1, 1)
//                                .forResult(PictureConfig.CHOOSE_REQUEST);
//                        break;
//                }
//            }
//        });
//        builder.create().show();
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        List<LocalMedia> images;
//        if (resultCode == RESULT_OK) {
//            if (requestCode == PictureConfig.CHOOSE_REQUEST) {
//
//                images = PictureSelector.obtainMultipleResult(data);
//                selectList.addAll(images);
//                LocalMedia media = images.get(0);
//                if (media.isCut() && !media.isCompressed())
//                    path = media.getCutPath();
//
//                Bitmap bitmap = BitmapFactory.decodeFile(path);
//                logo.setImageBitmap(bitmap);
//            }
//        }
//    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode== 0){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Handler handler=new Handler(Looper.getMainLooper());
                handler.post(new Runnable(){
                    public void run(){
                        Toast.makeText(MySettingActivity.this,"授权成功，请再次选择操作！", Toast.LENGTH_LONG).show();
                    }
                });
            }else{
                Handler handler=new Handler(Looper.getMainLooper());
                handler.post(new Runnable(){
                    public void run(){
                        Toast.makeText(MySettingActivity.this,"请给APP授权，否则无法正常使用！",Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    private void uploadPic(String imagePath){

        if(imagePath != null){
            Log.v("shuangchuan",imagePath);
            File file = new File(imagePath);
            MultipartBody body = new MultipartBody.Builder().addFormDataPart("hostID", String.valueOf(userid)).addFormDataPart("file",userid+".jpg",RequestBody.create(MediaType.parse("image/jpg"),file)).build();
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url("http://192.168.43.121:8080/uploadavatar").post(body).build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.v("uploadhead","fail");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String back = response.body().string();
                    if(back.equals("success")){
                        Handler handler=new Handler(Looper.getMainLooper());
                        handler.post(new Runnable(){
                            public void run(){
                                Toast.makeText(MySettingActivity.this,"头像上传成功！",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else if(back.equals("fail")){
                        Handler handler=new Handler(Looper.getMainLooper());
                        handler.post(new Runnable(){
                            public void run(){
                                Toast.makeText(MySettingActivity.this,"头像上传失败，请重试！",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }
    }
}
