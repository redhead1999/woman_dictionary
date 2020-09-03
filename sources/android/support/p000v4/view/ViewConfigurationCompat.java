package android.support.p000v4.view;

import android.os.Build.VERSION;
import android.view.ViewConfiguration;

/* renamed from: android.support.v4.view.ViewConfigurationCompat */
public final class ViewConfigurationCompat {
    static final ViewConfigurationVersionImpl IMPL;

    /* renamed from: android.support.v4.view.ViewConfigurationCompat$BaseViewConfigurationVersionImpl */
    static class BaseViewConfigurationVersionImpl implements ViewConfigurationVersionImpl {
        public boolean hasPermanentMenuKey(ViewConfiguration viewConfiguration) {
            return true;
        }

        BaseViewConfigurationVersionImpl() {
        }

        public int getScaledPagingTouchSlop(ViewConfiguration viewConfiguration) {
            return viewConfiguration.getScaledTouchSlop();
        }
    }

    /* renamed from: android.support.v4.view.ViewConfigurationCompat$FroyoViewConfigurationVersionImpl */
    static class FroyoViewConfigurationVersionImpl extends BaseViewConfigurationVersionImpl {
        FroyoViewConfigurationVersionImpl() {
        }

        public int getScaledPagingTouchSlop(ViewConfiguration viewConfiguration) {
            return ViewConfigurationCompatFroyo.getScaledPagingTouchSlop(viewConfiguration);
        }
    }

    /* renamed from: android.support.v4.view.ViewConfigurationCompat$HoneycombViewConfigurationVersionImpl */
    static class HoneycombViewConfigurationVersionImpl extends FroyoViewConfigurationVersionImpl {
        public boolean hasPermanentMenuKey(ViewConfiguration viewConfiguration) {
            return false;
        }

        HoneycombViewConfigurationVersionImpl() {
        }
    }

    /* renamed from: android.support.v4.view.ViewConfigurationCompat$IcsViewConfigurationVersionImpl */
    static class IcsViewConfigurationVersionImpl extends HoneycombViewConfigurationVersionImpl {
        IcsViewConfigurationVersionImpl() {
        }

        public boolean hasPermanentMenuKey(ViewConfiguration viewConfiguration) {
            return ViewConfigurationCompatICS.hasPermanentMenuKey(viewConfiguration);
        }
    }

    /* renamed from: android.support.v4.view.ViewConfigurationCompat$ViewConfigurationVersionImpl */
    interface ViewConfigurationVersionImpl {
        int getScaledPagingTouchSlop(ViewConfiguration viewConfiguration);

        boolean hasPermanentMenuKey(ViewConfiguration viewConfiguration);
    }

    static {
        if (VERSION.SDK_INT >= 14) {
            IMPL = new IcsViewConfigurationVersionImpl();
        } else if (VERSION.SDK_INT >= 11) {
            IMPL = new HoneycombViewConfigurationVersionImpl();
        } else if (VERSION.SDK_INT >= 8) {
            IMPL = new FroyoViewConfigurationVersionImpl();
        } else {
            IMPL = new BaseViewConfigurationVersionImpl();
        }
    }

    public static int getScaledPagingTouchSlop(ViewConfiguration viewConfiguration) {
        return IMPL.getScaledPagingTouchSlop(viewConfiguration);
    }

    public static boolean hasPermanentMenuKey(ViewConfiguration viewConfiguration) {
        return IMPL.hasPermanentMenuKey(viewConfiguration);
    }

    private ViewConfigurationCompat() {
    }
}
