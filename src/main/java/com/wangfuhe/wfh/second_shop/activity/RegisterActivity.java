package com.wangfuhe.wfh.second_shop.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.wangfuhe.wfh.second_shop.R;
import com.wangfuhe.wfh.second_shop.user.Muser;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;

public class RegisterActivity extends Activity {

    private ImageView mBack;
    private EditText mAccount;
    private EditText mPasswd;
    private EditText mAPasswd;
    private Button mRegister;
    private Button mGetidentifying_code;
    private EditText mIdentifying_code;
    private EditText mtelephone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initview();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIdentifying_code == null) {
                    Toast.makeText(getApplicationContext(), "请先完善个人信息", Toast.LENGTH_SHORT).show();
                } else {
                    registeruser();
                }
            }
        });
        mGetidentifying_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mtelephone.getText().toString() == null) {
                    Toast.makeText(getApplicationContext(), "请输入手机号", Toast.LENGTH_SHORT).show();
                    ;
                } else {
                    getidentifying_code();
                }
            }
        });
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getidentifying_code() {
        BmobSMS.requestSMSCode(mtelephone.getText().toString(), "wfh",new QueryListener<Integer>() {

            @Override
            public void done(Integer smsId,BmobException ex) {
                if(ex==null){//验证码发送成功
                    Log.i("wangfuhe", "短信id" + smsId);
                }else {
                    Log.i("wangfuhe", mtelephone.getText().toString());
                }
            }
        });
    }

    private void registeruser() {
        if(mPasswd.getText().toString().equals(mAPasswd.getText().toString())){
            Muser muser=new Muser();
             muser.setMobilePhoneNumber(mtelephone.getText().toString());
             muser.setUsername(mAccount.getText().toString());
            muser.setPassword(mAPasswd.getText().toString());
            muser.signOrLogin("验证码", new SaveListener<Muser>() {

                @Override
                public void done(Muser user, BmobException e) {
                    if (user != null) {
                        Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                    }else {
                        Toast.makeText(getApplicationContext(), "登录失败"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else {
            Toast.makeText(this,"密码输入错误，请重新输入",Toast.LENGTH_SHORT).show();
        }
    }

    private void initview() {
        mBack= (ImageView) findViewById(R.id.re_user_back_im);
        mAccount= (EditText) findViewById(R.id.re_account_et);
        mPasswd= (EditText) findViewById(R.id.re_password_et);
        mAPasswd= (EditText) findViewById(R.id.re_ag_password_et);
        mRegister= (Button) findViewById(R.id.register_btn);
        mGetidentifying_code= (Button) findViewById(R.id.re_gettelephone_btn);
        mtelephone= (EditText) findViewById(R.id.re_telephone_et);
        mIdentifying_code= (EditText) findViewById(R.id.re_identifying_code_et);
    }
}
