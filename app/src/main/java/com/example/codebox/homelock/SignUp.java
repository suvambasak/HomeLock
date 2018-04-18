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

public class SignUp extends AppCompatActivity implements View.OnClickListener {
    //all variables.
    private EditText editname, editemail, editaddress, editphone, editusername, editlockMac, editpassword, editconfirmPassword;
    private Button signUp;
    private String editandroidId;
    private boolean sucess = true;
    private ProgressDialog progress;
    private TextView passwordNotMatch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Sign Up");
        setContentView(R.layout.activity_sign_up);

        // ActionBar back button.
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        //initilizing all variable.
        passwordNotMatch = (TextView) findViewById(R.id.passwordNotMatch);

        signUp = (Button) findViewById(R.id.singUp);

        editname = (EditText) findViewById(R.id.name);
        editemail = (EditText) findViewById(R.id.email);
        editaddress = (EditText) findViewById(R.id.address);
        editphone = (EditText) findViewById(R.id.phone);
        editusername = (EditText) findViewById(R.id.username);
        editlockMac = (EditText) findViewById(R.id.lockMac);
        editpassword = (EditText) findViewById(R.id.password);
        editconfirmPassword = (EditText) findViewById(R.id.confirmPassword);

        //getting android id.
        editandroidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        //creating progress.
        progress = new ProgressDialog(this);

        //creating on clock listner.
        signUp.setOnClickListener(this);

    }

    //register method to insert into database
    private void register(final View v){

        //show progress.
        progress.setMessage("Please wait...");
        progress.show();

        //getting values
        final String name = editname.getText().toString().trim();
        final String email = editemail.getText().toString().trim();
        final String address = editaddress.getText().toString().trim();
        final String phone = editphone.getText().toString().trim();
        final String username = editusername.getText().toString().trim();

        //confirm password
        if(!editpassword.getText().toString().equals(editconfirmPassword.getText().toString())){
            editpassword.setText("");
            editconfirmPassword.setText("");
            progress.dismiss();
            sucess = false;
            passwordNotMatch.setText("Password is Not Matching !");
            return;
        }
        //make ing flag true for password
        sucess=true;

        //getting hash of password  Lock MAC and android ID.
        final String password = Hash.md5(editpassword.getText().toString());
        final String lockMac = Hash.md5(editlockMac.getText().toString().trim());
        final String androidId = Hash.md5(editandroidId);

        //making request.
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                DomainName.SIGNUP,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progress.dismiss();

                        try {
                            JSONObject jo = new JSONObject(response);
                            Snackbar.make(v, jo.getString("message"), Snackbar.LENGTH_LONG).show();
                           // Toast.makeText(getApplicationContext(), jo.getString("message"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progress.hide();
                Snackbar.make(v, error.getMessage(), Snackbar.LENGTH_LONG).show();
                //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();

                params.put("username",username);
                params.put("password",password);
                params.put("name",name);
                params.put("address",address);
                params.put("email",email);
                params.put("phone",phone);
                params.put("androidId",androidId);
                params.put("lockMac",lockMac);

                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    //on click listner.
    @Override
    public void onClick(View v) {
        if (v == signUp){

            //calling register method.
            register(v);
            if (sucess){
                finish();
                startActivity(new Intent(this,Login.class));
            }

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
