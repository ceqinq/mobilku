package com.legacies.legacies.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.legacies.legacies.Fragment.Donor;
import com.legacies.legacies.Fragment.Recipient;
import com.legacies.legacies.R;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialSetup();
    }

    private void initialSetup() {
        bottomNavigationView = findViewById(R.id.bottomnavigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        bottomNavigationView.setSelectedItemId(R.id.nvDonor);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nvDonor:
                    Donor donor = new Donor(MainActivity.this);
                    FragmentTransaction donorTransaction = getSupportFragmentManager().beginTransaction();
                    donorTransaction.replace(R.id.content,donor);
                    donorTransaction.commit();
                    return true;
                case R.id.nvRecipient:
                    Recipient recipient = new Recipient();
                    FragmentTransaction recipientTransaction = getSupportFragmentManager().beginTransaction();
                    recipientTransaction.replace(R.id.content,recipient);
                    recipientTransaction.commit();
                    return true;

            }
            return false;
        }

    };

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
