package com.example.virtualclassroom;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

//import android.widget.CustomAdapter;

public class Blog extends AppCompatActivity {
    String[] images;
    String[] titles;
    String[] description;
    ListView lView;
    FloatingActionButton fab;
    ListAdapter lAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog);
        final ListView list = findViewById(R.id.list);
        lView = (ListView) findViewById(R.id.list);


        fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent inten = new Intent(getBaseContext(),SubmitActivity.class);
                startActivity(inten);
            }
        });
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("articles");

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
//                ArticlesList.removeAll();
                int size = (int) dataSnapshot.getChildrenCount();
//                size  = 1;
                images = new String[size];
                titles = new String[size];
                description = new String[size];
                System.out.println(dataSnapshot.getChildrenCount());
                int i = 0;
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String value = ds.getValue(String.class);
                    try {
                        JSONObject jsonObject = new JSONObject(value);
                        String url = "images/"+jsonObject.get("url").toString();
                        System.out.println(url);
                        String body = jsonObject.get("text").toString();
                        System.out.println(body);
                        images[i] = url;
                        description[i] = body;
                        titles[i] = jsonObject.get("username").toString();
                        i++;
                    }catch (JSONException err){
                        Log.d("Error", err.toString());
                    }

                }
                System.out.println(images);
                lAdapter = new ListAdapter(Blog.this, titles, description, images);
                lView.setAdapter(lAdapter);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });

//        lView.setAdapter(lAdapter);
        System.out.println("Already set the Adapter *****************************");

        lView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent inten = new Intent(getBaseContext(),ShowArticleActivity.class);
                inten.putExtra("url",images[i]);
                inten.putExtra("title",titles[i]);
                inten.putExtra("description",description[i]);
                startActivity(inten);
//                Toast.makeText(Blog.this, titles[i]+" "+description[i], Toast.LENGTH_SHORT).show();

            }
        });
    }
}

