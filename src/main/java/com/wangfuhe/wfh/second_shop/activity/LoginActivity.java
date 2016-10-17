package com.wangfuhe.wfh.second_shop.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wangfuhe.wfh.second_shop.R;
import com.wangfuhe.wfh.second_shop.user.Muser;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class LoginActivity extends AppCompatActivity {

    private ImageView mBack;
    private EditText mAccount;
    private EditText mPasswd;
    private Button mlogin;
    private TextView mregister;
    private static final int LOGIN_CODE=1001;
    private CheckBox mautologin;
    SharedPreferences mrembpre;
    SharedPreferences.Editor mEditor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initview();
        mlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mAccount.getText().toString();
                String userpasswd = mPasswd.getText().toString();
                Muser muser = new Muser();
                muser.setPassword(userpasswd);
                muser.setUsername(username);
                muser.login(new SaveListener<Muser>() {
                    @Override
                    public void done(Muser muser, BmobException e) {
                        if (e==null){
                            Toast.makeText(getApplicationContext(), "登入成功", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            onBackPressed();
                        }else {
                            Toast.makeText(getApplicationContext(), "登入失败" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        mregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivityForResult(intent, LOGIN_CODE);
            }
        });
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        isautologin();
    }

    private void isautologin() {
        boolean ischeck=mrembpre.getBoolean("check",false);
        if(ischeck){
            mAccount.setText(mrembpre.getString("username",""));
            mPasswd.setText(mrembpre.getString("userpasswd",""));
        }
        mautologin.setChecked(ischeck);
        mautologin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mEditor.putString("username",mAccount.getText().toString());
                    mEditor.putString("userpasswd",mPasswd.getText().toString());
                }
                mEditor.putBoolean("check",isChecked);
                mEditor.apply();
            }
        });
    }

    private void userlogin(Intent intent) {
        String username=intent.getExtras().getString("username");
        String userpasswd=intent.getExtras().getString("userpasswd");
        mAccount.setText(username);
        mPasswd.setText(userpasswd);
    }


    private void initview() {
        mregister= (TextView) findViewById(R.id.user_register_tv);
        mBack= (ImageView) findViewById(R.id.user_back_im);
        mAccount= (EditText) findViewById(R.id.account_et);
        mPasswd= (EditText) findViewById(R.id.password_et);
        mlogin= (Button) findViewById(R.id.user_login_btn);
        mautologin= (CheckBox) findViewById(R.id.user_remb_checkBox);
        mrembpre=this.getSharedPreferences("note", 1);
        mEditor=mrembpre.edit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==LOGIN_CODE){
            if(resultCode==RESULT_OK){
                userlogin(data);
            }
        }
    }
}
