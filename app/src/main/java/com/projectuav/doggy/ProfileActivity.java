package com.projectuav.doggy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieProperty;
import com.airbnb.lottie.model.KeyPath;
import com.airbnb.lottie.value.LottieFrameInfo;
import com.airbnb.lottie.value.SimpleLottieValueCallback;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.projectuav.doggy.adapters.ProfileAdapter;
import com.projectuav.doggy.static_classify.static_classify_Classify;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.projectuav.doggy.MainActivity.REQUEST_PERMISSION;

public class ProfileActivity extends AppCompatActivity {
    final int dogTotalAmount = 120;
    int totalUnlockedNumber = 0;
    int totalLockedNumber = 0;
    Button remaining_btn,unlocked_btn,reset_btn, back_btn, snap_btn;
    TextView dogsTotal, snapsTotal, profileInfo;
    private String chosen;
    private boolean quant;
    Uri uri;
    LottieAnimationView badge1, badge10, badge50, badge100, badgeAll,profilePhotoDog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("doggy",MODE_PRIVATE);
        SharedPreferences prefImg = getApplicationContext().getSharedPreferences("imagesText",MODE_PRIVATE);
        final String snapsX = pref.getString("TotalSnaps","0");
        final String[] myDataset = getString(R.string.dog_list).split(",");
        profilePhotoDog = findViewById(R.id.profilePhotoDog);
        remaining_btn = findViewById(R.id.remaining_btn);
        unlocked_btn = findViewById(R.id.unlocked_btn);
        profileInfo = findViewById(R.id.profileInfo);
        snap_btn = findViewById(R.id.profile_snap);
        back_btn = findViewById(R.id.profile_back);
        snapsTotal = findViewById(R.id.snapsTotal);
        reset_btn = findViewById(R.id.reset_btn);
        dogsTotal = findViewById(R.id.dogsTotal);
        badgeAll = findViewById(R.id.badgeAll);
        badge100 = findViewById(R.id.badge100);
        badge50 = findViewById(R.id.badge50);
        badge10 = findViewById(R.id.badge10);
        badge1 = findViewById(R.id.badge1);


