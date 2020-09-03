package android.support.design.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.C0007R;
import android.support.design.widget.CoordinatorLayout.DefaultBehavior;
import android.support.design.widget.CoordinatorLayout.LayoutParams;
import android.support.design.widget.Snackbar.SnackbarLayout;
import android.support.p000v4.view.ViewCompat;
import android.support.p003v7.widget.AppCompatDrawableManager;
import android.support.p003v7.widget.AppCompatImageHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import java.util.List;

@DefaultBehavior(Behavior.class)
public class FloatingActionButton extends VisibilityAwareImageButton {
    private static final String LOG_TAG = "FloatingActionButton";
    private static final int SIZE_MINI = 1;
    private static final int SIZE_NORMAL = 0;
    private ColorStateList mBackgroundTint;
    private Mode mBackgroundTintMode;
    private int mBorderWidth;
    /* access modifiers changed from: private */
    public boolean mCompatPadding;
    private AppCompatImageHelper mImageHelper;
    /* access modifiers changed from: private */
    public int mImagePadding;
    private FloatingActionButtonImpl mImpl;
    private int mRippleColor;
    /* access modifiers changed from: private */
    public final Rect mShadowPadding;
    private int mSize;

    public static class Behavior extends android.support.design.widget.CoordinatorLayout.Behavior<FloatingActionButton> {
        private static final boolean SNACKBAR_BEHAVIOR_ENABLED = (VERSION.SDK_INT >= 11);
        private float mFabTranslationY;
        private ValueAnimatorCompat mFabTranslationYAnimator;
        private Rect mTmpRect;

        public boolean layoutDependsOn(CoordinatorLayout coordinatorLayout, FloatingActionButton floatingActionButton, View view) {
            return SNACKBAR_BEHAVIOR_ENABLED && (view instanceof SnackbarLayout);
        }

        public boolean onDependentViewChanged(CoordinatorLayout coordinatorLayout, FloatingActionButton floatingActionButton, View view) {
            if (view instanceof SnackbarLayout) {
                updateFabTranslationForSnackbar(coordinatorLayout, floatingActionButton, view);
            } else if (view instanceof AppBarLayout) {
                updateFabVisibility(coordinatorLayout, (AppBarLayout) view, floatingActionButton);
            }
            return false;
        }

        public void onDependentViewRemoved(CoordinatorLayout coordinatorLayout, FloatingActionButton floatingActionButton, View view) {
            if (view instanceof SnackbarLayout) {
                updateFabTranslationForSnackbar(coordinatorLayout, floatingActionButton, view);
            }
        }

        private boolean updateFabVisibility(CoordinatorLayout coordinatorLayout, AppBarLayout appBarLayout, FloatingActionButton floatingActionButton) {
            if (((LayoutParams) floatingActionButton.getLayoutParams()).getAnchorId() != appBarLayout.getId() || floatingActionButton.getUserSetVisibility() != 0) {
                return false;
            }
            if (this.mTmpRect == null) {
                this.mTmpRect = new Rect();
            }
            Rect rect = this.mTmpRect;
            ViewGroupUtils.getDescendantRect(coordinatorLayout, appBarLayout, rect);
            if (rect.bottom <= appBarLayout.getMinimumHeightForVisibleOverlappingContent()) {
                floatingActionButton.hide(null, false);
            } else {
                floatingActionButton.show(null, false);
            }
            return true;
        }

