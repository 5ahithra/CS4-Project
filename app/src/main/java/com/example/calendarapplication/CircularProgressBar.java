package com.example.calendarapplication;
import android.view.View;
import android.graphics.Paint;
import android.content.Context;
import android.util.AttributeSet;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.Color;

public class CircularProgressBar extends View {
    private int progress; // Progress value
    private Paint paint;

    public CircularProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        // Set other attributes like stroke width, colors, etc.
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int radius = Math.min(centerX, centerY) - 50; // Reduce the radius to make the circle smaller

        RectF rectF = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);

        paint.setStrokeWidth(20); // Change this according to your design
        paint.setColor(Color.GRAY); // Change the default color

        canvas.drawCircle(centerX, centerY, radius, paint);

        paint.setColor(Color.parseColor("#FFC0CB")); // Change color to pink
        paint.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawArc(rectF, -90, (360 * progress) / 100, false, paint);
    }

    // Method to update progress
    public void setProgress(int progress) {
        this.progress = progress;
        invalidate(); // Redraw the view
    }
}

