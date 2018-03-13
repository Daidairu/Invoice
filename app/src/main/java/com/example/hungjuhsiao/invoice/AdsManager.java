package com.example.hungjuhsiao.invoice;

/**
 * Created by hungju.hsiao on 2017/5/31.
 */


import android.app.Activity;
import android.view.Gravity;
import android.widget.Toast;


import com.xinstars.ads.BannerAd;
import com.xinstars.ads.data.AdRequest;
import com.xinstars.ads.data.AdSize;

/**
 * Created by chinghsien.lin on 2017/1/6.
 */

public class AdsManager {
    // 廣告的物件
    private BannerAd bannerAd = null;
    // 廣告顯示的高度\
    private int adsHeightInPixels;

    public AdsManager(Activity activity) {
        adsHeightInPixels = (int) activity.getResources().getDimension(R.dimen._80sdp);
    }

    // 顯示廣告
    public void displayAds(Activity activity) {
        if (bannerAd == null) {
            bannerAd = new BannerAd();
            bannerAd.init(activity, "AdID", 5, true, true, true, null);                  // 初始化相關物件
            bannerAd.setAdSize(AdSize.FILL_WIDTH);
            bannerAd.setAlignment(Gravity.BOTTOM);
            createRandomAds();                                                       // 隨機生成廣告
        }
    }

    // 隨機生成廣告
    private void createRandomAds() {
        int random = (int) (Math.random() * 3);
        switch (random) {
            case 0:
                //使用預設參數: Package Name, Device Country
                bannerAd.loadAd(new AdRequest.Builder().build());
                break;
            case 1:
                //使用自訂參數1:
                bannerAd.loadAd(new AdRequest.Builder().setGameID("Wanin").setLocale("ENG").build());
                break;
            case 2:
                //使用自訂參數2:
                bannerAd.loadAd(new AdRequest.Builder().setLocale("CHN").build());
                break;
        }
    }

    //廣告停止輪播
    public void setAdsOnPause() {
        if (bannerAd != null) {
            bannerAd.pause();
        }
    }

    // 廣告繼續顯示
    public void setAdsOnResume() {
        if (bannerAd != null) {
            bannerAd.resume();
        }
    }

    // 清空廣告
    public void clearAds() {
        if (bannerAd != null) {
            bannerAd.destroy();
            bannerAd = null;
        }
    }
}