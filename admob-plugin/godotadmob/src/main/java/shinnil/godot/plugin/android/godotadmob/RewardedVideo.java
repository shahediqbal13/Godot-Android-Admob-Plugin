package shinnil.godot.plugin.android.godotadmob;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

interface RewardedVideoListener {
    void onRewardedVideoLoaded();

    void onRewardedVideoFailedToLoad(int errorCode);

    void onRewardedVideoLeftApplication();

    void onRewardedVideoOpened();

    void onRewardedVideoClosed();

    void onRewarded(String type, int amount);

    void onRewardedVideoStarted();

    void onRewardedVideoCompleted();
}

public class RewardedVideo {
    private final Activity mActivity;
    private RewardedAd mRewardedAd = null;
    private RewardedVideoListener defaultRewardedVideoListener;

    public RewardedVideo(Activity activity, final RewardedVideoListener defaultRewardedVideoListener) {
        mActivity = activity;
        this.defaultRewardedVideoListener = defaultRewardedVideoListener;
    }

    public void load(final String id, AdRequest adRequest) {
        RewardedAd.load(mActivity, id,
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        mRewardedAd = null;
                        Log.w("godot", "AdMob: onRewardedVideoAdFailedToLoad. errorCode: " + loadAdError.getCode());
                        defaultRewardedVideoListener.onRewardedVideoFailedToLoad(loadAdError.getCode());
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        mRewardedAd = rewardedAd;
                        Log.w("godot", "AdMob: onRewardedVideoAdLoaded");
                        defaultRewardedVideoListener.onRewardedVideoLoaded();

                        mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when ad is shown.
                                Log.w("godot", "AdMob: onRewardedVideoAdOpened");
                                defaultRewardedVideoListener.onRewardedVideoOpened();
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when ad fails to show.
                                Log.w("godot", "AdMob: onAdFailedToShowFullScreenContent");
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when ad is dismissed.
                                // Set the ad reference to null so you don't show the ad a second time.
                                mRewardedAd = null;
                                Log.w("godot", "AdMob: onRewardedVideoAdClosed");
                                defaultRewardedVideoListener.onRewardedVideoClosed();
                            }
                        });
                    }
                });
    }

    public void show() {
        if (mRewardedAd != null) {
            mRewardedAd.show(mActivity, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem reward) {
                    // Handle the reward.
                    Log.w("godot", "AdMob: "
                            + String.format(" onRewarded! currency: %s amount: %d", reward.getType(), reward.getAmount()));
                    defaultRewardedVideoListener.onRewarded(reward.getType(), reward.getAmount());
                }
            });
        } else {
            Log.d("godot", "The rewarded ad wasn't ready yet.");
        }
    }
}