package android.support.p003v7.widget;

import android.graphics.drawable.Drawable;
import android.support.p000v4.content.ContextCompat;
import android.support.p003v7.appcompat.C0254R;
import android.util.AttributeSet;
import android.widget.ImageView;

/* renamed from: android.support.v7.widget.AppCompatImageHelper */
public class AppCompatImageHelper {
    private final AppCompatDrawableManager mDrawableManager;
    private final ImageView mView;

    public AppCompatImageHelper(ImageView imageView, AppCompatDrawableManager appCompatDrawableManager) {
        this.mView = imageView;
        this.mDrawableManager = appCompatDrawableManager;
    }

    public void loadFromAttributes(AttributeSet attributeSet, int i) {
        TintTypedArray obtainStyledAttributes = TintTypedArray.obtainStyledAttributes(this.mView.getContext(), attributeSet, C0254R.styleable.AppCompatImageView, i, 0);
        try {
            Drawable drawableIfKnown = obtainStyledAttributes.getDrawableIfKnown(C0254R.styleable.AppCompatImageView_android_src);
            if (drawableIfKnown != null) {
                this.mView.setImageDrawable(drawableIfKnown);
            }
            int resourceId = obtainStyledAttributes.getResourceId(C0254R.styleable.AppCompatImageView_srcCompat, -1);
            if (resourceId != -1) {
                Drawable drawable = this.mDrawableManager.getDrawable(this.mView.getContext(), resourceId);
                if (drawable != null) {
                    this.mView.setImageDrawable(drawable);
                }
            }
            Drawable drawable2 = this.mView.getDrawable();
            if (drawable2 != null) {
                DrawableUtils.fixDrawable(drawable2);
            }
        } finally {
            obtainStyledAttributes.recycle();
        }
    }

    public void setImageResource(int i) {
        if (i != 0) {
            AppCompatDrawableManager appCompatDrawableManager = this.mDrawableManager;
            Drawable drawable = appCompatDrawableManager != null ? appCompatDrawableManager.getDrawable(this.mView.getContext(), i) : ContextCompat.getDrawable(this.mView.getContext(), i);
            if (drawable != null) {
                DrawableUtils.fixDrawable(drawable);
            }
            this.mView.setImageDrawable(drawable);
            return;
        }
        this.mView.setImageDrawable(null);
    }
}
