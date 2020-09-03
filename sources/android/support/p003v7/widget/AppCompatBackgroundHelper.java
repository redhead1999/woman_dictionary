package android.support.p003v7.widget;

import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.support.p000v4.view.ViewCompat;
import android.support.p003v7.appcompat.C0254R;
import android.util.AttributeSet;
import android.view.View;

/* renamed from: android.support.v7.widget.AppCompatBackgroundHelper */
class AppCompatBackgroundHelper {
    private TintInfo mBackgroundTint;
    private final AppCompatDrawableManager mDrawableManager;
    private TintInfo mInternalBackgroundTint;
    private TintInfo mTmpInfo;
    private final View mView;

    AppCompatBackgroundHelper(View view, AppCompatDrawableManager appCompatDrawableManager) {
        this.mView = view;
        this.mDrawableManager = appCompatDrawableManager;
    }

    /* access modifiers changed from: 0000 */
    public void loadFromAttributes(AttributeSet attributeSet, int i) {
        TypedArray obtainStyledAttributes = this.mView.getContext().obtainStyledAttributes(attributeSet, C0254R.styleable.ViewBackgroundHelper, i, 0);
        try {
            if (obtainStyledAttributes.hasValue(C0254R.styleable.ViewBackgroundHelper_android_background)) {
                ColorStateList tintList = this.mDrawableManager.getTintList(this.mView.getContext(), obtainStyledAttributes.getResourceId(C0254R.styleable.ViewBackgroundHelper_android_background, -1));
                if (tintList != null) {
                    setInternalBackgroundTint(tintList);
                }
            }
            if (obtainStyledAttributes.hasValue(C0254R.styleable.ViewBackgroundHelper_backgroundTint)) {
                ViewCompat.setBackgroundTintList(this.mView, obtainStyledAttributes.getColorStateList(C0254R.styleable.ViewBackgroundHelper_backgroundTint));
            }
            if (obtainStyledAttributes.hasValue(C0254R.styleable.ViewBackgroundHelper_backgroundTintMode)) {
                ViewCompat.setBackgroundTintMode(this.mView, DrawableUtils.parseTintMode(obtainStyledAttributes.getInt(C0254R.styleable.ViewBackgroundHelper_backgroundTintMode, -1), null));
            }
        } finally {
            obtainStyledAttributes.recycle();
        }
    }

    /* access modifiers changed from: 0000 */
    public void onSetBackgroundResource(int i) {
        AppCompatDrawableManager appCompatDrawableManager = this.mDrawableManager;
        setInternalBackgroundTint(appCompatDrawableManager != null ? appCompatDrawableManager.getTintList(this.mView.getContext(), i) : null);
    }

    /* access modifiers changed from: 0000 */
    public void onSetBackgroundDrawable(Drawable drawable) {
        setInternalBackgroundTint(null);
    }

    /* access modifiers changed from: 0000 */
    public void setSupportBackgroundTintList(ColorStateList colorStateList) {
        if (this.mBackgroundTint == null) {
            this.mBackgroundTint = new TintInfo();
        }
        TintInfo tintInfo = this.mBackgroundTint;
        tintInfo.mTintList = colorStateList;
        tintInfo.mHasTintList = true;
        applySupportBackgroundTint();
    }

    /* access modifiers changed from: 0000 */
    public ColorStateList getSupportBackgroundTintList() {
        TintInfo tintInfo = this.mBackgroundTint;
        if (tintInfo != null) {
            return tintInfo.mTintList;
        }
        return null;
    }

    /* access modifiers changed from: 0000 */
    public void setSupportBackgroundTintMode(Mode mode) {
        if (this.mBackgroundTint == null) {
            this.mBackgroundTint = new TintInfo();
        }
        TintInfo tintInfo = this.mBackgroundTint;
        tintInfo.mTintMode = mode;
        tintInfo.mHasTintMode = true;
        applySupportBackgroundTint();
    }

    /* access modifiers changed from: 0000 */
    public Mode getSupportBackgroundTintMode() {
        TintInfo tintInfo = this.mBackgroundTint;
        if (tintInfo != null) {
            return tintInfo.mTintMode;
        }
        return null;
    }

    /* access modifiers changed from: 0000 */
    public void applySupportBackgroundTint() {
        Drawable background = this.mView.getBackground();
        if (background != null && (VERSION.SDK_INT != 21 || !applyFrameworkTintUsingColorFilter(background))) {
            TintInfo tintInfo = this.mBackgroundTint;
            if (tintInfo != null) {
                AppCompatDrawableManager.tintDrawable(background, tintInfo, this.mView.getDrawableState());
            } else {
                TintInfo tintInfo2 = this.mInternalBackgroundTint;
                if (tintInfo2 != null) {
                    AppCompatDrawableManager.tintDrawable(background, tintInfo2, this.mView.getDrawableState());
                }
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void setInternalBackgroundTint(ColorStateList colorStateList) {
        if (colorStateList != null) {
            if (this.mInternalBackgroundTint == null) {
                this.mInternalBackgroundTint = new TintInfo();
            }
            TintInfo tintInfo = this.mInternalBackgroundTint;
            tintInfo.mTintList = colorStateList;
            tintInfo.mHasTintList = true;
        } else {
            this.mInternalBackgroundTint = null;
        }
        applySupportBackgroundTint();
    }

    private boolean applyFrameworkTintUsingColorFilter(@NonNull Drawable drawable) {
        if (this.mTmpInfo == null) {
            this.mTmpInfo = new TintInfo();
        }
        TintInfo tintInfo = this.mTmpInfo;
        tintInfo.clear();
        ColorStateList backgroundTintList = ViewCompat.getBackgroundTintList(this.mView);
        if (backgroundTintList != null) {
            tintInfo.mHasTintList = true;
            tintInfo.mTintList = backgroundTintList;
        }
        Mode backgroundTintMode = ViewCompat.getBackgroundTintMode(this.mView);
        if (backgroundTintMode != null) {
            tintInfo.mHasTintMode = true;
            tintInfo.mTintMode = backgroundTintMode;
        }
        if (!tintInfo.mHasTintList && !tintInfo.mHasTintMode) {
            return false;
        }
        AppCompatDrawableManager.tintDrawable(drawable, tintInfo, this.mView.getDrawableState());
        return true;
    }
}
