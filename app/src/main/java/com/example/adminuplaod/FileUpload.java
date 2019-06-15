package com.example.adminuplaod;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.darsh.multipleimageselect.activities.AlbumSelectActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class FileUpload extends AppCompatActivity{
    private StorageReference mStorageRef;
    Button upload;
    Button select;
    int CODE=215;
    private SharedPreferences preferences;
    private Intent i;
    SharedPreferences.Editor editor;

    int uploadedImageNo;
    ProgressDialog progress;
    StorageReference riversRef;
    String category;
    ImageView imageView;
    private DatabaseReference mDatabaseRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_upload);

        preferences = getApplicationContext().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        category=preferences.getString( Constants.Category,"" );
        Log.e("Log",category);
        select= findViewById(R.id.upload);
        select.setOnClickListener(v -> selectFile());
        imageView=findViewById(R.id.showfile);
        upload=findViewById( R.id.select );
    }
    public void selectFile ()
    {
        i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i,"Select a file"), CODE);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String filePath = data.getDataString();
         imageView.setImageURI(data.getData());
         imageView.setVisibility(View.VISIBLE);

        Uri SelectedFileLocation=Uri.parse(filePath);
        upload.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UploadFile(SelectedFileLocation);
            }
        } );


        super.onActivityResult(requestCode, resultCode, data);
    }

    public  void UploadFile(Uri file) {

        mDatabaseRef= FirebaseDatabase.getInstance().getReference();
        uploadedImageNo = 0;
        switch (category){
            case "Celebrities" : uploadedImageNo=preferences.getInt( Constants.celebNo,1 );break;
            case "Cars" : uploadedImageNo=preferences.getInt( Constants.carNo,1 );break;
            case "Space" : uploadedImageNo=preferences.getInt( Constants.spaceNo,1 );break;
            case "Nature" : uploadedImageNo=preferences.getInt( Constants.natureNo,1 );break;
            case "Buildings" : uploadedImageNo=preferences.getInt( Constants.buildingNo,1 );break;
            case "Ocean" : uploadedImageNo=preferences.getInt( Constants.oceanNo,1 );break;
        }

        riversRef = mStorageRef.child(category + "/" + String.valueOf(uploadedImageNo) + ".jpg");
        progress = new ProgressDialog(this);
        progress.setMessage("Uploading Image");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.show();

        new Thread(() -> riversRef.putFile(file)
                .addOnSuccessListener(taskSnapshot -> {
                    riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                           Toast.makeText(getApplicationContext(),uri.toString(),Toast.LENGTH_LONG).show();
                            Upload upload = new Upload(String.valueOf(uploadedImageNo) + ".jpg",
                                    uri.toString());

                            DatabaseReference newRef = mDatabaseRef.child("/"+category).child(String.valueOf(uploadedImageNo));
                            newRef.setValue(upload);

                            DatabaseReference count=mDatabaseRef.child( "/count/" );
                            Count count1=new Count( preferences.getInt( Constants.celebNo,0 ),preferences.getInt( Constants.carNo,0),
                                                    preferences.getInt( Constants.buildingNo,0 ),preferences.getInt( Constants.natureNo,0 ),
                                                    preferences.getInt( Constants.spaceNo,0 ),preferences.getInt( Constants.oceanNo,0 ) );
                            count.setValue( count1 );

                        }
                    });
                    Toast.makeText(FileUpload.this, "Upload Success", Toast.LENGTH_SHORT).show();
                    progress.dismiss();
                    progress.cancel();

                    uploadedImageNo = uploadedImageNo + 1;
                    editor = preferences.edit();
                    switch (category){
                        case "Celebrities" : editor.putInt( Constants.celebNo,uploadedImageNo );break;
                        case "Cars" : editor.putInt( Constants.carNo,uploadedImageNo );break;
                        case "Space" : editor.putInt( Constants.spaceNo,uploadedImageNo );break;
                        case "Nature" : editor.putInt( Constants.natureNo,uploadedImageNo );break;
                        case "Buildings" :editor.putInt( Constants.buildingNo,uploadedImageNo );break;
                        case "Ocean" : editor.putInt( Constants.oceanNo,uploadedImageNo );break;
                    }

                    editor.apply();
                })
                .addOnFailureListener(exception -> {
                    progress.dismiss();
                    progress.cancel();
                    Toast.makeText(FileUpload.this, "Upload Failed", Toast.LENGTH_SHORT).show();
                    exception.printStackTrace();
                })).start();



    }

    @Override
    public void onDestroy() {
        super.onDestroy();


    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
    }
}