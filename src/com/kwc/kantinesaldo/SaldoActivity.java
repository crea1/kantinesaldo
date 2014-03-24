package com.kwc.kantinesaldo;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SaldoActivity extends Activity {

    private static final String TAG = "kantinesaldo";
    private static final String CARD_NUMBER = "card_number";
    private static final String CARD_PIN = "card_pin";
    private TextView balanceView;
    private Button updateButton;
    String cardNumber;
    String pin;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        setContentView(R.layout.main);

        balanceView = (TextView) findViewById(R.id.balanceView);

        updateButton = (Button) findViewById(R.id.refreshBalanceButton);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardNumber = prefs.getString(CARD_NUMBER, null);
                pin = prefs.getString(CARD_PIN, null);
                HttpGetBalance httpGetBalance = new HttpGetBalance(getApplicationContext(), prefs.getString(CARD_NUMBER, null), prefs.getString(CARD_PIN, null)) {

                    @Override
                    public void onResult(String balance) {
                        balanceView.setText(balance);
                        updateButton.setEnabled(true);
                    }
                };
                httpGetBalance.execute();
                updateButton.setEnabled(false);
            }
        });

        Button updateCardInfoButton = (Button) findViewById(R.id.updateCardInfoButton);
        updateCardInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CardInfoDialogFragment cardInfoDialogFragment = new CardInfoDialogFragment(prefs.getString(CARD_NUMBER, null), prefs.getString(CARD_PIN, null)) {

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
        });
    }

}
