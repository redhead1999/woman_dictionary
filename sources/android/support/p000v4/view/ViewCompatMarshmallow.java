package android.support.p000v4.view;

import android.view.View;

/* renamed from: android.support.v4.view.ViewCompatMarshmallow */
class ViewCompatMarshmallow {
    ViewCompatMarshmallow() {
    }

    public static void setScrollIndicators(View view, int i) {
        view.setScrollIndicators(i);
    }

    public static void setScrollIndicators(View view, int i, int i2) {
        view.setScrollIndicators(i, i2);
    }

    public static int getScrollIndicators(View view) {
        return view.getScrollIndicators();
    }

    static void offsetTopAndBottom(View view, int i) {
        view.offsetTopAndBottom(i);
    }

    static void offsetLeftAndRight(View view, int i) {
        view.offsetLeftAndRight(i);
    }
}
