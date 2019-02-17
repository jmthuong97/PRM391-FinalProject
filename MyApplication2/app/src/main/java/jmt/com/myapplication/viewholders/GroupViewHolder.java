package jmt.com.myapplication.viewholders;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import jmt.com.myapplication.R;

public class GroupViewHolder extends RecyclerView.ViewHolder {
    public TextView groupName;
    public ImageView groupImage;
    public TextView description;

    public GroupViewHolder(@NonNull View view) {
        super(view);

        groupName = view.findViewById(R.id.groupName);
        groupImage = view.findViewById(R.id.groupImage);
        description = view.findViewById(R.id.description);
    }
}
