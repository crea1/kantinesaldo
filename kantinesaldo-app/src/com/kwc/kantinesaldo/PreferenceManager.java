package com.kwc.kantinesaldo;

import android.content.SharedPreferences;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

/**
 * This handles saving and loading of user preferences and downloaded data.
 *
 * @author Marius Kristensen
 */
public class PreferenceManager {
    private static final String CARD_NUMBER = "card_number";
    private static final String CARD_PIN = "card_pin";
    private static final String BALANCE = "balance";
    private static final String BALANCE_DATE = "balance_date_long";
    private static final String PREV_BALANCE = "prev_balance";
    private static final String PREV_BALANCE_DATE = "prev_balance_date_long";
    private static final String SERVICE_STATE = "service_state";
    private static final String BALANCE_NOTIFICATION_THRESHOLD = "balance_notification_threshold";
    private static final String BALANCE_DATE_OLD = "balance_date";
    private static final String PREV_BALANCE_DATE_OLD = "prev_balance_date";
    private static final String DATES_MIGRATED = "date_migrated";

    private final SharedPreferences prefs;
    private Locale locale;

    public PreferenceManager(SharedPreferences prefs, Locale locale) {
        this.prefs = prefs;
        this.locale = locale;
        if (!prefs.getBoolean(DATES_MIGRATED, false)) {
            if (prefs.contains(BALANCE_DATE_OLD)) { convertSavedDateStringsToLong(BALANCE_DATE_OLD, BALANCE_DATE); }
            if (prefs.contains(PREV_BALANCE_DATE_OLD)) { convertSavedDateStringsToLong(PREV_BALANCE_DATE, PREV_BALANCE_DATE); }
            savePreference(DATES_MIGRATED, true);
        }
    }

    public void savePreference(String key, String value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void savePreference(String key, boolean value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void savePreference(String key, long value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public void savePreference(String key, float value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat(key, value);
        editor.commit();
    }


    public void setPin(String pin) {
        savePreference(CARD_PIN, pin);
    }

    public String getSavedPin() {
        return prefs.getString(CARD_PIN, null);
    }

    public void setSavedCardNumber(String cardNumber) {
        savePreference(CARD_NUMBER, cardNumber);
    }

    public String getSavedCardNumber() {
        return prefs.getString(CARD_NUMBER, null);
    }

    public void setSavedBalance(String balance) {
        savePreference(BALANCE, balance);
    }

    public String getSavedBalance() {
        return prefs.getString(BALANCE, null);
    }

    public void setSavedBalanceDate(long time) {
        savePreference(BALANCE_DATE, time);
    }

    public long getSavedBalanceDate() {
        return prefs.getLong(BALANCE_DATE, 0l);
        //return date == 0l ? null : formatDate(date, locale);
    }

    public void setSavedPrevBalance(String prevBalance) {
        savePreference(PREV_BALANCE, prevBalance);
    }

    public String getSavedPrevBalance() {
        return prefs.getString(PREV_BALANCE, null);
    }

    public void setSavedPrevBalanceDate(long time) {
        savePreference(PREV_BALANCE_DATE, time);
    }

    public String getSavedPrevBalanceDate() {
        long date = prefs.getLong(BALANCE_DATE, 0l);
        return date == 0l ? null : formatDate(date);
    }

    public void setSavedServiceState(boolean serviceState) {
        savePreference(SERVICE_STATE, serviceState);
    }

    public boolean getSavedServiceState() {
        return prefs.getBoolean(SERVICE_STATE, false);
    }

    public void setBalanceThreshold(float threshold) {
        savePreference(BALANCE_NOTIFICATION_THRESHOLD, threshold);
    }

    public float getBalanceTreshold() {
        return prefs.getFloat(BALANCE_NOTIFICATION_THRESHOLD, 0.0f);
    }

    protected void convertSavedDateStringsToLong(String oldKey, String newKey) {
        SharedPreferences.Editor edit = prefs.edit();
        if (prefs.contains(oldKey)) {
            try {
                DateFormat dateFormatter = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, locale);
                Date parsedDate = dateFormatter.parse(prefs.getString(oldKey, null));
                savePreference(newKey, parsedDate.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            edit.remove(oldKey);
            edit.commit();
        }

    }

    public String formatDate(Long date) {
        DateFormat dateFormatter = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, locale);
        return dateFormatter.format(date);
    }
}
