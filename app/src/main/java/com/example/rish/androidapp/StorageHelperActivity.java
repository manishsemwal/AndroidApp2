package com.example.rish.androidapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;

import android.net.Uri;

import android.support.annotation.NonNull;

import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


public class StorageHelperActivity extends AppCompatActivity {
    static final int UploadFromSelectApp = 9501;
    static final int UploadFromFilemanager = 9502;

    final String pathtofirebase = "gs://androidapp-6745a.appspot.com/MathsOlympiad/";
    final String pathtofirebaseupload = "gs://androidapp-6745a.appspot.com/MathsOlympiadUpload/";
    private static String downloaddirpath = "/storage/emulated/0/Olympy/";
    private  String name;
    private boolean status;
    private String extention = ".pdf";
    public static File dir = new File(downloaddirpath);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage_helper);
        name=getIntent().getStringExtra("Name");
        status=getIntent().getBooleanExtra("Status",false);
        String path=pathtofirebase+name+extention;
        if(isNetworkAvaliable(getApplicationContext())) {
            if (!status) {

                DownloadFromFirebaseFromPath(downloaddirpath, path);


            } else


            {

// UploadToFirebaseFromFilemanager("Testing");
                UploadToFirebaseFromSelectedApp("Testing2");


            }
        }

        else{
            Toast.makeText(getApplicationContext(),"Error No internet connection",Toast.LENGTH_SHORT).show();
        }
    }

    private void DownloadFromFirebaseFromPath(String downloadPathTo, String downloadPathFrom) {
        dir.mkdirs();
        final String DownloadPathTo = downloadPathTo;
        final String DownloadPathFrom = downloadPathFrom;
        final StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(DownloadPathFrom);


        Toast.makeText(getApplicationContext(), "Download file ...", Toast.LENGTH_SHORT).show();

        File file = new File(DownloadPathTo + name + DownloadPathFrom.substring(DownloadPathFrom.lastIndexOf('.')));
        final int flag[]=new int[1];flag[0]=1;
        if (file.exists()) {
            final File tempfile=file;
            storageReference.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                @Override
                public void onSuccess(StorageMetadata storageMetadata) {
                    if( storageMetadata.getSizeBytes()==tempfile.getTotalSpace()){
                        Toast.makeText(getApplicationContext(),"File already exists",Toast.LENGTH_SHORT).show();
                    }
                    else
                        flag[0]=0;
                }
            });
            if(flag[0]==0)
                file.delete();
        }
        try {
            storageReference.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getApplicationContext(), "Download completed", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Download Failed : " + e.toString(), Toast.LENGTH_SHORT).show();
                }
            });


        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Download Failed : " + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void UploadToFirebaseFromSelectedApp (String uploadName)
    {
        String UploadName = uploadName;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(Intent.createChooser(intent, "Upload from ..."), UploadFromSelectApp);
    }

    private void UploadToFirebaseFromFilemanager (String uploadName)
    {
        String UploadName = uploadName;
        Intent intent = new Intent("com.sec.android.app.myfiles.PICK_DATA");
        intent.putExtra("CONTENT_TYPE", "*/*");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        startActivityForResult(intent, UploadFromFilemanager);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        if (requestCode == UploadFromFilemanager)
        {
            final Uri currFileURI = intent.getData();

            try{
                Toast.makeText(getApplicationContext(), "Upload file ...", Toast.LENGTH_SHORT).show();
                String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
                StorageReference storageReference = FirebaseStorage.getInstance().getReference(pathtofirebaseupload+timeStamp+currFileURI.getLastPathSegment());
                storageReference.putFile(currFileURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getApplicationContext(), "File successfully uploaded..", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Uploading Failed..", Toast.LENGTH_SHORT).show();
                    }
                });}catch(Exception e){
                Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == UploadFromSelectApp)
        {
            Toast.makeText(getApplicationContext(), "Upload file  selected app...", Toast.LENGTH_SHORT).show();
            final Uri uri = intent.getData();


            try
            { StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(pathtofirebaseupload+uri.getLastPathSegment());
                storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getApplicationContext(), "File successfully uploaded..", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Uploading Failed..", Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        //TODO
                    }
                });



            } catch (Exception e) {
                Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
            }


        }


        super.onActivityResult(requestCode, resultCode, intent);
    }


    public static boolean isNetworkAvaliable(Context ctx) {
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if ((connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null && connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED)
                || (connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI) != null && connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .getState() == NetworkInfo.State.CONNECTED)) {
            return true;
        } else {
            return false;
        }
    }


}
