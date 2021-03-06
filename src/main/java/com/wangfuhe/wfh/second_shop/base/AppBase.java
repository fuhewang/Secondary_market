package com.wangfuhe.wfh.second_shop.base;

import android.app.Application;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobConfig;

/**
 * Created by wfh on 2016/5/20.
 */
public class AppBase extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Bmob.initialize(this, "aef77c3d67d570dfff523cba296bc5e4");
        BmobConfig config =new BmobConfig.Builder(this)
        ////设置appkey
        .setApplicationId("aef77c3d67d570dfff523cba296bc5e4")
        ////请求超时时间（单位为秒）：默认15s
        .setConnectTimeout(30)
        ////文件分片上传时每片的大小（单位字节），默认512*1024
        .setUploadBlockSize(1024*1024)
        ////文件的过期时间(单位为秒)：默认1800s
        .setFileExpiration(2500)
        .build();
        Bmob.initialize(config);
    }
}
