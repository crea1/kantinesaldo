package com.kwc.kantinesaldo;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.TextView;

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

    public void testParseFloat() throws Exception {
        activity.preferenceManager.setSavedBalance("20.0");
        assertEquals(20.0f, activity.preferenceManager.getBalance());

        activity.preferenceManager.setSavedBalance("20");
        assertEquals(20.0f, activity.preferenceManager.getBalance());

        activity.preferenceManager.setSavedBalance("20.30");
        assertEquals(20.3f, activity.preferenceManager.getBalance());

        activity.preferenceManager.setSavedBalance("-30.23");
        assertEquals(-30.23f, activity.preferenceManager.getBalance());

        activity.preferenceManager.setSavedBalance("10,4");
        assertEquals(10.4f, activity.preferenceManager.getBalance());

        activity.preferenceManager.setSavedBalance("300.230001");
        assertEquals(300.23f, activity.preferenceManager.getBalance());

        activity.preferenceManager.setSavedBalance(null);
        assertEquals(0f, activity.preferenceManager.getBalance());

        activity.preferenceManager.setSavedBalance("");
        assertEquals(0f, activity.preferenceManager.getBalance());

    }

    //    public void testConvertOldSavedDatesToLong() throws Exception {
//        long now = new Date().getTime();
//
//        Locale locale = activity.getResources().getConfiguration().locale;
//
//        //String formattedDate = activity.preferenceManager.formatDate(now, locale);
//        //activity.preferenceManager.savePreference(PreferenceManager.BALANCE_DATE_OLD, formattedDate);
//        //activity.preferenceManager.savePreference(PreferenceManager.PREV_BALANCE_DATE_OLD, formattedDate);
//        //activity.preferenceManager.convertSavedDateStringsToLong(PreferenceManager.BALANCE_DATE_OLD, PreferenceManager.BALANCE_DATE);
//        //activity.preferenceManager.convertSavedDateStringsToLong(PreferenceManager.PREV_BALANCE_DATE_OLD, PreferenceManager.PREV_BALANCE_DATE);
//
//        assertEquals(formattedDate, activity.preferenceManager.getSavedBalanceDate());
//        assertEquals(formattedDate, activity.preferenceManager.getSavedPrevBalanceDate());
//
//        activity.preferenceManager.setSavedBalanceDate(0l);
//        assertEquals(null, activity.preferenceManager.getSavedBalanceDate());
//    }

}
