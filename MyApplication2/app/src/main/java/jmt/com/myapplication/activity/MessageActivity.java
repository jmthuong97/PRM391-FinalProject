package jmt.com.myapplication.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;
import jmt.com.myapplication.R;
import jmt.com.myapplication.helpers.AutoColor;
import jmt.com.myapplication.helpers.Helper;
import jmt.com.myapplication.models.Message;
import jmt.com.myapplication.viewholders.MessageViewHolder;

public class MessageActivity extends AppCompatActivity {

    private final static int CHOOSE_FILE_REQUEST = 1;
    private final static long MAXIMUM_FILE_SIZE = 10 * 1024 * 1024; // equal to 10Mb
    private static final int MSG_TYPE_LEFT = 56;
    private static final int MSG_TYPE_RIGHT = 97;

    FirebaseRecyclerAdapter mAdapter;
    FirebaseStorage storage;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    RecyclerView recyclerView;

    private String displayName;
    private String imageURL;
    private String currentGroupId;
    private String mainColor;

    View uploadFileProgress;
    TextView txtProgress;
    CircleImageView profile_image;
    TextView groupName;
    ImageButton btn_send;
    ImageButton btn_uploadFile;
    EditText text_send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        // settings for group message
        Intent intent = getIntent();
        displayName = intent.getStringExtra("displayName");
        imageURL = intent.getStringExtra("imageURL");
        currentGroupId = intent.getStringExtra("id");
        mainColor = intent.getStringExtra("mainColor");

        uploadFileProgress = findViewById(R.id.uploadFileProgress);
        txtProgress = findViewById(R.id.txtProgress);
        profile_image = findViewById(R.id.groupImage);
        groupName = findViewById(R.id.groupName);
        btn_send = findViewById(R.id.btn_send);
        btn_uploadFile = findViewById(R.id.btn_uploadFile);
        text_send = findViewById(R.id.text_send);

        // set toolbar (back button)
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
        Helper.setColorToolAndStatusBar(toolbar, getWindow(), mainColor); // set toolbar color follow main color group

