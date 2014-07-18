package com.kwc.kantinesaldo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import java.util.Date;

/**
 * @author Marius Kristensen
 */
public class BalanceDownloadReceiver extends BroadcastReceiver {

    private static final String TAG = "kantinesaldo";
    private PreferenceManager preferenceManager;
    private static final long intervalMillis = 1000 * 60 * 60 * 6L; // 6 hours

    @Override
    public void onReceive(final Context context, Intent intent) {
        preferenceManager = new PreferenceManager(context.getSharedPreferences("kantinesaldo", Context.MODE_PRIVATE), context.getResources().getConfiguration().locale);
        Log.d(TAG, new Date().toString());
        downloadBalance(context);
        scheduleAlarms(context, preferenceManager.getSavedServiceState());

    }

    static void scheduleAlarms(Context context, boolean isServiceActive) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent balanceDownloadReceiver = new Intent(context, BalanceDownloadReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, balanceDownloadReceiver, PendingIntent.FLAG_UPDATE_CURRENT);
        if (isServiceActive) {

            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime() + intervalMillis,
                    intervalMillis,
                    pendingIntent);
            Log.d(TAG, "ON");
        } else {
            alarmManager.cancel(pendingIntent);
            Log.d(TAG, "OFF");
        }
    }

    private void downloadBalance(final Context context) {

        HttpGetBalance httpGetBalance = new HttpGetBalance(context, preferenceManager.getSavedCardNumber(), preferenceManager.getSavedPin()) {
            @Override
            public void onResult(String balance) {
                if (balance != null) {
                    if (!balance.equals(preferenceManager.getSavedBalance())) {
                        preferenceManager.savePreference(PreferenceManager.PREV_BALANCE, preferenceManager.getSavedBalance());
                        preferenceManager.savePreference(PreferenceManager.PREV_BALANCE_DATE, preferenceManager.getSavedBalanceDate());
                        preferenceManager.savePreference(PreferenceManager.BALANCE, balance);

                    }
                    preferenceManager.savePreference(PreferenceManager.BALANCE_DATE, new Date().getTime());
                    Log.d(TAG, "Downloaded balance " + balance);
                }
            }
        };
        httpGetBalance.execute();
    }
}
