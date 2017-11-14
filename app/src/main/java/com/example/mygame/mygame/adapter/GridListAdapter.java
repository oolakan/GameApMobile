package com.example.mygame.mygame.adapter;

import android.content.Context;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Toast;
import com.example.mygame.mygame.R;
import java.util.ArrayList;

/**
 * Created by sonu on 08/02/17.
 */

public class GridListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> arrayList;
    private LayoutInflater inflater;
    private int selectedPosition = -1;
    customButtonListener customListner;
    public GridListAdapter(Context context, ArrayList<String> arrayList) {
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
            view = inflater.inflate(R.layout.grid_custom_row_layout, viewGroup, false);
            viewHolder.gameNoButton = (Button) view.findViewById(R.id.game_nos);
            viewHolder.gameNoButton.setText(arrayList.get(i));
            view.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) view.getTag();
        //Set the position tag to both radio button and label
        viewHolder.gameNoButton.setTag(i);
        viewHolder.gameNoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.gameNoButton.setBackgroundColor(ResourcesCompat.getColor(context.getResources(), R.color.lightPrimaryColor, null));
                customListner.onButtonClickListner(i, arrayList.get(i),  viewHolder);
            }
        });
        return view;
    }
    //On selecting any view set the current position to selectedPositon and notify adapter
    private void itemClicked(View v) {
        selectedPosition = (Integer) v.getTag();
//        notifyDataSetChanged();
    }

    public class ViewHolder {
        private Button gameNoButton;
    }

    //Return the selectedPosition item
    public String getSelectedItem() {
        if (selectedPosition != -1) {
            Toast.makeText(context, "Selected Item : " + arrayList.get(selectedPosition), Toast.LENGTH_SHORT).show();
            return arrayList.get(selectedPosition);
        }
        return "";
    }

    public interface customButtonListener {
        public void onButtonClickListner(int position, String value, ViewHolder holder);
    }
    public void setCustomButtonListner(customButtonListener listener) {
        this.customListner = listener;
    }
}
