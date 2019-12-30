package com.example.virtualclassroom;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class SubmitActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 234;
    Button chooseBtn, submitBtn;
    ImageView previewImage;
    TextView content;
    private Uri filePath;
    private String name;
    private StorageReference mStorageRef;
    DatabaseReference myRef;
    FirebaseDatabase database = FirebaseDatabase.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);

        chooseBtn = findViewById(R.id.chooseBtn);
        submitBtn = findViewById(R.id.submitBtn);
        previewImage = findViewById(R.id.previewImage);
        content = findViewById(R.id.sendText);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        myRef = database.getReference("articles");
    }


    public void choose(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                String[] arrOfStr = filePath.toString().split("/");
                this.name = arrOfStr[arrOfStr.length-1];
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                previewImage.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String uploadFile() {
        //if there is a file to upload
        if (filePath != null) {

            //displaying a progress dialog while upload is going on
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();
            StorageReference riversRef = mStorageRef.child("images/"+this.name);
            riversRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //if the upload is successfull
                            //hiding the progress dialog
                            progressDialog.dismiss();

                            //and displaying a success toast
                            Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            //if the upload is not successfull
                            //hiding the progress dialog
                            progressDialog.dismiss();

                            //and displaying error message
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //calculating progress percentage
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                            //displaying percentage in progress dialog
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
            return riversRef.toString();
        }
        //if there is not any file
        else {
            return null;
        }
    }


    private void makeObject(String url,String text) {
        JSONObject article = new JSONObject();
        try {
            article.put("text",text);
            article.put("url",this.name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        myRef.push().setValue(article.toString());
        System.out.println(article);
    }

    public void submit(View view){
        String url = uploadFile();
        String text = content.getText().toString();
        if(url != null){
            makeObject(url,text);
            finish();
        }else{
            Toast.makeText(getApplicationContext(), "the url is null", Toast.LENGTH_LONG).show();
        }
    }


}
