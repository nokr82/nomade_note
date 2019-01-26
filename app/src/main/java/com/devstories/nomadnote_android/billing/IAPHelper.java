package com.devstories.nomadnote_android.billing;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import com.android.vending.billing.IInAppBillingService;
import com.devstories.nomadnote_android.base.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class IAPHelper implements ServiceConnection {

    IInAppBillingService mService;
    Activity activity;
    private BuyListener buyListener;

    public IAPHelper(Activity activity, BuyListener buyListener) {
        this.activity = activity;
        this.buyListener = buyListener;

        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        activity.bindService(serviceIntent, this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mService = null;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mService = IInAppBillingService.Stub.asInterface(service);

        detail();
    }


    private void detail() {

        ArrayList<String> skuList = new ArrayList<String> ();
        skuList.add("1gb");
        skuList.add("600mb");

        Bundle querySkus = new Bundle();
        querySkus.putStringArrayList("ITEM_ID_LIST", skuList);

        try {
            Bundle skuDetails = mService.getSkuDetails(3, this.activity.getPackageName(), "inapp", querySkus);
            int response = skuDetails.getInt("RESPONSE_CODE");

            // System.out.println(skuDetails);

            if (response == 0) {
                ArrayList<String> responseList = skuDetails.getStringArrayList("DETAILS_LIST");

                for (String thisResponse : responseList) {
                    JSONObject object = new JSONObject(thisResponse);
                    String sku = object.getString("productId");
                    String price = object.getString("price");

                    // System.out.println("sku : " + sku);
                    // System.out.println("price : " + price);
                }

                // buy("step1000");

                getPurchases();
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void destroy() {
        if (mService != null) {
            this.activity.unbindService(this);
        }
    }

    public void buy(String sku) {
        try {
            Bundle buyIntentBundle = mService.getBuyIntent(3, this.activity.getPackageName(), sku, "inapp", "bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ");
            PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
            if(pendingIntent == null) {
                getPurchases();
            } else {
                this.activity.startIntentSenderForResult(pendingIntent.getIntentSender(), 1001, new Intent(), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0));
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // System.out.println("requestCode : " + requestCode);
        // System.out.println("resultCode : " + resultCode);
        // System.out.println("data : " + data);

        if (requestCode == 1001) {
            int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
            String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
            String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");

            if (resultCode == Activity.RESULT_OK) {
                try {
                    JSONObject jo = new JSONObject(purchaseData);
                    String sku = Utils.getString(jo, "productId");
                    String purchaseToken = Utils.getString(jo, "purchaseToken");

                    // System.out.println(jo);

                    // Utils.alert(this.activity, "You have bought the " + sku + ". Excellent choice, adventurer!");

                    if(this.buyListener != null) {
                        this.buyListener.bought(sku, purchaseToken);
                    }
                }
                catch (JSONException e) {
                    // Utils.alert(this.activity, "Failed to parse purchase data.");
                    e.printStackTrace();

                    if(this.buyListener != null) {
                        this.buyListener.failed(e);
                    }
                }
            }
        }
    }

    public void getPurchases() {
        try {
            Bundle ownedItems = mService.getPurchases(3, this.activity.getPackageName(), "inapp", null);
            int response = ownedItems.getInt("RESPONSE_CODE");
            if (response == 0) {
                ArrayList<String> ownedSkus = ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
                ArrayList<String>  purchaseDataList = ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
                ArrayList<String>  signatureList = ownedItems.getStringArrayList("INAPP_DATA_SIGNATURE_LIST");
                String continuationToken = ownedItems.getString("INAPP_CONTINUATION_TOKEN");

                for (int i = 0; i < purchaseDataList.size(); ++i) {
                    String purchaseData = purchaseDataList.get(i);
                    String signature = signatureList.get(i);
                    String sku = ownedSkus.get(i);

                    try {
                        JSONObject jo = new JSONObject(purchaseData);
                        String purchaseToken = Utils.getString(jo, "purchaseToken");

                        consume(purchaseToken);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public int consume(String token) {
        int response = -1;
        try {
            response = mService.consumePurchase(3, this.activity.getPackageName(), token);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return response;
    }

    public interface BuyListener {
        void bought(String sku, String purchaseToken);
        void failed(Exception e);
    }
}
