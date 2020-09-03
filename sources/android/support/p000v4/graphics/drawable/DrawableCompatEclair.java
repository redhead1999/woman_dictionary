package android.support.p000v4.graphics.drawable;

import android.graphics.drawable.Drawable;

/* renamed from: android.support.v4.graphics.drawable.DrawableCompatEclair */
class DrawableCompatEclair {
    DrawableCompatEclair() {
    }

    public static Drawable wrapForTinting(Drawable drawable) {
        return !(drawable instanceof TintAwareDrawable) ? new DrawableWrapperEclair(drawable) : drawable;
    }
}
