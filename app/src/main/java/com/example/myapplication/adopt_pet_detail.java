package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class adopt_pet_detail extends AppCompatActivity {

    private TextView status, breed, gender, age, date, name, desc;
    private Button contactOwner;
    private ImageView pet_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adopt_pet_detail);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        status = findViewById(R.id.status_txt);
        breed = findViewById(R.id.city_text);
        gender = findViewById(R.id.contact_txt);
        age = findViewById(R.id.distance_txt);
        date = findViewById(R.id.date_txt);
        name = findViewById(R.id.pet_name);
        desc = findViewById(R.id.description);

        contactOwner = findViewById(R.id.btnVet);
        pet_image = findViewById(R.id.imageMain);

        Intent intent = getIntent();
        String animal_name = intent.getStringExtra("animal_name");
        String breed_pet = intent.getStringExtra("breed");
        String gender_pet = intent.getStringExtra("gender");
        String imageUrl = intent.getStringExtra("imageUrl");
        String age_pet = intent.getStringExtra("birth");
        String date_post = intent.getStringExtra("date-posted");
        String desc_text = intent.getStringExtra("desc");
       // String status_adoption = intent.getStringExtra("status");
        String phone = intent.getStringExtra("phone");

        name.setText(animal_name);
        status.setText("available");
        breed.setText(breed_pet);
        gender.setText(gender_pet);
        age.setText(age_pet);
        date.setText(date_post);
        desc.setText(desc_text);

        Glide.with(this)
                .load(imageUrl)
                .into(pet_image);

        contactOwner.setOnClickListener(view -> {
            String phoneNumber = "tel:" + phone;
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse(phoneNumber));
            if (callIntent.resolveActivity(getPackageManager()) !=null)
            {
                startActivity(callIntent);
            }
            else
            {
                Toast.makeText(this, "ERROR : app not found",
                        Toast.LENGTH_SHORT).show();
            }
        });

    }
}