package home.webParser.dogsijang;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

public class DogSijang_HTMLParser extends Object
{
	public enum CallbackEvent {
		HTML_PARSING_NONE,
		HTML_PARSING_DONE,
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
		//���� Progress ���� ���ؼ� �����!
		new Thread()
		{
			@Override
			public void run()
			{
				URL nURL;
				try
				{
					nURL = new URL(mContext.getResources().getString(R.string.dogsijang_sale_url));
					mHandler.post(new Runnable(){
						@Override
						public void run()
						{
							progressDialog = ProgressDialog.show(mContext, "", mContext.getResources().getString(R.string.progress_content));
						}
					});
					
					
					InputStream html = nURL.openStream();
					//�������� HTML�� ���ڵ�����
					source = new Source(new InputStreamReader(html, "EUC-KR"));
					
					//���̺�������
					Element form = null;
					List<Element> formList = source.getAllElements(HTMLElementName.FORM);
					int numOfForm = formList.size();
					for(int i = 0; i < numOfForm; i++) {
						form = (Element) formList.get(i);
						if(form.getName().equals("frm")) {
							break;
						}
					}
					//���̺� ���� TR ����
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
				}catch (MalformedURLException e)
				{
					e.printStackTrace();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				
			}
			
		}.start();
	}
}
