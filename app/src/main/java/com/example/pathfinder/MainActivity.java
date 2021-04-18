package com.example.pathfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
       ImageView img;
       TextView header,desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        img= findViewById(R.id.imageView);
         header=findViewById(R.id.header);
         desc=findViewById(R.id.desc);
        //Animations
        img.animate().alpha(1).translationY(600).setDuration(2000).setStartDelay(500);
        header.animate().alpha(1).translationY(-600).setDuration(2000).setStartDelay(500);
        desc.animate().alpha(1).translationY(-600).setDuration(2000).setStartDelay(500);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent= new Intent(MainActivity.this,PermissionActivity.class);
                startActivity(intent);
                finish();
            }
        },2000);
    }
}