        private void updateFabTranslationForSnackbar(CoordinatorLayout coordinatorLayout, final FloatingActionButton floatingActionButton, View view) {
            float fabTranslationYForSnackbar = getFabTranslationYForSnackbar(coordinatorLayout, floatingActionButton);
            if (this.mFabTranslationY != fabTranslationYForSnackbar) {
                float translationY = ViewCompat.getTranslationY(floatingActionButton);
                ValueAnimatorCompat valueAnimatorCompat = this.mFabTranslationYAnimator;
                if (valueAnimatorCompat != null && valueAnimatorCompat.isRunning()) {
                    this.mFabTranslationYAnimator.cancel();
                }
                if (!floatingActionButton.isShown() || Math.abs(translationY - fabTranslationYForSnackbar) <= ((float) floatingActionButton.getHeight()) * 0.667f) {
                    ViewCompat.setTranslationY(floatingActionButton, fabTranslationYForSnackbar);
                } else {
                    if (this.mFabTranslationYAnimator == null) {
                        this.mFabTranslationYAnimator = ViewUtils.createAnimator();
                        this.mFabTranslationYAnimator.setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
                        this.mFabTranslationYAnimator.setUpdateListener(new AnimatorUpdateListener() {
                            public void onAnimationUpdate(ValueAnimatorCompat valueAnimatorCompat) {
                                ViewCompat.setTranslationY(floatingActionButton, valueAnimatorCompat.getAnimatedFloatValue());
                            }
                        });
                    }
                    this.mFabTranslationYAnimator.setFloatValues(translationY, fabTranslationYForSnackbar);
                    this.mFabTranslationYAnimator.start();
                }
                this.mFabTranslationY = fabTranslationYForSnackbar;
            }
        }

        private float getFabTranslationYForSnackbar(CoordinatorLayout coordinatorLayout, FloatingActionButton floatingActionButton) {
            List dependencies = coordinatorLayout.getDependencies(floatingActionButton);
            int size = dependencies.size();
            float f = 0.0f;
            for (int i = 0; i < size; i++) {
                View view = (View) dependencies.get(i);
                if ((view instanceof SnackbarLayout) && coordinatorLayout.doViewsOverlap(floatingActionButton, view)) {
                    f = Math.min(f, ViewCompat.getTranslationY(view) - ((float) view.getHeight()));
                }
            }
            return f;
        }

        public boolean onLayoutChild(CoordinatorLayout coordinatorLayout, FloatingActionButton floatingActionButton, int i) {
            List dependencies = coordinatorLayout.getDependencies(floatingActionButton);
            int size = dependencies.size();
            for (int i2 = 0; i2 < size; i2++) {
                View view = (View) dependencies.get(i2);
                if ((view instanceof AppBarLayout) && updateFabVisibility(coordinatorLayout, (AppBarLayout) view, floatingActionButton)) {
                    break;
                }
            }
            coordinatorLayout.onLayoutChild(floatingActionButton, i);
            offsetIfNeeded(coordinatorLayout, floatingActionButton);
            return true;
        }

        private void offsetIfNeeded(CoordinatorLayout coordinatorLayout, FloatingActionButton floatingActionButton) {
            Rect access$200 = floatingActionButton.mShadowPadding;
            if (access$200 != null && access$200.centerX() > 0 && access$200.centerY() > 0) {
                LayoutParams layoutParams = (LayoutParams) floatingActionButton.getLayoutParams();
                int i = 0;
                int i2 = floatingActionButton.getRight() >= coordinatorLayout.getWidth() - layoutParams.rightMargin ? access$200.right : floatingActionButton.getLeft() <= layoutParams.leftMargin ? -access$200.left : 0;
                if (floatingActionButton.getBottom() >= coordinatorLayout.getBottom() - layoutParams.bottomMargin) {
                    i = access$200.bottom;
                } else if (floatingActionButton.getTop() <= layoutParams.topMargin) {
                    i = -access$200.top;
                }
                floatingActionButton.offsetTopAndBottom(i);
                floatingActionButton.offsetLeftAndRight(i2);
            }
        }
    }

    public static abstract class OnVisibilityChangedListener {
        public void onHidden(FloatingActionButton floatingActionButton) {
        }

        public void onShown(FloatingActionButton floatingActionButton) {
        }
    }

    private class ShadowDelegateImpl implements ShadowViewDelegate {
        private ShadowDelegateImpl() {
        }

        public float getRadius() {
            return ((float) FloatingActionButton.this.getSizeDimension()) / 2.0f;
        }

        public void setShadowPadding(int i, int i2, int i3, int i4) {
            FloatingActionButton.this.mShadowPadding.set(i, i2, i3, i4);
            FloatingActionButton floatingActionButton = FloatingActionButton.this;
            floatingActionButton.setPadding(i + floatingActionButton.mImagePadding, i2 + FloatingActionButton.this.mImagePadding, i3 + FloatingActionButton.this.mImagePadding, i4 + FloatingActionButton.this.mImagePadding);
        }

        public void setBackgroundDrawable(Drawable drawable) {
            FloatingActionButton.super.setBackgroundDrawable(drawable);
        }

