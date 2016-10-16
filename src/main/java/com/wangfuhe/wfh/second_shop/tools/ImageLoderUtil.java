package com.wangfuhe.wfh.second_shop.tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.LruCache;
import android.widget.ImageView;
import android.widget.ListView;

import com.wangfuhe.wfh.second_shop.R;
import com.wangfuhe.wfh.second_shop.adapter.UploadedGoodsAdap;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by wfh on 2016/6/1.
 */
public class ImageLoderUtil {
    private ImageView mImageView;
    private String murltag;
    private LruCache<String, Bitmap> mCache;
    private Set<NewAsynTast> mTask;
    private ListView mListView;
    public ImageLoderUtil(ListView listView) {
        mListView=listView;
        mTask = new HashSet<>();
        //获取最大可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cachesize = maxMemory / 4;
        mCache = new LruCache<String, Bitmap>(cachesize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                //在每次存入缓存的时候调用
                return value.getByteCount();
            }
        };

    }
    //增加到缓存
    public void addBitmapToCache(String url,Bitmap bitmap){
        if(getBitmap(url)==null){
            mCache.put(url,bitmap);
        }
    }
//从缓存中获取数据
    private Bitmap getBitmap(String url) {
        return mCache.get(url);
    }
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(mImageView.getTag().equals(murltag)){
                mImageView.setImageBitmap((Bitmap) msg.obj);
            }
        }
    };

    public void showImageByThread(ImageView imageView,final String url){
        murltag=url;
        mImageView=imageView;
        new Thread(){
            @Override
            public void run() {
                super.run();

            }
        };
    }
    public Bitmap getBitmapFromUrl(String urlstring){
        Bitmap bitmap;
        InputStream is=null;
        try {
            URL url=new URL(urlstring);
            HttpURLConnection connection= (HttpURLConnection) url.openConnection();
            is=new BufferedInputStream(connection.getInputStream());
            bitmap= BitmapFactory.decodeStream(is);
            connection.disconnect();
            return bitmap;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    public void showImageByTast(ImageView imageView,String url){
        //从缓存中取出图片
        Bitmap bitmap=getBitmap(url);
        //如果缓存中没有就必须下载
        if(bitmap ==null){
            imageView.setImageResource(R.mipmap.ic_launcher);
        }else {
            imageView.setImageBitmap(bitmap);
        }
    }

    public void cancelAll(){
        if(mTask!=null){
            for (NewAsynTast task:mTask){
                task.cancel(false);
            }
        }
    }

    //用来加载从start到end的所有图片
    public void loadImage(int start,int end){
        for (int i=start;i<end;i++){
            String url= UploadedGoodsAdap.GoodsId[i];
            Bitmap bitmap=getBitmap(url);

            //如果缓存中没有就必须下载
            if(bitmap==null){
                NewAsynTast task=new NewAsynTast(url);
                task.execute(url);
                mTask.add(task);
            }else {
                ImageView imageviw= (ImageView) mListView.findViewWithTag(url);
                imageviw.setImageBitmap(bitmap);
            }
        }
    }

    class NewAsynTast extends AsyncTask<String, Void, Bitmap> {

        private String murl;

        public NewAsynTast(String url) {
            this.murl = url;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            //从网络获取图片
            Bitmap bitmap = getBitmapFromUrl(params[0]);
            if (bitmap != null) {
                //将图片加入缓存
                addBitmapToCache(params[0], bitmap);
            }
            return bitmap;
        }
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            ImageView imageView= (ImageView) mListView.findViewWithTag(murl);
            if(imageView!=null && bitmap !=null) {
                imageView.setImageBitmap(bitmap);
            }
            mTask.remove(this);
        }
    }
}


