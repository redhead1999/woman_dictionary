package android.support.p000v4.graphics.drawable;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.ConstantState;
import android.support.annotation.Nullable;

/* renamed from: android.support.v4.graphics.drawable.DrawableWrapperEclair */
class DrawableWrapperEclair extends DrawableWrapperDonut {

    /* renamed from: android.support.v4.graphics.drawable.DrawableWrapperEclair$DrawableWrapperStateEclair */
    private static class DrawableWrapperStateEclair extends DrawableWrapperState {
        DrawableWrapperStateEclair(@Nullable DrawableWrapperState drawableWrapperState, @Nullable Resources resources) {
            super(drawableWrapperState, resources);
        }

        public Drawable newDrawable(@Nullable Resources resources) {
            return new DrawableWrapperEclair(this, resources);
        }
    }

    DrawableWrapperEclair(Drawable drawable) {
        super(drawable);
    }

    DrawableWrapperEclair(DrawableWrapperState drawableWrapperState, Resources resources) {
        super(drawableWrapperState, resources);
    }

    /* access modifiers changed from: 0000 */
    public DrawableWrapperState mutateConstantState() {
        return new DrawableWrapperStateEclair(this.mState, null);
    }

    /* access modifiers changed from: protected */
    public Drawable newDrawableFromState(ConstantState constantState, Resources resources) {
        return constantState.newDrawable(resources);
    }
}
