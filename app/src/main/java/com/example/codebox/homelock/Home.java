package com.example.codebox.homelock;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.IBinder;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Home extends AppCompatActivity implements View.OnClickListener {

    //variables
    TextView status, reply;
    ImageView imageView,dot;
    private Button lock, unlock, takeImage, mailImage, start_recording, stop_recording;

    private String androidId,username,device,email;
    private boolean mainBinderThread;
    private ProgressDialog progress;

    //Json Object for request.
    JSONObject req = new JSONObject();

    // Service
    Intent iUpdateService;
    private UpdateService updateService;
    private boolean isServiceBound;
    private ServiceConnection serviceConnection;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Home Lock");
        setContentView(R.layout.activity_home);

        //checking user is logged in or not!.
        if (!UserData.getInstance(this).isLoggedIn()){
            finish();
            startActivity(new Intent(this, Login.class));
        }

        //getting android ID Hash.
        androidId = Hash.md5(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
        //getting username.
        username = UserData.getInstance(this).getUsername();
        email = UserData.getInstance(this).getEmail();
        //device type
        device = "Phone";

        //Initilizing the variables.
        status  = (TextView) findViewById(R.id.status);
        reply = (TextView) findViewById(R.id.reply);
        lock = (Button) findViewById(R.id.lock);
        unlock = (Button) findViewById(R.id.unlock);
        takeImage = (Button) findViewById(R.id.takeImage);
        mailImage = (Button) findViewById(R.id.mailImage);
        start_recording = (Button) findViewById(R.id.start_recording);
        stop_recording = (Button) findViewById(R.id.stop_recording);


        imageView = (ImageView) findViewById(R.id.imageView);
        dot = (ImageView) findViewById(R.id.dot);


        String path = UserData.getInstance(getApplicationContext()).getImageName();
        if(path != null){
            try {
                File f=new File(path, "profile.jpg");
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                imageView.setPadding(0,0,0,0);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                imageView.setImageBitmap(b);
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        }


        //setting on click listner.
        lock.setOnClickListener(this);
        unlock.setOnClickListener(this);
        takeImage.setOnClickListener(this);
        mailImage.setOnClickListener(this);
        start_recording.setOnClickListener(this);
        stop_recording.setOnClickListener(this);

        Log.d("Main thread id : ", ""+Thread.currentThread().getId());

        // Starting service.
        iUpdateService = new Intent(getApplicationContext(),UpdateService.class);
        startService(iUpdateService);
    }

    // binding service to UI thread
    @Override
    protected void onResume() {
        super.onResume();
        mainBinderThread = true;
        bindService();
        Log.d("OnResumed","invoked");
        new DisplayStatusUpdate().start();
    }

    // unbind and stoping UI update thread.
    @Override
    protected void onPause() {
        super.onPause();
        mainBinderThread = false;
        Log.d("onPaused","invoked");
        unBindService();
    }

    // Service connection or binding method.
    private void bindService() {
        if (serviceConnection == null){
            Log.d("Click","bindService");
            serviceConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    UpdateService.UpdateServiceBinder updateServiceBinder = (UpdateService.UpdateServiceBinder) service;
                    updateService = updateServiceBinder.getService();

                    isServiceBound = true;
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    isServiceBound = false;
                }
            };
        }

        bindService(iUpdateService,serviceConnection, Context.BIND_AUTO_CREATE);
    }


    // Unbinding method.
    private void unBindService() {
        if (isServiceBound){
            unbindService(serviceConnection);
            isServiceBound=false;
        }
        stopService(iUpdateService);
    }


    // Thread to update UI status update.
    class DisplayStatusUpdate extends Thread{
        public void run(){
            while (mainBinderThread) {
                try {
                    if(isServiceBound){
                        Home.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                status.setText(updateService.getDeviceStatus());
                                if (updateService.getDeviceStatus().equals("Online"))
                                    dot.setBackgroundResource(R.drawable.online_dot);
                                if (updateService.getDeviceStatus().equals("Offline"))
                                    dot.setBackgroundResource(R.drawable.offline_dot);
                                try{
                                    if (updateService.getImageFile() != null) {
                                        imageView.setPadding(0,0,0,0);
                                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                        imageView.setImageBitmap(updateService.getImageFile());
                                    }
                                } catch (Exception e) {
                                    Log.i("ImageSet","Exception");
                                }
                            }
                        });
                    }
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }




    //On click listener.
