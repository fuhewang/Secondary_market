package com.wangfuhe.wfh.second_shop.activity;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;
import com.wangfuhe.wfh.second_shop.R;
import com.wangfuhe.wfh.second_shop.adapter.Myadapter;
import com.wangfuhe.wfh.second_shop.tools.Sqlhelp;
import com.wangfuhe.wfh.second_shop.user.Muser;
import com.wangfuhe.wfh.second_shop.user.UserGoods;

import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public final static int MENU_UPLOAD_PIC = 1002;//上传物品界面的回调码
    public final static int UPLOADEDGOODS = 3;//已上传物品的回调码

    private ArrayList<UserGoods> goodses;
    private RecyclerView mrecyclerView;
    private MenuDrawer mMenuDrawer;
    private Button msearch_btn;
    private RelativeLayout mSelf, mShop, mIdex, muserback, musercollect, muserselled;
    private TextView musername;
    private Context mContext;
    private Muser userinfo;
    private EditText msearch;
    private Myadapter mMyadapter;
    private static final int MAIN_LOGIN_CODE = 999;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                mMyadapter.notifyDataSetChanged();
                Log.i("wangfuhe","浮标查询返回正确");
            }else {
                Log.i("wangfuhe","浮标查询返回出错");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化侧滑菜单
        initMenu();
        //临时数据存储，假数据
        initdata();
        //设置瀑布流
        setwaterfall();
        //设置初始化浮标设置
        rightlowButton();
        //赋值上下文
        mContext = this;

    }

    private void setwaterfall() {
        mrecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mrecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL));
        new MyAsynTask().execute();
//        mMyadapter = new Myadapter(this, goodses);
        //初始化回掉监听和所有的控件监听
