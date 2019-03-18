package jmt.com.myapplication.fragments;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import jmt.com.myapplication.Database.DBHelper;
import jmt.com.myapplication.R;
import jmt.com.myapplication.helpers.Helper;
import jmt.com.myapplication.helpers.ICreateTaskSuccess;
import jmt.com.myapplication.models.ToDoList;
import jmt.com.myapplication.models.User;


@SuppressLint("ValidFragment")
public class CreateTaskFragment extends DialogFragment {
    private Button add;
    private Button cancel;

    private EditText name, descript, date;
    private DBHelper database;
    private ICreateTaskSuccess iCreateTaskSuccess;

    public CreateTaskFragment(ICreateTaskSuccess iCreateTaskSuccess) {
        this.iCreateTaskSuccess = iCreateTaskSuccess;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_create_task, container, false);
        add = rootView.findViewById(R.id.EnterButton);
        cancel = rootView.findViewById(R.id.CancelButton);
        database = new DBHelper(getContext(), null, null, 1);
        descript = rootView.findViewById(R.id.enterDescription);
        date = rootView.findViewById(R.id.enterDueDate);
        name = rootView.findViewById(R.id.enterTaskName);
        date.setText(getTodayDate());
        RunFragment();
        return rootView;


    }

    private void RunFragment() {
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckEditText()) createTask();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private String getTodayDate() {
        Date todayDate = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String date = dateFormat.format(todayDate);
        return date;
    }

    private boolean CheckEditText() {
        boolean result = true;
        String todayDate = date.getText().toString();
        String taskName = name.getText().toString();
        String taskDescription = descript.getText().toString();
        if (todayDate.equals("") || taskName.equals("") || taskDescription.equals("")) {
            result = false;
            Helper.makeToastMessage("You must fill in all the field", getContext());
        }
        return result;
    }

    private void createTask() {
        User currentUser = Helper.getCurrentUser();
        String todayDate = date.getText().toString();
        String taskName = name.getText().toString();
        String taskDescription = descript.getText().toString();
        String userId = currentUser.getUid();
        ToDoList toDoList = new ToDoList();
        toDoList.setUserID(userId);
        toDoList.setDesciption(taskDescription);
        toDoList.setName(taskName);
        toDoList.setDate(todayDate);
        toDoList.setStatus(false);
        database.addTask(toDoList);
        iCreateTaskSuccess.success();
        dismiss();
    }
}
