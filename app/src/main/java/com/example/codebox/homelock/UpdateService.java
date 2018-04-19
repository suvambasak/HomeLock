package com.example.codebox.homelock;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class UpdateService extends Service {

    // instance variables
    private boolean isConnected = false;
    private String deviceStatus = "Resolving...";
    public Bitmap image = null;

    private boolean serverControl = true;


    public UpdateService() {
    }

    // class for binding.
    class UpdateServiceBinder extends Binder {
        public UpdateService getService() {
            return UpdateService.this;
        }
    }


    // creating IBinder object.
    private IBinder mBinder = new UpdateServiceBinder();

    // sending service instance for binding.
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("Service IBinder","Inside onBind method");
        return mBinder;
    }

    // method for sending status of the device.
    public String getDeviceStatus(){
        return this.deviceStatus;
    }

    // method for sending image
    public Bitmap getImageFile(){
        return this.image;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        serverControl = false;
        Log.d("Inside Destroy","service destroyed");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Service Thread",""+Thread.currentThread().getId());
        serverControl = true;
        // starting the Server Connecter in another thread.
        new Thread(new Runnable() {
            @Override
            public void run() {
                new ServerConnecter().execute();
            }
        }).start();

        Log.d("Thread out","out");
        return START_STICKY;
    }



    // Inner class extends thread to connect server and pool the update from server.
    class ServerConnecter extends AsyncTask<String,Void,Void> {

        @Override
        protected Void doInBackground(String... params) {
            while (serverControl){
                try {
                    Thread.sleep(1000);
                    // Check Device online or not.
                    checkOnline();
                    Thread.sleep(1000);

                    // Get the notification updates from server.
                    getNotification();
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }


    //fetch Image from server.
    class ImageGet extends Thread {
        String stringURL;
        URL url = null;

        // constructor for setting URL
        ImageGet(String imageId){
            stringURL = DomainName.GETIMAGE + imageId;
        }


        public void run(){

            // Fteching image from server.
            try {
                url = new URL(stringURL);
                image = BitmapFactory.decodeStream(url.openConnection().getInputStream());

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.i("ImageFetch","DONE");
        }

    }



    // get notification update.
    private void getNotification(){
        // Getting username and android ID Hash
        final String username = UserData.getInstance(this).getUsername();
        final String androidIDHash = Hash.md5(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
        final String email = UserData.getInstance(this).getEmail();

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                DomainName.NOTIFICATION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //converting response into JSON object.
                        try {

                            JSONObject notificationResp = new JSONObject(response);

                            // Checking notification found or not.
                            if (notificationResp.getString("message").equals("Found")) {
                                Log.i("Notification : ",""+notificationResp.getString("notifyText"));
                                Log.i("Notif IMG :: ",""+notificationResp.getString("imageId"));


                                // When image is taker by owner.
                                // so not need notification.
                                if (notificationResp.getString("notifyText").equals("Image Taken"))
                                    new ImageGet(notificationResp.getString("imageId")).start();

                            } else {
                                Log.i("Notification ::" ,"No notification");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> params = new HashMap<>();
                // sending username and android ID.
                params.put("username",username);
                params.put("androidId",androidIDHash);
                params.put("email",email);
                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }


    // Device online checking method.
    private void checkOnline(){
        // Getting username and android ID hash.
        final String username = UserData.getInstance(this).getUsername();
        final String email = UserData.getInstance(this).getEmail();
        final String androidIDHash = Hash.md5(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                DomainName.ONLINECHECK,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting response into JSON object.
                            JSONObject tracker = new JSONObject(response);
                            Log.i("Online Msg :: ",""+tracker.getString("message"));

                            // notifiy for state change of the device.
//                            if (!deviceStatus.equals(tracker.getString("status")))
//                                onOffNotify("Device is " + tracker.getString("status"));

                            // updating the notification status.
                            deviceStatus = tracker.getString("status");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> params = new HashMap<>();
                // sending the username and Andorid ID.
                params.put("username",username);
                params.put("androidId",androidIDHash);
                params.put("email",email);
                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }
}
