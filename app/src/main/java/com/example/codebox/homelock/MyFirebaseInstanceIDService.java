package com.example.codebox.homelock;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by codebox on 26/8/17.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        String recent_token = FirebaseInstanceId.getInstance().getToken();
        Log.i("Now Token : ",recent_token);

        UserData.getInstance(getApplicationContext()).saveToken(recent_token);
    }
}
