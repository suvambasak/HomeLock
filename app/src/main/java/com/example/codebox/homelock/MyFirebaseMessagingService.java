package com.example.codebox.homelock;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * Created by codebox on 26/8/17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private Bitmap nImage;
    String mText;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.i("onMessageReceived","Notification received!!");

        if (remoteMessage.getData().get("tag").equals("onof")) {
            Log.i("Notification Type","ON/OFF");
            onOffNotify(remoteMessage.getData().get("msg"));
        }
        if (remoteMessage.getData().get("tag").equals("img")) {
            Log.i("Notification Type","IMAGE");

            mText = remoteMessage.getData().get("msg");

            downloadImage(remoteMessage.getData().get("imageId"));
        }
    }

    // Device online offline Notification.
    private void onOffNotify(String text){
        Bitmap icon = BitmapFactory.decodeResource(this.getResources(),R.drawable.lock);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.lockblack)
                .setColor(getColor(R.color.colorPrimary))
                .setContentTitle("Lock Alert")
                .setContentText(text)
                .setTicker("Lock Alert")
                .setLargeIcon(icon)
                .setAutoCancel(true)
                .setSound(Uri.parse("android.resource://com.example.codebox.homelock/"+R.raw.alert_tone))
                .setPriority(Notification.PRIORITY_HIGH)
                .setVibrate(new long[] { 100, 100 });

        Intent resultIntent = new Intent(this,Pin.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(Home.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(333, mBuilder.build());
    }

    // Image(Big picture) Notification.
    private void imageNotify(){
        Bitmap icon = BitmapFactory.decodeResource(this.getResources(),R.drawable.lock);

        NotificationCompat.BigPictureStyle style = new NotificationCompat.BigPictureStyle();
        style.setBigContentTitle(mText);
        style.bigPicture(nImage);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.lockblack)
                .setContentTitle("Lock Alert")
                .setContentText("Someone at Your Door!")
                .setTicker("Lock Alert")
                .setAutoCancel(true)
                .setColor(getColor(R.color.colorPrimary))
                .setLargeIcon(icon)
                .setPriority(Notification.PRIORITY_HIGH)
                .setVibrate(new long[] { 100, 100 })
                .setSound(Uri.parse("android.resource://com.example.codebox.homelock/"+R.raw.door_bell))
                .setLights(Color.WHITE, 500, 1000)
                .setStyle(style);

        Intent resultIntent = new Intent(this,Pin.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(Home.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(333, mBuilder.build());
    }


    // Method for downloading image.
    private void downloadImage(final String imageId){
        String URL = DomainName.GETIMAGE + imageId;
        ImageRequest imageRequest = new ImageRequest(URL, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                nImage = response;

                String path = saveToInternalStorage(nImage);
                UserData.getInstance(getApplicationContext()).saveImageName(path);
                imageNotify();

            }
        },0, 0, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestHandler.getInstance(this).addToRequestQueue(imageRequest);
    }

    private String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = contextWrapper.getDir("HomeLock", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"profile.jpg");

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }
}
