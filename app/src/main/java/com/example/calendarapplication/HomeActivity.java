package com.example.calendarapplication;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.time.LocalDate;
import java.util.Locale;
import android.content.Intent;
import java.time.temporal.ChronoUnit;
//Home acitivity including chart/dates
public class HomeActivity extends AppCompatActivity {
    private TextView countdownTextView;
    private CircularProgressBar circularProgressBar;
    private LocalDate userPeriodStartDate;
    private TextView additionalInfoTextView;
    private boolean isProgressBarVisible = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setBackgroundDrawableResource(R.drawable.grassback);
        Log.d("HomeActivity", "onCreate: Activity created");

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
        additionalInfoParams.topMargin = 60;
        additionalInfoTextView.setLayoutParams(additionalInfoParams);

        circularProgressBar = new CircularProgressBar(this, null);
        circularProgressBar.setLayoutParams(new LinearLayout.LayoutParams(
                1000,
                1000
        ));


        circularProgressBar.setProgress(0); // Set initial progress

        rootLayout.addView(countdownTextView);
        rootLayout.addView(additionalInfoTextView);
        rootLayout.addView(circularProgressBar);
        setContentView(rootLayout);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("periodStartDate")) {
            String dateString = intent.getStringExtra("periodStartDate");
            userPeriodStartDate = LocalDate.parse(dateString);
            Log.d("HomeActivity", "User's period start date: " + userPeriodStartDate);
        } else {

            userPeriodStartDate = LocalDate.now();
            Log.d("HomeActivity", "Default period start date: " + userPeriodStartDate);
        }
        // Calculate and update the countdown
        updateCountdown();

        Button toggleProgressBarButton = new Button(this);
        toggleProgressBarButton.setText("Toggle Progress Bar");
        toggleProgressBarButton.setBackgroundColor(Color.parseColor("#f3e3c8"));
        toggleProgressBarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleProgressBarVisibility();
            }
        });


        rootLayout.addView(toggleProgressBarButton);

        Button goToMainButton = new Button(this);
        goToMainButton.setText("Go Back");
        goToMainButton.setBackgroundColor(Color.parseColor("#f3e3c8"));
        goToMainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        rootLayout.addView(goToMainButton);



    }

    private void toggleProgressBarVisibility() {
        circularProgressBar.toggleVisibility();
        isProgressBarVisible = !isProgressBarVisible;
    }

    private void updateCountdown() {
        long daysUntilNextPeriod = calculateDaysUntilNextPeriod(userPeriodStartDate);

        countdownTextView.setText(String.format(Locale.getDefault(), "Days until next period: %d days", daysUntilNextPeriod));
        countdownTextView.setTextSize(24);
        countdownTextView.setTypeface(null, Typeface.BOLD);

        LocalDate nextCycleStartDate = userPeriodStartDate.plusDays(28);
        additionalInfoTextView.setText(String.format(Locale.getDefault(), "Next Cycle Start Date: %s", nextCycleStartDate.toString()));
        additionalInfoTextView.setTextSize(24);
        additionalInfoTextView.setTypeface(null, Typeface.BOLD);

        int progress = calculateProgress(daysUntilNextPeriod);
        circularProgressBar.setProgress(progress); // Update progress on CircularProgressBar

        new Handler().postDelayed(this::updateCountdown, 1000 * 60 * 60);
        Log.d("HomeActivity", "Days until next period: " + daysUntilNextPeriod);
    }


    private long calculateDaysUntilNextPeriod(LocalDate userPeriodStartDate) {
        LocalDate currentDate = LocalDate.now();
        long daysUntilNextPeriod;
        long periodLength = 28;

        if (currentDate.isEqual(userPeriodStartDate) || currentDate.isAfter(userPeriodStartDate)) {
            long daysSinceStart = ChronoUnit.DAYS.between(userPeriodStartDate, currentDate);
            daysUntilNextPeriod = periodLength - (daysSinceStart % periodLength);
        } else {
            long daysUntilStart = ChronoUnit.DAYS.between(currentDate, userPeriodStartDate);
            daysUntilNextPeriod = daysUntilStart + periodLength; // Add the periodLength for future dates
        }

        return daysUntilNextPeriod;
    }


    private int calculateProgress(long daysUntilNextCycle) {
        int maxProgress = 100;
        int totalCycleDays = 28;

        int remainingProgress = (int) ((daysUntilNextCycle * maxProgress) / totalCycleDays);
        return maxProgress - remainingProgress;
    }



}