        public boolean isCompatPaddingEnabled() {
            return FloatingActionButton.this.mCompatPadding;
        }
    }

    public /* bridge */ /* synthetic */ void setVisibility(int i) {
        super.setVisibility(i);
    }

    public FloatingActionButton(Context context) {
        this(context, null);
    }

    public FloatingActionButton(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public FloatingActionButton(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mShadowPadding = new Rect();
        ThemeUtils.checkAppCompatTheme(context);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, C0007R.styleable.FloatingActionButton, i, C0007R.style.Widget_Design_FloatingActionButton);
        this.mBackgroundTint = obtainStyledAttributes.getColorStateList(C0007R.styleable.FloatingActionButton_backgroundTint);
        this.mBackgroundTintMode = parseTintMode(obtainStyledAttributes.getInt(C0007R.styleable.FloatingActionButton_backgroundTintMode, -1), null);
        this.mRippleColor = obtainStyledAttributes.getColor(C0007R.styleable.FloatingActionButton_rippleColor, 0);
        this.mSize = obtainStyledAttributes.getInt(C0007R.styleable.FloatingActionButton_fabSize, 0);
        this.mBorderWidth = obtainStyledAttributes.getDimensionPixelSize(C0007R.styleable.FloatingActionButton_borderWidth, 0);
        float dimension = obtainStyledAttributes.getDimension(C0007R.styleable.FloatingActionButton_elevation, 0.0f);
        float dimension2 = obtainStyledAttributes.getDimension(C0007R.styleable.FloatingActionButton_pressedTranslationZ, 0.0f);
        this.mCompatPadding = obtainStyledAttributes.getBoolean(C0007R.styleable.FloatingActionButton_useCompatPadding, false);
        obtainStyledAttributes.recycle();
        this.mImageHelper = new AppCompatImageHelper(this, AppCompatDrawableManager.get());
        this.mImageHelper.loadFromAttributes(attributeSet, i);
        this.mImagePadding = (getSizeDimension() - ((int) getResources().getDimension(C0007R.dimen.design_fab_image_size))) / 2;
        getImpl().setBackgroundDrawable(this.mBackgroundTint, this.mBackgroundTintMode, this.mRippleColor, this.mBorderWidth);
        getImpl().setElevation(dimension);
        getImpl().setPressedTranslationZ(dimension2);
        getImpl().updatePadding();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int sizeDimension = getSizeDimension();
        int min = Math.min(resolveAdjustedSize(sizeDimension, i), resolveAdjustedSize(sizeDimension, i2));
        setMeasuredDimension(this.mShadowPadding.left + min + this.mShadowPadding.right, min + this.mShadowPadding.top + this.mShadowPadding.bottom);
    }

    public void setRippleColor(@ColorInt int i) {
        if (this.mRippleColor != i) {
            this.mRippleColor = i;
            getImpl().setRippleColor(i);
        }
    }

    @Nullable
    public ColorStateList getBackgroundTintList() {
        return this.mBackgroundTint;
    }

    public void setBackgroundTintList(@Nullable ColorStateList colorStateList) {
        if (this.mBackgroundTint != colorStateList) {
            this.mBackgroundTint = colorStateList;
            getImpl().setBackgroundTintList(colorStateList);
        }
    }

    @Nullable
    public Mode getBackgroundTintMode() {
        return this.mBackgroundTintMode;
    }

    public void setBackgroundTintMode(@Nullable Mode mode) {
        if (this.mBackgroundTintMode != mode) {
            this.mBackgroundTintMode = mode;
            getImpl().setBackgroundTintMode(mode);
        }
    }

    public void setBackgroundDrawable(Drawable drawable) {
        Log.i(LOG_TAG, "Setting a custom background is not supported.");
    }

    public void setBackgroundResource(int i) {
        Log.i(LOG_TAG, "Setting a custom background is not supported.");
    }

    public void setBackgroundColor(int i) {
        Log.i(LOG_TAG, "Setting a custom background is not supported.");
    }

    public void setImageResource(@DrawableRes int i) {
        this.mImageHelper.setImageResource(i);
    }

    public void show() {
        show(null);
    }

