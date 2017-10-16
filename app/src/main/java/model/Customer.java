package model;

import org.json.JSONException;
import org.json.JSONObject;

import utils.Constants;

/**
 * Created by HighStrit on 23/11/2016.
 */

public class Customer {
    private String customer_name, customer_email, customer_id, customer_phone_no;
    private JSONObject jsonObject;
    private String password, repeated_password;

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public String getCustomer_name() throws JSONException{
        return getJsonObject().getString(Constants.CUSTOMER_NAME);
    }

    public String getCustomer_email() throws JSONException{
        return getJsonObject().getString(Constants.CUSTOMER_EMAIL);
    }

    public String getCustomer_id() throws JSONException{
        return getJsonObject().getString(Constants.CUSTOMER_ID);
    }

    public String getCustomer_phone_no() throws JSONException{
        return getJsonObject().getString(Constants.CUSTOMER_PHONE_NO);
    }

    public boolean validatePassword(String password, String repeated_password){
        if(password.equals(repeated_password)){
            return true;
        }
        return false;
    }

    public boolean checkLength(String password){
        if(password.length() > 4){
            return true;
        }
        return false;
    }
}