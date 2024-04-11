package com.example.cwgo.activity;

import android.Manifest;
import android.app.AlertDialog;

import com.example.cwgo.bean.MyImageView;
import com.example.cwgo.bean.MyUserResponse;
import com.example.cwgo.ninegrid.GlideEngine;
import com.example.cwgo.utils.MyselfUtil;
import com.google.gson.Gson;
import com.luck.picture.lib.animators.AnimationType;
import com.luck.picture.lib.basic.IBridgePictureBehavior;
import com.luck.picture.lib.basic.PictureCommonFragment;
import com.luck.picture.lib.basic.PictureSelectionCameraModel;
import com.luck.picture.lib.basic.PictureSelectionModel;
import com.luck.picture.lib.config.PictureMimeType;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.cwgo.MyApplication;
import com.example.cwgo.R;
import com.example.cwgo.adapter.GridImageAdapter;
import com.example.cwgo.bean.User;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.config.SelectModeConfig;
import com.luck.picture.lib.dialog.RemindDialog;
import com.luck.picture.lib.engine.ImageEngine;
import com.luck.picture.lib.engine.UriToFileTransformEngine;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.entity.MediaExtraInfo;
import com.luck.picture.lib.interfaces.OnCallbackListener;
import com.luck.picture.lib.interfaces.OnKeyValueResultCallbackListener;
import com.luck.picture.lib.interfaces.OnMediaEditInterceptListener;
import com.luck.picture.lib.interfaces.OnPermissionDeniedListener;
import com.luck.picture.lib.interfaces.OnQueryFilterListener;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;
import com.luck.picture.lib.language.LanguageConfig;
import com.luck.picture.lib.permissions.PermissionConfig;
import com.luck.picture.lib.permissions.PermissionUtil;
import com.luck.picture.lib.style.PictureSelectorStyle;
import com.luck.picture.lib.style.SelectMainStyle;
import com.luck.picture.lib.style.TitleBarStyle;
import com.luck.picture.lib.utils.DateUtils;
import com.luck.picture.lib.utils.MediaUtils;
import com.luck.picture.lib.utils.SandboxTransformUtils;
import com.luck.picture.lib.utils.StyleUtils;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class MySettingActivity extends AppCompatActivity  implements IBridgePictureBehavior {
    private final static int ACTIVITY_RESULT = 1;
    private final static int CALLBACK_RESULT = 2;
    private final static int LAUNCHER_RESULT = 3;
    private ImageButton back;
    private MyImageView userimage;
    private EditText username, userid, userpassword, email, usersign;
    private Button btn_modify;
    private ImageEngine imageEngine;
    protected static Uri temUri;
    private Bitmap photo;

    private MyApplication mApp = MyApplication.getInstance();

    private User mydata;
    private PictureSelectorStyle selectorStyle;
    private ActivityResultLauncher<Intent> launcherResult;


    //这里引入有问题啊啊啊
    private List<LocalMedia> selectList = new ArrayList<>();
    String path="";
    private GridImageAdapter adapter;

    Handler handlerPra = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg){
            if (msg.what == 22) {
                mydata = (User) msg.obj;
                email.setText(mydata.getEmail());
                userimage.setImageURL(mydata.getAvatar());
                username.setText(mydata.getUserName());
                usersign.setText(mydata.getSignature());
                userpassword.setText(mydata.getPassword());
            } else if (msg.what == 33) {
                if (msg.obj.toString().equals("Success")){
                    Toast.makeText(MySettingActivity.this, "头像上传成功！", Toast.LENGTH_SHORT).show();
                    continuaModi();
                } else if (msg.obj.toString().equals("Fail")) {
                    Toast.makeText(MySettingActivity.this, "修改失败：头像上传失败！可能是图片过大哦", Toast.LENGTH_SHORT).show();
                }
            } else if (msg.what == 44) {
                if (msg.obj.toString().equals("Success")){
                    Toast.makeText(MySettingActivity.this, "用户信息修改成功！", Toast.LENGTH_SHORT).show();
                    initView();
                } else if (msg.obj.toString().equals("Fail")) {
                    Toast.makeText(MySettingActivity.this, "用户信息修改失败！可以重新试试哦", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };


    private int resultMode = LAUNCHER_RESULT;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_mysetting);
        userimage = (MyImageView) findViewById(R.id.r_logo);
        username =(EditText) findViewById(R.id.setting_username);
        usersign = (EditText) findViewById(R.id.setting_sign);
//        userid =(EditText) findViewById(R.id.setting_id);
        userpassword =(EditText) findViewById(R.id.setting_password);
        email =(EditText) findViewById(R.id.setting_email);
        btn_modify =(Button) findViewById(R.id.regist);
        back = (ImageButton) findViewById(R.id.back);
        imageEngine = GlideEngine.createGlideEngine();
        launcherResult = createActivityResultLauncher();
        adapter = new GridImageAdapter(MySettingActivity.this, selectList);
        launcherResult = createActivityResultLauncher();

        initView();

        userimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogBox();
            }
        });

        back.setOnClickListener(v -> {
            finish();
        });

        btn_modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (adapter.getData().size() != 0) {
                    String path = adapter.getData().get(0).getAvailablePath();
                    uploadPic(path);
//                    if (!modiAvaSuc) {
//                        Toast.makeText(MySettingActivity.this, "上传头像出错！", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
                }
            }
        });

    }

    private void initView() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    MyselfUtil rec = new MyselfUtil();
                    //hostID指的是什么？？
                    mydata = rec.httpGet(mApp.getUser().getEmail());
                    mApp.setUser(mydata);
                    handlerPra.sendMessage(handlerPra.obtainMessage(22,mydata));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void continuaModi() {
        ModifyInfo info = new ModifyInfo(username.getText().toString(), email.getText().toString(), usersign.getText().toString());
        String jsonStr = new Gson().toJson(info);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"),jsonStr);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("http://"+mApp.getIp()+":8000/user/update").post(body).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.v("call", "fail");
                handlerPra.sendMessage(handlerPra.obtainMessage(44, "Fail"));
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                String back = response.body().string();
                // 使用 Gson 库解析 JSON 数据
                Gson gson = new Gson();
                MyUserResponse responceJson = gson.fromJson(back, MyUserResponse.class);
                if (responceJson.getCode().equals("200") && responceJson.getMsg().equals("Success")) {
//                    handler.post(() -> Toast.makeText(MySettingActivity.this,"修改成功！",Toast.LENGTH_SHORT).show());
                    handlerPra.sendMessage(handlerPra.obtainMessage(44, "Success"));
                    // 直接下面这样会有问题(多线程
//                            Toast.makeText(MySettingActivity.this, "修改成功！", Toast.LENGTH_SHORT).show();
                }else {
//                    Toast.makeText(MySettingActivity.this, "出错啦！", Toast.LENGTH_SHORT).show();
                    handlerPra.sendMessage(handlerPra.obtainMessage(44, "Fail"));
                }
            }
        });
    }

    class ModifyInfo{
        String userName;
        String email;
        String signature;

        ModifyInfo(String userName, String email, String signature) {
            this.userName = userName;
            this.email = email;
            this.signature = signature;
        }

        public String getEmail() {
            return email;
        }

        public String getSignature() {
            return signature;
        }

        public String getUserName() {
            return userName;
        }
    }

    protected void showDialogBox(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        setTitle("设置头像");
        String[] items = {"拍照","选择本地照片"};
        builder.setNegativeButton("取消",null);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i){
                    case 0://拍照
                        /*if(ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
                            ActivityCompat.requestPermissions(RegisterActivity.this,new String[]{Manifest.permission.CAMERA},0);
                        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            File uriFile = new File(AlbumUtil.getPath(RegisterActivity.this, temUri));
                            temUri = FileProvider.getUriForFile(RegisterActivity.this, getPackageName(),uriFile);
                        } else
                            temUri = Uri.fromFile(new File(Environment.getDownloadCacheDirectory(),"image.jpg"));
                        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,temUri);
                        startActivityForResult(openCameraIntent,0);*/
                        PictureSelectionCameraModel cameraModel = PictureSelector.create(MySettingActivity.this)
                                        .openCamera(SelectMimeType.ofImage())
//                                .setRecordAudioInterceptListener(new MeOnRecordAudioInterceptListener())
//                                .setCropEngine(getCropFileEngine())
//                                .setCompressEngine(getCompressFileEngine())
//                                .setSelectLimitTipsListener(new MeOnSelectLimitTipsListener())
//                                .setAddBitmapWatermarkListener(getAddBitmapWatermarkListener())
//                                .setVideoThumbnailListener(getVideoThumbnailEventListener())
//                                .setCustomLoadingListener(getCustomLoadingListener())
                                .setLanguage(LanguageConfig.SYSTEM_LANGUAGE)
                                .setSandboxFileEngine(new MeSandboxFileEngine())
//                                .isOriginalControl(cb_original.isChecked())
//                                .setPermissionDescriptionListener(getPermissionDescriptionListener())
//                                .setOutputAudioDir(getSandboxAudioOutputPath())
                                .setSelectedData(adapter.getData());;
                        forOnlyCameraResult(cameraModel);
//                        PictureSelector.create(MySettingActivity.this)
//                                .openCamera(SelectMimeType.ofImage())
//                                .forResult(PictureConfig.CHOOSE_REQUEST);
                        break;
                    case 1://选择本地图片
                        /*if(ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
                            ActivityCompat.requestPermissions(RegisterActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},0);
                        Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        openAlbumIntent.setType("image/*");
                        startActivityForResult(openAlbumIntent,1);*/
                        PictureSelectionModel selectionModel = PictureSelector.create(MySettingActivity.this)
                                .openGallery(SelectMimeType.ofImage())
                                .setSelectorUIStyle(selectorStyle)
                                .setImageEngine(imageEngine)
                                .setPermissionDeniedListener(new MeOnPermissionDeniedListener())
                                //裁剪引擎
                                //.setCropEngine(new ImageCropEngine())
                                .setSandboxFileEngine(new MeSandboxFileEngine())
                                .isAutoVideoPlay(true)
                                .isLoopAutoVideoPlay(false)
                                .isUseSystemVideoPlayer(false)
                                .isPageSyncAlbumCount(true)
                                .setQueryFilterListener(new OnQueryFilterListener() {
                                    @Override
                                    public boolean onFilter(LocalMedia media) {
                                        return false;
                                    }
                                })
                                //.setExtendLoaderEngine(getExtendLoaderEngine())
                                .setSelectionMode(SelectModeConfig.MULTIPLE)
                                .setLanguage(LanguageConfig.SYSTEM_LANGUAGE)
                                .isDisplayTimeAxis(true)
                                // 是否只查询指定目录下资源
                                .isOnlyObtainSandboxDir(false)
                                .isPageStrategy(true)
                                //是否开启原图功能
                                .isOriginalControl(false)
                                .isDisplayCamera(true)
                                .isOpenClickSound(true)
                                //滑动选择，但没看出来在哪里用
                                //.isFastSlidingSelect(cb_fast_select.isChecked())
                                //.setOutputCameraImageFileName("luck.jpeg")
                                //.setOutputCameraVideoFileName("luck.mp4")
                                // 是否支持图片和视频同时选择
                                .isWithSelectVideoImage(false)
                                .isPreviewFullScreenMode(true)
                                .isVideoPauseResumePlay(true)
                                .isPreviewZoomEffect(true)
                                .isPreviewImage(true)
                                .isPreviewVideo(true)
                                //.setQueryOnlyMimeType(PictureMimeType.ofGIF())
                                // 达到最大可选数量的蒙层
                                .isMaxSelectEnabledMask(true)
                                .setMaxSelectNum(1)
                                .setRecyclerAnimationMode(AnimationType.DEFAULT_ANIMATION)
                                .isGif(false)
                                .setSelectedData(adapter.getData())
                                .setEditMediaInterceptListener(new MeOnMediaEditInterceptListener(getSandboxPath(), buildOptions()));
                        forSelectResult(selectionModel);
                        break;
                }
            }
        });
        builder.create().show();
    }

    @Override
    public void onSelectFinish(@Nullable PictureCommonFragment.SelectorResult result) {
        if (result == null) {
//            Log.d(TAG, "selected null");
            return;
        }
        if (result.mResultCode == RESULT_OK) {
//            Log.d(TAG, result.mResultData == null?"SelectorResult没有数据":"SelectorResult加了数据");
            ArrayList<LocalMedia> selectorResult = PictureSelector.obtainSelectorList(result.mResultData);
            analyticalSelectResults(selectorResult);
        } else if (result.mResultCode == RESULT_CANCELED) {
//            Log.i(TAG, "onSelectFinish PictureSelector Cancel");
        }
    }
    /**
     * 创建自定义输出目录
     *
     * @return
     */
    private String getSandboxPath() {
        File externalFilesDir = MySettingActivity.this.getExternalFilesDir("");
        File customFile = new File(externalFilesDir.getAbsolutePath(), "Sandbox");
        if (!customFile.exists()) {
            customFile.mkdirs();
        }
        return customFile.getAbsolutePath() + File.separator;
    }

    private void forSelectResult(PictureSelectionModel model) {
        switch (resultMode) {
            case ACTIVITY_RESULT:
                model.forResult(PictureConfig.CHOOSE_REQUEST);
                break;
            case CALLBACK_RESULT:
                model.forResult(new MeOnResultCallbackListener());
                break;
            default:
                model.forResult(launcherResult);
                break;
        }
    }

    private void forOnlyCameraResult(PictureSelectionCameraModel model) {
        switch (resultMode) {
            case ACTIVITY_RESULT:
                model.forResultActivity(PictureConfig.REQUEST_CAMERA);
                break;
            case CALLBACK_RESULT:
                model.forResultActivity(new MeOnResultCallbackListener());
                break;
            default:
                model.forResultActivity(launcherResult);
                break;
        }

    }

    /**
     * 处理选择结果
     *
     * @param result
     */
    private void analyticalSelectResults(ArrayList<LocalMedia> result) {
        for (LocalMedia media : result) {
            if (media.getWidth() == 0 || media.getHeight() == 0) {
                if (PictureMimeType.isHasImage(media.getMimeType())) {
                    MediaExtraInfo imageExtraInfo = MediaUtils.getImageSize(MySettingActivity.this, media.getPath());
                    media.setWidth(imageExtraInfo.getWidth());
                    media.setHeight(imageExtraInfo.getHeight());
                } else if (PictureMimeType.isHasVideo(media.getMimeType())) {
                    MediaExtraInfo videoExtraInfo = MediaUtils.getVideoSize(MySettingActivity.this, media.getPath());
                    media.setWidth(videoExtraInfo.getWidth());
                    media.setHeight(videoExtraInfo.getHeight());
                }
            }
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                boolean isMaxSize = result.size() == adapter.getSelectMax();
                int oldSize = adapter.getData().size();
                adapter.notifyItemRangeRemoved(0, isMaxSize ? oldSize + 1 : oldSize);
                adapter.getData().clear();
                selectList.clear();

                adapter.getData().addAll(result);
                selectList.addAll(result);
//                Log.d(TAG, adapter.getData().size()==0?"adapter里没有数据":"adapter里加了数据");
                adapter.notifyItemRangeInserted(0, result.size());
                String path = adapter.getData().get(0).getAvailablePath();
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                userimage.setImageBitmap(bitmap);
            }
        });
    }

    /**
     * 选择结果
     */
    private class MeOnResultCallbackListener implements OnResultCallbackListener<LocalMedia> {
        @Override
        public void onResult(ArrayList<LocalMedia> result) {
            analyticalSelectResults(result);
        }

        @Override
        public void onCancel() {
        }
    }

    /**
     * 权限拒绝后回调
     */
    private static class MeOnPermissionDeniedListener implements OnPermissionDeniedListener {

        @Override
        public void onDenied(Fragment fragment, String[] permissionArray,
                             int requestCode, OnCallbackListener<Boolean> call) {
            String tips;
            if (TextUtils.equals(permissionArray[0], PermissionConfig.CAMERA[0])) {
                tips = "缺少相机权限\n可能会导致不能使用摄像头功能";
            } else if (TextUtils.equals(permissionArray[0], Manifest.permission.RECORD_AUDIO)) {
                tips = "缺少录音权限\n访问您设备上的音频、媒体内容和文件";
            } else {
                tips = "缺少存储权限\n访问您设备上的照片、媒体内容和文件";
            }
            RemindDialog dialog = RemindDialog.buildDialog(fragment.getContext(), tips);
            dialog.setButtonText("去设置");
            dialog.setButtonTextColor(0xFF7D7DFF);
            dialog.setContentTextColor(0xFF333333);
            dialog.setOnDialogClickListener(new RemindDialog.OnDialogClickListener() {
                @Override
                public void onClick(View view) {
                    PermissionUtil.goIntentSetting(fragment, requestCode);
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }

    /**
     * 自定义沙盒文件处理
     */
    private static class MeSandboxFileEngine implements UriToFileTransformEngine {

        @Override
        public void onUriToFileAsyncTransform(Context context, String srcPath, String mineType, OnKeyValueResultCallbackListener call) {
            if (call != null) {
                call.onCallback(srcPath, SandboxTransformUtils.copyPathToSandbox(context, srcPath, mineType));
            }
        }
    }

    /**
     * 自定义编辑
     */
    private static class MeOnMediaEditInterceptListener implements OnMediaEditInterceptListener {
        private final String outputCropPath;
        private final UCrop.Options options;

        public MeOnMediaEditInterceptListener(String outputCropPath, UCrop.Options options) {
            this.outputCropPath = outputCropPath;
            this.options = options;
        }

        @Override
        public void onStartMediaEdit(Fragment fragment, LocalMedia currentLocalMedia, int requestCode) {
            String currentEditPath = currentLocalMedia.getAvailablePath();
            Uri inputUri = PictureMimeType.isContent(currentEditPath)
                    ? Uri.parse(currentEditPath) : Uri.fromFile(new File(currentEditPath));
            Uri destinationUri = Uri.fromFile(
                    new File(outputCropPath, DateUtils.getCreateFileName("CROP_") + ".jpeg"));
            UCrop uCrop = UCrop.of(inputUri, destinationUri);
            options.setHideBottomControls(false);
            uCrop.withOptions(options);
//            uCrop.setImageEngine(new UCropImageEngine() {
//                @Override
//                public void loadImage(Context context, String url, ImageView imageView) {
//                    if (!ImageLoaderUtils.assertValidRequest(context)) {
//                        return;
//                    }
//                    Glide.with(context).load(url).override(180, 180).into(imageView);
////                }
//
//                @Override
//                public void loadImage(Context context, Uri url, int maxWidth, int maxHeight, OnCallbackListener<Bitmap> call) {
//                    Glide.with(context).asBitmap().load(url).override(maxWidth, maxHeight).into(new CustomTarget<Bitmap>() {
//                        @Override
//                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                            if (call != null) {
//                                call.onCall(resource);
//                            }
//                        }
//
//                        @Override
//                        public void onLoadCleared(@Nullable Drawable placeholder) {
//                            if (call != null) {
//                                call.onCall(null);
//                            }
//                        }
//                    });
//                }
//            });
            uCrop.start(fragment.requireActivity(), fragment, requestCode);
        }
    }
    /**
     * 创建一个ActivityResultLauncher
     *
     * @return
     */
    private ActivityResultLauncher<Intent> createActivityResultLauncher() {
        return registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        int resultCode = result.getResultCode();
                        if (resultCode == RESULT_OK) {
                            ArrayList<LocalMedia> selectList = PictureSelector.obtainSelectorList(result.getData());
                            analyticalSelectResults(selectList);
                        } else if (resultCode == RESULT_CANCELED) {
//                            Log.i(TAG, "onActivityResult PictureSelector Cancel");
                        }
                    }
                });
    }
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
//                userimage.setImageBitmap(bitmap);
//            }
//        }
//    }

    /**
     * 配制UCrop，可根据需求自我扩展
     *
     * @return
     */
    private UCrop.Options buildOptions() {
        UCrop.Options options = new UCrop.Options();
        options.setHideBottomControls(false);
        options.setFreeStyleCropEnabled(true);
        options.setShowCropFrame(true);
        options.setShowCropGrid(true);
        // 圆形头像剪裁
        options.setCircleDimmedLayer(false);
        options.withAspectRatio(-1, -1);
//        options.setCropOutputPathDir(getSandboxPath());
//        options.isCropDragSmoothToCenter(false);
//        options.setSkipCropMimeType(getNotSupportCrop());
//        options.isForbidCropGifWebp(cb_not_gif.isChecked());
//        options.isForbidSkipMultipleCrop(true);
        options.setMaxScaleMultiplier(100);
        if (selectorStyle != null && selectorStyle.getSelectMainStyle().getStatusBarColor() != 0) {
            SelectMainStyle mainStyle = selectorStyle.getSelectMainStyle();
//            boolean isDarkStatusBarBlack = mainStyle.isDarkStatusBarBlack();
            int statusBarColor = mainStyle.getStatusBarColor();
//            options.isDarkStatusBarBlack(isDarkStatusBarBlack);
            if (StyleUtils.checkStyleValidity(statusBarColor)) {
                options.setStatusBarColor(statusBarColor);
                options.setToolbarColor(statusBarColor);
            } else {
                options.setStatusBarColor(ContextCompat.getColor(MySettingActivity.this, R.color.ps_color_grey));
                options.setToolbarColor(ContextCompat.getColor(MySettingActivity.this, R.color.ps_color_grey));
            }
            TitleBarStyle titleBarStyle = selectorStyle.getTitleBarStyle();
            if (StyleUtils.checkStyleValidity(titleBarStyle.getTitleTextColor())) {
                options.setToolbarWidgetColor(titleBarStyle.getTitleTextColor());
            } else {
                options.setToolbarWidgetColor(ContextCompat.getColor(MySettingActivity.this, R.color.ps_color_white));
            }
        } else {
            options.setStatusBarColor(ContextCompat.getColor(MySettingActivity.this, R.color.ps_color_grey));
            options.setToolbarColor(ContextCompat.getColor(MySettingActivity.this, R.color.ps_color_grey));
            options.setToolbarWidgetColor(ContextCompat.getColor(MySettingActivity.this, R.color.ps_color_white));
        }
        return options;
    }

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
            File file = new File(imagePath);
            String emailStr = email.getText().toString();
            MultipartBody body = new MultipartBody.Builder()
                    .addFormDataPart("email", email.getText().toString())
                    .addFormDataPart("file",username.getText().toString() +".jpg",
                            RequestBody.create(MediaType.parse("image/jpg"),file)).build();
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url("http://"+mApp.getIp()+":8000/user/uploadAvatar").post(body).build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.v("uploadhead","fail");
                }

                @Override
                public void onResponse(Call call, okhttp3.Response response) throws IOException {
                    if(response.code() == 200){
//                        Handler handler=new Handler(Looper.getMainLooper());
//                        handler.post(new Runnable(){
//                            public void run(){
//                                Toast.makeText(MySettingActivity.this,"头像上传成功！",Toast.LENGTH_SHORT).show();
//                                modiAvaSuc = true;
//                            }
//                        });
                        handlerPra.sendMessage(handlerPra.obtainMessage(33, "Success"));
                    }else {
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
