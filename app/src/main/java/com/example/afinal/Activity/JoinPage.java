package com.example.afinal.Activity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.afinal.Model.User;
import com.example.afinal.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class JoinPage extends AppCompatActivity {
    EditText join_id, join_password, join_password_check,
            join_Email, join_birthday;
    Button join_button_join, join_button_back, join_button_id;

    FirebaseDatabase database;
    DatabaseReference myRef;
    LoadingDialog loadingDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_page);
        loadingDialog = new LoadingDialog(JoinPage.this);
        findID();
        bindButton();

    }
    private void findID() {
        join_id = findViewById(R.id.join_id);
        join_password = findViewById(R.id.join_password);
        join_password_check = findViewById(R.id.join_password_check);
        join_Email = findViewById(R.id.join_Email);
        join_birthday = findViewById(R.id.join_birthday);
        join_button_id = findViewById(R.id.join_button_id);
        join_button_join = findViewById(R.id.join_button_join);
        join_button_back = findViewById(R.id.join_button_back);

        join_birthday.setShowSoftInputOnFocus(false);
        join_birthday.setFocusable(false);
    }

    private void bindButton() {
        join_birthday.setRawInputType(InputType.TYPE_NULL);
        join_birthday.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            final int year = calendar.get(Calendar.YEAR);
            final int month = calendar.get(Calendar.MONTH);
            final int day = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(JoinPage.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int day) {
                    month = month + 1;
                    String date = day+"/"+month+"/"+year;
                    join_birthday.setText(date);
                }
            },year, month, day);
            datePickerDialog.show();
        });
        join_button_id.setOnClickListener(v -> {
            if(TextUtils.isEmpty(join_id.getText())) {
                join_id.setError("Empty ID");
                return;
            }
            String ID = join_id.getText().toString().trim();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            Query checkUser = reference.orderByChild("id").equalTo(ID);
            checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) Toast.makeText(JoinPage.this, "ID is not available", Toast.LENGTH_SHORT).show();
                    else Toast.makeText(JoinPage.this, "ID is available", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("Error", "onCancelled: " + error.getMessage());
                }
            });
        });
        join_button_join.setOnClickListener(v -> {
            database = FirebaseDatabase.getInstance();
            myRef = database.getReference("Users");
            //get all the values
            String ID = join_id.getText().toString().trim();
            String Password = join_password.getText().toString().trim();
            String PasswordCheck = join_password_check.getText().toString().trim();
            String Email = join_Email.getText().toString().trim();
            String Birthday = join_birthday.getText().toString().trim();
            // validate the values
            if(TextUtils.isEmpty(ID)) {
                join_id.setError("Empty ID");
                return;
            }
            if(TextUtils.isEmpty(Password)) {
                join_password.setError("Empty Password");
                return;
            }
            if(TextUtils.isEmpty(PasswordCheck)) {
                join_password_check.setError("Empty PasswordCheck");
                return;
            }
            if(TextUtils.isEmpty(Email)) {
                join_Email.setError("Empty Email");
                return;
            }
            if(TextUtils.isEmpty(Birthday)) {
                join_birthday.setError("Empty Birthday");
                return;
            }
            if(!Password.equals(PasswordCheck)){
                join_password_check.setError("Wrong Password Check");
                return;
            }
            if(!Patterns.EMAIL_ADDRESS.matcher(Email).matches()){
                join_Email.setError("Required Email@");
                return;
            }
            // pass all
            loadingDialog.startLoadingDialog();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            Query checkUser = reference.orderByChild("id").equalTo(ID);
            checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        loadingDialog.dismissDialog();
                        Toast.makeText(JoinPage.this, "ID is not available", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else {
                        User user = new User(ID,Password,Email,Birthday);
                        myRef.child(ID).setValue(user);
                        loadingDialog.dismissDialog();
                        Toast.makeText(JoinPage.this, "Member registration success", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(JoinPage.this, LoginPage.class));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("Error", "onCancelled: " + error.getMessage());
                }
            });
        });
        join_button_back.setOnClickListener(v -> {
            startActivity(new Intent(JoinPage.this, LoginPage.class));
        });
    }

}