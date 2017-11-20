package com.example.mygame.mygame.utils;

import java.util.UUID;

public final class Constants {

    public static final String EMPTY = "";
    public static final String CODE = "code";
    public static final String OTP = "otp";
    public static final String PHONE = "phone";

    //endpoints
    public static final String SERVICE_URL = "http://www.rashollinvestment.com/";
    public static final String BASE_URL = Constants.SERVICE_URL + "api/v1";
    public static final String SEND_OTP_FOR_REG_URL = Constants.BASE_URL + "/users/otp";
    public static final String SEND_OTP_FOR_PASSWORD_RESET_URL = Constants.BASE_URL + "/users/otp/password_reset";
    public static final String USER_TRANSACTION_HISTORY_URL = Constants.BASE_URL + "/users/transactions";
    public static final String TRANSACTION_INITIALIZATION_URL = Constants.BASE_URL + "/customer/transaction/initialize";
    public static final String CANCEL_REQUEST_URL =   Constants.BASE_URL + "/ride_request/cancel/";
    public static final String SIGN_IN_URL = Constants.BASE_URL + "/users/login";
    public static final String REGISTER_URL = Constants.BASE_URL + "/users/register/";
    public static final String PASSWORD_RESET_URL = Constants.BASE_URL + "/users/reset_password";
    public static final String GAMES_INFORMATION_URL = Constants.BASE_URL + "/game";
    public static final String GAME_CHECK_URL = Constants.BASE_URL + "/game/gameAvailability";
    public static final String GAME_TRANSACTION_URL = Constants.BASE_URL + "/game/save/transactions";
    public static final String SALES_REPORT_URL = Constants.BASE_URL + "/game/transactions/";
    public static final String WINNING_GAMES_URL = Constants.BASE_URL + "/winnings/view/";
    public static final String CREDIT_BALANCE_URL = Constants.BASE_URL + "/credit/";
    public static final String CREDIT_BALANCE_UPDATE_URL = Constants.BASE_URL + "/credit/balance/";
    public static final String AACOUNT_BALANCE_UPDATE_URL = Constants.BASE_URL + "/credit/update/";
    public static final String VALIDATE_GAME_URL = Constants.BASE_URL + "/game/validate_game/";
    public static final String AGENTS_URL = Constants.BASE_URL + "/users/agent/";
    public static final String UPDATE_AGENT_URL = Constants.BASE_URL + "/users/agent/";
    public static final String HISTORY = "History";
    //User

    public static final String USER = "user";
    public static final String USER_ID = "id";
    public static final String USER_NAME = "name";
    public static final String USER_EMAIL = "email";
    public static final String USER_PHONE_NO = "phone";

    public static final String USER_ADDRESS = "location";
    public static final String USER_JSON_RESPONSE = "UserData";

    public static final String USERNAME = "username";
    public static final String USER_PREFS = "PREF.USERNAME";
    public static final String GAME_SERIAL_NO = "PREF.SERIAL_NO";
    public static final String USERS_ID = "users_id";
    public static final String USER_PASSWORD = "password";
    public static final String ACTIVITY_TYPE = "activivty_type";
    public static final String REGISTRATION_ACTIVITY_TYPE = "REGISTRATION";
    public static final String PASSWORD_RESET_ACTIVITY_TYPE = "PASSWORD_RESET";
    public static final String STATUS = "status";
    public static final String NEW_PASSWORD = "new_password";
    public static final String SPACE = " ";
    public static final String PASSWORD = "password";
    public static final String ID = "id";

    public static final String GAME_NAMES_JSON_RESPONSE = "GameNames";
    public static final String GAME_TYPES_JSON_RESPONSE = "GameTypes";
    public static final String GAME_TYPE_OPTIONS_JSON_RESPONSE = "GameTypeOptions";
    public static final String GAME_QUATERS_JSON_RESPONSE = "GameQuaters";
    public static final String NAME = "name";

    public static final String MESSAGE = "message";
    public static final String GAME_NAMES_ID = "game_names_id";
    public static final String GAME_QUATERS_ID = "game_quaters_id";
    public static final String GAME_NAME = "game_name";
    public static final String GAME_TYPE = "game_type";
    public static final String GAME_TYPE_OPTION = "game_type_option";
    public static final String GAME_NO_PLAYED = "game_no_played";

    public static final String GAME_TYPES_ID = "game_types_id";
    public static final String GAME_TYPE_OPTIONS_ID = "game_type_options_id";
    public static final String AMOUNT_PAID = "amount_paid";
    public static final String TIME_PLAYED = "time_played";
    public static final String PAYMENT_OPTION = "payment_option";
    public static final String TRANSACTIONS = "transactions";
    public static final String GAME_QUATERS = "quater";
    public static final String START_TIME = "start_time";
    public static final String STOP_TIME = "stop_time";
    public static final String DRAW_TIME = "draw_time";
    public static final String TOTAL_AMOUNT_STAKED = "total_amount_staked";
    public static final String LINES = "lines";

    public static final String TICKET_ID = "ticket_id";
    public static final String UNIT_STAKE = "unit_stake";
    public static final String APPROVAL_STATUS = "approval_status";
    public static final String PENDING = "PENDING";
    public static final String ACTIVE = "ACTIVE";
    public static final String BLOCKED = "BLOCKED";
    public static final String ROLES_ID = "roles_id";
    public static final CharSequence AGENTS_ROLE_ID = "3";
    public static final CharSequence MERCHANTS_ROLE_ID = "2";
    public static final CharSequence ADMINS_ROLE_ID = "1";
    public static final String SERIAL_NO = "serial_no";
    public static final String TOTAL_AMOUNT = "total_amount";

    public static final String CREDIT_BALANCE = "credit_balance";

    public static final String GAME_TRANSACTIONS = "Transactions";
    public static final String DAY = "day";
    public static final String DATE_PLAYED = "date_played";
    public static final String WINNING_AMOUNT = "winning_amount";

    public static final String GAME_QUATER_NAME = "game_quater_name";
    public static final String CREDIT_JSON_RESPONSE = "Credit";
    public static final String AMOUNT = "amount";
    public static final String WON = "WON";
    public static final String LOOSE = "LOOSE";
    public static final String AGENTS_JSON_RESPONSE = "Agents";

    public static final String APPROVED = "APPROVED";

    public static final String SERIAL_UUID = "00001101-0000-1000-8000-00805f9b34fb";
}