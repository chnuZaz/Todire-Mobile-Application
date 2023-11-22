package com.example.todire;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todire.listAdaptors.CustomAdaptor;

import java.util.ArrayList;


public class GroupFragment extends Fragment {


    Button joinBtn;
    EditText groupID;
    ListView groupList;
    TextView emptyGroups;
    GroupManager groupManager;
    ProgressBar progressBar;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_group, container, false);

        groupManager = new GroupManager(getContext());

        joinBtn = view.findViewById(R.id.join_btn);
        groupID = view.findViewById(R.id.group_id);
        groupList = view.findViewById(R.id.group_list);
        progressBar = view.findViewById(R.id.group_list_progressbar);
        progressBar.setVisibility(View.VISIBLE);
        emptyGroups = view.findViewById(R.id.emptt_groups);

        groupManager.getUserGroups(new GroupManager.OnGroupsLoadedListener() {
            @Override
            public void onGroupsLoaded(ArrayList<String> groups, ArrayList<String> documentid, ArrayList<String> AdminName) {
               try {
                   CustomAdaptor customAdaptor = new CustomAdaptor(getContext(), groups, AdminName);
                   groupList.setAdapter(customAdaptor);
                   progressBar.setVisibility(View.GONE);
                   groupList.setVisibility(View.VISIBLE);

                   groupList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                       @Override
                       public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                           Intent intent = new Intent(getContext(), GroupInside.class);
                           intent.putExtra("GroupId", documentid.get(position));
                           intent.putExtra("GroupName",groups.get(position));
                           startActivity(intent);
                       }
                   });


               }catch (Exception e){
                   groupList.setVisibility(View.GONE);
                   emptyGroups.setText("Loading Failed");
                   emptyGroups.setVisibility(View.VISIBLE);
               }

            }

            @Override
            public void onGroupsLoadFailed() {
                groupList.setVisibility(View.GONE);
                emptyGroups.setText("Loading Failed");
                emptyGroups.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void emptyGroups() {
                groupList.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                emptyGroups.setVisibility(View.VISIBLE);
            }
        });


       joinBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               String groupId = groupID.getText().toString();
               if(TextUtils.isEmpty(groupId)){
                   groupID.setError("Please Enter Group ID");
                   return;
               }
               groupManager.joinGroup(groupId, new GroupManager.OnGroupJoinListner() {
                   @Override
                   public void OnGroupJoined() {
                       Toast.makeText(getContext(),"Joined", Toast.LENGTH_SHORT).show();
                   }

                   @Override
                   public void OnGroupJoinFailed() {
                       Toast.makeText(getContext(), "Joining Failed", Toast.LENGTH_SHORT).show();
                   }

                   @Override
                   public void OnGroupNotExists() {
                       Toast.makeText(getContext(), "Group Does not Exists", Toast.LENGTH_SHORT).show();

                   }

                   @Override
                   public void OnAlradyGroupMember() {
                       Toast.makeText(getContext(), "Alrady a Member of the Group", Toast.LENGTH_SHORT).show();
                   }
               });
           }
       });
        return view;

    }
}