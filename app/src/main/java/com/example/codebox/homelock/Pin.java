package com.example.codebox.homelock;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class Pin extends AppCompatActivity implements View.OnClickListener {

    //Declare variable.
    private TextView loginTitle, loginConstrain;
    private EditText loginPinEdit;
    private Button loginSubmit;
    private String pin;
    private int wrongCount = 0;
    private boolean pinChange,addNewMember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);

        Intent intent = getIntent();
        pinChange = intent.getBooleanExtra("pin_change",false);
        addNewMember = intent.getBooleanExtra("new_member",false);



        //initilizing button textview edittext.
        loginTitle = (TextView) findViewById(R.id.loginTitle);
        loginConstrain = (TextView) findViewById(R.id.loginConstrain);
        loginPinEdit = (EditText) findViewById(R.id.loginPinEdit);
        loginSubmit = (Button) findViewById(R.id.loginSubmit);

        //on click listner.
        loginSubmit.setOnClickListener(this);

        //getting the pin hash.
        pin = UserData.getInstance(this).getPinHash();

    }

    private void timeOut() {
        //log out for 3 times wrong pin.
        Toast.makeText(getApplicationContext(), "Timeout", Toast.LENGTH_SHORT).show();
        //Log out.
        UserData.getInstance(getApplicationContext()).logOut();
        // send to login activity.
        startActivity(new Intent(getApplicationContext(), Login.class));
        finish();
    }

    @Override
    public void onClick(View v) {
        if (v == loginSubmit){
            //get the pin
            String inputPin = loginPinEdit.getText().toString();
            //checking the pin length.
            if (inputPin.length() >= 4){
                //getting the pin hash
                String pinHash = Hash.md5(inputPin);
                //checking the pin hash.
                if (!pinHash.equals(pin)){
                    // pin hash not matched,
                    loginConstrain.setText("PINs don't match. Try again");

                    if (++wrongCount > 3 ){
                        timeOut();
                        return;
                    }
                    Log.i("Wrong COUNT ::",""+wrongCount);
                }else{
                    // pin hash matched.
                    // Chnage Pin Change status.
                    if (pinChange){
                        finish();
                        startActivity(new Intent(this,PinSetup.class));
                    }
                    if(addNewMember){
                        finish();
                        startActivity(new Intent(this,AddMember.class));
                    }else{
                        // start home activity.
                        startActivity(new Intent(getApplicationContext(), Home.class));
                        finish();
                    }
                }

            }else{
                //when pin length is less than 4.
                loginConstrain.setText("PIN must be at least 4 digit");
            }
        }
    }
}
