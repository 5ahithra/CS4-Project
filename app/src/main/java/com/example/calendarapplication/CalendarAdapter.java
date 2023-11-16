package com.example.calendarapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.time.LocalDate;
import java.util.ArrayList;
import com.example.calendarapplication.R;


import android.widget.ImageView;

import com.example.calendarapplication.R;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder> {
    private final ArrayList<String> daysOfMonth;
    private final LocalDate selectedDate;

    private LocalDate periodStartDate;
    private LocalDate periodEndDate;


    public interface OnItemListener {
        void onItemClick(int position, String dayText);
    }

    // Add a listener field
    private OnItemListener onItemListener;

    public CalendarAdapter(ArrayList<String> daysOfMonth, LocalDate selectedDate, LocalDate periodStartDate, LocalDate periodEndDate, OnItemListener onItemListener) {
        this.daysOfMonth = daysOfMonth;
        this.selectedDate = selectedDate;
        this.periodStartDate = periodStartDate;
        this.periodEndDate = periodEndDate;
        this.onItemListener = onItemListener; // Initialize the listener
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_cell, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) (parent.getHeight() * 0.166666666);

        // Passing the listener to the ViewHolder
        return new CalendarViewHolder(view, onItemListener);
    }

    public void updatePeriodDates(LocalDate startDate, LocalDate endDate) {
        this.periodStartDate = startDate;
        this.periodEndDate = endDate;
        notifyDataSetChanged();
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

