package com.ssa.horizontalscrollview.myview;

import java.util.HashMap;
import java.util.Map;

import com.ssa.horizontalscrollview.myUtils.DisplayUtil;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

public class GalleryHorizontalScrollView extends HorizontalScrollView implements
		OnClickListener {
	private LinearLayout mContainer;// MyHorizontalScrollView�е�LinearLayout
	private int mChildWidth;// ��Ԫ�صĿ��
	private int mChildHeight;// ��Ԫ�صĸ߶�

	private int mAllLastIndex;// ��ǰ�����һ�ŵ�index
	private int mdisplayLastIndex;// ��ǰ��ʾ�����һ�ŵ�index
	private int mAllFirstIndex;// ��ǰ�ĵ�һ��index

	private GalleryHorizontalScrollViewAdapter mAdapter;// ����������
	private int mScreenWidth;// ��Ļ�Ŀ��

	private int mCountOneScreen;

	private Map<View, Integer> mViewPos = new HashMap<View, Integer>();

	private OnCurrentImageChangeListener mOnCurrentImageChangeListener;

	private OnClickImageChangeListener mOnClickImageChangeListener;

	public void setmOnCurrentImageChangeListener(
			OnCurrentImageChangeListener mListener) {
		this.mOnCurrentImageChangeListener = mListener;
	}

	public void setmOnClickImageListener(OnClickImageChangeListener mListener) {
		this.mOnClickImageChangeListener = mListener;
	}

	/**
	 * ͼƬ����ʱ�ص��ӿ�
	 */
	public interface OnCurrentImageChangeListener {
		void onCurrentImgChanged(int position, View view);
	}

	/**
	 * ���ͼƬʱ�ص��ӿ�
	 */
	public interface OnClickImageChangeListener {
		void onClickImageChangeListener(int position, View view);
	}

	public GalleryHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// ��ȡ��Ļ���
		mScreenWidth = getResources().getDisplayMetrics().widthPixels;
	}

	/**
	 * ��ʼ�����ݣ�����������
	 */
	public void initData(GalleryHorizontalScrollViewAdapter mAdapter) {
		this.mAdapter = mAdapter;
		mContainer = (LinearLayout) getChildAt(0);
		final View view = mAdapter.getView(0, null, mContainer);
		mContainer.addView(view);
		if (mChildHeight == 0 && mChildWidth == 0) {
			/*int w = View.MeasureSpec.makeMeasureSpec(0,
					View.MeasureSpec.UNSPECIFIED);
			int h = View.MeasureSpec.makeMeasureSpec(0,
					View.MeasureSpec.UNSPECIFIED);*/
			/**
			 * ����ע�͵�����һλ��ʦ��д�������Ҳ��˺ö����ϣ��ò���0��View.MeasureSpec.UNSPECIFIED��һ�ֲ�̫������������
			 * �õ�����Ӧ����
			 * ��ViewΪmatch_parentʱ���޷�������View�Ĵ�С������մ��񽲵ģ�ȷʵ����ôһ����,��������ԭ��Ҫ���Դ����������Կ�һ���δ���Ĳ��ͣ�
			 * ��View���Ϊ�������ֵʱ������100px��
			 * int w =View.MeasureSpec.makeMeasureSpec(100, View.MeasureSpec.EXACTLY);
			 * int h =View.MeasureSpec.makeMeasureSpec(100, View.MeasureSpec.EXACTLY);
			 * view.measure(w, h);
			 * ��View���Ϊwrap_contentʱ��
			 * int w =View.MeasureSpec.makeMeasureSpec((1<<30)-1, View.MeasureSpec.AT_MOST);
			 * int h =View.MeasureSpec.makeMeasureSpec((1<<30)-1, View.MeasureSpec.AT_MOST);
			 * view.measure(w, h);
			 * 
			 * �ҵĴ�View�߶�Ϊ�̶���150dip,���Ϊwrap_content
			 */
			int heightPx = DisplayUtil.dip2px(getContext(), 150);
			int w =View.MeasureSpec.makeMeasureSpec((1<<30)-1, View.MeasureSpec.AT_MOST);
			int h =View.MeasureSpec.makeMeasureSpec(heightPx, View.MeasureSpec.EXACTLY);
			view.measure(w, h);
			mChildHeight = view.getMeasuredHeight();
			mChildWidth = view.getMeasuredWidth();
			// ����ÿ�μ��ض��ٸ�item
			mdisplayLastIndex = mScreenWidth / mChildWidth;
			mCountOneScreen = mdisplayLastIndex + 1;
			initFirstScreenChildren(mdisplayLastIndex + 1);

		}
	}

	/**
	 * ���ص�һ����Ԫ��
	 * 
	 * @param mDisplayCountOneScreen
	 */
	private void initFirstScreenChildren(int mDisplayCountOneScreen) {
		mContainer = (LinearLayout) getChildAt(0);
		mContainer.removeAllViews();
		mViewPos.clear();
		for (int i = 0; i < mDisplayCountOneScreen; i++) {
			View view = mAdapter.getView(i, null, mContainer);
			// �����Ƶĵ���¼�
			view.setOnClickListener(this);
			mContainer.addView(view);
			mViewPos.put(view, i);
			mAllLastIndex = i;
		}

		// ��ʼ����ˢ�½���
		if (null != mOnCurrentImageChangeListener) {
			notifyCurrentImgChanged();
		}
	}

	private void notifyCurrentImgChanged() {
		// ��������еı�����ɫ�����ʱ����Ϊ��ɫ
		for (int i = 0; i < mContainer.getChildCount(); i++) {
			mContainer.getChildAt(i).setBackgroundColor(Color.WHITE);
		}
		mOnCurrentImageChangeListener.onCurrentImgChanged(mAllFirstIndex,
				mContainer.getChildAt(0));
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		/*
		 * Log.e("X", getX()+""); Log.e("ChildX",
		 * mContainer.getChildAt(0).getX()+""); Log.e("RawX",getLeft() +"");
		 */
		switch (ev.getAction()) {

		case MotionEvent.ACTION_MOVE:
			int scrollX = getScrollX();
			Log.e("ScrollX", scrollX + "");
			if (scrollX >= mChildWidth) {
				// ������һҳ���Ƴ���һ��
				loadNextImg();
			}
			if (scrollX == 0) {
				// ������һҳ���Ƴ����һ��
				loadPreImg();
			}
			break;
		}

		return super.onTouchEvent(ev);
	}

	private void loadNextImg() {// ����߽�ֵ����
		if (mAllLastIndex == mAdapter.getCount() - 1) {
			return;
		}
		// �Ƴ���һ��ͼƬ���ҽ�ˮƽ����λ����0
		scrollTo(0, 0);
		mViewPos.remove(mContainer.getChildAt(0));
		mContainer.removeViewAt(0);

		// ��ȡ��һ��ͼƬ����������onclick�¼����Ҽ���������
		View view = mAdapter.getView(++mAllLastIndex, null, mContainer);
		view.setOnClickListener(this);
		mContainer.addView(view);
		mViewPos.put(view, mAllLastIndex);

		// ��ǰ��һ��ͼƬС��
		mAllFirstIndex++;
		// ��������˹��������򴥷�
		if (mOnCurrentImageChangeListener != null) {
			notifyCurrentImgChanged();
		}

	}

	private void loadPreImg() {
		if (mAllFirstIndex == 0) {
			return;
		}
		int index = mAllLastIndex - mCountOneScreen;
		if (index >= 0) {
			// �Ƴ����һ��
			int oldViewPos = mContainer.getChildCount() - 1;
			mViewPos.remove(mContainer.getChildAt(oldViewPos));
			mContainer.removeViewAt(oldViewPos);
			// �������View���ڵ�һ��λ��
			View view = mAdapter.getView(index, null, mContainer);
			mViewPos.put(view, index);
			mContainer.addView(view, 0);
			view.setOnClickListener(this);
			// ˮƽ����λ�������ƶ�View�Ŀ�ȵ�����
			scrollTo(mChildWidth, 0);

			mAllLastIndex--;
			mAllFirstIndex--;

			if (null != mOnCurrentImageChangeListener) {
				notifyCurrentImgChanged();
			}
		}
	}

	@Override
	public void onClick(View v) {
		if(null!=mOnClickImageChangeListener){
			mOnClickImageChangeListener.onClickImageChangeListener(mViewPos.get(v), v);
		}
	}
}
