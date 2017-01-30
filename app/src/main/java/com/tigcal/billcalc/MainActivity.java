package com.tigcal.billcalc;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,##0.00");
    public static final String COMMA = ",";
    public static final String EMPTY_STRING = "";
    public static final String SPACE_STRING = " ";
    
    private TextView totalKwhText;
    private TextView totalAmountText;
    private TextView tigcalKwhText;
    private TextView neighborKwhText;

    private int totalKwh = 0;
    private BigDecimal totalAmount = BigDecimal.ZERO;
    private TextView tigcalAmountText;
    private TextView neighborAmountText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        totalKwhText = (TextView) findViewById(R.id.total_kwh_text);
        totalAmountText = (TextView) findViewById(R.id.total_amount_text);
        tigcalKwhText = (TextView) findViewById(R.id.tigcal_kwh_text);
        neighborKwhText = (TextView) findViewById(R.id.neighbor_kwh_text);
        tigcalAmountText = (TextView) findViewById(R.id.tigcal_amount_text);
        neighborAmountText = (TextView) findViewById(R.id.neighbor_amount_text);

        View.OnFocusChangeListener decimalTextOnFocusChangeListener = new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(!(view instanceof EditText)) {
                    return;
                }

                EditText editText = (EditText) view;
                if(!hasFocus && !EMPTY_STRING.equals(editText.getText().toString())) {
                    String input = editText.getText().toString();
                    BigDecimal decimal = new BigDecimal(input.replaceAll("\\,", EMPTY_STRING));
                    editText.setText(formatDecimal(decimal));
                }
            }
        };

        totalAmountText.setOnFocusChangeListener(decimalTextOnFocusChangeListener);

        tigcalKwhText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!EMPTY_STRING.equals(s.toString())) {
                    tigcalAmountText.setText(formatDecimal(computeAmountDue(new BigDecimal(s.toString()))));
                } else {
                    tigcalAmountText.setText(formatDecimal(BigDecimal.ZERO));
                }
            }
        });
        neighborKwhText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!EMPTY_STRING.equals(s.toString())) {
                    neighborAmountText.setText(formatDecimal(computeAmountDue(new BigDecimal(s.toString()))));
                } else {
                    neighborAmountText.setText(formatDecimal(BigDecimal.ZERO));
                }
            }
        });

        displayCachedBill();
    }

    private void displayCachedBill() {
        //TODO
    }

    private BigDecimal computeAmountDue(BigDecimal kwh) {
        totalKwh = getInteger(totalKwhText.getText().toString());
        totalAmount = getDecimalValue(totalAmountText.getText().toString());

        if(totalAmount.equals(BigDecimal.ZERO)) {
            return BigDecimal.ZERO;
        }

        if(kwh.compareTo(new BigDecimal(totalKwh)) > 0) {
            return totalAmount;
        }

        //TODO if this + other kwh is more than total

        MathContext mathContext = new MathContext(2, RoundingMode.HALF_UP);
        BigDecimal tigcalAmount = kwh.divide(BigDecimal.valueOf(totalKwh), mathContext);
        return tigcalAmount.multiply(totalAmount);
    }

    private int getInteger(String integerString) {
        if(integerString == null || EMPTY_STRING.equals(integerString)) {
            return 0;
        } else {
            return Integer.parseInt(integerString);
        }
    }

    private String formatDecimal(BigDecimal decimal) {
        return DECIMAL_FORMAT.format(decimal);
    }

    private BigDecimal getDecimalValue(String decimalString) {
        BigDecimal decimalValue = BigDecimal.ZERO;

        if (decimalString != null && !decimalString.equals(EMPTY_STRING)) {
            if (decimalString.contains(COMMA)) {
                decimalString = decimalString.replace(COMMA, EMPTY_STRING);
            }

            if (decimalString.contains(SPACE_STRING)) {
                decimalString = decimalString.replace(SPACE_STRING, EMPTY_STRING);
            }

            try {
                decimalValue = BigDecimal.valueOf(Math.max(0, Double.parseDouble(decimalString)));
            } catch (NumberFormatException exception) {
                //Do nothing, decimal value will still be BigDecimal.ZERO
            }
        }

        return decimalValue;
    }
}