        setup();
    }

    private void setup() {
        // firebase storage for save file upload
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("messages/" + currentGroupId + "/" + Helper.getCurrentUser().getUid());
        databaseReference = FirebaseDatabase.getInstance().getReference("messages");

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = text_send.getText().toString();
                if (!msg.equals("")) {
                    sendMessage(msg, Message.TEXT, "NONE");
                    text_send.setText("");
                }
            }
        });

        btn_uploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), CHOOSE_FILE_REQUEST);
            }
        });

        // set color for send button
        btn_send.getBackground().setTint(AutoColor.COLOR(mainColor).Dark());
        btn_uploadFile.getBackground().setTint(AutoColor.COLOR(mainColor).Dark());

        // set display name in toolbar
        groupName.setText(displayName);
        // set image in toolbar
        if (imageURL.equals("DEFAULT"))
            profile_image.setImageResource(R.mipmap.ic_launcher);
        else
            Helper.setImageFromURL(this, imageURL, profile_image);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, DetailsGroupActivity.class);
            intent.putExtra("groupId", currentGroupId);
            intent.putExtra("mainColor", mainColor);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        readMessage();
        View process_bar = findViewById(R.id.progress_bar);
        process_bar.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.stopListening();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOOSE_FILE_REQUEST && resultCode == Activity.RESULT_OK
                && data != null && data.getData() != null) {
            Uri fileSelected = data.getData();

            uploadFile(fileSelected);
        }
    }

    private void uploadFile(Uri fileSelected) {
        UploadTask uploadTask = null;
        final boolean isImageFile = Helper.isImageFile(fileSelected, this);
        final String nameFile = Helper.getFileName(fileSelected, this);
        String uniqueIdForFileName = databaseReference.push().getKey();
        String path = "/" + uniqueIdForFileName + nameFile;
        final StorageReference ref = storageReference.child(path);

        if (isImageFile) uploadTask = ref.putBytes(Helper.reduceSizeImage(fileSelected, this));
        else if (Helper.getSizeFile(fileSelected, this) < MAXIMUM_FILE_SIZE)
            uploadTask = ref.putFile(fileSelected);
        else Helper.makeToastMessage("Maximum file size must be less than 5MB", this);

        if (uploadTask != null) {
            uploadFileProgress.setVisibility(View.VISIBLE); // show progress bar
            btn_uploadFile.setVisibility(View.GONE);
            uploadTask
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            String percent = (int) Math.round(progress) + "%";
                            txtProgress.setText(percent);
                        }
                    })
                    .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public com.google.android.gms.tasks.Task<Uri> then(@NonNull com.google.android.gms.tasks.Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            // Continue with the task to get the download URL
                            return ref.getDownloadUrl();
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                uploadFileProgress.setVisibility(View.GONE);
                                btn_uploadFile.setVisibility(View.VISIBLE);
                                txtProgress.setText("");
                                if (isImageFile)
                                    sendMessage(downloadUri.toString(), Message.IMAGE, nameFile);
                                else sendMessage(downloadUri.toString(), Message.FILE, nameFile);
                            }
                        }
                    });
        }

    }

    private void sendMessage(String content, String type, String fileURL) {
        databaseReference = FirebaseDatabase.getInstance().getReference("messages");
        String idMessage = databaseReference.push().getKey();

        Message message = new Message();
        message.setContent(content);
        message.setFileURL(fileURL);
        message.setType(type);
        message.setId(idMessage);
        message.setSender(Helper.getCurrentUser());
        databaseReference.child(currentGroupId).child(idMessage).setValue(message);
    }

    private void readMessage() {
        databaseReference = FirebaseDatabase.getInstance().getReference("messages").child(currentGroupId);
        FirebaseRecyclerOptions<Message> options =
                new FirebaseRecyclerOptions.Builder<Message>()
                        .setQuery(databaseReference, Message.class)
                        .build();

        mAdapter = new FirebaseRecyclerAdapter<Message, MessageViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MessageViewHolder viewHolder, int position, @NonNull Message message) {
                setContentMessage(viewHolder, message);
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                recyclerView.scrollToPosition(getItemCount() - 1);
            }

            @NonNull
            @Override
            public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                if (viewType == MSG_TYPE_RIGHT) {
                    View view = LayoutInflater.from(MessageActivity.this).inflate(R.layout.item_chat_right, viewGroup, false);
                    // set background color for own's user message
                    TextView contentMessage = view.findViewById(R.id.textMessage);
                    contentMessage.getBackground().setTint(AutoColor.COLOR(mainColor).Light());
                    return new MessageViewHolder(view);
                } else {
                    View view = LayoutInflater.from(MessageActivity.this).inflate(R.layout.item_chat_left, viewGroup, false);
                    return new MessageViewHolder(view);
                }
            }

            @Override
            public int getItemViewType(int position) {
                Message message = getItem(position);
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (message.getSender().getUid().equals(currentUser.getUid()))
                    return MSG_TYPE_RIGHT;
                else return MSG_TYPE_LEFT;
            }

        };

        mAdapter.startListening();
        recyclerView.setAdapter(mAdapter);
    }

    private void setContentMessage(MessageViewHolder viewHolder, Message message) {
        viewHolder.textMessage.setOnClickListener(null);
        viewHolder.textMessage.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        viewHolder.imageMessage.setImageDrawable(null);
        viewHolder.profile_image.setImageDrawable(null);

        if (viewHolder.displayName != null)
            viewHolder.displayName.setText(message.getSender().getDisplayName());
        viewHolder.profile_image.setImageDrawable(null);
        Helper.setAvatar(MessageActivity.this,
                message.getSender().getPhotoURL(),
                message.getSender().getProviderId(),
                viewHolder.profile_image);

        switch (message.getType()) {
            case Message.TEXT:
                viewHolder.textMessage.setText(message.getContent());
                break;
            case Message.IMAGE:
                Helper.setImageRoundCorner(MessageActivity.this, message.getContent(), viewHolder.imageMessage);
                break;
            case Message.FILE:
                final String link = message.getContent();
                SpannableString content = new SpannableString(message.getFileURL());
                content.setSpan(new UnderlineSpan(), 0, message.getFileURL().length(), 0);
                viewHolder.textMessage.setText(content);
                viewHolder.textMessage.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_cloud_download_black_24dp, 0, 0, 0);
                viewHolder.textMessage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(link));
                        startActivity(intent);
                    }
                });
                break;
        }
    }
}
