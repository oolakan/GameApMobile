package com.example.mygame.mygame.custom;

import android.content.Context;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import com.example.mygame.mygame.model.Game;
import com.example.mygame.mygame.utils.Constants;

import com.example.mygame.mygame.R;

public class SpinnerCustomList extends BaseAdapter {
	Context context;
	ArrayList<Game> items;
	LayoutInflater inflter;

	public SpinnerCustomList(Context applicationContext, ArrayList<Game> items) {
		this.context = applicationContext;
		this.items = items;
		inflter = (LayoutInflater.from(applicationContext));
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int i) {
		return null;
	}

	@Override
	public long getItemId(int i) {
		return 0;
	}

	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		view = inflter.inflate(R.layout.spinner_list, null);
		TextView names = (TextView) view.findViewById(R.id.name);
		TextView id = (TextView) view.findViewById(R.id.id);
		TextView dayName = (TextView) view.findViewById(R.id.dayName);
		TextView gameQuaterName = (TextView) view.findViewById(R.id.gameQuaterName);
		LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.spinner_item_layout);
		if(i==0){
			linearLayout.setBackgroundResource(R.drawable.background_colorprimary);
		}
		else {
			linearLayout.setBackgroundResource(R.drawable.background_white);
			names.setTextColor(ResourcesCompat.getColor(context.getResources(),R.color.black, null));
		}
		if (TextUtils.equals(items.get(i).getGameDay(), Constants.EMPTY)){
			dayName.setVisibility(View.GONE);
			gameQuaterName.setVisibility(View.GONE);
		}
		names.setText(items.get(i).getName());
		id.setText(items.get(i).getId());
		dayName.setText(items.get(i).getGameDay());
		gameQuaterName.setText(items.get(i).getGameQuaterName());
		return view;
	}
}