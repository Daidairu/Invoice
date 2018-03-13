package com.xinstars.ads;

public interface IAdListener {
	
    void onAdLoaded();
    void onAdLoadFailed(int errorCode);
    void onAdOpened();
    void onAdClosed();
    void onAdLeftApplication();
    
    //Video
    //void onVideoStarted ();
	//void onVideoCompleted (boolean skipped);

}
