package com.example.cwgo.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cwgo.R;
import com.example.cwgo.bean.MyImageView;
import com.example.cwgo.bean.Post;
import com.example.cwgo.ninegrid.NineGridTestLayout;

import java.util.ArrayList;
import java.util.List;

public class AllPostAdapter extends BaseAdapter {

    private static String TAG = "AllPostAdapter";

    private List<Post> datas;
    private LayoutInflater inflater;
    private ViewHolder viewHolder;

    public AllPostAdapter() {
    }

    public AllPostAdapter(List<Post> datas, Context context) {
        this.datas = datas;
        inflater = LayoutInflater.from(context);
    }

    public void setDatas(List<Post> datas) {
        this.datas = datas;
    }

    public void setInflater(Context context) {
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int i) {
        return datas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        viewHolder = new ViewHolder();
        if(view == null ){
            view = inflater.inflate(R.layout.post_item, null);
            viewHolder.img_avatar = view.findViewById(R.id.img_avatar);
            viewHolder.tv_name = view.findViewById(R.id.tv_name);
            viewHolder.tv_content = view.findViewById(R.id.tv_content);
            viewHolder.mImageLayout = view.findViewById(R.id.img_image);
            viewHolder.tv_time = view.findViewById(R.id.tv_time);
            viewHolder.img_shoucang = view.findViewById(R.id.img_shoucang);
            viewHolder.img_pinglun = view.findViewById(R.id.img_pinglun);
            viewHolder.img_dianzan = view.findViewById(R.id.img_dianzan);
            viewHolder.tv_num_dianzan = view.findViewById(R.id.tv_num_dianzan);

            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)view.getTag();
        }
        viewHolder.img_avatar.setImageURL(datas.get(i).getAvatar().replace("localhost", "10.0.2.2"));
        viewHolder.tv_name.setText(datas.get(i).getUserName());
        viewHolder.tv_time.setText(datas.get(i).getTime());
        viewHolder.tv_content.setText(datas.get(i).getText());
        viewHolder.tv_num_dianzan.setText(Integer.toString(datas.get(i).getHasPraised()));

        viewHolder.mImageLayout.setIsShowAll(false);
        viewHolder.mImageLayout.setSpacing(5);

        if(datas.get(i).getIsCollected() ==1)
            viewHolder.img_shoucang.setSelected(true);
        else
            viewHolder.img_shoucang.setSelected(false);

        if(datas.get(i).getIsPraised() ==1)
            viewHolder.img_dianzan.setSelected(true);
        else
            viewHolder.img_dianzan.setSelected(false);

        Post data = datas.get(i);
        Log.d(TAG, data.toString() + " " +data.getPostImage() +" "+ data.getText() + " "+data.getUserID());
        String sImage = data.getPostImage();
        String image_item;
        List<String> image_list = new ArrayList<>();
        // String postImage字段，它包括九个图片的图片名，中间用“|”隔开
        int p=0;
        for(int k=0; k<sImage.length();k++){
            if(sImage.charAt(k) == '|'){
                image_item = sImage.substring(p,k);
                //image_item = sImage.substring(p,k);
                Log.v(TAG, image_item);
                p=k+1;
                // 换成虚拟机可访问的本地服务器的地址
                image_item = image_item.replace("localhost", "10.0.2.2");
                image_list.add(image_item);
            }
        }
        // 只有一张图的情况和处理最后一张图
        if(image_list.size() == 0){
            Log.v(TAG, sImage);
            sImage = sImage.replace("localhost", "10.0.2.2");
            image_list.add(sImage);
        }

        else{
            image_item = sImage.substring(p,sImage.length());
            image_item = image_item.replace("localhost", "10.0.2.2");
            image_list.add(image_item);
            Log.v(TAG,image_item);
        }
        // 一下子处理九张图片
        viewHolder.mImageLayout.setUrlList(image_list);

        viewHolder.img_shoucang.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mOnItemCollectListener.onCollectClick(i);
            }
        });
        viewHolder.img_pinglun.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mOnItemCommentListener.onCommentClick(i);
            }
        });
        viewHolder.img_dianzan.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mOnItemPraiseListener.onPraiseClick(i);
            }
        });

        return view;
    }

    public static class ViewHolder{
        public MyImageView img_avatar;
        public TextView tv_name,tv_content,tv_time,tv_num_dianzan;
        public NineGridTestLayout mImageLayout;
        public ImageView img_shoucang,img_dianzan,img_pinglun;
    }

    public interface onItemCollectListener{
        void onCollectClick(int i);
    }

    public interface onItemCommentListener{
        void onCommentClick(int i);
    }

    public interface onItemPraiseListener{
        void onPraiseClick(int i);
    }

    private onItemCollectListener mOnItemCollectListener;
    private onItemCommentListener mOnItemCommentListener;
    private onItemPraiseListener mOnItemPraiseListener;

    public void setOnItemCollectListener(onItemCollectListener mOnItemCollectListener) {
        this.mOnItemCollectListener = mOnItemCollectListener;
    }

    public void setOnItemCommentClickListener(onItemCommentListener mOnItemCommentListener) {
        this.mOnItemCommentListener = mOnItemCommentListener;
    }
    public void setOnItemPraiseClickListener(onItemPraiseListener mOnItemPraiseListener) {
        this.mOnItemPraiseListener = mOnItemPraiseListener;
    }
}