    public void show(@Nullable OnVisibilityChangedListener onVisibilityChangedListener) {
        show(onVisibilityChangedListener, true);
    }

    /* access modifiers changed from: private */
    public void show(OnVisibilityChangedListener onVisibilityChangedListener, boolean z) {
        getImpl().show(wrapOnVisibilityChangedListener(onVisibilityChangedListener), z);
    }

    public void hide() {
        hide(null);
    }

    public void hide(@Nullable OnVisibilityChangedListener onVisibilityChangedListener) {
        hide(onVisibilityChangedListener, true);
    }

    /* access modifiers changed from: private */
    public void hide(@Nullable OnVisibilityChangedListener onVisibilityChangedListener, boolean z) {
        getImpl().hide(wrapOnVisibilityChangedListener(onVisibilityChangedListener), z);
    }

    public void setUseCompatPadding(boolean z) {
        if (this.mCompatPadding != z) {
            this.mCompatPadding = z;
            getImpl().onCompatShadowChanged();
        }
    }

    public boolean getUseCompatPadding() {
        return this.mCompatPadding;
    }

    @Nullable
    private InternalVisibilityChangedListener wrapOnVisibilityChangedListener(@Nullable final OnVisibilityChangedListener onVisibilityChangedListener) {
        if (onVisibilityChangedListener == null) {
            return null;
        }
        return new InternalVisibilityChangedListener() {
            public void onShown() {
                onVisibilityChangedListener.onShown(FloatingActionButton.this);
            }

            public void onHidden() {
                onVisibilityChangedListener.onHidden(FloatingActionButton.this);
            }
        };
    }

    /* access modifiers changed from: 0000 */
    public final int getSizeDimension() {
        if (this.mSize != 1) {
            return getResources().getDimensionPixelSize(C0007R.dimen.design_fab_size_normal);
        }
        return getResources().getDimensionPixelSize(C0007R.dimen.design_fab_size_mini);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        getImpl().onAttachedToWindow();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getImpl().onDetachedFromWindow();
    }

    /* access modifiers changed from: protected */
    public void drawableStateChanged() {
        super.drawableStateChanged();
        getImpl().onDrawableStateChanged(getDrawableState());
    }

    @TargetApi(11)
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        getImpl().jumpDrawableToCurrentState();
    }

    public boolean getContentRect(@NonNull Rect rect) {
        if (!ViewCompat.isLaidOut(this)) {
            return false;
        }
        rect.set(0, 0, getWidth(), getHeight());
        rect.left += this.mShadowPadding.left;
        rect.top += this.mShadowPadding.top;
        rect.right -= this.mShadowPadding.right;
        rect.bottom -= this.mShadowPadding.bottom;
        return true;
    }

    @NonNull
    public Drawable getContentBackground() {
        return getImpl().getContentBackground();
    }

    private static int resolveAdjustedSize(int i, int i2) {
        int mode = MeasureSpec.getMode(i2);
        int size = MeasureSpec.getSize(i2);
        if (mode != Integer.MIN_VALUE) {
            return (mode == 0 || mode != 1073741824) ? i : size;
        }
        return Math.min(i, size);
    }

    static Mode parseTintMode(int i, Mode mode) {
        if (i == 3) {
            return Mode.SRC_OVER;
        }
        if (i == 5) {
            return Mode.SRC_IN;
        }
        if (i == 9) {
            return Mode.SRC_ATOP;
        }
        if (i != 14) {
            return i != 15 ? mode : Mode.SCREEN;
        }
        return Mode.MULTIPLY;
    }

    public float getCompatElevation() {
        return getImpl().getElevation();
    }

    public void setCompatElevation(float f) {
        getImpl().setElevation(f);
    }

    private FloatingActionButtonImpl getImpl() {
        if (this.mImpl == null) {
            this.mImpl = createImpl();
        }
        return this.mImpl;
    }

    private FloatingActionButtonImpl createImpl() {
        int i = VERSION.SDK_INT;
        if (i >= 21) {
            return new FloatingActionButtonLollipop(this, new ShadowDelegateImpl());
        }
        if (i >= 14) {
            return new FloatingActionButtonIcs(this, new ShadowDelegateImpl());
        }
        return new FloatingActionButtonEclairMr1(this, new ShadowDelegateImpl());
    }
}
