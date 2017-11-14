package model;

import org.json.JSONException;
import org.json.JSONObject;

import utils.Constants;

/**
 * Created by HighStrit on 23/11/2016.
 */

public class User {
    private String customer_name, customer_email, customer_id, customer_phone_no;
    private JSONObject jsonObject;
    private String password, repeated_password, rolesId, ticket_id;


    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public String getUser_name() throws JSONException{
        return getJsonObject().getString(Constants.USER_NAME);
    }

    public String getUser_email() throws JSONException{
        return getJsonObject().getString(Constants.USER_EMAIL);
    }

    public String getUser_id() throws JSONException{
        return getJsonObject().getString(Constants.ID);
    }

    public String getUserId() throws JSONException{
        return getJsonObject().getString(Constants.USERS_ID);
    }

    public String getUser_phone_no() throws JSONException{
        return getJsonObject().getString(Constants.USER_PHONE_NO);
    }

    public String getUserLocation() throws JSONException{
        return getJsonObject().getString(Constants.USER_ADDRESS);
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

    public String getRolesId() throws JSONException{
        return getJsonObject().getString(Constants.ROLES_ID);
    }

    public void setRolesId(String rolesId) {
        this.rolesId = rolesId;
    }

    public String getTicket_id() throws JSONException{
        return getJsonObject().getString(Constants.TICKET_ID);
    }

    public String getCredit() throws JSONException{
        return getJsonObject().getString(Constants.CREDIT_BALANCE);
    }
}