package com.wolf.expensetracker;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    EditText expenseEditText, expenseReasonEditText;
    ListView expenseListView;
    ArrayAdapter<String> adapter;
    ArrayList<String> expenses;
    DBHelper dbHelper;
    TextView totalTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        expenseEditText = findViewById(R.id.expenseEditText);
        expenseReasonEditText = findViewById(R.id.expenseReasonEditText);
        expenseListView = findViewById(R.id.expenseListView);
        totalTextView = findViewById(R.id.totalTextView);

        dbHelper = new DBHelper(this);
        expenses = dbHelper.getAllExpenses();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, expenses);
        expenseListView.setAdapter(adapter);

        expenseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showDeleteDialog(position);
            }
        });

        updateTotal();
    }

    public void addExpense(View view) {
        String expenseReason = expenseReasonEditText.getText().toString().trim();
        String expenseAmount = expenseEditText.getText().toString().trim();

        if (!expenseReason.isEmpty() && !expenseAmount.isEmpty()) {
            dbHelper.addExpense(expenseReason, Double.parseDouble(expenseAmount));
            expenses.add(expenseReason + " - ₹" + expenseAmount);
            adapter.notifyDataSetChanged();
            expenseReasonEditText.setText("");
            expenseEditText.setText("");
            updateTotal();
        } else {
            Toast.makeText(this, "Enter expense reason and amount", Toast.LENGTH_SHORT).show();
        }
    }


    public void resetExpenses(View view) {
        dbHelper.resetExpenses();
        expenses.clear();
        adapter.notifyDataSetChanged();
        updateTotal();
    }

    private void updateTotal() {
        double total = dbHelper.getTotal();
        totalTextView.setText("Total - ₹" + total);
    }

    private void showDeleteDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Expense");
        builder.setMessage("Are you sure you want to delete this expense?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            dbHelper.deleteExpense(expenses.get(position));
            expenses.remove(position);
            adapter.notifyDataSetChanged();
            updateTotal();
            // Fetch expenses again after deletion
            expenses = dbHelper.getAllExpenses();
        });
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}
