package com.wangfuhe.wfh.second_shop.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.wangfuhe.wfh.second_shop.R;
import com.wangfuhe.wfh.second_shop.activity.Instanview;

/**
 * Created by wfh on 2016/5/12.
 */
public class FragmenTwo extends Fragment {
    private ImageView mImageView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.item_image,container,false);
         mImageView= (ImageView) view.findViewById(R.id.show_pic_iv);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mImageView.setImageBitmap(Instanview.pic2);
    }
}
