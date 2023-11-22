package com.example.todire.listAdaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import androidx.core.content.ContextCompat;

import com.example.todire.R;

import java.util.ArrayList;

public class CustomAdaptor extends BaseAdapter {

    ArrayList<String> groupnames;
    ArrayList<String> adminNames;
    LayoutInflater inflater;
    Context context;
    public CustomAdaptor(Context context, ArrayList<String> arr , ArrayList<String> AdminName){

        if (context != null) {
            this.context = context;
            this.groupnames = arr;
            this.adminNames = AdminName;
            inflater = LayoutInflater.from(context);
        } else {
            throw new IllegalArgumentException("Context cannot be null.");
        }

    }

    @Override
    public int getCount() {
         return groupnames.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       convertView = inflater.inflate(R.layout.list_view, null);
        TextView textView = (TextView) convertView.findViewById(R.id.textview);
        TextView adminName = (TextView) convertView.findViewById(R.id.adminName);
        textView.setText(groupnames.get(position));
        adminName.setText("Admin: " + adminNames.get(position));



        return convertView;
    }
}