package com.example.pathfinder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;

public class MapActivity2 extends AppCompatActivity {

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    public SearchView searchView;
    public String city="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map2);
      searchView = findViewById(R.id.search_bar);
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        SecondFragment f1 = new SecondFragment();
        fragmentTransaction.setReorderingAllowed(true).add(R.id.fragment_container,f1).commit();

    }

    public void add_map_fragment(){
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        SecondFragment f1 = new SecondFragment();
        fragmentTransaction.setReorderingAllowed(true).replace(R.id.fragment_container,f1).commit();
    }

    public void add_suggestion_fragment(){
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        FirstFragment f1 = new FirstFragment();
        fragmentTransaction.setReorderingAllowed(true).replace(R.id.fragment_container,f1).commit();
    }
}