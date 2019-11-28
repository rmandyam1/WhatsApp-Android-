package com.example.whatsapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {

    ListView conversation;
    ArrayList<String> messages = new ArrayList<>();
    HashMap<String, String> chats = new HashMap<>();
    ArrayAdapter<String> adapter;
    EditText chatEditText;
    String otherUser;
    String otherUID;
    static int messageCount = 1;

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        //Intent intent = new Intent(this, ChatsListActivity.class);
        Intent intent = new Intent(this, ChatsListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void onSend(View view) {
//        messages.add(chatEditText.getText().toString());
//        adapter.notifyDataSetChanged();

        //Add a functionality where numbers are changed even if other user is chatting in order

        /*if (messageCount < 100) {
            FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("conversations").child(otherUID).child("00" + messageCount + "-you").setValue(chatEditText.getText().toString());
            FirebaseDatabase.getInstance().getReference().child("users").child(otherUID).child("conversations").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("00" + messageCount + "-" + FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).setValue(chatEditText.getText().toString());
        } else */if (messageCount < 10) {
            FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("conversations").child(otherUID).child("0" + messageCount + "-you").setValue(chatEditText.getText().toString());
            FirebaseDatabase.getInstance().getReference().child("users").child(otherUID).child("conversations").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("0" + messageCount + "-" + FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).setValue(chatEditText.getText().toString());
        } else {
            FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("conversations").child(otherUID).child(messageCount + "-you").setValue(chatEditText.getText().toString());
            FirebaseDatabase.getInstance().getReference().child("users").child(otherUID).child("conversations").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(messageCount + "-" + FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).setValue(chatEditText.getText().toString());
        }

        chatEditText.setText("");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        otherUser = intent.getStringExtra("user");
        otherUID = intent.getStringExtra("uid");
        Log.i("otherUser", otherUser);

        getSupportActionBar().setTitle("Chat with " + otherUser);

        chatEditText = findViewById(R.id.chatEditText);

        conversation = findViewById(R.id.conversation);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, messages);
        conversation.setAdapter(adapter);

            FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("conversations").child(otherUID).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    chats.put(dataSnapshot.getKey(), (String) dataSnapshot.getValue());
                    Log.i("I'm here!!", "HEREEEE");
                    if (dataSnapshot.getKey().contains("you")) {
                        messages.add((String) dataSnapshot.getValue());
                    } else {
                        messages.add(">" + dataSnapshot.getValue());
                    }
                    messageCount++;
                    adapter.notifyDataSetChanged();
                }
                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {}
                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
    }
}
