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
public class SettingsFragment extends DialogFragment {

    private final PreferenceManager preferenceManager;

    public SettingsFragment(PreferenceManager preferenceManager) {
        this.preferenceManager = preferenceManager;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(getString(R.string.settings));
        View settingsView = LayoutInflater.from(getActivity()).inflate(R.layout.settings, null);
        builder.setView(settingsView);

        final Switch serviceSwitch = (Switch) settingsView.findViewById(R.id.serviceSwitch);
        final EditText thresholdText = (EditText) settingsView.findViewById(R.id.threshold);

        serviceSwitch.setChecked(preferenceManager.getSavedServiceState());

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                preferenceManager.setBalanceThreshold(Float.parseFloat(thresholdText.getText().toString()));
                if (preferenceManager.isCardInfoSet()) {
                    preferenceManager.setSavedServiceState(serviceSwitch.isChecked());
                } else {
                    preferenceManager.setSavedServiceState(false);
                }
                BalanceDownloadReceiver.scheduleAlarms(getActivity(), preferenceManager.getSavedServiceState());
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SettingsFragment.this.getDialog().cancel();
            }
        });

        return builder.create();
    }
}
