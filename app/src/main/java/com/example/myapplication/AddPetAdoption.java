package com.example.myapplication;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddPetAdoption extends AppCompatActivity {

    private Spinner mGenderSpinner;
    private String save_curr_date, save_curr_time, category;

    private EditText mName, mSpecies, mBreed, mBirth;
    private CircleImageView mPicture;
    private FloatingActionButton mFabChoosePic;
    private RadioButton cat, dog;

    Calendar myCalendar = Calendar.getInstance();

    private int mGender = 0;
    public static final int GENDER_UNKNOWN = 0;
    public static final int GENDER_MALE = 1;
    public static final int GENDER_FEMALE = 2;
    private static final int gallery_pick = 1;
    private Uri filepath;
    private String p_key;
    private StorageReference storage_img_ref;
    private String download_img_url;

    private String name, species, breed, picture, birth, desc;
    private int id, gender;
    private EditText descriptionEditText;

    private Menu action;
    private Bitmap bitmap;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference AnimalRef;
    private ProgressDialog loading_bar;
    FirebaseAuth mFirebaseAuth;
    DatabaseReference UserRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pet_adoption);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mName = findViewById(R.id.fName);
        mSpecies = findViewById(R.id.Lname);
        mBreed = findViewById(R.id.phone);
        mBirth = findViewById(R.id.birth);
        mPicture = findViewById(R.id.userPic);
        mFabChoosePic = findViewById(R.id.choosePic);
        descriptionEditText = findViewById(R.id.description_textbox);
        mFirebaseAuth = FirebaseAuth.getInstance();
        UserRef = FirebaseDatabase.getInstance().getReference("Users");
        storage_img_ref = FirebaseStorage.getInstance().getReference().child("Image");
        AnimalRef = FirebaseDatabase.getInstance().getReference().child("Animals");
        mGenderSpinner = findViewById(R.id.gender);
        mBirth = findViewById(R.id.birth);

        mBirth.setFocusableInTouchMode(false);
        mBirth.setFocusable(false);
        mBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AddPetAdoption.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        mFabChoosePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFile();
            }
        });

        setupSpinner();

        Intent intent = getIntent();
        id = intent.getIntExtra("animal_id", 0);
        name = intent.getStringExtra("animal_name");
        species = intent.getStringExtra("breed");
        breed = intent.getStringExtra("birth");
        birth = intent.getStringExtra("date");
        gender = intent.getIntExtra("gender", 0);
        picture = intent.getStringExtra("image");
        desc = intent.getStringExtra("desc");
        setDataFromIntentExtra();
    }

    private void setDataFromIntentExtra() {

        if (id != 0) {

            readMode();
            getSupportActionBar().setTitle("Edit " + name.toString());
            mName.setText(name);
            mSpecies.setText(species);
            mBreed.setText(breed);
            mBirth.setText(birth);

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.skipMemoryCache(true);
            requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
            requestOptions.placeholder(R.drawable.profile);
            requestOptions.error(R.drawable.profile);

            Glide.with(AddPetAdoption.this)
                    .load(picture)
                    .apply(requestOptions)
                    .into(mPicture);

            switch (gender) {
                case GENDER_MALE:
                    mGenderSpinner.setSelection(1);
                    break;
                case GENDER_FEMALE:
                    mGenderSpinner.setSelection(2);
                    break;
                default:
                    mGenderSpinner.setSelection(0);
                    break;
            }
            cat.setChecked(true);

        } else {
            getSupportActionBar().setTitle("Add a Pet");
        }
    }

    private void setupSpinner() {
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.array_gender_options, android.R.layout.simple_spinner_item);
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mGenderSpinner.setAdapter(genderSpinnerAdapter);

        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.gender_male))) {
                        mGender = GENDER_MALE;
                    } else if (selection.equals(getString(R.string.gender_female))) {
                        mGender = GENDER_FEMALE;
                    } else {
                        mGender = GENDER_UNKNOWN;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGender = 0;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_editor, menu);
        action = menu;
        action.findItem(R.id.menu_save).setVisible(false);

        if (id == 0) {
            action.findItem(R.id.menu_edit).setVisible(false);
            action.findItem(R.id.menu_delete).setVisible(false);
            action.findItem(R.id.menu_save).setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.menu_edit:
                editMode();

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(mName, InputMethodManager.SHOW_IMPLICIT);

                action.findItem(R.id.menu_edit).setVisible(false);
                action.findItem(R.id.menu_delete).setVisible(false);
                action.findItem(R.id.menu_save).setVisible(true);

                return true;
            case R.id.menu_save:
                //Save
                if (id == 0) {
                    if (TextUtils.isEmpty(mName.getText().toString()) ||
                            TextUtils.isEmpty(mSpecies.getText().toString()) ||
                            TextUtils.isEmpty(mBreed.getText().toString()) ||
                            TextUtils.isEmpty(mBirth.getText().toString())) {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                        alertDialog.setMessage("Please complete the field!");
                        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        alertDialog.show();
                    } else {
                        validateAnimalData();
                        action.findItem(R.id.menu_edit).setVisible(true);
                        action.findItem(R.id.menu_save).setVisible(false);
                        action.findItem(R.id.menu_delete).setVisible(true);
                        readMode();
                    }

                } else {
                    updateData("update", id);
                    action.findItem(R.id.menu_edit).setVisible(true);
                    action.findItem(R.id.menu_save).setVisible(false);
                    action.findItem(R.id.menu_delete).setVisible(true);

                    readMode();
                }

                return true;
            case R.id.menu_delete:

                AlertDialog.Builder dialog = new AlertDialog.Builder(AddPetAdoption.this);
                dialog.setMessage("Delete this pet?");
                dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        deleteData("delete", id, picture);
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setBirth();
        }

    };

    private void setBirth() {
        String myFormat = "dd MMMM yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        mBirth.setText(sdf.format(myCalendar.getTime()));
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void chooseFile() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
             filepath = data.getData();
            try {

                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);
                mPicture.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void validateAnimalData() {

        String name = mName.getText().toString();
        String species = mSpecies.getText().toString();
        String breed = mBreed.getText().toString();
        int gender = mGender;
        String genderPet;
        String birth = mBirth.getText().toString();
        String desc = descriptionEditText.getText().toString();

        if (bitmap == null) {
            picture = "";
        } else if(TextUtils.isEmpty(species)) {
            Toast.makeText(this, "Breed please", Toast.LENGTH_SHORT).show();
        } else if(TextUtils.isEmpty(name))
        {
            Toast.makeText(this, "Name please", Toast.LENGTH_SHORT).show();
        }
        else if (gender == 0)
        {
            genderPet = "Female";
            picture = getStringImage(bitmap);
            storeAnimalInfo(name, genderPet, breed, birth, category, desc);
        }
        else
        {
            genderPet = "Male";
            picture = getStringImage(bitmap);
            storeAnimalInfo(name, genderPet, breed, birth, category, desc);
        }
}

    private void storeAnimalInfo(String animal_name, String gender, String breed, String birth, String category, String desc) {

//        loading_bar.setTitle("Adding new Animal");
//        loading_bar.setMessage("Please wait, while we are adding the animal");
//        loading_bar.setCanceledOnTouchOutside(false);
//        loading_bar.show();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat curr_date = new SimpleDateFormat("MMM,dd,yyyy");
        save_curr_date = curr_date.format(calendar.getTime());
        SimpleDateFormat curr_time = new SimpleDateFormat("HH:mm:ss a");
        save_curr_time = curr_time.format(calendar.getTime());
        //create random key for each product
        //instead using fire-base unique key, i will use the date and the time
        //TODO- change this
        p_key = (String) save_curr_time + save_curr_date;

        final StorageReference path = storage_img_ref.child(filepath.getLastPathSegment() + p_key + ".jpg");
        final UploadTask uploadTask = path.putFile(filepath);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String msg = e.toString();
                Toast.makeText(AddPetAdoption.this, "Error : " + msg, Toast.LENGTH_SHORT).show();
               // loading_bar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AddPetAdoption.this, "Animal image upload successes!", Toast.LENGTH_SHORT).show();
                Task<Uri> url_task = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        download_img_url = path.getDownloadUrl().toString();
                        return path.getDownloadUrl();
                    }
                }).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(AddPetAdoption.this, "Getting animal image url successfully", Toast.LENGTH_SHORT).show();
                        download_img_url = task.getResult().toString();
                        saveAnimalInfoToDatabese(animal_name, gender, breed, birth, download_img_url, category, save_curr_time, save_curr_date, p_key, desc);
                    }
                });
            }
        });
    }

    private void saveAnimalInfoToDatabese(String animal_name, String gender, String breed, String birth, String download_img_url, String category, String time, String date, String animal_id, String desc) {

        AnimalRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String id = AnimalRef.push().getKey();
                if (!(snapshot.child("Animals").child(id).exists())) {
                    HashMap<String, Object> animal_map = new HashMap<>();
                    animal_map.put("animal_id", id);
                    animal_map.put("animal_name", animal_name);
                    animal_map.put("date", date);
                    animal_map.put("time", time);
                    animal_map.put("gender", gender);
                    animal_map.put("breed", breed);
                    animal_map.put("birth-date", birth);
                    animal_map.put("desc", desc);
                    animal_map.put("imageUrl", download_img_url);

                    AnimalRef.child(id).updateChildren(animal_map).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                            Toast.makeText(AddPetAdoption.this, "Pet is added successfully added for adoption", Toast.LENGTH_SHORT).show();
                        } else {
                            loading_bar.dismiss();
                            String msg = task.getException().toString();
                            Toast.makeText(AddPetAdoption.this, "Error : " + msg, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void updateData(final String key, final int id) {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating...");
        progressDialog.show();

        readMode();

        String name = mName.getText().toString().trim();
        String species = mSpecies.getText().toString().trim();
        String breed = mBreed.getText().toString().trim();
        int gender = mGender;
        String birth = mBirth.getText().toString().trim();
        String picture = null;
        if (bitmap == null) {
            picture = "";
        } else {
            picture = getStringImage(bitmap);
        }


    }

    private void deleteData(final String key, final int id, final String pic) {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Deleting...");
        progressDialog.show();

        readMode();


    }

    void readMode(){

        mName.setFocusableInTouchMode(false);
        mSpecies.setFocusableInTouchMode(false);
        mBreed.setFocusableInTouchMode(false);
        mName.setFocusable(false);
        mSpecies.setFocusable(false);
        mBreed.setFocusable(false);

        mGenderSpinner.setEnabled(false);
        mBirth.setEnabled(false);

        mFabChoosePic.setVisibility(View.INVISIBLE);

    }

    private void editMode(){

        mName.setFocusableInTouchMode(true);
        mSpecies.setFocusableInTouchMode(true);
        mBreed.setFocusableInTouchMode(true);

        mGenderSpinner.setEnabled(true);
        mBirth.setEnabled(true);

        mFabChoosePic.setVisibility(View.VISIBLE);
    }
}