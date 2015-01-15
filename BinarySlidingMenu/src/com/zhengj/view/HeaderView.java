package com.zhengj.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zj.R;

/**
 * 
 * @since July 4, 2013
 * @author kzhang
 */
public class HeaderView extends LinearLayout {

	private ProgressBar	progressBar;
	private ImageView	arrows;
	private TextView	tvRefresh;
	private TextView	tvDate;
	private boolean		isArrowsUp	= true;

	public HeaderView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public HeaderView(Context context) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.pull_header, this);

		init(context);
	}

	private void init(Context context) {
		progressBar = (ProgressBar) findViewById(R.id.pull_to_refresh_progress);
		arrows = (ImageView) findViewById(R.id.pull_to_refresh_image);
		tvRefresh = (TextView) findViewById(R.id.pull_to_refresh_text);
		tvDate = (TextView) findViewById(R.id.pull_to_refresh_updated_at);
	}

	/**
	 * 下拉刷新
	 */
	public int setStartRefresh() {
		arrows.setVisibility(View.VISIBLE);
		tvRefresh.setVisibility(View.VISIBLE);
		tvDate.setVisibility(View.VISIBLE);
		progressBar.setVisibility(View.GONE);
		tvRefresh.setText("下拉刷新");

		if (!isArrowsUp) {
			RotateAnimation mReverseFlipAnimation = new RotateAnimation(-180, 0, RotateAnimation.RELATIVE_TO_SELF,
					0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
			mReverseFlipAnimation.setInterpolator(new LinearInterpolator());
			mReverseFlipAnimation.setDuration(250);
			mReverseFlipAnimation.setFillAfter(true);

			arrows.clearAnimation();
			arrows.setAnimation(mReverseFlipAnimation);
		}

		isArrowsUp = true;
		return PullScrollView.PULL_DOWN_STATE;
	}

	/**
	 * 松开手刷新
	 */
	public int releaseFreshing() {
		arrows.setVisibility(View.VISIBLE);
		tvRefresh.setVisibility(View.VISIBLE);
		tvDate.setVisibility(View.VISIBLE);
		progressBar.setVisibility(View.GONE);
		tvRefresh.setText("松开手刷新");

		if (isArrowsUp) {
			RotateAnimation animationUp = new RotateAnimation(0, -180, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
					RotateAnimation.RELATIVE_TO_SELF, 0.5f);
			animationUp.setInterpolator(new LinearInterpolator());
			animationUp.setDuration(250);
			animationUp.setFillAfter(true);

			arrows.clearAnimation();
			arrows.setAnimation(animationUp);
		}

		isArrowsUp = false;
		return PullScrollView.RELEASE_TO_REFRESH;
	}

	/**
	 * 正在刷新
	 */
	public int setRefreshing() {
		arrows.clearAnimation();
		arrows.setVisibility(View.GONE);
		tvRefresh.setVisibility(View.VISIBLE);
		tvDate.setVisibility(View.VISIBLE);
		progressBar.setVisibility(View.VISIBLE);
		tvRefresh.setText("正在刷新...");
		return PullScrollView.REFRESHING;
	}

	/**
	 * 设置 View 高度
	 * 
	 * @param presetHeight
	 *            原始高度
	 * @param currentHeight
	 *            当前高度
	 */
	public int setPadding(int presetHeight, int currentHeight) {
		this.setPadding(0, currentHeight, 0, 0);

		// 初始化箭头状态向下
		if (currentHeight <= presetHeight) {
			return setStartRefresh();
		} else { // 改变按钮状态向上
			return releaseFreshing();
		}
	}

	/**
	 * 初始化 HeadView PaddingTop
	 */
	public void setPaddingTop(int paddingTop) {
		this.setPadding(0, paddingTop, 0, 0);
	}
}
