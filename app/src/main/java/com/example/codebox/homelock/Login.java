package com.example.codebox.homelock;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity implements View.OnClickListener {
    //Variables
    private EditText editusername, editpassword;
    private Button login,newLock,getAccess;
    private ProgressDialog progress;
    private ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Home Lock");
        setContentView(R.layout.activity_login);

        //If the user is already logged in.
        if (UserData.getInstance(this).isLoggedIn()){
            finish();
            startActivity(new Intent(this, Pin.class));
            return;
        }

        //initilizaing buttons
        login = (Button) findViewById(R.id.login);
        newLock = (Button) findViewById(R.id.newLock);
        getAccess = (Button) findViewById(R.id.getAccess);

        editusername = (EditText) findViewById(R.id.username);
        editpassword = (EditText) findViewById(R.id.password);

        imageView = (ImageView) findViewById(R.id.imageView);

        //creating progress.
        progress = new ProgressDialog(this);
        progress.setMessage("Please wait...");

        login.setOnClickListener(this);
        newLock.setOnClickListener(this);
        imageView.setOnClickListener(this);
        getAccess.setOnClickListener(this);

    }

    private void login(final View v){
        //showing the progress.
        progress.show();

        //getting username and password.
        final String username = editusername.getText().toString().trim();
        //Hash the password.
        final String password = Hash.md5(editpassword.getText().toString());
        //getting token.
        final String token = UserData.getInstance(getApplication()).getToken();


        Log.d("username",username);
        Log.d("password",password);
        Log.d("TOKEN",token);

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                DomainName.SIGNIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progress.dismiss();
                        //converting response into JSON object.
                        try {
                            JSONObject jo = new JSONObject(response);

                            if (!jo.getBoolean("error")){
                                UserData.getInstance(getApplicationContext())
                                        .userLogin(
                                                jo.getString("username"),
                                                jo.getString("email"),
                                                jo.getString("name")
                                        );
                                UserData.getInstance(getApplicationContext()).saveOwnerStatus("ADMIN");
                                startActivity(new Intent(getApplicationContext(),PinSetup.class));
                                finish();
                            }else{
                                //wrong username password.
                                Snackbar.make(v, jo.getString("message"), Snackbar.LENGTH_LONG).show();
                                //Toast.makeText(Login.this, jo.getString("message"), Toast.LENGTH_SHORT).show();
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
                params.put("username",username);
                params.put("password", password);
                params.put("token",token);

                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login:
                login(v);
                break;
            case R.id.newLock:
                startActivity(new Intent(Login.this,SignUp.class));
                break;
            case R.id.imageView:
                finish();
                startActivity(new Intent(getApplicationContext(), IPChange.class));
                break;
            case R.id.getAccess:
                startActivity(new Intent(getApplicationContext(),GetAccess.class));
                break;
            default:
                Toast.makeText(getApplicationContext(), "Something is wrong!", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
