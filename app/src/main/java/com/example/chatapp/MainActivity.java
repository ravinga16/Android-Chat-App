package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ListView listView_of_ChatToomsNames;
    private EditText room_name;
    private Button add_room_button;

    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> list_of_rooms = new ArrayList<>();

    private String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //bind
        room_name = findViewById(R.id.add_room);
        add_room_button = findViewById(R.id.add_room_button);
        listView_of_ChatToomsNames = findViewById(R.id.add_rooms_listView);

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list_of_rooms);
        listView_of_ChatToomsNames.setAdapter(arrayAdapter);

        Request_UserName();

        add_room_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference mReference = database.getReference(room_name.getText().toString() );
                mReference.setValue("");

                room_name.setText("");
                room_name.requestFocus();

            }
        });

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mReference = database.getReference(room_name.getText().toString() );
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: ==========>>>>");
                Set<String> set = new HashSet<>();
                Iterator i = dataSnapshot.getChildren().iterator();
                while(i.hasNext()){
                    set.add(((DataSnapshot)i.next()).getKey());
                }

                list_of_rooms.clear();
                list_of_rooms.addAll(set);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        listView_of_ChatToomsNames.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                System.out.println(list_of_rooms.get(i));
                Intent intent = new Intent(MainActivity.this, MessagesActivity.class);
                intent.putExtra("user_name", name);
                intent.putExtra("room_name", list_of_rooms.get(i).toString());
                startActivity(intent);
            }
        });

    }

    private void Request_UserName(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("enter user name");
        final EditText inputField = new EditText(MainActivity.this);
        builder.setView(inputField);

        builder.setPositiveButton("Ok", new Dialog.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                name = inputField.getText().toString();
                System.out.println(name);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();

                Request_UserName();
            }
        });

        builder.show();

    }
}
