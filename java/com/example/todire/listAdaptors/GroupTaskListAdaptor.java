package com.example.todire.listAdaptors;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.todire.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class GroupTaskListAdaptor extends BaseAdapter {

    ArrayList<String> description;
    ArrayList<Boolean> assignStatus ;
    ArrayList<String> assignUserNames;
    ArrayList<Boolean> taskStatus;
    LayoutInflater inflater;
    Context context;


    public GroupTaskListAdaptor(ArrayList<String> description,
                                ArrayList<Boolean> assignStatus
                                ,ArrayList<String> assignUserNames
                                ,ArrayList<Boolean> taskStatus
                                ,Context context) {
        this.description = description;
        this.assignStatus = assignStatus;
        this.assignUserNames = assignUserNames;
        this.taskStatus = taskStatus;
        this.context = context;
        inflater = LayoutInflater.from(context);

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

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.group_task_list_adaptor, null);
        TextView descriptionText = (TextView) convertView.findViewById(R.id.description2);
        TextView completeStatus = (TextView) convertView.findViewById(R.id.task_status2);
        TextView assignedStatus = (TextView) convertView.findViewById(R.id.assign_text) ;
        LinearLayout backgroundLayout = (LinearLayout) convertView.findViewById(R.id.background_layout2);
        descriptionText.setText(description.get(position));



        if(taskStatus.get(position)){
            backgroundLayout.setBackgroundResource(R.drawable.user_task_background_completed);
            completeStatus.setText("Completed");
        }else{
            backgroundLayout.setBackgroundResource(R.drawable.user_task_background_notcompleted);
        }

        if(assignStatus.get(position)){
            assignedStatus.setText("Assigned : "+assignUserNames.get(position).toString());
        }else{
            assignedStatus.setText("Not Assigned");
        }



        return convertView;
    }
}
