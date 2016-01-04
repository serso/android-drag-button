package org.solovyev.android.views.dragbutton;

import android.support.annotation.NonNull;

import java.util.EventListener;


public interface DragListener extends EventListener {

    /**
     * @return 'true': if drag event has taken place (i.e. onDrag() method returned true) then click action will be suppresed
     */
    boolean isSuppressOnClickEvent();

    /**
     * @param button drag button object for which onDrag listener was set
     * @param event      drag event
     * @return 'true' if drag event occurred, 'false' otherwise
     */
    boolean onDrag(@NonNull DragButton button, @NonNull DragEvent event);

}
