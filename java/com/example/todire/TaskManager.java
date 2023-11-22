package com.example.todire;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.protobuf.Internal;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TaskManager {

    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;
    private Context context;
    private String currentUserID;

    public TaskManager(Context context) {
        this.context = context;
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        currentUserID = fAuth.getUid();

    }

    public static boolean isNetworkAvailable(@NonNull Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void createTask(String description, boolean setDueDate, String dueDate, onTaskCreatedListner listner){
        DocumentReference taskDocRef = fStore.collection("User_participation")
                .document(currentUserID)
                .collection("Tasks")
                .document();
        Map<String,Object> data = new HashMap<>();
        data.put("Description",description);
        data.put("Completed",false);
        data.put("SetDueDate", setDueDate);
         if(setDueDate){
             data.put("DueDate",dueDate);
         }

        if(isNetworkAvailable(context)){
            taskDocRef.set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        listner.onTaskCreated();
                    }else{
                        listner.onTaskFailed();
                    }
                }
            });
        }else{
            listner.onTaskFailed();
        }
    }

    public void getTasks(onGetTaskLisner lisner){


        ArrayList<String> descriptionArray = new ArrayList<>();
        ArrayList<Boolean> statusArray = new ArrayList<>();
        ArrayList<String> taskId = new ArrayList<>();
        ArrayList<Boolean> dueDateStatusArray = new ArrayList<>();
        ArrayList<String> dueDate = new ArrayList<>();
        ArrayList<Boolean> expireStatus = new ArrayList<>();

        CollectionReference taskColRef = fStore.collection("User_participation")
                .document(currentUserID)
                .collection("Tasks");

        taskColRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().isEmpty()){
                        lisner.onEmptyTask();
                    }else{
                        int totalDocuments = task.getResult().size();
                        int successCount =0;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> data = document.getData();
                            String description = (String) data.get("Description");
                            boolean status = (Boolean) data.get("Completed") ;
                            String id = (String) document.getId();
                            descriptionArray.add(description);
                            taskId.add(id);
                            statusArray.add(status);

                            Boolean dueDateStatus = (Boolean) data.get("SetDueDate");

                            if(dueDateStatus){
                                dueDateStatusArray.add(true);
                                String dueDateAsString = (String) data.get("DueDate");
                                dueDate.add(dueDateAsString);

                                TaskUtil.performComparison(dueDateAsString, new TaskUtil.DateTimeListener() {
                                    @Override
                                    public void onDateBeforeCurrent() {
                                        expireStatus.add(false);
                                    }

                                    @Override
                                    public void onDateAfterCurrent() {
                                        expireStatus.add(true);
                                    }

                                    @Override
                                    public void onDateComparisonError() {
                                        lisner.onGetTaskFailed();
                                    }
                                });


                            }else{
                                dueDateStatusArray.add(false);
                                dueDate.add("0");
                                expireStatus.add(false);
                            }


                            successCount++;
                            if(successCount == totalDocuments){
                                lisner.onGetTask(descriptionArray, taskId, statusArray, dueDateStatusArray,dueDate,expireStatus);
                            }
                        }
                        }

                }else{
                    lisner.onGetTaskFailed();
                }
            }
        });

    }


    public void deleteTask(String taskId, onTaskDeleteListner listner){
        DocumentReference taskDocRef = fStore.collection("User_participation")
                .document(currentUserID)
                .collection("Tasks")
                .document(taskId);
        if(isNetworkAvailable(context)){
        taskDocRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    listner.onTaskDeleted();
                }else{
                    listner.onTaskDeleteFailed();
                }
            }
        });
        }else{
            listner.onNetworkFaliure();
        }
    }

    public void completeTask(String taskId, onTaskCompleteListner listner){
        DocumentReference taskDocRef = fStore.collection("User_participation")
                .document(currentUserID)
                .collection("Tasks")
                .document(taskId);
        String fieldValue = "Completed";

        if(isNetworkAvailable(context)){

            taskDocRef.update(fieldValue,true).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    listner.onCompleted();
                }else{
                    listner.onCompleteFailed();
                }
                }
            });
        }else{
            listner.onNetworkFaliure();
        }
    }

    public interface onTaskCreatedListner{
        void onTaskCreated();
        void onTaskFailed();
    }

    public interface onGetTaskLisner{
        void onGetTask(ArrayList<String> description, ArrayList<String> taskId, ArrayList<Boolean> statusArray, ArrayList<Boolean> dueDateStatus, ArrayList<String> dueDateArray, ArrayList<Boolean> expireStatus);
        void onGetTaskFailed();
        void onEmptyTask();
    }

    public interface onTaskDeleteListner{
        void onTaskDeleted();
        void onTaskDeleteFailed();
        void onNetworkFaliure();
    }

    public interface onTaskCompleteListner{
        void onCompleted();
        void onCompleteFailed();
        void onNetworkFaliure();

    }
}
