package com.freadapp.fread.helpers;

import android.content.Context;
import android.net.ConnectivityManager;

import static android.content.Context.CONNECTIVITY_SERVICE;

public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String LANGUAGE_PARAM = "en";

    public static String buildWordSearchURL(String wordToSearch) {

        return Constants.OXFORD_API_ENDPOINT_URL + LANGUAGE_PARAM + "/" + wordToSearch;

    }

    public static boolean isNetworkAvailableAndConnected(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);

        boolean isNetworkAvailable = connectivityManager.getActiveNetworkInfo() != null;
        boolean isNetworkConnected = isNetworkAvailable && connectivityManager.getActiveNetworkInfo().isConnected();

        return isNetworkConnected;
    }


}
