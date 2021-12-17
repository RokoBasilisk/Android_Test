package com.example.afinal.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.afinal.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginPage extends AppCompatActivity {
    EditText login_id, login_password;
    Button login_button_login, login_button_join;
    boolean check = false;
    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        loadingDialog = new LoadingDialog(LoginPage.this);
        findID();
        bindButton();
    }
    private void findID() {
        login_id = findViewById(R.id.login_id);
        login_password = findViewById(R.id.login_password);
        login_button_login = findViewById(R.id.login_button_login);
        login_button_join = findViewById(R.id.login_button_join);
    }
    private void bindButton() {
        login_button_login.setOnClickListener(v -> {
            loadingDialog.startLoadingDialog();
            String ID = login_id.getText().toString().trim();
            String Password = login_password.getText().toString().trim();
            // validate the values
            if(TextUtils.isEmpty(ID)) {
                login_id.setError("Empty ID");
                return;
            }
            if(TextUtils.isEmpty(Password)) {
                login_password.setError("Empty Password");
                return;
            }
            // pass all
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            Query checkUser = reference.orderByChild("id").equalTo(ID);
            checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(!snapshot.exists()) {
                        loadingDialog.dismissDialog();
                        Toast.makeText(LoginPage.this, "ID/password is incorrect", Toast.LENGTH_SHORT).show();
                    } else {
                        String passwordfromDB = snapshot.child(ID).child("password").getValue(String.class);
                        if(!passwordfromDB.equals(Password)) {
                            loadingDialog.dismissDialog();
                            Toast.makeText(LoginPage.this, "ID/password is incorrect", Toast.LENGTH_SHORT).show();
                        } else {
                            loadingDialog.dismissDialog();
                            startActivity(new Intent(LoginPage.this, MainPage.class));
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("Error", "onCancelled: " + error.getMessage());
                }
            });
        });
        login_button_join.setOnClickListener(v -> {
            startActivity(new Intent(LoginPage.this, JoinPage.class));
        });
    }
}