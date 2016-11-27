package com.ssa.horizontalscrollview;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.ssa.horizontalscrollview.myUtils.BitmapUtil;
import com.ssa.horizontalscrollview.myview.GalleryHorizontalScrollView;
import com.ssa.horizontalscrollview.myview.GalleryHorizontalScrollView.OnClickImageChangeListener;
import com.ssa.horizontalscrollview.myview.GalleryHorizontalScrollView.OnCurrentImageChangeListener;
import com.ssa.horizontalscrollview.myview.GalleryHorizontalScrollViewAdapter;

public class MainActivity extends Activity {
    private GalleryHorizontalScrollView mHorizontalScrollView;
    private GalleryHorizontalScrollViewAdapter mAdapter;
    private ImageView mImg;
    /*private List<Integer> mDataRes = new ArrayList<Integer>(Arrays.asList(
    		R.drawable.a, R.drawable.b, R.drawable.c, R.drawable.d,
    		R.drawable.e,R.drawable.f,R.drawable.g));*/
    private List<Integer> mDataRes = new ArrayList<Integer>(Arrays.asList(R.drawable.a, R.drawable.b, R.drawable.c));
    private List<Bitmap> mDatas = new ArrayList<Bitmap>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImg = (ImageView) findViewById(R.id.iv_content);
        fillDatas(mDataRes);
        mHorizontalScrollView = (GalleryHorizontalScrollView) findViewById(R.id.mhsv_gallery_container);
        mAdapter = new GalleryHorizontalScrollViewAdapter(this, mDatas);
        mHorizontalScrollView.setmOnCurrentImageChangeListener(new OnCurrentImageChangeListener() {

            @Override
            public void onCurrentImgChanged(int position, View view) {
                mImg.setImageBitmap(mDatas.get(position));
                view.setBackgroundColor(Color.parseColor("#6d9eeb"));
            }
        });
        mHorizontalScrollView.setmOnClickImageListener(new OnClickImageChangeListener() {

            @Override
            public void onClickImageChangeListener(int position, View view) {
                mImg.setImageBitmap(mDatas.get(position));
            }
        });
        mHorizontalScrollView.initData(mAdapter);
    }

    private void fillDatas(List<Integer> resIds) {
        for (Integer id : resIds) {
            mDatas.add(BitmapUtil.decodeSampledBitmapFromResources(getResources(), id, 300, 300));
        }
    }
}
