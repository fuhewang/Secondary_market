package com.wangfuhe.wfh.second_shop.tools;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.wangfuhe.wfh.second_shop.user.UserGoods;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
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
                query.findObjects(new FindListener<UserGoods>() {
                    @Override
                    public void done(List<UserGoods> list, BmobException e) {
                        if (e==null){
                            if (list != null) {
                                goodses.clear();
                                goodses.addAll(list);
                                handler.sendEmptyMessage(1);
                                Log.i("wfh","浮标查询：进行查询了");
                            }
                        }else {
                            handler.sendEmptyMessage(0);
                        }
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
                query.or(queries);
                query.findObjects(new FindListener<UserGoods>() {
                    @Override
                    public void done(List<UserGoods> list, BmobException e) {
                        if (e==null){
                            if (list != null) {
                                goodses.clear();
                                goodses.addAll(list);
                                Log.i("wfh",search);
                                handler.sendEmptyMessage(1);
                                Toast.makeText(context,"由于Bmob的模糊查询突然要收费了，暂停服务",Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            handler.sendEmptyMessage(0);

                        }
                    }
                });
            }
        }.start();
    }
}
