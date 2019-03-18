package jmt.com.myapplication.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import jmt.com.myapplication.Database.DBHelper;
import jmt.com.myapplication.R;
import jmt.com.myapplication.adapters.TodolistAdapter;
import jmt.com.myapplication.helpers.Helper;
import jmt.com.myapplication.helpers.ICreateTaskSuccess;
import jmt.com.myapplication.models.ToDoList;
import jmt.com.myapplication.models.User;


public class ToDoListFragment extends Fragment {

    private RecyclerView recyclerView;
    private TodolistAdapter todolistAdapter;
    private View rootView;
    private ArrayList<ToDoList> checkList;
    private DBHelper database;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_to_do_list, container, false);

        return rootView;
    }

    @Override
    public void onStart() {
        database = new DBHelper(getContext(), null, null, 1);

        recyclerView = rootView.findViewById(R.id.toDoListRecycleView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        FloatingActionButton addTask = rootView.findViewById(R.id.addToDoTask);
        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCreateTaskFragment();
            }
        });

        getDataTodolist();
        super.onStart();
    }

    private void openCreateTaskFragment() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        CreateTaskFragment createTaskFragment = new CreateTaskFragment(new ICreateTaskSuccess() {
            @Override
            public void success() {
                getDataTodolist();
            }
        });
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(android.R.id.content, createTaskFragment).addToBackStack(null).commit();
    }

    @Override
    public void onDestroy() {
        checkList = null;
        super.onDestroy();

    }

    private void getDataTodolist() {
        final User currentUser = Helper.getCurrentUser();
        checkList = database.getTaskList(currentUser.getUid());

        if (!checkList.isEmpty()) {
            rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
            todolistAdapter = new TodolistAdapter(getContext(), checkList);
            recyclerView.setAdapter(todolistAdapter);
        } else {
            rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
            if (todolistAdapter != null) {
                todolistAdapter.notifyDataSetChanged();
            }

            Helper.makeToastMessage("No To Do List Found !!", getContext());
        }
    }
}
