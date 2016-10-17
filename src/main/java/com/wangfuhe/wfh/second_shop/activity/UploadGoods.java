package com.wangfuhe.wfh.second_shop.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.wangfuhe.wfh.second_shop.R;
import com.wangfuhe.wfh.second_shop.base.BaseActivity;
import com.wangfuhe.wfh.second_shop.user.UserGoods;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;


/**
 * 上传物品
 */
public class UploadGoods extends BaseActivity implements View.OnClickListener {

    public final static int USER_CONSULT_DOC_PICTURE = 1003;//通过相册获取图片回调码
    public final static int USER_CONSULT_DOC_CAMERA = 1004;//通过照相机获取图片回调码
    public final static int USER_CONSULT_DOC_CROP = 1005;//截取图片的回调码
    private int SELECT_PICTURE = 0;


    private Spinner mGoodscategory;
    private ArrayList<String> mcategorylist;
    private ArrayAdapter<String> adapter;
    private ImageView mGoodsPic1, mGoodsPic2, mGoodsPic3;//物品图片
    private EditText mGoodsDescribe, mGoodsWttrade;//物品描述，和交换方式。
    private RatingBar mNODegree;//物品新旧程度
    private EditText mMinPrice, mMaxPrice;//物品最高和最低价格
    private Button mGoodsUpload;//物品上传按钮
    private static int positonpic = 0;
    private BmobFile mpicture1, mpicture2, mpicture3;
    private String mcategory;

    @Override
    public void initview() {
        setContentView(R.layout.activity_upload_goods);
        //初始化界面
        setview();
        initListener();//设置监听
    }

    private void initListener() {
        mGoodsUpload.setOnClickListener(this);
        mGoodsPic1.setOnClickListener(this);
        mGoodsPic2.setOnClickListener(this);
        mGoodsPic3.setOnClickListener(this);
    }

