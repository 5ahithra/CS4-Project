package com.example.calendarapplication;

import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.time.LocalDate;
import java.util.Locale;
import android.content.Intent;
import java.time.temporal.ChronoUnit;

public class HomeActivity extends AppCompatActivity {

    private TextView countdownTextView;
    private CircularProgressBar circularProgressBar; // Change ProgressBar to CircularProgressBar
    private LocalDate userPeriodStartDate;
    private TextView additionalInfoTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        LinearLayout rootLayout = new LinearLayout(this);
        rootLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT

        ));
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.setGravity(Gravity.CENTER);


        countdownTextView = new TextView(this);
        LinearLayout.LayoutParams countdownParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        countdownParams.topMargin = 50;
        countdownTextView.setLayoutParams(countdownParams);

        additionalInfoTextView = new TextView(this);
        LinearLayout.LayoutParams additionalInfoParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        additionalInfoParams.topMargin = 60; // Set top margin as needed to bring it down
        additionalInfoTextView.setLayoutParams(additionalInfoParams);


        // Create a new instance of CircularProgressBar
        circularProgressBar = new CircularProgressBar(this, null);

        circularProgressBar.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        circularProgressBar.setProgress(0); // Set initial progress

        // Add views to the root layout
        rootLayout.addView(countdownTextView);
        rootLayout.addView(additionalInfoTextView);
        rootLayout.addView(circularProgressBar);

        setContentView(rootLayout);

        // Replace "periodStartDate" with the key used to pass the date
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("periodStartDate")) {
            String dateString = intent.getStringExtra("periodStartDate");
            userPeriodStartDate = LocalDate.parse(dateString);
        } else {
            // Set a default value if no date is provided
            userPeriodStartDate = LocalDate.now();
        }

        // Calculate and update the countdown
        updateCountdown();
    }

    private void updateCountdown() {
        // Fetch the user's period start date from the intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("periodStartDate")) {
            String dateString = intent.getStringExtra("periodStartDate");
            userPeriodStartDate = LocalDate.parse(dateString);
        } else {
            // Set a default value if no date is provided
            userPeriodStartDate = LocalDate.now();
        }

        // Calculate the days until the next period based on the user's start date
        long daysUntilNextPeriod = calculateDaysUntilNextPeriod(userPeriodStartDate);

        // Update the TextView with days until next period
        countdownTextView.setText(String.format(Locale.getDefault(), "Days until next period: %d days", daysUntilNextPeriod));

        countdownTextView.setTextSize(24);
        // Calculate and display the date 28 days from the inputted start date
        LocalDate nextCycleStartDate = userPeriodStartDate.plusDays(28);
        additionalInfoTextView.setText(String.format(Locale.getDefault(), "Next Cycle Start Date: %s", nextCycleStartDate.toString()));
        additionalInfoTextView.setTextSize(24);
        // Update the progress bar
        int progress = calculateProgress(daysUntilNextPeriod);
        circularProgressBar.setProgress(progress); // Update progress on CircularProgressBar

        // Schedule the next update
        new Handler().postDelayed(this::updateCountdown, 1000 * 60 * 60);  // Update every hour
    }

    private long calculateDaysUntilNextPeriod(LocalDate userPeriodStartDate) {
        LocalDate currentDate = LocalDate.now();
        long daysUntilNextPeriod;

        long periodLength = 28; // Assuming the period cycle is 28 days

        if (currentDate.isEqual(userPeriodStartDate) || currentDate.isAfter(userPeriodStartDate)) {
            long daysSinceStart = ChronoUnit.DAYS.between(userPeriodStartDate, currentDate);
            daysUntilNextPeriod = periodLength - (daysSinceStart % periodLength);
        } else {
            // Calculate days remaining until the start date from today
            long daysUntilStart = ChronoUnit.DAYS.between(currentDate, userPeriodStartDate);
            long missedPeriods = daysUntilStart / periodLength;
            long remainingDays = daysUntilStart % periodLength;

            if (remainingDays == 0) {
                daysUntilNextPeriod = 0;
            } else {
                daysUntilNextPeriod = periodLength - remainingDays;
                if (missedPeriods > 0) {
                    daysUntilNextPeriod += (missedPeriods * periodLength);
                }
            }
        }

        return daysUntilNextPeriod;
    }

    private int calculateProgress(long daysUntilNextCycle) {
        int maxProgress = 100; // Assuming max progress is 100
        int totalCycleDays = 28; // Update this with your cycle length

        // Calculate the remaining progress out of the total cycle days
        int remainingProgress = (int) ((daysUntilNextCycle * maxProgress) / totalCycleDays);
        return maxProgress - remainingProgress;
    }


    // Other methods for date calculations as per your requirements
}


