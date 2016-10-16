package com.wangfuhe.wfh.second_shop.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.wangfuhe.wfh.second_shop.R;
import com.wangfuhe.wfh.second_shop.fragment.FragmenThree;
import com.wangfuhe.wfh.second_shop.fragment.FragmenTwo;
import com.wangfuhe.wfh.second_shop.fragment.FragmentOne;
import com.wangfuhe.wfh.second_shop.user.Muser;
import com.wangfuhe.wfh.second_shop.user.UserGoods;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class Instanview extends AppCompatActivity {

    private ViewPager mShowPic;
    private ArrayList<Fragment> fragments=new ArrayList<>();
    private FragmentOne mOne;
    private FragmenTwo mTwo;
    private FragmenThree mThree;
    private Intent mIntent;
    private UserGoods mgoods;
    public static Bitmap pic1,pic2,pic3;
    private TextView mdescribe;
    private TextView mprice;
    private TextView msellerinfo;
    private Button mcollect;
    private Muser userinfo;
    private Button mconection;
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            StringBuffer sellerinfo=new StringBuffer();
            sellerinfo.append("卖家信息\n").append("电话："+mgoods.getMaster().getMobilePhoneNumber());
            msellerinfo.setText(sellerinfo);
            mconection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri =Uri.parse("tel:"+mgoods.getMaster().getMobilePhoneNumber());
                    Intent it = new Intent( Intent.ACTION_DIAL , uri );
                    startActivity(it);

                }
            });
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instanview);
        mIntent=this.getIntent();
        userinfo= BmobUser.getCurrentUser(getApplicationContext(),Muser.class);
        mgoods= (UserGoods) mIntent.getExtras().getSerializable("goods");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mgoods!=null){
            new MyInsAsytask().execute(mgoods);
        }

    }

    private void setView(){
        mdescribe.setText(mgoods.getDescribe());
        mprice.setText("￥" + mgoods.getMinprice() + "~" + mgoods.getMaxprice());
        if(mgoods.getMaster()!=null)
        new Thread(){
            @Override
            public void run() {
                super.run();
                BmobQuery<Muser> query=new BmobQuery<Muser>();
                query.addWhereEqualTo("objectId",mgoods.getMaster().getObjectId().toString());
                query.findObjects(getApplicationContext(), new FindListener<Muser>() {
                    @Override
                    public void onSuccess(List<Muser> list) {
                        if(list.size()!=0) {
                            mgoods.setMaster(list.get(0));
                            mHandler.sendEmptyMessage(0);
                        }
                    }
                    @Override
                    public void onError(int i, String s) {
                        Log.i("wangfuhe","master查询失败");
                    }
                });
            }
        }.start();
        mcollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               mgoods.setCollector(userinfo);
                mgoods.update(getApplicationContext(), new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(getApplicationContext(),"收藏成功",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        Toast.makeText(getApplicationContext(),"收藏失败"+s,Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    private void initviewpager() {
        fragments.add(mOne);
        fragments.add(mTwo);
        fragments.add(mThree);
        FragmentPagerAdapter adapter=new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        };
        mShowPic.setAdapter(adapter);
        mShowPic.setCurrentItem(1);
    }

    private void initview() {
        mOne=new FragmentOne();
        mTwo=new FragmenTwo();
        mThree =new FragmenThree();
        mShowPic= (ViewPager) findViewById(R.id.viewpager_show_vp);
        mprice= (TextView) findViewById(R.id.rl_price_tv);
        mdescribe= (TextView) findViewById(R.id.rl_descr_user_tv);
        msellerinfo= (TextView) findViewById(R.id.info_seller_tv);
        mcollect= (Button) findViewById(R.id.goods_collect_btn);
        mconection= (Button) findViewById(R.id.goods_connition_btn);
        if(userinfo==null && mcollect!=null){
            mcollect.setEnabled(false);
        }else {
            mcollect.setEnabled(true);
        }
    }
    public Bitmap getBitmapFromUrl(String urlstring){
        Bitmap bitmap;
        InputStream is=null;
        synchronized (this) {
            try {
                URL url = new URL(urlstring);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                is = new BufferedInputStream(connection.getInputStream());
                bitmap = BitmapFactory.decodeStream(is);
                connection.disconnect();
                return bitmap;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
    class MyInsAsytask extends AsyncTask<UserGoods,Void,Void>{

        @Override
        protected Void doInBackground(UserGoods... params) {
            pic1=getBitmapFromUrl(params[0].getPic1().getFileUrl(getApplicationContext()));
            pic2=getBitmapFromUrl(params[0].getPic2().getFileUrl(getApplicationContext()));
            pic3=getBitmapFromUrl(params[0].getPic3().getFileUrl(getApplicationContext()));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (pic1!=null&&pic2!=null&&pic3!=null)
            {
                initview();
                initviewpager();
                setView();
            }
        }
    }
}
