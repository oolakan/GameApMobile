package model;

/**
 * Created by swifta on 11/2/17.
 */

public class Transaction {


    private String game_no_played;
    private String game_names_id;
    private String game_types_id;
    private String game_type_options_id;
    private String game_quaters_id;
    private String amount_paid;
    private String time_played;
    private String payment_option;


    public String getGame_no_played() {
        return game_no_played;
    }

    public void setGame_no_played(String game_no_played) {
        this.game_no_played = game_no_played;
    }

    public String getGame_names_id() {
        return game_names_id;
    }

    public void setGame_names_id(String game_names_id) {
        this.game_names_id = game_names_id;
    }

    public String getGame_types_id() {
        return game_types_id;
    }

    public void setGame_types_id(String game_types_id) {
        this.game_types_id = game_types_id;
    }

    public String getGame_type_options_id() {
        return game_type_options_id;
    }

    public void setGame_type_options_id(String game_type_options_id) {
        this.game_type_options_id = game_type_options_id;
    }

    public String getGame_quaters_id() {
        return game_quaters_id;
    }

    public void setGame_quaters_id(String game_quaters_id) {
        this.game_quaters_id = game_quaters_id;
    }

    public String getAmount_paid() {
        return amount_paid;
    }

    public void setAmount_paid(String amount_paid) {
        this.amount_paid = amount_paid;
    }

    public String getTime_played() {
        return time_played;
    }

    public void setTime_played(String time_played) {
        this.time_played = time_played;
    }

    public String getPayment_option() {
        return payment_option;
    }

    public void setPayment_option(String payment_option) {
        this.payment_option = payment_option;
    }


}
