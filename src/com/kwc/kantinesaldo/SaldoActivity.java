package com.kwc.kantinesaldo;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class SaldoActivity extends Activity {

    private static final String TAG = "kantinesaldo";
    private static final String PREF_CARD_NUMBER = "card_number";
    private static final String PREF_CARD_PIN = "card_pin";
    private static final String PREF_BALANCE = "balance";
    private static final String PREF_BALANCE_DATE = "balance_date";
    private static final String STATE_BALANCE = "balance";
    private static final String STATE_CARDINFO_SHOWING = "cardinfo_showing";
    private TextView balanceView;
    private TextView dateTimeView;
    private Button updateButton;
    private SharedPreferences prefs;
    private String balance;
    private CardInfoDialogFragment cardInfoDialogFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getPreferences(MODE_PRIVATE);
        setContentView(R.layout.main);

        dateTimeView = (TextView) findViewById(R.id.dateTimeView);
        dateTimeView.setText(getSavedBalanceDate());
        balanceView = (TextView) findViewById(R.id.balanceView);
        balanceView.setText(getSavedBalance());


        updateButton = (Button) findViewById(R.id.refreshBalanceButton);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cardNumber = getSavedCardNumber();
                String pin = getSavedPin();

                if (cardNumber != null && !cardNumber.isEmpty() || pin != null && !pin.isEmpty()) {
                    HttpGetBalance httpGetBalance = new HttpGetBalance(getApplicationContext(), cardNumber, pin) {

                        @Override
                        public void onResult(String balance) {
                            SaldoActivity.this.balance = balance;

                            DateFormat balanceDate = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, Locale.UK);
                            String balanceDates = balanceDate.format(new Date());

                            dateTimeView.setText(balanceDates);
                            balanceView.setText(balance);

                            savePreference(PREF_BALANCE, balance);
                            savePreference(PREF_BALANCE_DATE, balanceDates);

                            updateButton.setEnabled(true);
                        }
                    };
                    httpGetBalance.execute();
                    updateButton.setEnabled(false);
                } else {
                    showCardInfoDialog();
                }
            }
        });
    }

    private void showCardInfoDialog() {
        cardInfoDialogFragment = new CardInfoDialogFragment(getSavedCardNumber(), getSavedPin()) {

            @Override
            protected void saveCardInfo(String cardNumber, String pin) {
                savePreference(PREF_CARD_NUMBER, cardNumber);
                savePreference(PREF_CARD_PIN, pin);
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
        outState.putString(STATE_BALANCE, balance);

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
            this.balance = savedInstanceState.getString(STATE_BALANCE);
            if (savedInstanceState.getBoolean(STATE_CARDINFO_SHOWING)) {
                showCardInfoDialog();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (balance != null) {
            balanceView.setText(balance);
        }
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

}
