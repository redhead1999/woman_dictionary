package android.support.design.widget;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.Nullable;
import android.support.design.C0007R;
import android.view.ViewTreeObserver.OnPreDrawListener;

abstract class FloatingActionButtonImpl {
    static final int[] EMPTY_STATE_SET = new int[0];
    static final int[] FOCUSED_ENABLED_STATE_SET = {16842908, 16842910};
    static final int[] PRESSED_ENABLED_STATE_SET = {16842919, 16842910};
    static final int SHOW_HIDE_ANIM_DURATION = 200;
    CircularBorderDrawable mBorderDrawable;
    Drawable mContentBackground;
    float mElevation;
    private OnPreDrawListener mPreDrawListener;
    float mPressedTranslationZ;
    Drawable mRippleDrawable;
    final ShadowViewDelegate mShadowViewDelegate;
    Drawable mShapeDrawable;
    private final Rect mTmpRect = new Rect();
    final VisibilityAwareImageButton mView;

    interface InternalVisibilityChangedListener {
        void onHidden();

        void onShown();
    }

    /* access modifiers changed from: 0000 */
    public abstract float getElevation();

    /* access modifiers changed from: 0000 */
    public abstract void getPadding(Rect rect);

    /* access modifiers changed from: 0000 */
    public abstract void hide(@Nullable InternalVisibilityChangedListener internalVisibilityChangedListener, boolean z);

    /* access modifiers changed from: 0000 */
    public abstract void jumpDrawableToCurrentState();

    /* access modifiers changed from: 0000 */
    public abstract void onCompatShadowChanged();

    /* access modifiers changed from: 0000 */
    public abstract void onDrawableStateChanged(int[] iArr);

    /* access modifiers changed from: 0000 */
    public abstract void onElevationChanged(float f);

    /* access modifiers changed from: 0000 */
    public void onPaddingUpdated(Rect rect) {
    }

    /* access modifiers changed from: 0000 */
    public void onPreDraw() {
    }

    /* access modifiers changed from: 0000 */
    public abstract void onTranslationZChanged(float f);

    /* access modifiers changed from: 0000 */
    public boolean requirePreDrawListener() {
        return false;
    }

    /* access modifiers changed from: 0000 */
    public abstract void setBackgroundDrawable(ColorStateList colorStateList, Mode mode, int i, int i2);

    /* access modifiers changed from: 0000 */
    public abstract void setBackgroundTintList(ColorStateList colorStateList);

    /* access modifiers changed from: 0000 */
    public abstract void setBackgroundTintMode(Mode mode);

    /* access modifiers changed from: 0000 */
    public abstract void setRippleColor(int i);

    /* access modifiers changed from: 0000 */
    public abstract void show(@Nullable InternalVisibilityChangedListener internalVisibilityChangedListener, boolean z);

    FloatingActionButtonImpl(VisibilityAwareImageButton visibilityAwareImageButton, ShadowViewDelegate shadowViewDelegate) {
        this.mView = visibilityAwareImageButton;
        this.mShadowViewDelegate = shadowViewDelegate;
    }

    /* access modifiers changed from: 0000 */
    public final void setElevation(float f) {
        if (this.mElevation != f) {
            this.mElevation = f;
            onElevationChanged(f);
        }
    }

    /* access modifiers changed from: 0000 */
    public final void setPressedTranslationZ(float f) {
        if (this.mPressedTranslationZ != f) {
            this.mPressedTranslationZ = f;
            onTranslationZChanged(f);
        }
    }

    /* access modifiers changed from: 0000 */
    public final Drawable getContentBackground() {
        return this.mContentBackground;
    }

    /* access modifiers changed from: 0000 */
    public final void updatePadding() {
        Rect rect = this.mTmpRect;
        getPadding(rect);
        onPaddingUpdated(rect);
        this.mShadowViewDelegate.setShadowPadding(rect.left, rect.top, rect.right, rect.bottom);
    }

    /* access modifiers changed from: 0000 */
    public void onAttachedToWindow() {
        if (requirePreDrawListener()) {
            ensurePreDrawListener();
            this.mView.getViewTreeObserver().addOnPreDrawListener(this.mPreDrawListener);
        }
    }

    /* access modifiers changed from: 0000 */
    public void onDetachedFromWindow() {
        if (this.mPreDrawListener != null) {
            this.mView.getViewTreeObserver().removeOnPreDrawListener(this.mPreDrawListener);
            this.mPreDrawListener = null;
        }
    }

    /* access modifiers changed from: 0000 */
    public CircularBorderDrawable createBorderDrawable(int i, ColorStateList colorStateList) {
        Resources resources = this.mView.getResources();
        CircularBorderDrawable newCircularDrawable = newCircularDrawable();
        newCircularDrawable.setGradientColors(resources.getColor(C0007R.color.design_fab_stroke_top_outer_color), resources.getColor(C0007R.color.design_fab_stroke_top_inner_color), resources.getColor(C0007R.color.design_fab_stroke_end_inner_color), resources.getColor(C0007R.color.design_fab_stroke_end_outer_color));
        newCircularDrawable.setBorderWidth((float) i);
        newCircularDrawable.setBorderTint(colorStateList);
        return newCircularDrawable;
    }

    /* access modifiers changed from: 0000 */
    public CircularBorderDrawable newCircularDrawable() {
        return new CircularBorderDrawable();
    }

    private void ensurePreDrawListener() {
        if (this.mPreDrawListener == null) {
            this.mPreDrawListener = new OnPreDrawListener() {
                public boolean onPreDraw() {
                    FloatingActionButtonImpl.this.onPreDraw();
                    return true;
                }
            };
        }
    }

    /* access modifiers changed from: 0000 */
    public GradientDrawable createShapeDrawable() {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(1);
        gradientDrawable.setColor(-1);
        return gradientDrawable;
    }
}
