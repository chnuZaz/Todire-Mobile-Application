package com.example.todire.listAdaptors;

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

public class MemberListAdaptor extends BaseAdapter {

    ArrayList<String> members;
    ArrayList<String> memberId;
    ArrayList<Boolean> selectedStatus;
    LayoutInflater inflater;
    Context context;



    public MemberListAdaptor( ArrayList<String> members,ArrayList<String> memberId, ArrayList<Boolean> selectedStatus, Context context) {
        this.members = members;
        this.memberId = memberId;
        this.context = context;
        this.selectedStatus = selectedStatus;
        inflater = LayoutInflater.from(context);

    }


    @Override
    public int getCount() {
        return members.size();
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
        convertView = inflater.inflate(R.layout.member_list_adaptor, null);
        TextView username = (TextView) convertView.findViewById(R.id.user_name);
        LinearLayout background = (LinearLayout) convertView.findViewById(R.id.background_memberlist);
        username.setText(members.get(position));

        background.setBackgroundResource(R.drawable.user_task_background_notcompleted);
        if(selectedStatus.get(position)){
            background.setBackgroundResource(R.drawable.user_task_background_completed);
        }
        return convertView;
    }
}
