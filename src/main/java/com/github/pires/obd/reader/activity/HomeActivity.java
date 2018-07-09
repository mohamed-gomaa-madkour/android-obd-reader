package com.github.pires.obd.reader.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.github.pires.obd.reader.R;

public class HomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    public void openSitting(View view) {
        startActivity(new Intent(this, ConfigActivity.class));
    }
    public void openApi(View view) {
        startActivity(new Intent(this, Search.class));
    }
    public void openTripList(View view) {
        startActivity(new Intent(this, TripListActivity.class));
    }

    public void openMain(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }

    public void openDiagnostics(View view) {
        startActivity(new Intent(this,TroubleCodesActivity.class));
    }
}