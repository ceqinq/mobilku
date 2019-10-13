package com.legacies.legacies.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.legacies.legacies.R;
import com.legacies.legacies.Tool.SqliteSetting;

public class Login extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initialSetup();
    }


    private void initialSetup() {
        etEmail = findViewById(R.id.etLoginEmail);
        etPassword = findViewById(R.id.etLoginPassword);

        btnLogin = findViewById(R.id.btnloginLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                if (email.length() < 5) {
                    Toast.makeText(Login.this, "Mohon isi Email dengan benar.", Toast.LENGTH_SHORT).show();
                } else if (password.length() < 6) {
                    Toast.makeText(Login.this, "Password minimal 6 karakter.", Toast.LENGTH_SHORT).show();
                } else {
                    login(email,password);
                }
            }
        });
    }

    private void login(final String email, String password) {
        final ProgressDialog pdialog = new ProgressDialog(this);
        pdialog.setMessage("Memuat...");
        pdialog.setCancelable(false);
        pdialog.show();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    final String id = email.replace(".","dot");
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User/"+id);
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                String nama = dataSnapshot.child("Nama").getValue().toString();
                                String tglLahir = dataSnapshot.child("TglLahir").getValue().toString();
                                String gender = dataSnapshot.child("Gender").getValue().toString();
                                String goldar = dataSnapshot.child("GolDar").getValue().toString();
                                String status = (Integer.parseInt(dataSnapshot.child("Status").getValue().toString()) == 1) ? "Terverifikasi" : "Belum Terverifikasi";
                                String alamat = dataSnapshot.child("Alamat").getValue().toString();
                                String lat = dataSnapshot.child("Lat").getValue().toString();
                                String lng = dataSnapshot.child("Long").getValue().toString();
                                String nomorHp = dataSnapshot.child("Phone").getValue().toString();

                                SqliteSetting setting = new SqliteSetting(Login.this);
                                setting.simpan("ID", id);
                                setting.simpan("Email", email);
                                setting.simpan("Nama", nama);
                                setting.simpan("TglLahir", tglLahir);
                                setting.simpan("Gender", gender);
                                setting.simpan("GolDar", goldar);
                                setting.simpan("Status", status);
                                setting.simpan("Alamat", alamat);
                                setting.simpan("Lat", lat);
                                setting.simpan("Long", lng);
                                setting.simpan("Phone", nomorHp);
                            }
                            pdialog.dismiss();
                            finish();
                            startActivity(new Intent(Login.this, MainActivity.class));
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                } else {
                    pdialog.dismiss();
                    Toast.makeText(Login.this, "Email atau Password salah.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }




}
