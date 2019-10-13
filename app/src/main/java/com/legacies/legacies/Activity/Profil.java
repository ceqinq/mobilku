package com.legacies.legacies.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.renderscript.Sampler;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.legacies.legacies.Adapter.ListProfilAdapter;
import com.legacies.legacies.Object.ListProfilItem;
import com.legacies.legacies.R;
import com.legacies.legacies.Tool.SqliteSetting;

import java.util.ArrayList;

public class Profil extends AppCompatActivity {

    RecyclerView review;
    SqliteSetting setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);
        setting = new SqliteSetting(this);
        initialSetup();
        cekStatus();
    }

    private void initialSetup() {
        review = findViewById(R.id.profilReview);
        review.setLayoutManager(new LinearLayoutManager(this));
        review.setHasFixedSize(true);
        review.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        setAdapter();
    }

    private void setAdapter() {

        ArrayList<ListProfilItem> arrayList = new ArrayList<>();
        arrayList.add(new ListProfilItem("ID", setting.ambil1("ID")));
        arrayList.add(new ListProfilItem("Nama", setting.ambil1("Nama")));
        arrayList.add(new ListProfilItem("Alamat Rumah", setting.ambil1("Alamat")));
        arrayList.add(new ListProfilItem("Nomor HP", setting.ambil1("Phone")));
        arrayList.add(new ListProfilItem("Golongan Darah", setting.ambil1("GolDar")));
        arrayList.add(new ListProfilItem("Gender", setting.ambil1("Gender")));
        arrayList.add(new ListProfilItem("Tanggal Lahir", setting.ambil1("TglLahir")));
        arrayList.add(new ListProfilItem("Status", setting.ambil1("Status")));

        ListProfilAdapter adapter = new ListProfilAdapter(arrayList);
        review.setAdapter(adapter);
    }


    private void cekStatus() {
        String id = setting.ambil1("ID");
        String status = setting.ambil1("Status");
        if (status.equals("Belum Terverifikasi")) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User/"+id+"/Status");
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String value = dataSnapshot.getValue().toString();
                        if (value.equals("1")) {
                            setting.simpan("Status", value);
                            setAdapter();
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }


}
