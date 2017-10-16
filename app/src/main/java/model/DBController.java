package model;
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

import org.json.JSONException;

import java.util.HashMap;

import utils.Constants;
import model.Customer;

public class DBController extends SQLiteOpenHelper{
    public DBController(Context applicationcontext) {
        super(applicationcontext, "jasiride.db", null, 2);
    }
    @Override
    public void onCreate(SQLiteDatabase database) {
        String customer, customer_card, promo;
        customer="CREATE TABLE IF NOT EXISTS customer(id INTEGER PRIMARY KEY AUTOINCREMENT, customers_id TEXT, customer_name TEXT, customer_email TEXT, customer_phone_no TEXT, status TEXT)";
        customer_card	=	"CREATE TABLE IF NOT EXISTS customer_card(id INTEGER PRIMARY KEY AUTOINCREMENT,reference TEXT, authorization_code TEXT, last_four_digit TEXT, name_on_card TEXT)";
        promo	=	"CREATE TABLE IF NOT EXISTS promo(id INTEGER PRIMARY KEY AUTOINCREMENT,promo_code TEXT)";
        database.execSQL(customer_card);
        database.execSQL(customer);
        database.execSQL(promo);
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
     * Create of update Customer's data
     * @param customer
     * @throws JSONException
     */
    public void createOrUpdateCustomerRecord(Customer customer) throws JSONException{
        SQLiteDatabase db=this.getWritableDatabase();
        try{
            String sql="SELECT DISTINCT * FROM "+Constants.CUSTOMER+" WHERE "+Constants.CUSTOMERS_ID+ "='"+customer.getCustomer_id()+"'";
            Cursor cursor=db.rawQuery(sql, null);
            /**
             * Update Customers data if exist
             */
            if(cursor.getCount()>0){
                ContentValues val=new ContentValues();
                val.put(Constants.STATUS,"1");
                val.put(Constants.CUSTOMERS_ID, customer.getCustomer_id());
                val.put(Constants.CUSTOMER_NAME, customer.getCustomer_name());
                val.put(Constants.CUSTOMER_EMAIL, customer.getCustomer_email());
                val.put(Constants.CUSTOMER_PHONE_NO, customer.getCustomer_phone_no());
                db.update(Constants.CUSTOMER,val,Constants.CUSTOMERS_ID+" = "+customer.getCustomer_id(),null);
                db.close();
            }
            /**
             * Create Customer Info
             */
            else{
                ContentValues val=new ContentValues();
                val.put(Constants.STATUS, "1");
                val.put(Constants.CUSTOMERS_ID, customer.getCustomer_id());
                val.put(Constants.CUSTOMER_NAME, customer.getCustomer_name());
                val.put(Constants.CUSTOMER_EMAIL, customer.getCustomer_email());
                val.put(Constants.CUSTOMER_PHONE_NO, customer.getCustomer_phone_no());
                db.insert(Constants.CUSTOMER, null, val);
                db.close();
            }
        }
        catch (SQLException ex){
            ex.printStackTrace();
        }
    }
    /**
     * dd payment card r update already added card
     * @param reference
     * @param lastFourDigit
     * @param nameOnCard
     */
    public void createOrUpdateCardRecord(String reference, String authorization_code, String lastFourDigit,String nameOnCard){
        SQLiteDatabase db=this.getWritableDatabase();
        try{
            String sql="SELECT * FROM " + Constants.CUSTOMER_CARD + " WHERE " + Constants.LAST_FOUR_DIGIT+ " = '" + lastFourDigit + "'";
            Cursor cursor=db.rawQuery(sql, null);
            /**
             * Update Customers data if exist
             */
            if(cursor.getCount()>0){
                ContentValues val=new ContentValues();
                val.put(Constants.REFERENCE,reference);
                val.put(Constants.AUTHORIZATION_CODE, authorization_code);
                val.put(Constants.LAST_FOUR_DIGIT, lastFourDigit);
                val.put(Constants.NAME_ON_CARD, nameOnCard);
                db.update(Constants.CUSTOMER_CARD,val, Constants.LAST_FOUR_DIGIT + " = " +lastFourDigit,null);
                db.close();
            }
            /**
             * Create Customer Card
             * */
            else{
                ContentValues val=new ContentValues();
                val.put(Constants.REFERENCE,reference);
                val.put(Constants.AUTHORIZATION_CODE, authorization_code);
                val.put(Constants.LAST_FOUR_DIGIT, lastFourDigit);
                val.put(Constants.NAME_ON_CARD, nameOnCard);
                db.insert(Constants.CUSTOMER_CARD, null, val);
                db.close();
            }
        }
        catch (SQLException ex){
            ex.printStackTrace();
        }
    }

    /**
     * Delete card
     * @return
     */
    public void deleteCard()
    {
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("DELETE FROM customer_card");
        db.close();
    }
    public HashMap<String, String> getCardDetails(){
        SQLiteDatabase db=this.getWritableDatabase();
        String sql="select * from customer_card";
        HashMap<String, String> customer_card = new HashMap<String, String>();
        Cursor c=db.rawQuery(sql, null);
        if(c.moveToFirst()){
            do{
                customer_card.put(Constants.REFERENCE, c.getString(1));
                customer_card.put(Constants.AUTHORIZATION_CODE,c.getString(2));
                customer_card.put(Constants.LAST_FOUR_DIGIT,c.getString(3));
                customer_card.put(Constants.NAME_ON_CARD, c.getString(4));
            }while(c.moveToNext());
        }
        return customer_card;
    }
    /**
     * Get Customer data
     * @return
     */
    public HashMap<String, String> getCustomer(){
        SQLiteDatabase db=this.getWritableDatabase();
        String sql="select * from "+Constants.CUSTOMER;
        HashMap<String, String> customer = new HashMap<String, String>();
        Cursor c=db.rawQuery(sql, null);
        if(c.moveToFirst()){
            do{
                customer.put(Constants.CUSTOMERS_ID, c.getString(1));
                customer.put(Constants.CUSTOMER_NAME,c.getString(2));
                customer.put(Constants.CUSTOMER_EMAIL,c.getString(3));
                customer.put(Constants.CUSTOMER_PHONE_NO, c.getString(4));
            }while(c.moveToNext());
        }
        return customer;
    }
    /**
     * Get Login status
     * @return
     */
    public boolean confirmLoginStatus(){
        boolean stat=false;
        String _stat="";
        String sql="select * from "+Constants.CUSTOMER;
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor c=db.rawQuery(sql, null);
        if(c.getCount()!=0){
            if(c.moveToFirst()){
                _stat=c.getString(5);
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
    //delete customer data from storage
    public void logout(String customer_id){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("DELETE from "+ Constants.CUSTOMER);
        db.close();
    }
    //create or update promo code
    public void createOrUpdatePromoCode(String promoCode){
        int id = 1;
        SQLiteDatabase db=this.getWritableDatabase();
        try{
            String sql="SELECT DISTINCT * FROM promo";
            Cursor cursor=db.rawQuery(sql, null);
            if(cursor.getCount()>0){
                ContentValues val=new ContentValues();
                val.put("promo_code",promoCode);
                db.update("promo",val,"id="+id,null);
                db.close();
            }
            else{
                ContentValues val=new ContentValues();
                val.put("promo_code", promoCode);
                db.insert("promo", null, val);
                db.close();
            }
        }
        catch (SQLException ex){
            ex.printStackTrace();
        }
    }
    //get promo code
    public String getPromoCode(){
        SQLiteDatabase db=this.getWritableDatabase();
        String code ="";
        String sql="select * from promo";
        Cursor c=db.rawQuery(sql, null);
        if(c.moveToFirst()){
            do{
                code = c.getString(1);
            }while(c.moveToNext());
        }
        return code;
    }
}