//        initlisterner(mMyadapter);
//        mrecyclerView.setAdapter(mMyadapter);
//        //设置瀑布流item之间的距离
//        SpaceItem spaceItem = new SpaceItem(16);
//        mrecyclerView.addItemDecoration(spaceItem);
//        lac.setOrder(LayoutAnimationController.ORDER_NORMAL);
//        mrecyclerView.setLayoutAnimation(lac);
//        mrecyclerView.startLayoutAnimation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.userinfo = BmobUser.getCurrentUser(getApplicationContext(), Muser.class);
        if (userinfo != null) {
            musername.setText(userinfo.getUsername());
        }
    }

    private void initMenu() {
        mMenuDrawer = MenuDrawer.attach(this, Position.LEFT);
        mMenuDrawer.setTouchMode(MenuDrawer.TOUCH_MODE_FULLSCREEN);
        mMenuDrawer.setDropShadowSize(0);
        mMenuDrawer.setContentView(R.layout.activity_main);
        mMenuDrawer.setMenuView(R.layout.menu_left);
        mMenuDrawer.setMenuSize(600);
        mSelf = (RelativeLayout) findViewById(R.id.menu_self_info_rl);
        mIdex = (RelativeLayout) findViewById(R.id.menu_purchased_rl);
        mShop = (RelativeLayout) findViewById(R.id.menu_upload_rl);
        muserback = (RelativeLayout) findViewById(R.id.menu_back_rl);
        musercollect = (RelativeLayout) findViewById(R.id.menu_collected_rl);
        muserselled = (RelativeLayout) findViewById(R.id.menu_salesed_rl);
        musername = (TextView) findViewById(R.id.user_name_tv);
        msearch = (EditText) findViewById(R.id.search_et);
        msearch_btn= (Button) findViewById(R.id.search_btn);
        msearch.clearFocus();
        msearch_btn.setOnClickListener(this);
        mSelf.setOnClickListener(this);
        mIdex.setOnClickListener(this);
        mShop.setOnClickListener(this);
        muserback.setOnClickListener(this);
        musercollect.setOnClickListener(this);
        muserselled.setOnClickListener(this);
        musername.setOnClickListener(this);
//        mTopPic = (ImageView) findViewById(R.id.user_top_pic_im);

    }

    private void initlisterner(final Myadapter myadapter) {
        myadapter.setOnClickListener(new Myadapter.OnItemClickListener() {
            @Override
            public void ItemClickListener(View view, int postion) {
                Intent intent = new Intent(MainActivity.this, Instanview.class);
                intent.putExtra("goods", goodses.get(postion));
                startActivityForResult(intent, 1);
            }

            @Override
            public void ItemLongClickListener(View view, int postion) {
                //长按删除
                goodses.remove(postion);
                myadapter.notifyItemRemoved(postion);
            }
        });

//        mTopPic.setOnClickListener(this);
    }

    private void initdata() {
        goodses = new ArrayList<>();
    }

    private void rightlowButton() {
        final ImageView fabIconNew = new ImageView(this);
        fabIconNew.setImageResource(R.drawable.ic_action_new_light);
        final FloatingActionButton rightLowButton = new FloatingActionButton.Builder(this)
                .setContentView(fabIconNew)
                .build();
        SubActionButton.Builder rLSubBuilder = new SubActionButton.Builder(this);
        ImageView rlIcon1 = new ImageView(this);
        ImageView rlIcon2 = new ImageView(this);
        ImageView rlIcon3 = new ImageView(this);
        ImageView rlIcon4 = new ImageView(this);
        rlIcon1.setImageDrawable(getResources().getDrawable(R.drawable.book));
        rlIcon2.setImageDrawable(getResources().getDrawable(R.drawable.phone));
        rlIcon3.setImageDrawable(getResources().getDrawable(R.drawable.clothing));
        rlIcon4.setImageDrawable(getResources().getDrawable(R.drawable.bycycle));

        final FloatingActionMenu rightLowerMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(rLSubBuilder.setContentView(rlIcon1).build())
                .addSubActionView(rLSubBuilder.setContentView(rlIcon2).build())
                .addSubActionView(rLSubBuilder.setContentView(rlIcon3).build())
                .addSubActionView(rLSubBuilder.setContentView(rlIcon4).build())
                .attachTo(rightLowButton)
                .build();
        initrightbutton(rlIcon1, rlIcon2, rlIcon3, rlIcon4);//设置浮标的监听事件
        if (mMenuDrawer.isMenuVisible()) {
            rightLowerMenu.close(true);
        }
        rightLowerMenu.setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener() {
            @Override
            public void onMenuOpened(FloatingActionMenu menu) {
                // Rotate the icon of rightLowerButton 45 degrees clockwise
                fabIconNew.setRotation(0);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, -90);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(fabIconNew, pvhR);
                animation.start();
            }

            @Override
            public void onMenuClosed(FloatingActionMenu menu) {
                // Rotate the icon of rightLowerButton 45 degrees counter-clockwise
                fabIconNew.setRotation(-90);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 0);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(fabIconNew, pvhR);
                animation.start();
            }
        });

    }

    //设置浮标的监听事件
    private void initrightbutton(ImageView rlIcon1, ImageView rlIcon2, ImageView rlIcon3, ImageView rlIcon4) {
        rlIcon1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Sqlhelp.GetUserGoodsByCategory(getApplicationContext(), "书籍类", mHandler, goodses);
            }
        });
        rlIcon2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Sqlhelp.GetUserGoodsByCategory(getApplicationContext(), "电器类", mHandler, goodses);
            }
        });
        rlIcon3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Sqlhelp.GetUserGoodsByCategory(getApplicationContext(), "服装类", mHandler, goodses);
            }
        });
        rlIcon4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Sqlhelp.GetUserGoodsByCategory(getApplicationContext(), "工具类", mHandler, goodses);
                Log.i("wangfuhe","工具类点击了");
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_upload_rl:
                Intent uploadit = new Intent(MainActivity.this, UploadGoods.class);
                startActivityForResult(uploadit, MENU_UPLOAD_PIC);
//                overridePendingTransition(R.anim.outscaleanim,R.anim.inscaleanim);
                mMenuDrawer.closeMenu();
                break;
            case R.id.menu_self_info_rl:
                Intent it = new Intent(MainActivity.this, MyselfInfo.class);
                startActivityForResult(it, 2);
//                overridePendingTransition(R.anim.outscaleanim,R.anim.inscaleanim);
                mMenuDrawer.closeMenu();
                break;
