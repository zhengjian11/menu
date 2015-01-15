package com.zhengj.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

import com.nineoldandroids.view.ViewHelper;
import com.zhengj.utils.ScreenUtils;
import com.zj.R;

/**
 * 
 * @author zhengjian
 * 
 */
public class BinarySlidingMenu extends ViewGroup {
	/**
	 * 菜单的宽度
	 */
	private int mMenuWidth;
	private int mHalfMenuWidth = 0;

	private boolean isOperateRight =false;
	private boolean isOperateLeft = false;


	private View mContent;

	private boolean isLeftMenuOpen;
	private boolean isRightMenuOpen = false;

	private int minScrollWidth = 0;
	private int correctWidth =  0; 

	/**
	 * 回调的接口
	 * 
	 * @author zhengjian
	 * 
	 */
	public interface OnMenuOpenListener {
		/**
		 * 
		 * @param isOpen
		 *            true打开菜单，false关闭菜单
		 * @param flag
		 *            0 左侧， 1右侧
		 */
		void onMenuOpen(boolean isOpen, int flag);
	}

	public OnMenuOpenListener mOnMenuOpenListener;
	private VelocityTracker mVelocityTracker;
	private int left_distince;
	private Scroller cmScroller;

	public void setOnMenuOpenListener(OnMenuOpenListener mOnMenuOpenListener) {
		this.mOnMenuOpenListener = mOnMenuOpenListener;
	}

	public BinarySlidingMenu(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		mScreenWidth = ScreenUtils.getScreenWidth(context);
		cmScroller = new Scroller(context);
		left_distince = context.getResources().getDimensionPixelSize(
				R.dimen.left_distince);
		ViewConfiguration configuration = ViewConfiguration.get(context);
		touchSlop = configuration.getScaledTouchSlop();
		drag_min_distince = context.getResources().getDimensionPixelSize(
				R.dimen.drag_min_distince);
		minScrollWidth = left_distince / 3;
		correctWidth = context.getResources().getDimensionPixelSize(
				R.dimen.correctWidth);
		
	}

	/**
	 * 屏幕宽度
	 */
	private int mScreenWidth;

	/**
	 * dp 菜单距离屏幕的右边距
	 */
	// private int mMenuRightPadding;

	public BinarySlidingMenu(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

	}