        List<String> myUnlockedDatasetList = new ArrayList<>();
        List<String> myLockedDatasetList = new ArrayList<>();
        for (int i = 0;i< dogTotalAmount ; i++){
            if (pref.getBoolean(myDataset[i],false)){
                myUnlockedDatasetList.add(myDataset[i] + "-_-_-_-_-" + "1" + "-_-_-_-_-" + prefImg.getString(myDataset[i],null));
                totalUnlockedNumber = totalUnlockedNumber + 1;
            }
            else{
                myLockedDatasetList.add(myDataset[i] + "-_-_-_-_-" + "0" + "-_-_-_-_-" + prefImg.getString(myDataset[i],null));
                totalLockedNumber = totalLockedNumber + 1;
            }
        }
        String[] myUnlockedDataset = new String[totalUnlockedNumber];
        for(int x = 0; x < totalUnlockedNumber; x++){
            myUnlockedDataset[x] = myUnlockedDatasetList.get(x);
        }
        String[] myLockedDataset = new String[totalLockedNumber];
        for(int x = 0; x < totalLockedNumber; x++){
            myLockedDataset[x] = myLockedDatasetList.get(x);
        }
        snapsTotal.setText("Total Snaps: " + String.valueOf(snapsX));
        dogsTotal.setText("Total Doggs: " + String.valueOf(totalUnlockedNumber));
        if (totalUnlockedNumber >= 50){
            ;
        }
        else if (totalUnlockedNumber >= 25){
            badgeAll.addValueCallback(
                    new KeyPath("**"),
                    LottieProperty.COLOR_FILTER,
                    new SimpleLottieValueCallback<ColorFilter>() {
                        @Override
                        public ColorFilter getValue(LottieFrameInfo<ColorFilter> frameInfo) {
                            return new PorterDuffColorFilter(getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                        }
                    }
            );
        }
        else if (totalUnlockedNumber >= 10){
            badgeAll.addValueCallback(
                    new KeyPath("**"),
                    LottieProperty.COLOR_FILTER,
                    new SimpleLottieValueCallback<ColorFilter>() {
                        @Override
                        public ColorFilter getValue(LottieFrameInfo<ColorFilter> frameInfo) {
                            return new PorterDuffColorFilter(getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                        }
                    }
            );
            badge100.addValueCallback(
                    new KeyPath("**"),
                    LottieProperty.COLOR_FILTER,
                    new SimpleLottieValueCallback<ColorFilter>() {
                        @Override
                        public ColorFilter getValue(LottieFrameInfo<ColorFilter> frameInfo) {
                            return new PorterDuffColorFilter(getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                        }
                    }
            );
        }
        else if (totalUnlockedNumber >= 5){
            badgeAll.addValueCallback(
                    new KeyPath("**"),
                    LottieProperty.COLOR_FILTER,
                    new SimpleLottieValueCallback<ColorFilter>() {
                        @Override
                        public ColorFilter getValue(LottieFrameInfo<ColorFilter> frameInfo) {
                            return new PorterDuffColorFilter(getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                        }
                    }
            );
            badge100.addValueCallback(
                    new KeyPath("**"),
                    LottieProperty.COLOR_FILTER,
                    new SimpleLottieValueCallback<ColorFilter>() {
                        @Override
                        public ColorFilter getValue(LottieFrameInfo<ColorFilter> frameInfo) {
                            return new PorterDuffColorFilter(getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                        }
                    }
            );
            badge50.addValueCallback(
                    new KeyPath("**"),
                    LottieProperty.COLOR_FILTER,
                    new SimpleLottieValueCallback<ColorFilter>() {
                        @Override
                        public ColorFilter getValue(LottieFrameInfo<ColorFilter> frameInfo) {
                            return new PorterDuffColorFilter(getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                        }
                    }
            );
        }
        else if (totalUnlockedNumber >= 1){
            badgeAll.addValueCallback(
                    new KeyPath("**"),
                    LottieProperty.COLOR_FILTER,
                    new SimpleLottieValueCallback<ColorFilter>() {
                        @Override
                        public ColorFilter getValue(LottieFrameInfo<ColorFilter> frameInfo) {
                            return new PorterDuffColorFilter(getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                        }
                    }
            );
            badge100.addValueCallback(
                    new KeyPath("**"),
                    LottieProperty.COLOR_FILTER,
                    new SimpleLottieValueCallback<ColorFilter>() {
                        @Override
                        public ColorFilter getValue(LottieFrameInfo<ColorFilter> frameInfo) {
                            return new PorterDuffColorFilter(getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                        }
                    }
            );
            badge50.addValueCallback(
                    new KeyPath("**"),
                    LottieProperty.COLOR_FILTER,
                    new SimpleLottieValueCallback<ColorFilter>() {
                        @Override
                        public ColorFilter getValue(LottieFrameInfo<ColorFilter> frameInfo) {
                            return new PorterDuffColorFilter(getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                        }
                    }
            );
            badge10.addValueCallback(
                    new KeyPath("**"),
                    LottieProperty.COLOR_FILTER,
                    new SimpleLottieValueCallback<ColorFilter>() {
                        @Override
                        public ColorFilter getValue(LottieFrameInfo<ColorFilter> frameInfo) {
                            return new PorterDuffColorFilter(getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                        }
                    }
            );

        }
        else{
            badgeAll.addValueCallback(
                    new KeyPath("**"),
                    LottieProperty.COLOR_FILTER,
                    new SimpleLottieValueCallback<ColorFilter>() {
                        @Override
                        public ColorFilter getValue(LottieFrameInfo<ColorFilter> frameInfo) {
                            return new PorterDuffColorFilter(getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                        }
                    }
            );
            badge100.addValueCallback(
                    new KeyPath("**"),
                    LottieProperty.COLOR_FILTER,
                    new SimpleLottieValueCallback<ColorFilter>() {
                        @Override
                        public ColorFilter getValue(LottieFrameInfo<ColorFilter> frameInfo) {
                            return new PorterDuffColorFilter(getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                        }
                    }
            );
             badge50.addValueCallback(
                    new KeyPath("**"),
                    LottieProperty.COLOR_FILTER,
                    new SimpleLottieValueCallback<ColorFilter>() {
                        @Override
                        public ColorFilter getValue(LottieFrameInfo<ColorFilter> frameInfo) {
                            return new PorterDuffColorFilter(getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                        }
                    }
            );
             badge10.addValueCallback(
                    new KeyPath("**"),
                    LottieProperty.COLOR_FILTER,
                    new SimpleLottieValueCallback<ColorFilter>() {
                        @Override
                        public ColorFilter getValue(LottieFrameInfo<ColorFilter> frameInfo) {
                            return new PorterDuffColorFilter(getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                        }
                    }
            );
             badge1.addValueCallback(
                    new KeyPath("**"),
                    LottieProperty.COLOR_FILTER,
                    new SimpleLottieValueCallback<ColorFilter>() {
                        @Override
                        public ColorFilter getValue(LottieFrameInfo<ColorFilter> frameInfo) {
                            return new PorterDuffColorFilter(getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                        }
                    }
            );
        }
        profilePhotoDog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaPlayer mediaPlayer = MediaPlayer.create(ProfileActivity.this, R.raw.alealeaa);
                mediaPlayer.start();
            }
        });

        remaining_btn.setText("REMAINING("+String.valueOf(totalLockedNumber)+")");
        unlocked_btn.setText("UNLOCKED("+String.valueOf(totalUnlockedNumber)+")");

//        Collections.reverse(Arrays.asList(myDataset));
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager( new GridLayoutManager(ProfileActivity.this, 2));
//        recyclerView.setLayoutManager(layoutManager);
        ProfileAdapter mAdapter = new ProfileAdapter(myUnlockedDataset);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnListClickListener(new ProfileAdapter.OnItemClickListener() {
            @Override
            public void onListClick(int position) {
//                Toast.makeText(ProfileActivity.this, String.valueOf(position) + myDataset[position], Toast.LENGTH_SHORT).show();
            }
        });
        remaining_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remaining_btn.setBackgroundResource(R.drawable.button_shape_gold);
                remaining_btn.setTextColor(Color.parseColor("#111111"));
                unlocked_btn.setBackgroundResource(R.drawable.button_shape_dark);
                unlocked_btn.setTextColor(Color.parseColor("#C3CEFD"));
                ProfileAdapter mAdapter = new ProfileAdapter(myLockedDataset);
                recyclerView.setAdapter(mAdapter);
                mAdapter.setOnListClickListener(new ProfileAdapter.OnItemClickListener() {
                    @Override
                    public void onListClick(int position) {
                    }
                });
            }
        });
        unlocked_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unlocked_btn.setBackgroundResource(R.drawable.button_shape_gold);
                unlocked_btn.setTextColor(Color.parseColor("#222222"));
                remaining_btn.setBackgroundResource(R.drawable.button_shape_dark);
                remaining_btn.setTextColor(Color.parseColor("#C3CEFD"));
                if (totalUnlockedNumber == 0){
                    Toast.makeText(ProfileActivity.this, "You have not unlocked any dog!", Toast.LENGTH_SHORT).show();
                }
                ProfileAdapter mAdapter = new ProfileAdapter(myUnlockedDataset);
                recyclerView.setAdapter(mAdapter);
                mAdapter.setOnListClickListener(new ProfileAdapter.OnItemClickListener() {
                    @Override
                    public void onListClick(int position) {
                    }
                });
            }

        });
        reset_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        snap_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, AllNamesActivity.class);
                startActivity(intent);
            }
        });
    }
    @SuppressLint("UseCompatLoadingForDrawables")
    public void reset() {
        new AlertDialog.Builder(this)
                .setIcon(getDrawable(R.drawable.hayekarlopehle))
                .setTitle("RESET ALL PROGRESS")
                .setMessage("Are you sure? This will delete everything and cannot be reversed.")
                .setNegativeButton("No", null)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteEverything();
                    }
                })
                .show();
    }

    public void deleteEverything() {
        SharedPreferences prefX = getApplicationContext().getSharedPreferences("doggy",MODE_PRIVATE);
        SharedPreferences prefImgX = getApplicationContext().getSharedPreferences("imagesText",MODE_PRIVATE);
        SharedPreferences.Editor prefXXX = prefX.edit();
        SharedPreferences.Editor prefImgXXX = prefImgX.edit();
        final String[] myDataset = getString(R.string.dog_list).split(",");
        final int dogTotalAmount = 120;
        for (int i = 0;i< dogTotalAmount ; i++){
            prefXXX.putBoolean(myDataset[i], false);
            prefXXX.apply();
            prefImgXXX.putString(myDataset[i],"");
            prefImgXXX.apply();
        }
        prefXXX.putString("TotalSnaps","0");
        prefXXX.apply();
        finish();
    }
}