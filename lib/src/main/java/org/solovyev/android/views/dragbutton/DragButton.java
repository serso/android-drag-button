package org.solovyev.android.views.dragbutton;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.TextView;

import static android.view.HapticFeedbackConstants.*;


public class DragButton extends Button {

    @Nullable
    private PointF startPoint = null;
    @Nullable
    private DragListener onDragListener;
    private boolean showText = true;
    @Nullable
    private CharSequence textBackup;
    private boolean vibrateOnDrag = true;

    public DragButton(Context context) {
        super(context);
        init(context);
    }

    public DragButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DragButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DragButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(@NonNull Context context) {
        // we control haptic feedback ourselves
        setHapticFeedbackEnabled(false);
    }

    public boolean isVibrateOnDrag() {
        return vibrateOnDrag;
    }

    public void setVibrateOnDrag(boolean vibrateOnDrag) {
        this.vibrateOnDrag = vibrateOnDrag;
    }

    public static boolean drawDrawables(@NonNull Canvas canvas, @NonNull TextView v) {
        int compoundPaddingLeft = v.getCompoundPaddingLeft();
        int compoundPaddingTop = v.getCompoundPaddingTop();
        int compoundPaddingRight = v.getCompoundPaddingRight();
        int compoundPaddingBottom = v.getCompoundPaddingBottom();
        int scrollX = v.getScrollX();
        int scrollY = v.getScrollY();
        int right = v.getRight();
        int left = v.getLeft();
        int bottom = v.getBottom();
        int top = v.getTop();
        Drawable[] drawables = v.getCompoundDrawables();
        int vspace = bottom - top - compoundPaddingBottom - compoundPaddingTop;
        int hspace = right - left - compoundPaddingRight - compoundPaddingLeft;
        Drawable topDr = drawables[1];
        if (topDr != null) {
            canvas.save();
            canvas.translate((float) (scrollX + compoundPaddingLeft + (hspace - topDr.getBounds().width()) / 2), (float) (scrollY + v.getPaddingTop() + vspace / 2));
            topDr.draw(canvas);
            canvas.restore();
            return true;
        }

        return false;
    }

    public void setOnDragListener(@Nullable DragListener onDragListener) {
        this.onDragListener = onDragListener;
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        boolean consumed = false;

        // in order to avoid possible NPEs
        final PointF localStartPoint = startPoint;
        final DragListener localOnDragListener = onDragListener;

        if (localOnDragListener != null) {
            // only if onDrag() listener specified

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // start tracking: set start point
                    startPoint = new PointF(event.getX(), event.getY());
                    break;

                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    // stop tracking

                    startPoint = null;
                    if (localStartPoint != null) {
                        consumed = localOnDragListener.onDrag(DragButton.this, new DragEvent(localStartPoint, event));
                        if (consumed && localOnDragListener.isSuppressOnClickEvent()) {
                            if (isVibrateOnDrag()) {
                                performHapticFeedback(KEYBOARD_TAP, FLAG_IGNORE_GLOBAL_SETTING | FLAG_IGNORE_VIEW_SETTING);
                            }
                            final MotionEvent newEvent = MotionEvent.obtain(event);
                            newEvent.setAction(MotionEvent.ACTION_CANCEL);
                            super.onTouchEvent(newEvent);
                            newEvent.recycle();
                            return true;
                        }
                    }
                    break;
            }
        }

        return super.onTouchEvent(event) || consumed;
    }

    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent event) {
        return super.dispatchTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        CharSequence text = getText();
        if (!TextUtils.isEmpty(text)) {
            super.onDraw(canvas);
        } else {
            if (!drawDrawables(canvas, this)) {
                super.onDraw(canvas);
            }
        }
    }

    public boolean isShowText() {
        return showText;
    }

    public void setShowText(boolean showText) {
        if (this.showText != showText) {
            if (showText) {
                setText(textBackup);
                textBackup = null;
            } else {
                textBackup = this.getText();
                setText(null);
            }
            this.showText = showText;
        }
    }
}
