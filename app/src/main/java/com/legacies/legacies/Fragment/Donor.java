package com.legacies.legacies.Fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.legacies.legacies.Activity.Profil;
import com.legacies.legacies.Adapter.DonorAdapter;
import com.legacies.legacies.Object.DonorItem;
import com.legacies.legacies.R;
import com.legacies.legacies.Tool.SGps;
import com.legacies.legacies.Tool.SqliteSetting;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */

public class Donor extends Fragment {

    Button btnDonor;
    Activity activity;
    RecyclerView recyclerView;
    ArrayList<DonorItem> donorArray = new ArrayList<>();
    TextView tvBelumAda, tvCooldown, tvSaldo;


    public Donor(Activity activity) {
        // Required empty public constructor
        this.activity = activity;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_donor, container, false);
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        initialSetup(view);
        ambilLog();
        return view;
    }

    private void initialSetup(View view) {
        tvSaldo = view.findViewById(R.id.tvSaldo);
        btnDonor = view.findViewById(R.id.btnDonor);
        tvBelumAda = view.findViewById(R.id.belumAda);
        tvCooldown = view.findViewById(R.id.tvCooldown);

        recyclerView = view.findViewById(R.id.review);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setHasFixedSize(true);

        setAdapter();

        setOnClick();
    }

    private void setOnClick() {
        btnDonor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ProgressDialog pDialog = new ProgressDialog(activity);
                pDialog.setMessage("Loading...");
                pDialog.setCancelable(false);
                pDialog.show();

                final SGps gps = new SGps(activity);
                gps.on(new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {

                        pDialog.dismiss();

                        String query = "geo:"+location.getLatitude()+","+location.getLongitude()+"?q=Rumah Sakit";

                        Uri gmmIntentUri = Uri.parse(query);
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);

                        gps.off();

                    }
                });

            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_donor, menu);
       // super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.tb_profil:
                startActivity(new Intent(activity, Profil.class));
                return true;
            case R.id.tb_refresh:
                ambilLog();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void setAdapter() {
        DonorAdapter adapter = new DonorAdapter(donorArray);
        recyclerView.setAdapter(adapter);
    }

    private void ambilLog() {
        final ProgressDialog pdialog = new ProgressDialog(activity);
        pdialog.setMessage("Memuat...");
        pdialog.setCancelable(false);
        pdialog.show();

        SqliteSetting setting = new SqliteSetting(activity);
        String id = setting.ambil1("ID");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("LogUser/"+id);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ArrayList<DonorItem> arrayList = new ArrayList<>();
                    for (DataSnapshot children : dataSnapshot.getChildren()) {
                        String tempat = children.child("Tempat").getValue().toString();
                        String tanggal = children.child("Tanggal").getValue().toString();
                        String jumlah = children.child("Jumlah").getValue().toString();
                        arrayList.add(new DonorItem(tempat,tanggal,jumlah));
                    }
                    donorArray = arrayList;
                    tvBelumAda.setVisibility(View.INVISIBLE);
                } else {
                    Toast.makeText(activity, "Belum ada catatan donor.", Toast.LENGTH_SHORT).show();
                    tvBelumAda.setVisibility(View.VISIBLE);
                }
                setAdapter();
                setCooldown();
                setSaldo();
                pdialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private long getDiffDay(String date) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

        Date now = new Date();
        Date last = null;
        try {
            last = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long diff = now.getTime() - last.getTime();

        return diff / (24 * 60 * 60 * 1000);
    }

    private void setCooldown() {
        if (!donorArray.isEmpty() && getDiffDay(donorArray.get(donorArray.size()-1).tanggal) < 90){
            tvCooldown.setText("Cooldown: " + (90-getDiffDay(donorArray.get(donorArray.size()-1).tanggal)) + " Hari");
            tvCooldown.setVisibility(View.VISIBLE);
            btnDonor.setVisibility(View.INVISIBLE);
        } else {
            tvCooldown.setVisibility(View.INVISIBLE);
            btnDonor.setVisibility(View.VISIBLE);
        }
    }

    private void setSaldo() {
        int saldo = 0;
        for (int i = 0; i < donorArray.size();i++) {
            saldo += Integer.parseInt(donorArray.get(i).jumlah);
        }

        tvSaldo.setText(saldo+"");
    }

}
