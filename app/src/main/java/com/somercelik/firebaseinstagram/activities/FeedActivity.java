package com.somercelik.firebaseinstagram.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.somercelik.firebaseinstagram.adapters.FeedRecyclerAdapter;
import com.somercelik.firebaseinstagram.R;

import java.util.ArrayList;
import java.util.Map;

public class FeedActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    ArrayList<String> userEmailFromFirebase;
    ArrayList<String> userCommentFromFirebase;
    ArrayList<String> userImageFromFirebase;
    FeedRecyclerAdapter feedRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        userEmailFromFirebase = new ArrayList<>();
        userCommentFromFirebase = new ArrayList<>();
        userImageFromFirebase = new ArrayList<>();

        getDataFromFirestore();

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        feedRecyclerAdapter = new FeedRecyclerAdapter(userEmailFromFirebase, userCommentFromFirebase, userImageFromFirebase);
        recyclerView.setAdapter(feedRecyclerAdapter);
    }


    //Menuyu bağlamak için
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.options_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //Menude seçilenler için
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.upload) {     //Add post'a tıklanınca
            Intent uploadIntent = new Intent(FeedActivity.this, UploadActivity.class);
            startActivity(uploadIntent);
        } else if (item.getItemId() == R.id.signout) {  //Signout'a tıklanırsa
            firebaseAuth.signOut();
            Intent intentToSignIn = new Intent(FeedActivity.this, SignInUpActivity.class);
            startActivity(intentToSignIn);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void getDataFromFirestore() {
        CollectionReference collectionReference = firebaseFirestore.collection("Posts");
        //Tarihe göre azalan bir sırada gösterim için kullanıyoruz
        collectionReference.orderBy("date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(FeedActivity.this, error.getLocalizedMessage().toString(), Toast.LENGTH_LONG).show();
                }
                if (value != null) {
                    for (DocumentSnapshot snapshot : value.getDocuments()) {
                        Map<String, Object> data = snapshot.getData();
                        String comment = (String) data.get("comment");
                        String userEmail = (String) data.get("userEmail");
                        String downloadUrl = (String) data.get("downloadUrl");
                        userCommentFromFirebase.add(comment);
                        userEmailFromFirebase.add(userEmail);
                        userImageFromFirebase.add(downloadUrl);
                        feedRecyclerAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

    }



}