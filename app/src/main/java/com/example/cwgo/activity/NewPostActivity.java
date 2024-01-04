package com.example.cwgo.activity;

import static com.luck.picture.lib.thread.PictureThreadUtils.runOnUiThread;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cwgo.DragListener;
import com.example.cwgo.R;
import com.example.cwgo.adapter.GridImageAdapter;
import com.example.cwgo.bean.Post;
import com.example.cwgo.fragment.NewPostFragment;
import com.example.cwgo.ninegrid.FullyGridLayoutManager;
import com.example.cwgo.ninegrid.GlideEngine;
import com.google.gson.Gson;
import com.luck.picture.lib.animators.AnimationType;
import com.luck.picture.lib.basic.IBridgePictureBehavior;
import com.luck.picture.lib.basic.IBridgeViewLifecycle;
import com.luck.picture.lib.basic.PictureCommonFragment;
import com.luck.picture.lib.basic.PictureSelectionModel;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.config.SelectModeConfig;
import com.luck.picture.lib.decoration.GridSpacingItemDecoration;
import com.luck.picture.lib.dialog.RemindDialog;
import com.luck.picture.lib.engine.ImageEngine;
import com.luck.picture.lib.engine.UriToFileTransformEngine;
import com.luck.picture.lib.engine.VideoPlayerEngine;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.entity.MediaExtraInfo;
import com.luck.picture.lib.interfaces.OnCallbackListener;
import com.luck.picture.lib.interfaces.OnExternalPreviewEventListener;
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
import com.luck.picture.lib.utils.DensityUtil;
import com.luck.picture.lib.utils.MediaUtils;
import com.luck.picture.lib.utils.SandboxTransformUtils;
import com.luck.picture.lib.utils.StyleUtils;
import com.yalantis.ucrop.UCrop;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NewPostActivity extends AppCompatActivity implements IBridgePictureBehavior {

    private final static int ACTIVITY_RESULT = 1;
    private final static int CALLBACK_RESULT = 2;
    private final static int LAUNCHER_RESULT = 3;
    private final static String TAG = "NewPostFragment";

    private int maxSelectNum = 9;
    private List<LocalMedia> selectList = new ArrayList<>();
    private GridImageAdapter adapter;
    private PictureSelectorStyle selectorStyle;
    private ImageEngine imageEngine;
    private int language = LanguageConfig.SYSTEM_LANGUAGE;
    private int animationMode = AnimationType.DEFAULT_ANIMATION;
    private int chooseMode = SelectMimeType.ofImage();
    private VideoPlayerEngine videoPlayerEngine = null;
    private int resultMode = LAUNCHER_RESULT;
    private ActivityResultLauncher<Intent> launcherResult;
    // 是拖动时候的放大和缩小吗
    private boolean needScaleBig = true;
    private boolean needScaleSmall = false;
    private boolean isHasLiftDelete;
    private RecyclerView mRecyclerView;
    private EditText ed_content;
    private Post post = new Post();
    private Button btn_submit;
    private String dynamicID;
    private TextView tvDeleteText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        selectorStyle = new PictureSelectorStyle();
        launcherResult = createActivityResultLauncher();
        if (savedInstanceState != null && savedInstanceState.getParcelableArrayList("selectorList") != null) {
            selectList.clear();
            selectList.addAll(savedInstanceState.getParcelableArrayList("selectorList"));
        }

        mRecyclerView = findViewById(R.id.recycler);
        ed_content = findViewById(R.id.content_et);
        btn_submit = findViewById(R.id.send_btn);
        tvDeleteText = findViewById(R.id.tv_delete_text);

        btn_submit.setOnClickListener(v -> {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date(System.currentTimeMillis());
            String DateTime = simpleDateFormat.format(date);
            post.setTime(DateTime);
//            post.setPostContent(ed_content.getText().toString());
//            post.setHostID(Integer.toString(1));
            String jsonstr = new Gson().toJson(post);
            System.out.println(jsonstr);
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonstr);
            OkHttpClient client = new OkHttpClient();
            // 先传帖子
            Request request = new Request.Builder()
                    .url(".../postadd")
                    .post(body)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(() -> Toast.makeText(getBaseContext(),"帖子发布失败！",Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    dynamicID = response.body().string();
                    for(LocalMedia media : selectList) {
                        OkHttpClient client  = new OkHttpClient();
                        String path = media.getPath();
                        File file = new File(path);
                        Log.v("Image",path);
                        MultipartBody body = new MultipartBody.Builder()
                                .addFormDataPart("dynamicID", dynamicID)
                                .addFormDataPart("file", "img01.png", RequestBody.create(MediaType.parse("image/png"), file))
                                .addFormDataPart("ss","",RequestBody.create(MediaType.parse("image/png"),file))
                                .build();
                        Request request = new Request.Builder()
                                .url(".../uploadimage")
                                .post(body)
                                .build();
                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                Handler handler = new Handler(Looper.getMainLooper());
                                handler.post(() -> Toast.makeText(getBaseContext(),"图片上传失败！",Toast.LENGTH_SHORT).show());
                            }

                            @Override
                            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                Handler handler = new Handler(Looper.getMainLooper());
                                handler.post(() -> Toast.makeText(getBaseContext(),"帖子发布成功！",Toast.LENGTH_SHORT).show());
                            }
                        });
                    }

                }
            });
        });

        initWidget();
    }

    private void initWidget() {
        FullyGridLayoutManager manager = new FullyGridLayoutManager(getBaseContext(), 3, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);
        RecyclerView.ItemAnimator itemAnimator = mRecyclerView.getItemAnimator();
        if (itemAnimator != null) {
            ((SimpleItemAnimator) itemAnimator).setSupportsChangeAnimations(false);
        }
        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(4,
                DensityUtil.dip2px(getBaseContext(), 8), false));

        adapter = new GridImageAdapter(this.getBaseContext(), selectList);
        adapter.setSelectMax(maxSelectNum);
        mRecyclerView.setAdapter(adapter);
        imageEngine = GlideEngine.createGlideEngine();

        adapter.setOnItemClickListener(new GridImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                // 预览图片、视频、音频
                PictureSelector.create(getBaseContext())
                        .openPreview()
                        .setImageEngine(imageEngine)
                        .setSelectorUIStyle(selectorStyle)
                        .setLanguage(language)
                        .isAutoVideoPlay(true)
                        .isLoopAutoVideoPlay(true)
                        .isPreviewFullScreenMode(true)
                        .isVideoPauseResumePlay(true)
                        .isPreviewZoomEffect(true, mRecyclerView)
                        .setAttachViewLifecycle(new IBridgeViewLifecycle() {
                            @Override
                            public void onViewCreated(Fragment fragment, View view, Bundle savedInstanceState) {
//                                PictureSelectorPreviewFragment previewFragment = (PictureSelectorPreviewFragment) fragment;
//                                MediumBoldTextView tvShare = view.findViewById(R.id.tv_share);
//                                tvShare.setVisibility(View.VISIBLE)
//                                previewFragment.addAminViews(tvShare);
//                                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) tvShare.getLayoutParams();
//                                layoutParams.topMargin = cb_preview_full.isChecked() ? DensityUtil.getStatusBarHeight(getContext()) : 0;
//                                tvShare.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        PicturePreviewAdapter previewAdapter = previewFragment.getAdapter();
//                                        ViewPager2 viewPager2 = previewFragment.getViewPager2();
//                                        LocalMedia media = previewAdapter.getItem(viewPager2.getCurrentItem());
//                                        ToastUtils.showToast(fragment.getContext(), "自定义分享事件:" + viewPager2.getCurrentItem());
//                                    }
//                                });
                            }

                            @Override
                            public void onDestroy(Fragment fragment) {
//                                if (cb_preview_full.isChecked()) {
//                                    // 如果是全屏预览模式且是startFragmentPreview预览，回到自己的界面时需要恢复一下自己的沉浸式状态
//                                    // 以下提供2种解决方案:
//                                    // 1.通过ImmersiveManager.immersiveAboveAPI23重新设置一下沉浸式
//                                    int statusBarColor = ContextCompat.getColor(getContext(), R.color.ps_color_grey);
//                                    int navigationBarColor = ContextCompat.getColor(getContext(), R.color.ps_color_grey);
//                                    ImmersiveManager.immersiveAboveAPI23(MainActivity.this,
//                                            true, true,
//                                            statusBarColor, navigationBarColor, false);
//                                    // 2.让自己的titleBar的高度加上一个状态栏高度且内容PaddingTop下沉一个状态栏的高度
//                                }
                            }
                        })
//                        .setInjectLayoutResourceListener(new OnInjectLayoutResourceListener() {
//                            @Override
//                            public int getLayoutResourceId(Context context, int resourceSource) {
//                                return resourceSource == InjectResourceSource.PREVIEW_LAYOUT_RESOURCE
//                                        ? R.layout.ps_custom_fragment_preview
//                                        : InjectResourceSource.DEFAULT_LAYOUT_RESOURCE;
//                            }
//                        })
                        .setExternalPreviewEventListener(new MyExternalPreviewEventListener())
//                        .setInjectActivityPreviewFragment(new OnInjectActivityPreviewListener() {
//                            @Override
//                            public PictureSelectorPreviewFragment onInjectPreviewFragment() {
//                                return cb_custom_preview.isChecked() ? CustomPreviewFragment.newInstance() : null;
//                            }
//                        })
                        .startActivityPreview(position, true, adapter.getData());
            }

            @Override
            public void openPicture() {
                // 相册或单独拍照模式
                boolean mode = true;
                if (mode) {
//                    // 进入系统相册
//                    if (cb_system_album.isChecked()) {
//                        PictureSelectionSystemModel systemGalleryMode = PictureSelector.create(getContext())
//                                .openSystemGallery(chooseMode)
//                                .setSelectionMode(cb_choose_mode.isChecked() ? SelectModeConfig.MULTIPLE : SelectModeConfig.SINGLE)
//                                .setCompressEngine(getCompressFileEngine())
//                                .setCropEngine(getCropFileEngine())
//                                .setSkipCropMimeType(getNotSupportCrop())
//                                .setSelectLimitTipsListener(new MeOnSelectLimitTipsListener())
//                                .setAddBitmapWatermarkListener(getAddBitmapWatermarkListener())
//                                .setVideoThumbnailListener(getVideoThumbnailEventListener())
//                                .setCustomLoadingListener(getCustomLoadingListener())
//                                .isOriginalControl(cb_original.isChecked())
//                                .setPermissionDescriptionListener(getPermissionDescriptionListener())
//                                .setSandboxFileEngine(new MeSandboxFileEngine());
//                        forSystemResult(systemGalleryMode);
//                    } else {
                    // 进入相册
                    PictureSelectionModel selectionModel = PictureSelector.create(NewPostActivity.this)
                            .openGallery(chooseMode)
                            .setSelectorUIStyle(selectorStyle)
                            .setImageEngine(imageEngine)
                            .setVideoPlayerEngine(videoPlayerEngine)
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
                            .setLanguage(language)
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
                            .setMaxSelectNum(maxSelectNum)
                            .setRecyclerAnimationMode(animationMode)
                            .isGif(false)
                            .setSelectedData(adapter.getData())
                            .setEditMediaInterceptListener(new MeOnMediaEditInterceptListener(getSandboxPath(), buildOptions()));
                    forSelectResult(selectionModel);
//                    }
                }
            }
        });
        adapter.setItemLongClickListener((holder, position, v) -> {
            int itemViewType = holder.getItemViewType();
            if (itemViewType != GridImageAdapter.TYPE_CAMERA) {
                mItemTouchHelper.startDrag(holder);
            }
        });
        // 绑定拖拽事件
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    @Override
    public void onSelectFinish(@Nullable PictureCommonFragment.SelectorResult result) {
        if (result == null) {
            return;
        }
        if (result.mResultCode == RESULT_OK) {
            ArrayList<LocalMedia> selectorResult = PictureSelector.obtainSelectorList(result.mResultData);
            analyticalSelectResults(selectorResult);
        } else if (result.mResultCode == RESULT_CANCELED) {
            Log.i(TAG, "onSelectFinish PictureSelector Cancel");
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

    private final ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
        @Override
        public boolean isLongPressDragEnabled() {
            return true;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        }

        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType != GridImageAdapter.TYPE_CAMERA) {
                viewHolder.itemView.setAlpha(0.7f);
            }
            return makeMovementFlags(ItemTouchHelper.DOWN | ItemTouchHelper.UP
                    | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, ItemTouchHelper.START | ItemTouchHelper.END);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            try {
                //得到item原来的position
                int fromPosition = viewHolder.getAbsoluteAdapterPosition();
                //得到目标position
                int toPosition = target.getAbsoluteAdapterPosition();
                int itemViewType = target.getItemViewType();
                if (itemViewType != GridImageAdapter.TYPE_CAMERA) {
                    if (fromPosition < toPosition) {
                        for (int i = fromPosition; i < toPosition; i++) {
                            Collections.swap(adapter.getData(), i, i + 1);
                        }
                    } else {
                        for (int i = fromPosition; i > toPosition; i--) {
                            Collections.swap(adapter.getData(), i, i - 1);
                        }
                    }
                    adapter.notifyItemMoved(fromPosition, toPosition);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                @NonNull RecyclerView.ViewHolder viewHolder, float dx, float dy, int actionState, boolean isCurrentlyActive) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType != GridImageAdapter.TYPE_CAMERA) {
                if (needScaleBig) {
                    needScaleBig = false;
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.playTogether(
                            ObjectAnimator.ofFloat(viewHolder.itemView, "scaleX", 1.0F, 1.1F),
                            ObjectAnimator.ofFloat(viewHolder.itemView, "scaleY", 1.0F, 1.1F));
                    animatorSet.setDuration(50);
                    animatorSet.setInterpolator(new LinearInterpolator());
                    animatorSet.start();
                    animatorSet.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            needScaleSmall = true;
                        }
                    });
                }
                int targetDy = tvDeleteText.getTop() - viewHolder.itemView.getBottom();
