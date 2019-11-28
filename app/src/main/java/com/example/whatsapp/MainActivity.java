package com.example.whatsapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText emailEditText;
    EditText passEditText;
    Button goButton;
    TextView insteadTextView;
    FirebaseAuth mAuth;
    boolean isLogin = true;
    static ArrayList<String> uids = new ArrayList<>();
    static int numUsers = 0;

    /*
    <div>
        Icons made by <a href="https://www.flaticon.com/authors/smashicons" title="Smashicons">Smashicons</a>
        from <a href="https://www.flaticon.com/" title="Flaticon">www.flaticon.com</a>
         is licensed by <a href="http://creativecommons.org/licenses/by/3.0/" title="Creative Commons BY 3.0" target="_blank">CC 3.0 BY</a>
     </div>
     */

    public void changeScreen(View view) {
        if (isLogin) {
            isLogin = false;
            goButton.setText("SIGN UP");
            insteadTextView.setText("login instead");
        } else {
            isLogin = true;
            goButton.setText("LOGIN");
            insteadTextView.setText("sign up instead");
        }
    }

    public void onClick(View view) {
        InputMethodManager mgr = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

        if (isLogin) {
            mAuth.signInWithEmailAndPassword(emailEditText.getText().toString(), passEditText.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.i("Info", "signInWithEmail:success");
                                FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("conversations").addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                        uids.add(dataSnapshot.getKey());
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
                                Intent intent = new Intent(MainActivity.this, ChatsListActivity.class);
                                startActivity(intent);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.i("Info", "signInWithEmail:failure " + task.getException());
                                Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            mAuth.createUserWithEmailAndPassword(emailEditText.getText().toString(), passEditText.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.i("Info", "createUserWithEmail:success");
                                String email = emailEditText.getText().toString();
                                String name = email.substring(0, email.indexOf('@'));
                                //uids.add(task.getResult().getUser().getUid());
                                FirebaseDatabase.getInstance().getReference().child("users").child(task.getResult().getUser().getUid()).child("email").setValue(emailEditText.getText().toString());
                                FirebaseDatabase.getInstance().getReference().child("users").child(task.getResult().getUser().getUid()).child("name").setValue(name);
                                Intent intent = new Intent(MainActivity.this, ProfilePicActivity.class);
                                startActivity(intent);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.i("Info", "createUserWithEmail:failure " + task.getException());
                                Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            Log.i("Info", mAuth.getCurrentUser().getEmail());
            FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("conversations").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    uids.add(dataSnapshot.getKey());
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
            Intent intent = new Intent(MainActivity.this, ChatsListActivity.class);
            startActivity(intent);
        }

        emailEditText = findViewById(R.id.emailEditText);
        passEditText = findViewById(R.id.passEditText);
        goButton = findViewById(R.id.goButton);
        insteadTextView = findViewById(R.id.insteadTextView);
    }
}
