package com.example.myapplication.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.myInterface.AnimalClickListner;

public class AnimalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


    public TextView txt_animal_name;
    public TextView txt_animal_info;
    public ImageView img_view;
    public AnimalClickListner listener;


    public AnimalViewHolder(@NonNull View itemView) {
        super(itemView);

        img_view = (ImageView) itemView.findViewById(R.id.places_img);
        txt_animal_name = (TextView) itemView.findViewById(R.id.animal_name);
        txt_animal_info = (TextView) itemView.findViewById(R.id.place_detail);

    }


    public void setAnimalClickListener(AnimalClickListner listener) {

        this.listener = listener;
    }


    @Override
    public void onClick(View v) {
        listener.onClick(v, getAdapterPosition(), false);
    }
}
