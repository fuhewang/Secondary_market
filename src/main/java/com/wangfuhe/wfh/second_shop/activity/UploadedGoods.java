package com.wangfuhe.wfh.second_shop.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.wangfuhe.wfh.second_shop.R;
import com.wangfuhe.wfh.second_shop.adapter.UploadedGoodsAdap;
import com.wangfuhe.wfh.second_shop.user.Muser;
import com.wangfuhe.wfh.second_shop.user.UserGoods;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;

public class UploadedGoods extends AppCompatActivity {

    private static ArrayList<UserGoods> uploadgoods = new ArrayList<>();
    private ListView mListView;
    private UploadedGoodsAdap mUploadedGoodsAdap;
    private Muser userinfo;
    private ImageView mback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploaded_goods);
        mListView = (ListView) findViewById(R.id.uploaded_listview_lv);
        mback= (ImageView) findViewById(R.id.user_back_im);
        mback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        userinfo = BmobUser.getCurrentUser(getApplicationContext(), Muser.class);
        if (userinfo != null) {
            new MyAsynTask().execute(userinfo);
        }
    }

    class MyAsynTask extends AsyncTask<Muser, Void, ArrayList<UserGoods>> {

        @Override
        protected ArrayList<UserGoods> doInBackground(Muser... params) {
            BmobQuery<UserGoods> query = new BmobQuery<UserGoods>();
            //查询当前用户的上传物品
            query.addWhereEqualTo("master", userinfo);
            query.findObjects(getApplicationContext(), new FindListener<UserGoods>() {
                @Override
                public void onSuccess(List<UserGoods> list) {
                    Toast.makeText(getApplicationContext(), "下载成功", Toast.LENGTH_SHORT).show();
                    if (list != null) {
                        uploadgoods.clear();
                        for (Iterator iterator = list.iterator(); iterator.hasNext(); ) {
                            UserGoods goods = (UserGoods) iterator.next();
                            if (!uploadgoods.contains(goods)) {
                                uploadgoods.add(goods);
                                Log.i("wangfuhe", "uploaded:下载成功" + uploadgoods.get(0).getDescribe());
                            }
                        }

                        if (!uploadgoods.isEmpty()) {
                            mUploadedGoodsAdap = new UploadedGoodsAdap(getApplicationContext(),
                                    uploadgoods, mListView);
                            mListView.setAdapter(mUploadedGoodsAdap);
                        }
                    }
                }

                @Override
                public void onError(int i, String s) {
                    Toast.makeText(getApplicationContext(), "数据查询出错", Toast.LENGTH_SHORT).show();
                }
            });
            return uploadgoods;
        }
    }
}
