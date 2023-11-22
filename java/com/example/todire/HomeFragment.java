package com.example.todire;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todire.listAdaptors.UserTaskListAdaptor;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class HomeFragment extends Fragment {


    ListView taskList;
    TextView emptyTaskText;
    TaskManager taskManager;
    ProgressBar progressBar;
    UserTaskListAdaptor userTaskListAdaptor;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        taskManager = new TaskManager(getContext());
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        taskList = (ListView) view.findViewById(R.id.task_list);
        progressBar = view.findViewById(R.id.progressBar_homefragment);
        emptyTaskText = view.findViewById(R.id.empty_task_text);

        taskManager.getTasks(new TaskManager.onGetTaskLisner() {
            @Override
            public void onGetTask(ArrayList<String> description, ArrayList<String> taskId, ArrayList<Boolean> statusArray, ArrayList<Boolean> dueDateStatus, ArrayList<String> dueDateArray, ArrayList<Boolean> expireStatus) {
               try {
                   userTaskListAdaptor = new UserTaskListAdaptor(getContext(),description,statusArray,dueDateStatus,expireStatus);
                   taskList.setAdapter(userTaskListAdaptor);
                   userTaskListAdaptor.notifyDataSetChanged();
                   progressBar.setVisibility(View.GONE);
                   taskList.setVisibility(View.VISIBLE);
                   emptyTaskText.setVisibility(View.GONE);

                   taskList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                       @Override
                       public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                           createDialogBox(taskId,description,statusArray,position,dueDateStatus,dueDateArray,expireStatus);

                       }
                   });


               }catch(Exception e){
                   if(getContext() != null){
                   Toast.makeText(getContext(),e.toString(),Toast.LENGTH_SHORT).show();}
                   progressBar.setVisibility(View.INVISIBLE);
               }

            }

            @Override
            public void onGetTaskFailed() {
                if(getContext() != null) {
                    Toast.makeText(getContext(), "Task Loading Failed", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onEmptyTask() {
                if(getContext() != null){
                    Toast.makeText(getContext(),"Empty Tasks",Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.INVISIBLE);
                emptyTaskText.setVisibility(View.VISIBLE);

            }
        });

        return view;
    }


    public void createDialogBox(ArrayList<String> taskId, ArrayList<String> description, ArrayList<Boolean> status, int position,ArrayList<Boolean> dueDateStatus, ArrayList<String> dueDateArray, ArrayList<Boolean> expireStatus){

        AlertDialog.Builder builder2 = new AlertDialog.Builder(getContext(),R.style.RoundedCornersDialog);
        View dialogView = getLayoutInflater().inflate(R.layout.task_dialogbox_layout, null);
        builder2.setView(dialogView).setCancelable(false);

        AlertDialog dialog = builder2.create();
        TextView massage = dialogView.findViewById(R.id.result_massage);
        TextView descriptionText = dialogView.findViewById(R.id.descriptionText);
        ProgressBar progressBar = dialogView.findViewById(R.id.dialog_progress_bar);
        View cancelBtn = dialogView.findViewById(R.id.cancel_dialog);
        Button completeBtn = dialogView.findViewById(R.id.complete_button);
        Button deleteBtn = dialogView.findViewById(R.id.delete_button);

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
                taskManager.completeTask(taskId.get(position), new TaskManager.onTaskCompleteListner() {
                    @Override
                    public void onCompleted() {
                        massage.setText("Task Completed");
                        massage.setVisibility(View.VISIBLE);
                        completeBtn.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        status.set(position,true);
                        userTaskListAdaptor.notifyDataSetChanged();


                    }

                    @Override
                    public void onCompleteFailed() {
                        massage.setText("Failed. Try Agian");
                        massage.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);

                    }

                    @Override
                    public void onNetworkFaliure() {
                        massage.setText("Connection Failed");
                        massage.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);

                    }
                });
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskManager.deleteTask(taskId.get(position), new TaskManager.onTaskDeleteListner() {
                    @Override
                    public void onTaskDeleted() {
                        massage.setText("Task Deleted");
                        massage.setVisibility(View.VISIBLE);
                        completeBtn.setVisibility(View.GONE);
                        deleteBtn.setVisibility(View.GONE);
                        clearData(position, description, taskId, status, dueDateStatus, dueDateArray, expireStatus, expireStatus);
                    }

                    @Override
                    public void onTaskDeleteFailed() {
                        massage.setText("Failed. Try Agian");
                        massage.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onNetworkFaliure() {
                        massage.setText("Connection Failed");
                    }
                });
            }
        });
        dialog.show();

    }

    private void clearData(int position, ArrayList<String> description, ArrayList<String> taskId, ArrayList<Boolean> statusArray, ArrayList<Boolean> dueDateStatus, ArrayList<String> dueDateArray, ArrayList<Boolean> expireStatus, ArrayList<Boolean> expireArray) {
        if (position < description.size()) {
            description.remove(position);
        }
        if (position < taskId.size()) {
            taskId.remove(position);
        }
        if (position < statusArray.size()) {
            statusArray.remove(position);
        }
        if (position < dueDateStatus.size()) {
            dueDateStatus.remove(position);
        }
        if (position < dueDateArray.size()) {
            dueDateArray.remove(position);
        }
        if (position < expireStatus.size()) {
            expireStatus.remove(position);
        }
        if (position < expireStatus.size()) {
            expireArray.remove(position);
        }
        userTaskListAdaptor.notifyDataSetChanged();
    }


}