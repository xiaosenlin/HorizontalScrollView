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
	private LinearLayout mContainer;// MyHorizontalScrollView中的LinearLayout
	private int mChildWidth;// 子元素的宽度
	private int mChildHeight;// 子元素的高度

	private int mAllLastIndex;// 当前的最后一张的index
	private int mdisplayLastIndex;// 当前显示的最后一张的index
	private int mAllFirstIndex;// 当前的第一张index

	private GalleryHorizontalScrollViewAdapter mAdapter;// 数据适配器
	private int mScreenWidth;// 屏幕的宽度

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
	 * 图片滚动时回调接口
	 */
	public interface OnCurrentImageChangeListener {
		void onCurrentImgChanged(int position, View view);
	}

	/**
	 * 点击图片时回调接口
	 */
	public interface OnClickImageChangeListener {
		void onClickImageChangeListener(int position, View view);
	}

	public GalleryHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// 获取屏幕宽度
		mScreenWidth = getResources().getDisplayMetrics().widthPixels;
	}

	/**
	 * 初始化数据，设置适配器
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
			 * 上面注释掉的是一位老师的写法，但我查了好多资料，用参数0和View.MeasureSpec.UNSPECIFIED是一种不太优美的做法；
			 * 好的做法应该是
			 * 当View为match_parent时，无法测量出View的大小（任玉刚大神讲的，确实是这么一回事,这个具体的原因要结合源码分析，可以看一下任大神的博客）
			 * 当View宽高为具体的数值时，比如100px：
			 * int w =View.MeasureSpec.makeMeasureSpec(100, View.MeasureSpec.EXACTLY);
			 * int h =View.MeasureSpec.makeMeasureSpec(100, View.MeasureSpec.EXACTLY);
			 * view.measure(w, h);
			 * 当View宽高为wrap_content时：
			 * int w =View.MeasureSpec.makeMeasureSpec((1<<30)-1, View.MeasureSpec.AT_MOST);
			 * int h =View.MeasureSpec.makeMeasureSpec((1<<30)-1, View.MeasureSpec.AT_MOST);
			 * view.measure(w, h);
			 * 
			 * 我的此View高度为固定的150dip,宽度为wrap_content
			 */
			int heightPx = DisplayUtil.dip2px(getContext(), 150);
			int w =View.MeasureSpec.makeMeasureSpec((1<<30)-1, View.MeasureSpec.AT_MOST);
			int h =View.MeasureSpec.makeMeasureSpec(heightPx, View.MeasureSpec.EXACTLY);
			view.measure(w, h);
			mChildHeight = view.getMeasuredHeight();
			mChildWidth = view.getMeasuredWidth();
			// 计算每次加载多少个item
			mdisplayLastIndex = mScreenWidth / mChildWidth;
			mCountOneScreen = mdisplayLastIndex + 1;
			initFirstScreenChildren(mdisplayLastIndex + 1);

		}
	}

	/**
	 * 加载第一屏的元素
	 * 
	 * @param mDisplayCountOneScreen
	 */
	private void initFirstScreenChildren(int mDisplayCountOneScreen) {
		mContainer = (LinearLayout) getChildAt(0);
		mContainer.removeAllViews();
		mViewPos.clear();
		for (int i = 0; i < mDisplayCountOneScreen; i++) {
			View view = mAdapter.getView(i, null, mContainer);
			// 待完善的点击事件
			view.setOnClickListener(this);
			mContainer.addView(view);
			mViewPos.put(view, i);
			mAllLastIndex = i;
		}

		// 初始化并刷新界面
		if (null != mOnCurrentImageChangeListener) {
			notifyCurrentImgChanged();
		}
	}

	private void notifyCurrentImgChanged() {
		// 先清除所有的背景颜色，点击时设置为蓝色
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
				// 加载下一页，移除第一张
				loadNextImg();
			}
			if (scrollX == 0) {
				// 加载上一页，移除最后一张
				loadPreImg();
			}
			break;
		}

		return super.onTouchEvent(ev);
	}

	private void loadNextImg() {// 数组边界值计算
		if (mAllLastIndex == mAdapter.getCount() - 1) {
			return;
		}
		// 移除第一张图片，且将水平滚动位置置0
		scrollTo(0, 0);
		mViewPos.remove(mContainer.getChildAt(0));
		mContainer.removeViewAt(0);

		// 获取下一张图片，并且设置onclick事件，且加入容器中
		View view = mAdapter.getView(++mAllLastIndex, null, mContainer);
		view.setOnClickListener(this);
		mContainer.addView(view);
		mViewPos.put(view, mAllLastIndex);

		// 当前第一张图片小标
		mAllFirstIndex++;
		// 如果设置了滚动监听则触发
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
			// 移除最后一张
			int oldViewPos = mContainer.getChildCount() - 1;
			mViewPos.remove(mContainer.getChildAt(oldViewPos));
			mContainer.removeViewAt(oldViewPos);
			// 将加入的View放在第一个位置
			View view = mAdapter.getView(index, null, mContainer);
			mViewPos.put(view, index);
			mContainer.addView(view, 0);
			view.setOnClickListener(this);
			// 水平滚动位置向左移动View的宽度的像素
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
