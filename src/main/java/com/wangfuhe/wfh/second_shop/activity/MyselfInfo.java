package com.wangfuhe.wfh.second_shop.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.wangfuhe.wfh.second_shop.R;
import com.wangfuhe.wfh.second_shop.base.BaseActivity;
import com.wangfuhe.wfh.second_shop.user.Muser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class MyselfInfo extends BaseActivity implements View.OnClickListener {

    public final static int CONSULT_DOC_PICTURE = 1000;//获取本地照片的回调码
    public final static int CONSULT_DOC_CAMERA = 1001;//获取照相机的回调码
    public final static int CONSULT_DOC_CROP = 1002;//获取截图的回调骂
    private int SELECT_PICTURE = 0;//选择图片方式的参数

    private TextView mUser_name;
    private Context mContext;
    private static final int MYSELF_LOGIN = 1000;
    private RadioButton mMale, mFemale;
    private EditText mAddress, mPhone, mEmail, mQQ;
    private Button mSave;
    private Boolean ismale = true;
    private ImageView mTopPic;
    private BmobFile musertop;
    //通过handler实现加载头像
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            setTopPic(userinfo);
        }
    };
    @Override
    public void initview() {
        setContentView(R.layout.activity_myself_info);
        mContext = this;
        mUser_name = (TextView) findViewById(R.id.user_name_tv);
        mMale = (RadioButton) findViewById(R.id.user_male_rb);
        mFemale = (RadioButton) findViewById(R.id.user_female_rb);
        mAddress = (EditText) findViewById(R.id.user_address_tv);
        mPhone = (EditText) findViewById(R.id.user_phone_tv);
        mEmail = (EditText) findViewById(R.id.user_email_tv);
        mQQ = (EditText) findViewById(R.id.user_qq_tv);
        mSave = (Button) findViewById(R.id.user_save_btn);
        mTopPic = (ImageView) findViewById(R.id.user_top_pic_im);
        if (userinfo == null) {
            mSave.setEnabled(false);
            setLog("button_false");
        }
        intilistern();//建立监听
        //获取当前用户
        if (userinfo != null) {
            setuserinfo(userinfo);
        }
    }

    private void intilistern() {
        mUser_name.setOnClickListener(this);
        mSave.setOnClickListener(this);
        mTopPic.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_name_tv://登入界面
                Intent intent = new Intent(mContext, LoginActivity.class);
                startActivityForResult(intent, MYSELF_LOGIN);
                break;
            case R.id.user_save_btn://保存按钮
                usersave();
                break;
            case R.id.user_top_pic_im://更换头像
                getPicForTop();
                break;
        }
    }
   //保存用户个人信息
    private void usersave() {
        final Muser newuser = new Muser();
        String straddress = mAddress.getText().toString();
        String strphone = mPhone.getText().toString();
        String stremail = mEmail.getText().toString();
        String strqq = mQQ.getText().toString();
        if (straddress != null) {
            newuser.setUseraddress(straddress);
        }
        if (strphone != null) {
            newuser.setMobilePhoneNumber(strphone);
        }
        if (strqq != null) {
            newuser.setUserqq(strqq);
        }
        if (stremail != null) {
            newuser.setEmail(stremail);
        }
        if (mMale.isChecked()) {
            newuser.setGender(ismale);
        } else {
            newuser.setGender(!ismale);
        }
        if (musertop != null) {
            newuser.setToppic(musertop);
            newuser.getToppic().uploadblock(new UploadFileListener() {
                @Override
                public void done(BmobException e) {
                    if (e==null){
                        newuser.update(userinfo.getObjectId(), new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                            if (e==null) {
                                showToastS("保存成功");
                            }else {
                                showToastS("保存失败"+e.getMessage());
                            }
                            }
                        });
                    }else {
                        setLog("头像上传失败");
                    }
                }
            });
        }else {
            newuser.update(userinfo.getObjectId(), new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e==null){
                        showToastS("保存成功");
                    }else {
                        showToastS("保存失败"+e.getMessage());
                    }
                }
            });
        }

    }

    private void getPicForTop() {
        getPicForChoiceMot();//通过选择获得头像
    }

    private void getPicForChoiceMot() {
        CharSequence[] items = {"相册", "相机"};
        new AlertDialog.Builder(this).setTitle("选择头像来源").setItems(items,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == SELECT_PICTURE) {
                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            startActivityForResult(intent, CONSULT_DOC_PICTURE);
                        } else {

                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, CONSULT_DOC_CAMERA);
                        }
                    }
                }).create().show();
    }
