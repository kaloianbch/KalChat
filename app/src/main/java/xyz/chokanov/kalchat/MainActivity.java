package xyz.chokanov.kalchat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Starting activity for the application. Creates chat and populates chat screen.
 * If the user is registered their data is pulled from the DB, otherwise a new user is registered
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private TextView mTextShowUser;
    private EditText mTextInput;
    private Button mButtonSend, mButtonSettings;
    private RecyclerView mChatRecView;
    private List<String[]> mChatMessages = new ArrayList<>(); //TODO - not have an array list with every message ever in the room
    private String roomName = "General";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: started.");
        final User user = new User();
        DatabaseReference userBDRef = FirebaseDatabase.getInstance().getReference().getRoot()
                .child("UserList").child(User.getId());
        final DatabaseReference chatDBRef = FirebaseDatabase.getInstance().getReference().getRoot()
                .child(roomName);
        mTextShowUser = findViewById(R.id.txtId);
        mTextInput = findViewById(R.id.txtInput);
        mButtonSend = findViewById(R.id.btnSend);
        mButtonSettings = findViewById(R.id.btnSettings);
        mChatRecView = findViewById(R.id.recviewChat);

        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(this, mChatMessages);
        mChatRecView.setAdapter(recyclerViewAdapter);
        mChatRecView.setLayoutManager(new LinearLayoutManager(this));

        mButtonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });

        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(chatDBRef, user.getUsername(),user.getAvatarAsString(), mTextInput.getText().toString());
                mTextInput.setText("");
            }

        });
        userBDRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Attempt to get user data. if none exists a new user will be made
                try {
                    user.setAvatarImage(dataSnapshot.child("Image").getValue().toString());
                    user.setUsername(dataSnapshot.child("UserName").getValue().toString());
                    mTextShowUser.setText("User: " + user.getUsername());
                }catch (NullPointerException e){
                    user.createNewUser();
                    mTextShowUser.setText("User: " + user.getUsername());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TODO
            }
        });

        chatDBRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                appendChatData(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                appendChatData(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //TODO
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                //TODO
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TODO
            }
        });
    }

    /**
     * Appends message to chat array list.
     * @param dataSnapshot Database snapshot containing message inforamtion
     */
    private void appendChatData(DataSnapshot dataSnapshot) {
        try {
            mChatMessages.add(new String [] {dataSnapshot.child("Message").getValue().toString(),
                    dataSnapshot.child("TimeSent").getValue().toString(),
                    dataSnapshot.child("User").getValue().toString(),
                    dataSnapshot.child("Image").getValue().toString()});
                    mChatRecView.scrollToPosition(mChatMessages.size()-1);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Adds message to database
     * @param root Database reference for the chat room
     * @param name id of sender
     * @param message Message to be sent... duh
     */
    private void sendMessage(DatabaseReference root, String name, String avatar, String message){
        Map<String,Object> chatMap = new HashMap<String, Object>();
        String messageIdKey = root.push().getKey();
        root.updateChildren(chatMap);

        DatabaseReference msgDBRef = root.child(messageIdKey);
        Map<String, Object> msgMap = new HashMap<String, Object>();
        msgMap.put("User", name);
        msgMap.put("Image", avatar); //TODO - JESUS CHRIST FIX THIS LATER PLEASE
        msgMap.put("Message", message);
        msgMap.put("TimeSent", new SimpleDateFormat("HH:mm").format(
                Calendar.getInstance().getTime()));
        msgDBRef.updateChildren(msgMap);
        mChatRecView.scrollToPosition(mChatMessages.size()-1);
    }
}
