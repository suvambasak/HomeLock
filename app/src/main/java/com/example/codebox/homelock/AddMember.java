package com.example.codebox.homelock;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddMember extends AppCompatActivity {
    ImageView qrcode;
    private String code;
    private TextView loadingstatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);

        qrcode = (ImageView) findViewById(R.id.qrcode);
        loadingstatus = (TextView) findViewById(R.id.loadingStatus);
        getKey();
    }



    private void getKey() {
        final String username = UserData.getInstance(getApplicationContext()).getUsername();
        final String email = UserData.getInstance(getApplicationContext()).getEmail();
        final String androidId = Hash.md5(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                DomainName.GIVE_ACCESS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loadingstatus.setText("Scan this QR Code");
                        //converting response into JSON object.
                        try {
                            JSONObject jo = new JSONObject(response);

                            if (!jo.getBoolean("error")){
                                code = jo.getString("key");
                                Log.i("Code",code);
                                setQrCode();
                            }else{
                                //wrong username password.
                                Toast.makeText(AddMember.this, jo.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingstatus.setText("Retry!!");
                Toast.makeText(AddMember.this, error.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> params = new HashMap<>();
                params.put("username",username);
                params.put("androidId",androidId);
                params.put("email",email);
                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }


    private void setQrCode() {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(code, BarcodeFormat.QR_CODE, 1000, 1000);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            qrcode.setImageBitmap(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
}
