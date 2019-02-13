package jmt.com.myapplication.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import jmt.com.myapplication.R;
import jmt.com.myapplication.adapters.MessageAdapter;
import jmt.com.myapplication.helpers.Helper;

import jmt.com.myapplication.models.Message;

public class MessageActivity extends AppCompatActivity {

    private final static String TEXT = "TEXT";
    private String currentGroupId;
    CircleImageView profile_image;
    TextView groupName;
    ImageButton btn_send;
    EditText text_send;

    DatabaseReference databaseReference;
    MessageAdapter messageAdapter;
    List<Message> messageList;

    RecyclerView recyclerView;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        setup();

        btn_send = findViewById(R.id.btn_send);
        text_send = findViewById(R.id.text_send);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = text_send.getText().toString();
                if (!msg.equals("")) {
                    sendMessage(msg, TEXT);
                    text_send.setText("");
                }
            }
        });

        readMessage();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    private void setup() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        profile_image = findViewById(R.id.groupImage);
        groupName = findViewById(R.id.groupName);

        intent = getIntent();

        currentGroupId = intent.getStringExtra("id");

        groupName.setText(intent.getStringExtra("displayName"));
        profile_image.setImageResource(R.mipmap.ic_launcher);
    }

    private void sendMessage(String content, String type) {
        databaseReference = FirebaseDatabase.getInstance().getReference("messages");

        String idMessage = databaseReference.push().getKey();

        Message message = new Message();
        message.setContent(content);
        message.setType(type);
        message.setId(idMessage);
        message.setSender(Helper.GetCurrentUser());
        databaseReference.child(currentGroupId).child(idMessage).setValue(message);
    }

    private void readMessage() {
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        messageList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("messages").child(currentGroupId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messageList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);
                    messageList.add(message);
                    messageAdapter = new MessageAdapter(MessageActivity.this, messageList);
                    recyclerView.setAdapter(messageAdapter);
                }
                View process_bar = findViewById(R.id.progress_bar);
                process_bar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("TestReadMessage", databaseError.toString());
            }
        });

    }
}
