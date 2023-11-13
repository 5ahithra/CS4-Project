package com.example.calendarapplication;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    private TextView countdownTextView;
    private ProgressBar circularProgressBar;
    private LocalDate nextPeriodStartDate;  // Set this to the expected start date of the next period

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        countdownTextView = findViewById(R.id.countdownTextView);
        circularProgressBar = findViewById(R.id.circularProgressBar);

        // Calculate and update the countdown
        updateCountdown();
    }

    private void updateCountdown() {
        // Calculate the days until the next period
        long daysUntilNextPeriod = calculateDaysUntilNextPeriod();

        // Update the TextView
        countdownTextView.setText(String.format(Locale.getDefault(), "%d days", daysUntilNextPeriod));

        // Update the progress bar
        int progress = calculateProgress(daysUntilNextPeriod);
        circularProgressBar.setProgress(progress);

        // Schedule the next update (you can use Handler or a Timer for this)
        // For simplicity, this example uses a delayed runnable
        new Handler().postDelayed(() -> updateCountdown(), 1000 * 60 * 60);  // Update every hour
    }

    private long calculateDaysUntilNextPeriod() {
        LocalDate currentDate = LocalDate.now();
        // Replace this with your logic to calculate the expected start date of the next period
        // For example, assuming a period occurs every 30 days:
        nextPeriodStartDate = currentDate.plusDays(30);
        return ChronoUnit.DAYS.between(currentDate, nextPeriodStartDate);
    }

    private int calculateProgress(long daysUntilNextPeriod) {
        // Replace this with your logic to map days to progress (adjust as needed)
        int maxProgress = circularProgressBar.getMax();
        return (int) ((maxProgress - daysUntilNextPeriod) * 100 / maxProgress);
    }
}

