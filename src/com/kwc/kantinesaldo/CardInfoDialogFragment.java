package com.kwc.kantinesaldo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * @author Marius Kristensen
 */
public abstract class CardInfoDialogFragment extends DialogFragment {

    String cardNumber;
    String pin;

    public CardInfoDialogFragment(String cardNumber, String pin) {
        super();
        this.cardNumber = cardNumber;
        this.pin = pin;
        setRetainInstance(true);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Kortinformasjon");

        View cardInfoView = LayoutInflater.from(getActivity()).inflate(R.layout.cardinfo, null);
        builder.setView(cardInfoView);

        final EditText cardNumberText = (EditText) cardInfoView.findViewById(R.id.cardNumberEdit);
        final EditText pinText = (EditText) cardInfoView.findViewById(R.id.pinEdit);

        cardNumberText.setText(cardNumber);
        pinText.setText(pin);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                saveCardInfo(cardNumberText.getText().toString(), pinText.getText().toString());
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                CardInfoDialogFragment.this.getDialog().cancel();
            }
        });

        return builder.create();
    }



    protected abstract void saveCardInfo(String cardNumber, String pin);
}
