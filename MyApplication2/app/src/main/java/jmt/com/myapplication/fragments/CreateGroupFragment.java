package jmt.com.myapplication.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

import jmt.com.myapplication.R;
import jmt.com.myapplication.helpers.Helper;
import jmt.com.myapplication.models.Group;
import jmt.com.myapplication.models.User;

public class CreateGroupFragment extends DialogFragment {

    private final static int PICK_IMAGE_REQUEST = 1;

    DatabaseReference databaseReference;
    FirebaseStorage storage;
    StorageReference storageReference;

    View rootView;
    View loadingScreen;
    EditText groupNameEditText;
    EditText descriptionEditText;
    RadioGroup colorPickerGroupBtn;
    TextView fileNameSelected;
    TextView textProgress;
    Button chooseBtn;
    Button okBtn;

    Uri fileSelected = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_create_group, container, false);
        (rootView.findViewById(R.id.button_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("groups"); // realtime database
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("groups");

        loadingScreen = rootView.findViewById(R.id.loadingScreen);
        groupNameEditText = rootView.findViewById(R.id.groupNameEditText);
        descriptionEditText = rootView.findViewById(R.id.descriptionEditText);
        colorPickerGroupBtn = rootView.findViewById(R.id.colorPickerGroupBtn);
        fileNameSelected = rootView.findViewById(R.id.fileNameSelected);
        textProgress = rootView.findViewById(R.id.textProgress);
        chooseBtn = rootView.findViewById(R.id.chooseBtn);
        okBtn = rootView.findViewById(R.id.okBtn);

        chooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkRequire()) doCreateGroup();
            }
        });

        this.rootView = rootView;
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
                && data != null && data.getData() != null) {
            fileSelected = data.getData();
            fileNameSelected.setText(Helper.getFileName(fileSelected, getActivity()));
        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private boolean checkRequire() {
        String groupName = groupNameEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        if (groupName.trim().isEmpty()) {
            Helper.makeToastMessage("Group name field cannot be empty !", getContext());
            ScrollView scrollView = rootView.findViewById(R.id.scrollViewAddGroup);
            scrollView.fullScroll(ScrollView.FOCUS_UP);
            groupNameEditText.requestFocus();
            return false;
        } else if (description.trim().isEmpty()) {
            Helper.makeToastMessage("Description field cannot be empty !", getContext());
            ScrollView scrollView = rootView.findViewById(R.id.scrollViewAddGroup);
            scrollView.fullScroll(ScrollView.FOCUS_UP);
            descriptionEditText.requestFocus();
            return false;
        } else return true;
    }

    private void doCreateGroup() {
        loadingScreen.setVisibility(View.VISIBLE);
        String groupId = databaseReference.push().getKey();
        String nameFile = fileNameSelected.getText().toString();

        if (fileSelected != null) {
            String path = "/" + groupId + "/cover." + nameFile.substring(nameFile.lastIndexOf("."));
            final StorageReference ref = storageReference.child(path);
            ref.putBytes(Helper.reduceSizeImage(fileSelected, getActivity()))
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            String percent = "Upload image is " + Math.round(progress) + "% done";
                            textProgress.setText(percent);
                        }
                    })
                    .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
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
                                textProgress.setText("Creating group ...");
                                createGroup(downloadUri.toString());
                                Log.d("downloadUri", downloadUri.toString());
                            }
                        }
                    });

        } else createGroup("DEFAULT");
    }

    private void createGroup(String imageURL) {
        User currentUser = Helper.getCurrentUser();
        List<String> members = new ArrayList<>();
        members.add(currentUser.getUid());

        String groupId = databaseReference.push().getKey();

        Group group = new Group();
        group.setId(groupId);
        group.setDisplayName(groupNameEditText.getText().toString());
        group.setDescription(descriptionEditText.getText().toString());
        group.setMainColor(getPickerColor());
        group.setStatus(true);
        group.setMembers(members);
        group.setImageURL(imageURL);

        databaseReference.child(groupId).setValue(group)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        loadingScreen.setVisibility(View.GONE);
                        getActivity().getSupportFragmentManager().popBackStackImmediate();
                    }
                });
    }

    private String getPickerColor() {
        RadioButton checker = rootView.findViewById(colorPickerGroupBtn.getCheckedRadioButtonId());
        return checker.getText().toString();
    }

}
