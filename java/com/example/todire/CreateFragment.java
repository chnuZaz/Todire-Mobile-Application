package com.example.todire;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class CreateFragment extends Fragment {

    Button createBtn;
    EditText descriptionInput;
    Switch setDueDateSw;
    TextView dueDateText;
    TaskManager taskManager;
    CustomProgressDialog customProgressDialog;
    Calendar calendar;
    boolean setDueDate = false;
    String dueDate ;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        calendar = Calendar.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create, container, false);

        createBtn = view.findViewById(R.id.create_btn);
        descriptionInput = view.findViewById(R.id.description);
        taskManager = new TaskManager(getContext());
        customProgressDialog = new CustomProgressDialog(getContext());
        setDueDateSw = view.findViewById(R.id.set_due_date);
        dueDateText = view.findViewById(R.id.due_date_text);


        setDueDateSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setDueDate = true;
                    showDateTimePickerDialog();
                    dueDateText.setVisibility(View.VISIBLE);
                } else {
                    setDueDate = false;
                    dueDateText.setVisibility(View.GONE);
                }
            }
        });

        dueDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePickerDialog();
            }
        });





        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = descriptionInput.getText().toString();
                if(TextUtils.isEmpty(description)){
                    descriptionInput.setError("Please Enter Your Task");
                    return;
                }
                if(setDueDate && TextUtils.isEmpty(dueDate)){
                    Toast.makeText(getContext(),"Please pick a Due Date",Toast.LENGTH_SHORT).show();
                    return;
                }
                customProgressDialog.showDialog("Please wait...", false);

                taskManager.createTask(description,setDueDate,dueDate, new TaskManager.onTaskCreatedListner() {

                    @Override
                    public void onTaskCreated() {
                        Toast.makeText(getContext(),"Task Created",Toast.LENGTH_SHORT).show();
                        descriptionInput.setText("");
                        customProgressDialog.hideDialog();
                        dueDate = null;
                        dueDateText.setText("Pick a Date and Time");
                    }

                    @Override
                    public void onTaskFailed() {
                        Toast.makeText(getContext(),"Try Again",Toast.LENGTH_SHORT).show();
                        customProgressDialog.hideDialog();
                    }
                });



            }
        });



        return view;
    }




    private void showDateTimePickerDialog() {

        int year_current = calendar.get(Calendar.YEAR);
        int month_current = calendar.get(Calendar.MONTH);
        int date_current = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                int year_select = year;
                int month_select = month;
                int date_select = dayOfMonth;

                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // Your code when a time is selected
                        Calendar selectedDateTime = Calendar.getInstance();
                        selectedDateTime.set(year_select, month_select, date_select, hourOfDay, minute);

                        if (selectedDateTime.compareTo(calendar) > 0 || selectedDateTime.equals(calendar)) {
                            // The selected date and time is today or in the future
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault());
                            String selectedDateTimeString = dateFormat.format(selectedDateTime.getTime());
                            dueDateText.setText(year_select+"/"+month_select+"/"+date_select +" at "+hourOfDay+":"+minute);
                            dueDate = selectedDateTimeString;

                        } else {
                            // The selected date and time is in the past
                            Toast.makeText(getContext(),"Enter Valid Date and Time",Toast.LENGTH_SHORT).show();

                        }
                    }
                }, hour, minute, true);
                timePickerDialog.show();
            }
        }, year_current, month_current, date_current);

        datePickerDialog.show();
    }

}