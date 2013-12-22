package home.webParser.dogsijang;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class ListItem_Main extends ArrayAdapter<DogData> {

	OnClickListener mOnClickListener = null;
	public ListItem_Main(Context context, int resource,
			ArrayList<DogData> objects, OnClickListener onClickListener) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
		mResourceID = resource;
		mOnClickListener = onClickListener;
	}
	private int mResourceID = 0;
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
			convertView = inflater.inflate(mResourceID, null);
			holder = new ViewHolder();
			holder.tvSpecies = (TextView) convertView.findViewById(R.id.species);
			holder.tvCharacter = (TextView) convertView.findViewById(R.id.character);
			holder.tvPrice = (TextView) convertView.findViewById(R.id.price);
			holder.ibCall = (ImageButton) convertView.findViewById(R.id.call);
			holder.ibCall.setOnClickListener(mOnClickListener);
			convertView.setTag(holder);
		} 
		holder = (ViewHolder) convertView.getTag();
		DogData currItem = getItem(position);
		holder.ibCall.setTag(position);
		holder.tvSpecies.setText(currItem.strSpecies);
		holder.tvCharacter.setText(currItem.strCharacter);
		holder.tvPrice.setText(currItem.strPrice);

		int textColor = getContext().getResources().getColor(R.color.blackText);
		if(currItem.blReadMark) {
			textColor = getContext().getResources().getColor(R.color.grayText);
		} 
		holder.tvSpecies.setTextColor(textColor); 
		holder.tvCharacter.setTextColor(textColor); 
		holder.tvPrice.setTextColor(textColor); 


		return convertView;
	}
	private class ViewHolder {
		TextView tvSpecies;
		TextView tvCharacter;
		TextView tvPrice;
		ImageButton ibCall;
	}

}