//压缩保存图片
    private Uri saveBitmap(Bitmap bm) {
        File tmpDir = new File(Environment.getExternalStorageDirectory() + "/com.wangfuhe.Second_shop");
        if (!tmpDir.exists()) {
            tmpDir.mkdir();
        }
        File img = new File(tmpDir.getAbsolutePath() + "toppic.png");
        musertop = new BmobFile(img);
        try {
            FileOutputStream fos = new FileOutputStream(img);
            bm.compress(Bitmap.CompressFormat.PNG, 85, fos);
            fos.flush();
            fos.close();
            return Uri.fromFile(img);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
//转化文件格式
    private Uri convertUri(Uri uri) {
        InputStream is = null;
        try {
            is = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            is.close();
            return saveBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
//截取图片
    private void startImageZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CONSULT_DOC_CROP);
    }
//设置回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MYSELF_LOGIN) {
            if (resultCode == RESULT_OK) {
               userinfo = BmobUser.getCurrentUser(Muser.class);
            }
        }
        if (requestCode == CONSULT_DOC_CAMERA) {
            if (data == null) {
                return;
            } else {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap bm = extras.getParcelable("data");
                    Uri uri = saveBitmap(bm);
                    startImageZoom(uri);
                }
            }
        } else if (requestCode == CONSULT_DOC_PICTURE) {
            if (data == null) {
                return;
            }
            Uri uri;
            uri = data.getData();
            Uri fileUri = convertUri(uri);
            startImageZoom(fileUri);
        } else if (requestCode == CONSULT_DOC_CROP) {
            if (data == null) {
                return;
            }
            Bundle extras = data.getExtras();
            if (extras == null) {
                return;
            }
            Bitmap bm = extras.getParcelable("data");
            new MyAsytask().execute(bm);
        }
    }
//登录后获取用户信息
    public void setuserinfo(Muser userinfo) {
        this.userinfo = userinfo;
        if (userinfo.getUseraddress() != null) {
            mAddress.setText(userinfo.getUseraddress());
        }
        if (userinfo.getMobilePhoneNumber() != null) {
            mPhone.setText(userinfo.getMobilePhoneNumber());
        }
        if (userinfo.getUserqq() != null) {
            mQQ.setText(userinfo.getUserqq());
        }
        if (userinfo.getEmail() != null) {
            mEmail.setText(userinfo.getEmail());
        }
        if (userinfo.getGender() != null) {
            mMale.setChecked(userinfo.getGender());
            mFemale.setChecked(!userinfo.getGender());
        }
        if (userinfo.getToppic() != null) {
            handler.sendEmptyMessage(1);
        }
        String username = userinfo.getUsername();

        mUser_name.setText(username);
        mSave.setEnabled(true);
        Log.i("wangfuhe", "button_true");

    }
//设置用户头像
    private void setTopPic(Muser userinfo) {
        File tmpDir = new File(Environment.getExternalStorageDirectory() + "/com.wangfuhe.Second_shop");
        if (!tmpDir.exists()) {
            tmpDir.mkdir();
        }
        final File img = new File(tmpDir.getAbsolutePath() + "usertoppic.png");
        Log.i("wangfuhe", tmpDir.getAbsolutePath());
        userinfo.getToppic().download(img, new DownloadFileListener() {
            @Override
            public void onProgress(Integer integer, long l) {

            }

            @Override
            public void done(String s, BmobException e) {
                if (e==null){
                    setLog("下载成功");
                    try {
                        setLog("设置头像前");
                        FileInputStream fis = new FileInputStream(img);
                        Bitmap bitmap = BitmapFactory.decodeStream(fis);
                        fis.close();
                        mTopPic.setImageBitmap(bitmap);
                        setLog("设置头像");
                    } catch (FileNotFoundException ef) {
                        ef.printStackTrace();
                    } catch (IOException ef) {
                        ef.printStackTrace();
                    }
                }else {
                    setLog("下载失败");
                }
            }
        });
    }


    class MyAsytask extends AsyncTask<Bitmap, Void, Bitmap> {
//获取头像
        @Override
        protected Bitmap doInBackground(Bitmap... params) {

            return toRoundBitmap(params[0]);
        }

        public Bitmap toRoundBitmap(Bitmap bitmap) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            float roundPx;
            float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
            if (width <= height) {
                roundPx = width / 2;

                left = 0;
                top = 0;
                right = width;
                bottom = width;

                height = width;

                dst_left = 0;
                dst_top = 0;
                dst_right = width;
                dst_bottom = width;
            } else {
                roundPx = height / 2;

                float clip = (width - height) / 2;

                left = clip;
                right = width - clip;
                top = 0;
                bottom = height;
                width = height;

                dst_left = 0;
                dst_top = 0;
                dst_right = height;
                dst_bottom = height;
            }

            Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);

            final Paint paint = new Paint();
            final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
            final Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
            final RectF rectF = new RectF(dst);

            paint.setAntiAlias(true);// 设置画笔无锯齿

            canvas.drawARGB(0, 0, 0, 0); // 填充整个Canvas

            // 以下有两种方法画圆,drawRounRect和drawCircle
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);// 画圆角矩形，第一个参数为图形显示区域，第二个参数和第三个参数分别是水平圆角半径和垂直圆角半径。
            // canvas.drawCircle(roundPx, roundPx, roundPx, paint);

            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));// 设置两张图片相交时的模式,参考http://trylovecatch.iteye.com/blog/1189452
            canvas.drawBitmap(bitmap, src, dst, paint); // 以Mode.SRC_IN模式合并bitmap和已经draw了的Circle

            return output;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            saveBitmap(bitmap);
            mTopPic.setImageBitmap(bitmap);
        }
    }
}
