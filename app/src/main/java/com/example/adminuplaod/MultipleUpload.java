package com.example.adminuplaod;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;


public class MultipleUpload extends Activity {
    private static final int PICK_IMAGE = 1;
    Button upload,choose;
    TextView alert;

    ArrayList<Uri> FileList = new ArrayList<Uri>();
    private Uri FileUri;
    private ProgressDialog progressDialog;
    private int upload_count = 0;
    private int uploadedImageNo;
    private SharedPreferences preferences;
    private String category;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_upload);
        upload = findViewById(R.id.select);
        choose = findViewById(R.id.upload);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("File Uploading Please Wait...........");
        preferences = getApplicationContext().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        category=preferences.getString( Constants.Category,"" );


        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                startActivityForResult(intent,PICK_IMAGE);

            }
        });



        upload.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {

                progressDialog.show();
                //alert.setText("If Loading Takes too int please Press the button again");
                switch (category){
                    case "Celebrities" : uploadedImageNo=preferences.getInt( Constants.celebNo,1 );break;
                    case "Cars" : uploadedImageNo=preferences.getInt( Constants.carNo,1 );break;
                    case "Space" : uploadedImageNo=preferences.getInt( Constants.spaceNo,1 );break;
                    case "Nature" : uploadedImageNo=preferences.getInt( Constants.natureNo,1 );break;
                    case "Buildings" : uploadedImageNo=preferences.getInt( Constants.buildingNo,1 );break;
                    case "Ocean" : uploadedImageNo=preferences.getInt( Constants.oceanNo,1 );break;
                }

                StorageReference ImageFolder = FirebaseStorage.getInstance().getReference().child(category + "/" + String.valueOf(uploadedImageNo) + ".jpg");


                for(upload_count = 0; upload_count < FileList.size(); upload_count++){


                    Uri IndividualFile = FileList.get(upload_count);
                    final StorageReference ImageName = ImageFolder.child("Image"+IndividualFile.getLastPathSegment());

                    ImageName.putFile(IndividualFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            ImageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String url = String.valueOf(uri);
                                    StoreLink(url);

                                }
                            });
                        }
                    });
                }

            }
        });

    }

    private void StoreLink(String url) {

        DatabaseReference newRef = FirebaseDatabase.getInstance().getReference().child("/"+category).child(String.valueOf(uploadedImageNo));

        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("Filelink",url);


        newRef.push().setValue(hashMap);

        progressDialog.dismiss();
        alert.setText("File Uploaded Successfully");
        upload.setVisibility(View.GONE);
        FileList.clear();


    }


    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE){

            if(resultCode == RESULT_OK){

                if(data.getClipData() != null){
                    int countClipData = data.getClipData().getItemCount();
                    int currentImageSelect = 0;
                    while (currentImageSelect < countClipData){
                        FileUri = data.getClipData().getItemAt(currentImageSelect).getUri();
                        FileList.add(FileUri);
                        currentImageSelect = currentImageSelect +1;
                    }

                    alert.setVisibility(View.VISIBLE);
                    alert.setText("You Have Selected "+ FileList.size() +" Images");
                    choose.setVisibility(View.GONE);

                }else{
                    Toast.makeText(this, "Please Select Multiple File", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }
}




