package com.example.mygame.mygame.custom;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import model.Game;
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
		if(i==0){
			names.setBackgroundColor(ResourcesCompat.getColor(context.getResources(),R.color.colorPrimary, null));
		}
		else {
			names.setBackgroundColor(ResourcesCompat.getColor(context.getResources(),R.color.white, null));
			names.setTextColor(ResourcesCompat.getColor(context.getResources(),R.color.primaryDark, null));
		}
		names.setText(items.get(i).getName());
		id.setText(items.get(i).getId());
		return view;
	}
}