package eu.toldi.infinityforlemmy;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import androidx.annotation.Nullable;


public class DualBadgeDrawable extends Drawable {
    private Drawable leftBadge;
    private Drawable rightBadge;

    public DualBadgeDrawable(Drawable leftBadge, Drawable rightBadge) {
        this.leftBadge = leftBadge;
        this.rightBadge = rightBadge;
    }

    @Override
    public void draw(Canvas canvas) {
        if (leftBadge == null || rightBadge == null) {
            return;
        }

        int width = getBounds().width();
        int height = getBounds().height();

        // Draw the left badge on the left half of the canvas
        Rect leftRect = new Rect(0, 0, width / 2, height);
        leftBadge.setBounds(leftRect);
        leftBadge.draw(canvas);

        // Draw the right badge on the right half of the canvas
        Rect rightRect = new Rect(width / 2, 0, width, height);
        rightBadge.setBounds(rightRect);
        rightBadge.draw(canvas);
    }

    @Override
    public void setAlpha(int alpha) {
        if (leftBadge != null) {
            leftBadge.setAlpha(alpha);
        }
        if (rightBadge != null) {
            rightBadge.setAlpha(alpha);
        }
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        if (leftBadge != null) {
            leftBadge.setColorFilter(colorFilter);
        }
        if (rightBadge != null) {
            rightBadge.setColorFilter(colorFilter);
        }
    }

    @Override
    public int getOpacity() {
        return leftBadge != null ? leftBadge.getOpacity() : rightBadge.getOpacity();
    }

    @Override
    public int getIntrinsicWidth() {
        int leftWidth = leftBadge != null ? leftBadge.getIntrinsicWidth() : 0;
        int rightWidth = rightBadge != null ? rightBadge.getIntrinsicWidth() : 0;
        return leftWidth + rightWidth;
    }

    @Override
    public int getIntrinsicHeight() {
        int leftHeight = leftBadge != null ? leftBadge.getIntrinsicHeight() : 0;
        int rightHeight = rightBadge != null ? rightBadge.getIntrinsicHeight() : 0;
        return Math.max(leftHeight, rightHeight);
    }
}