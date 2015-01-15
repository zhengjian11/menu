package activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.zhengj.view.BinarySlidingMenu;
import com.zhengj.view.BinarySlidingMenu.OnMenuOpenListener;
import com.zhengj.view.Fragment1;
import com.zhengj.view.Fragment2;
import com.zhengj.view.MyViewPaper;
import com.zj.R;

public class MainActivity extends FragmentActivity {
	private BinarySlidingMenu mMenu;
	private List<String> mDatas = new ArrayList<String>();
	private List<Fragment> fragments = new ArrayList<Fragment>();
	private MyViewPaper pager;
	private Button button1;
	private Button button2;
	private FragmentTabHost mTabHost;
	private RadioGroup m_radioGroup;
	String tabs[] = { "Tab1", "Tab2" };
	Class cls[] = { Fragment1.class, Fragment2.class };
	private LayoutInflater inflater;
	private View view1;
	private View left_menu;
	private View main;
	private View right_menu;
	private Button right_btn;
	private Button left_btn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		inflater = LayoutInflater.from(this);
		left_menu = inflater.inflate(R.layout.layout_menu, null);
		main = inflater.inflate(R.layout.main, null);
		right_menu = inflater.inflate(R.layout.layout_menu2, null);

		mMenu = (BinarySlidingMenu) findViewById(R.id.mRoot);
		right_btn = (Button) main.findViewById(R.id.right_btn);
		left_btn = (Button) main.findViewById(R.id.left_btn);
		mMenu.addView(left_menu);
		mMenu.addView(main);
		mMenu.addView(right_menu);
		right_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				mMenu.scrollRightMenu();
			}
		});

		left_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				mMenu.scrollLeftMenu();
			}
		});

		mMenu.setOnMenuOpenListener(new OnMenuOpenListener() {
			@Override
			public void onMenuOpen(boolean isOpen, int flag) {
				if (isOpen) {
					// Toast.makeText(getApplicationContext(),
					// flag == 0 ? "LeftMenu Open" : "RightMenu Open",
					// Toast.LENGTH_SHORT).show();
				} else {
					// Toast.makeText(getApplicationContext(),
					// flag == 0 ? "LeftMenu Close" : "RightMenu Close",
					// Toast.LENGTH_SHORT).show();
				}

			}
		});
		// pager = (MyViewPaper) findViewById(R.id.pager);
		// pager.setMenu(mMenu);
		// pager.setOnPageChangeListener(new OnPageChangeListener() {
		//
		// @Override
		// public void onPageSelected(int arg0) {
		// mMenu.setIsScroll(false);
		// }
		//
		// @Override
		// public void onPageScrolled(int arg0, float arg1, int arg2) {
		//
		// }
		//
		// @Override
		// public void onPageScrollStateChanged(int arg0) {
		//
		// }
		// });
		// pager.requestDisallowInterceptTouchEvent(false);
		// 消息
		// Fragment1 placardFragment = new Fragment1();
		// fragments.add(placardFragment);
		//
		// // 查询界面
		// Fragment2 findFragement = new Fragment2();
		// fragments.add(findFragement);
		init();
		// pager.setAdapter(new FragmentAdapter(getSupportFragmentManager()));

		// // 初始化数据
		// for (int i = 'A'; i <= 'Z'; i++)
		// {
		// mDatas.add((char) i + "");
		// }
		// // 设置适配器
		// setListAdapter(new ArrayAdapter<String>(this, R.layout.item,
		// mDatas));
		// button1 = (Button)findViewById(R.id.button1);
		// button1.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View arg0) {
		// pager.setCurrentItem(0,false);
		//
		// }
		// });
		//
		// button2 = (Button)findViewById(R.id.button2);
		// button2.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View arg0) {
		// pager.setCurrentItem(1,false);
		// }
		// });
	}

	private void init() {
		mTabHost = (FragmentTabHost) this.findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
		mTabHost.getTabWidget().setVisibility(View.GONE);
		for (int i = 0; i < tabs.length; i++) {
			// View tabView =
			// this.getLayoutInflater().inflate(R.layout.tab_indicator, null);
			mTabHost.addTab(mTabHost.newTabSpec(tabs[i]).setIndicator(tabs[i]),
					cls[i], null);
		}
		m_radioGroup = (RadioGroup) findViewById(R.id.main_radiogroup);
		m_radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.RadioButton0:

					mTabHost.setCurrentTabByTag(tabs[0]);
					break;
				case R.id.RadioButton1:
					mTabHost.setCurrentTabByTag(tabs[1]);
					break;
				}
			}
		});

		((RadioButton) m_radioGroup.getChildAt(0)).toggle();
	}

	/**
	 * 页面适配器
	 */
	public class FragmentAdapter extends FragmentPagerAdapter {

		public FragmentAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int arg0) {
			return fragments.get(arg0);
		}

		@Override
		public int getCount() {
			return fragments.size();
		}

	}
}