//            case R.id.user_top_pic_im:
//                getPicForTop();
//                break;
            case R.id.menu_purchased_rl:
                mMenuDrawer.closeMenu();
                Intent uploadedit = new Intent(MainActivity.this, UploadedGoods.class);
                startActivityForResult(uploadedit, UPLOADEDGOODS);
                break;
            case R.id.menu_back_rl:
                BmobUser.logOut(getApplicationContext());   //清除缓存用户对象
                // 现在的currentUser是null了
                this.userinfo = BmobUser.getCurrentUser(getApplicationContext(), Muser.class);
                musername.setText(R.string.loginorsign);
                mMenuDrawer.closeMenu();
                break;
            case R.id.menu_collected_rl:
                mMenuDrawer.closeMenu();
                Intent collectit = new Intent(MainActivity.this, CollectGoods.class);
                startActivityForResult(collectit, 4);
                break;
            case R.id.menu_salesed_rl:
                mMenuDrawer.closeMenu();
                break;
            case R.id.user_name_tv:
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivityForResult(intent, MAIN_LOGIN_CODE);
                mMenuDrawer.closeMenu();
//                overridePendingTransition(R.anim.outscaleanim,R.anim.inscaleanim);
                break;
            case R.id.search_btn:
                String search=msearch.getText().toString();
                if(search!=null){
                    Sqlhelp.GetGoodsBySearch(getApplicationContext(),search,mHandler,goodses);
                }
                break;
        }
    }

    //
    class SpaceItem extends RecyclerView.ItemDecoration {
        private int space;

        public SpaceItem(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.left = space;
            outRect.bottom = space;
            outRect.right = space;
            outRect.top = space;
//            if (parent.getChildAdapterPosition(view) == 0) {
//                outRect.top = space;
//
//            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MENU_UPLOAD_PIC) {
            {
                if (resultCode == RESULT_OK) {
                    BmobQuery<UserGoods> query = new BmobQuery<UserGoods>();
                    //查询当前用户的上传物品
                    query.addWhereEqualTo("issellde", false);
                    query.findObjects(getApplicationContext(), new FindListener<UserGoods>() {
                        @Override
                        public void onSuccess(List<UserGoods> list) {
                            if (list != null) {
                                goodses.clear();
                                if (list.size() != goodses.size()) {
                                    goodses.addAll(list);
                                }
                                if (mMyadapter != null) {
                                    mMyadapter.notifyDataSetChanged();
                                }
                            }
                        }

                        @Override
                        public void onError(int i, String s) {
                            Toast.makeText(getApplicationContext(), "数据查询出错", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }
        if (requestCode == MAIN_LOGIN_CODE) {
            if (resultCode == RESULT_OK) {
                userinfo = BmobUser.getCurrentUser(getApplicationContext(), Muser.class);
                if (userinfo != null) {
                    musername.setText(userinfo.getUsername());
                }
            }
        }
    }

    class MyAsynTask extends AsyncTask<Void, Void, ArrayList<UserGoods>> {

        @Override
        protected ArrayList<UserGoods> doInBackground(Void... params) {
            BmobQuery<UserGoods> query = new BmobQuery<UserGoods>();
            //查询当前用户的上传物品
            query.addWhereEqualTo("issellde", false);
            query.findObjects(getApplicationContext(), new FindListener<UserGoods>() {
                @Override
                public void onSuccess(List<UserGoods> list) {
                    Toast.makeText(getApplicationContext(), "下载成功", Toast.LENGTH_SHORT).show();
                    Log.i("wangfuhe", "首页查询数目" + list.size());
                    if (list != null)
                        if (list.size() != goodses.size()) {
                            goodses.addAll(list);
                        }


                    mMyadapter = new Myadapter(getApplicationContext(),
                            goodses, mrecyclerView);
                    //初始化回掉监听和所有的控件监听
                    initlisterner(mMyadapter);
                    mrecyclerView.setAdapter(mMyadapter);
                    //设置瀑布流item之间的距离
                    SpaceItem spaceItem = new SpaceItem(16);
                    mrecyclerView.addItemDecoration(spaceItem);
                }

                @Override
                public void onError(int i, String s) {
                    Toast.makeText(getApplicationContext(), "数据查询出错", Toast.LENGTH_SHORT).show();
                }
            });
            return goodses;
        }
    }
}
