package com.example.mygame.mygame.custom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.mygame.mygame.R;

import org.json.JSONException;

import java.util.ArrayList;

import com.example.mygame.mygame.model.User;
import com.example.mygame.mygame.utils.Constants;

public class UsersCustomList extends BaseAdapter {
	private Context context;
	private ArrayList<User> arrayList;
	private LayoutInflater inflater;
	private int selectedPosition = -1;
	customButtonListener customListner;
	public UsersCustomList(Context context, ArrayList<User> arrayList) {
		this.context = context;
		this.arrayList = arrayList;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return arrayList.size();
	}

	@Override
	public Object getItem(int i) {
		return arrayList.get(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(final int i, View view, ViewGroup viewGroup) {
		final ViewHolder viewHolder;
		if (view == null) {
			viewHolder = new ViewHolder();
			view = inflater.inflate(R.layout.users_item, viewGroup, false);
			viewHolder.blockBtn = (Button) view.findViewById(R.id.blocked);
			viewHolder.unBlockBtn = view.findViewById(R.id.unblocked);
			viewHolder.creditBtn = view.findViewById(R.id.addCredit);

			viewHolder.name = view.findViewById(R.id.name);
			viewHolder.ticketId = view.findViewById(R.id.ticket_id);
			viewHolder.creditBalance = view.findViewById(R.id.credit_balance);

			try {
				viewHolder.name.setText(arrayList.get(i).getUser_name());
				viewHolder.ticketId.setText(arrayList.get(i).getTicket_id());
				viewHolder.creditBalance.setText(String.format("N%s.00",arrayList.get(i).getCredit()));
			} catch (JSONException e) {
				e.printStackTrace();
			}


			view.setTag(viewHolder);
		} else
			viewHolder = (ViewHolder) view.getTag();
		//Set the position tag to both radio button and label
		viewHolder.blockBtn.setTag(i);
		viewHolder.unBlockBtn.setTag(i);
		viewHolder.creditBtn.setTag(i);
		viewHolder.blockBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					customListner.onButtonClickListner(i, arrayList.get(i).getUserId(), Constants.BLOCKED, viewHolder);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
		viewHolder.unBlockBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					customListner.onButtonClickListner(i, arrayList.get(i).getUserId(), Constants.APPROVED, viewHolder);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
		viewHolder.creditBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					customListner.onButtonClickListner(i, arrayList.get(i).getUserId(), Constants.CREDIT_BALANCE,  viewHolder);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
		return view;
	}
	//On selecting any view set the current position to selectedPositon and notify adapter
	private void itemClicked(View v) {
		selectedPosition = (Integer) v.getTag();
		notifyDataSetChanged();
	}

	public class ViewHolder {
		private Button blockBtn;
		private Button unBlockBtn;
		private Button creditBtn;

		private TextView name;
		private TextView ticketId;
		private TextView creditBalance;
	}

	public interface customButtonListener {
		public void onButtonClickListner(int position, String userId, String title, ViewHolder holder);
	}
	public void setCustomButtonListner(customButtonListener listener) {
		this.customListner = listener;
	}
}