package dk.chat;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    ChildEventListener mChildEventListner;
    ChatAdapter mAdapter;
    ListView mListView;
    EditText mEditText;
    Button mButton;
    String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initFirebaseDatabase();
        userName = "Guest" + new Random().nextInt(5000);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = mEditText.getText().toString();
                if(!TextUtils.isEmpty(message)){
                    if (!TextUtils.isEmpty(message)) {
                        mEditText.setText("");
                        ChatData chatData = new ChatData();
                        chatData.userName = userName;
                        chatData.message = message;
                        chatData.time = System.currentTimeMillis();
                        mDatabaseReference.push().setValue(chatData);
                    }
                }
            }
        });
    }

    private void initView(){
        mListView = (ListView) findViewById(R.id.list_message);
        mEditText = (EditText) findViewById(R.id.edit_message);
        mButton = (Button) findViewById(R.id.btn_send);
        mAdapter = new ChatAdapter(this, 0);
        mListView.setAdapter(mAdapter);
    }

    private void initFirebaseDatabase(){
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("message");
        mChildEventListner = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ChatData chatData = dataSnapshot.getValue(ChatData.class);
                chatData.firebaseKey = dataSnapshot.getKey();
                mAdapter.add(chatData);
                mListView.smoothScrollToPosition(mAdapter.getCount());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                String firebaseKey = dataSnapshot.getKey();
                int count = mAdapter.getCount();
                for (int i = 0; i < count; i++) {
                    if (mAdapter.getItem(i).firebaseKey.equals(firebaseKey)) {
                        mAdapter.remove(mAdapter.getItem(i));
                        break;
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDatabaseReference.addChildEventListener(mChildEventListner);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        mDatabaseReference.removeEventListener(mChildEventListner);
    }




}
