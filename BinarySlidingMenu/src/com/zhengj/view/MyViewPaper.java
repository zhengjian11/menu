package com.zhengj.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class MyViewPaper  extends ViewPager{
	private BinarySlidingMenu menu;
	public MyViewPaper(Context context) {
		super(context);
	}
	public MyViewPaper(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}


@Override
public void setCurrentItem(int item, boolean smoothScroll) {
	// TODO Auto-generated method stub
	super.setCurrentItem(item, smoothScroll);
}


@Override
public boolean onInterceptTouchEvent(MotionEvent arg0) {
	// TODO Auto-generated method stub
	Log.e("mypaper=========onInterceptTouchEvent", "");
	return super.onInterceptTouchEvent(arg0);
}
    
@Override
public boolean onTouchEvent(MotionEvent ev) {
	Log.e("myPaperfffffffffffffffff", "");
	switch (ev.getAction()) {

	case MotionEvent.ACTION_DOWN:
		
		return true;
	default:
		break;
	}
	
    return super.onTouchEvent(ev);
}

public void setMenu(BinarySlidingMenu menu){
	this.menu = menu;
}
}
