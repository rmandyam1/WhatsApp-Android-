package com.example.whatsapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ContactsListActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    ListView usersListView;
    ArrayList<String> users = new ArrayList<>();
    ArrayAdapter<String> adapter;
    ArrayList<String> uid = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_list);

        mAuth = FirebaseAuth.getInstance();

        usersListView = findViewById(R.id.usersListView);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, users);
        usersListView.setAdapter(adapter);

        FirebaseDatabase.getInstance().getReference().child("users").addChildEventListener(new ChildEventListener() {
            //int counter = 0;
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (!dataSnapshot.getKey().equals(mAuth.getCurrentUser().getUid())) {

                    String name = (String) dataSnapshot.child("name").getValue();
                    //String url = (String) dataSnapshot.child("profile pic").child("imageURL").getValue(); // New
                    uid.add(dataSnapshot.getKey());
                    users.add(name);
                    //users.add(new UserData(name, url));//New
                    adapter.notifyDataSetChanged();

                    Log.i("Num users insidechildAdded", Integer.toString(users.size()));
                }

                //New from here
//                counter++;
//                if (counter == MainActivity.numUsers) {
//                    CustomAdapter customAdapter = new CustomAdapter(ContactsListActivity.this, users);
//                    usersListView.setAdapter(customAdapter);
//                }
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) { }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) { }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) { }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

        usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("Inside On Click", "Here");
                Intent intent = new Intent(ContactsListActivity.this, ChatActivity.class);
                if (!MainActivity.uids.contains(uid.get(i))) {
                    Log.i("uid", uid.get(i));
                    MainActivity.uids.add(uid.get(i));
                }
                Log.i("user", users.get(i));
                intent.putExtra("user", users.get(i));
                intent.putExtra("uid", uid.get(i));
                startActivity(intent);
            }
        });
    }
}
