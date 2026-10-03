package com.smartpantry.app;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/** Add/Edit Ingredient screen (Create + Update), with input validation. */
public class AddEditActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "item_id";
    private static final String[] UNITS = {"pcs", "g", "kg", "ml", "l", "tsp", "tbsp", "cup"};

    private DatabaseHelper db;
    private EditText etName, etQty, etExpiry;
    private Spinner spUnit;
    private long editId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = new DatabaseHelper(this);
        etName = findViewById(R.id.etName);
        etQty = findViewById(R.id.etQty);
        etExpiry = findViewById(R.id.etExpiry);
        spUnit = findViewById(R.id.spUnit);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnClear = findViewById(R.id.btnClearDate);

        spUnit.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, UNITS));

        // If an id was passed in the Intent we are editing; otherwise we are adding.
        editId = getIntent().getLongExtra(EXTRA_ID, -1);
        if (editId != -1) {
            setTitle("Edit Ingredient");
            PantryItem item = db.getItem(editId);
            if (item != null) {
                etName.setText(item.getName());
                etQty.setText(UnitUtil.format(item.getQuantity()));
                etExpiry.setText(item.getExpiry());
                for (int i = 0; i < UNITS.length; i++) {
                    if (UNITS[i].equalsIgnoreCase(item.getUnit())) spUnit.setSelection(i);
                }
            }
        } else {
            setTitle("Add Ingredient");
        }

        etExpiry.setOnClickListener(v -> showDatePicker());
        btnClear.setOnClickListener(v -> etExpiry.setText(""));
        btnSave.setOnClickListener(v -> save());
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        String current = etExpiry.getText().toString().trim();
        if (!current.isEmpty()) {
            try {
                cal.setTime(new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(current));
            } catch (ParseException ignored) { }
        }
        new DatePickerDialog(this, (view, y, m, d) ->
                etExpiry.setText(String.format(Locale.US, "%04d-%02d-%02d", y, m + 1, d)),
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void save() {
        String name = etName.getText().toString().trim();
        String qtyText = etQty.getText().toString().trim();

        // ---- Input validation ----
        if (name.isEmpty()) {
            etName.setError("Ingredient name is required");
            return;
        }
        if (!name.matches(".*[A-Za-z].*")) {
            etName.setError("Name must contain letters");
            return;
        }
        if (name.length() > 50) {
            etName.setError("Name is too long (max 50 characters)");
            return;
        }
        double qty;
        try {
            qty = Double.parseDouble(qtyText);
        } catch (NumberFormatException e) {
            etQty.setError("Enter a valid quantity");
            return;
        }
        if (qty <= 0 || qty > 100000) {
            etQty.setError("Quantity must be greater than 0");
            return;
        }

        String unit = spUnit.getSelectedItem().toString();
        String expiry = etExpiry.getText().toString().trim();

        if (editId == -1) {
            db.addItem(name, qty, unit, expiry);
            Toast.makeText(this, "Ingredient added", Toast.LENGTH_SHORT).show();
        } else {
            db.updateItem(editId, name, qty, unit, expiry);
            Toast.makeText(this, "Ingredient updated", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
