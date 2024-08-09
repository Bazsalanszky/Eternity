package eu.toldi.infinityforlemmy;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatTextView;
import android.content.res.TypedArray;



public class CustomTextView extends AppCompatTextView {

    private float radius = 6f;
    private boolean roundedView = true;
    private int shape = 0;

    public CustomTextView(Context context) {
        super(context);
        init(null);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CustomTextView);

            radius = a.getDimension(R.styleable.CustomTextView_lib_setRadius, radius);
            roundedView = a.getBoolean(R.styleable.CustomTextView_lib_setRoundedView, roundedView);
            shape = a.getInt(R.styleable.CustomTextView_lib_setShape, shape);

            a.recycle();
        }

        updateBackground();
    }

    private void updateBackground() {
        GradientDrawable drawable = new GradientDrawable();

        if (shape == 0) {  // Rectangle
            drawable.setShape(GradientDrawable.RECTANGLE);
        } else if (shape == 1) {  // Oval
            drawable.setShape(GradientDrawable.OVAL);
        }

        if (roundedView) {
            drawable.setCornerRadius(radius);
        } else {
            drawable.setCornerRadius(0);
        }

        this.setBackground(drawable);
    }

    public void setBackgroundColor(int color) {
        GradientDrawable background = (GradientDrawable) this.getBackground();
        background.setColor(color);
        this.setBackground(background);
    }

    public void setBorderColor(int borderColor, int borderWidthDp) {
        GradientDrawable background = (GradientDrawable) this.getBackground();
        background.setStroke(dpToPx(borderWidthDp), borderColor);
        this.setBackground(background);
    }

    public void setBorderColor(int borderColor) {
        this.setBorderColor(borderColor, 1);
    }

    private int dpToPx(int dp) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}

