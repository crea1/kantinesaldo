package com.kwc.kantinesaldo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;

/**
 * @author Marius Kristensen
 */
public abstract class CardInfoDialogFragment extends DialogFragment {

    final private String cardNumber;
    final private String pin;
    private final boolean serviceActive;

    public CardInfoDialogFragment(String cardNumber, String pin, boolean serviceActive) {
        super();
        this.cardNumber = cardNumber;
        this.pin = pin;
        this.serviceActive = serviceActive;
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
        final Switch serviceSwitch = (Switch) cardInfoView.findViewById(R.id.serviceSwitch);

        cardNumberText.setText(cardNumber);
        pinText.setText(pin);
        serviceSwitch.setChecked(serviceActive);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                saveSettings(cardNumberText.getText().toString(), pinText.getText().toString(), serviceSwitch.isChecked());
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



    protected abstract void saveSettings(String cardNumber, String pin, boolean isServiceActive);
}
