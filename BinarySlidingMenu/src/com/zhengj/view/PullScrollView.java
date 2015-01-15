package com.zhengj.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

/**
 * 
 * @since July 4, 2013
 * @author kzhang
 */
public class PullScrollView extends ScrollView  {

	private static final String	TAG					= "RefreshAndMoreView";

	// pull state 上拉开始加载更多
	public static final int		PULL_UP_STATE		= 0;

	// pull state 下拉开始刷新
	public static final int		PULL_DOWN_STATE		= 1;

	// release states 释放 去刷新
	public static final int		RELEASE_TO_REFRESH	= 3;

	// release states 释放 去加载更多
	public static final int		RELEASE_TO_LOADING	= 4;

	// 正在刷新
	public static final int		REFRESHING			= 5;

	// 正在加载更多
	public static final int		LOADING				= 6;

	// 没做任何操作
	private static final int	DONE				= 7;

	// 实际的padding的距离与界面上偏移距离的比例
	private final static int	RATIO				= 3;

	private LinearLayout		innerLayout;
	private LinearLayout		bodyLayout;

	private HeaderView			headerView;
	private FooterView			footerView;

	private int					headContentHeight;
	private int					footContentHeight;
	// Down 初始化 Y
	private int					startY;
	private int					scrollY				= -1;

	private Context				mContext;

	// 上下拉状态
	private int					pullState;

	// 刷新、加载更多 接口
	private OnPullListener		onPullListener;

	public PullScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public PullScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public PullScrollView(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {

		this.mContext = context;

		// ScrollView 可以滑动必须有且只有一个子View - ScrollView 内装的View都将放在 innerLayout 里面
		// ScrollView 设置为上下滚动 LinearLayout.VERTICAL
		innerLayout = new LinearLayout(context);
		innerLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		innerLayout.setOrientation(LinearLayout.VERTICAL);

		addheaderView();

		// 设置 bodyLayout 区域
		bodyLayout = new LinearLayout(context);
		bodyLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		bodyLayout.setOrientation(LinearLayout.VERTICAL);
		innerLayout.addView(bodyLayout);

		// 初始化刷新、加载状态
		pullState = DONE;
	}

	/**
	 * 添加 headerView
	 */
	private void addheaderView() {
		headerView = new HeaderView(mContext);
		measureView(headerView);

		headContentHeight = headerView.getMeasuredHeight();

		// 初始化 headerView 位置（不可见）
		headerView.setPaddingTop(-1 * headContentHeight);
		headerView.invalidate();

		innerLayout.addView(headerView);
		addView(innerLayout);
	}

	/**
	 * 添加 footerView
	 */
	private void addfooterView() {
		footerView = new FooterView(mContext);
		measureView(footerView);
		footContentHeight = footerView.getMeasuredHeight();
		footerView.setPaddingButtom();
		footerView.invalidate();
		innerLayout.addView(footerView);
	}

	/**
	 * 添加 BodyView : 滑动内容区域
	 */
	public void addBodyView(View view) {
		bodyLayout.addView(view);
	}

	/**
	 * footer view 在此添加保证添加到 innerLayout 中的最后
	 */
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		addfooterView();
	}

