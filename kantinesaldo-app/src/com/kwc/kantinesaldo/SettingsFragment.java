package com.kwc.kantinesaldo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

/**
 * @author Marius Kristensen
 */
public class SettingsFragment extends DialogFragment {

    private static final String TAG = "kantinesaldo";

    private final PreferenceManager preferenceManager;

    public SettingsFragment(PreferenceManager preferenceManager) {
        super();
        this.preferenceManager = preferenceManager;
        setRetainInstance(true);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(getString(R.string.settings));
        View settingsView = LayoutInflater.from(getActivity()).inflate(R.layout.settings, null);
        builder.setView(settingsView);

        final Switch serviceSwitch = (Switch) settingsView.findViewById(R.id.serviceSwitch);
        final TextView thresholdText = (TextView) settingsView.findViewById(R.id.threshold);
        final SeekBar seekBar = (SeekBar) settingsView.findViewById(R.id.seekBar);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                if (progress == 0) {
                    thresholdText.setText(R.string.no_notification);
                } else {
                    thresholdText.setText(Integer.toString(progress) + ",-");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        serviceSwitch.setChecked(preferenceManager.getSavedServiceState());
        thresholdText.setText(Float.toString(preferenceManager.getBalanceTreshold()));
        seekBar.setProgress((int) preferenceManager.getBalanceTreshold());

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                float threshold = seekBar.getProgress();
                preferenceManager.setBalanceThreshold(threshold);
                if (preferenceManager.isCardInfoSet()) {
                    preferenceManager.setSavedServiceState(serviceSwitch.isChecked());
                } else {
                    preferenceManager.setSavedServiceState(false);
                }
                BalanceDownloadReceiver.scheduleAlarms(getActivity(), preferenceManager.getSavedServiceState());
                Log.d(TAG, "Saving (threshold:" + threshold + ", serviceState:" + serviceSwitch.isChecked() + ")");
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
