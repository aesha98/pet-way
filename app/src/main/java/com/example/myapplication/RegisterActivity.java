package com.example.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {


    private Button create_account_btn;
    private EditText InputName, InputPhone, InputPassword, InputEmail;

    private ProgressDialog loadingBar;

    FirebaseAuth mFirebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference UserRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        create_account_btn = (Button) findViewById(R.id.register_btn);
        InputName = (EditText) findViewById(R.id.register_username_input);
        InputPhone = (EditText) findViewById(R.id.register_phone_number_input);
        InputPassword = (EditText) findViewById(R.id.register_passwordr_input);
        InputEmail = (EditText) findViewById(R.id.register_email);
        TextView textSignin = (TextView)findViewById(R.id.txtSignin);

       textSignin.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
            Intent intent = new Intent(RegisterActivity.this, LogInActivity.class);
               startActivity(intent);
           }
       });


        mFirebaseAuth = FirebaseAuth.getInstance();
        UserRef = FirebaseDatabase.getInstance().getReference("Users");

        create_account_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = InputName.getText().toString();
                String email = InputEmail.getText().toString();
                String phone = InputPhone.getText().toString();
                String pass = InputPassword.getText().toString();


                if(pass.isEmpty() || name.isEmpty() || email.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setMessage("Email or password is wrong").setTitle("Warning").setPositiveButton("OK", null);

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else
                {
                    mFirebaseAuth.createUserWithEmailAndPassword(name,pass).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isComplete())
                            {
                                loadingBar.setTitle("Create Account");
                                loadingBar.setMessage("Please wait while we checking you data...");
                                loadingBar.setCanceledOnTouchOutside(false);
                                loadingBar.show();
                                AddUserToDatabase(name, phone, email);
                                gotomainpage();
                            }
                            else
                            {
                                Toast.makeText(RegisterActivity.this, "Error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });
    }
    private void gotomainpage()
    {
        Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    };



    private void AddUserToDatabase(final String name, final String phone, final String email) {

        final DatabaseReference rootReff;
        rootReff = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        String userKey = user.getUid();

        rootReff.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!(dataSnapshot.child("Users").child(userKey).exists())) {
                    HashMap<String, Object> userDataMap = new HashMap<>();
                    userDataMap.put("username", name);
                    userDataMap.put("email", email);
                    userDataMap.put("phone", phone);
                    userDataMap.put("fName", null);
                    userDataMap.put("lname", null);
                    userDataMap.put("birth", null);
                    userDataMap.put("gender", null);
                    userDataMap.put("profile_picture", null);

                    rootReff.child("Users").child(userKey).updateChildren(userDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                Toast.makeText(RegisterActivity.this, "Congratulation your account has been created ", Toast.LENGTH_SHORT).show();


                               Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                                startActivity(intent);

                            }
                            else{
                                loadingBar.dismiss();
                                Toast.makeText(RegisterActivity.this, " Network Error: try again after some time... ", Toast.LENGTH_SHORT).show();



                            }
                        }
                    });

                } else {
                    Toast.makeText(RegisterActivity.this, "This" + phone + "Already exists", Toast.LENGTH_SHORT).show();

                    Toast.makeText(RegisterActivity.this, "This" + phone + "Please try another email", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}