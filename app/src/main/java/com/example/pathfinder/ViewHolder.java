package com.example.pathfinder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ViewHolder extends RecyclerView.ViewHolder {
  ImageView img;
  TextView name;
    public ViewHolder(@NonNull View itemView) {
        super(itemView);

        img = itemView.findViewById(R.id.icon);
        name = itemView.findViewById(R.id.city_name);
    }
}
