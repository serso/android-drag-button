package org.solovyev.android.views.dragbutton;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;

import java.util.*;

public class DirectionDragButton extends DragButton {

    @NonNull
    private final static Float DEFAULT_DIRECTION_TEXT_SCALE_FLOAT = 0.4f;

    @NonNull
    private final static Integer DEFAULT_DIRECTION_TEXT_ALPHA = 140;

    private final static int DEFAULT_DIRECTION_TEXT_COLOR = Color.WHITE;

    @NonNull
    private final static String DEFAULT_DIRECTION_TEXT_SCALE = "0.4;0.4;0.4;0.4";
    @NonNull
    private final Map<Direction, DirectionTextData> textDataMap = new EnumMap<>(Direction.class);
    @NonNull
    protected String directionTextScale = DEFAULT_DIRECTION_TEXT_SCALE;
    @NonNull
    protected Integer directionTextAlpha = DEFAULT_DIRECTION_TEXT_ALPHA;
    protected int directionTextColor = DEFAULT_DIRECTION_TEXT_COLOR;
    private boolean initialized = false;

    @SuppressWarnings("unused")
    public DirectionDragButton(Context context) {
        super(context);
        init(context, null);
    }

    @SuppressWarnings("unused")
    public DirectionDragButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    @SuppressWarnings("unused")
    public DirectionDragButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @SuppressWarnings("unused")
    public DirectionDragButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        if (attrs != null) {
            final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DirectionDragButton);
            directionTextColor = a.getColor(R.styleable.DirectionDragButton_directionTextColor, DEFAULT_DIRECTION_TEXT_COLOR);
            directionTextScale = Drag.defaultIfNull(a.getString(R.styleable.DirectionDragButton_directionTextScale), DEFAULT_DIRECTION_TEXT_SCALE);
            directionTextAlpha = a.getInt(R.styleable.DirectionDragButton_directionTextAlpha, DEFAULT_DIRECTION_TEXT_ALPHA);
            for (Direction direction : Direction.values()) {
                if (a.hasValue(direction.attributeId)) {
                    final String value = a.getString(direction.attributeId);
                    if (!TextUtils.isEmpty(value)) {
                        textDataMap.put(direction, new DirectionTextData(direction, value));
                    }
                }
            }
            a.recycle();
        }

        for (Map.Entry<Direction, Float> entry : getDirectionTextScales().entrySet()) {
            final DirectionTextData td = textDataMap.get(entry.getKey());
            if (td != null) {
                td.scale = entry.getValue();
            }
        }

        initialized = true;
    }

    @Override
    public void onSizeChanged(int w, int h, int oldW, int oldH) {
        measureText();
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int before, int after) {
        measureText();
    }

    protected void measureText() {
        if (!initialized) {
            return;
        }
        final int width = getWidth() - getPaddingLeft() - getPaddingRight();
        final int height = getHeight() - getPaddingTop() - getPaddingBottom();

        final Paint basePaint = getPaint();
        for (DirectionTextData textData : textDataMap.values()) {
            initDirectionTextPaint(basePaint, textData);
            textData.position = textData.direction.getTextPosition(textData.paint, basePaint, textData.text, getText(), width, height);
        }
        invalidate();
    }

    protected void initDirectionTextPaint(@NonNull Paint basePaint, @NonNull DirectionTextData textData) {
        textData.init(basePaint, directionTextColor, directionTextAlpha);
    }

    public void setDirectionTextColor(int directionTextColor) {
        this.directionTextColor = directionTextColor;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final TextPaint paint = getPaint();
        for (DirectionTextData td : textDataMap.values()) {
            if (td.show) {
                initDirectionTextPaint(paint, td);
                final String text = td.text;
                final PointF position = td.position;
                canvas.drawText(text, 0, text.length(), position.x, position.y, td.paint);
            }
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    @Nullable
    public String getTextUp() {
        return getText(Direction.up);
    }

    @SuppressWarnings("UnusedDeclaration")
    @Nullable
    public String getTextDown() {
        return getText(Direction.down);
    }

    @Nullable
    public String getText(@NonNull DragDirection direction) {
        final Direction guiDragDirection = Direction.valueOf(direction);
        return guiDragDirection == null ? null : getText(guiDragDirection);
    }

    @SuppressWarnings("UnusedDeclaration")
    public void showDirectionText(boolean show, @NonNull DragDirection direction) {
        final Direction guiDragDirection = Direction.valueOf(direction);
        final DirectionTextData td = this.textDataMap.get(guiDragDirection);
        if (td != null) {
            td.show = show;
            invalidate();
        }
    }

    @NonNull
    public DirectionDragButton setText(@Nullable String text, @NonNull Direction direction) {
        if (!TextUtils.isEmpty(text)) {
            final DirectionTextData data = new DirectionTextData(direction, text);
            initDirectionTextPaint(getPaint(), data);
            textDataMap.put(direction, data);
        } else {
            textDataMap.remove(direction);
        }
        measureText();
        return this;
    }

    @Nullable
    private String getText(@NonNull Direction direction) {
        DirectionTextData td = textDataMap.get(direction);
        if (td == null) {
            return null;
        } else {
            if (td.show) {
                return td.text;
            } else {
                return null;
            }
        }
    }

    @NonNull
    public String getDirectionTextScale() {
        return directionTextScale;
    }

    @NonNull
    private Map<Direction, Float> getDirectionTextScales() {
        final List<Float> scales = new ArrayList<>();

        final StringTokenizer t = new StringTokenizer(getDirectionTextScale(), ";");
        while (t.hasMoreTokens()) {
            final String scale = t.nextToken();
            if (TextUtils.isEmpty(scale)) {
                continue;
            }
            try {
                scales.add(Float.valueOf(scale));
            } catch (NumberFormatException e) {
                Log.w(Drag.TAG, e.getMessage(), e);
            }
        }

        final Map<Direction, Float> result = new HashMap<>();
        for (Direction direction : Direction.values()) {
            result.put(direction, DEFAULT_DIRECTION_TEXT_SCALE_FLOAT);
        }

        if (scales.size() == 1) {
            final Float scale = scales.get(0);
            for (Map.Entry<Direction, Float> entry : result.entrySet()) {
                entry.setValue(scale);
            }
        } else {
            for (int i = 0; i < scales.size(); i++) {
                for (Direction direction : Direction.values()) {
                    if (direction.getAttributePosition() == i) {
                        result.put(direction, scales.get(i));
                    }
                }
            }
        }

        return result;
    }


    public enum Direction {
        up(DragDirection.up, 0, R.styleable.DirectionDragButton_textUp) {
            @NonNull
            @Override
            public PointF getTextPosition(@NonNull Paint paint, @NonNull Paint basePaint, @NonNull CharSequence text, CharSequence baseText, int w, int h) {
                return getUpDownTextPosition(paint, basePaint, text, baseText, 1, w, h);
            }
        },
        down(DragDirection.down, 2, R.styleable.DirectionDragButton_textDown) {
            @NonNull
            @Override
            public PointF getTextPosition(@NonNull Paint paint, @NonNull Paint basePaint, @NonNull CharSequence text, CharSequence baseText, int w, int h) {
                return getUpDownTextPosition(paint, basePaint, text, baseText, -1, w, h);
            }
        },
        left(DragDirection.left, 3, R.styleable.DirectionDragButton_textLeft) {
            @NonNull
            @Override
            public PointF getTextPosition(@NonNull Paint paint, @NonNull Paint basePaint, @NonNull CharSequence text, CharSequence baseText, int w, int h) {
                return getLeftRightTextPosition(paint, basePaint, text, baseText, w, h, true);
            }
        },

        right(DragDirection.right, 1, R.styleable.DirectionDragButton_textRight) {
            @NonNull
            @Override
            public PointF getTextPosition(@NonNull Paint paint, @NonNull Paint basePaint, @NonNull CharSequence text, CharSequence baseText, int w, int h) {
                return getLeftRightTextPosition(paint, basePaint, text, baseText, w, h, false);
            }
        };

        @NonNull
        private final DragDirection dragDirection;

        private final int attributePosition;
        private final int attributeId;

        Direction(@NonNull DragDirection dragDirection, int attributePosition, int attributeId) {
            this.dragDirection = dragDirection;
            this.attributePosition = attributePosition;
            this.attributeId = attributeId;
        }

        @NonNull
        private static PointF getLeftRightTextPosition(@NonNull Paint paint, @NonNull Paint basePaint, CharSequence text, @NonNull CharSequence baseText, int w, int h, boolean left) {
            final PointF result = new PointF();

            if (left) {
                result.x = paint.measureText(" ");
            } else {
                float width = paint.measureText(text.toString() + " ");
                result.x = w - width;
            }

            float selfHeight = paint.ascent() + paint.descent();

            basePaint.measureText(getNotEmpty(baseText, "|"));

            result.y = h / 2 - selfHeight / 2;

            return result;
        }

        @NonNull
        private static String getNotEmpty(@Nullable CharSequence s, @NonNull String def) {
            return TextUtils.isEmpty(s) ? def : s.toString();
        }

        @NonNull
        private static PointF getUpDownTextPosition(@NonNull Paint paint, @NonNull Paint basePaint, @NonNull CharSequence text, CharSequence baseText, float direction, int w, int h) {
            final PointF result = new PointF();

            float width = paint.measureText(text.toString() + " ");
            result.x = w - width;

            float selfHeight = paint.ascent() + paint.descent();

            basePaint.measureText(getNotEmpty(baseText, "|"));

            if (direction < 0) {
                result.y = h / 2 + h / 3 - selfHeight / 2;
            } else {
                result.y = h / 2 - h / 3 - selfHeight / 2;
            }

            return result;
        }

        @Nullable
        public static Direction valueOf(@NonNull DragDirection dragDirection) {
            for (Direction direction : values()) {
                if (direction.dragDirection == dragDirection) {
                    return direction;
                }
            }
            return null;
        }

        public int getAttributePosition() {
            return attributePosition;
        }

        @NonNull
        public abstract PointF getTextPosition(@NonNull Paint paint, @NonNull Paint basePaint, @NonNull CharSequence text, CharSequence baseText, int w, int h);
    }

    @SuppressWarnings("unused")
    protected static class DirectionTextData {

        @NonNull
        private final Direction direction;
        @NonNull
        private final TextPaint paint = new TextPaint();
        @NonNull
        private String text;
        @NonNull
        private PointF position;
        @NonNull
        private Float scale = DEFAULT_DIRECTION_TEXT_SCALE_FLOAT;

        private boolean show = true;

        private DirectionTextData(@NonNull Direction direction, @NonNull String text) {
            this.direction = direction;
            this.text = text;
        }

        protected void init(@NonNull Paint basePaint,
                            int color,
                            int alpha) {
            paint.set(basePaint);
            paint.setColor(color);
            paint.setAlpha(alpha);
            paint.setTextSize(basePaint.getTextSize() * scale);
        }

        @NonNull
        public Direction getDirection() {
            return direction;
        }

        @NonNull
        public String getText() {
            return text;
        }

        @NonNull
        public PointF getPosition() {
            return position;
        }

        @NonNull
        public TextPaint getPaint() {
            return paint;
        }

        @NonNull
        public Float getScale() {
            return scale;
        }

        public boolean isShow() {
            return show;
        }
    }

}