	/**
	 * 滑动时，首先会触发 onInterceptTouchEvent事件，然后触发 onTouchEvent 事件时
	 * 
	 * onInterceptTouchEvent 总是将 onTouchEvent 事件中的 ACTION_DOWN 事件拦截
	 * 
	 * 所以在此做监听，以防万一
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				// 首先拦截down事件,记录y坐标
				scrollY = getScrollY();
				Log.i(TAG, "在down时候记录当前位置scrollY[onInterceptTouchEvent]" + scrollY);
				startY = (int) event.getY();
				Log.i(TAG, "在down时候记录当前位置startY[onInterceptTouchEvent]" + startY);
				break;
			case MotionEvent.ACTION_MOVE:
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
		}
		return super.onInterceptTouchEvent(event);
	}

	/**
	 * 监听上下拉首饰操作
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (isLoadable()) {
			switch (event.getAction()) {

				case MotionEvent.ACTION_DOWN:
					scrollY = getScrollY();
					startY = (int) event.getY();
					break;

				case MotionEvent.ACTION_MOVE:
					if (pullState != REFRESHING && pullState != LOADING) {
					int tempY = (int) event.getY() - startY;

					// 如果 ScrollViwe 滑到最顶端，且有下拉刷新手势，则激活下拉刷新动作
					if (tempY > 0 && scrollY == 0) {
						changeheaderViewHeight(tempY);
					}

					// 如果 ScrollViwe 滑倒最底端，且有上拉刷加载更多手势，则激活上拉加载更多动作
					else if (tempY < 0) { // 上拉加载更多
						changefooterViewHeight(tempY);
					}
					break;
					}

				case MotionEvent.ACTION_UP:

					// 重置 headerView、footerView ,激化监听
					resetPullStateForActionUp();
					break;
			}
		}
		return super.onTouchEvent(event);
	}

	/*
	 * 改变 headerView 高度
	 */
	private void changeheaderViewHeight(int tempY) {

		if (pullState == DONE) {
			pullState = headerView.setStartRefresh();
		}

		if (pullState == PULL_DOWN_STATE || pullState == RELEASE_TO_REFRESH) {
			pullState = headerView.setPadding(headContentHeight, -1 * headContentHeight + tempY / RATIO);
		}
	}

	/*
	 * 改变 footerView 高度
	 */
	private void changefooterViewHeight(int tempY) {
		if (footerView.getVisibility() == View.GONE) {
			return;
		}

		if (getChildAt(0).getMeasuredHeight() <= getScrollY() + getHeight()) {
			if (pullState == DONE) {
				pullState = footerView.setStartLoad();
			}
		}

		if (pullState == PULL_UP_STATE || pullState == RELEASE_TO_LOADING) {
			pullState = footerView.setPadding(footContentHeight, (Math.abs(-tempY) / RATIO));
		}
	}

	/*
	 * 当手离开屏幕后重置 pullState 状态及 headerView、footerView，激活监听事件
	 */
	private void resetPullStateForActionUp() {
		if (pullState != REFRESHING && pullState != LOADING) {

			// 松开手刷新
			if (pullState == RELEASE_TO_REFRESH) {
				pullState = headerView.setRefreshing();
				headerView.setPaddingTop(0);
				onPullListener.refresh();
			}
			// 松开手加载更多
			else if (pullState == RELEASE_TO_LOADING) {
				pullState = footerView.setLoading();
				footerView.setPaddingButtom();
				onPullListener.loadMore();
			}
			// 重置到最初状态
			else {
				headerView.setPaddingTop(-1 * headContentHeight);
				footerView.setPaddingButtom();
				pullState = DONE;
			}
		}
	}

	/**
	 * 判断是否可以上下拉刷新加载手势
	 * 
	 * @return true：可以
	 */
	private boolean isLoadable() {
		if (pullState == REFRESHING || pullState == LOADING)
			return false;
		return true;
	}

	/**
	 * 重置下拉刷新按钮状态并隐藏
	 */
	public void setheaderViewReset() {
		headerView.setStartRefresh();
		headerView.setPaddingTop(-1 * headContentHeight);
		pullState = DONE;
	}

	/**
	 * 加载更多按钮不可见
	 */
	public void setfooterViewGone() {
		footerView.setStartLoad();
		footerView.hide();
		pullState = DONE;
	}

	/**
	 * 加载更多按钮重置为加载更多状态
	 */
	public void setfooterViewReset() {
		footerView.setStartLoad();
		pullState = DONE;
	}

	/**
	 * 如果：高度>0,则有父类完全决定子窗口高度大小；否则，由子窗口自己觉得自己的高度大小
	 * 
	 * 设置 headerView、HootViwe 的 LayoutParams 属性
	 * 
	 * @param childView
	 */
	private void measureView(View childView) {
		ViewGroup.LayoutParams p = childView.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		}
		childView.measure(childWidthSpec, childHeightSpec);
	}

	public interface OnPullListener {
		void refresh();

		void loadMore();
	}

	public void setOnPullListener(OnPullListener onPullListener) {
		this.onPullListener = onPullListener;
	}

}
