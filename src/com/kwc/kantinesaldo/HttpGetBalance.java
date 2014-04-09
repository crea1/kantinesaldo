package com.kwc.kantinesaldo;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Marius Kristensen
 */
public abstract class HttpGetBalance extends AsyncTask<Void, Void, String> {

    public abstract void onResult(String balance);

    private static final String TAG = "kantinesaldo";

    private static final String baseUrl = "http://icare.myissworld.net/loginAction.do?requiresPIN=true";
    private static final String cardNumberParam = "&iCardNumber=";
    private static final String pinParam = "&PIN=";
    private final Context context;
    private final String cardNumber;
    private final String pin;

    public HttpGetBalance(Context applicationContext, String cardNumber, String pin) {
        super();
        this.context = applicationContext;
        this.cardNumber = cardNumber;
        this.pin = pin;
    }


    @Override
    protected String doInBackground(Void... voids) {
        String result = null;
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
        return result;

    }

    @Override
    protected void onPostExecute(String html) {
        if (html == null) {
            Toast.makeText(context, "Feil ved henting av saldo", Toast.LENGTH_LONG).show();
            onResult(null);
        } else if (pinIsIncorrect(html)) {
            Toast.makeText(context, "Feil pinkode", Toast.LENGTH_LONG).show();
            onResult(null);
        } else if (cardnumberIsIncorrect(html)) {
            Toast.makeText(context, "Ugyldig kortnummer", Toast.LENGTH_LONG).show();
            onResult(null);
        } else {
            onResult(extractBalanceFromHtml(html));
        }

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

    private boolean pinIsIncorrect(String html) {
        return html.contains("The PIN you entered is incorrect.")  || html.contains("Please enter your PIN");
    }

    private boolean cardnumberIsIncorrect(String html) {
        return html.contains("Invalid card number");
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
