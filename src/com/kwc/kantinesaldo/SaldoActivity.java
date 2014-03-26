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

public class SaldoActivity extends Activity {

    private static final String TAG = "kantinesaldo";
    private static final String CARD_NUMBER = "card_number";
    private static final String CARD_PIN = "card_pin";
    private static final String STATE_BALANCE = "balance";
    private TextView balanceView;
    private Button updateButton;
    private SharedPreferences prefs;
    private String balance;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getPreferences(MODE_PRIVATE);
        setContentView(R.layout.main);

        balanceView = (TextView) findViewById(R.id.balanceView);


        updateButton = (Button) findViewById(R.id.refreshBalanceButton);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cardNumber = getCardNumber();
                String pin = getPin();

                if (cardNumber != null && !cardNumber.isEmpty() || pin != null && !pin.isEmpty()) {
                    HttpGetBalance httpGetBalance = new HttpGetBalance(getApplicationContext(), cardNumber, pin) {

                        @Override
                        public void onResult(String balance) {
                            SaldoActivity.this.balance = balance;
                            balanceView.setText(balance);
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
        CardInfoDialogFragment cardInfoDialogFragment = new CardInfoDialogFragment(getCardNumber(), getPin()) {

            @Override
            protected void saveCardInfo(String cardNumber, String pin) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(CARD_NUMBER, cardNumber);
                editor.putString(CARD_PIN, pin);
                editor.commit();
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
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            this.balance = savedInstanceState.getString(STATE_BALANCE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (balance != null) {
            balanceView.setText(balance);
        }
    }

    private String getPin() {
        return prefs.getString(CARD_PIN, null);
    }

    private String getCardNumber() {
        return prefs.getString(CARD_NUMBER, null);
    }

}
