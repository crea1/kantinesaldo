package com.kwc.kantinesaldo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.util.Log;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author Marius Kristensen
 */
public class BalanceDownloadReceiver extends BroadcastReceiver {

    private static final String TAG = "kantinesaldo";
    private static final String PREF_CARD_NUMBER = "card_number";
    private static final String PREF_CARD_PIN = "card_pin";
    private static final String PREF_BALANCE = "balance";
    private static final String PREF_BALANCE_DATE = "balance_date";
    private static final String PREF_PREV_BALANCE = "prev_balance";
    private static final String PREF_PREV_BALANCE_DATE = "prev_balance_date";
    private static final String PREF_SERVICE_STATE = "service_state";
    private SharedPreferences prefs;
    private static final long intervalMillis = 1000 * 60 * 60 * 6L; // 6 hours

    @Override
    public void onReceive(final Context context, Intent intent) {
        prefs = context.getSharedPreferences("kantinesaldo", Context.MODE_PRIVATE);
        Log.d(TAG, new Date().toString());
        downloadBalance(context);
        scheduleAlarms(context, getSavedServiceState());

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

        HttpGetBalance httpGetBalance = new HttpGetBalance(context, getSavedCardNumber(), getSavedPin()) {
            @Override
            public void onResult(String balance) {
                if (balance != null) {
                    DateFormat balanceDate = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, Locale.UK);
                    String balanceDates = balanceDate.format(new Date());

                    if (!balance.equals(getSavedBalance())) {
                        savePreference(PREF_PREV_BALANCE, getSavedBalance());
                        savePreference(PREF_PREV_BALANCE_DATE, getSavedBalanceDate());
                        savePreference(PREF_BALANCE, balance);

                    }
                    savePreference(PREF_BALANCE_DATE, balanceDates);
                    Log.d(TAG, "Downloaded balance " + balance);
                }
            }
        };
        httpGetBalance.execute();
    }

    private void savePreference(String key, String value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    private String getSavedPin() {
        return prefs.getString(PREF_CARD_PIN, null);
    }

    private String getSavedCardNumber() {
        return prefs.getString(PREF_CARD_NUMBER, null);
    }


    private String getSavedBalance() {
        return prefs.getString(PREF_BALANCE, null);
    }

    private String getSavedBalanceDate() {
        return prefs.getString(PREF_BALANCE_DATE, null);
    }

    private boolean getSavedServiceState() {
        return prefs.getBoolean(PREF_SERVICE_STATE, false);
    }
}
