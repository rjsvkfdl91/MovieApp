package com.example.s522050.movieapp.Helper;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkHelper {

    public static boolean isNetworkConnected(Context context) {

        ConnectivityManager connectivityManager= (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        try{
            assert connectivityManager != null;
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
