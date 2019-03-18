package jmt.com.myapplication.activity;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.RemoteInput;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import jmt.com.myapplication.R;
import jmt.com.myapplication.fragments.BuyFragment;
import jmt.com.myapplication.fragments.GroupsFragment;
import jmt.com.myapplication.fragments.ToDoListFragment;
import jmt.com.myapplication.helpers.Helper;
import jmt.com.myapplication.models.User;


public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    MessageReceiver receiver;
    private final String NOTIFICATION_CHANEL = "My notification chanel";
    private final int NOTIFICATION_ID = 100;
    private final String KEY_TEXT_REPLY = "RESULT_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        displaySelectedScreen(R.id.nav_message);
        navigationView.getMenu().getItem(0).setChecked(true);

        getCurrentUser();
        //init token

        Helper.initToken();
        //noti response
        //if fail disable
        receiver = new MessageReceiver();
        Intent intent = getIntent();
        Bundle bundle = RemoteInput.getResultsFromIntent(intent);
        if (bundle != null) {
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.cancel(NOTIFICATION_ID);
            String msg = bundle.getString(KEY_TEXT_REPLY);

            Bundle bundleInc = intent.getExtras();

            String displayName = "";
            if (bundleInc != null) {
                displayName = bundleInc.getString("title");
                Log.d("bigboy", " big boi bundle got " + bundleInc.getString("title") + bundle.getString("title"));
            }

            Helper.notiRep(msg, displayName);

        }
    }

    private void getCurrentUser() {
        User currentUser = Helper.getCurrentUser();
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);

        if (currentUser != null) {
            ImageView currentUserAvatar = headerView.findViewById(R.id.currentUserAvatar);
            TextView currentUserName = headerView.findViewById(R.id.currentUserName);
            TextView currentUserEmail = headerView.findViewById(R.id.currentUserEmail);

            Helper.setAvatar(this, currentUser.getPhotoURL(), currentUser.getProviderId(), currentUserAvatar);
            currentUserName.setText(currentUser.getDisplayName());
            currentUserEmail.setText(currentUser.getEmail());
        }
    }

    private void logout() {
        // create a dialog alert
        new AlertDialog.Builder(this)
                .setTitle("Do you want to log out?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AuthUI.getInstance()
                                .signOut(HomeActivity.this)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Helper.makeToastMessage("User Signed Out", HomeActivity.this);
                                        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                                        HomeActivity.this.startActivity(intent);
                                    }
                                });
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        User currentUser = Helper.getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
        String childPath = currentUser.getUid() + "/premiumAccount/status";
        databaseReference.child(childPath).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null && dataSnapshot.getValue().equals(true)) {
                    ImageView premiumAcc = findViewById(R.id.ic_premium_account);
                    premiumAcc.setVisibility(View.VISIBLE);
                    Helper.upgradeFileSize();
                    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                    Menu nav_Menu = navigationView.getMenu();
                    nav_Menu.findItem(R.id.donateBtn).setVisible(false);
                    nav_Menu.findItem(R.id.nav_toDoList).setVisible(true);
                } else {
                    Log.d("Premium Account", dataSnapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return super.onPrepareOptionsMenu(menu);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        displaySelectedScreen(id);
        return true;
    }

    private void displaySelectedScreen(int id) {
        Fragment fragment = null;

        switch (id) {
            case R.id.nav_message:
                fragment = new GroupsFragment();
                setTitle("Message");
                break;
            case R.id.donateBtn:
                setTitle("Premium Account");
                fragment = new BuyFragment();
                break;
            case R.id.logoutBtn:
                logout();
                break;
            case R.id.nav_toDoList:
                setTitle("To Do List");
                fragment = new ToDoListFragment();
                break;
        }

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_home, fragment);
            ft.commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            String message = "";
            String title = "";
            if (bundle != null) {
                message = bundle.getString("Message");
                title = bundle.getString("Title");
            }
            Log.d("bigboy", "bigboi received title" + title);

            createNotification(message, title);
        }
    }

    private void createNotification(String message, String title) {
        Intent intent = new Intent(this, HomeActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        intent.putExtras(bundle);

        Log.d("bigboy", "big boi in bundle title " + title);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);


        RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY)
                .setLabel("Enter text here")
                .build();
        NotificationCompat.Action action = new NotificationCompat.Action.Builder(
                android.R.drawable.ic_dialog_alert,
                "Reply",
                pendingIntent)
                .addRemoteInput(remoteInput)
                .build();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setColor(Color.argb(255, 0, 0, 255))
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setChannelId(NOTIFICATION_CHANEL)
                .setContentIntent(pendingIntent)
                .addAction(action);

        NotificationChannel channel = new NotificationChannel(
                NOTIFICATION_CHANEL,
                "Notification chanel",
                NotificationManager.IMPORTANCE_HIGH);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }


    @Override
    protected void onResume() {
        registerReceiver(receiver, new IntentFilter("FIREBASE_MESSAGE_ACTION"));
        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(receiver);
        super.onPause();
    }
}