//                Log.d(TAG, "tvDeleteText.getTop()"+String.valueOf(tvDeleteText.getTop()));
//                Log.d(TAG, "viewHolder.itemView.getBottom()"+String.valueOf(viewHolder.itemView.getBottom()));
//                Log.d(TAG, String.valueOf(dy));
                if (dy >= targetDy) {
                    //拖到删除处
                    Log.d(TAG, "再拖转处");
                    mDragListener.deleteState(true);
                    if (isHasLiftDelete) {
                        Log.d(TAG, "isHasLiftDelete");
                        //在删除处放手，则删除item
                        viewHolder.itemView.setVisibility(View.INVISIBLE);
                        adapter.delete(viewHolder.getAbsoluteAdapterPosition());
                        resetState();
                        return;
                    }
                } else {
                    //没有到删除处
                    if (View.INVISIBLE == viewHolder.itemView.getVisibility()) {
                        //如果viewHolder不可见，则表示用户放手，重置删除区域状态
                        Log.d(TAG, "没有删除，松手了");
                        mDragListener.dragState(false);
                    }
                    mDragListener.deleteState(false);
                }
                super.onChildDraw(c, recyclerView, viewHolder, dx, dy, actionState, isCurrentlyActive);
            }
        }

        @Override
        public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
            int itemViewType = viewHolder != null ? viewHolder.getItemViewType() : GridImageAdapter.TYPE_CAMERA;
            if (itemViewType != GridImageAdapter.TYPE_CAMERA) {
                if (ItemTouchHelper.ACTION_STATE_DRAG == actionState) {
                    Log.d(TAG, "onSelectedChanged");
                    mDragListener.dragState(true);
                }
                super.onSelectedChanged(viewHolder, actionState);
            }
        }

        @Override
        public long getAnimationDuration(@NonNull RecyclerView recyclerView, int animationType, float animateDx, float animateDy) {
            isHasLiftDelete = true;
            Log.d(TAG, "getAnimationDuration");
            return super.getAnimationDuration(recyclerView, animationType, animateDx, animateDy);
        }

        @Override
        public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType != GridImageAdapter.TYPE_CAMERA) {
                viewHolder.itemView.setAlpha(1.0F);
                if (needScaleSmall) {
                    needScaleSmall = false;
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.playTogether(
                            ObjectAnimator.ofFloat(viewHolder.itemView, "scaleX", 1.1F, 1.0F),
                            ObjectAnimator.ofFloat(viewHolder.itemView, "scaleY", 1.1F, 1.0F));
                    animatorSet.setInterpolator(new LinearInterpolator());
                    animatorSet.setDuration(50);
                    animatorSet.start();
                    animatorSet.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            needScaleBig = true;
                        }
                    });
                }
                super.clearView(recyclerView, viewHolder);
                adapter.notifyItemChanged(viewHolder.getAbsoluteAdapterPosition());
                resetState();
            }
        }
    });

    /**
     * 重置
     */
    private void resetState() {
        isHasLiftDelete = false;
        mDragListener.deleteState(false);
        mDragListener.dragState(false);
    }

    private final DragListener mDragListener = new DragListener() {
        @Override
        public void deleteState(boolean isDelete) {
            if (isDelete) {
                Log.d(TAG, "isDelete");
                if (!TextUtils.equals(getString(R.string.app_let_go_drag_delete), tvDeleteText.getText())) {
                    tvDeleteText.setText(getString(R.string.app_let_go_drag_delete));
                    tvDeleteText.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_dump_delete, 0, 0);
                }
            } else {
                if (!TextUtils.equals(getString(R.string.app_drag_delete), tvDeleteText.getText())) {
                    tvDeleteText.setText(getString(R.string.app_drag_delete));
                    tvDeleteText.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_normal_delete, 0, 0);
                }
            }

        }

        @Override
        public void dragState(boolean isStart) {
            if (isStart) {
                if (tvDeleteText.getAlpha() == 0F) {
                    ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(tvDeleteText, "alpha", 0F, 1F);
                    alphaAnimator.setInterpolator(new LinearInterpolator());
                    alphaAnimator.setDuration(120);
                    alphaAnimator.start();
                }
            } else {
                if (tvDeleteText.getAlpha() == 1F) {
                    ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(tvDeleteText, "alpha", 1F, 0F);
                    alphaAnimator.setInterpolator(new LinearInterpolator());
                    alphaAnimator.setDuration(120);
                    alphaAnimator.start();
                }
            }
        }
    };

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
            Log.i(TAG, "PictureSelector Cancel");
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

    /**
     * 处理选择结果
     *
     * @param result
     */
    private void analyticalSelectResults(ArrayList<LocalMedia> result) {
        for (LocalMedia media : result) {
            if (media.getWidth() == 0 || media.getHeight() == 0) {
                if (PictureMimeType.isHasImage(media.getMimeType())) {
                    MediaExtraInfo imageExtraInfo = MediaUtils.getImageSize(getBaseContext(), media.getPath());
                    media.setWidth(imageExtraInfo.getWidth());
                    media.setHeight(imageExtraInfo.getHeight());
                } else if (PictureMimeType.isHasVideo(media.getMimeType())) {
                    MediaExtraInfo videoExtraInfo = MediaUtils.getVideoSize(getBaseContext(), media.getPath());
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

                adapter.getData().addAll(result);
                adapter.notifyItemRangeInserted(0, result.size());
            }
        });
    }



    /**
     * 外部预览监听事件
     */
    private class MyExternalPreviewEventListener implements OnExternalPreviewEventListener {

        @Override
        public void onPreviewDelete(int position) {
            adapter.remove(position);
            adapter.notifyItemRemoved(position);
        }

        @Override
        public boolean onLongPressDownload(Context context, LocalMedia media) {
            return false;
        }
    }


    /**
     * 创建自定义输出目录
     *
     * @return
     */
    private String getSandboxPath() {
        File externalFilesDir = getBaseContext().getExternalFilesDir("");
        File customFile = new File(externalFilesDir.getAbsolutePath(), "Sandbox");
        if (!customFile.exists()) {
            customFile.mkdirs();
        }
        return customFile.getAbsolutePath() + File.separator;
    }

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
                options.setStatusBarColor(ContextCompat.getColor(getBaseContext(), R.color.ps_color_grey));
                options.setToolbarColor(ContextCompat.getColor(getBaseContext(), R.color.ps_color_grey));
            }
            TitleBarStyle titleBarStyle = selectorStyle.getTitleBarStyle();
            if (StyleUtils.checkStyleValidity(titleBarStyle.getTitleTextColor())) {
                options.setToolbarWidgetColor(titleBarStyle.getTitleTextColor());
            } else {
                options.setToolbarWidgetColor(ContextCompat.getColor(getBaseContext(), R.color.ps_color_white));
            }
        } else {
            options.setStatusBarColor(ContextCompat.getColor(getBaseContext(), R.color.ps_color_grey));
            options.setToolbarColor(ContextCompat.getColor(getBaseContext(), R.color.ps_color_grey));
            options.setToolbarWidgetColor(ContextCompat.getColor(getBaseContext(), R.color.ps_color_white));
        }
        return options;
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

    @Override
    public void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (adapter != null && adapter.getData() != null && adapter.getData().size() > 0) {
            outState.putParcelableArrayList("selectorList",
                    adapter.getData());
        }
    }

}