package com.wangfuhe.wfh.second_shop.tools;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.wangfuhe.wfh.second_shop.user.UserGoods;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by wfh on 2016/6/11.
 */
public class Sqlhelp {
    public static void GetUserGoodsByCategory(final Context context, final String category,
                                              final Handler handler,
                                              final ArrayList<UserGoods> goodses) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                BmobQuery<UserGoods> query1 = new BmobQuery<UserGoods>();
                query1.addWhereEqualTo("issellde", false);
                BmobQuery<UserGoods> query2 = new BmobQuery<UserGoods>();
                query2.addWhereEqualTo("category", category);
                List<BmobQuery<UserGoods>> queries = new ArrayList<BmobQuery<UserGoods>>();
                queries.add(query1);
                queries.add(query2);
                BmobQuery<UserGoods> query = new BmobQuery<>();
                query.and(queries);
                query.findObjects(context, new FindListener<UserGoods>() {
                    @Override
                    public void onSuccess(List<UserGoods> list) {
                        if (list != null) {
                            goodses.clear();
                            goodses.addAll(list);
                            handler.sendEmptyMessage(1);
                            Log.i("wangfuhe","浮标查询：进行查询了");
                        }
                    }

                    @Override
                    public void onError(int i, String s) {
                        handler.sendEmptyMessage(0);
                    }
                });
            }
        }.start();

    }
    public static void GetGoodsBySearch(final Context context, final String search,
                                        final Handler handler,
                                        final ArrayList<UserGoods> goodses){
        new Thread() {
            @Override
            public void run() {
                super.run();
                BmobQuery<UserGoods> query1 = new BmobQuery<UserGoods>();
                query1.addWhereEqualTo("issellde", false);
                BmobQuery<UserGoods> query2 = new BmobQuery<UserGoods>();
                query2.addWhereContains("describe", search);
                List<BmobQuery<UserGoods>> queries = new ArrayList<BmobQuery<UserGoods>>();
                queries.add(query1);
                queries.add(query2);
                BmobQuery<UserGoods> query = new BmobQuery<>();
                query.and(queries);
                query.findObjects(context, new FindListener<UserGoods>() {
                    @Override
                    public void onSuccess(List<UserGoods> list) {
                        if (list != null) {
                            goodses.clear();
                            goodses.addAll(list);
                            handler.sendEmptyMessage(1);
                        }
                    }

                    @Override
                    public void onError(int i, String s) {
                        handler.sendEmptyMessage(0);
                    }
                });
            }
        }.start();
    }
}
