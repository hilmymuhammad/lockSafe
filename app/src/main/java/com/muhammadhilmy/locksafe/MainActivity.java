package com.muhammadhilmy.locksafe;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.muhammadhilmy.locksafe.utils.LockScreen;

public class MainActivity extends AppCompatActivity {
    ImageButton buttonHome;
    ImageButton buttonOut;
    DatabaseReference status;
    DatabaseReference loc;
    ToggleButton toggleButton;
    TextView textView;
    ImageButton btPlacesAPI;
    private TextView tvPlaceAPI;
    // konstanta untuk mendeteksi hasil balikan dari place picker
    private int PLACE_PICKER_REQUEST = 1;
//    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        toggleButton = (ToggleButton) findViewById(R.id.toggleButton);

        //database config
        buttonHome = (ImageButton) findViewById(R.id.imageButton);
        buttonOut = (ImageButton) findViewById(R.id.imageButtonOut);
        status = FirebaseDatabase.getInstance().getReference().child("status");
        loc = FirebaseDatabase.getInstance().getReference().child("location");

        //picker location
        tvPlaceAPI = (TextView) findViewById(R.id.tv_place_id);
        btPlacesAPI = (ImageButton)findViewById(R.id.bt_ppicker);


        //
        LockScreen.getInstance().init(this, true);

        if (LockScreen.getInstance().isActive()) {
            toggleButton.setChecked(true);
        } else {
            toggleButton.setChecked(false);

        }

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    LockScreen.getInstance().active();
                } else {
                    LockScreen.getInstance().deactivate();
                }
            }
        });

        btPlacesAPI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // membuat Intent untuk Place Picker
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    //menjalankan place picker
                    startActivityForResult(builder.build(MainActivity.this), PLACE_PICKER_REQUEST);

                    // check apabila <a title="Solusi Tidak Bisa Download Google Play Services di Android" href="http://www.twoh.co/2014/11/solusi-tidak-bisa-download-google-play-services-di-android/" target="_blank">Google Play Services tidak terinstall</a> di HP
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
//     mGoogleApiClient.connect();

        status.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                String hobi_saya = dataSnapshot.getValue(String.class);
//                textView_hobi.setText(hobi_saya);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        buttonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               status.setValue("Anak sedang di rumah");
            }
        });

        buttonOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                status.setValue("Anak sedang di luar");
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // menangkap hasil balikan dari Place Picker, dan menampilkannya pada TextView
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                String toastMsg = String.format(
                        "Place: %s \n" +
                                "Alamat: %s \n" , place.getName(), place.getAddress());
                tvPlaceAPI.setText(toastMsg);
                loc.setValue(toastMsg);
            }
        }
    }
}
