package com.wangfuhe.wfh.second_shop.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.wangfuhe.wfh.second_shop.R;
import com.wangfuhe.wfh.second_shop.adapter.UploadedGoodsAdap;
import com.wangfuhe.wfh.second_shop.base.BaseActivity;
import com.wangfuhe.wfh.second_shop.user.Muser;
import com.wangfuhe.wfh.second_shop.user.UserGoods;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class CollectGoods extends BaseActivity {
    private static ArrayList<UserGoods> uploadgoods = new ArrayList<>();
    private ListView mListView;
    private UploadedGoodsAdap mUploadedGoodsAdap;
    private ImageView mback;

    @Override
    public void initview() {
        setContentView(R.layout.activity_collected_goods);
        mListView = (ListView) findViewById(R.id.collected_listview_lv);
        mback = (ImageView) findViewById(R.id.collect_user_back_im);
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
        if (userinfo != null) {
            new MyAsynTask().execute(userinfo);
        }
        if (mListView != null) {
            setclick();
        }
    }

    private void setclick() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CollectGoods.this, Instanview.class);
                intent.putExtra("goods", uploadgoods.get(position));
                startActivityForResult(intent, 2);
            }
        });
    }

    class MyAsynTask extends AsyncTask<Muser, Void, ArrayList<UserGoods>> {
        @Override
        protected ArrayList<UserGoods> doInBackground(Muser... params) {
            BmobQuery<UserGoods> query = new BmobQuery<UserGoods>();
            //查询当前用户的上传物品
            query.addWhereEqualTo("collector", userinfo);
            query.findObjects(new FindListener<UserGoods>() {
                @Override
                public void done(List<UserGoods> list, BmobException e) {
                    if (e == null) {
                        showToastS("下载成功");
                        if (list != null)
                            uploadgoods.clear();
                        for (Iterator iterator = list.iterator(); iterator.hasNext(); ) {
                            UserGoods goods = (UserGoods) iterator.next();
                            if (!uploadgoods.contains(goods)) {
                                uploadgoods.add(goods);
                            }
                        }
                        mUploadedGoodsAdap = new UploadedGoodsAdap(getApplicationContext(),
                                uploadgoods, mListView);
                        mListView.setAdapter(mUploadedGoodsAdap);
                    } else {
                        showToastS("数据查询出错");
                    }
                }
            });
            return uploadgoods;
        }
    }
}
