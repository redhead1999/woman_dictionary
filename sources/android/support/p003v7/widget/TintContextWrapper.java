package android.support.p003v7.widget;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.support.p003v7.app.AppCompatDelegate;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/* renamed from: android.support.v7.widget.TintContextWrapper */
public class TintContextWrapper extends ContextWrapper {
    private static final ArrayList<WeakReference<TintContextWrapper>> sCache = new ArrayList<>();
    private Resources mResources;
    private final Theme mTheme;

    public static Context wrap(@NonNull Context context) {
        if (!shouldWrap(context)) {
            return context;
        }
        int size = sCache.size();
        for (int i = 0; i < size; i++) {
            WeakReference weakReference = (WeakReference) sCache.get(i);
            TintContextWrapper tintContextWrapper = weakReference != null ? (TintContextWrapper) weakReference.get() : null;
            if (tintContextWrapper != null && tintContextWrapper.getBaseContext() == context) {
                return tintContextWrapper;
            }
        }
        TintContextWrapper tintContextWrapper2 = new TintContextWrapper(context);
        sCache.add(new WeakReference(tintContextWrapper2));
        return tintContextWrapper2;
    }

    private static boolean shouldWrap(@NonNull Context context) {
        if ((context instanceof TintContextWrapper) || (context.getResources() instanceof TintResources) || (context.getResources() instanceof VectorEnabledTintResources)) {
            return false;
        }
        if (!AppCompatDelegate.isCompatVectorFromResourcesEnabled() || VERSION.SDK_INT <= 20) {
            return true;
        }
        return false;
    }

    private TintContextWrapper(@NonNull Context context) {
        super(context);
        if (VectorEnabledTintResources.shouldBeUsed()) {
            this.mTheme = getResources().newTheme();
            this.mTheme.setTo(context.getTheme());
            return;
        }
        this.mTheme = null;
    }

    public Theme getTheme() {
        Theme theme = this.mTheme;
        return theme == null ? super.getTheme() : theme;
    }

    public void setTheme(int i) {
        Theme theme = this.mTheme;
        if (theme == null) {
            super.setTheme(i);
        } else {
            theme.applyStyle(i, true);
        }
    }

    public Resources getResources() {
        if (this.mResources == null) {
            this.mResources = this.mTheme == null ? new TintResources(this, super.getResources()) : new VectorEnabledTintResources(this, super.getResources());
        }
        return this.mResources;
    }
}
