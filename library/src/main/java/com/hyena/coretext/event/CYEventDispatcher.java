package com.hyena.coretext.event;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangzc on 16/4/9.
 */
public class CYEventDispatcher {

    private List<CYLayoutEventListener> mLayoutListeners;

    public CYEventDispatcher() {
    }

    public void addLayoutEventListener(CYLayoutEventListener listener) {
        if (mLayoutListeners == null)
            mLayoutListeners = new ArrayList<CYLayoutEventListener>();
        if (!mLayoutListeners.contains(listener)) {
            mLayoutListeners.add(listener);
        }
    }

    public void removeLayoutEventListener(CYLayoutEventListener listener) {
        if (mLayoutListeners == null)
            return;
        mLayoutListeners.remove(listener);
    }

    public void requestLayout() {
        requestLayout(false);
    }

    public void requestLayout(boolean force) {
        if (mLayoutListeners == null || mLayoutListeners.isEmpty())
            return;

        for (int i = 0; i < mLayoutListeners.size(); i++) {
            CYLayoutEventListener listener = mLayoutListeners.get(i);
            listener.doLayout(force);
        }
    }

    public void postInvalidate() {
        if (mLayoutListeners == null || mLayoutListeners.isEmpty())
            return;
        for (int i = 0; i < mLayoutListeners.size(); i++) {
            CYLayoutEventListener listener = mLayoutListeners.get(i);
            listener.onInvalidate();
        }
    }
}
