package com.example.codebox.homelock;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class IPChange extends AppCompatActivity implements View.OnClickListener {
    private TextView storedIpAddress;
    private EditText newIpAddress;
    private Button save, autoFetch;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ipchange);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Initilization.
        storedIpAddress = (TextView) findViewById(R.id.storedIpAddress);
        newIpAddress = (EditText) findViewById(R.id.newIpAddress);
        save = (Button) findViewById(R.id.save);
        autoFetch = (Button) findViewById(R.id.auto_fetch);

        progress = new ProgressDialog(this);

        // Setting old IP
        storedIpAddress.setText(UserData.getIP());

        // On click listener.
        save.setOnClickListener(this);
        autoFetch.setOnClickListener(this);
    }

    private void changeIP(){
        UserData.getInstance(this).deleteOldIP();
        String IP = newIpAddress.getText().toString();

        UserData.getInstance(this).currentIP(IP);

        Toast.makeText(getApplicationContext(), "IP changed to "+UserData.getIP(), Toast.LENGTH_SHORT).show();
    }

    private void changeIP(String IP){
        UserData.getInstance(this).deleteOldIP();
        UserData.getInstance(this).currentIP(IP);

        Toast.makeText(getApplicationContext(), "IP changed to "+UserData.getIP(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.save:
                changeIP();
                finish();
                startActivity(new Intent(getApplicationContext(), Login.class));
                break;
            case R.id.auto_fetch:
                fetchIpAddress();
                break;
        }
    }

    private void fetchIpAddress(){
        progress.setMessage("Fetching...");
        progress.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                DomainName.FETCH_IP,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progress.dismiss();

                        try {
                            JSONObject jo = new JSONObject(response);
                            if(!jo.getBoolean("error")){
                                changeIP(jo.getString("message"));
                            }else{
                                Toast.makeText(IPChange.this, "Failed!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progress.hide();
                Toast.makeText(getApplicationContext(), error.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
