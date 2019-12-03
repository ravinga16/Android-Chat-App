package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MessagesActivity extends AppCompatActivity {

    private Button SendMessage;
    private TextView displayMessage;
    private EditText inputMesaage;

    private String room_name, user_name;
    private DatabaseReference root;

    String tempKey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        //bind

        SendMessage = findViewById(R.id.send);
        displayMessage = findViewById(R.id.display_message);
        inputMesaage = findViewById(R.id.input_message);

        room_name = getIntent().getExtras().get("room_name").toString();
        user_name = getIntent().getExtras().get("user_name").toString();

        setTitle("Room Name:"+ room_name);
        root = FirebaseDatabase.getInstance().getReference().child(room_name);

        SendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> uniqueKeyMap = new HashMap<>();
                tempKey = root.push().getKey();
                root.updateChildren(uniqueKeyMap);

                DatabaseReference userRef = root.child(tempKey);
                Map<String, Object> userMessageMap = new HashMap<>();
                userMessageMap.put("name", user_name);
                userMessageMap.put("message", inputMesaage.getText().toString());
                userRef.updateChildren(userMessageMap);
            }
        });

        root.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Append_Chat_Conversation(dataSnapshot);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    String chatMessage, chatUserName;
    private void  Append_Chat_Conversation(DataSnapshot dataSnapshot){
        Iterator i = dataSnapshot.getChildren().iterator();

        while(i.hasNext()){
            chatMessage = (String) ((DataSnapshot)i.next()).getValue();
            chatUserName = (String) ((DataSnapshot)i.next()).getValue();

            displayMessage.append(chatUserName + ":" + chatMessage + "\n \n");
        }
    }
}
