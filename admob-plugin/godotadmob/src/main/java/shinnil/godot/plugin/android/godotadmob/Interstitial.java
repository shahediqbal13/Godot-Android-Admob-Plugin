package shinnil.godot.plugin.android.godotadmob;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

interface InterstitialListener {
    void onInterstitialLoaded();

    void onInterstitialFailedToLoad(int errorCode);

    void onInterstitialOpened();

    void onInterstitialLeftApplication();

    void onInterstitialClosed();
}

public class Interstitial {
    private InterstitialAd mInterstitialAd = null; // Interstitial object
    private final Activity mActivity;

    public Interstitial(final String id, final AdRequest adRequest, final Activity activity, final InterstitialListener defaultInterstitialListener) {
        mActivity = activity;

        InterstitialAd.load(activity, id, adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.w("godot", "AdMob: onAdLoaded");
                        defaultInterstitialListener.onInterstitialLoaded();

                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when fullscreen content is dismissed.
                                Log.w("godot", "AdMob: onAdClosed");
                                defaultInterstitialListener.onInterstitialClosed();
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when fullscreen content failed to show.
                                Log.w("godot", "AdMob: onAdFailedToLoad(int errorCode) - error code: " + Integer.toString(adError.getCode()));
                                defaultInterstitialListener.onInterstitialFailedToLoad(adError.getCode());
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when fullscreen content is shown.
                                // Make sure to set your reference to null so you don't
                                // show it a second time.
                                mInterstitialAd = null;
                                Log.w("godot", "AdMob: onAdOpened()");
                                defaultInterstitialListener.onInterstitialOpened();
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                        // Handle the error
                        mInterstitialAd = null;
                        Log.w("godot", "AdMob: onAdFailedToLoad(int errorCode) - error code: " + Integer.toString(adError.getCode()));
                        defaultInterstitialListener.onInterstitialFailedToLoad(adError.getCode());
                    }
                });
    }

    public void show() {
        if (mInterstitialAd != null) {
            mInterstitialAd.show(mActivity);
        } else {
            Log.w("w", "AdMob: showInterstitial - interstitial not loaded");
        }
    }
}