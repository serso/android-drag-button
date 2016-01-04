package org.solovyev.android.views.dragbutton;


import android.support.annotation.NonNull;

public class DragListenerWrapper implements DragListener {

    @NonNull
    private final DragListener listener;

    public DragListenerWrapper(@NonNull DragListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean isSuppressOnClickEvent() {
        return listener.isSuppressOnClickEvent();
    }

    @Override
    public boolean onDrag(@NonNull DragButton button, @NonNull DragEvent event) {
        return listener.onDrag(button, event);
    }
}
