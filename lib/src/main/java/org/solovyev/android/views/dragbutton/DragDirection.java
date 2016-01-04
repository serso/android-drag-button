package org.solovyev.android.views.dragbutton;

public enum DragDirection {

    up(180f - 45f, 180f - 0f),
    down(0f, 45f),
    left(90f - 45f, 90f + 45f),
    right(90f - 45f, 90f + 45f);

    final float angleFrom;
    final float angleTo;

    DragDirection(float angleFrom, float angleTo) {
        this.angleFrom = angleFrom;
        this.angleTo = angleTo;
    }
}
