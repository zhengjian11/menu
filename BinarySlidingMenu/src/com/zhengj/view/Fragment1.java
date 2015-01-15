package com.zhengj.view;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.zj.R;

public class Fragment1 extends Fragment {
    private String mImageUrl;

    private ImageView mImageView;


    private ProgressBar progressBar;



    private List<String> mDatas = new ArrayList<String>();

	private ListView mylist;


    public static Fragment1 newInstance(String imageUrl) {
        final Fragment1 f = new Fragment1();

        final Bundle args = new Bundle();
        args.putString("url", imageUrl);
        f.setArguments(args);

        return f;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
       }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment1, container, false);
    	// 初始化数据
		for (int i = 'A'; i <= 'Z'; i++)
		{
			mDatas.add((char) i + "");
		}
		mylist = (ListView)	v.findViewById(R.id.mylist);
		mylist.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.item, mDatas));
		
		mylist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
			 Toast.makeText(getActivity(), "=======131243243243333333333333========", 2000).show();
				
			}
		});
        return v;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        
        
    }
    
}
