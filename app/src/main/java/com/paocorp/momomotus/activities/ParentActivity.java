package com.paocorp.momomotus.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.paocorp.momomotus.R;
import com.paocorp.momomotus.models.Global;
import com.paocorp.momomotus.util.IabHelper;
import com.paocorp.momomotus.util.IabResult;
import com.paocorp.momomotus.util.Inventory;
import com.paocorp.momomotus.util.Purchase;

public class ParentActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    protected NavigationView navigationView;
    protected IabHelper mHelper;
    protected boolean hideAd = false;
    protected boolean mIsRemoveAdds = false;
    protected String SKU_NOAD = Global.SKU_NOAD;
    protected ShareDialog shareDialog;
    protected InterstitialAd mInterstitialAd;

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        Intent intent = new Intent(getApplicationContext(), MotusActivity.class);
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
        } else if (id == R.id.nav_billing) {
            intent = new Intent(getApplicationContext(), BillingActivity.class);
        }
        intent.putExtras(b);
        startActivity(intent);
        return true;
    }

    protected void launchInterstitial() {
        mInterstitialAd = new InterstitialAd(this);

        if (!hideAd && !mIsRemoveAdds) {
            hideAd = true;
            mInterstitialAd.setAdUnitId(this.getResources().getString(R.string.interstitial));
            AdRequest adRequest = new AdRequest.Builder().build();
            mInterstitialAd.loadAd(adRequest);
            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    }
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null) try {
            mHelper.dispose();
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
        mHelper = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
