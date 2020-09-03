package android.support.design.widget;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.Nullable;
import android.support.design.C0007R;
import android.support.p000v4.graphics.drawable.DrawableCompat;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;

class FloatingActionButtonEclairMr1 extends FloatingActionButtonImpl {
    private int mAnimationDuration;
    /* access modifiers changed from: private */
    public boolean mIsHiding;
    ShadowDrawableWrapper mShadowDrawable;
    private StateListAnimator mStateListAnimator = new StateListAnimator();

    private abstract class BaseShadowAnimation extends Animation {
        private float mShadowSizeDiff;
        private float mShadowSizeStart;

        /* access modifiers changed from: protected */
        public abstract float getTargetShadowSize();

        private BaseShadowAnimation() {
        }

        public void reset() {
            super.reset();
            this.mShadowSizeStart = FloatingActionButtonEclairMr1.this.mShadowDrawable.getShadowSize();
            this.mShadowSizeDiff = getTargetShadowSize() - this.mShadowSizeStart;
        }

        /* access modifiers changed from: protected */
        public void applyTransformation(float f, Transformation transformation) {
            FloatingActionButtonEclairMr1.this.mShadowDrawable.setShadowSize(this.mShadowSizeStart + (this.mShadowSizeDiff * f));
        }
    }

    private class ElevateToTranslationZAnimation extends BaseShadowAnimation {
        private ElevateToTranslationZAnimation() {
            super();
        }

        /* access modifiers changed from: protected */
        public float getTargetShadowSize() {
            return FloatingActionButtonEclairMr1.this.mElevation + FloatingActionButtonEclairMr1.this.mPressedTranslationZ;
        }
    }

    private class ResetElevationAnimation extends BaseShadowAnimation {
        private ResetElevationAnimation() {
            super();
        }

        /* access modifiers changed from: protected */
        public float getTargetShadowSize() {
            return FloatingActionButtonEclairMr1.this.mElevation;
        }
    }

    /* access modifiers changed from: 0000 */
    public void onCompatShadowChanged() {
    }

    FloatingActionButtonEclairMr1(VisibilityAwareImageButton visibilityAwareImageButton, ShadowViewDelegate shadowViewDelegate) {
        super(visibilityAwareImageButton, shadowViewDelegate);
        this.mAnimationDuration = visibilityAwareImageButton.getResources().getInteger(17694720);
        this.mStateListAnimator.setTarget(visibilityAwareImageButton);
        this.mStateListAnimator.addState(PRESSED_ENABLED_STATE_SET, setupAnimation(new ElevateToTranslationZAnimation()));
        this.mStateListAnimator.addState(FOCUSED_ENABLED_STATE_SET, setupAnimation(new ElevateToTranslationZAnimation()));
        this.mStateListAnimator.addState(EMPTY_STATE_SET, setupAnimation(new ResetElevationAnimation()));
    }

    /* access modifiers changed from: 0000 */
    public void setBackgroundDrawable(ColorStateList colorStateList, Mode mode, int i, int i2) {
        Drawable[] drawableArr;
        this.mShapeDrawable = DrawableCompat.wrap(createShapeDrawable());
        DrawableCompat.setTintList(this.mShapeDrawable, colorStateList);
        if (mode != null) {
            DrawableCompat.setTintMode(this.mShapeDrawable, mode);
        }
        this.mRippleDrawable = DrawableCompat.wrap(createShapeDrawable());
        DrawableCompat.setTintList(this.mRippleDrawable, createColorStateList(i));
        if (i2 > 0) {
            this.mBorderDrawable = createBorderDrawable(i2, colorStateList);
            drawableArr = new Drawable[]{this.mBorderDrawable, this.mShapeDrawable, this.mRippleDrawable};
        } else {
            this.mBorderDrawable = null;
            drawableArr = new Drawable[]{this.mShapeDrawable, this.mRippleDrawable};
        }
        this.mContentBackground = new LayerDrawable(drawableArr);
        ShadowDrawableWrapper shadowDrawableWrapper = new ShadowDrawableWrapper(this.mView.getResources(), this.mContentBackground, this.mShadowViewDelegate.getRadius(), this.mElevation, this.mElevation + this.mPressedTranslationZ);
        this.mShadowDrawable = shadowDrawableWrapper;
        this.mShadowDrawable.setAddPaddingForCorners(false);
        this.mShadowViewDelegate.setBackgroundDrawable(this.mShadowDrawable);
    }

    /* access modifiers changed from: 0000 */
    public void setBackgroundTintList(ColorStateList colorStateList) {
        if (this.mShapeDrawable != null) {
            DrawableCompat.setTintList(this.mShapeDrawable, colorStateList);
        }
        if (this.mBorderDrawable != null) {
            this.mBorderDrawable.setBorderTint(colorStateList);
        }
    }

