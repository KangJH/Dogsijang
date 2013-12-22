package home.webParser.dogsijang;

import home.webParser.dogsijang.DogSijang_HTMLParser.CallbackEvent;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MainActivity extends Activity implements OnItemClickListener, OnClickListener, DogSijang_HTMLParser.Callback {

	ArrayList<DogData> mDogDatas = null;
	Comparator<DogData> mArrayComparator = null;
	ListView		mMainListView = null;
	ListItem_Main	mMainAdapter = null;
	DogDataDB		mDogDB = null;
	Handler mHandler = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mDogDB = new DogDataDB(this);
		mDogDatas = mDogDB.loadDB();
		if(mDogDatas == null) {
			mDogDatas = new ArrayList<DogData>();
		}
		//sort dog array by 'no'
		mArrayComparator = new Comparator<DogData>() {
			private final Collator   collator = Collator.getInstance();
			@Override
			public int compare(DogData lhs, DogData rhs) {
				// TODO Auto-generated method stub
				return collator.compare(Integer.toString(lhs.iNo), Integer.toString(rhs.iNo));
			}
		};
		Collections.sort(mDogDatas, mArrayComparator);
		Collections.reverse(mDogDatas);
		/*for(DogData obj : mDogDatas) {
			Log.d("Test", "old:" + obj.iNo + ", " + obj.strSpecies);
		}*/
		mMainListView = (ListView)findViewById(R.id.listview_main);
		mHandler = new Handler();
		DogSijang_HTMLParser hp = new DogSijang_HTMLParser(this, mHandler, mDogDatas, this);
		hp.open();
		mMainAdapter = new ListItem_Main(this, R.layout.listitem_main, mDogDatas, this);
        mMainListView.setAdapter(mMainAdapter);
        mMainListView.setOnItemClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		switch(parent.getId()) {
		case R.id.listview_main:
			if(mMainAdapter != null && mMainAdapter.getCount() > position) {
				DogData data = mMainAdapter.getItem(position);
				if(data.strUri != null && data.strUri.length() > 0) {
					String mainBoardName = getResources().getString(R.string.dogsijang_main_url);//"http://www.dogsijang.co.kr/board_dog";
					String url = mainBoardName + data.strUri.substring(1);
					data.blReadMark = true;
					mDogDB.update(data);
					mMainAdapter.notifyDataSetChanged();
					openBrowser(this, url);
				}
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.call:
			int position = (Integer) v.getTag();
			if(mMainAdapter != null && mMainAdapter.getCount() > position) {
				DogData data = mMainAdapter.getItem(position);
				if(data.strContactNum != null && data.strContactNum.length() > 0) {
					makePhoneCall(data.strContactNum);
				}
			}
			break;
		default:
			break;
		}
	}
	
	@Override
	public void OnCallback(CallbackEvent event, long param1, long param2,
			Object extraObj) {
		if(event == CallbackEvent.HTML_PARSING_DONE) {
			if(mDogDB != null) {
				boolean blIsNewDataAdded = false;
				int iLatestNo = mDogDB.getLatestDogNo();
				for(DogData data : mDogDatas) {
					if(data.iNo > iLatestNo) {
						mDogDB.add(data);
						blIsNewDataAdded = true;
					}
				}
				if(blIsNewDataAdded) {
					Collections.sort(mDogDatas, mArrayComparator);
					Collections.reverse(mDogDatas);
				}
			}
			if(mMainAdapter != null) {
				mMainAdapter.notifyDataSetChanged();
			}
		}
		
	}
	
	private static final String HTTPS = "https://";
	private static final String HTTP = "http://";

	private void openBrowser(final Context context, String url) {

	     if (!url.startsWith(HTTP) && !url.startsWith(HTTPS)) {
	            url = HTTP + url;
	     }

	     Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
	     //context.startActivity(Intent.createChooser(intent, "Chose browser"));
	     context.startActivity(intent);

	}
	
	private void makePhoneCall(String phoneNumber) {
		String trimedPhoneNumber = phoneNumber.replaceAll("-", "");
		Intent callIntent = new Intent(Intent.ACTION_CALL);
		callIntent.setData(Uri.parse("tel:" + trimedPhoneNumber));
		startActivity(callIntent);
	}

}
