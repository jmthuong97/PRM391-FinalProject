package jmt.com.myapplication.activity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import jmt.com.myapplication.R;
import jmt.com.myapplication.helpers.Helper;
import jmt.com.myapplication.models.Group;
import jmt.com.myapplication.models.User;

public class MeetingActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private DatabaseReference databaseReference;
    private List<String> groupString;
    private List<Group> groupList;
    private Spinner spinnerGroupName;
    private Group selectedGroup;

    TextView textViewFromTime;
    TextView textViewToTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting);

        Intent intent = getIntent();
        TextView textViewDate = (TextView) findViewById(R.id.textViewDate);
        textViewDate.setText(intent.getStringExtra("dateMeeting"));
        textViewFromTime = findViewById(R.id.textViewFromTime);
        textViewToTime = findViewById(R.id.textViewToTime);
        textViewFromTime.setText("0:00");
        textViewToTime.setText("0:00");

        spinnerGroupName = findViewById(R.id.spinnerGroupName);
        spinnerGroupName.setOnItemSelectedListener(this);

        databaseReference = FirebaseDatabase.getInstance().getReference("groups");
        groupList = new ArrayList<>();
        groupString = new ArrayList<>();
        getDataGroup();

        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter<String> aa = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, groupString);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spinnerGroupName.setAdapter(aa);
    }

    private void getDataGroup() {
        final User currentUser = Helper.getCurrentUser();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Group group = snapshot.getValue(Group.class);
                    if (group.getMembers().contains(currentUser.getUid())) {
                        groupString.add(group.getDisplayName());
                        groupList.add(group);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void onClickSave(View view) {
        Toast.makeText(this, "Selected: " + spinnerGroupName.getSelectedItem(), Toast.LENGTH_LONG).show();
        if (isReasonableTime()) {

        } else {
            Toast.makeText(this,
                    "Meeting start time " + textViewFromTime.getText()
                            + " is later than the end of the meeting " + textViewToTime.getText()
                            + ". Please enter again.", Toast.LENGTH_LONG).show();
        }

    }

    public void onClickFromTime(View view) {
        textViewFromTime = findViewById(R.id.textViewFromTime);
        pickTime(textViewFromTime);
    }

    public void onClickToTime(View view) {
        textViewToTime = findViewById(R.id.textViewToTime);
        pickTime(textViewToTime);
    }

    public void pickTime(final TextView textView) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                textView.setText(hourOfDay + ":" + minute);
            }
        }, hour, minute, true);
        timePickerDialog.show();
    }

    public ArrayList<Integer> splitTimeList(String timeString) {
        String[] temp = timeString.split(":");
        ArrayList<Integer> hourAndMinuteList = new ArrayList<>();
        hourAndMinuteList.add(Integer.parseInt(temp[0]));
        hourAndMinuteList.add(Integer.parseInt(temp[1]));
        return hourAndMinuteList;
    }

    public boolean isReasonableTime() {
        ArrayList<Integer> fromTimeList = splitTimeList(textViewFromTime.getText().toString());
        ArrayList<Integer> toTimeList = splitTimeList(textViewToTime.getText().toString());
        if (fromTimeList.get(0) < toTimeList.get(0)) {
            return true;
        } else if (fromTimeList.get(0) == toTimeList.get(0)) {
            if (fromTimeList.get(1) < toTimeList.get(1)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedGroup = groupList.get(position);
        Toast.makeText(getApplicationContext(), position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}

