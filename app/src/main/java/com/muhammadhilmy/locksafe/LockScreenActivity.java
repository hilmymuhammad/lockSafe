package com.muhammadhilmy.locksafe;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class LockScreenActivity extends AppCompatActivity {

    //database read

    DatabaseReference status;
    DatabaseReference loc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        //
        final TextView mytextView = (TextView) findViewById(R.id.textView2);
        final TextView mytextLoc = (TextView) findViewById(R.id.textLoc);
        status = FirebaseDatabase.getInstance().getReference().child("status");
        loc = FirebaseDatabase.getInstance().getReference().child("location");

        //ads
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        UnlockBar unlock = findViewById(R.id.unlock);
        //retriev status
        status.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String myText =  dataSnapshot.getValue(String.class);
                mytextView.setText(myText);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                mytextView.setText("Error Found!");
            }
        });
        //retrieve location
        loc.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String myLoc =  dataSnapshot.getValue(String.class);
                mytextLoc.setText(myLoc);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                mytextLoc.setText("Location not Found");
            }
        });
            /* Attach listener */
            unlock.setOnUnlockListenerRight(new UnlockBar.OnUnlockListener() {
            @Override
            public void onUnlock()
            {
                Toast.makeText(LockScreenActivity.this, "Kunci Terbuka", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        unlock.setOnUnlockListenerLeft(new UnlockBar.OnUnlockListener() {
            @Override
            public void onUnlock()
            {
                Toast.makeText(LockScreenActivity.this, "Unlocked", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

            }

    @Override
    public void onAttachedToWindow() {

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|
//                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON|
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        super.onAttachedToWindow();

    }
    @Override
    protected void onResume() {
        super.onResume();
        ((LockApplication) getApplication()).lockScreenShow = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
//        ((LockApplication) getApplication()).lockScreenShow = false;
        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.moveTaskToFront(getTaskId(),0);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return false;
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//
//    }
}

