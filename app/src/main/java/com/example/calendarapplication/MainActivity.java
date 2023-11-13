package com.example.calendarapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.calendarapplication.CalendarAdapter;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import android.app.AlertDialog; // Add this import


public class MainActivity extends AppCompatActivity implements CalendarAdapter.OnItemListener {

    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private LocalDate selectedDate;
    private ArrayList<LocalDate> periodDays;

    private LocalDate periodStartDate;
    private LocalDate periodEndDate;

    private static final int PERIOD_DURATION = 5;

    private CalendarAdapter calendarAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initWidgets();
        periodDays = new ArrayList<>();

        // Show a DatePickerDialog to get the period start date
        showDatePickerDialog();
    }

    private void initWidgets() {
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        monthYearText = findViewById(R.id.monthYearTV);
    }

    private void showDatePickerDialog() {
        // Inflate the custom layout
        View dialogView = getLayoutInflater().inflate(R.layout.custom_date_picker_dialog, null);

        // Find views in the custom layout
        DatePicker datePicker = dialogView.findViewById(R.id.datePicker);

        // Set up the DatePickerDialog with the custom layout
        datePicker.init(
                LocalDate.now().getYear(),
                LocalDate.now().getMonthValue() - 1,
                LocalDate.now().getDayOfMonth(),
                (view, year, monthOfYear, dayOfMonth) -> {
                    selectedDate = LocalDate.of(year, monthOfYear + 1, dayOfMonth);
                    periodStartDate = selectedDate;
                    periodEndDate = periodStartDate.plusDays(PERIOD_DURATION - 1);

                    setMonthView();
                }
        );

        // Set the title
        new AlertDialog.Builder(this)
                .setTitle("Select Period Start Date")
                .setView(dialogView)
                .setPositiveButton("OK", (dialog, which) -> setMonthView())
                .show();
    }

    private void setMonthView() {
        monthYearText.setText(monthYearFromDate(selectedDate));
        ArrayList<String> daysInMonth = daysInMonthArray(selectedDate);

        calendarAdapter = new CalendarAdapter(daysInMonth, selectedDate, periodStartDate, periodEndDate, this);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }

    private ArrayList<String> daysInMonthArray(LocalDate date) {
        ArrayList<String> daysInMonthArray = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);
        int daysInMonth = yearMonth.lengthOfMonth();
        LocalDate firstOfMonth = date.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();

        for (int i = 1; i <= 42; i++) {
            if (i <= dayOfWeek || i > daysInMonth + dayOfWeek) {
                daysInMonthArray.add("");
            } else {
                daysInMonthArray.add(String.valueOf(i - dayOfWeek));
            }
        }
        return daysInMonthArray;
    }

    private String monthYearFromDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        return date.format(formatter);
    }

    public void previousMonthAction(View view) {
        selectedDate = selectedDate.minusMonths(1);
        setMonthView();
    }

    public void nextMonthAction(View view) {
        selectedDate = selectedDate.plusMonths(1);
        setMonthView();
    }

    @Override
    public void onItemClick(int position, String dayText) {
        if (!dayText.isEmpty()) {
            int day = Integer.parseInt(dayText);
            LocalDate currentDate = LocalDate.of(selectedDate.getYear(), selectedDate.getMonthValue(), day);

            // Check if the selected date is not in the past or too far in the future
            if (currentDate.isAfter(LocalDate.now()) && currentDate.isBefore(LocalDate.now().plusYears(1))) {
                showConfirmationDialog(currentDate);
            } else {
                // Show an error message or handle it in an appropriate way
                Toast.makeText(this, "Invalid date selection", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showConfirmationDialog(final LocalDate selectedDate) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Period Start Date")
                .setMessage("Do you want to log this date as the start of your period?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Calculate start and end dates of the period based on selectedDate
                    periodStartDate = selectedDate;
                    periodEndDate = periodStartDate.plusDays(PERIOD_DURATION - 1);

                    // Notify the adapter that data has changed
                    calendarAdapter.updatePeriodDates(periodStartDate, periodEndDate);
                    calendarAdapter.notifyDataSetChanged();

                    String message = "Period Start Date: " + selectedDate.toString();

                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }
}

