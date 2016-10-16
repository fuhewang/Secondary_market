package com.wangfuhe.wfh.second_shop.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wangfuhe.wfh.second_shop.R;
import com.wangfuhe.wfh.second_shop.tools.IndexImageLoderUtil;
import com.wangfuhe.wfh.second_shop.user.UserGoods;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wfh on 2016/5/10.
 */
public class Myadapter extends RecyclerView.Adapter<Myadapter.MyViewHolder> {
    private Context mContext;
    private List<Integer> heights;
    private ArrayList<UserGoods> goodses;
    private OnItemClickListener mListener;
    private IndexImageLoderUtil mIndexImageLoderUtil;
    private boolean mFirst;
    private int mstart,mend;
    public static String[] GoodsId;
    public Myadapter(Context mContext, ArrayList<UserGoods> goodses,RecyclerView recyclerView) {
        this.mContext = mContext;
        this.goodses = goodses;
        mIndexImageLoderUtil=new IndexImageLoderUtil(recyclerView);
        GoodsId=new String[goodses.size()];
        for (int i=0;i<goodses.size();i++){
            GoodsId[i]=goodses.get(i).getPic1().getFileUrl(mContext);
        }
        mFirst=true;

    }

    public interface OnItemClickListener {
        void ItemClickListener(View view, int postion);

        void ItemLongClickListener(View view, int postion);
    }

    public void setOnClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    private void GetRandomHeigh(ArrayList<UserGoods> goodses) {
        heights = new ArrayList<>();
        for (int i = 0; i <= goodses.size(); i++) {
//            heights.add((int) (400 + Math.random()*400));
            switch ((i+1)%4){
                case 1:
                    heights.add(700);
                    break;
                case 2:heights.add(550);
                       break;
                case 3:heights.add(700);
                    break;
                case 0:heights.add(550);
                    break;
            }
        }

    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_goods, parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        GetRandomHeigh(goodses);
        UserGoods goods = goodses.get(position);
        ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
        params.height = heights.get(position);
        holder.itemView.setLayoutParams(params);
        holder.mImageView.setTag(goods.getPic1().getFileUrl(mContext));
        mIndexImageLoderUtil.showImageByTast(holder.mImageView, goods.getPic1().getFileUrl(mContext));
        holder.mDescribe.setText(goods.getDescribe());
        holder.mPrice.setText("￥"+String.valueOf(goods.getMinprice())+"~"
                +String.valueOf(goods.getMaxprice()));
        initlistener(holder);
    }

    private void initlistener(final MyViewHolder holder) {
        if(mListener!=null){//如果设置了监听那么它就不为空，然后回调相应的方法
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();//得到当前点击item的位置pos
                    mListener.ItemClickListener(holder.itemView,pos);//把事件交给我们实现的接口那里处理
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = holder.getLayoutPosition();//得到当前点击item的位置pos
                    mListener.ItemLongClickListener(holder.itemView,pos);//把事件交给我们实现的接口那里处理
                    return true;
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return goodses.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView mImageView;
        TextView mDescribe;
        TextView mPrice;

        public MyViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.temp_iv);
            mDescribe = (TextView) itemView.findViewById(R.id.goods_describe_tv);
            mPrice = (TextView) itemView.findViewById(R.id.goods_price_tv);
        }
    }
//    class ImageScrollistener extends RecyclerView.OnScrollListener{
//        @Override
//        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//            super.onScrollStateChanged(recyclerView, newState);
//        }
//
//        @Override
//        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//            super.onScrolled(recyclerView, dx, dy);
//        }
//    }
}
