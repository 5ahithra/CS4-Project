package com.example.calendarapplication;
import android.view.View;
import android.graphics.Paint;
import android.content.Context;
import android.util.AttributeSet;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.Color;
import android.view.ViewGroup;
//Progress Chart Circle Class(no xml)
public class CircularProgressBar extends View {
    private int progress; // Progress value
    private Paint paint;
    private boolean isVisible = true;



    public CircularProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        // Set the background to transparent
        setBackgroundColor(Color.TRANSPARENT);
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);



        int desiredSizeInPixels = 200;


        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(desiredSizeInPixels, desiredSizeInPixels);
        setLayoutParams(params);


    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT);
        super.onDraw(canvas);
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int radius = Math.min(centerX, centerY) - 50; // Reduce the radius to make the circle smaller

        RectF rectF = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);

        paint.setStrokeWidth(30); // size
        paint.setColor(Color.GRAY); // outer color

        canvas.drawCircle(centerX, centerY, radius, paint);

        paint.setColor(Color.parseColor("#8BE18F")); // inner color
        paint.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawArc(rectF, -90, (360 * progress) / 100, false, paint);
    }

    // Method to update progress
    public void setProgress(int progress) {
        this.progress = progress;
        invalidate();

    }

    // Method to toggle visibility
    public void toggleVisibility() {
        isVisible = !isVisible;
        setVisibility(isVisible ? VISIBLE : INVISIBLE);
        invalidate();
    }

}