    //上传商品信息
    private void UploadGoodsInfo() {
        final UserGoods goods = new UserGoods();
        String gDescribe = mGoodsDescribe.getText().toString();
        String gWTtrade = mGoodsWttrade.getText().toString();
        Float currentDegree = mNODegree.getRating();
        Double minprice = null;
        Double maxprice = null;
        if (!mMaxPrice.getText().toString().equals("") && !mMinPrice.getText().toString().equals("")) {
            minprice = Double.parseDouble(mMinPrice.getText().toString());
            maxprice = Double.parseDouble(mMaxPrice.getText().toString());
        }
        boolean isread = true;
        if (mcategory == null) {
            Toast.makeText(getApplicationContext(),
                    "请选择宝贝分类", Toast.LENGTH_SHORT).show();
            isread = false;
        } else {
            goods.setCategory(mcategory);
        }
        if (gDescribe == null) {
            isread = false;
            Toast.makeText(getApplicationContext(),
                    "请描述你的宝贝", Toast.LENGTH_SHORT).show();
        } else {
            goods.setDescribe(gDescribe);
        }
        if (currentDegree == null) {
            isread = false;
            Toast.makeText(getApplicationContext(),
                    "请描述你宝贝的新旧程度", Toast.LENGTH_SHORT).show();
        } else {
            goods.setNodegree(currentDegree);
        }
        if (minprice == null || maxprice == null) {
            isread = false;
            Toast.makeText(getApplicationContext(),
                    "请设置宝贝的价格区间", Toast.LENGTH_SHORT).show();
        } else {
            goods.setMaxprice(maxprice);
            goods.setMinprice(minprice);
        }
        if (mpicture1 == null || mpicture2 == null || mpicture3 == null) {
            Toast.makeText(getApplicationContext(),
                    "请上传宝贝图片", Toast.LENGTH_SHORT).show();
            isread = false;
        } else {
            if (isread) {
                goods.setPic1(mpicture1);
                goods.setPic2(mpicture2);
                goods.setPic3(mpicture3);
//                依次加载图片信息和上传商品信息
                goods.getPic1().upload(new UploadFileListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            showToastS("图片1上传成功");
                            goods.getPic2().upload(new UploadFileListener() {
                                @Override
                                public void done(BmobException e) {
                                    if (e == null) {
                                        showToastS("图片2上传成功");
                                        goods.getPic3().upload(new UploadFileListener() {
                                            @Override
                                            public void done(BmobException e) {
                                                if (e == null) {
                                                    showToastS("图片3上传成功");
                                                    goods.setIssellde(false);
                                                    if (mGoodsWttrade.getText().toString() != null) {
                                                        goods.setWttrade(mGoodsWttrade.getText().toString());
                                                    }
                                                    goods.setMaster(userinfo);

                                                    goods.save(new SaveListener<String>() {
                                                        @Override
                                                        public void done(String s, BmobException e) {
                                                            if (e == null) {
                                                                showToastS("宝贝上传成功");
                                                                setResult(RESULT_OK);
                                                            } else {
                                                                showToastS("宝贝上传失败");
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    }
                });


            }
        }


    }

    private void setview() {
        mcategorylist = new ArrayList<>();
        mcategorylist.add("工具类");
        mcategorylist.add("服装类");
        mcategorylist.add("电器类");
        mcategorylist.add("书籍类");
        //设置类别下拉列表
        mGoodscategory = (Spinner) findViewById(R.id.goods_category_sp);
        //设置下拉列表的适配器
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mcategorylist);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //设置下拉列表选择监听器
        mGoodscategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(),
                        "选择" + adapter.getItem(position), Toast.LENGTH_SHORT).show();
                mcategory = adapter.getItem(position);
                parent.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                parent.setVisibility(View.VISIBLE);
            }
        });
        mGoodscategory.setAdapter(adapter);
        //物品图片
        mGoodsPic1 = (ImageView) findViewById(R.id.goods_pic1_iv);
        mGoodsPic2 = (ImageView) findViewById(R.id.goods_pic2_iv);
        mGoodsPic3 = (ImageView) findViewById(R.id.goods_pic3_iv);
        //物品描述
        mGoodsDescribe = (EditText) findViewById(R.id.goods_describe_et);
        //物品交换方式
        mGoodsWttrade = (EditText) findViewById(R.id.goods_wttrade_et);
        //物品的新旧程度
        mNODegree = (RatingBar) findViewById(R.id.goods_nodegree_rb);
        //物品价格
        mMaxPrice = (EditText) findViewById(R.id.goods_maxprice_et);
        mMinPrice = (EditText) findViewById(R.id.goods_minprice_et);
        //上传按钮
        mGoodsUpload = (Button) findViewById(R.id.goods_upload_btn);
    }

    //选择商品的图片来源
    private void getPicForChoiceMot() {
        CharSequence[] items = {"相册", "相机"};
        new AlertDialog.Builder(this).setTitle("选择物品图片来源").setItems(items,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == SELECT_PICTURE) {
                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            startActivityForResult(intent, USER_CONSULT_DOC_PICTURE);
                        } else {

                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, USER_CONSULT_DOC_CAMERA);
                        }
                    }
                }).create().show();
    }

    //保存图片
    private Uri saveBitmap(Bitmap bm) {
        File tmpDir = new File(Environment.getExternalStorageDirectory() + "/com.wangfuhe.Second_shop");
        if (!tmpDir.exists()) {
            tmpDir.mkdir();
        }
        File img = new File(tmpDir.getAbsolutePath() + "goodspic.png" + positonpic);
        switch (positonpic) {
            case 1:
                mpicture1 = new BmobFile(img);
                break;
            case 2:
                mpicture2 = new BmobFile(img);
                break;
            case 3:
                mpicture3 = new BmobFile(img);
                break;
        }

        try {
            FileOutputStream fos = new FileOutputStream(img);
            bm.compress(Bitmap.CompressFormat.PNG, 95, fos);
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

    //重置图片路径
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

    private void startImageZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, USER_CONSULT_DOC_CROP);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == USER_CONSULT_DOC_CAMERA) {
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
        } else if (requestCode == USER_CONSULT_DOC_PICTURE) {
            if (data == null) {
                return;
            }
            Uri uri;
            uri = data.getData();
            Uri fileUri = convertUri(uri);
            startImageZoom(fileUri);
        } else if (requestCode == USER_CONSULT_DOC_CROP) {
            if (data == null) {
                return;
            }
            Bundle extras = data.getExtras();
            if (extras == null) {
                return;
            }
            Bitmap bm = extras.getParcelable("data");
            saveBitmap(bm);
            switch (positonpic) {
                case 1:
                    mGoodsPic1.setImageBitmap(bm);
                    break;
                case 2:
                    mGoodsPic2.setImageBitmap(bm);
                    break;
                case 3:
                    mGoodsPic3.setImageBitmap(bm);
                    break;
            }

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.goods_upload_btn:
                UploadGoodsInfo();
                break;
            case R.id.goods_pic1_iv:
                getPicForChoiceMot();
                positonpic = 1;
                break;
            case R.id.goods_pic2_iv:
                getPicForChoiceMot();
                positonpic = 2;
                break;
            case R.id.goods_pic3_iv:
                getPicForChoiceMot();
                positonpic = 3;
                break;
        }
    }
}
