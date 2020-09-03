package android.support.p000v4.graphics.drawable;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/* renamed from: android.support.v4.graphics.drawable.DrawableWrapperKitKat */
class DrawableWrapperKitKat extends DrawableWrapperHoneycomb {

    /* renamed from: android.support.v4.graphics.drawable.DrawableWrapperKitKat$DrawableWrapperStateKitKat */
    private static class DrawableWrapperStateKitKat extends DrawableWrapperState {
        DrawableWrapperStateKitKat(@Nullable DrawableWrapperState drawableWrapperState, @Nullable Resources resources) {
            super(drawableWrapperState, resources);
        }

        public Drawable newDrawable(@Nullable Resources resources) {
            return new DrawableWrapperKitKat(this, resources);
        }
    }

    DrawableWrapperKitKat(Drawable drawable) {
        super(drawable);
    }

    DrawableWrapperKitKat(DrawableWrapperState drawableWrapperState, Resources resources) {
        super(drawableWrapperState, resources);
    }

    public void setAutoMirrored(boolean z) {
        this.mDrawable.setAutoMirrored(z);
    }

    public boolean isAutoMirrored() {
        return this.mDrawable.isAutoMirrored();
    }

    /* access modifiers changed from: 0000 */
    @NonNull
    public DrawableWrapperState mutateConstantState() {
        return new DrawableWrapperStateKitKat(this.mState, null);
    }
}
