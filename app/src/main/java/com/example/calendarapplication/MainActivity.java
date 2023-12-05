package com.example.calendarapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.calendarapplication.CalendarAdapter;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit; // Add this import
import java.util.ArrayList;
import android.app.AlertDialog; // Add this import
import com.example.calendarapplication.HomeActivity;

public class MainActivity extends AppCompatActivity {

    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private LocalDate selectedDate;
    private ArrayList<LocalDate> periodDays;

    private LocalDate periodStartDate;
    private LocalDate periodEndDate;
    private LocalDate userPeriodStartDate;

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

        // Assuming the button ID for navigating to the home page is "homeButton"
        findViewById(R.id.redirectToHomeButton).setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View v) {
                // Start HomeActivity when the "home" button is clicked
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                intent.putExtra("periodStartDate", userPeriodStartDate.toString());
                startActivity(intent);
            }
        });
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
                    userPeriodStartDate = selectedDate;


                    setMonthView();
                    updateCountdown(); // Update countdown when a new date is selected
                }
        );

        // Set the title
        new AlertDialog.Builder(this)
                .setTitle("Select Period Start Date")
                .setView(dialogView)
                .setPositiveButton("OK", (dialog, which) -> setMonthView())
                .show();
    }
    private void onPeriodStartDateSelected(LocalDate selectedDate) {
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        intent.putExtra("periodStartDate", selectedDate.toString()); // Added this line
        startActivity(intent);
    }

    private void setMonthView() {
        monthYearText.setText(monthYearFromDate(selectedDate));
        ArrayList<String> daysInMonth = daysInMonthArray(selectedDate);

        calendarAdapter = new CalendarAdapter(daysInMonth, selectedDate, periodStartDate, periodEndDate);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);

        updateCountdown(); // Update countdown when setting month view
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

    private void updateCountdown() {
        LocalDate currentDate = LocalDate.now();
        long daysLeft = ChronoUnit.DAYS.between(currentDate, periodEndDate);

        TextView countdownTextView = findViewById(R.id.countdownTextView);
        countdownTextView.setText(daysLeft + " days left");
    }

    public void previousMonthAction(View view) {
        selectedDate = selectedDate.minusMonths(1);
        setMonthView();
    }

    public void nextMonthAction(View view) {
        selectedDate = selectedDate.plusMonths(1);
        setMonthView();
    }

    public void redirectToHome(View view) {
        startActivity(new Intent(MainActivity.this, HomeActivity.class));
    }
}
