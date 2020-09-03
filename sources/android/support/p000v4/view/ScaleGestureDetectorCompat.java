package android.support.p000v4.view;

import android.os.Build.VERSION;

/* renamed from: android.support.v4.view.ScaleGestureDetectorCompat */
public final class ScaleGestureDetectorCompat {
    static final ScaleGestureDetectorImpl IMPL;

    /* renamed from: android.support.v4.view.ScaleGestureDetectorCompat$BaseScaleGestureDetectorImpl */
    private static class BaseScaleGestureDetectorImpl implements ScaleGestureDetectorImpl {
        public boolean isQuickScaleEnabled(Object obj) {
            return false;
        }

        public void setQuickScaleEnabled(Object obj, boolean z) {
        }

        private BaseScaleGestureDetectorImpl() {
        }
    }

    /* renamed from: android.support.v4.view.ScaleGestureDetectorCompat$ScaleGestureDetectorCompatKitKatImpl */
    private static class ScaleGestureDetectorCompatKitKatImpl implements ScaleGestureDetectorImpl {
        private ScaleGestureDetectorCompatKitKatImpl() {
        }

        public void setQuickScaleEnabled(Object obj, boolean z) {
            ScaleGestureDetectorCompatKitKat.setQuickScaleEnabled(obj, z);
        }

        public boolean isQuickScaleEnabled(Object obj) {
            return ScaleGestureDetectorCompatKitKat.isQuickScaleEnabled(obj);
        }
    }

    /* renamed from: android.support.v4.view.ScaleGestureDetectorCompat$ScaleGestureDetectorImpl */
    interface ScaleGestureDetectorImpl {
        boolean isQuickScaleEnabled(Object obj);

        void setQuickScaleEnabled(Object obj, boolean z);
    }

    static {
        if (VERSION.SDK_INT >= 19) {
            IMPL = new ScaleGestureDetectorCompatKitKatImpl();
        } else {
            IMPL = new BaseScaleGestureDetectorImpl();
        }
    }

    private ScaleGestureDetectorCompat() {
    }

    public static void setQuickScaleEnabled(Object obj, boolean z) {
        IMPL.setQuickScaleEnabled(obj, z);
    }

    public static boolean isQuickScaleEnabled(Object obj) {
        return IMPL.isQuickScaleEnabled(obj);
    }
}
