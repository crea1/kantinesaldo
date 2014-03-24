package com.kwc.kantinesaldo;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                new HttpGetBalance().execute();
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


    private class HttpGetBalance extends AsyncTask<Void, Void, String> {

        private static final String baseUrl = "http://icare.myissworld.net/loginAction.do?requiresPIN=true";
        private static final String cardNumberParam = "&iCardNumber=";
        private static final String pinParam = "&PIN=";
        private String result = null;

        @Override
        protected String doInBackground(Void... voids) {
            try {
                String url = baseUrl + cardNumberParam + cardNumber + pinParam + pin;
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    Log.d(TAG, "fetching from " + url);
                    InputStream in = connection.getInputStream();
                    result = readStream(in);
                }
            } catch (MalformedURLException e) {
                Log.e(TAG, "MalformedURLException");
            } catch (IOException e) {
                Log.e(TAG, "IOException");
            }
            Log.d(TAG, "result= " + result);
            return result;

        }

        @Override
        protected void onPostExecute(String s) {
            balanceView.setText(extractBalanceFromHtml(result));
            updateButton.setEnabled(true);
        }

        private String extractBalanceFromHtml(String html) {
            String regex = "(-*\\d+\\.\\d+)";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(html);
            if (matcher.find()) {
                return matcher.group(0);
            } else {
                return null;
            }

        }

        private String readStream(InputStream in) {
            BufferedReader reader = null;
            StringBuilder data = new StringBuilder("");
            try {
                reader = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = reader.readLine()) != null) {
                    data.append(line);
                }
            } catch (IOException e) {
                Log.e(TAG, "IOException");
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return data.toString();
        }
    }

}
