package com.example.codebox.homelock;

import android.app.ProgressDialog;
import android.content.Intent;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Update extends AppCompatActivity implements View.OnClickListener {
    //variable
    private EditText editpassword;
    private String androidId;
    private Button apply;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Update Details");
        setContentView(R.layout.activity_update);

        //ActionBar back button.
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        //initilizing variable.
        editpassword = (EditText) findViewById(R.id.password);
        apply = (Button) findViewById(R.id.apply);

        //creating progresss.
        progress = new ProgressDialog(this);
        progress.setMessage("Please wait...");

        //getting android id.
        androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        apply.setOnClickListener(this);
    }

    //updateId method.
    private void updateId(){
        //showing progress.
        progress.show();
        //getting password hash android id hash username.
        final String password = Hash.md5(editpassword.getText().toString());
        final String updatedAndroidId = Hash.md5(androidId);
        final String email = UserData.getInstance(getApplicationContext()).getEmail();

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                DomainName.ANDROIDIDUPDATE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progress.dismiss();
                        //converting response into JSON object.
                        try {
                            JSONObject jo = new JSONObject(response);
                            Toast.makeText(getApplicationContext(), jo.getString("message"), Toast.LENGTH_SHORT).show();

                            finish();
                            startActivity(new Intent(getApplicationContext(),Home.class));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progress.dismiss();
                Toast.makeText(getApplicationContext(), error.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> params = new HashMap<>();
                params.put("password", password);
                params.put("androidId", updatedAndroidId);
                params.put("email",email);
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }


    //onclick listner.
    @Override
    public void onClick(View v) {
        if (v == apply)
            updateId();
    }

    //ActionBar back button.

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
