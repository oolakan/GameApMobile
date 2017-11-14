package com.example.mygame.mygame.model;
/**
 * @author JOSEPH
 *
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import com.example.mygame.mygame.utils.Constants;

public class DBController extends SQLiteOpenHelper {
    public DBController(Context applicationcontext) {
        super(applicationcontext, "lottostars.db", null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase database) {
        String customer, customer_card, customer_transactions;
        customer="CREATE TABLE IF NOT EXISTS user(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "users_id TEXT, ticket_id TEXT, name TEXT, email TEXT, phone TEXT, location TEXT, status TEXT, roles_id TEXT)";

        customer_card	=	"CREATE TABLE IF NOT EXISTS customer_card(id INTEGER PRIMARY KEY AUTOINCREMENT,reference TEXT, authorization_code TEXT, last_four_digit TEXT, name_on_card TEXT)";

        customer_transactions	=	"CREATE TABLE IF NOT EXISTS transactions(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                " users_id TEXT, " +
                "game_no_played TEXT, " +
                "game_names_id TEXT, " +
                "game_name TEXT, " +
                "game_types_id TEXT, " +
                "game_type TEXT, " +
                "game_type_options_id TEXT, " +
                "game_type_option TEXT, " +
                "game_quaters_id TEXT, " +
                "game_quater_name TEXT, " +
                "lines TEXT, " +
                "ticket_id TEXT," +
                "unit_stake TEXT, " +
                "total_amount TEXT, " +
                "date_played TEXT, " +
                "time_played TEXT, " +
                "payment_option TEXT, " +
                "serial_no TEXT) ";

        database.execSQL(customer_card);
        database.execSQL(customer);
        database.execSQL(customer_transactions);
    }
    @Override
    public void onUpgrade(SQLiteDatabase database, int version_old, int current_version) {
        String query1, query2;
        query1 = "DROP TABLE IF EXISTS customer";
        database.execSQL(query1);
        query2	=	"DROP TABLE IF EXISTS customer_card";
        database.execSQL(query2);
        onCreate(database);
    }
    /**
     * Create of update User's data
     * @param customer
     * @throws JSONException
     */
    public void createOrUpdateUserRecord(User customer) throws JSONException{
        SQLiteDatabase db=this.getWritableDatabase();
        try{
            String sql="SELECT DISTINCT * FROM "+Constants.USER+" WHERE "+Constants.USERS_ID+ "='"+customer.getUser_id()+"'";
            Cursor cursor=db.rawQuery(sql, null);
            /**
             * Update Customers data if exist
             */
            if(cursor.getCount()>0){
                ContentValues val=new ContentValues();
                val.put(Constants.STATUS,"1");
                val.put(Constants.TICKET_ID, customer.getTicket_id());
                val.put(Constants.USERS_ID, customer.getUser_id());
                val.put(Constants.USER_NAME, customer.getUser_name());
                val.put(Constants.USER_EMAIL, customer.getUser_email());
                val.put(Constants.USER_PHONE_NO, customer.getUser_phone_no());
                val.put(Constants.USER_ADDRESS, customer.getUserLocation());
                val.put(Constants.ROLES_ID, customer.getRolesId());
                db.update(Constants.USER,val,Constants.USERS_ID+" = "+customer.getUser_id(),null);
                db.close();
            }
            /**
             * Create User Info
             */
            else{
                ContentValues val=new ContentValues();
                val.put(Constants.STATUS, "1");
                val.put(Constants.TICKET_ID, customer.getTicket_id());
                val.put(Constants.USERS_ID, customer.getUser_id());
                val.put(Constants.USER_NAME, customer.getUser_name());
                val.put(Constants.USER_EMAIL, customer.getUser_email());
                val.put(Constants.USER_PHONE_NO, customer.getUser_phone_no());
                val.put(Constants.USER_ADDRESS, customer.getUserLocation());
                val.put(Constants.ROLES_ID, customer.getRolesId());
                db.insert(Constants.USER, null, val);
                db.close();
            }
        }
        catch (SQLException ex){
            ex.printStackTrace();
        }
    }

    /**
     * Create of update User's data
     * @param transaction
     * @throws JSONException
     */
    public void createTransaction(Transaction transaction) throws JSONException{
        SQLiteDatabase db=this.getWritableDatabase();
        try {
            ContentValues val = new ContentValues();

            val.put(Constants.USERS_ID, transaction.getUserId());
            val.put(Constants.GAME_NO_PLAYED, transaction.getGame_no_played());
            val.put(Constants.GAME_NAMES_ID, transaction.getGame_names_id());
            val.put(Constants.GAME_TYPES_ID, transaction.getGame_types_id());
            val.put(Constants.GAME_TYPE_OPTIONS_ID, transaction.getGame_type_options_id());
            val.put(Constants.GAME_QUATERS_ID, transaction.getGame_quaters_id());
            val.put(Constants.LINES, transaction.getLines());
            val.put(Constants.TICKET_ID, transaction.getTicketId());
            val.put(Constants.SERIAL_NO, transaction.getSerialNo());
            val.put(Constants.UNIT_STAKE, transaction.getUnitStake());
            val.put(Constants.TOTAL_AMOUNT, transaction.getTotalAmount());
            val.put(Constants.DATE_PLAYED, transaction.getDate_played());
            val.put(Constants.TIME_PLAYED, transaction.getTime_played());
            val.put(Constants.PAYMENT_OPTION, transaction.getPayment_option());
            val.put(Constants.GAME_NAME, transaction.getGameName());
            val.put(Constants.GAME_TYPE, transaction.getGameType());
            val.put(Constants.GAME_TYPE_OPTION, transaction.getGameTypeOption());
            val.put(Constants.GAME_QUATER_NAME, transaction.getGameQuater());

            db.insert(Constants.TRANSACTIONS, null, val);
            db.close();

        }
        catch (SQLException ex){
            ex.printStackTrace();
        }
    }
    /**
     * Get User data
     * @return
     */
    public HashMap<String, String> getUser(){
        SQLiteDatabase db=this.getWritableDatabase();
        String sql="select * from "+Constants.USER;
        HashMap<String, String> user = new HashMap<String, String>();
        Cursor c=db.rawQuery(sql, null);
        if(c.moveToFirst()){
            do{
                user.put(Constants.USERS_ID, c.getString(1));
                user.put(Constants.TICKET_ID, c.getString(2));
                user.put(Constants.USER_NAME,c.getString(3));
                user.put(Constants.USER_EMAIL,c.getString(4));
                user.put(Constants.USER_PHONE_NO, c.getString(5));
                user.put(Constants.USER_ADDRESS, c.getString(6));
                user.put(Constants.STATUS, c.getString(7));
                user.put(Constants.ROLES_ID, c.getString(8));
            }while(c.moveToNext());
        }
        return user;
    }

    /**
     * Get User data
     * @return
     */
    public JSONArray getTransactions() throws JSONException{
        SQLiteDatabase db=this.getWritableDatabase();
        String sql="select * from "+Constants.TRANSACTIONS;
        JSONObject transaction = new JSONObject();
        JSONArray transactions = new JSONArray();
        Cursor c=db.rawQuery(sql, null);
        if(c.moveToFirst()) {
            do{

                transaction.put(Constants.USERS_ID, c.getString(1));
                transaction.put(Constants.GAME_NO_PLAYED, c.getString(2));
                transaction.put(Constants.GAME_NAMES_ID, c.getString(3));
                transaction.put(Constants.GAME_NAME, c.getString(4));
                transaction.put(Constants.GAME_TYPES_ID, c.getString(5));
                transaction.put(Constants.GAME_TYPE, c.getString(6));
                transaction.put(Constants.GAME_TYPE_OPTIONS_ID, c.getString(7));
                transaction.put(Constants.GAME_TYPE_OPTION, c.getString(8));
                transaction.put(Constants.GAME_QUATERS_ID, c.getString(9));
                transaction.put(Constants.GAME_QUATER_NAME, c.getString(10));

                transaction.put(Constants.TICKET_ID, c.getString(12));
                transaction.put(Constants.UNIT_STAKE, c.getString(13));
                transaction.put(Constants.TOTAL_AMOUNT, c.getString(14));
                transaction.put(Constants.DATE_PLAYED, c.getString(15));
                transaction.put(Constants.TIME_PLAYED, c.getString(16));
                transaction.put(Constants.PAYMENT_OPTION, c.getString(17));
                transaction.put(Constants.SERIAL_NO, c.getString(18));

                transactions.put(transaction);
            }while(c.moveToNext());
        }
        return transactions;
    }

    public void deleteTransactions() {
        SQLiteDatabase db=this.getWritableDatabase();
        String sql="delete from "+Constants.TRANSACTIONS;
        db.execSQL(sql);
    }

    public void deleteUser() {
        SQLiteDatabase db=this.getWritableDatabase();
        String sql="delete from "+Constants.USER;
        db.execSQL(sql);
    }


    /**
     * Get Login status
     * @return
     */
    public boolean confirmLoginStatus(){
        boolean stat=false;
        String _stat="";
        String sql="select * from "+Constants.USER;
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor c=db.rawQuery(sql, null);
        if(c.getCount()!=0){
            if(c.moveToFirst()){
                _stat=c.getString(7);
            }
            while(c.moveToNext());

        }
        db.close();
        if(_stat.equals("1")){
            stat=true;
        }
        else{
            stat=false;
        }
        return stat;
    }
}