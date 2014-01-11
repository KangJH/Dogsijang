package home.webParser.dogsijang;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.conn.ConnectTimeoutException;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;

public class DogSijang_HTMLParser extends Object
{
	public enum CallbackEvent {
		HTML_PARSING_NONE,
		HTML_PARSING_DONE,
		HTML_PARSING_ERROR,
		HTML_PARSING_MAX
	}
	public interface Callback {
		void OnCallback(DogSijang_HTMLParser.CallbackEvent event, long param1, long param2, Object extraObj);
	};
	private Context mContext;
	private Handler mHandler;
	private ProgressDialog progressDialog;
	private Source source;
	private Callback mCb;
	private ArrayList<DogData> mNewDogData;
	
	public DogSijang_HTMLParser(Context context, Handler handler, Callback cb)
	{
		mContext = context;
		mHandler = handler;
		mNewDogData = new ArrayList<DogData>();
		mCb = cb;
		/*for(DogData dog : mOriginDogData) {
			Log.d("Test", "Old:" + dog.iNo + ", " + dog.strSpecies);
		}*/
	}
	
	public void open()
	{
		try
		{
			process();
		}catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private void process() throws IOException
	{
		//상태 Progress 띄우기 위해서 사용함!
		new Thread()
		{
			@Override
			public void run()
			{
				URL nURL;
				try
				{
					nURL = new URL(mContext.getResources().getString(R.string.dogsijang_sale_url));
					InputStream html = null;
					if(isConnected()) {
						mHandler.post(new Runnable(){
							@Override
							public void run()
							{
								progressDialog = ProgressDialog.show(mContext, "", mContext.getResources().getString(R.string.progress_content));
							}
						});
						html = nURL.openStream();
						parsing(html);
						if(mCb != null) {
							mCb.OnCallback(CallbackEvent.HTML_PARSING_DONE, 0, 0, mNewDogData);
						}
						mHandler.post(new Runnable()
						{
							public void run()
							{
								progressDialog.dismiss();
							}
						});
					} else {
						showNetworkErrorDialog();
					}
					
					
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					mHandler.post(new Runnable()
					{
						public void run()
						{
							progressDialog.dismiss();
							showNetworkErrorDialog();
						}
					});
					e.printStackTrace();
				}
				
			}
			
		}.start();
	}
	
	private boolean isConnected() {
		boolean ret = false;
		ConnectivityManager manager = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		         
		if (mobile.isConnected() || wifi.isConnected()){
			ret = true;
		}
		return ret;
	}
	
	private void showNetworkErrorDialog(){
		mHandler.post(new Runnable()
		{
			public void run()
			{
				AlertDialog.Builder alt_bld = new AlertDialog.Builder(mContext);
			    alt_bld.setMessage(mContext.getResources().getString(R.string.reconnect_network)).setCancelable(
			        false).setPositiveButton(mContext.getResources().getString(R.string.dialog_yes),
			        new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int id) {
			            // Action for 'Yes' Button
			        	open();
			        }
			        }).setNegativeButton(mContext.getResources().getString(R.string.dialog_no),
			        new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int id) {
			            // Action for 'NO' Button
			            dialog.cancel();
			            if(mCb != null) {
							mCb.OnCallback(CallbackEvent.HTML_PARSING_ERROR, 0, 0, mNewDogData);
						}
			        }
			        });
			    AlertDialog alert = alt_bld.create();
			    // Title for AlertDialog
			    alert.setTitle(mContext.getResources().getString(R.string.disconnected_network_title));
			    alert.show();
			}
		});
	    
	}
	
	private void parsing(InputStream html) throws UnsupportedEncodingException, IOException {
		//가져오는 HTML의 인코딩형식
		
		source = new Source(new InputStreamReader(html, "EUC-KR"));//테이블가져오기
		Element form = null;
		List<Element> formList = source.getAllElements(HTMLElementName.FORM);
		int numOfForm = formList.size();
		for(int i = 0; i < numOfForm; i++) {
			form = (Element) formList.get(i);
			if(form.getName().equals("frm")) {
				break;
			}
		}
		//테이블 안의 TR 개수
		List<Element> trList = form.getAllElements(HTMLElementName.TR);
		int tr_count = trList.size();
		
		Element tr = null;
		
		
		for(int i=2; i<tr_count; i++)
		{
			tr = (Element) trList.get(i);
			List<Element> tdList = tr.getAllElements(HTMLElementName.TD);
			if(tdList.size() > 8) {
				DogData dogData = new DogData();
				dogData.iNo = Integer.parseInt(tdList.get(0).getContent().toString().trim()); 
				Element species = tdList.get(1).getAllElements(HTMLElementName.A).get(0);
				dogData.strSpecies = species.getContent().toString();dogData.strCharacter = tdList.get(2).getAllElements(HTMLElementName.FONT).get(0).getAllElements(HTMLElementName.A).get(0).getContent().toString();
				dogData.strPrice = tdList.get(6).getContent().toString();
				dogData.strUri = species.getAttributes().getValue("href");
				dogData.strContactNum = tdList.get(8).getAllElements(HTMLElementName.FONT).get(0).getContent().toString().trim();

				mNewDogData.add(dogData);
			}
		}
		
		
	}
}
