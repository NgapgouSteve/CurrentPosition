package com.ngapgou_steve.currentposition.tools;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class PlayService {

    private static final String TAG = "SplashScreen";
    private static final int ERROR_DIAOLG_REQUEST = 9001;

    public static boolean isServicesOk(Context context, Activity activity){
        Log.e(TAG,"isServicesOK(): checking google services version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);
        if (available == ConnectionResult.SUCCESS){
            Log.e(TAG,"isServicesOK(): Google play services is working");
            return true;
        }else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Log.e(TAG,"isServicesOK(): an error occurred but we can fix it");
            Dialog dialog =GoogleApiAvailability.getInstance().getErrorDialog(activity, available, ERROR_DIAOLG_REQUEST);
            dialog.show();
        }else {
            //Toast.makeText(context, "You can't make Maps request", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}
