package com.example.todire;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.todire.listAdaptors.GroupTaskListAdaptor;
import com.example.todire.listAdaptors.MemberListAdaptor;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Collections;

public class GroupInside extends AppCompatActivity {

    TextView emptyTaskText;
    ListView taskList;
    Toolbar toolbar;
    String groupId;
    String groupName;
    ArrayList<String> membersList;
    ArrayList<String> membersId;
    ArrayList<Boolean> listAdaptorUtil;
    boolean assignedTask = false;
    CustomProgressDialog customProgressDialog;
    GroupTaskManager groupTaskManager;
    GroupTaskListAdaptor groupTaskListAdaptor;
    GroupManager groupManager;
    FirebaseAuth fAuth;
    String currentUserId;

    boolean admin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_inside);

        Intent intent = getIntent();
        groupId = intent.getStringExtra("GroupId");
        groupName = intent.getStringExtra("GroupName");

        customProgressDialog = new CustomProgressDialog(GroupInside.this);

        fAuth = FirebaseAuth.getInstance();
        currentUserId = fAuth.getUid();
        membersList = new ArrayList<>();
        membersId = new ArrayList<>();

        groupManager = new GroupManager(GroupInside.this);
        groupTaskManager = new GroupTaskManager(GroupInside.this);

        if(!TaskManager.isNetworkAvailable(GroupInside.this)){
            customProgressDialog.showDialog("Connection Failed",true);
        }

        emptyTaskText = findViewById(R.id.empty_group_task_text);
        taskList = findViewById(R.id.group_task_list);
        toolbar = findViewById(R.id.group_inside_toolbar);
        toolbar.setTitle(groupName);
        toolbar.setSubtitle("ID : "+groupId);
        setSupportActionBar(toolbar);

        groupTaskManager.getGroupTask(groupId, new GroupTaskManager.onGetGrouptaskListner() {
            @Override
            public void onGetTask(ArrayList<String> descriptions, ArrayList<Boolean> assignStatus, ArrayList<String> assingnedUserId, ArrayList<String> assignedUserName, ArrayList<String> taskId,ArrayList<Boolean> taskStatus){
                Toast.makeText(GroupInside.this,"complete",Toast.LENGTH_SHORT).show();
                groupTaskListAdaptor = new GroupTaskListAdaptor(descriptions
                                                                                    ,assignStatus
                                                                                    ,assignedUserName
                                                                                    ,taskStatus
                                                                                    ,GroupInside.this);

                taskList.setAdapter(groupTaskListAdaptor);
                taskList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        completeDialogBox(taskId,descriptions,taskStatus,position,assignStatus,assingnedUserId,assignedUserName);
                    }
                });

            }

            @Override
            public void onGetTaskFailed() {
                Toast.makeText(GroupInside.this,"faled",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onEmptytask() {
                Toast.makeText(GroupInside.this,"empty tasks",Toast.LENGTH_SHORT).show();
                taskList.setVisibility(View.GONE);
                emptyTaskText.setVisibility(View.VISIBLE);
            }
        });

        customProgressDialog.showDialog("Please wait...",false);
        groupManager.getMemberList(groupId, new GroupManager.OnGetMemberListner() {
            @Override
            public void onSuccessful(ArrayList<String> members, ArrayList<String> memberId) {
                membersList = members;
                membersId = memberId;
                listAdaptorUtil = new ArrayList<>(Collections.nCopies(membersList.size(), false));
                customProgressDialog.hideDialog();
            }

            @Override
            public void onFailiure() {
                Toast.makeText(GroupInside.this,"Loading Failed, Please Refresh the Page",Toast.LENGTH_SHORT).show();
                customProgressDialog.hideDialog();

            }

            @Override
            public void onEmptyMembers() {
                customProgressDialog.hideDialog();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.group_inside_toolbar_menu,menu);

        MenuItem menuItem1 = menu.findItem(R.id.create_group_task);
        MenuItem menuItem2 = menu.findItem(R.id.delete_group);
        MenuItem menuItem3 = menu.findItem(R.id.share_group_id);

        if (menuItem1 != null && menuItem2 != null) {
            menuItem1.setEnabled(false);
            menuItem1.setVisible(false);
        }

        menuItem3.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                shareLink(groupId);
                return true;
            }
        });


        groupManager.AdminMemberDirect(groupId, new GroupManager.OnDrectionListner() {
            @Override
            public void OnDirectionFailed() {
                Toast.makeText(GroupInside.this, "Loading Failed. Please Refresh the Page",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void OnAdminDirection() {
                menuItem1.setEnabled(true);
                menuItem1.setVisible(true);
                menuItem2.setIcon(R.drawable.delete);
                admin = true;

                menuItem2.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(@NonNull MenuItem item) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(GroupInside.this);
                        builder.setTitle("Confirmation");
                        builder.setMessage("Are you sure you want to Delete");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                customProgressDialog.showDialog("Please Wait...",false);

                                groupManager.deleteGroups(groupId, new GroupManager.OnGroupDeleteListner() {
                                    @Override
                                    public void onDeleted() {
                                        customProgressDialog.hideDialog();
                                        Toast.makeText(GroupInside.this,"Success",Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(GroupInside.this,HomePage.class);
                                        startActivity(intent);
                                        finish();
                                    }

                                    @Override
                                    public void onDeleteFailed() {
                                        customProgressDialog.hideDialog();
                                        Toast.makeText(GroupInside.this,"Failed. try again",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();

                        return true;
                    }
                });


            }

            @Override
            public void OnMemberDirection() {
                    menuItem2.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(@NonNull MenuItem item) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(GroupInside.this);
                            builder.setTitle("Confirmation");
                            builder.setMessage("Are you sure you want to leave?");
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    customProgressDialog.showDialog("Please Wait...",false);

                                    groupManager.leavegroup(groupId, new GroupManager.onGroupLeaveListner() {
                                        @Override
                                        public void onLeave() {
                                            customProgressDialog.hideDialog();
                                            Toast.makeText(GroupInside.this,"Success",Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(GroupInside.this,HomePage.class);
                                            startActivity(intent);
                                            finish();

                                        }

                                        @Override
                                        public void onLeaveFailed() {
                                            customProgressDialog.hideDialog();
                                            Toast.makeText(GroupInside.this,"Failed. try again",Toast.LENGTH_SHORT).show();

                                        }

                                        @Override
                                        public void onNetworkFailiure() {
                                            customProgressDialog.hideDialog();
                                            Toast.makeText(GroupInside.this,"Connection Failed",Toast.LENGTH_SHORT).show();

                                        }
                                    });
                                }
                            });
                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();


                            return true;
                        }
                    });
            }
        });


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.create_group_task:
                createDialogBox();
                break;
        }

            return true;
    }


    public void createDialogBox(){



        AlertDialog.Builder builder2 = new AlertDialog.Builder(GroupInside.this);
        View dialogView = getLayoutInflater().inflate(R.layout.group_task_dialog_box, null);
        builder2.setView(dialogView).setCancelable(false);

        AlertDialog dialog = builder2.create();
        EditText descriptionText = dialogView.findViewById(R.id.group_task_description);
        View cancelBtn = dialogView.findViewById(R.id.cancel_create_dialog);
        Button createBtn = dialogView.findViewById(R.id.create_group_taskbtn);
        ListView memberList = dialogView.findViewById(R.id.group_member_list);
        Switch assignMember = dialogView.findViewById(R.id.assign_member_switch);

        if(membersList.size()<2){
            assignMember.setEnabled(false);
        }

        MemberListAdaptor memberListAdaptor = new MemberListAdaptor(membersList,membersId, listAdaptorUtil,GroupInside.this);
        memberList.setAdapter(memberListAdaptor);

        memberList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                     clearSelectedMember();
                     listAdaptorUtil.set(position,true);
                     memberListAdaptor.notifyDataSetChanged();


            }
        });

        dialog.show();

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String descritpion = descriptionText.getText().toString();
                if(TextUtils.isEmpty(descritpion)){
                    descriptionText.setError("Please Enter Task Description");
                    return;
                }

                customProgressDialog.showDialog("Please wait...", false);

                GroupTaskManager groupTaskManager = new GroupTaskManager(GroupInside.this);

                String selectedMemberId = "0";
                if(assignedTask){
                    int trueIndex = listAdaptorUtil.indexOf(true);
                    selectedMemberId = membersId.get(trueIndex);
                }

                groupTaskManager.createTask(descritpion, groupId,assignedTask,selectedMemberId , new TaskManager.onTaskCreatedListner() {
                    @Override
                    public void onTaskCreated() {
                        Toast.makeText(GroupInside.this,"created",Toast.LENGTH_SHORT).show();
                        customProgressDialog.hideDialog();
                        descriptionText.setText("");
                        assignedTask = false;
                        dialog.dismiss();


                    }

                    @Override
                    public void onTaskFailed() {
                        Toast.makeText(GroupInside.this,"failed, Try Again",Toast.LENGTH_SHORT).show();
                        customProgressDialog.hideDialog();
                    }
                });

            }
        });


            assignMember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        memberList.setVisibility(View.VISIBLE);
                        assignedTask = true;

                    } else {
                        memberList.setVisibility(View.GONE);
                        assignedTask = false;

                        if (listAdaptorUtil != null) {
                            clearSelectedMember();
                            memberListAdaptor.notifyDataSetChanged();
                        }
                    }
                }
            });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        dialog.show();

    }


    private void clearSelectedMember(){
        int trueIndex = listAdaptorUtil.indexOf(true);
        // Changing the true value to false
        if (trueIndex != -1) {
            listAdaptorUtil.set(trueIndex, false);
        }
    }


    public void completeDialogBox(ArrayList<String> taskId,
                                  ArrayList<String> description,
                                  ArrayList<Boolean> status,
                                  int position,
                                  ArrayList<Boolean> assignStatus,
                                  ArrayList<String> assingnedUserId,
                                  ArrayList<String> assignedUserName){

        if (position >= 0 && position < taskId.size() && position < description.size() && position < status.size() &&
                position < assignStatus.size() && position < assingnedUserId.size() && position < assignedUserName.size()) {

            if (assignStatus.get(position)) {
                if (!assingnedUserId.get(position).equals(currentUserId) && !admin) {
                    customProgressDialog.showDialog("Task is Assigned to another member", true);
                    return;
                }
            }
        }

        AlertDialog.Builder builder2 = new AlertDialog.Builder(GroupInside.this,R.style.RoundedCornersDialog);
        View dialogView = getLayoutInflater().inflate(R.layout.task_dialogbox_layout, null);
        builder2.setView(dialogView).setCancelable(false);

        AlertDialog dialog = builder2.create();
        TextView massage = dialogView.findViewById(R.id.result_massage);
        TextView descriptionText = dialogView.findViewById(R.id.descriptionText);
        ProgressBar progressBar = dialogView.findViewById(R.id.dialog_progress_bar);
        View cancelBtn = dialogView.findViewById(R.id.cancel_dialog);
        Button completeBtn = dialogView.findViewById(R.id.complete_button);
        Button deleteBtn = dialogView.findViewById(R.id.delete_button);


        if (position >= 0 && position < taskId.size() && position < description.size() && position < status.size() &&
                position < assignStatus.size() && position < assingnedUserId.size() && position < assignedUserName.size()) {

            if (assignStatus.get(position)) {
                if (!assingnedUserId.get(position).equals(currentUserId) && !admin) {
                    customProgressDialog.showDialog("Task is Assigned to another member", true);
                    return;
                }
            }
        }

        if(!admin){
            deleteBtn.setVisibility(View.GONE);
        }


        if(status.get(position)){
            completeBtn.setVisibility(View.GONE);
        }

        dialog.show();




        descriptionText.setText(description.get(position));

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        completeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                groupTaskManager.completeGroupTask(groupId, taskId.get(position), new TaskManager.onTaskCompleteListner() {
                    @Override
                    public void onCompleted() {
                        massage.setText("Task Completed");
                        status.set(position,true);
                        groupTaskListAdaptor.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                        massage.setVisibility(View.VISIBLE);
                        completeBtn.setVisibility(View.GONE);

                    }

                    @Override
                    public void onCompleteFailed() {
                        massage.setText("Failed. Try again");
                        progressBar.setVisibility(View.GONE);
                        massage.setVisibility(View.VISIBLE);


                    }

                    @Override
                    public void onNetworkFaliure() {
                        massage.setText("Connection failed");
                        progressBar.setVisibility(View.GONE);
                        massage.setVisibility(View.VISIBLE);


                    }
                });


            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                groupTaskManager.deleteGroupTask(groupId, taskId.get(position), new TaskManager.onTaskDeleteListner() {
                    @Override
                    public void onTaskDeleted() {
                        massage.setText("Task Deleted");
                        status.set(position,true);
                        groupTaskListAdaptor.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                        massage.setVisibility(View.VISIBLE);
                        clearData(position,description,assignStatus,assingnedUserId,assignedUserName,taskId,status);
                        groupTaskListAdaptor.notifyDataSetChanged();
                        deleteBtn.setVisibility(View.GONE);
                        completeBtn.setVisibility(View.GONE);
                    }

                    @Override
                    public void onTaskDeleteFailed() {
                        massage.setText("Failed. Try again");
                        progressBar.setVisibility(View.GONE);
                        massage.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onNetworkFaliure() {
                        massage.setText("Connection failed");
                        progressBar.setVisibility(View.GONE);
                        massage.setVisibility(View.VISIBLE);
                    }
                });

            }
        });
        dialog.show();

    }

    private void clearData(int position,
                           ArrayList<String> descriptions,
                           ArrayList<Boolean> assignStatus,
                           ArrayList<String> assingnedUserId,
                           ArrayList<String> assignedUserName,
                           ArrayList<String> taskId,
                           ArrayList<Boolean> taskStatus
                           ) {

        if (position < descriptions.size()) {
            descriptions.remove(position);
        }
        if (position < taskId.size()) {
            taskId.remove(position);
        }
        if (position < taskStatus.size()) {
            taskStatus.remove(position);
        }
        if (position < assignStatus.size()) {
            assignStatus.remove(position);
        }
        if (position < assingnedUserId.size()) {
            assingnedUserId.remove(position);
        }
        if (position < assignedUserName.size()) {
            assignedUserName.remove(position);
        }

        groupTaskListAdaptor.notifyDataSetChanged();
    }

    private void shareLink(String groupId) {

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Join our group with this ID: " + groupId);

        startActivity(Intent.createChooser(shareIntent, "Share Group"));
    }


}