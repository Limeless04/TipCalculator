package com.tipcalculator;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.os.LocaleListCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.NumberFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private EditText etBillAmount;
    private Spinner spinnterTipPercent;
    private TextView tvTotalValue;
    private TextView tvTotalTipValue;


    private void calculateFinalAmount(){
        // Clean the input string before converting to Double
        String input = etBillAmount.getText().toString().replaceAll("[^\\d]", "");
        if (input.isEmpty()) return;

        // Convert back to a double (remembering we multiplied by 100 for formatting)
        double billAmount = Double.parseDouble(input);

        Locale localeId = new Locale("in", "ID");
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(localeId);
        currencyFormat.setMinimumFractionDigits(2);
        currencyFormat.setMaximumFractionDigits(2);

        String selectedTip = spinnterTipPercent.getSelectedItem().toString();

        double tipPercentage = Double.parseDouble(selectedTip.replace("%", ""))/ 100;
        double calculatedTip = billAmount * tipPercentage;
        double total = billAmount + calculatedTip;

        String formatedTip = currencyFormat.format(calculatedTip);
        String formatedTotal = currencyFormat.format(total);


        tvTotalTipValue.setText(formatedTip);
        tvTotalValue.setText(formatedTotal);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1. init view
        etBillAmount = findViewById(R.id.etBillAmount);
        spinnterTipPercent = findViewById(R.id.spinnerTipPercent);
        Button btnCalculate = findViewById(R.id.btnCalculate);
        tvTotalValue = findViewById(R.id.tvTotalValue);
        tvTotalTipValue = findViewById(R.id.tvTipAmountValue);

        // 2. create dropdown options
        String[] tipOptions = {"10%", "15%", "20%", "25%"};

        // 3. Setup adapter
        // This tell the spiner how to show the data
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                tipOptions
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnterTipPercent.setAdapter(adapter);

        // 4. Button logic
        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculateFinalAmount();
            }
        });

        etBillAmount.addTextChangedListener(new TextWatcher() {
            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    etBillAmount.removeTextChangedListener(this);

                    // 1. Better Regex: Remove everything except digits (0-9)
                    // This is safer than trying to name every symbol like Rp, dots, etc.
                    String cleanString = s.toString().replaceAll("[^\\d]", "");

                    if (!cleanString.isEmpty()) {
                        try {
                            double parsed = Double.parseDouble(cleanString);

                            // 2. IMPORTANT: NumberFormat already knows the Locale is Indonesian
                            // because of your 'attachBaseContext' override.
                            Locale localeID = new Locale("in", "ID");
                            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(localeID);

                            // If you want whole Rupiah (no ,00), use this:
                            currencyFormat.setMaximumFractionDigits(0);

                            String formatted = currencyFormat.format(parsed);

                            current = formatted;
                            etBillAmount.setText(formatted);
                            etBillAmount.setSelection(formatted.length());
                        } catch (NumberFormatException e) {
                            // Handle potential error
                        }
                    }

                    etBillAmount.addTextChangedListener(this);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });


    }
}