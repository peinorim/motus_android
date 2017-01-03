package com.paocorp.momomotus.activities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.widget.ShareDialog;
import com.paocorp.momomotus.R;
import com.paocorp.momomotus.models.Global;
import com.paocorp.momomotus.util.IabHelper;
import com.paocorp.momomotus.util.IabResult;
import com.paocorp.momomotus.util.Inventory;
import com.paocorp.momomotus.util.Purchase;

import java.util.ArrayList;
import java.util.List;

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

        String base64EncodedPublicKey = this.getResources().getString(R.string.billingKey);
        mHelper = new IabHelper(this, base64EncodedPublicKey);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    // Oh no, there was a problem.
                    Log.d("BILLING-ISSUE", "Problem setting up In-app Billing: " + result);
                    return;
                }
                if (result.isSuccess()) {
                    try {
                        List additionalSkuList = new ArrayList<>();
                        additionalSkuList.add(SKU_NOAD);
                        mHelper.queryInventoryAsync(true, additionalSkuList, additionalSkuList, mGotInventoryListener);
                    } catch (IabHelper.IabAsyncInProgressException e) {
                        e.printStackTrace();
                    }
                }
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

    protected IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        @Override
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            if (result.isFailure()) {
                // handle error here
                Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_LONG).show();
            } else {
                mIsRemoveAdds = inventory.hasPurchase(SKU_NOAD);
                Purchase purchase = inventory.getPurchase(SKU_NOAD);
                if (!mIsRemoveAdds || (purchase != null && purchase.getPurchaseState() != 0)) {
                    Global.isNoAdsPurchased = false;
                    launchInterstitial();
                } else {
                    Global.isNoAdsPurchased = true;
                    Menu menu = navigationView.getMenu();
                    MenuItem nav_billing = menu.findItem(R.id.nav_billing);
                    nav_billing.setVisible(false);
                }
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        queryPurchasedItems();
    }

    @Override
    protected void onResume() {
        super.onResume();
        queryPurchasedItems();
    }

    protected void queryPurchasedItems() {
        //check if user has bought "remove adds"
        if (mHelper.isSetupDone() && !mHelper.isAsyncInProgress()) {
            try {
                mHelper.queryInventoryAsync(mGotInventoryListener);
            } catch (IabHelper.IabAsyncInProgressException e) {
                e.printStackTrace();
            }
        }
    }

}