    /* access modifiers changed from: 0000 */
    public void setBackgroundTintMode(Mode mode) {
        if (this.mShapeDrawable != null) {
            DrawableCompat.setTintMode(this.mShapeDrawable, mode);
        }
    }

    /* access modifiers changed from: 0000 */
    public void setRippleColor(int i) {
        if (this.mRippleDrawable != null) {
            DrawableCompat.setTintList(this.mRippleDrawable, createColorStateList(i));
        }
    }

    /* access modifiers changed from: 0000 */
    public float getElevation() {
        return this.mElevation;
    }

    /* access modifiers changed from: 0000 */
    public void onElevationChanged(float f) {
        ShadowDrawableWrapper shadowDrawableWrapper = this.mShadowDrawable;
        if (shadowDrawableWrapper != null) {
            shadowDrawableWrapper.setShadowSize(f, this.mPressedTranslationZ + f);
            updatePadding();
        }
    }

    /* access modifiers changed from: 0000 */
    public void onTranslationZChanged(float f) {
        ShadowDrawableWrapper shadowDrawableWrapper = this.mShadowDrawable;
        if (shadowDrawableWrapper != null) {
            shadowDrawableWrapper.setMaxShadowSize(this.mElevation + f);
            updatePadding();
        }
    }

    /* access modifiers changed from: 0000 */
    public void onDrawableStateChanged(int[] iArr) {
        this.mStateListAnimator.setState(iArr);
    }

    /* access modifiers changed from: 0000 */
    public void jumpDrawableToCurrentState() {
        this.mStateListAnimator.jumpToCurrentState();
    }

    /* access modifiers changed from: 0000 */
    public void hide(@Nullable final InternalVisibilityChangedListener internalVisibilityChangedListener, final boolean z) {
        if (this.mIsHiding || this.mView.getVisibility() != 0) {
            if (internalVisibilityChangedListener != null) {
                internalVisibilityChangedListener.onHidden();
            }
            return;
        }
        Animation loadAnimation = AnimationUtils.loadAnimation(this.mView.getContext(), C0007R.anim.design_fab_out);
        loadAnimation.setInterpolator(AnimationUtils.FAST_OUT_LINEAR_IN_INTERPOLATOR);
        loadAnimation.setDuration(200);
        loadAnimation.setAnimationListener(new AnimationListenerAdapter() {
            public void onAnimationStart(Animation animation) {
                FloatingActionButtonEclairMr1.this.mIsHiding = true;
            }

            public void onAnimationEnd(Animation animation) {
                FloatingActionButtonEclairMr1.this.mIsHiding = false;
                FloatingActionButtonEclairMr1.this.mView.internalSetVisibility(8, z);
                InternalVisibilityChangedListener internalVisibilityChangedListener = internalVisibilityChangedListener;
                if (internalVisibilityChangedListener != null) {
                    internalVisibilityChangedListener.onHidden();
                }
            }
        });
        this.mView.startAnimation(loadAnimation);
    }

    /* access modifiers changed from: 0000 */
    public void show(@Nullable final InternalVisibilityChangedListener internalVisibilityChangedListener, boolean z) {
        if (this.mView.getVisibility() != 0 || this.mIsHiding) {
            this.mView.clearAnimation();
            this.mView.internalSetVisibility(0, z);
            Animation loadAnimation = AnimationUtils.loadAnimation(this.mView.getContext(), C0007R.anim.design_fab_in);
            loadAnimation.setDuration(200);
            loadAnimation.setInterpolator(AnimationUtils.LINEAR_OUT_SLOW_IN_INTERPOLATOR);
            loadAnimation.setAnimationListener(new AnimationListenerAdapter() {
                public void onAnimationEnd(Animation animation) {
                    InternalVisibilityChangedListener internalVisibilityChangedListener = internalVisibilityChangedListener;
                    if (internalVisibilityChangedListener != null) {
                        internalVisibilityChangedListener.onShown();
                    }
                }
            });
            this.mView.startAnimation(loadAnimation);
        } else if (internalVisibilityChangedListener != null) {
            internalVisibilityChangedListener.onShown();
        }
    }

    /* access modifiers changed from: 0000 */
    public void getPadding(Rect rect) {
        this.mShadowDrawable.getPadding(rect);
    }

    private Animation setupAnimation(Animation animation) {
        animation.setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
        animation.setDuration((long) this.mAnimationDuration);
        return animation;
    }

    private static ColorStateList createColorStateList(int i) {
        return new ColorStateList(new int[][]{FOCUSED_ENABLED_STATE_SET, PRESSED_ENABLED_STATE_SET, new int[0]}, new int[]{i, i, 0});
    }
}
