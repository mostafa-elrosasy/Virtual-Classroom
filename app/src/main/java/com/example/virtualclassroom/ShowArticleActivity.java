package com.example.virtualclassroom;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ShowArticleActivity extends AppCompatActivity {

//    private StorageReference mStorageRef;
    ImageView viewImage;
    TextView titl,des;
    List<ImageUploadInfo> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_article);


        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        String title = intent.getStringExtra("title");
        String description = intent.getStringExtra("description");
        titl = findViewById(R.id.title);
        des = findViewById(R.id.desc);
        titl.setText(title);
        des.setText(description);

        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(url);
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
        {
            @Override
            public void onSuccess(Uri downloadUrl)
            {
                System.out.println(downloadUrl);
                new DownloadImageTask((ImageView) findViewById(R.id.viewImage))
                .execute(downloadUrl.toString());
            }
        });



        // Read from the database







//        new DownloadImageTask((ImageView) findViewById(R.id.viewImage))
//                .execute("gs://signupsys-95524.appspot.com/images/image%253A143193");


//        Picasso.get().load("http://i.imgur.com/DvpvklR.png").into(viewImage);


//        mStorageRef = FirebaseStorage.getInstance().getReference();
//
//        File localFile = null;
//        try {
//            localFile = File.createTempFile("images", "jpg");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        StorageReference riversRef = mStorageRef.child("images/image%3A143188");
//
//        riversRef.getFile(localFile)
//                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                        // Successfully downloaded data to local file
//                        // ...
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                // Handle failed download
//                // ...
//            }
//        });
    }
}
class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;

    public DownloadImageTask(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);
    }
}

class ImageUploadInfo {

    public String imageName;

    public String imageURL;

    public ImageUploadInfo() {

    }

    public ImageUploadInfo(String name, String url) {

        this.imageName = name;
        this.imageURL= url;
    }

    public String getImageName() {
        return imageName;
    }

    public String getImageURL() {
        return imageURL;
    }

}