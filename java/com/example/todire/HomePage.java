package com.example.todire;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class HomePage extends AppCompatActivity {

    Toolbar toolbar;
    GroupManager groupManager;
    private MenuItem loadingMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        BottomNavigationView navigationView = findViewById(R.id.navigation_bar);
        replacefragment(new HomeFragment());

        toolbar = findViewById(R.id.top_toolbar);
        setSupportActionBar(toolbar);

        groupManager = new GroupManager(HomePage.this);


        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                switch (id){
                    case R.id.nav_home:
                        replacefragment(new HomeFragment());
                        toolbar.setTitle("My Tasks");
                        return true;
                    case R.id.nav_groups:
                        replacefragment(new GroupFragment());
                        toolbar.setTitle("My Groups");
                        return true;
                    case R.id.nav_create:
                        replacefragment(new CreateFragment());
                        toolbar.setTitle("Create");
                        return true;



                }
                return false;
            }
        });

    }


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.group_toolbar_menu, menu);
        loadingMenuItem = menu.findItem(R.id.loading_progress);

        return true;
    }


        @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.logout_menu:
               new AlertDialog.Builder(HomePage.this)
                                .setMessage("Are you sure want to logout")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        logout();
                                        finish();
                                    }
                                })
                                .setNegativeButton("No",null)
                                .show();
                        return true;

            case R.id.create_group2:
                AlertDialog.Builder builder = new AlertDialog.Builder(HomePage.this);
                builder.setTitle("Enter Group Name");

                // Set up the input
                final EditText input = new EditText(HomePage.this);
                // Specify the type of input expected
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showProgressBar();
                        String gname = input.getText().toString();

                        groupManager.createGroup(gname, new GroupManager.OnGroupCreationListner() {
                            @Override
                            public void OnGroupCreated() {
                                Toast.makeText(HomePage.this,"Group Created",Toast.LENGTH_SHORT).show();
                                hideProgressBar();
                            }

                            @Override
                            public void OnGroupCreationFailed() {
                                Toast.makeText(HomePage.this,"Group Creation Failed",Toast.LENGTH_SHORT).show();
                                hideProgressBar();

                            }
                        });
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();



        }

        return true;
    }

    public void logout(){
        FirebaseAuth.getInstance().signOut(); //logout
        Intent intent = new Intent(HomePage.this,LoginPage.class);
        startActivity(intent);
    }

    private void replacefragment(Fragment fragment){

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_frame,fragment);
        fragmentTransaction.commit();

    }

    private void showProgressBar() {
        if (loadingMenuItem != null) {
            View actionView = loadingMenuItem.getActionView();
            if (actionView != null) {
                ProgressBar progressBar = actionView.findViewById(R.id.top_meu_progressBar);
                progressBar.setVisibility(View.VISIBLE); // Show the ProgressBar
            }
        }
    }

    private void hideProgressBar() {
        if (loadingMenuItem != null) {
            View actionView = loadingMenuItem.getActionView();
            if (actionView != null) {
                ProgressBar progressBar = actionView.findViewById(R.id.top_meu_progressBar);
                progressBar.setVisibility(View.GONE); // Hide the ProgressBar
            }
        }
    }

}