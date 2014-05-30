package com.kwc.kantinesaldo;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Date;

public class SaldoActivity extends Activity {

    private static final String TAG = "kantinesaldo";
    private static final String PREF_CARD_NUMBER = "card_number";
    private static final String PREF_CARD_PIN = "card_pin";
    private static final String PREF_BALANCE = "balance";
    private static final String PREF_BALANCE_DATE = "balance_date";
    private static final String PREF_PREV_BALANCE = "prev_balance";
    private static final String PREF_PREV_BALANCE_DATE = "prev_balance_date";
    private static final String PREF_SERVICE_STATE = "service_state";
    private static final String STATE_CARDINFO_SHOWING = "cardinfo_showing";
    private TextView balanceView;
    private TextView dateTimeView;
    private TextView prevBalanceView;
    private TextView prevBalanceTextView;
    private TextView prevDateTimeView;
    private TextView diffView;
    private Button updateButton;
    private SharedPreferences prefs;
    private CardInfoDialogFragment cardInfoDialogFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences("kantinesaldo", MODE_PRIVATE);
        setContentView(R.layout.main);

        balanceView = (TextView) findViewById(R.id.balanceView);
        dateTimeView = (TextView) findViewById(R.id.dateText);
        prevBalanceView = (TextView) findViewById(R.id.prevBalanceView);
        prevDateTimeView = (TextView) findViewById(R.id.prevDateText);
        diffView = (TextView) findViewById(R.id.diff);
        prevBalanceTextView = (TextView) findViewById(R.id.prevBalanceText);

        if (!isCardInfoSet()) {
            showCardInfoDialog();
        }

        updateButton = (Button) findViewById(R.id.refreshBalanceButton);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (isCardInfoSet()) {
                    if (isNetworkAvailable()) {
                        HttpGetBalance httpGetBalance = new HttpGetBalance(getApplicationContext(), getSavedCardNumber(), getSavedPin()) {

                            @Override
                            public void onResult(String balance) {
                                if (balance != null) {
                                    DateFormat balanceDate = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, getResources().getConfiguration().locale);
                                    String balanceDates = balanceDate.format(new Date());

                                    if (!balance.equals(getSavedBalance())) {
                                        savePreference(PREF_PREV_BALANCE, getSavedBalance());
                                        savePreference(PREF_PREV_BALANCE_DATE, getSavedBalanceDate());
                                        savePreference(PREF_BALANCE, balance);

                                    }
                                    savePreference(PREF_BALANCE_DATE, balanceDates);
                                    updateDisplay();

                                }

                                updateButton.setEnabled(true);
                            }
                        };
                        httpGetBalance.execute();
                        updateButton.setEnabled(false);
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.not_connecteded, Toast.LENGTH_LONG).show();
                    }
                } else {
                    showCardInfoDialog();
                }
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return (activeNetworkInfo != null && activeNetworkInfo.isConnected());
    }

    private boolean isCardInfoSet() {
        String cardNumber = getSavedCardNumber();
        String pin = getSavedPin();
        return cardNumber != null && !cardNumber.isEmpty() && pin != null && !pin.isEmpty();
    }

    private void showCardInfoDialog() {
        cardInfoDialogFragment = new CardInfoDialogFragment(getSavedCardNumber(), getSavedPin(), getSavedServiceState()) {

            @Override
            protected void saveSettings(String cardNumber, String pin, boolean isServiceActive) {
                savePreference(PREF_CARD_NUMBER, cardNumber);
                savePreference(PREF_CARD_PIN, pin);
                if (isCardInfoSet()) {
                    savePreference(PREF_SERVICE_STATE, isServiceActive);
                } else {
                    savePreference(PREF_SERVICE_STATE, false);
                }
                BalanceDownloadReceiver.scheduleAlarms(SaldoActivity.this, getSavedServiceState());
            }

        };
        cardInfoDialogFragment.show(getFragmentManager(), TAG);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            showCardInfoDialog();
        }
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (cardInfoDialogFragment != null && cardInfoDialogFragment.getDialog() != null) {
            outState.putBoolean(STATE_CARDINFO_SHOWING, true);
        } else {
            outState.putBoolean(STATE_CARDINFO_SHOWING, false);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(STATE_CARDINFO_SHOWING)) {
                showCardInfoDialog();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDisplay();
    }

    private void updateDisplay() {
        balanceView.setText(getSavedBalance());

        String savedBalanceDate = getSavedBalanceDate();
        if (savedBalanceDate != null) {
            dateTimeView.setText(getResources().getString(R.string.datetime_text, savedBalanceDate));
        }

        String savedPrevBalance = getSavedPrevBalance();
        if (savedPrevBalance != null) {
            prevBalanceView.setText(savedPrevBalance);
            String savedPrevBalanceDate = getSavedPrevBalanceDate();
            prevDateTimeView.setText(getResources().getString(R.string.datetime_prev_text, savedPrevBalanceDate));
            prevBalanceTextView.setText(getResources().getString(R.string.prev_saldo_text));
            try {
                float diff = Float.parseFloat(getSavedBalance()) - Float.parseFloat(savedPrevBalance);
                DecimalFormat format = new DecimalFormat("#.00");
                diffView.setText("" + format.format(diff));
            } catch (NumberFormatException e) {
                diffView.setText("");
            }
        }
    }

    private void savePreference(String key, String value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    private void savePreference(String key, boolean value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
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

    private String getSavedPrevBalance() {
        return prefs.getString(PREF_PREV_BALANCE, null);
    }

    private String getSavedPrevBalanceDate() {
        return prefs.getString(PREF_PREV_BALANCE_DATE, null);
    }

    private boolean getSavedServiceState() {
        return prefs.getBoolean(PREF_SERVICE_STATE, false);
    }
}
