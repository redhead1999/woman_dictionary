package android.support.p003v7.widget;

import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.SeekBar;

/* renamed from: android.support.v7.widget.AppCompatSeekBarHelper */
class AppCompatSeekBarHelper extends AppCompatProgressBarHelper {
    private static final int[] TINT_ATTRS = {16843074};
    private final SeekBar mView;

    AppCompatSeekBarHelper(SeekBar seekBar, AppCompatDrawableManager appCompatDrawableManager) {
        super(seekBar, appCompatDrawableManager);
        this.mView = seekBar;
    }

    /* access modifiers changed from: 0000 */
    public void loadFromAttributes(AttributeSet attributeSet, int i) {
        super.loadFromAttributes(attributeSet, i);
        TintTypedArray obtainStyledAttributes = TintTypedArray.obtainStyledAttributes(this.mView.getContext(), attributeSet, TINT_ATTRS, i, 0);
        Drawable drawableIfKnown = obtainStyledAttributes.getDrawableIfKnown(0);
        if (drawableIfKnown != null) {
            this.mView.setThumb(drawableIfKnown);
        }
        obtainStyledAttributes.recycle();
    }
}
