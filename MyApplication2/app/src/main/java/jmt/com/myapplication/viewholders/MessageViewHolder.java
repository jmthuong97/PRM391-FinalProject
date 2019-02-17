package jmt.com.myapplication.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import jmt.com.myapplication.R;

public class MessageViewHolder extends RecyclerView.ViewHolder {
    public TextView textMessage;
    public TextView displayName;
    public ImageView imageMessage;
    public ImageView profile_image;

    public MessageViewHolder(View view) {
        super(view);
        textMessage = view.findViewById(R.id.textMessage);
        displayName = view.findViewById(R.id.displayName);
        imageMessage = view.findViewById(R.id.imageMessage);
        profile_image = view.findViewById(R.id.profile_image);
    }
}
