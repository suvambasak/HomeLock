package com.example.codebox.homelock;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by codebox on 6/5/17.
 * To Store user data using SharedPreference.
 */

public class UserData {
    private static UserData mInstance;
    private static Context mCtx;

    //SharedPreference name.
    private static final String SHARED_PREF_NAME = "homeLock";
    private static final String SHARED_PREF_IP = "IPAddress";
    private static final String TOKEN_SHARED_PREF = "homeLockToken";
    private static final String IMAGE_SHARED_PREF = "notiImg";
    private static final String OWNER_SHIP = "ownerShip";

    //Name, Username, Email address Key name.
    private static final String KEY_USERNAME = "username";
    private static final String KEY_USER_EMAIL = "email";
    private static final String KEY_USER_NAME = "name";
    // Google FCM Token.
    private static final String KEY_TOKEN = "token";
    // Notification Image Key.
    private static final String KEY_IMAGE = "image";

    private static final String KEY_PIN = "pin";

    private static final String KEY_USER_TYPE = "usertype";

    private static final String IP = "currentIpAddress";

    private UserData(Context context) {
        mCtx = context;
    }

    public static synchronized UserData getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new UserData(context);
        }
        return mInstance;
    }



    //Owner status methods

    //Saving owner status.
    public boolean saveOwnerStatus(String status){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(OWNER_SHIP,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(KEY_USER_TYPE,status);
        editor.commit();
        return true;
    }

    //Getting owner status
    public String getOwnerStatus(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(OWNER_SHIP,Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USER_TYPE,null);
    }

    //Delete owner status
    public boolean deleteOwnerStatus(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(OWNER_SHIP,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        return true;
    }




    //Notification Image function.

    // Save Image Name method.
    public boolean saveImageName(String filename){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(IMAGE_SHARED_PREF,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(KEY_IMAGE,filename);
        editor.commit();
        Log.i("Sharedpref IMG","Saved");
        return true;
    }

    // Get Image Name method.
    public String getImageName(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(IMAGE_SHARED_PREF,Context.MODE_PRIVATE);
        String filename = sharedPreferences.getString(KEY_IMAGE,null);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        Log.i("Image SharePref","Clean");

        return filename;
    }


    // Methods for FCM token.

    // Saveing current token.
    public boolean saveToken(String token){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(TOKEN_SHARED_PREF,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(KEY_TOKEN, token);
        editor.apply();
        return true;
    }

    // getting current token.
    public String getToken(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(TOKEN_SHARED_PREF,Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_TOKEN,null);
    }


    // Mehtod for IP Address

    // Storing IP address.
    public boolean currentIP(String ip){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_IP,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Putting value;
        editor.putString(IP,ip);
        editor.apply();

        return true;
    }


    // method for getting IP Address
    public static String getIP(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_IP,Context.MODE_PRIVATE);
        return sharedPreferences.getString(IP,null);
    }

    //method for delete old IP.
    public boolean deleteOldIP(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_IP,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //clearing the saved data.
        editor.clear();
        editor.apply();
        return true;
    }




    //Storing username, email, name during login.
    public boolean userLogin(String username, String email, String name){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //Putting data name value pair.
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_NAME, name);
        editor.apply();
        return true;
    }

    //Storing PIN Hash.
    public boolean storePin(String pin){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //putting pin.
        editor.putString(KEY_PIN, pin);
        editor.apply();
        return true;
    }


    //Check logged in or not.
    public boolean isLoggedIn(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);

        //If sharedPreference is null then user is not logged in.
        if (sharedPreferences.getString(KEY_PIN,null) != null){

            // if username is not stored then it return null.
            return true;
        }
        return false;
    }

    //method for logging out.
    public boolean logOut(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //clearing the saved data.
        editor.clear();
        editor.apply();
        return true;
    }




    //Methods for getting user details..

    //method for getting username.
    public String getUsername(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USERNAME,null);
    }

    //method for getting email.
    public String getEmail(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USER_EMAIL,null);
    }

    //method for getting name.
    public String getName(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USER_NAME,null);
    }

    //method for getting pin hash.
    public String getPinHash(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_PIN,null);
    }
}