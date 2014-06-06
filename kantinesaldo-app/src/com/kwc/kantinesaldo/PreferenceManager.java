package com.kwc.kantinesaldo;

import android.content.ContextWrapper;
import android.content.SharedPreferences;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

/**
 * @author Marius Kristensen
 */
public class PreferenceManager {
    public static final String CARD_NUMBER = "card_number";
    public static final String CARD_PIN = "card_pin";
    public static final String BALANCE = "balance";
    public static final String BALANCE_DATE = "balance_date";
    public static final String PREV_BALANCE = "prev_balance";
    public static final String PREV_BALANCE_DATE = "prev_balance_date";
    public static final String SERVICE_STATE = "service_state";

    private final SharedPreferences prefs;

    public PreferenceManager(SharedPreferences prefs) {
        this.prefs = prefs;
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
        return prefs.getString(BALANCE_DATE, null);
    }

    public String getSavedPrevBalance() {
        return prefs.getString(PREV_BALANCE, null);
    }

    public String getSavedPrevBalanceDate() {
        return prefs.getString(PREV_BALANCE_DATE, null);
    }

    public boolean getSavedServiceState() {
        return prefs.getBoolean(SERVICE_STATE, false);
    }

}
