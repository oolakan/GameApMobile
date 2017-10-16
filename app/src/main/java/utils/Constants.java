package utils;
public final class Constants {

    public static final String EMPTY = "";
    public static final String CODE = "code";
    public static final String OTP = "otp";
    public static final String OTP_PHONE_NO = "otp_phone_no";
    public static final String RIDE_REQUESTS_ID = "ride_requests_id";
    public static final String CENTER_LAT = "center_lat";
    public static final String CENTER_LNG = "center_lng";
    public static final String BUSINESS = "business";
    public static final String REFERENCE = "reference";
    public static final String REUSABLE = "reusable";
    public static final String AUTH_TOKEN = "auth_token";
    public static final String LAST_FOUR_DIGIT = "last_four_digit";
    public static final String CARD_TYPE = "card_type";
    public static final String AUTHORIZATION_CODE = "authorization_code";
    public static final String NAME_ON_CARD = "name_on_card";
    public static final String MINIMUM_TAXI_CAPACITY = "1";
    //endpoints
    public static final String SERVICE_URL = "http://www.jasiride.com/";
    public static final String BASE_URL = Constants.SERVICE_URL + "api/v1/api/v1";
    public static final String CUSTOMER_UPLOAD_IMAGE_URL = Constants.BASE_URL + "/customer/profile_img";
    public static final String SEND_OTP_FOR_REG_URL = Constants.BASE_URL + "/customer/otp";
    public static final String SEND_OTP_FOR_PASSWORD_RESET_URL = Constants.BASE_URL + "/customer/otp/password_reset";
    public static final String TRANSACTION_VERIFICATION_URL = Constants.BASE_URL + "/customer/transaction/verify";
    public static final String CUSTOMER_ACTIVITY_URL = Constants.BASE_URL + "/customer/history";
    public static final String TRANSACTION_INITIALIZATION_URL = Constants.BASE_URL + "/customer/transaction/initialize";
    public static final String CANCEL_REQUEST_URL =   Constants.BASE_URL + "/ride_request/cancel/";
    public static final String SIGN_IN_URL = "http://www.jasiride.com/api/v1/api/v1/customer/login";
    public static final String REGISTER_URL = "http://www.jasiride.com/api/v1/api/v1/customer/register";
    public static final String PASSWORD_RESET_URL = Constants.BASE_URL + "/customer/reset_password";
    public static final String DISTANCE_TIME_COST_URL = Constants.BASE_URL + "/address/distanceTimeCost";
    public static final String RIDE_REQUEST_URL = Constants.BASE_URL + "/ride_request";
    public static final String GET_ADDRESS_URL = Constants.BASE_URL + "/address";
    public static final String NEAREST_DRIVER_URL = Constants.BASE_URL + "/driver/nearest";
    public static final String DRIVER_RATING_REQUEST_URL = Constants.BASE_URL + "/rating";


    public static final String HISTORY = "History";
    //Customer
    public static final String CUSTOMER = "customer";
    public static final String CUSTOMER_ID = "id";
    public static final String CUSTOMER_NAME = "customer_name";
    public static final String CUSTOMER_EMAIL = "customer_email";
    public static final String CUSTOMER_PHONE_NO = "customer_phone_no";
    public static final String CUSTOMER_SOCIAL_ID = "customer_social_id";
    public static final String CUSTOMER_IMG = "customer_img";
    public static final String CUSTOMER_LAT = "customer_lat";
    public static final String CUSTOMER_LNG = "customer_lng";
    public static final String CUSTOMER_ADDRESS = "customer_address";
    public static final String CUSTOMER_PROVINCE = "customer_province";
    public static final String CUSTOMER_TYPES_ID = "customer_types_id";
    public static final String CUSTOMER_COUNTRY = "customer_country";

    public static final String CARD_NUMBER = "cardNo";
    public static final String EXPIRY_MONTH = "cardMm";
    public static final String EXPIRTY_YEAR = "cardYr";
    public static final String CVV = "cvv";
    public static final String CUSTOMER_JSON_RESPONSE = "CustomerData";

    public static final String RIDERS_PICKUP_LAT = "riders_pickup_lat";
    public static final String RIDERS_PICKUP_LNG = "riders_pickup_lng";
    public static final String RIDERS_DESTINATION_LAT = "riders_destination_lat";
    public static final String RIDERS_DESTINATION_LNG = "riders_destination_lng";
    public static final String CUSTOMER_JSONOBJECT = "customer";

    //End of customer constants
    //Ride request constants
    public static final String RIDERS_PICKUP_ADDRESS = "riders_pickup_address";
    public static final String RIDERS_DESTINATION_ADDRESS = "riders_destination_address";
    public static final String PICKUP_TIMESTAMP = "pickup_timestamp";
    public static final String CUSTOMERS_ID = "customers_id";
    public static final String RIDE_TYPE = "ride_type";
    public static final String TAXI_TYPE = "taxi_type";
    public static final String LAT = "lat";
    public static final String LNG = "lng";
    public static final String RIDER_REQUEST_JSON_RESPONSE = "driver";
    public static final String ADDRESS = "address";
    public static final String OUT_JSON = "/json";
    public static final char KILOMETER = 'K';

