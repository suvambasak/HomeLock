package com.example.codebox.homelock;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class PinSetup extends AppCompatActivity implements View.OnClickListener {

    //declaring variables.
    private Button submit;
    private TextView constrain,confirm;
    private EditText pinEdit;

    private int click = 0;
    private String pin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Lock");
        setContentView(R.layout.activity_pin_setup);

        // initilizing button,textview and edit text.
        submit = (Button) findViewById(R.id.submit);
        constrain = (TextView) findViewById(R.id.constrain);
        confirm = (TextView) findViewById(R.id.confirm);
        pinEdit = (EditText) findViewById(R.id.pinEdit);

        //setting on click listner.
        submit.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        //Do nothing.
    }

    @Override
    public void onClick(View v) {
        if (v == submit){
            // checking the input size
            if (pinEdit.getText().toString().length() >= 4){
                ++click;

                //first click
                if (click == 1){
                    pin = pinEdit.getText().toString();
                    confirm.setText("Confirm your PIN");
                    pinEdit.setText("");
                    submit.setText("ok");
                }else{

                    //second click.
                    String pinCheck = pinEdit.getText().toString();
                    if (!pinCheck.equals(pin)){
                        confirm.setText("PINs don't match. Try again");
                        pinEdit.setText("");
                        submit.setText("Continue");
                        click = 0;
                    }else{
                        //getting pin hash.
                        String pinHash = Hash.md5(pin);
                        //Storing pin hash.
                        UserData.getInstance(this).storePin(pinHash);
                        //Starting Home activity.
                        startActivity(new Intent(getApplicationContext(),Home.class));
                        finish();
                    }
                }
            }
        }
    }
}
