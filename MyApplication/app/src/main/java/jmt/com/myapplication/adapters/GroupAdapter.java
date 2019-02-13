package jmt.com.myapplication.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import jmt.com.myapplication.R;
import jmt.com.myapplication.activity.MessageActivity;
import jmt.com.myapplication.models.Group;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {
    private Context context;
    private List<Group> groups;

    public GroupAdapter(Context context, List<Group> groups) {
        this.context = context;
        this.groups = groups;
    }

    @NonNull
    @Override
    public GroupAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.group_item, viewGroup, false);
        return new GroupAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final Group group = groups.get(i);

        viewHolder.groupName.setText(group.getDisplayName());
        viewHolder.description.setText(group.getDescription());
        viewHolder.groupImage.setImageResource(R.mipmap.ic_launcher); // here
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MessageActivity.class);
                intent.putExtra("displayName", group.getDisplayName());
                intent.putExtra("id", "03JjYuirRv0tdVxocGS7");
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    // view holder of group item
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView groupName;
        ImageView groupImage;
        TextView description;

        ViewHolder(View view) {
            super(view);

            groupName = view.findViewById(R.id.groupName);
            groupImage = view.findViewById(R.id.groupImage);
            description = view.findViewById(R.id.description);
        }
    }
}
