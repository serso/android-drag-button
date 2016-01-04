package org.solovyev.android.views.dragbutton;

import android.content.Context;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.MotionEvent;


public class SimpleDragListener implements DragListener {

    @NonNull
    private static final PointF axis = new PointF(0, 1);

    @NonNull
    private final DragProcessor processor;

    private final float minDistancePxs;

    public SimpleDragListener(@NonNull DragProcessor processor, @NonNull Context context) {
        this.processor = processor;
        this.minDistancePxs = context.getResources().getDimensionPixelSize(R.dimen.drag_min_distance);
    }

    @Override
    public boolean onDrag(@NonNull DragButton button, @NonNull DragEvent event) {
        boolean consumed = false;

        final MotionEvent motionEvent = event.motionEvent;
        final PointF start = event.start;

        final PointF end = new PointF(motionEvent.getX(), motionEvent.getY());
        final float distance = Drag.getDistance(start, end);

        final MutableBoolean right = new MutableBoolean();
        final double angle = Math.toDegrees(Drag.getAngle(start, Drag.sum(start, axis), end, right));

        final long duration = motionEvent.getEventTime() - motionEvent.getDownTime();
        final DragDirection direction = getDirection(distance, (float) angle, right.value);
        if (direction != null && duration > 40 && duration < 2500) {
            consumed = processor.processDragEvent(direction, button, start, motionEvent);
        }

        return consumed;
    }

    @Nullable
    private DragDirection getDirection(float distance, float angle, boolean right) {
        if (distance > minDistancePxs) {
            for (DragDirection direction : DragDirection.values()) {
                if (direction == DragDirection.left && right) {
                    continue;
                }
                if (direction == DragDirection.right && !right) {
                    continue;
                }
                if (direction.angleFrom <= angle && angle <= direction.angleTo) {
                    return direction;
                }
            }
        }
        return null;
    }

    @Override
    public boolean isSuppressOnClickEvent() {
        return true;
    }

    public interface DragProcessor {

        boolean processDragEvent(@NonNull DragDirection dragDirection, @NonNull DragButton dragButton, @NonNull PointF startPoint2d, @NonNull MotionEvent motionEvent);
    }
}