package com.example.mygame.mygame.custom;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import com.example.mygame.mygame.R;
import com.example.mygame.mygame.model.Transaction;
import com.example.mygame.mygame.utils.Constants;

public class GameTransactionsCustomList extends ArrayAdapter<Transaction> {
	private Activity context;
	private ArrayList<Transaction> transactions;
	private TextView imgV;

	public GameTransactionsCustomList(Activity context, ArrayList<Transaction> transactions) {
		// TODO Auto-generated constructor stub
		super(context, R.layout.transaction_item, transactions);
		this.context=context;
		this.transactions = transactions;
	}
	@SuppressLint({ "InflateParams", "ViewHolder" })
	@Override
	public View getView(int position, View view, ViewGroup parent) {

		LayoutInflater inflater=context.getLayoutInflater();
		View rowView=inflater.inflate(R.layout.transaction_item, null,true);
		TextView gameNoPlayed = rowView.findViewById(R.id.game_no_played);
        TextView gameName = rowView.findViewById(R.id.game_name);
        TextView gameType = rowView.findViewById(R.id.game_type);
        TextView gameTypeOption = rowView.findViewById(R.id.game_type_option);
		TextView serialNo = rowView.findViewById(R.id.serial_no);
		TextView ticketId = rowView.findViewById(R.id.ticket_id);
		TextView unitStake = rowView.findViewById(R.id.unit_stake);
		TextView totalAmount = rowView.findViewById(R.id.total_amount);
		TextView datePlayed = rowView.findViewById(R.id.date_played);
		TextView paymentOption = rowView.findViewById(R.id.payment_option);
		TextView gameResult = rowView.findViewById(R.id.game_result);

		String timePlayed[] = this.transactions.get(position).getTime_played().split(":");
		int hr = Integer.parseInt(timePlayed[0]);
		String mm = timePlayed[1];
        String s;
        String gameTimePlayed = "";
        if ( hr >= 12 ) {
            gameTimePlayed = String.format("%s:%sPM",(hr - 12), mm );
        }
        else {
            gameTimePlayed = String.format( "%s:%sAM", hr, mm );
        }
		try {
            gameNoPlayed.setText(this.transactions.get(position).getGame_no_played());
            gameName.setText(this.transactions.get(position).getGameName());
            gameType.setText(this.transactions.get(position).getGameType());
            gameTypeOption.setText(this.transactions.get(position).getGameTypeOption());
            serialNo.setText(this.transactions.get(position).getSerialNo());
            ticketId.setText(this.transactions.get(position).getTicketId());
            unitStake.setText(this.transactions.get(position).getUnitStake());
            totalAmount.setText(this.transactions.get(position).getTotalAmount());
            datePlayed.setText(String.format("%s, %s", this.transactions.get(position).getDate_played(), gameTimePlayed));
            paymentOption.setText(this.transactions.get(position).getPayment_option());
            gameResult.setText(this.transactions.get(position).getStatus());
            if (TextUtils.equals(this.transactions.get(position).getStatus(), Constants.WON)) {
                gameResult.setTextColor(ResourcesCompat.getColor(context.getResources(), R.color.green, null));
            } else if(TextUtils.equals(this.transactions.get(position).getStatus(), Constants.LOOSE)){
                gameResult.setTextColor(ResourcesCompat.getColor(context.getResources(), R.color.red, null));
            }
            else if(TextUtils.equals(this.transactions.get(position).getStatus(), Constants.PENDING)){
                gameResult.setTextColor(ResourcesCompat.getColor(context.getResources(), R.color.yellow, null));
            }
            else {
                gameResult.setText(Constants.PENDING);
                gameResult.setTextColor(ResourcesCompat.getColor(context.getResources(), R.color.yellow, null));
            }
        }
        catch (Exception ex){}
		return rowView;		
    }
	/**
	 * Get date from timestamp
	 * @param _time
     * @return
     */
	public String getPeriod(long _time) {
        String new_time = "";
        new_time = new java.text.SimpleDateFormat("MMM dd, yyyy").
                format(new java.util.Date(_time * 1000));
        return new_time;
    }
}