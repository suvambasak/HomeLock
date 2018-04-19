package com.example.codebox.homelock;

import android.app.ProgressDialog;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.view.Gravity.RIGHT;

public class MemberList extends AppCompatActivity implements AdapterView.OnItemLongClickListener, PopupMenu.OnMenuItemClickListener {

    private ProgressDialog progress;
    private String memberEmail,memberName;
    private String editandroidId;
    ArrayList<Member> members;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Members");
        setContentView(R.layout.activity_member_list);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        editandroidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        progress = new ProgressDialog(this);

        members = new ArrayList<Member>();

        getMemberList();
    }


    void getMemberList(){
        progress.setTitle("Fetching members");
        progress.setMessage("Wait while fetching...");
        progress.show();

        final String username = UserData.getInstance(getApplicationContext()).getUsername();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                DomainName.GET_MEMBER_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progress.dismiss();
                        try {

                            JSONArray dataSet = new JSONArray(response);
                            Log.d("Data","Fetched");
                            int index = dataSet.length();
                            Log.d("length",""+index);
//
//
                            for (int i=0; i<index; i++){
                                JSONObject tuple = dataSet.getJSONObject(i);

                                members.add(new Member(
                                        tuple.getString("name"),
                                        tuple.getString("email"),
                                        tuple.getString("enable")
                                        )
                                );
                            }

                            MemberAdapter memberAdapter = new MemberAdapter(MemberList.this, members);
                            ListView listView = (ListView) findViewById(R.id.listview_members);
                            listView.setAdapter(memberAdapter);

                            listView.setOnItemLongClickListener(MemberList.this);


//                            Toast.makeText(getApplicationContext(), "Total " + index + " updates", Toast.LENGTH_SHORT).show();
                            Snackbar.make(findViewById(R.id.listview_members), "Total " + index + " Member(s)!", Snackbar.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progress.hide();
                //Snackbar.make(v, error.getMessage(), Snackbar.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("username",username);
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


    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        Member member = members.get(i);

        memberEmail = member.getemail();
        memberName = member.getName();

        PopupMenu popup = new PopupMenu(this,view,RIGHT);
        popup.setOnMenuItemClickListener(MemberList.this);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.popup_menu,popup.getMenu());
        popup.show();

        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch(item.getItemId()){
            case R.id.remove:
                removeMember("remove");
                return true;
            case R.id.add:
                removeMember("add");
                return true;
            default:
                return false;
        }
    }

    private void removeMember(final String operation){
        progress.setTitle("Fetching members");
        progress.setMessage("Wait while fetching...");
        progress.show();

        final String username = UserData.getInstance(getApplicationContext()).getUsername();
        final String androidId = Hash.md5(editandroidId);
        final String email = memberEmail;

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                DomainName.REMOVE_MEMBER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progress.dismiss();
                        try {
                            JSONObject jo = new JSONObject(response);
                            if (!jo.getBoolean("error")){
                                Toast.makeText(getApplicationContext(), memberName+" "+jo.getString("message"), Toast.LENGTH_SHORT).show();
                                members.clear();
                                getMemberList();
                            }else{
                                Toast.makeText(MemberList.this, jo.getString("message"), Toast.LENGTH_SHORT).show();
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
                params.put("username",username);
                params.put("email",email);
                params.put("operation",operation);
                params.put("androidId",androidId);
                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

}
