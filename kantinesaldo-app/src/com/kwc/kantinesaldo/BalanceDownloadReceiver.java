package com.kwc.kantinesaldo;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import java.util.Date;

/**
 * This is the services that updates the balance every n hours if the option is enabled
 * in the user preferences.
 *
 * @author Marius Kristensen
 */
public class BalanceDownloadReceiver extends BroadcastReceiver {

    private static final String TAG = "kantinesaldo";
    private static final int NOTIFICATION_ID = 1;
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
            Log.d(TAG, "Automatic download turned ON");
        } else {
            alarmManager.cancel(pendingIntent);
            Log.d(TAG, "Automatic download turned OFF");
        }
    }

    private void downloadBalance(final Context context) {

        HttpGetBalance httpGetBalance = new HttpGetBalance(context, preferenceManager.getSavedCardNumber(), preferenceManager.getSavedPin()) {
            @Override
            public void onResult(String balance) {
                if (balance != null) {
                    if (!balance.equals(preferenceManager.getSavedBalance())) {
                        preferenceManager.setSavedPrevBalance(preferenceManager.getSavedBalance());
                        preferenceManager.setSavedPrevBalanceDate(preferenceManager.getSavedBalanceDate());
                        preferenceManager.setSavedBalance(balance);
                        fireBalanceBelowThresholdNotification(context);
                    }
                    preferenceManager.setSavedBalanceDate(new Date().getTime());
                    Log.d(TAG, "Downloaded balance " + balance);
                }
            }
        };
        httpGetBalance.execute();
    }

    public void fireBalanceBelowThresholdNotification(Context context) {
        float balanceTreshold = preferenceManager.getBalanceTreshold();
        float balance = preferenceManager.getBalance();
        if (balanceTreshold >= balance) {
            Log.d(TAG, "Notification fired " + balance + " is below " + balanceTreshold);
            Intent intent = new Intent(context, SaldoActivity.class);
            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
            taskStackBuilder.addParentStack(SaldoActivity.class);
            taskStackBuilder.addNextIntent(intent);
            PendingIntent saldoActivity = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            Notification.Builder notificationBuilder = new Notification.Builder(context)
                    .setSmallIcon(R.drawable.ic_stat_info)
                    .setTicker(context.getString(R.string.low_balance))
                    .setAutoCancel(true)
                    .setContentTitle(context.getString(R.string.low_balance))
                    .setContentText(context.getString(R.string.low_balance_text, balance))
                    .setContentIntent(saldoActivity);

            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(1, notificationBuilder.build());
        }
    }
}
