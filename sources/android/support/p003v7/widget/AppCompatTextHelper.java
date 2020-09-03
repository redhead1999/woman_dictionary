package android.support.p003v7.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.p003v7.appcompat.C0254R;
import android.support.p003v7.text.AllCapsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.widget.TextView;

/* renamed from: android.support.v7.widget.AppCompatTextHelper */
class AppCompatTextHelper {
    private static final int[] TEXT_APPEARANCE_ATTRS = {C0254R.attr.textAllCaps};
    private static final int[] VIEW_ATTRS = {16842804, 16843119, 16843117, 16843120, 16843118};
    private TintInfo mDrawableBottomTint;
    private TintInfo mDrawableLeftTint;
    private TintInfo mDrawableRightTint;
    private TintInfo mDrawableTopTint;
    final TextView mView;

    static AppCompatTextHelper create(TextView textView) {
        if (VERSION.SDK_INT >= 17) {
            return new AppCompatTextHelperV17(textView);
        }
        return new AppCompatTextHelper(textView);
    }

    AppCompatTextHelper(TextView textView) {
        this.mView = textView;
    }

    /* access modifiers changed from: 0000 */
    public void loadFromAttributes(AttributeSet attributeSet, int i) {
        boolean z;
        boolean z2;
        Context context = this.mView.getContext();
        AppCompatDrawableManager appCompatDrawableManager = AppCompatDrawableManager.get();
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, VIEW_ATTRS, i, 0);
        int resourceId = obtainStyledAttributes.getResourceId(0, -1);
        if (obtainStyledAttributes.hasValue(1)) {
            this.mDrawableLeftTint = createTintInfo(context, appCompatDrawableManager, obtainStyledAttributes.getResourceId(1, 0));
        }
        if (obtainStyledAttributes.hasValue(2)) {
            this.mDrawableTopTint = createTintInfo(context, appCompatDrawableManager, obtainStyledAttributes.getResourceId(2, 0));
        }
        if (obtainStyledAttributes.hasValue(3)) {
            this.mDrawableRightTint = createTintInfo(context, appCompatDrawableManager, obtainStyledAttributes.getResourceId(3, 0));
        }
        if (obtainStyledAttributes.hasValue(4)) {
            this.mDrawableBottomTint = createTintInfo(context, appCompatDrawableManager, obtainStyledAttributes.getResourceId(4, 0));
        }
        obtainStyledAttributes.recycle();
        if (!(this.mView.getTransformationMethod() instanceof PasswordTransformationMethod)) {
            if (resourceId != -1) {
                TypedArray obtainStyledAttributes2 = context.obtainStyledAttributes(resourceId, C0254R.styleable.TextAppearance);
                if (obtainStyledAttributes2.hasValue(C0254R.styleable.TextAppearance_textAllCaps)) {
                    z = obtainStyledAttributes2.getBoolean(C0254R.styleable.TextAppearance_textAllCaps, false);
                    z2 = true;
                } else {
                    z2 = false;
                    z = false;
                }
                obtainStyledAttributes2.recycle();
            } else {
                z2 = false;
                z = false;
            }
            TypedArray obtainStyledAttributes3 = context.obtainStyledAttributes(attributeSet, TEXT_APPEARANCE_ATTRS, i, 0);
            if (obtainStyledAttributes3.hasValue(0)) {
                z = obtainStyledAttributes3.getBoolean(0, false);
                z2 = true;
            }
            obtainStyledAttributes3.recycle();
            if (z2) {
                setAllCaps(z);
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void onSetTextAppearance(Context context, int i) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(i, TEXT_APPEARANCE_ATTRS);
        if (obtainStyledAttributes.hasValue(0)) {
            setAllCaps(obtainStyledAttributes.getBoolean(0, false));
        }
        obtainStyledAttributes.recycle();
    }

    /* access modifiers changed from: 0000 */
    public void setAllCaps(boolean z) {
        TextView textView = this.mView;
        textView.setTransformationMethod(z ? new AllCapsTransformationMethod(textView.getContext()) : null);
    }

    /* access modifiers changed from: 0000 */
    public void applyCompoundDrawablesTints() {
        if (this.mDrawableLeftTint != null || this.mDrawableTopTint != null || this.mDrawableRightTint != null || this.mDrawableBottomTint != null) {
            Drawable[] compoundDrawables = this.mView.getCompoundDrawables();
            applyCompoundDrawableTint(compoundDrawables[0], this.mDrawableLeftTint);
            applyCompoundDrawableTint(compoundDrawables[1], this.mDrawableTopTint);
            applyCompoundDrawableTint(compoundDrawables[2], this.mDrawableRightTint);
            applyCompoundDrawableTint(compoundDrawables[3], this.mDrawableBottomTint);
        }
    }

    /* access modifiers changed from: 0000 */
    public final void applyCompoundDrawableTint(Drawable drawable, TintInfo tintInfo) {
        if (drawable != null && tintInfo != null) {
            AppCompatDrawableManager.tintDrawable(drawable, tintInfo, this.mView.getDrawableState());
        }
    }

    protected static TintInfo createTintInfo(Context context, AppCompatDrawableManager appCompatDrawableManager, int i) {
        ColorStateList tintList = appCompatDrawableManager.getTintList(context, i);
        if (tintList == null) {
            return null;
        }
        TintInfo tintInfo = new TintInfo();
        tintInfo.mHasTintList = true;
        tintInfo.mTintList = tintList;
        return tintInfo;
    }
}
