package android.support.p000v4.view.animation;

import android.graphics.Path;
import android.os.Build.VERSION;
import android.view.animation.Interpolator;

/* renamed from: android.support.v4.view.animation.PathInterpolatorCompat */
public final class PathInterpolatorCompat {
    private PathInterpolatorCompat() {
    }

    public static Interpolator create(Path path) {
        if (VERSION.SDK_INT >= 21) {
            return PathInterpolatorCompatApi21.create(path);
        }
        return PathInterpolatorCompatBase.create(path);
    }

    public static Interpolator create(float f, float f2) {
        if (VERSION.SDK_INT >= 21) {
            return PathInterpolatorCompatApi21.create(f, f2);
        }
        return PathInterpolatorCompatBase.create(f, f2);
    }

    public static Interpolator create(float f, float f2, float f3, float f4) {
        if (VERSION.SDK_INT >= 21) {
            return PathInterpolatorCompatApi21.create(f, f2, f3, f4);
        }
        return PathInterpolatorCompatBase.create(f, f2, f3, f4);
    }
}
