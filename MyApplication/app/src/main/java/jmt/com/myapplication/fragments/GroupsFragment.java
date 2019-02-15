package jmt.com.myapplication.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

import jmt.com.myapplication.R;
import jmt.com.myapplication.adapters.GroupAdapter;
import jmt.com.myapplication.helpers.Helper;
import jmt.com.myapplication.models.Group;
import jmt.com.myapplication.models.User;

public class GroupsFragment extends Fragment {
    DatabaseReference databaseReference;

    public View rootView;
    private RecyclerView recyclerView;
    private GroupAdapter groupAdapter;
    private List<Group> groupList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_groups, container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference("groups");

        recyclerView = rootView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        groupList = new ArrayList<>();

        FloatingActionButton fabAddGroup = rootView.findViewById(R.id.addGroup);
        fabAddGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                AddGroupFragment addGroupFragment = new AddGroupFragment();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.add(android.R.id.content, addGroupFragment).addToBackStack(null).commit();
            }
        });

        FloatingActionButton fabScanGroup = rootView.findViewById(R.id.qr_scan);
        fabScanGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator.forSupportFragment(GroupsFragment.this)
                        .setPrompt("Scan a barcode")
                        .initiateScan();
            }
        });

        getDataGroups();
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        String code = result.getContents();

        //if qrcode has nothing in it
        if (code == null || !Helper.isCorrectQRCode(code))
            Helper.makeToastMessage("QR Code Not Found", getContext());
        else {
            String groupId = Helper.decodeQRCode(code);
            databaseReference.child(groupId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Group group = dataSnapshot.getValue(Group.class);
                            if (group.getDisplayName() != null) Alert(group);
                            else
                                Helper.makeToastMessage("Group Not Found", getContext());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.d("TestScanQRGroup", databaseError.toString());
                            Helper.makeToastMessage(databaseError.getMessage(), getContext());
                        }
                    });
        }
    }

    // alert when scan group
    private void Alert(final Group group) {
        new AlertDialog.Builder(getContext())
                .setTitle("Do you want to join this group?")
                .setMessage(group.getDisplayName())
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("Do you want to join this group?", "Yes");
                        User currentUser = Helper.GetCurrentUser();
                        List<String> newArrMembers = group.getMembers();

                        if (newArrMembers.contains(currentUser.getUid()))
                            Helper.makeToastMessage("You are already in this group !", getContext());
                        else {
                            newArrMembers.add(currentUser.getUid());
                            databaseReference.child(group.getId()).child("members").setValue(newArrMembers);
                        }

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("Do you want to join this group?", "No");
                    }
                })
                .show();
    }


    // get list group of current user
    private void getDataGroups() {
        final User currentUser = Helper.GetCurrentUser();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                groupList.clear();
                // set loading screen to off
                rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Group group = snapshot.getValue(Group.class);
                    if (group.getMembers().contains(currentUser.getUid())) {
                        groupList.add(group);
                        groupAdapter = new GroupAdapter(getContext(), groupList);
                        recyclerView.setAdapter(groupAdapter);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Helper.makeToastMessage("No groups were found !", getContext());
            }
        });
    }
}
