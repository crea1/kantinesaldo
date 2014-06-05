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
        assertEquals("", dateText.getText());
        assertEquals("", prevDateText.getText());
        assertTrue(View.VISIBLE == dateText.getVisibility());
        assertTrue(View.VISIBLE == prevDateText.getVisibility());
    }


}
