package com.projectuav.doggy;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.projectuav.doggy.adapters.AllNamesAdapter;

public class AllNamesActivity extends AppCompatActivity {

    Button backButton, homeButton;
    InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_names);

            mInterstitialAd = new InterstitialAd(this);
//            mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");//TestAd
            mInterstitialAd.setAdUnitId("ca-app-pub-1591924812627373/9543995178");//MyAd
            mInterstitialAd.loadAd(new AdRequest.Builder().build());

            SharedPreferences pref = getApplicationContext().getSharedPreferences("doggy", MODE_PRIVATE);
            final String[] myDataset = getString(R.string.dog_list).split(",");

            for (int i = 0; i < 120; i++) {
                if (pref.getBoolean(myDataset[i], false)) {
                    myDataset[i] = (myDataset[i] + "-_-_-_-_-" + "1");
                } else {
                    myDataset[i] = (myDataset[i] + "-_-_-_-_-" + "0");
                }
            }


            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.AllNamesRV);
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
//        recyclerView.setLayoutManager( new GridLayoutManager(AllNamesActivity.this, 2));
            recyclerView.setLayoutManager(layoutManager);
            AllNamesAdapter mAdapter = new AllNamesAdapter(myDataset);
            recyclerView.setAdapter(mAdapter);
            mAdapter.setOnListClickListener(new AllNamesAdapter.OnItemClickListener() {
                @Override
                public void onListClick(int position) {
//                Toast.makeText(ProfileActivity.this, String.valueOf(position) + myDataset[position], Toast.LENGTH_SHORT).show();
                    ;
                }
            });

            backButton = (Button) findViewById(R.id.allBack);
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    mInterstitialAd.show();
                }
            });

            homeButton = (Button) findViewById(R.id.allHome);
            homeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AllNamesActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    mInterstitialAd.show();
                }
            });


        }


    public void onBackPressed() {
        mInterstitialAd.show();
        finish();
    }
}