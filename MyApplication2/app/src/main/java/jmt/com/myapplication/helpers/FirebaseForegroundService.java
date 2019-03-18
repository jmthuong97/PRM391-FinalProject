package jmt.com.myapplication.helpers;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseForegroundService extends FirebaseMessagingService {
    public FirebaseForegroundService() {

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Bundle bundle = new Bundle();
        bundle.putString("Title", remoteMessage.getNotification().getTitle());
        bundle.putString("Message", remoteMessage.getNotification().getBody());

        Intent intent = new Intent("FIREBASE_MESSAGE_ACTION");
        intent.putExtras(bundle);
        sendBroadcast(intent);
    }
}
