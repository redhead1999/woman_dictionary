package android.support.p003v7.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.p003v7.appcompat.C0254R;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/* renamed from: android.support.v7.widget.ButtonBarLayout */
public class ButtonBarLayout extends LinearLayout {
    private boolean mAllowStacking;
    private int mLastWidthSize = -1;

    public ButtonBarLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, C0254R.styleable.ButtonBarLayout);
        this.mAllowStacking = obtainStyledAttributes.getBoolean(C0254R.styleable.ButtonBarLayout_allowStacking, false);
        obtainStyledAttributes.recycle();
    }

    public void setAllowStacking(boolean z) {
        if (this.mAllowStacking != z) {
            this.mAllowStacking = z;
            if (!this.mAllowStacking && getOrientation() == 1) {
                setStacked(false);
            }
            requestLayout();
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x004d, code lost:
        if ((android.support.p000v4.view.ViewCompat.getMeasuredWidthAndState(r8) & android.support.p000v4.view.ViewCompat.MEASURED_STATE_MASK) == 16777216) goto L_0x004f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x006f, code lost:
        if (((r6 + getPaddingLeft()) + getPaddingRight()) > r0) goto L_0x004f;
     */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x007b  */
    /* JADX WARNING: Removed duplicated region for block: B:35:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onMeasure(int r9, int r10) {
        /*
            r8 = this;
            int r0 = android.view.View.MeasureSpec.getSize(r9)
            boolean r1 = r8.mAllowStacking
            r2 = 0
            if (r1 == 0) goto L_0x0018
            int r1 = r8.mLastWidthSize
            if (r0 <= r1) goto L_0x0016
            boolean r1 = r8.isStacked()
            if (r1 == 0) goto L_0x0016
            r8.setStacked(r2)
        L_0x0016:
            r8.mLastWidthSize = r0
        L_0x0018:
            boolean r1 = r8.isStacked()
            r3 = 1
            if (r1 != 0) goto L_0x002f
            int r1 = android.view.View.MeasureSpec.getMode(r9)
            r4 = 1073741824(0x40000000, float:2.0)
            if (r1 != r4) goto L_0x002f
            r1 = -2147483648(0xffffffff80000000, float:-0.0)
            int r1 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r1)
            r4 = 1
            goto L_0x0031
        L_0x002f:
            r1 = r9
            r4 = 0
        L_0x0031:
            super.onMeasure(r1, r10)
            boolean r1 = r8.mAllowStacking
            if (r1 == 0) goto L_0x0078
            boolean r1 = r8.isStacked()
            if (r1 != 0) goto L_0x0078
            int r1 = android.os.Build.VERSION.SDK_INT
            r5 = 11
            if (r1 < r5) goto L_0x0051
            int r0 = android.support.p000v4.view.ViewCompat.getMeasuredWidthAndState(r8)
            r1 = -16777216(0xffffffffff000000, float:-1.7014118E38)
            r0 = r0 & r1
            r1 = 16777216(0x1000000, float:2.3509887E-38)
            if (r0 != r1) goto L_0x0072
        L_0x004f:
            r2 = 1
            goto L_0x0072
        L_0x0051:
            int r1 = r8.getChildCount()
            r5 = 0
            r6 = 0
        L_0x0057:
            if (r5 >= r1) goto L_0x0065
            android.view.View r7 = r8.getChildAt(r5)
            int r7 = r7.getMeasuredWidth()
            int r6 = r6 + r7
            int r5 = r5 + 1
            goto L_0x0057
        L_0x0065:
            int r1 = r8.getPaddingLeft()
            int r6 = r6 + r1
            int r1 = r8.getPaddingRight()
            int r6 = r6 + r1
            if (r6 <= r0) goto L_0x0072
            goto L_0x004f
        L_0x0072:
            if (r2 == 0) goto L_0x0078
            r8.setStacked(r3)
            goto L_0x0079
        L_0x0078:
            r3 = r4
        L_0x0079:
            if (r3 == 0) goto L_0x007e
            super.onMeasure(r9, r10)
        L_0x007e:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.p003v7.widget.ButtonBarLayout.onMeasure(int, int):void");
    }

    private void setStacked(boolean z) {
        setOrientation(z ? 1 : 0);
        setGravity(z ? 5 : 80);
        View findViewById = findViewById(C0254R.C0256id.spacer);
        if (findViewById != null) {
            findViewById.setVisibility(z ? 8 : 4);
        }
        for (int childCount = getChildCount() - 2; childCount >= 0; childCount--) {
            bringChildToFront(getChildAt(childCount));
        }
    }

    private boolean isStacked() {
        return getOrientation() == 1;
    }
}