//    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.lock:
                status.setText("Lock");

                try {
                    //putting value for lock.
                    req.put("device",device);
                    req.put("username",username);
                    req.put("androidId",androidId);
                    req.put("email",email);
                    req.put("request","Lock");

                    // sending the request.
                    new Send(req.toString(),"Lock").start();

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {}

                break;
            case R.id.unlock:
                status.setText("Un-Lock");

                try {
                    //putting value for unlock.
                    req.put("device",device);
                    req.put("username",username);
                    req.put("androidId",androidId);
                    req.put("email",email);
                    req.put("request","Unlock");

                    // Confirming from user !!
                    new AlertDialog.Builder(this)
                            .setTitle("Confirm")
                            .setMessage("Are you sure to Un-Lock the Door?")
                            .setIcon(R.drawable.alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    // sending the request.
                                    new Send(req.toString(),"Lock").start();
                                }})
                            .setNegativeButton(android.R.string.no, null).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {}

                break;
            case R.id.takeImage:
                status.setText("Take Image");

                try {
                    //putting value for take image.
                    req.put("device",device);
                    req.put("username",username);
                    req.put("androidId",androidId);
                    req.put("email",email);
                    req.put("request","TakeImage");

                    // sending the request.
                    new Send(req.toString(),"Lock").start();

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {}

                break;
            case R.id.mailImage:
                status.setText("Email Image");

                try {
                    //putting value for Email image.
                    req.put("device",device);
                    req.put("username",username);
                    req.put("androidId",androidId);
                    req.put("email",email);
                    req.put("request","Email");

                    // sending the request.
                    new Send(req.toString(),"Lock").start();

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {}

                break;
            case R.id.start_recording:
                Toast.makeText(getApplicationContext(), "Not Added Yet", Toast.LENGTH_SHORT).show();
                break;
            case R.id.stop_recording:
                Toast.makeText(getApplicationContext(), "Not Added Yet", Toast.LENGTH_SHORT).show();
                break;
            default:
                status.setText("Status");
                break;
        }
    }




    //Adding menu.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }


    //option selection in menu.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.changeId:
                //changin android ID.
                //Staring android Update activity.
                startActivity(new Intent(Home.this, Update.class));
                break;
            case R.id.change_ip:
                finish();
                startActivity(new Intent(Home.this, IPChange.class));
                break;
            case R.id.changePin:
                Intent intentChangePin = new Intent(this,Pin.class);
                intentChangePin.putExtra("pin_change",true);
                finish();
                startActivity(intentChangePin);
                break;
            case R.id.newMember:
                Intent intentNewMember  = new Intent(Home.this,Pin.class);
                intentNewMember.putExtra("new_member",true);
                startActivity(intentNewMember);

                break;
            case R.id.memberList:
                if (UserData.getInstance(getApplicationContext()).getOwnerStatus().equals("MEMBER")){
                    Log.i("MemberStatus",UserData.getInstance(getApplicationContext()).getOwnerStatus());
                    Toast.makeText(getApplicationContext(), "Only for Admin.", Toast.LENGTH_SHORT).show();
                    break;
                }
                startActivity(new Intent(this,MemberList.class));
                break;
            case R.id.logout:
                // mathod for ereasing the shared preference user data and starting login activity.
                new AlertDialog.Builder(this)
                        .setTitle("Confirm")
                        .setMessage("Do you want to logout?")
                        .setIcon(R.drawable.alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                logout();
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
                break;
            case R.id.about:
                // redirect to the website
                // not added yet.
                Toast.makeText(getApplicationContext(), "This features is not added yet.", Toast.LENGTH_SHORT).show();
                break;
//            case R.id.test:
//
//                break;
        }
        return true;
    }

    // Inner class for sending Request.
    private class Send extends Thread {
        String request, response, requestingFor;

        // initilizing the variables.
        Send(String request, String requestingFor) {
            this.request = request;
            this.requestingFor = requestingFor;
        }
        // thread.
        public void run(){
            try {
                // establish connection.
                Socket socket = new Socket(DomainName.IP,DomainName.PORT);
                PrintWriter toServer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
                BufferedReader fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // send the request.
                toServer.print(request);
                toServer.flush();

                // get reply.
                response = fromServer.readLine();

                Log.e("value reponse :  ",response);

                // chnage satus in UI thread.
                Home.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        reply.setText(response.toString());
                    }
                });


            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    private void logout(){
        progress = new ProgressDialog(this);

        final String status = UserData.getInstance(getApplicationContext()).getOwnerStatus();
        final String email = UserData.getInstance(getApplicationContext()).getEmail();
        final String finalUsername = username;

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                DomainName.LOGOUT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progress.dismiss();
                        //converting response into JSON object.
                        try {
                            JSONObject jo = new JSONObject(response);

                            if (!jo.getBoolean("error")){
                                Log.i("LogoutMSg",jo.getString("message"));

                                UserData.getInstance(getApplicationContext()).logOut();
                                UserData.getInstance(getApplicationContext()).deleteOwnerStatus();
                                finish();
                                startActivity(new Intent(getApplicationContext(), Login.class));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progress.dismiss();
            }
        }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> params = new HashMap<>();

                params.put("status",status);
                params.put("email",email);
                params.put("username",finalUsername);

                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }
}