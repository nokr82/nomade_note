package com.devstories.nomadnote_android.base;

import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

public class GoogleAnalytics {
    public static void sendEventGoogleAnalytics(GlobalApplication application, String category, String action) {
        FirebaseAnalytics firebaseAnalytics = application.getFirebaseAnalytics();

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, category);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, action);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

}