    public static final String RIDE_REQUEST_ID = "id";
    public static final String RIDE_TOTAL_DISTANCE = "total_distance";
    public static final String RIDE_TOTAL_COST = "total_estimated_cost";
    public static final String STATUS = "status";
    public static final String MESSAGE = "message";
    public static final String RIDE_STATUS = "ride_status";

    //Driver
    public static final String DRIVER_ID = "id";
    public static final String DRIVER_IMAGE = "driver_img";
    public static final String DRIVERS_ID = "drivers_id";
    public static final String DRIVER_JSON_RESPONSE = "driver";
    public static final String DRIVER_NAME = "driver_name";
    public static final String DRIVER_EMAIL_ADDRESS = "driver_email";
    public static final String DRIVER_PHONE_NO = "driver_phone_no";
    public static final String DRIVER_IMG_URL = "driver_img";
    public static final String DRIVER_CURRENT_LAT = "driver_current_lat";
    public static final String DRIVER_CURRENT_LNG = "driver_current_lng";
    public static final String DRIVER_PLACE_NAME = "driver_current_place_name";
    public static final String DRIVER_PLACE_ID = "driver_current_place_id";
    public static final String DRIVER_PLACE_VICINITY = "driver_current_place_vicinity";
    public static final String DRIVER_PLACE_COUNTRY = "driver_current_country";
    public static final String TAXIS_ID = "taxis_id";
    public static final String DRIVER_TOTAL_RATERS = "no_of_raters";
    public static final String DRIVER_AVERAGE_RATING = "total_ratings";
    public static final String RESULT = "result";
    public static final String RATING = "rating";


    public static final String API_KEY = "AIzaSyDfxCKlNXlyTUdDS_1gWYQAS2zH7pE1qgk";
    public static final String RIDE_REQUEST_JSON_RESPONSE = "RideRequest";

    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;

    public static final int USE_ADDRESS_NAME = 1;
    public static final int USE_ADDRESS_LOCATION = 2;

    public static final String PACKAGE_NAME =
            "jaysicom.morotaxi";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";
    public static final String RESULT_ADDRESS = PACKAGE_NAME + ".RESULT_ADDRESS";
    public static final String LOCATION_LATITUDE_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_LATITUDE_DATA_EXTRA";
    public static final String LOCATION_LONGITUDE_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_LONGITUDE_DATA_EXTRA";
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_NAME_DATA_EXTRA";
    public static final String FETCH_TYPE_EXTRA = PACKAGE_NAME + ".FETCH_TYPE_EXTRA";

    public static final String CUSTOMER_PASSWORD = "password";

    public static final String TAXI = "taxi";
    public static final String TAXI_ID = "id";
    public static final String TAXI_MODEL = "taxi_model";
    public static final String TAXI_NAME = "taxi_name";
    public static final String TAXI_CAPACITY = "taxi_capacity";
    public static final String CAPACITY = "capacity";

    public static final String PLACE_DIRECTION_URL = "https://maps.googleapis.com/maps/api/directions/json";
    public static final String GROUP = "group";
    public static final String INDIVIDUAL = "individual";
    public static final String ACTUAL_COST = "actual_cost";
    public static final String TOTAL_ACTUAL_COST = "total_actual_cost";
    public static final String TOTAL_ESTIMATED_COST = "total_estimated_cost";
    public static final String ACTUAL_DISTANCE = "distance";
    public static final String ACTUAL_TIME = "time";
    public static final String ESTIMATED_COST = "estimated_cost";
    public static final String JPG_EXTENSION = ".jpg";
    public static final String IMAGE_DIRECTORY_NAME = ".iRide/.customer/.profile";
    public static final String ACCESS_CODE = "access_code";
    public static final String AUTHORIZATION_URL = "authorization_url";
    public static final String EMAIL = "email";
    public static final String SPACE = " ";
    public static final String CUSTOMER_CARD = "customer_card";
    public static final String AUTH_CODE = "auth_code";
    public static final String PAYMENT_OPTION_SHARED_PREFERENCE = "SHARED_PREFS";
    public static final String PAYMENT_OPTION = "payment_option";
    public static final String ACTIVITY_TYPE = "activity_type";
    public static final String REGISTRATION_ACTIVITY_TYPE = "registration_activity_type";
    public static final String PASSWORD_RESET_ACTIVITY_TYPE = "password_reset_activity_type";
    public static final String NEW_PASSWORD = "new_password";
    public static final String REQUEST_CANCELLED = "Request Canceled";
}