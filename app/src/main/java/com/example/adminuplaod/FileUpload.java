package com.example.adminuplaod;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.darsh.multipleimageselect.activities.AlbumSelectActivity;
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
import java.util.Objects;

public class FileUpload extends AppCompatActivity{
    private StorageReference mStorageRef;
    Button upload;
    Button select;
    int CODE=215;
    private SharedPreferences preferences;
    private Intent i;
    SharedPreferences.Editor editor;
    private static final int PICK_IMAGE = 1;
    ArrayList<Uri> FileList = new ArrayList<Uri>();
    private Uri FileUri;
    private int upload_count = 0;
    int uploadedImageNo;
    final int[] node = new int[1];
    ProgressDialog progress;
    StorageReference riversRef;
    String category;
    ImageView imageView;
    Count cn;
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
        getCount();
    }

    private void getCount(){
        //Log.e( "Msg","Getting image no" );

        FirebaseDatabase mFirebase= FirebaseDatabase.getInstance();
        DatabaseReference mdata= mFirebase.getReference("count");
        mdata.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Log.e("msg", String.valueOf( dataSnapshot.getValue()) );
                 cn = dataSnapshot.getValue( Count.class );
                switch (category) {
                    case "Celebrities":
                        node[0] = Objects.requireNonNull( cn ).getCeleb();
                        break;
                    case "Cars":
                        node[0] = Objects.requireNonNull( cn ).getCars();
                        break;
                    case "Space":
                        node[0] = Objects.requireNonNull( cn ).getSpace();
                        break;
                    case "Nature":
                        node[0] = Objects.requireNonNull( cn ).getNature();
                        break;
                    case "Buildings":
                        node[0] = Objects.requireNonNull( cn ).getBuilding();
                        break;
                    case "Ocean":
                        node[0] = Objects.requireNonNull( cn ).getOcean();
                        break;
                }

                Toast.makeText( getApplicationContext(),String.valueOf( node[0]),Toast.LENGTH_SHORT ).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void selectFile () {
        /*i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i,"Select a file"), CODE);*/

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        startActivityForResult(intent,PICK_IMAGE);
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
                    UploadFile(  );


                }else{
                    if(data.getData()!=null){

                        Uri mImageUri=data.getData();
                        FileList.add( mImageUri );

                        UploadFile();

                    }
                }


            }


        }

    }

    public  void UploadFile() {

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
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progress.setIndeterminate(true);



        for(upload_count = 0; upload_count < FileList.size(); upload_count++){
            int t= node[0]+ upload_count+1;
            Uri IndividualFile = FileList.get(upload_count);
            final StorageReference ImageName = riversRef.child("Image"+IndividualFile.getLastPathSegment());

            ImageName.putFile(IndividualFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    double progres = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    progress.show();
                    ImageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = String.valueOf(uri);
                            String name = String.valueOf( t )+ ".jpg";
                            Upload x= new Upload( name,url );
                            StoreLink(x,t);

                        }
                    });


                }
            });


        }
    }

    private void StoreLink(Upload upload, int num) {

        DatabaseReference newRef = mDatabaseRef.child("/"+category);
        newRef.push().setValue( upload );
        switch (category){
            case "Celebrities" : cn.setCeleb(  num );break;
            case "Cars" : cn.setCars( num );break;
            case "Space" : cn.setSpace( num );break;
            case "Nature" : cn.setNature( num );break;
            case "Buildings" :cn.setBuilding( num );break;
            case "Ocean" : cn.setOcean( num );break;
        }
        DatabaseReference count= mDatabaseRef.child( "/count/" );
        count.setValue( cn );

        if(num == node[0]+FileList.size()){
            progress.dismiss();
        }

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