package android.support.p000v4.widget;

import android.os.Build.VERSION;
import android.view.View;
import android.view.View.OnTouchListener;

/* renamed from: android.support.v4.widget.ListPopupWindowCompat */
public final class ListPopupWindowCompat {
    static final ListPopupWindowImpl IMPL;

    /* renamed from: android.support.v4.widget.ListPopupWindowCompat$BaseListPopupWindowImpl */
    static class BaseListPopupWindowImpl implements ListPopupWindowImpl {
        public OnTouchListener createDragToOpenListener(Object obj, View view) {
            return null;
        }

        BaseListPopupWindowImpl() {
        }
    }

    /* renamed from: android.support.v4.widget.ListPopupWindowCompat$KitKatListPopupWindowImpl */
    static class KitKatListPopupWindowImpl extends BaseListPopupWindowImpl {
        KitKatListPopupWindowImpl() {
        }

        public OnTouchListener createDragToOpenListener(Object obj, View view) {
            return ListPopupWindowCompatKitKat.createDragToOpenListener(obj, view);
        }
    }

    /* renamed from: android.support.v4.widget.ListPopupWindowCompat$ListPopupWindowImpl */
    interface ListPopupWindowImpl {
        OnTouchListener createDragToOpenListener(Object obj, View view);
    }

    static {
        if (VERSION.SDK_INT >= 19) {
            IMPL = new KitKatListPopupWindowImpl();
        } else {
            IMPL = new BaseListPopupWindowImpl();
        }
    }

    private ListPopupWindowCompat() {
    }

    public static OnTouchListener createDragToOpenListener(Object obj, View view) {
        return IMPL.createDragToOpenListener(obj, view);
    }
}
