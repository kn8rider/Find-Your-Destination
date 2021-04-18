package com.example.pathfinder;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class PermissionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);
     if(ContextCompat.checkSelfPermission(PermissionActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
         startActivity(new Intent(PermissionActivity.this,MapActivity2.class));
         finish();
         return;
     }
       else{
           ask_permission();
     }
    }
     void ask_permission (){
         Dexter.withContext(PermissionActivity.this)
                 .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                 .withListener(new PermissionListener() {
                     @Override
                     public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                         startActivity(new Intent(PermissionActivity.this,MapActivity2.class));
                         finish();
                     }

                     @Override
                     public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                         if(permissionDeniedResponse.isPermanentlyDenied())
                         {
                             AlertDialog.Builder builder=new AlertDialog.Builder(PermissionActivity.this);
                             builder.setTitle("Permission Denied")
                                     .setMessage("Permission to access device location is permanentaly denied. you need to go to settings to allow the permission")
                                     .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                         @Override
                                         public void onClick(DialogInterface dialogInterface, int i) {
                                            ask_permission();
                                         }
                                     }).show();
                         } else
                         {
                             Toast.makeText(PermissionActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                             AlertDialog.Builder builder=new AlertDialog.Builder(PermissionActivity.this);
                             builder.setTitle("Permission Denied")
                                     .setMessage("Permission to access device location is permanentaly denied. you need to go to settings to allow the permission")
                                     .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                         @Override
                                         public void onClick(DialogInterface dialogInterface, int i) {
                                            ask_permission();
                                         }
                                     }).show();
                         }
                     }

                     @Override
                     public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                         permissionToken.continuePermissionRequest();
                     }
                 }).check();
     }

}