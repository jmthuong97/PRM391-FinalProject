package jmt.com.myapplication.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import jmt.com.myapplication.R;
import jmt.com.myapplication.helpers.AutoColor;
import jmt.com.myapplication.helpers.SetImageFromURL;
import jmt.com.myapplication.models.Message;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;
    private Context context;
    private List<Message> messages;
    private String mainColor;

    public MessageAdapter(Context context, List<Message> messages, String mainColor) {
        this.context = context;
        this.messages = messages;
        this.mainColor = mainColor;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, viewGroup, false);
            // set background color for own's user message
            TextView contentMessage = view.findViewById(R.id.showMessage);
            contentMessage.getBackground().setTint(AutoColor.COLOR(mainColor).Light());
            return new MessageAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, viewGroup, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Message message = messages.get(i);

        viewHolder.showMessage.setText(message.getContent());
        if (viewHolder.displayName != null)
            viewHolder.displayName.setText(message.getSender().getDisplayName());

        new SetImageFromURL(message.getSender().getPhotoURL(), message.getSender().getProviderId())
                .setImage(viewHolder.profile_image);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (messages.get(position).getSender().getUid().equals(currentUser.getUid()))
            return MSG_TYPE_RIGHT;
        else return MSG_TYPE_LEFT;
    }

    // view holder of user item
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView showMessage;
        TextView displayName;
        ImageView profile_image;

        ViewHolder(View view) {
            super(view);

            showMessage = view.findViewById(R.id.showMessage);
            displayName = view.findViewById(R.id.displayName);
            profile_image = view.findViewById(R.id.profile_image);
        }
    }
}

