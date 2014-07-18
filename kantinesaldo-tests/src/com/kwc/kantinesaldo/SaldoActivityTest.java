package com.kwc.kantinesaldo;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.TextView;

import java.util.Date;
import java.util.Locale;

public class SaldoActivityTest extends ActivityInstrumentationTestCase2<SaldoActivity> {

    private SaldoActivity activity;
    private TextView dateText;
    private TextView prevDateText;

    public SaldoActivityTest() {
        super(SaldoActivity.class);

    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        activity = getActivity();
        dateText = (TextView) activity.findViewById(R.id.dateText);
        prevDateText = (TextView) activity.findViewById(R.id.prevDateText);
    }

    public void testDateFieldsVisible() throws Exception {
        assertNotNull(dateText);
        assertNotNull(prevDateText);
        assertTrue(View.VISIBLE == dateText.getVisibility());
        assertTrue(View.VISIBLE == prevDateText.getVisibility());
    }

    public void testConvertOldSavedDatesToLong() throws Exception {
        long now = new Date().getTime();

        Locale locale = activity.getResources().getConfiguration().locale;

        String formattedDate = activity.preferenceManager.formatDate(now, locale);
        activity.preferenceManager.savePreference(PreferenceManager.BALANCE_DATE_OLD, formattedDate);
        activity.preferenceManager.savePreference(PreferenceManager.PREV_BALANCE_DATE_OLD, formattedDate);
        activity.preferenceManager.convertSavedDateStringsToLong(PreferenceManager.BALANCE_DATE_OLD, PreferenceManager.BALANCE_DATE);
        activity.preferenceManager.convertSavedDateStringsToLong(PreferenceManager.PREV_BALANCE_DATE_OLD, PreferenceManager.PREV_BALANCE_DATE);

        assertEquals(formattedDate, activity.preferenceManager.getSavedBalanceDate());
        assertEquals(formattedDate, activity.preferenceManager.getSavedPrevBalanceDate());

        activity.preferenceManager.savePreference(PreferenceManager.BALANCE_DATE, 0l);
        assertEquals(null, activity.preferenceManager.getSavedBalanceDate());
    }

}
