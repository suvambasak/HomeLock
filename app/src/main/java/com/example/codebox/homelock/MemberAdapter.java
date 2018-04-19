package com.example.codebox.homelock;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by suvam on 4/10/17.
 */

public class MemberAdapter extends ArrayAdapter<Member> {

    private static final String LOG_TAG = MemberAdapter.class.getSimpleName();

    public MemberAdapter(Activity context, ArrayList<Member> member) {
        super(context, 0, member);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.member, parent, false);
        }

        Member currentMember = getItem(position);


        TextView nameTextView = (TextView) listItemView.findViewById(R.id.name);
        nameTextView.setText(currentMember.getName());

        TextView numberTextView = (TextView) listItemView.findViewById(R.id.email);
        numberTextView.setText(currentMember.getemail());

        TextView permissionTextView = (TextView) listItemView.findViewById(R.id.permission);
        permissionTextView.setText(currentMember.getPermission());

        return listItemView;
    }

}
