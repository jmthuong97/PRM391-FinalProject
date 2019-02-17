package jmt.com.myapplication.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import jmt.com.myapplication.R;
import jmt.com.myapplication.activity.MessageActivity;
import jmt.com.myapplication.helpers.Helper;
import jmt.com.myapplication.models.Group;
import jmt.com.myapplication.viewholders.GroupViewHolder;

public class GroupAdapter extends RecyclerView.Adapter<GroupViewHolder> {
    private Context context;
    private List<Group> groups;

    public GroupAdapter(Context context, List<Group> groups) {
        this.context = context;
        this.groups = groups;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_group, viewGroup, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder viewHolder, int i) {
        final Group group = groups.get(i);

        viewHolder.groupName.setText(group.getDisplayName());
        viewHolder.description.setText(group.getDescription());

        if (group.getImageURL().equals("DEFAULT")) // check if group image is default (user don't upload image)
            viewHolder.groupImage.setImageResource(R.mipmap.ic_launcher);
        else
            Helper.setImageFromURL(context, group.getImageURL(), viewHolder.groupImage);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MessageActivity.class);
                intent.putExtra("id", group.getId());
                intent.putExtra("displayName", group.getDisplayName());
                intent.putExtra("mainColor", group.getMainColor());
                intent.putExtra("imageURL", group.getImageURL());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }
}
