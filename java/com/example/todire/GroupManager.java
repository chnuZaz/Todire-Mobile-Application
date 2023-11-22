package com.example.todire;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GroupManager {

    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;
    private Context context;
    String username;  // used in joingroup  and create group methods


    private CollectionReference groupColRef;
    String currentUserID;
    int successCount = 0;  // this variable for ensure that all the document are fetched from the collection in the getUserGroups method

    public GroupManager(Context context) {
        this.context = context;
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        currentUserID = fAuth.getUid();
        groupColRef = fStore.collection("Groups");
    }



    public void createGroup(String groupName, OnGroupCreationListner listner){



        String CustomID = CustomIdGenerator.generateCustomId(8);
        DocumentReference newDocRef = groupColRef.document(CustomID);   // newDocRef is created for each group

        Map<String, Object> groupData = new HashMap<>();
        groupData.put("Group_Name",groupName);
        groupData.put("AdminID",currentUserID);

        newDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
              if(task.isSuccessful()){
                  DocumentSnapshot document = task.getResult();
                  if(document.exists()){

                      createGroup(groupName,listner);
                  }else{

                      DocumentReference userDocRef = fStore.collection("users").document(currentUserID);

                      userDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                          @Override
                          public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                              if(task.isSuccessful()){
                                  DocumentSnapshot document = task.getResult();
                                  if(document.exists()){
                                      username = document.getString("FirstName");

                                  }else{
                                      listner.OnGroupCreationFailed();
                                  }
                              }else {
                                  listner.OnGroupCreationFailed();
                              }
                          }
                      });


                      newDocRef.set(groupData).addOnCompleteListener(new OnCompleteListener<Void>() {
                          @Override
                          public void onComplete(@NonNull Task<Void> task) {
                              if(task.isSuccessful()) {
                                  // Saving Group date

                                  DocumentReference groupDatacolRef = newDocRef.collection("members").document(currentUserID);

                                  Map<String, Object> groupUser = new HashMap<>();
                                  groupUser.put("username", username);
                                  String GroupID = newDocRef.getId();

                                  groupDatacolRef.set(groupUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                      @Override
                                      public void onComplete(@NonNull Task<Void> task) {
                                          if (task.isSuccessful()) {
                                              CollectionReference UserGroupColRef = fStore.collection("User_participation").document(currentUserID).collection("Groups");
                                              DocumentReference g_doc = UserGroupColRef.document(GroupID);
                                              Map<String, Object> userdata = new HashMap<>();
                                              userdata.put("Group_Name", groupName);
                                              g_doc.set(userdata).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                  @Override
                                                  public void onComplete(@NonNull Task<Void> task) {
                                                      if (task.isSuccessful()) {
                                                          listner.OnGroupCreated();
                                                      } else {
                                                          listner.OnGroupCreationFailed();
                                                      }
                                                  }
                                              });
                                          } else {
                                              listner.OnGroupCreationFailed();
                                          }

                                      }
                                  });


                              }else {
                                  listner.OnGroupCreationFailed();
                              }
                          }
                      });
                  }
              }else {
                  listner.OnGroupCreationFailed();
              }
            }
        });

    }


    public void joinGroup(String GroupID, OnGroupJoinListner listener) {
        DocumentReference groupDocRef = groupColRef.document(GroupID);
        DocumentReference memberRef = groupDocRef.collection("members").document(currentUserID);
        DocumentReference userDocRef = fStore.collection("users").document(currentUserID);

        userDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        username = document.getString("FirstName");
                        groupDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> groupTask) {
                                if (groupTask.isSuccessful()) {
                                    DocumentSnapshot groupDoc = groupTask.getResult();
                                    if (groupDoc.exists()) {
                                        memberRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> memberTask) {
                                                if (memberTask.isSuccessful()) {
                                                    DocumentSnapshot memberDoc = memberTask.getResult();
                                                    if (memberDoc.exists()) {
                                                        listener.OnAlradyGroupMember();
                                                    } else {
                                                        String groupName = groupDoc.getString("Group_Name");

                                                        Map<String, Object> newUser = new HashMap<>();
                                                        newUser.put("username", username);

                                                        memberRef.set(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> memberSetTask) {
                                                                if (memberSetTask.isSuccessful()) {
                                                                    CollectionReference userGroupColRef = fStore.collection("User_participation")
                                                                            .document(currentUserID).collection("Groups");
                                                                    DocumentReference docRef = userGroupColRef.document(GroupID);

                                                                    Map<String, Object> userData = new HashMap<>();
                                                                    userData.put("Group_Name", groupName);

                                                                    docRef.set(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> docSetTask) {
                                                                            if (docSetTask.isSuccessful()) {
                                                                                listener.OnGroupJoined();
                                                                            } else {
                                                                                listener.OnGroupJoinFailed();
                                                                            }
                                                                        }
                                                                    });
                                                                } else {
                                                                    listener.OnGroupJoinFailed();
                                                                }
                                                            }
                                                        });
                                                    }
                                                } else {
                                                    listener.OnGroupJoinFailed();
                                                }
                                            }
                                        });
                                    } else {
                                        listener.OnGroupNotExists();
                                    }
                                } else {
                                    listener.OnGroupJoinFailed();
                                }
                            }
                        });
                    } else {
                        listener.OnGroupJoinFailed();
                    }
                } else {
                    listener.OnGroupJoinFailed();
                }
            }
        });
    }

    public void getUserGroups(final OnGroupsLoadedListener listner){
        ArrayList<String> gNames = new ArrayList<String>();
        ArrayList<String> documentid = new ArrayList<String>();
        ArrayList<String> adminName = new ArrayList<>();
        CollectionReference UserGroupColRef = fStore.collection("User_participation").document(currentUserID).collection("Groups");
        UserGroupColRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().isEmpty()){
                        listner.emptyGroups();
                    }
                    int totalDocuments = task.getResult().size();


                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Accessing the groups and admins data
                        String documentId = document.getId();
                        Map<String, Object> data = document.getData();

                        String groupName = (String) data.get("Group_Name");
                         gNames.add(groupName);
                         documentid.add(documentId);
                         DocumentReference groupdocRef = fStore.collection("Groups").document(documentId);
                         groupdocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                             @Override
                             public void onSuccess(DocumentSnapshot documentSnapshot) {
                                 if(documentSnapshot.exists()){
                                     String id =  documentSnapshot.getString("AdminID");
                                     DocumentReference usersdocRef = fStore.collection("users").document(id);
                                     usersdocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                         @Override
                                         public void onSuccess(DocumentSnapshot documentSnapshot) {
                                             successCount++;
                                             if(documentSnapshot.exists()){
                                                 adminName.add(documentSnapshot.getString("FirstName"));
                                                 if (successCount == totalDocuments) {
                                                     listner.onGroupsLoaded(gNames, documentid, adminName);
                                                 }
                                             }
                                             else{
                                                 listner.onGroupsLoadFailed();
                                             }
                                         }
                                     });

                                 }
                                 else{
                                     listner.onGroupsLoadFailed();
                                 }
                             }
                         });


                  }

              }
                else{
                    listner.onGroupsLoadFailed();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listner.onGroupsLoadFailed();
            }
        });

    }

    public void getMemberList(String groupId, OnGetMemberListner listner){
        CollectionReference memberColref = groupColRef.document(groupId).collection("members");

        ArrayList<String> memberNames = new ArrayList<>();
        ArrayList<String> membersId = new ArrayList<>();
        memberColref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    int totalDocuments = task.getResult().size();
                    int successCount = 0;
                    if(task.getResult().isEmpty() || totalDocuments==1){
                        listner.onEmptyMembers();
                    }else{


                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Accessing the groups and admins data
                            String documentId = document.getId();
                            Map<String, Object> data = document.getData();
                            String memberName = (String) data.get("username");
                            memberNames.add(memberName);
                            membersId.add(documentId);

                            successCount++;

                            if(successCount == totalDocuments){  //ensuring all the documents are added to the list
                                listner.onSuccessful(memberNames, membersId);
                            }
                        }

                    }
                }else{
                    listner.onFailiure();
                }
            }
        });
    }



    public void AdminMemberDirect(String groupId, GroupManager.OnDrectionListner listner){

        DocumentReference groupdocRef = fStore.collection("Groups").document(groupId);
        groupdocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    String adminId = documentSnapshot.getString("AdminID");

                    if(adminId.equals(currentUserID)){
                        listner.OnAdminDirection();

                    } else  {
                        listner.OnMemberDirection();
                    }

                }else{
                    listner.OnDirectionFailed();
                }
            }
        });


    }



    public void deleteGroups(String groupId, OnGroupDeleteListner listner){

        DocumentReference groupdocRef = fStore.collection("Groups").document(groupId);

        CollectionReference memberColRef = fStore.collection("Groups").document(groupId).collection("members");
        memberColRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().isEmpty()){
                        listner.onDeleteFailed();

                    }else{
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String userId = document.getId();
                        DocumentReference usersdocRef = fStore.collection("User_participation").document(userId).collection("Groups").document(groupId);
                        usersdocRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){

                                }else {
                                    listner.onDeleteFailed();
                                }
                            }
                        });

                    }
                    groupdocRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                listner.onDeleted();
                            }else{
                                listner.onDeleteFailed();
                            }
                        }
                    });

                    }
            }else{
                listner.onDeleteFailed();}
            }
        });


    }

    public void leavegroup(String groupId, onGroupLeaveListner listner){
        DocumentReference memberDocRef = fStore.collection("Groups").document(groupId).collection("members").document(currentUserID);
        DocumentReference usersdocRef = fStore.collection("User_participation").document(currentUserID).collection("Groups").document(groupId);

        if(!TaskManager.isNetworkAvailable(context)){
            listner.onNetworkFailiure();
        }
        memberDocRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    usersdocRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                listner.onLeave();
                            }else{
                                listner.onLeaveFailed();
                            }
                        }
                    });
                }   else{
                    listner.onLeaveFailed();
                }
            }
        });
    }




    public interface OnGroupCreationListner{

        void OnGroupCreated();
        void OnGroupCreationFailed();

    }

    public interface OnGroupJoinListner{
        void OnGroupJoined();
        void OnGroupJoinFailed();

        void OnGroupNotExists();
        void OnAlradyGroupMember();
    }

    // Define an interface for the callback
    public interface OnGroupsLoadedListener {
        void onGroupsLoaded(ArrayList<String> groups, ArrayList<String> documentid, ArrayList<String> AdminName);
        void onGroupsLoadFailed();
        void emptyGroups();
    }

    public interface OnGroupDeleteListner {
        void onDeleted();
        void onDeleteFailed();
    }

    public interface OnGetMemberListner{
        void onSuccessful(ArrayList<String> members, ArrayList<String> memberId);
        void onFailiure();
        void onEmptyMembers();
    }

    public interface OnDrectionListner{
        void OnDirectionFailed();
        void OnAdminDirection();
        void OnMemberDirection();
    }

    public interface onGroupLeaveListner{
        void onLeave();
        void onLeaveFailed();
        void onNetworkFailiure();
    }
}


