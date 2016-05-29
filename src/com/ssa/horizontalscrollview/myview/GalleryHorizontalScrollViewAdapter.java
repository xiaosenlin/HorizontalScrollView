package com.ssa.horizontalscrollview.myview;

import java.util.List;

import com.ssa.horizontalscrollview.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class GalleryHorizontalScrollViewAdapter {
	private LayoutInflater mInflater;
	private List<Bitmap> mDatas;

	public GalleryHorizontalScrollViewAdapter(Context context, List<Bitmap> mDatas) {
		mInflater = LayoutInflater.from(context);
		this.mDatas = mDatas;
	}

	public Object getItem(int position) {
		return mDatas.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public int getCount() {
		return mDatas.size();
	}
	
	public View getView(int position, View contentView, ViewGroup parent) {
		ViewHolder myHolder = null;
		if (null == contentView) {
			contentView = mInflater.inflate(R.layout.activity_gallery_item,
					parent, false);
			myHolder = new ViewHolder(contentView);
			contentView.setTag(myHolder);
		}else {
			myHolder = (ViewHolder)contentView.getTag();
		}
		//myHolder.ivImg.setImageResource(mDatas.get(position));
		myHolder.ivImg.setImageBitmap(mDatas.get(position));
		myHolder.tvText.setText("Img_"+position);
		
		
		return contentView;
	}

	private static class ViewHolder {
		ImageView ivImg;
		TextView tvText;

		public ViewHolder(View view) {
			ivImg = (ImageView)view.findViewById(R.id.iv_content);
			tvText =(TextView)view.findViewById(R.id.tv_index);
		}
	}

}
