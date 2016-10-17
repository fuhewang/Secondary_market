package com.wangfuhe.wfh.second_shop.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wangfuhe.wfh.second_shop.R;
import com.wangfuhe.wfh.second_shop.tools.ImageLoderUtil;
import com.wangfuhe.wfh.second_shop.user.UserGoods;

import java.util.ArrayList;

/**
 * Created by wfh on 2016/6/1.
 */
public class UploadedGoodsAdap extends BaseAdapter implements AbsListView.OnScrollListener {
    private Context mContext;
    private ArrayList<UserGoods> UploadGoods;
    private ImageLoderUtil mImageLoderUtil;
    private boolean mFirst;
    private int mstart,mend;
    public static String[] GoodsId;
    public UploadedGoodsAdap(Context mcontext,ArrayList<UserGoods> uploadGoods,
                            ListView listView){

        this.mContext=mcontext;
        this.UploadGoods=uploadGoods;
        if(!UploadGoods.isEmpty())
        Log.i("wangfuhe","adapter接收"+this.UploadGoods.get(0).getDescribe());
        mImageLoderUtil=new ImageLoderUtil(listView);
        GoodsId=new String[uploadGoods.size()];
        for (int i=0;i<uploadGoods.size();i++){
            GoodsId[i]=uploadGoods.get(i).getPic1().getFileUrl();
        }
        mFirst=true;
        listView.setOnScrollListener(this);
    }

    @Override
    public int getCount() {
        Log.i("wangfuhe","listviewcount:"+UploadGoods.size());
        return UploadGoods.size();
    }

    @Override
    public Object getItem(int position) {
        return UploadGoods.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       ViewHolder viewHolder;
        if(convertView==null){
            convertView= LayoutInflater.from(mContext).inflate(
              R.layout.item_uploaded,null);
            viewHolder=new ViewHolder();
            viewHolder.mImageView= (ImageView) convertView.findViewById(R.id.uploaded_pic_iv);
            viewHolder.mdescribe= (TextView) convertView.findViewById(R.id.uploaded_describe_tv);
            viewHolder.mprice= (TextView) convertView.findViewById(R.id.uploaded_price_tv);
            convertView.setTag(viewHolder);
        }else {
            viewHolder=(ViewHolder)convertView.getTag();
        }
        UserGoods bean=UploadGoods.get(position);
        viewHolder.mImageView.setTag(bean.getPic1().getFileUrl());
        mImageLoderUtil.showImageByTast(viewHolder.mImageView, bean.getPic1().getFileUrl());
        viewHolder.mdescribe.setText(bean.getDescribe());
//        Log.i("wangfuhe","listview显示"+bean.getMaxprice().toString());
        viewHolder.mprice.setText("￥"+String.valueOf(bean.getMinprice()));
        return convertView;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if(scrollState==SCROLL_STATE_IDLE){
            //加载可见项
            mImageLoderUtil.loadImage(mstart,mend);
        }else{
            //停止任务
            mImageLoderUtil.cancelAll();
        }

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        mstart=firstVisibleItem;
        mend=firstVisibleItem+visibleItemCount;
        if(mFirst && visibleItemCount>0){
            mImageLoderUtil.loadImage(mstart,mend);
            mFirst=false;
        }
    }
    class ViewHolder{
        ImageView mImageView;
        TextView mdescribe;
        TextView mprice;
    }
}
