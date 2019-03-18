package jmt.com.myapplication.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import jmt.com.myapplication.Database.DBHelper;
import jmt.com.myapplication.R;
import jmt.com.myapplication.helpers.Helper;
import jmt.com.myapplication.models.ToDoList;
import jmt.com.myapplication.models.User;

public class TaskActivity extends AppCompatActivity {
    private int id;
    private String name, date, description, status;
    private TextView header;
    private EditText getName, getDate, getDescription;
    private CheckBox checkFinish;
    private DBHelper database;
    private Button btnOk, btnDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        Intent intent = getIntent();
        name = intent.getStringExtra("Name");
        date = intent.getStringExtra("Date");
        description = intent.getStringExtra("Description");
        String getId = intent.getStringExtra("TaskId");
        status = intent.getStringExtra("Status");
        id = Integer.parseInt(getId);
        database = new DBHelper(this, null, null, 1);
        header = findViewById(R.id.header);
        getName = findViewById(R.id.showTaskName);
        getDate = findViewById(R.id.showDate);
        getDescription = findViewById(R.id.showDescription);
        checkFinish = findViewById(R.id.finishOrNot);
        btnOk = findViewById(R.id.okButton);
        btnDelete = findViewById(R.id.Delete);
        if (status.equalsIgnoreCase("true")) {
            checkFinish.setChecked(true);
        } else {
            checkFinish.setChecked(false);
        }
        setTextForShow();
        run();
    }

    private void setTextForShow() {
        getName.setText(name);
        getDate.setText(date);
        getDescription.setText(description);
        getName.setTextColor(Color.parseColor("#272284"));
        getDate.setTextColor(Color.parseColor("#272284"));
        getDescription.setTextColor(Color.parseColor("#272284"));
    }

    private void run() {
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editData();
                finish();
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database.deleteTask(id);
                finish();
            }
        });
    }

    private void editData() {
        User currentUser = Helper.getCurrentUser();
        ToDoList toDoList = new ToDoList();
        toDoList.setId(id);
        toDoList.setUserID(currentUser.getUid());
        toDoList.setName(name);
        toDoList.setDesciption(description);
        toDoList.setDate(date);
        if (checkFinish.isChecked()) {
            toDoList.setStatus(true);
            database.updateTask(toDoList);
        } else {
            toDoList.setStatus(false);
            database.updateTask(toDoList);
        }
    }


}
