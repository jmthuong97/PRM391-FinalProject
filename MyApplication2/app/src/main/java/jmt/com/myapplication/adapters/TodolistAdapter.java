package jmt.com.myapplication.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import jmt.com.myapplication.R;
import jmt.com.myapplication.activity.TaskActivity;
import jmt.com.myapplication.models.ToDoList;
import jmt.com.myapplication.viewholders.ToDoListViewHolder;

public class TodolistAdapter extends RecyclerView.Adapter<ToDoListViewHolder> {
    private Context context;
    private List<ToDoList> toDoLists;

    public TodolistAdapter(Context context, List<ToDoList> toDoLists) {
        this.context = context;
        this.toDoLists = toDoLists;
    }

    @NonNull
    @Override
    public ToDoListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_to_do_list, viewGroup, false);
        return new ToDoListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ToDoListViewHolder toDoListViewHolder, int i) {
        final ToDoList toDoList = toDoLists.get(i);

        String text = "-" + toDoList.getDate() + "  " + toDoList.getName();
        if (toDoList.isStatus()) {
            toDoListViewHolder.listTask.setTextColor(Color.parseColor("#42f477"));
        } else {
            toDoListViewHolder.listTask.setTextColor(Color.parseColor("#f47141"));
        }
        toDoListViewHolder.listTask.setText(text);
        toDoListViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TaskActivity.class);

                intent.putExtra("TaskId", toDoList.getId() + "");
                intent.putExtra("Name", toDoList.getName());
                intent.putExtra("Description", toDoList.getDesciption());
                intent.putExtra("Date", toDoList.getDate());
                intent.putExtra("Status", toDoList.isStatus() + "");
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return toDoLists.size();
    }
}