	public BinarySlidingMenu(Context context) {
		this(context, null, 0);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		mMenuWidth = left_distince;
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		final int width = MeasureSpec.getSize(widthMeasureSpec);

		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		Log.e("onMeasure", "onMeasure width = " + width);

		mContent = getChildAt(1);

		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		}
	}

	private float mLastMotionX;
	private float mLastMotionY;
	private int touchSlop;

	private int drag_min_distince = 10;
	private float mLastMotionX1;

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {

		final int action = ev.getAction();
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(ev);

		float x = ev.getX();
		float y = ev.getY();
		switch (action) {
		case MotionEvent.ACTION_MOVE:
			x = ev.getX();
			y = ev.getY();

			final int xDiff = (int) Math.abs(x - mLastMotionX);
			final int yDiff = (int) Math.abs(y - mLastMotionY);

			boolean xMoved = (Math.abs(xDiff) > (Math.abs(yDiff) + drag_min_distince))
					&& (xDiff > touchSlop);

			if (xMoved) {
				return true;
			} else {
				return false;
			}
		case MotionEvent.ACTION_DOWN:
			mLastMotionX = x;
			mLastMotionY = y;
			break;

		case MotionEvent.ACTION_CANCEL:
			return false;
		case MotionEvent.ACTION_UP:
			mLastMotionX = x;
			mLastMotionY = y;
		default:
			break;
		}

		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		int action = ev.getAction();

		switch (action) {
		case MotionEvent.ACTION_MOVE:
			float x1 = ev.getX();
			if (mLastMotionX1 != 0) {

				int dx = (int) (x1 - mLastMotionX1);
				int scrollX = getScrollX();
				int resultScrollX = scrollX - dx;

				if (dx < 0) {
					if (resultScrollX >= 2 * left_distince) {
						dx = 0;
					}
				} else {

					if (resultScrollX <= 0) {
						dx = 0;
					}
				}
				scrollBy(-dx, 0);
			}
			mLastMotionX1 = x1;
			break;
		case MotionEvent.ACTION_DOWN:
			mLastMotionX1 = 0;
			break;

		case MotionEvent.ACTION_UP:
			mLastMotionX1 = 0;

			int scrollX = getScrollX();
			int dx = 2 * left_distince - getScrollX();

			if (getScrollX() <= mMenuWidth) {

				if (isLeftMenuOpen) {
					if (scrollX > minScrollWidth) {

						dx = mMenuWidth - scrollX;
						int startX = scrollX;
						cmScroller.startScroll(startX, 0, dx, 0,
								Math.abs(dx) * 2);

						if (isLeftMenuOpen && mOnMenuOpenListener != null) {
							mOnMenuOpenListener.onMenuOpen(false, 0);
						}
						isLeftMenuOpen = false;

					} else
					{

						dx = -scrollX;
						int startX = scrollX;
						cmScroller.startScroll(startX, 0, dx, 0,
								Math.abs(dx) * 2);
						if (!isLeftMenuOpen && mOnMenuOpenListener != null) {
							mOnMenuOpenListener.onMenuOpen(true, 0);
						}
						isLeftMenuOpen = true;

					}
				} else {

					if (scrollX < mMenuWidth - minScrollWidth) {
						dx = -scrollX;
						int startX = scrollX;
						cmScroller.startScroll(startX, 0, dx, 0,
								Math.abs(dx) * 2);
						if (isLeftMenuOpen && mOnMenuOpenListener != null) {
							mOnMenuOpenListener.onMenuOpen(true, 0);
						}
						isLeftMenuOpen = true;

					} else {
						dx = mMenuWidth - scrollX;
						int startX = scrollX;
						cmScroller.startScroll(startX, 0, dx, 0,
								Math.abs(dx) * 2);
						if (!isLeftMenuOpen && mOnMenuOpenListener != null) {
							mOnMenuOpenListener.onMenuOpen(false, 0);
						}
						isLeftMenuOpen = false;

					}

				}
			}

			if (getScrollX() >= mMenuWidth) {
				if (isRightMenuOpen == false) {
					if (scrollX > (mMenuWidth + minScrollWidth)) {
						int startX = scrollX;
						cmScroller.startScroll(startX, 0, dx, 0,
								Math.abs(dx) * 2);
						if (!isRightMenuOpen && mOnMenuOpenListener != null) {
							mOnMenuOpenListener.onMenuOpen(true, 1);
						}
						isRightMenuOpen = true;
					} else {
						dx = mMenuWidth - getScrollX();
						int startX = scrollX;
						cmScroller.startScroll(startX, 0, dx, 0,
								Math.abs(dx) * 2);
						if (isRightMenuOpen && mOnMenuOpenListener != null) {
							mOnMenuOpenListener.onMenuOpen(false, 1);
						}
						isRightMenuOpen = false;
					}
				} else {
					if (scrollX < (2 * mMenuWidth - minScrollWidth)) {
						dx = mMenuWidth - getScrollX();
						int startX = scrollX;
						cmScroller.startScroll(startX, 0, dx, 0,
								Math.abs(dx) * 2);
						if (isRightMenuOpen && mOnMenuOpenListener != null) {
							mOnMenuOpenListener.onMenuOpen(false, 1);
						}
						isRightMenuOpen = false;
					} else {
						int startX = scrollX;
						cmScroller.startScroll(startX, 0, dx, 0,
								Math.abs(dx) * 2);
						if (!isRightMenuOpen && mOnMenuOpenListener != null) {
							mOnMenuOpenListener.onMenuOpen(true, 1);
						}
						isRightMenuOpen = true;
					}
				}
			}

			postInvalidate();
			break;
		default:
			break;
		}
		return true;
	}

	@Override
	public void computeScroll() {
		if (cmScroller.computeScrollOffset()) {
			scrollTo(cmScroller.getCurrX(), cmScroller.getCurrY());
		}
		scrollChanged();
		postInvalidate();
	}

	protected void scrollChanged() {
		int l = getScrollX();
		if (getScrollX() >= mMenuWidth) {
			float scale = l * 1.0f / mMenuWidth;
			isOperateRight = true;
			isOperateLeft = false;
			float scale1 = 1 - ((mMenuWidth * (scale - 1)) / mMenuWidth) * 0.2f;
			float scale2 = 1 - ((mMenuWidth * (scale - 1)) / mMenuWidth) * 0.3f;
			ViewHelper.setAlpha(getChildAt(2),  Math.abs(scale-1));
			ViewHelper.setScaleX(mContent, (scale1));
			ViewHelper.setScaleY(mContent, (scale2));

		} else {
			float scale = l * 1.0f / mMenuWidth;
			isOperateRight = false;
			isOperateLeft = true;
			float scale1 = ((mMenuWidth * (scale - 1)) / mMenuWidth) * 0.2f + 1;
			float scale2 = ((mMenuWidth * (scale - 1)) / mMenuWidth) * 0.3f + 1;
			ViewHelper.setAlpha(getChildAt(0), Math.abs(scale-1));
			ViewHelper.setScaleX(mContent, (scale1));
			ViewHelper.setScaleY(mContent, (scale2));
		}

	}

	public void scrollRightMenu() {
		int scrollX = getScrollX();
		int dx = 2 * left_distince - getScrollX();
		Log.d("scrollX", "scrollX===" + scrollX);

		if (isRightMenuOpen) {
			dx = mMenuWidth - getScrollX();
			int startX = scrollX;
			cmScroller.startScroll(startX, 0, dx, 0, Math.abs(dx) * 2);
			if (isRightMenuOpen && mOnMenuOpenListener != null) {
				mOnMenuOpenListener.onMenuOpen(false, 1);
			}
			isRightMenuOpen = false;
		} else {
			int startX = scrollX;
			if (!isRightMenuOpen && mOnMenuOpenListener != null) {
				mOnMenuOpenListener.onMenuOpen(true, 1);
			}
			cmScroller.startScroll(startX, 0, dx, 0, Math.abs(dx) * 2);
			isRightMenuOpen = true;
		}

		postInvalidate();
	}

	public void scrollLeftMenu() {
		int scrollX = getScrollX();
		int dx = 2 * left_distince - getScrollX();

		if (isLeftMenuOpen) {
			dx = mMenuWidth - scrollX;
			int startX = scrollX;
			cmScroller.startScroll(startX, 0, dx, 0, Math.abs(dx) * 2);

			if (isLeftMenuOpen && mOnMenuOpenListener != null) {
				mOnMenuOpenListener.onMenuOpen(false, 0);
			}
			isLeftMenuOpen = false;
		} else {
			dx = -scrollX;
			int startX = scrollX;
			cmScroller.startScroll(startX, 0, dx, 0, Math.abs(dx) * 2);
			if (!isLeftMenuOpen && mOnMenuOpenListener != null) {
				mOnMenuOpenListener.onMenuOpen(true, 0);
			}
			isLeftMenuOpen = true;
		}

		postInvalidate();
	}

	public boolean isRightMenuOpen() {
		return isRightMenuOpen;
	}

	@Override
	protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {
		final int childCount = getChildCount();

		for (int i = 0; i < childCount; i++) {

			final View childView = getChildAt(i);

			if (childView.getVisibility() != View.GONE) {

				if (i == 0) {
					childView.layout(0, 0, left_distince+correctWidth,
							childView.getMeasuredHeight());

				} else if (i == 1) {
					childView
							.layout(left_distince, 0,

							left_distince + mScreenWidth,
									childView.getMeasuredHeight());

				} else {
					childView.layout(left_distince + mScreenWidth-correctWidth, 0,

					2 * left_distince + mScreenWidth,
							childView.getMeasuredHeight());
				}
			}
		}
		scrollTo(left_distince, 0);

	}
}
