package com.paocorp.momomotus.activities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.paocorp.momomotus.R;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    RadioGroup optGame;
    RadioGroup diff;
    TextView mainsubtitle;
    Button startGame;
    PackageInfo pInfo;
    private InterstitialAd mInterstitialAd;
    CallbackManager callbackManager;
    ShareDialog shareDialog;

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

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        LayoutInflater.from(this).inflate(R.layout.nav_header_main, navigationView);

        TextView txt_version = (TextView) findViewById(R.id.app_version);

        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            txt_version.setText(String.format("%s v%s", this.getResources().getString(R.string.app_name), pInfo.versionName));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(this.getResources().getString(R.string.interstitial));
        requestNewInterstitial();
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                showInterstitial();
            }
        });

        // Capture our button from layout
        startGame = (Button) findViewById(R.id.btnsend);
        startGame.setEnabled(true);
        mainsubtitle = (TextView) findViewById(R.id.mainsubtitle);
        optGame = (RadioGroup) findViewById(R.id.optGame);
        diff = (RadioGroup) findViewById(R.id.diff);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
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
            intent.putExtras(b);
            startActivity(intent);
        } else if (optGame.getCheckedRadioButtonId() == R.id.radio_seven) {
            b.putInt("nb", 7);
            intent.putExtras(b);
            startActivity(intent);
        } else if (optGame.getCheckedRadioButtonId() == R.id.radio_eight) {
            b.putInt("nb", 8);
            intent.putExtras(b);
            startActivity(intent);
        } else if (optGame.getCheckedRadioButtonId() == R.id.radio_nine) {
            b.putInt("nb", 9);
            intent.putExtras(b);
            startActivity(intent);
            finish();
        } else if (optGame.getCheckedRadioButtonId() == R.id.radio_ten) {
            b.putInt("nb", 10);
            intent.putExtras(b);
            startActivity(intent);
        } else {
            b.putInt("nb", 6);
            intent.putExtras(b);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onRadioButtonClicked(View v) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_home) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        Intent intent = new Intent(MainActivity.this, MotusActivity.class);
        Bundle b = new Bundle();

        int id = item.getItemId();
        if (id == R.id.nav_share_fb) {
            if (ShareDialog.canShow(ShareLinkContent.class)) {
                String fbText = getResources().getString(R.string.fb_ContentDesc);
                ShareLinkContent linkContent = new ShareLinkContent.Builder()
                        .setContentUrl(Uri.parse(getResources().getString(R.string.store_url)))
                        .setContentTitle(getResources().getString(R.string.app_name))
                        .setContentDescription(fbText)
                        .setImageUrl(Uri.parse(getResources().getString(R.string.app_icon_url)))
                        .build();

                shareDialog.show(linkContent);
            }
            return true;
        } else if (id == R.id.new_6) {
            b.putInt("nb", 6);
        } else if (id == R.id.new_7) {
            b.putInt("nb", 7);
        } else if (id == R.id.new_8) {
            b.putInt("nb", 8);
        } else if (id == R.id.new_9) {
            b.putInt("nb", 9);
        } else if (id == R.id.new_10) {
            b.putInt("nb", 10);
        } else if (id == R.id.rate_app) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.app_url)));
        }
        intent.putExtras(b);
        startActivity(intent);
        return true;
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);
    }

    private void showInterstitial() {
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }
}
