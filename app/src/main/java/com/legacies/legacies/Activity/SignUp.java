package com.legacies.legacies.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.legacies.legacies.R;
import com.legacies.legacies.Tool.SGps;
import com.legacies.legacies.Tool.SqliteSetting;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class SignUp extends AppCompatActivity {

    EditText etNama, etTanggalLahir, etEmail, etPassword, etAlamat, etNomorHp;
    Spinner spGender, spGoldar, spRhesus, spProvinsi, spKab;
    Button btnDaftar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

       // hideActionBar();
        viewInitialSetup();
        SGps.reqHighGps(this);
    }

    private void viewInitialSetup() {

        // Find View
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etNama = findViewById(R.id.etNama);
        etTanggalLahir = findViewById(R.id.etTanggalLahir);
        spGender = findViewById(R.id.spinnerGender);
        spGoldar = findViewById(R.id.spinnerGoldar);
        spRhesus = findViewById(R.id.spinnerRhesus);
        btnDaftar = findViewById(R.id.btnDaftar);
        etAlamat = findViewById(R.id.etAlamat);
        spProvinsi = findViewById(R.id.spinnerProvinsi);
        spKab = findViewById(R.id.spinnerKab);
        etNomorHp = findViewById(R.id.etNomorHp);

        // Set Edittext Tanggal lahir untuk Datetimepicker
        final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String date = dayOfMonth + "/" + (month+1) + "/" + year;
                etTanggalLahir.setText(date);
            }
        };

        etTanggalLahir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar myCalendar = Calendar.getInstance();
                DatePickerDialog dialog = new DatePickerDialog(SignUp.this, android.R.style.Theme_Holo_Light_Dialog, dateSetListener , myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH));
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        setOnClick();

        // Set Spinner Adapter
        setSpinnerAdapter();
    }

    private void setSpinnerAdapter() {

        // Spinner Gender
        String[] genderList = new String[]{"","M","F"};

        ArrayAdapter<String> adapterGender = new ArrayAdapter<>(this, R.layout.sp_view, genderList);
        adapterGender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGender.setAdapter(adapterGender);

        // Spinner Golongan Darah
        String[] golonganDarahList = new String[]{"None","A","B","O","AB"};

        ArrayAdapter<String> adapterGoldar = new ArrayAdapter<>(this, R.layout.sp_view, golonganDarahList);
        adapterGoldar.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGoldar.setAdapter(adapterGoldar);

        // Spinner Rhesus Darah
        String[] rhesusList = new String[]{"None","+","-"};

        ArrayAdapter<String> adapterRhesus = new ArrayAdapter<>(this, R.layout.sp_view, rhesusList);
        adapterGoldar.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRhesus.setAdapter(adapterRhesus);

        setSpinnerProvKota();
    }

    private void setOnClick() {
        btnDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }

    public static int getDiffYears(Calendar a) {
        Calendar b = Calendar.getInstance();
        int diff = b.get(Calendar.YEAR) - a.get(Calendar.YEAR);
        if (a.get(Calendar.MONTH) > b.get(Calendar.MONTH) + 1 ||
                (a.get(Calendar.MONTH) == (b.get(Calendar.MONTH) + 1) && a.get(Calendar.DAY_OF_MONTH) > b.get(Calendar.DAY_OF_MONTH))) {
            diff--;
        }
        return diff;
    }

    private void register() {

        // Input handler
        final String email = etEmail.getText().toString();
        final String password = etPassword.getText().toString();
        final String nama = etNama.getText().toString();
        final String noHp = etNomorHp.getText().toString();
        final String tglLahir = etTanggalLahir.getText().toString();
        final String gender = spGender.getSelectedItem().toString();
        final String goldar = spGoldar.getSelectedItem().toString();
        final String rhesus = spRhesus.getSelectedItem().toString();
        final String alamat = etAlamat.getText().toString();
        final String kota = spKab.getSelectedItem().toString();
        final String prov = spProvinsi.getSelectedItem().toString();


        if (email.length() < 4 || !email.contains("@") || !email.contains(".")) {
            Toast.makeText(this, "Mohon isi Email dengan benar.", Toast.LENGTH_SHORT).show();
        } else if (password.length() < 6) {
            Toast.makeText(this, "Password minimal 6 karakter.", Toast.LENGTH_SHORT).show();
        } else if (nama.length() < 3) {
            Toast.makeText(this, "Mohon isi Nama dengan benar.", Toast.LENGTH_SHORT).show();
        } else if (noHp.length() < 8) {
            Toast.makeText(this, "Mohon isi Nomor HP dengan benar.", Toast.LENGTH_SHORT).show();
        } else if (tglLahir.isEmpty()) {
            Toast.makeText(this, "Mohon isi Tanggal Lahir dengan benar.", Toast.LENGTH_SHORT).show();
        }  else if (gender.isEmpty()) {
            Toast.makeText(this, "Mohon isi Jenis Kelamin dengan benar.", Toast.LENGTH_SHORT).show();
        } else if (alamat.length() < 6) {
            Toast.makeText(this, "Mohon isi Alamat dengan benar.", Toast.LENGTH_SHORT).show();
        }  else {
            String[] tglLahirArray = tglLahir.split("/");

            Calendar tglLahirCalendar = Calendar.getInstance();
            tglLahirCalendar.set(Integer.parseInt(tglLahirArray[2]), Integer.parseInt(tglLahirArray[1]),Integer.parseInt(tglLahirArray[0]));

            int umur = getDiffYears(tglLahirCalendar);
            Log.d("Umur", umur+"");
            if (umur < 17) {
                Toast.makeText(this, "Maaf, anda tidak bisa mendaftar. Minimal usia pendonor adalah 17 tahun.", Toast.LENGTH_LONG).show();
            } else {

                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("Konfirmasi");
                dialog.setMessage("Apakah anda yakin, data yang diisi sudah benar?");
                dialog.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        createAccount(email,password,nama,noHp,tglLahir,gender,goldar,rhesus,alamat,kota,prov);
                    }
                });
                dialog.setNegativeButton("Tidak", null);
                dialog.create().show();
            }
        }
    }

    private void createAccount(final String email, String password, final String nama, final String nomorHp, final String tglLahir, final String gender, final String goldar, final String rhesus, final String alamat, final String kota, final String prov) {
        final ProgressDialog pdialog = new ProgressDialog(this);
        pdialog.setMessage("Memuat...");
        pdialog.setCancelable(false);
        pdialog.show();
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    final String id = email.replace(".","dot");
                    final String alamatLengkap = alamat + ", " + kota + ", " + prov;

                    final LatLng latLng = getLocationFromAddress(alamatLengkap);

                    // Save user data
                    final Map<String, Object> dataMap = new HashMap<>();
                    dataMap.put("Email", email);
                    dataMap.put("Nama", nama);
                    dataMap.put("TglLahir", tglLahir);
                    dataMap.put("Gender", gender);
                    dataMap.put("GolDar", goldar+rhesus);
                    dataMap.put("Status", 0);
                    dataMap.put("Alamat",alamatLengkap);
                    assert latLng != null;
                    dataMap.put("Lat", latLng.latitude);
                    dataMap.put("Long", latLng.longitude);
                    dataMap.put("Phone", nomorHp);


                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User/"+id);
                    ref.updateChildren(dataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            pdialog.dismiss();
                            if (task.isSuccessful()) {
                                SqliteSetting setting = new SqliteSetting(SignUp.this);
                                setting.simpan("ID", id);
                                setting.simpan("Email", email);
                                setting.simpan("Nama", nama);
                                setting.simpan("TglLahir", tglLahir);
                                setting.simpan("Gender", gender);
                                setting.simpan("GolDar", goldar+rhesus);
                                setting.simpan("Status", "Belum Terverifikasi");
                                setting.simpan("Alamat",alamatLengkap);
                                setting.simpan("Lat", ""+latLng.latitude);
                                setting.simpan("Long", ""+latLng.longitude);
                                setting.simpan("Phone", nomorHp);

                                //Save Koordinat

                                DatabaseReference koorRef = FirebaseDatabase.getInstance().getReference("Koordinat/"+id);
                                koorRef.setValue(latLng.latitude+","+latLng.longitude);

                                finish();
                                startActivity(new Intent(SignUp.this, MainActivity.class));
                            } else {
                                Log.d("Error", task.getException().toString());
                                Toast.makeText(SignUp.this, "Gagal Register. Periksa koneksi internet.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                } else {
                    pdialog.dismiss();
                    Log.d("Error", task.getException().toString());
                    Toast.makeText(SignUp.this, "Gagal Register. Periksa koneksi internet.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private LatLng getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(this);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }

    public String loadJSONFromAsset() {
        String json;
        try {
            InputStream is = getAssets().open("indonesia1.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }

    private void setSpinnerProvKota(){
        try {
            final JSONObject jsonObject = new JSONObject(loadJSONFromAsset());
            Iterator<String> keys = jsonObject.keys();

            final ArrayList<String> provArrayList = new ArrayList<>();

            while (keys.hasNext()) {
                provArrayList.add(keys.next());
            }

            ArrayAdapter<String> adapterProv = new ArrayAdapter<>(this,R.layout.sp_view,provArrayList);
            adapterProv.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spProvinsi.setAdapter(adapterProv);


            spProvinsi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Log.d("Provinsi", provArrayList.get(position));

                    try {
                        JSONArray jsonArrayKab = jsonObject.getJSONArray(provArrayList.get(position));

                        ArrayList<String> arrayKab = new ArrayList<>();
                        for (int i = 0; i < jsonArrayKab.length();i++) {
                            arrayKab.add(""+jsonArrayKab.get(i));
                        }

                        ArrayAdapter<String> kabAdapter = new ArrayAdapter<>(SignUp.this,R.layout.sp_view,arrayKab);
                        kabAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spKab.setAdapter(kabAdapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
