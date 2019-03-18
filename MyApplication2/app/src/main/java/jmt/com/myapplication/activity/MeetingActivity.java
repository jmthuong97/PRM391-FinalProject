package jmt.com.myapplication.activity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;

import jmt.com.myapplication.R;
import jmt.com.myapplication.fragments.MeetingFragment;

public class MeetingActivity extends AppCompatActivity {

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
    }


    public void onClickSave(View view) {
        if(isReasonableTime()) {

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
        String [] temp = timeString.split(":");
        ArrayList<Integer> hourAndMinuteList = new ArrayList<>();
        hourAndMinuteList.add(Integer.parseInt(temp[0]));
        hourAndMinuteList.add(Integer.parseInt(temp[1]));
        return hourAndMinuteList;
    }

    public boolean isReasonableTime() {
        ArrayList<Integer> fromTimeList = splitTimeList(textViewFromTime.getText().toString());
        ArrayList<Integer> toTimeList = splitTimeList(textViewToTime.getText().toString());
        if(fromTimeList.get(0) < toTimeList.get(0)) {
            return true;
        } else if(fromTimeList.get(0) == toTimeList.get(0)) {
            if(fromTimeList.get(1) < toTimeList.get(1)) {
                return true;
            }
        }
        return false;
    }

}
