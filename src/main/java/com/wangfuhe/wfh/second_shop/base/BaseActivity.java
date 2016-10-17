package com.wangfuhe.wfh.second_shop.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.wangfuhe.wfh.second_shop.user.Muser;

import cn.bmob.v3.BmobUser;

public abstract class BaseActivity extends AppCompatActivity {

    protected Muser userinfo=BmobUser.getCurrentUser(Muser.class);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initview();
    }
    public abstract void initview();
    protected void showToastL(String s){
        Toast.makeText(this,s,Toast.LENGTH_LONG).show();
    }
    protected void showToastS(String s){
        Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
    }
    protected void setLog(String s){
        Log.i("wfh",s);
    }
}
