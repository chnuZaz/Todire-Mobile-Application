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





public class TaskListAdaptor extends BaseAdapter {

    ArrayList<String> task;
    ArrayList<Boolean> status;
    LayoutInflater inflater;
    Context context;
    public TaskListAdaptor(Context context, ArrayList<String> arr , ArrayList<Boolean> status){

        if (context != null) {
            this.context = context;
            this.task = arr;
            this.status = status;
            inflater = LayoutInflater.from(context);
        } else {
            throw new IllegalArgumentException("Context cannot be null.");
        }

    }

    @Override
    public int getCount() {
        return task.size();
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
        convertView = inflater.inflate(R.layout.task_list_adaptor, null);
        TextView textView = (TextView) convertView.findViewById(R.id.textview);
        textView.setText(task.get(position));


       /* if (status.get(position)) {
            textView.setTextColor("#556644"); // Use your custom color resource
        } else {
            textView.setTextColor("#556622"); // Use your custom color resource
        }*/


        return convertView;
    }
}
