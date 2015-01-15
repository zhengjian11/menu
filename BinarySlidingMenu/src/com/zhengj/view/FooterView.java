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
public class FooterView extends LinearLayout {

	private ProgressBar	progressBar;
	private ImageView	arrows;
	private TextView	tvRefresh;
	private boolean		isArrowsUp	= true;

	public FooterView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FooterView(Context context) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.pull_footer, this);

		init(context);
	}

	private void init(Context context) {
		progressBar = (ProgressBar) findViewById(R.id.pull_to_load_progress);
		arrows = (ImageView) findViewById(R.id.pull_to_load_image);
		tvRefresh = (TextView) findViewById(R.id.pull_to_load_text);
	}

	/**
	 * 上拉加载更多
	 */
	public int setStartLoad() {
		progressBar.setVisibility(View.GONE);
		arrows.setVisibility(View.VISIBLE);
		tvRefresh.setText("上拉加载更多");

		if (!isArrowsUp) {
			RotateAnimation mReverseFlipAnimation = new RotateAnimation(180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
					RotateAnimation.RELATIVE_TO_SELF, 0.5f);
			mReverseFlipAnimation.setInterpolator(new LinearInterpolator());
			mReverseFlipAnimation.setDuration(250);
			mReverseFlipAnimation.setFillAfter(true);

			arrows.clearAnimation();
			arrows.startAnimation(mReverseFlipAnimation);
		}

		isArrowsUp = true;
		return PullScrollView.PULL_UP_STATE;
	}

	/**
	 * 松开手加载更多
	 */
	public int releaseLoad() {
		progressBar.setVisibility(View.GONE);
		arrows.setVisibility(View.VISIBLE);
		tvRefresh.setText("松开手加载更多");

		if (isArrowsUp) {
			RotateAnimation animationUp = new RotateAnimation(0, 180, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
					RotateAnimation.RELATIVE_TO_SELF, 0.5f);
			animationUp.setInterpolator(new LinearInterpolator());
			animationUp.setDuration(250);
			animationUp.setFillAfter(true);

			arrows.clearAnimation();
			arrows.setAnimation(animationUp);
		}

		isArrowsUp = false;
		return PullScrollView.RELEASE_TO_LOADING;
	}

	/**
	 * 正在加载更多
	 */
	public int setLoading() {
		arrows.clearAnimation();
		progressBar.setVisibility(View.VISIBLE);
		arrows.setVisibility(View.GONE);
		tvRefresh.setText("正在加载更多");
		return PullScrollView.LOADING;
	}

	/**
	 * 设置 View 高度
	 * 
	 * @param presetHeight
	 *            原始高度
	 * @param currentHeight
	 *            当前高度
	 */
	public int setPadding(int presetHeight, int paddingHeight) {
		this.setPadding(0, 0, 0, paddingHeight);

		// 初始化箭头状态向上
		if (paddingHeight <= presetHeight) {
			return setStartLoad();
		} else { // 改变按钮状态向下
			return releaseLoad();
		}
	}

	/**
	 * 初始化 FootView PaddingButtom
	 */
	public void setPaddingButtom() {
		this.setPadding(0, 0, 0, 0);
		arrows.clearAnimation();
	}

	/**
	 * 显示加载更多按钮
	 */
	public void show() {
		this.setVisibility(View.VISIBLE);
	}

	/**
	 * 隐藏加载更多按钮
	 */
	public void hide() {
		this.setVisibility(View.GONE);
	}

}
