package com.example.calendarapplication;
//Main calendar format
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import android.app.AlertDialog;


public class MainActivity extends AppCompatActivity {

    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private LocalDate selectedDate;
    private ArrayList<LocalDate> periodDays;


    private LocalDate periodEndDate;
    private LocalDate userPeriodStartDate;

    private static final int PERIOD_DURATION = 5;

    private CalendarAdapter calendarAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("MainActivity", "onCreate: Activity created");
        initWidgets();
        periodDays = new ArrayList<>();

        if (savedInstanceState != null) {
            userPeriodStartDate = (LocalDate) savedInstanceState.getSerializable("userPeriodStartDate");
        }

        if (userPeriodStartDate == null) {
            Log.d("MainActivity", "userPeriodStartDate is null. Showing DatePickerDialog.");
            showDatePickerDialog();
        } else {
            Log.d("MainActivity", "userPeriodStartDate: " + userPeriodStartDate);
            redirectToHomeIfValid();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (userPeriodStartDate != null) {
            outState.putSerializable("userPeriodStartDate", userPeriodStartDate);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            userPeriodStartDate = (LocalDate) savedInstanceState.getSerializable("userPeriodStartDate");
        }
    }



    private void redirectToHomeIfValid() {
        if (userPeriodStartDate != null) {
            Log.d("MainActivity", "Redirecting to HomeActivity with userPeriodStartDate.");
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            intent.putExtra("periodStartDate", userPeriodStartDate.toString());
            startActivity(intent);
            // Remove finish() here to prevent MainActivity from finishing
        } else {
            Log.d("MainActivity", "userPeriodStartDate is null. Cannot redirect.");
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        handleIntentData(getIntent());
    }

    private void handleIntentData(Intent intent) {
        if (intent != null && intent.hasExtra("periodStartDate")) {
            String dateString = intent.getStringExtra("periodStartDate");
            if (dateString != null && !dateString.isEmpty()) {
                userPeriodStartDate = LocalDate.parse(dateString);
                setMonthView();
                return;
            }
        }

        Log.d("MainActivity", "No valid periodStartDate found in the intent.");
    }




    private void initWidgets() {
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        monthYearText = findViewById(R.id.monthYearTV);

        // Find the redirectToHomeButton and set its click listener
        findViewById(R.id.redirectToHomeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userPeriodStartDate != null) {
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    intent.putExtra("periodStartDate", userPeriodStartDate.toString());
                    startActivity(intent);
                } else {
                    // Handle case when userPeriodStartDate is null
                    Toast.makeText(MainActivity.this, "Please select a start date", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void showDatePickerDialog() {
        Log.d("MainActivity", "Showing DatePickerDialog.");
        View dialogView = getLayoutInflater().inflate(R.layout.custom_date_picker_dialog, null);
        DatePicker datePicker = dialogView.findViewById(R.id.datePicker);

        datePicker.init(
                LocalDate.now().getYear(),
                LocalDate.now().getMonthValue() - 1,
                LocalDate.now().getDayOfMonth(),
                (view, year, monthOfYear, dayOfMonth) -> {
                    selectedDate = LocalDate.of(year, monthOfYear + 1, dayOfMonth);
                    Log.d("MainActivity", "Selected Date: " + selectedDate);
                    userPeriodStartDate = selectedDate;

                    periodEndDate = selectedDate.plusDays(PERIOD_DURATION);

                    // Check if the selected date is too far in the past
                    LocalDate maxAllowedDate = LocalDate.now().minusDays(PERIOD_DURATION);
                    if (userPeriodStartDate.isBefore(maxAllowedDate)) {
                        Toast.makeText(MainActivity.this, "Cycle date has already been passed, please select another date", Toast.LENGTH_SHORT).show();
                    } else {
                        setMonthView();
                        updateCountdown();
                    }
                }
        );

        new AlertDialog.Builder(this)
                .setTitle("Select Period Start Date")
                .setView(dialogView)
                .setPositiveButton("OK", (dialog, which) -> setMonthView())
                .show();
    }




    private void setMonthView() {
        monthYearText.setText(monthYearFromDate(selectedDate));
        ArrayList<String> daysInMonth = daysInMonthArray(selectedDate);

        calendarAdapter = new CalendarAdapter(daysInMonth, selectedDate, userPeriodStartDate, periodEndDate);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);

        calendarRecyclerView.setAdapter(calendarAdapter);

        updateCountdown();
        Log.d("MainActivity", "Selected date in setMonthView: " + selectedDate);
        Log.d("MainActivity", "Period start date in setMonthView: " + userPeriodStartDate);
        Log.d("MainActivity", "Period end date in setMonthView: " + periodEndDate);
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
        if (date != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
            return date.format(formatter);
        } else {

            return "";
        }
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

    public void changePeriodStartDate(View view) {
        showDatePickerDialog();
    }






}
