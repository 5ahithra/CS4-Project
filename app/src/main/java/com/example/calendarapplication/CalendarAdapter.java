package com.example.calendarapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.time.LocalDate;
import java.util.ArrayList;
//Adapt and change dates of calendaer depending on real time
public class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder> {
    private final ArrayList<String> daysOfMonth;
    private final LocalDate selectedDate;
    private LocalDate periodStartDate;
    private LocalDate periodEndDate;

    public CalendarAdapter(ArrayList<String> daysOfMonth, LocalDate selectedDate, LocalDate periodStartDate, LocalDate periodEndDate) {
        this.daysOfMonth = daysOfMonth;
        this.selectedDate = selectedDate;
        this.periodStartDate = periodStartDate;
        this.periodEndDate = periodEndDate;
    }

    public void updatePeriodDates(LocalDate startDate, LocalDate endDate) {
        this.periodStartDate = startDate;
        this.periodEndDate = endDate;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_cell, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) (parent.getHeight() * 0.166666666);
        return new CalendarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        String dayText = daysOfMonth.get(position);
        holder.dayOfMonth.setText(dayText);

        if (!dayText.isEmpty()) {
            int day = Integer.parseInt(dayText);
            LocalDate currentDate = LocalDate.of(selectedDate.getYear(), selectedDate.getMonthValue(), day);

            if (isWithinPeriod(currentDate)) {
                holder.redCircleImageView.setVisibility(View.VISIBLE);
            } else {
                holder.redCircleImageView.setVisibility(View.INVISIBLE);
            }
        } else {
            holder.redCircleImageView.setVisibility(View.INVISIBLE);
        }
    }

    private boolean isWithinPeriod(LocalDate date) {
        return periodStartDate != null && periodEndDate != null &&
                !date.isBefore(periodStartDate) && !date.isAfter(periodEndDate);
    }

    @Override
    public int getItemCount() {
        return daysOfMonth.size();
    }
}
