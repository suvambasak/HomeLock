package com.example.codebox.homelock;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class GetAccess extends AppCompatActivity implements View.OnClickListener {
    private Button go;
    private EditText editUsername,
            editName,
            editAddress,
            editEmail,
            editphone,
            editpassword,
            editconfirmPassword;
    private TextView passwordNotMatch;
    private String key;
    private ProgressDialog progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Get Access");
        setContentView(R.layout.activity_get_access);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        go = (Button) findViewById(R.id.submit);
        editUsername = (EditText) findViewById(R.id.username);
        editName = (EditText) findViewById(R.id.name);
        editAddress = (EditText) findViewById(R.id.address);
        editEmail = (EditText) findViewById(R.id.email);
        editphone = (EditText) findViewById(R.id.phone);
        editpassword = (EditText) findViewById(R.id.password);
        editconfirmPassword = (EditText) findViewById(R.id.confirmPassword);
        passwordNotMatch = (TextView) findViewById(R.id.passwordNotMatch);

        progress = new ProgressDialog(this);
        progress.setMessage("Please wait...");

        go.setOnClickListener(this);
        scanCode();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.submit:
                Log.i("Calling","verify");
                verify();
                break;
        }
    }

    private void verify(){
        //showing the progress.
        progress.show();
        Log.i("Inside","Verifiy method");


        //confirm password
        if(!editpassword.getText().toString().equals(editconfirmPassword.getText().toString())){
            editpassword.setText("");
            editconfirmPassword.setText("");
            progress.dismiss();
//            sucess = false;
            passwordNotMatch.setText("Password is Not Matching !");
            return;
        }

        //getting username and password.
        final String username = editUsername.getText().toString().trim();
        final String name = editName.getText().toString().trim();
        final String address = editAddress.getText().toString().trim();
        final String email = editEmail.getText().toString().trim();
        final String phone = editphone.getText().toString().trim();
        final String code = key;
        //getting android ID Hash.
        final String androidId = Hash.md5(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
        final String password = Hash.md5(editpassword.getText().toString());

        Log.d("username",username);

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                DomainName.GET_ACCESS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progress.dismiss();
                        //converting response into JSON object.
                        try {
                            JSONObject jo = new JSONObject(response);

                            if (!jo.getBoolean("error")){
                                Log.i("Reply","Positive");
                                Log.i("msg",jo.getString("message"));
                                UserData.getInstance(getApplicationContext())
                                        .userLogin(
                                                username,
                                                email,
                                                name
                                        );
                                UserData.getInstance(getApplicationContext()).saveOwnerStatus("MEMBER");

                                String token = UserData.getInstance(getApplication()).getToken();
                                Log.d("TOKEN",token);
                                updateToken(email, token);

                                progress.dismiss();

                                startActivity(new Intent(getApplicationContext(),PinSetup.class));
                                finish();
                            }else{
                                //wrong username password.
                                Toast.makeText(GetAccess.this, jo.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progress.dismiss();
                Toast.makeText(GetAccess.this, error.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> params = new HashMap<>();
                params.put("username",username);
                params.put("key",code);
                params.put("name",name);
                params.put("address",address);
                params.put("email",email);
                params.put("phone",phone);
                params.put("androidId",androidId);
                params.put("password",password);

                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void updateToken(final String email, final String token) {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                DomainName.UPDATE_TOKEN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //converting response into JSON object.
                        try {
                            JSONObject jo = new JSONObject(response);

                            if (!jo.getBoolean("error")){
                                Toast.makeText(GetAccess.this, "Token Updated", Toast.LENGTH_SHORT).show();
                            }else{
                                //wrong username password.
                                Toast.makeText(GetAccess.this, jo.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(GetAccess.this, error.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> params = new HashMap<>();
                params.put("email",email);
                params.put("token", token);
                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }


    private void scanCode(){
        final Activity activity = this;

        IntentIntegrator integrator = new IntentIntegrator(activity);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt("Scan");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null){
            if(result.getContents()==null){
                Toast.makeText(this, "You cancelled the scanning", Toast.LENGTH_LONG).show();
                finish();
            }
            else {
                String code = result.getContents();
                Log.i("Code", code);

                String[] items = code.split("/");
                editUsername.setText(items[0]);
                key = items[1];
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
