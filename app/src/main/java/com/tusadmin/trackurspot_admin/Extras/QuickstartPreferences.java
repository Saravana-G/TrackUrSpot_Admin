package com.tusadmin.trackurspot_admin.Extras;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class QuickstartPreferences {

    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String USER_EMAIL = "email";
    public static final String USER_NAME = "name";
    public static final String USER_INSTITUTION_ID = "ins_id";
    public static final String USER_INSTITUTION_NAME = "ins_name";
    public static final String NOTIFICATION_MESSAGE = "notification message";
    public static  Double longitude_single = 76.9616311d;
    public static Double latitude_single = 11.0195312d;
    public static String busid="0";
    public static String last_updated="--";
    static SharedPreferences sharedPreferences;
    static SharedPreferences.Editor editor;


    public static void initQuickstartPreferences(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
         editor = sharedPreferences.edit();
    }


    public static void insertString(String key, String value) {
        sharedPreferences.edit().putString(key, value).apply();
    }

    public static String extractString(String key){
        return sharedPreferences.getString(key,"");
    }

    public static void modifyString(String key, String value){
        editor.putString(key,value);
        editor.commit();
    }
    public static void remove(){
        editor.clear();
        editor.commit();
    }
}
