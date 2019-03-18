package jmt.com.myapplication.viewholders;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import jmt.com.myapplication.R;

public class ToDoListViewHolder extends RecyclerView.ViewHolder {

    public TextView listTask;

    public ToDoListViewHolder(@NonNull View itemView) {
        super(itemView);


        listTask = itemView.findViewById(R.id.listTask);

    }


}
