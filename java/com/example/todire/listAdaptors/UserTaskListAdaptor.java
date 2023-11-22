package com.example.todire.listAdaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.core.content.ContextCompat;

import com.example.todire.R;

import java.util.ArrayList;

public class UserTaskListAdaptor extends BaseAdapter {

    ArrayList<String> description;
    ArrayList<Boolean> status ;
    ArrayList<Boolean> setDueDate;
    ArrayList<Boolean> expireStatus;
    LayoutInflater inflater;
    Context context;
    public UserTaskListAdaptor(Context context, ArrayList<String> description ,  ArrayList<Boolean> status, ArrayList<Boolean> setDuedate, ArrayList<Boolean> expireStatus ){

        if (context != null) {
            this.context = context;
            this.description = description;
            this.status = status;
            this.setDueDate = setDuedate;
            this.expireStatus= expireStatus;
            inflater = LayoutInflater.from(context);
        } else {
            throw new IllegalArgumentException("Context cannot be null.");
        }

    }

    @Override
    public int getCount() {
        return description.size();
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
        convertView = inflater.inflate(R.layout.user_task_list_adaptor, null);
        TextView descriptionText = (TextView) convertView.findViewById(R.id.description);
        TextView completeStatus = (TextView) convertView.findViewById(R.id.task_status_text);
        LinearLayout backgroundLayout = (LinearLayout) convertView.findViewById(R.id.background_layout);
        View timerLogo = (View) convertView.findViewById(R.id.timer_logo);
        descriptionText.setText(description.get(position));



        if(status.get(position)){
            backgroundLayout.setBackgroundResource(R.drawable.user_task_background_completed);
            completeStatus.setText("Completed");
        }else{
            backgroundLayout.setBackgroundResource(R.drawable.user_task_background_notcompleted);


        }

        if(setDueDate.get(position)){
            timerLogo.setVisibility(View.VISIBLE);
            if(expireStatus.get(position) && !status.get(position)){
                backgroundLayout.setBackgroundResource(R.drawable.user_task_background_expired);
            }
        } else {
            timerLogo.setVisibility(View.INVISIBLE);
        }


        return convertView;
    }
}