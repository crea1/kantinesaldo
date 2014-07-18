package com.kwc.kantinesaldo;

import android.content.SharedPreferences;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

/**
 * @author Marius Kristensen
 */
public class PreferenceManager {
    public static final String CARD_NUMBER = "card_number";
    public static final String CARD_PIN = "card_pin";
    public static final String BALANCE = "balance";
    public static final String BALANCE_DATE = "balance_date_long";
    public static final String PREV_BALANCE = "prev_balance";
    public static final String PREV_BALANCE_DATE = "prev_balance_date_long";
    public static final String SERVICE_STATE = "service_state";
    protected static final String BALANCE_DATE_OLD = "balance_date";
    protected static final String PREV_BALANCE_DATE_OLD = "prev_balance_date";
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

    public String getSavedPin() {
        return prefs.getString(CARD_PIN, null);
    }

    public String getSavedCardNumber() {
        return prefs.getString(CARD_NUMBER, null);
    }

    public String getSavedBalance() {
        return prefs.getString(BALANCE, null);
    }

    public String getSavedBalanceDate() {
        long date = prefs.getLong(BALANCE_DATE, 0l);
        return date == 0l ? null : formatDate(date, locale);
    }

    public String getSavedPrevBalance() {
        return prefs.getString(PREV_BALANCE, null);
    }

    public String getSavedPrevBalanceDate() {
        long date = prefs.getLong(BALANCE_DATE, 0l);
        return date == 0l ? null : formatDate(date, locale);
    }

    public boolean getSavedServiceState() {
        return prefs.getBoolean(SERVICE_STATE, false);
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

    protected String formatDate(Long date, Locale locale) {
        DateFormat dateFormatter = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, locale);
        return dateFormatter.format(date);
    }
}
