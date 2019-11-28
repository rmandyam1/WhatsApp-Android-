package com.example.whatsapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ChatsListActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    ListView chatsListView;
    ArrayList<UserData> chats = new ArrayList<>();
    CustomAdapter customAdapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.main_menu, menu);

        FirebaseDatabase.getInstance().getReference().child("users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                MainActivity.numUsers++;
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

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.newMessage:
                Intent intent = new Intent(this, ContactsListActivity.class);
                startActivity(intent);
                break;
            case R.id.logout:
                mAuth.signOut();
                MainActivity.uids.clear();
                intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.deleteAccount:
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                MainActivity.uids.clear();

                FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).removeValue();

                user.delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.i("Info", "User account deleted.");
                                    Toast.makeText(ChatsListActivity.this, "User account deleted.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                mAuth.signOut();

                startActivity(intent);
                break;
            default:
                return false;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats_list);

        mAuth = FirebaseAuth.getInstance();

        chatsListView = findViewById(R.id.chatsListView);

        final ArrayList<String> url = new ArrayList<>();
        final ArrayList<String> name = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference().child("users").addChildEventListener(new ChildEventListener() {
            int counter = 0;
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                for (int i = 0; i < MainActivity.uids.size(); i++) {
                    Log.i("uid " + i, MainActivity.uids.get(i));
                    if (dataSnapshot.getKey().equals(MainActivity.uids.get(i))) {
                        name.add((String) dataSnapshot.child("name").getValue());
                        Log.i("name " + counter, name.get(counter));
                        url.add((String) dataSnapshot.child("profile pic").child("imageURL").getValue());
                        Log.i("url " + counter, url.get(counter));
                        chats.add(new UserData(name.get(counter), url.get(counter)));
                        counter++;
                        if (counter == MainActivity.uids.size()) {
                            customAdapter = new CustomAdapter(ChatsListActivity.this, chats);
                            chatsListView.setAdapter(customAdapter);
                        }
                    }
                }
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

        chatsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(ChatsListActivity.this, ChatActivity.class);
                Log.i("user", name.get(i));
                intent.putExtra("user", name.get(i));
                Log.i("uid", MainActivity.uids.get(i));
                intent.putExtra("uid", MainActivity.uids.get(i));

                startActivity(intent);
            }
        });



//        FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("conversations").addChildEventListener(new ChildEventListener() {
//            int counter = 0;
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                Log.i("I'M HERE", "I'M HEREEEE");
//                uid.add(dataSnapshot.getKey());
//                Log.i("uid " + counter, uid.get(counter));
//
//                FirebaseDatabase.getInstance().getReference().child("users")./*child(uid.get(counter)).*/addChildEventListener(new ChildEventListener() {
//                    int counter1 = 0;
//                    @Override
//                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                        Log.i("I'M HERE USERS", "I'M HEREEEE");
//
//                        for (int i = 0; i < uid.size(); i++) {
//                            if (dataSnapshot.getKey().equals(uid.get(i))) {
//                                name.add((String) dataSnapshot.child("name").getValue());
//                                Log.i("name " + counter1, name.get(counter1));
//                                url.add((String) dataSnapshot.child("profile pic").child("imageURL").getValue());
//                                Log.i("url " + counter1, url.get(counter1));
//                                chats.add(new UserData(name.get(counter1), url.get(counter1)));
//                                counter1++;
//                                break;
//                            }
//                        }
//                        if (counter1 == (uid.size() - 1)) {
//                            customAdapter = new CustomAdapter(ChatsListActivity.this, chats);
//                            chatsListView.setAdapter(customAdapter);
//                        }
//                    }
//                    @Override
//                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
//                    @Override
//                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }
//                    @Override
//                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) { }
//                });
//                counter++;
//            }
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) { }
//        });



//        chats.add(new UserData("JAVA", "https://www.tutorialspoint.com/java/images/java-mini-logo.jpg"));
//        chats.add(new UserData("Python", "https://www.tutorialspoint.com/python/images/python-mini.jpg"));
//        chats.add(new UserData("Javascript", "https://www.tutorialspoint.com/javascript/images/javascript-mini-logo.jpg"));
//        chats.add(new UserData("Cprogramming", "https://www.tutorialspoint.com/cprogramming/images/c-mini-logo.jpg"));
//        chats.add(new UserData("Cplusplus", "https://www.tutorialspoint.com/cplusplus/images/cpp-mini-logo.jpg"));
//        chats.add(new UserData("Android", "https://www.tutorialspoint.com/android/images/android-mini-logo.jpg"));
//

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mAuth.signOut();
        MainActivity.uids.clear();
    }
}
