package com.example.todire;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class GroupTaskManager {

    Context context;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String currentUserID;
    int totalCount = 0; // Create a counter to keep track of the number of asynchronous calls

    public GroupTaskManager(Context context) {
        this.context = context;
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        currentUserID = fAuth.getUid();
    }


    public void createTask(String description, String groupId, boolean assignStatus, String assignId, TaskManager.onTaskCreatedListner listner){
        DocumentReference taskDocref = fStore.collection("Groups").document(groupId).collection("Tasks").document();

        Map<String, Object> taskData = new HashMap<>();
        taskData.put("Description", description);
        taskData.put("Assigned", assignStatus);
        taskData.put("Completed",false);
        if(assignStatus){
            taskData.put("AssignedTo", assignId);
        }

        taskDocref.set(taskData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    listner.onTaskCreated();
                }else{
                    listner.onTaskFailed();
                }
            }
        });
    }





 //  method destroy the the order of the arrays due to asynchronous data retrieving  (solution has already implemented)
    public void getGroupTask(String groupId, onGetGrouptaskListner listner) {
        CollectionReference taskColRef = fStore.collection("Groups").document(groupId).collection("Tasks");
        CollectionReference membercolRef = fStore.collection("Groups").document(groupId).collection("members");

        ArrayList<String> descriptions = new ArrayList<>();
        ArrayList<Boolean> assignStatus = new ArrayList<>();
        ArrayList<String> assingnedUserId = new ArrayList<>();
        ArrayList<String> assignedUserName = new ArrayList<>();
        ArrayList<String> taskId = new ArrayList<>();
        ArrayList<Boolean> taskStatus = new ArrayList<>();



        taskColRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().isEmpty()) {
                        listner.onEmptytask();
                    } else {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String documentId = document.getId();
                            Map<String, Object> data = document.getData();
                            String description = (String) data.get("Description");
                            Boolean assignedStatus = (boolean) data.get("Assigned");
                            Boolean status = (boolean) data.get("Completed");
                            taskId.add(documentId);
                            assignStatus.add(assignedStatus);
                            descriptions.add(description);
                            taskStatus.add(status);

                            if (assignedStatus) {
                                String assignedId = (String) data.get("AssignedTo");
                                assingnedUserId.add(assignedId);
                                assignedUserName.add("0");
                                DocumentReference doc = membercolRef.document(assignedId);
                                int arrayPosition = assignedUserName.size()-1;
                                totalCount++; // Increment the counter

                                doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> userTask) {
                                        if (userTask.isSuccessful()) {
                                            if (userTask.getResult().exists()) {
                                                DocumentSnapshot documentSnapshot = userTask.getResult();
                                                String userName = documentSnapshot.getString("username");
                                                assignedUserName.set(arrayPosition,userName);
                                            } else {
                                                assignedUserName.set(arrayPosition,"left");
                                            }
                                        } else {
                                            listner.onGetTaskFailed();
                                        }

                                        totalCount--; // Decrement the counter

                                        if (totalCount == 0 && descriptions.size()==assignedUserName.size()) {
                                            listner.onGetTask(descriptions, assignStatus, assingnedUserId, assignedUserName, taskId, taskStatus);
                                        }
                                    }
                                });
                            } else {
                                assignedUserName.add("0");
                                assingnedUserId.add("0");
                            }
                        }
                        if (totalCount == 0) {
                            listner.onGetTask(descriptions, assignStatus, assingnedUserId, assignedUserName, taskId, taskStatus);
                        }
                    }
                } else {
                    listner.onGetTaskFailed();
                }
            }
        });
    }

    public void completeGroupTask(String groupId, String taskId, TaskManager.onTaskCompleteListner listner){

        if(!TaskManager.isNetworkAvailable(context)){
            listner.onNetworkFaliure();
        }
        DocumentReference taskDocRef = fStore.collection("Groups").document(groupId).collection("Tasks").document(taskId);
        String fieldName = "Completed";
        taskDocRef.update(fieldName,true).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    listner.onCompleted();
                }else{
                    listner.onCompleteFailed();
                }
            }
        });

    }

    public void deleteGroupTask(String groupId, String taskId, TaskManager.onTaskDeleteListner listner){

        if(!TaskManager.isNetworkAvailable(context)){
            listner.onNetworkFaliure();
        }
        DocumentReference taskDocRef = fStore.collection("Groups").document(groupId).collection("Tasks").document(taskId);
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
    }


    public interface onGetGrouptaskListner{
        void onGetTask(
        ArrayList<String> descriptions,
        ArrayList<Boolean> assignStatus,
        ArrayList<String> assingnedUserId,
        ArrayList<String> assignedUserName,
        ArrayList<String> taskId,
        ArrayList<Boolean> taskStatus);
        void onGetTaskFailed();
        void onEmptytask();
    }
}
