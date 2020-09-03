package android.support.p003v7.widget;

import android.content.Context;
import android.support.p003v7.appcompat.C0254R;
import android.util.AttributeSet;
import android.widget.SeekBar;

/* renamed from: android.support.v7.widget.AppCompatSeekBar */
public class AppCompatSeekBar extends SeekBar {
    private AppCompatSeekBarHelper mAppCompatSeekBarHelper;
    private AppCompatDrawableManager mDrawableManager;

    public AppCompatSeekBar(Context context) {
        this(context, null);
    }

    public AppCompatSeekBar(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, C0254R.attr.seekBarStyle);
    }

    public AppCompatSeekBar(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mDrawableManager = AppCompatDrawableManager.get();
        this.mAppCompatSeekBarHelper = new AppCompatSeekBarHelper(this, this.mDrawableManager);
        this.mAppCompatSeekBarHelper.loadFromAttributes(attributeSet, i);
    }
}
