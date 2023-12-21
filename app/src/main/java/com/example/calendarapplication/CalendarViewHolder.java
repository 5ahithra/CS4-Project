package com.example.calendarapplication;
//Display current cycle with circles
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CalendarViewHolder extends RecyclerView.ViewHolder {
    public final TextView dayOfMonth;
    public final ImageView redCircleImageView;

    public CalendarViewHolder(@NonNull View itemView) {
        super(itemView);
        dayOfMonth = itemView.findViewById(R.id.cellDayText);
        redCircleImageView = itemView.findViewById(R.id.redCircleImageView);
    }
}
