package com.paocorp.momomotus.activities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.widget.ShareDialog;
import com.paocorp.momomotus.R;

public class MainActivity extends ParentActivity {

    RadioGroup optGame;
    RadioGroup diff;
    TextView mainsubtitle;
    Button startGame;
    PackageInfo pInfo;
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        LayoutInflater.from(this).inflate(R.layout.nav_header_main, navigationView);

        TextView txt_version = (TextView) findViewById(R.id.app_version);

        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            txt_version.setText(String.format("%s v%s", this.getResources().getString(R.string.app_name), pInfo.versionName));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        // Capture our button from layout
        startGame = (Button) findViewById(R.id.btnsend);
        startGame.setEnabled(true);
        mainsubtitle = (TextView) findViewById(R.id.mainsubtitle);
        optGame = (RadioGroup) findViewById(R.id.optGame);
        diff = (RadioGroup) findViewById(R.id.diff);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        launchInterstitial();
    }

    public void startGame(View v) {
        Intent intent = new Intent(MainActivity.this, MotusActivity.class);
        Bundle b = new Bundle();
        if (diff != null && diff.getCheckedRadioButtonId() == R.id.diffHard) {
            b.putBoolean("diff", true);
        } else {
            b.putBoolean("diff", false);
        }
        if (optGame.getCheckedRadioButtonId() == R.id.radio_six) {
            b.putInt("nb", 6);
        } else if (optGame.getCheckedRadioButtonId() == R.id.radio_seven) {
            b.putInt("nb", 7);
        } else if (optGame.getCheckedRadioButtonId() == R.id.radio_eight) {
            b.putInt("nb", 8);
        } else if (optGame.getCheckedRadioButtonId() == R.id.radio_nine) {
            b.putInt("nb", 9);
        } else if (optGame.getCheckedRadioButtonId() == R.id.radio_ten) {
            b.putInt("nb", 10);
        } else {
            b.putInt("nb", 6);
        }
        intent.putExtras(b);
        startActivity(intent);
        finish();
    }

    public boolean onRadioButtonClicked(View v) {
        return true;
    }

}
