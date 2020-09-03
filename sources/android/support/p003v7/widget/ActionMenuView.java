package android.support.p003v7.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.p003v7.view.menu.ActionMenuItemView;
import android.support.p003v7.view.menu.MenuBuilder;
import android.support.p003v7.view.menu.MenuBuilder.ItemInvoker;
import android.support.p003v7.view.menu.MenuItemImpl;
import android.support.p003v7.view.menu.MenuPresenter.Callback;
import android.support.p003v7.view.menu.MenuView;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewDebug.ExportedProperty;
import android.view.accessibility.AccessibilityEvent;

/* renamed from: android.support.v7.widget.ActionMenuView */
public class ActionMenuView extends LinearLayoutCompat implements ItemInvoker, MenuView {
    static final int GENERATED_ITEM_PADDING = 4;
    static final int MIN_CELL_SIZE = 56;
    private static final String TAG = "ActionMenuView";
    private Callback mActionMenuPresenterCallback;
    private boolean mFormatItems;
    private int mFormatItemsWidth;
    private int mGeneratedItemPadding;
    private MenuBuilder mMenu;
    /* access modifiers changed from: private */
    public MenuBuilder.Callback mMenuBuilderCallback;
    private int mMinCellSize;
    /* access modifiers changed from: private */
    public OnMenuItemClickListener mOnMenuItemClickListener;
    private Context mPopupContext;
    private int mPopupTheme;
    private ActionMenuPresenter mPresenter;
    private boolean mReserveOverflow;

    /* renamed from: android.support.v7.widget.ActionMenuView$ActionMenuChildView */
    public interface ActionMenuChildView {
        boolean needsDividerAfter();

        boolean needsDividerBefore();
    }

    /* renamed from: android.support.v7.widget.ActionMenuView$ActionMenuPresenterCallback */
    private class ActionMenuPresenterCallback implements Callback {
        public void onCloseMenu(MenuBuilder menuBuilder, boolean z) {
        }

        public boolean onOpenSubMenu(MenuBuilder menuBuilder) {
            return false;
        }

        private ActionMenuPresenterCallback() {
        }
    }

    /* renamed from: android.support.v7.widget.ActionMenuView$LayoutParams */
    public static class LayoutParams extends android.support.p003v7.widget.LinearLayoutCompat.LayoutParams {
        @ExportedProperty
        public int cellsUsed;
        @ExportedProperty
        public boolean expandable;
        boolean expanded;
        @ExportedProperty
        public int extraPixels;
        @ExportedProperty
        public boolean isOverflowButton;
        @ExportedProperty
        public boolean preventEdgeOffset;

        public LayoutParams(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
        }

        public LayoutParams(android.view.ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
        }

        public LayoutParams(LayoutParams layoutParams) {
            super((android.view.ViewGroup.LayoutParams) layoutParams);
            this.isOverflowButton = layoutParams.isOverflowButton;
        }

        public LayoutParams(int i, int i2) {
            super(i, i2);
            this.isOverflowButton = false;
        }

        LayoutParams(int i, int i2, boolean z) {
            super(i, i2);
            this.isOverflowButton = z;
        }
    }

    /* renamed from: android.support.v7.widget.ActionMenuView$MenuBuilderCallback */
    private class MenuBuilderCallback implements MenuBuilder.Callback {
        private MenuBuilderCallback() {
        }

        public boolean onMenuItemSelected(MenuBuilder menuBuilder, MenuItem menuItem) {
            return ActionMenuView.this.mOnMenuItemClickListener != null && ActionMenuView.this.mOnMenuItemClickListener.onMenuItemClick(menuItem);
        }

        public void onMenuModeChange(MenuBuilder menuBuilder) {
            if (ActionMenuView.this.mMenuBuilderCallback != null) {
                ActionMenuView.this.mMenuBuilderCallback.onMenuModeChange(menuBuilder);
            }
        }
    }

    /* renamed from: android.support.v7.widget.ActionMenuView$OnMenuItemClickListener */
    public interface OnMenuItemClickListener {
        boolean onMenuItemClick(MenuItem menuItem);
    }

    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        return false;
    }

    public int getWindowAnimations() {
        return 0;
    }

    public ActionMenuView(Context context) {
        this(context, null);
    }

    public ActionMenuView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setBaselineAligned(false);
        float f = context.getResources().getDisplayMetrics().density;
        this.mMinCellSize = (int) (56.0f * f);
        this.mGeneratedItemPadding = (int) (f * 4.0f);
        this.mPopupContext = context;
        this.mPopupTheme = 0;
    }

    public void setPopupTheme(@StyleRes int i) {
        if (this.mPopupTheme != i) {
            this.mPopupTheme = i;
            if (i == 0) {
                this.mPopupContext = getContext();
            } else {
                this.mPopupContext = new ContextThemeWrapper(getContext(), i);
            }
        }
    }

    public int getPopupTheme() {
        return this.mPopupTheme;
    }

    public void setPresenter(ActionMenuPresenter actionMenuPresenter) {
        this.mPresenter = actionMenuPresenter;
        this.mPresenter.setMenuView(this);
    }

    public void onConfigurationChanged(Configuration configuration) {
        if (VERSION.SDK_INT >= 8) {
            super.onConfigurationChanged(configuration);
        }
        ActionMenuPresenter actionMenuPresenter = this.mPresenter;
        if (actionMenuPresenter != null) {
            actionMenuPresenter.updateMenuView(false);
            if (this.mPresenter.isOverflowMenuShowing()) {
                this.mPresenter.hideOverflowMenu();
                this.mPresenter.showOverflowMenu();
            }
        }
    }

    public void setOnMenuItemClickListener(OnMenuItemClickListener onMenuItemClickListener) {
        this.mOnMenuItemClickListener = onMenuItemClickListener;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        boolean z = this.mFormatItems;
        this.mFormatItems = MeasureSpec.getMode(i) == 1073741824;
        if (z != this.mFormatItems) {
            this.mFormatItemsWidth = 0;
        }
        int size = MeasureSpec.getSize(i);
        if (this.mFormatItems) {
            MenuBuilder menuBuilder = this.mMenu;
            if (!(menuBuilder == null || size == this.mFormatItemsWidth)) {
                this.mFormatItemsWidth = size;
                menuBuilder.onItemsChanged(true);
            }
        }
        int childCount = getChildCount();
        if (!this.mFormatItems || childCount <= 0) {
            for (int i3 = 0; i3 < childCount; i3++) {
                LayoutParams layoutParams = (LayoutParams) getChildAt(i3).getLayoutParams();
                layoutParams.rightMargin = 0;
                layoutParams.leftMargin = 0;
            }
            super.onMeasure(i, i2);
            return;
        }
        onMeasureExactFormat(i, i2);
    }

    /* JADX WARNING: Removed duplicated region for block: B:135:0x0251 A[LOOP:5: B:135:0x0251->B:139:0x0270, LOOP_START, PHI: r13 
      PHI: (r13v3 int) = (r13v2 int), (r13v4 int) binds: [B:134:0x024f, B:139:0x0270] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARNING: Removed duplicated region for block: B:141:0x0275  */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x0278  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void onMeasureExactFormat(int r30, int r31) {
        /*
            r29 = this;
            r0 = r29
            int r1 = android.view.View.MeasureSpec.getMode(r31)
            int r2 = android.view.View.MeasureSpec.getSize(r30)
            int r3 = android.view.View.MeasureSpec.getSize(r31)
            int r4 = r29.getPaddingLeft()
            int r5 = r29.getPaddingRight()
            int r4 = r4 + r5
            int r5 = r29.getPaddingTop()
            int r6 = r29.getPaddingBottom()
            int r5 = r5 + r6
            r6 = -2
            r7 = r31
            int r6 = getChildMeasureSpec(r7, r5, r6)
            int r2 = r2 - r4
            int r4 = r0.mMinCellSize
            int r7 = r2 / r4
            int r8 = r2 % r4
            r9 = 0
            if (r7 != 0) goto L_0x0035
            r0.setMeasuredDimension(r2, r9)
            return
        L_0x0035:
            int r8 = r8 / r7
            int r4 = r4 + r8
            int r8 = r29.getChildCount()
            r14 = r7
            r7 = 0
            r10 = 0
            r12 = 0
            r13 = 0
            r15 = 0
            r16 = 0
            r17 = 0
        L_0x0045:
            if (r7 >= r8) goto L_0x00c5
            android.view.View r11 = r0.getChildAt(r7)
            int r9 = r11.getVisibility()
            r19 = r3
            r3 = 8
            if (r9 != r3) goto L_0x0057
            goto L_0x00bf
        L_0x0057:
            boolean r3 = r11 instanceof android.support.p003v7.view.menu.ActionMenuItemView
            int r13 = r13 + 1
            if (r3 == 0) goto L_0x0066
            int r9 = r0.mGeneratedItemPadding
            r20 = r13
            r13 = 0
            r11.setPadding(r9, r13, r9, r13)
            goto L_0x0069
        L_0x0066:
            r20 = r13
            r13 = 0
        L_0x0069:
            android.view.ViewGroup$LayoutParams r9 = r11.getLayoutParams()
            android.support.v7.widget.ActionMenuView$LayoutParams r9 = (android.support.p003v7.widget.ActionMenuView.LayoutParams) r9
            r9.expanded = r13
            r9.extraPixels = r13
            r9.cellsUsed = r13
            r9.expandable = r13
            r9.leftMargin = r13
            r9.rightMargin = r13
            if (r3 == 0) goto L_0x0088
            r3 = r11
            android.support.v7.view.menu.ActionMenuItemView r3 = (android.support.p003v7.view.menu.ActionMenuItemView) r3
            boolean r3 = r3.hasText()
            if (r3 == 0) goto L_0x0088
            r3 = 1
            goto L_0x0089
        L_0x0088:
            r3 = 0
        L_0x0089:
            r9.preventEdgeOffset = r3
            boolean r3 = r9.isOverflowButton
            if (r3 == 0) goto L_0x0091
            r3 = 1
            goto L_0x0092
        L_0x0091:
            r3 = r14
        L_0x0092:
            int r3 = measureChildForCells(r11, r4, r3, r6, r5)
            int r13 = java.lang.Math.max(r15, r3)
            boolean r15 = r9.expandable
            if (r15 == 0) goto L_0x00a0
            int r16 = r16 + 1
        L_0x00a0:
            boolean r9 = r9.isOverflowButton
            if (r9 == 0) goto L_0x00a5
            r12 = 1
        L_0x00a5:
            int r14 = r14 - r3
            int r9 = r11.getMeasuredHeight()
            int r10 = java.lang.Math.max(r10, r9)
            r9 = 1
            if (r3 != r9) goto L_0x00bb
            int r3 = r9 << r7
            r11 = r10
            long r9 = (long) r3
            long r9 = r17 | r9
            r17 = r9
            r10 = r11
            goto L_0x00bc
        L_0x00bb:
            r11 = r10
        L_0x00bc:
            r15 = r13
            r13 = r20
        L_0x00bf:
            int r7 = r7 + 1
            r3 = r19
            r9 = 0
            goto L_0x0045
        L_0x00c5:
            r19 = r3
            r3 = 2
            if (r12 == 0) goto L_0x00ce
            if (r13 != r3) goto L_0x00ce
            r5 = 1
            goto L_0x00cf
        L_0x00ce:
            r5 = 0
        L_0x00cf:
            r7 = 0
        L_0x00d0:
            if (r16 <= 0) goto L_0x0182
            if (r14 <= 0) goto L_0x0182
            r9 = 2147483647(0x7fffffff, float:NaN)
            r3 = 2147483647(0x7fffffff, float:NaN)
            r9 = 0
            r11 = 0
            r20 = 0
        L_0x00de:
            if (r9 >= r8) goto L_0x0124
            android.view.View r22 = r0.getChildAt(r9)
            android.view.ViewGroup$LayoutParams r22 = r22.getLayoutParams()
            r23 = r7
            r7 = r22
            android.support.v7.widget.ActionMenuView$LayoutParams r7 = (android.support.p003v7.widget.ActionMenuView.LayoutParams) r7
            r22 = r10
            boolean r10 = r7.expandable
            if (r10 != 0) goto L_0x00f8
        L_0x00f4:
            r7 = r1
            r24 = r2
            goto L_0x011a
        L_0x00f8:
            int r10 = r7.cellsUsed
            if (r10 >= r3) goto L_0x0109
            int r3 = r7.cellsUsed
            r10 = 1
            int r7 = r10 << r9
            long r10 = (long) r7
            r7 = r1
            r24 = r2
            r20 = r10
            r11 = 1
            goto L_0x011a
        L_0x0109:
            int r7 = r7.cellsUsed
            if (r7 != r3) goto L_0x00f4
            r7 = 1
            int r10 = r7 << r9
            r7 = r1
            r24 = r2
            long r1 = (long) r10
            long r1 = r20 | r1
            int r11 = r11 + 1
            r20 = r1
        L_0x011a:
            int r9 = r9 + 1
            r1 = r7
            r10 = r22
            r7 = r23
            r2 = r24
            goto L_0x00de
        L_0x0124:
            r24 = r2
            r23 = r7
            r22 = r10
            r7 = r1
            long r17 = r17 | r20
            if (r11 <= r14) goto L_0x0130
            goto L_0x0189
        L_0x0130:
            int r3 = r3 + 1
            r1 = 0
        L_0x0133:
            if (r1 >= r8) goto L_0x0179
            android.view.View r2 = r0.getChildAt(r1)
            android.view.ViewGroup$LayoutParams r9 = r2.getLayoutParams()
            android.support.v7.widget.ActionMenuView$LayoutParams r9 = (android.support.p003v7.widget.ActionMenuView.LayoutParams) r9
            r10 = 1
            int r11 = r10 << r1
            long r10 = (long) r11
            long r25 = r20 & r10
            r27 = 0
            int r23 = (r25 > r27 ? 1 : (r25 == r27 ? 0 : -1))
            if (r23 != 0) goto L_0x0154
            int r2 = r9.cellsUsed
            if (r2 != r3) goto L_0x0151
            long r17 = r17 | r10
        L_0x0151:
            r23 = r3
            goto L_0x0174
        L_0x0154:
            if (r5 == 0) goto L_0x0168
            boolean r10 = r9.preventEdgeOffset
            if (r10 == 0) goto L_0x0168
            r10 = 1
            if (r14 != r10) goto L_0x0168
            int r11 = r0.mGeneratedItemPadding
            int r10 = r11 + r4
            r23 = r3
            r3 = 0
            r2.setPadding(r10, r3, r11, r3)
            goto L_0x016a
        L_0x0168:
            r23 = r3
        L_0x016a:
            int r2 = r9.cellsUsed
            r3 = 1
            int r2 = r2 + r3
            r9.cellsUsed = r2
            r9.expanded = r3
            int r14 = r14 + -1
        L_0x0174:
            int r1 = r1 + 1
            r3 = r23
            goto L_0x0133
        L_0x0179:
            r1 = r7
            r10 = r22
            r2 = r24
            r3 = 2
            r7 = 1
            goto L_0x00d0
        L_0x0182:
            r24 = r2
            r23 = r7
            r22 = r10
            r7 = r1
        L_0x0189:
            if (r12 != 0) goto L_0x0190
            r1 = 1
            if (r13 != r1) goto L_0x0191
            r2 = 1
            goto L_0x0192
        L_0x0190:
            r1 = 1
        L_0x0191:
            r2 = 0
        L_0x0192:
            if (r14 <= 0) goto L_0x024c
            r9 = 0
            int r3 = (r17 > r9 ? 1 : (r17 == r9 ? 0 : -1))
            if (r3 == 0) goto L_0x024c
            int r13 = r13 - r1
            if (r14 < r13) goto L_0x01a1
            if (r2 != 0) goto L_0x01a1
            if (r15 <= r1) goto L_0x024c
        L_0x01a1:
            int r1 = java.lang.Long.bitCount(r17)
            float r1 = (float) r1
            if (r2 != 0) goto L_0x01e4
            r2 = 1
            long r2 = r17 & r2
            r5 = 1056964608(0x3f000000, float:0.5)
            r9 = 0
            int r11 = (r2 > r9 ? 1 : (r2 == r9 ? 0 : -1))
            if (r11 == 0) goto L_0x01c5
            r13 = 0
            android.view.View r2 = r0.getChildAt(r13)
            android.view.ViewGroup$LayoutParams r2 = r2.getLayoutParams()
            android.support.v7.widget.ActionMenuView$LayoutParams r2 = (android.support.p003v7.widget.ActionMenuView.LayoutParams) r2
            boolean r2 = r2.preventEdgeOffset
            if (r2 != 0) goto L_0x01c6
            float r1 = r1 - r5
            goto L_0x01c6
        L_0x01c5:
            r13 = 0
        L_0x01c6:
            int r2 = r8 + -1
            r3 = 1
            int r9 = r3 << r2
            long r9 = (long) r9
            long r9 = r17 & r9
            r11 = 0
            int r3 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1))
            if (r3 == 0) goto L_0x01e5
            android.view.View r2 = r0.getChildAt(r2)
            android.view.ViewGroup$LayoutParams r2 = r2.getLayoutParams()
            android.support.v7.widget.ActionMenuView$LayoutParams r2 = (android.support.p003v7.widget.ActionMenuView.LayoutParams) r2
            boolean r2 = r2.preventEdgeOffset
            if (r2 != 0) goto L_0x01e5
            float r1 = r1 - r5
            goto L_0x01e5
        L_0x01e4:
            r13 = 0
        L_0x01e5:
            r2 = 0
            int r2 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r2 <= 0) goto L_0x01f0
            int r14 = r14 * r4
            float r2 = (float) r14
            float r2 = r2 / r1
            int r9 = (int) r2
            goto L_0x01f1
        L_0x01f0:
            r9 = 0
        L_0x01f1:
            r11 = r23
            r1 = 0
        L_0x01f4:
            if (r1 >= r8) goto L_0x0249
            r2 = 1
            int r3 = r2 << r1
            long r2 = (long) r3
            long r2 = r17 & r2
            r14 = 0
            int r5 = (r2 > r14 ? 1 : (r2 == r14 ? 0 : -1))
            if (r5 != 0) goto L_0x0205
            r2 = 1
            r5 = 2
            goto L_0x0246
        L_0x0205:
            android.view.View r2 = r0.getChildAt(r1)
            android.view.ViewGroup$LayoutParams r3 = r2.getLayoutParams()
            android.support.v7.widget.ActionMenuView$LayoutParams r3 = (android.support.p003v7.widget.ActionMenuView.LayoutParams) r3
            boolean r2 = r2 instanceof android.support.p003v7.view.menu.ActionMenuItemView
            if (r2 == 0) goto L_0x0227
            r3.extraPixels = r9
            r2 = 1
            r3.expanded = r2
            if (r1 != 0) goto L_0x0224
            boolean r2 = r3.preventEdgeOffset
            if (r2 != 0) goto L_0x0224
            int r2 = -r9
            r5 = 2
            int r2 = r2 / r5
            r3.leftMargin = r2
            goto L_0x0225
        L_0x0224:
            r5 = 2
        L_0x0225:
            r2 = 1
            goto L_0x0235
        L_0x0227:
            r5 = 2
            boolean r2 = r3.isOverflowButton
            if (r2 == 0) goto L_0x0237
            r3.extraPixels = r9
            r2 = 1
            r3.expanded = r2
            int r10 = -r9
            int r10 = r10 / r5
            r3.rightMargin = r10
        L_0x0235:
            r11 = 1
            goto L_0x0246
        L_0x0237:
            r2 = 1
            if (r1 == 0) goto L_0x023e
            int r10 = r9 / 2
            r3.leftMargin = r10
        L_0x023e:
            int r10 = r8 + -1
            if (r1 == r10) goto L_0x0246
            int r10 = r9 / 2
            r3.rightMargin = r10
        L_0x0246:
            int r1 = r1 + 1
            goto L_0x01f4
        L_0x0249:
            r23 = r11
            goto L_0x024d
        L_0x024c:
            r13 = 0
        L_0x024d:
            r1 = 1073741824(0x40000000, float:2.0)
            if (r23 == 0) goto L_0x0273
        L_0x0251:
            if (r13 >= r8) goto L_0x0273
            android.view.View r2 = r0.getChildAt(r13)
            android.view.ViewGroup$LayoutParams r3 = r2.getLayoutParams()
            android.support.v7.widget.ActionMenuView$LayoutParams r3 = (android.support.p003v7.widget.ActionMenuView.LayoutParams) r3
            boolean r5 = r3.expanded
            if (r5 != 0) goto L_0x0262
            goto L_0x0270
        L_0x0262:
            int r5 = r3.cellsUsed
            int r5 = r5 * r4
            int r3 = r3.extraPixels
            int r5 = r5 + r3
            int r3 = android.view.View.MeasureSpec.makeMeasureSpec(r5, r1)
            r2.measure(r3, r6)
        L_0x0270:
            int r13 = r13 + 1
            goto L_0x0251
        L_0x0273:
            if (r7 == r1) goto L_0x0278
            r1 = r22
            goto L_0x027a
        L_0x0278:
            r1 = r19
        L_0x027a:
            r2 = r24
            r0.setMeasuredDimension(r2, r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.p003v7.widget.ActionMenuView.onMeasureExactFormat(int, int):void");
    }

    static int measureChildForCells(View view, int i, int i2, int i3, int i4) {
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        int makeMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i3) - i4, MeasureSpec.getMode(i3));
        ActionMenuItemView actionMenuItemView = view instanceof ActionMenuItemView ? (ActionMenuItemView) view : null;
        boolean z = true;
        boolean z2 = actionMenuItemView != null && actionMenuItemView.hasText();
        int i5 = 2;
        if (i2 <= 0 || (z2 && i2 < 2)) {
            i5 = 0;
        } else {
            view.measure(MeasureSpec.makeMeasureSpec(i2 * i, Integer.MIN_VALUE), makeMeasureSpec);
            int measuredWidth = view.getMeasuredWidth();
            int i6 = measuredWidth / i;
            if (measuredWidth % i != 0) {
                i6++;
            }
            if (!z2 || i6 >= 2) {
                i5 = i6;
            }
        }
        if (layoutParams.isOverflowButton || !z2) {
            z = false;
        }
        layoutParams.expandable = z;
        layoutParams.cellsUsed = i5;
        view.measure(MeasureSpec.makeMeasureSpec(i * i5, 1073741824), makeMeasureSpec);
        return i5;
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5;
        int i6;
        int i7;
        int i8;
        if (!this.mFormatItems) {
            super.onLayout(z, i, i2, i3, i4);
            return;
        }
        int childCount = getChildCount();
        int i9 = (i4 - i2) / 2;
        int dividerWidth = getDividerWidth();
        int i10 = i3 - i;
        int paddingRight = (i10 - getPaddingRight()) - getPaddingLeft();
        boolean isLayoutRtl = ViewUtils.isLayoutRtl(this);
        int i11 = paddingRight;
        int i12 = 0;
        int i13 = 0;
        for (int i14 = 0; i14 < childCount; i14++) {
            View childAt = getChildAt(i14);
            if (childAt.getVisibility() != 8) {
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                if (layoutParams.isOverflowButton) {
                    int measuredWidth = childAt.getMeasuredWidth();
                    if (hasSupportDividerBeforeChildAt(i14)) {
                        measuredWidth += dividerWidth;
                    }
                    int measuredHeight = childAt.getMeasuredHeight();
                    if (isLayoutRtl) {
                        i7 = getPaddingLeft() + layoutParams.leftMargin;
                        i8 = i7 + measuredWidth;
                    } else {
                        i8 = (getWidth() - getPaddingRight()) - layoutParams.rightMargin;
                        i7 = i8 - measuredWidth;
                    }
                    int i15 = i9 - (measuredHeight / 2);
                    childAt.layout(i7, i15, i8, measuredHeight + i15);
                    i11 -= measuredWidth;
                    i12 = 1;
                } else {
                    i11 -= (childAt.getMeasuredWidth() + layoutParams.leftMargin) + layoutParams.rightMargin;
                    boolean hasSupportDividerBeforeChildAt = hasSupportDividerBeforeChildAt(i14);
                    i13++;
                }
            }
        }
        if (childCount == 1 && i12 == 0) {
            View childAt2 = getChildAt(0);
            int measuredWidth2 = childAt2.getMeasuredWidth();
            int measuredHeight2 = childAt2.getMeasuredHeight();
            int i16 = (i10 / 2) - (measuredWidth2 / 2);
            int i17 = i9 - (measuredHeight2 / 2);
            childAt2.layout(i16, i17, measuredWidth2 + i16, measuredHeight2 + i17);
            return;
        }
        int i18 = i13 - (i12 ^ 1);
        if (i18 > 0) {
            i5 = i11 / i18;
            i6 = 0;
        } else {
            i6 = 0;
            i5 = 0;
        }
        int max = Math.max(i6, i5);
        if (isLayoutRtl) {
            int width = getWidth() - getPaddingRight();
            while (i6 < childCount) {
                View childAt3 = getChildAt(i6);
                LayoutParams layoutParams2 = (LayoutParams) childAt3.getLayoutParams();
                if (childAt3.getVisibility() != 8 && !layoutParams2.isOverflowButton) {
                    int i19 = width - layoutParams2.rightMargin;
                    int measuredWidth3 = childAt3.getMeasuredWidth();
                    int measuredHeight3 = childAt3.getMeasuredHeight();
                    int i20 = i9 - (measuredHeight3 / 2);
                    childAt3.layout(i19 - measuredWidth3, i20, i19, measuredHeight3 + i20);
                    width = i19 - ((measuredWidth3 + layoutParams2.leftMargin) + max);
                }
                i6++;
            }
        } else {
            int paddingLeft = getPaddingLeft();
            while (i6 < childCount) {
                View childAt4 = getChildAt(i6);
                LayoutParams layoutParams3 = (LayoutParams) childAt4.getLayoutParams();
                if (childAt4.getVisibility() != 8 && !layoutParams3.isOverflowButton) {
                    int i21 = paddingLeft + layoutParams3.leftMargin;
                    int measuredWidth4 = childAt4.getMeasuredWidth();
                    int measuredHeight4 = childAt4.getMeasuredHeight();
                    int i22 = i9 - (measuredHeight4 / 2);
                    childAt4.layout(i21, i22, i21 + measuredWidth4, measuredHeight4 + i22);
                    paddingLeft = i21 + measuredWidth4 + layoutParams3.rightMargin + max;
                }
                i6++;
            }
        }
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        dismissPopupMenus();
    }

    public void setOverflowIcon(@Nullable Drawable drawable) {
        getMenu();
        this.mPresenter.setOverflowIcon(drawable);
    }

    @Nullable
    public Drawable getOverflowIcon() {
        getMenu();
        return this.mPresenter.getOverflowIcon();
    }

    public boolean isOverflowReserved() {
        return this.mReserveOverflow;
    }

    public void setOverflowReserved(boolean z) {
        this.mReserveOverflow = z;
    }

    /* access modifiers changed from: protected */
    public LayoutParams generateDefaultLayoutParams() {
        LayoutParams layoutParams = new LayoutParams(-2, -2);
        layoutParams.gravity = 16;
        return layoutParams;
    }

    public LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return new LayoutParams(getContext(), attributeSet);
    }

    /* access modifiers changed from: protected */
    public LayoutParams generateLayoutParams(android.view.ViewGroup.LayoutParams layoutParams) {
        if (layoutParams == null) {
            return generateDefaultLayoutParams();
        }
        LayoutParams layoutParams2 = layoutParams instanceof LayoutParams ? new LayoutParams((LayoutParams) layoutParams) : new LayoutParams(layoutParams);
        if (layoutParams2.gravity <= 0) {
            layoutParams2.gravity = 16;
        }
        return layoutParams2;
    }

    /* access modifiers changed from: protected */
    public boolean checkLayoutParams(android.view.ViewGroup.LayoutParams layoutParams) {
        return layoutParams != null && (layoutParams instanceof LayoutParams);
    }

    public LayoutParams generateOverflowButtonLayoutParams() {
        LayoutParams generateDefaultLayoutParams = generateDefaultLayoutParams();
        generateDefaultLayoutParams.isOverflowButton = true;
        return generateDefaultLayoutParams;
    }

    public boolean invokeItem(MenuItemImpl menuItemImpl) {
        return this.mMenu.performItemAction(menuItemImpl, 0);
    }

    public void initialize(MenuBuilder menuBuilder) {
        this.mMenu = menuBuilder;
    }

    public Menu getMenu() {
        if (this.mMenu == null) {
            Context context = getContext();
            this.mMenu = new MenuBuilder(context);
            this.mMenu.setCallback(new MenuBuilderCallback());
            this.mPresenter = new ActionMenuPresenter(context);
            this.mPresenter.setReserveOverflow(true);
            ActionMenuPresenter actionMenuPresenter = this.mPresenter;
            Callback callback = this.mActionMenuPresenterCallback;
            if (callback == null) {
                callback = new ActionMenuPresenterCallback();
            }
            actionMenuPresenter.setCallback(callback);
            this.mMenu.addMenuPresenter(this.mPresenter, this.mPopupContext);
            this.mPresenter.setMenuView(this);
        }
        return this.mMenu;
    }

    public void setMenuCallbacks(Callback callback, MenuBuilder.Callback callback2) {
        this.mActionMenuPresenterCallback = callback;
        this.mMenuBuilderCallback = callback2;
    }

    public MenuBuilder peekMenu() {
        return this.mMenu;
    }

    public boolean showOverflowMenu() {
        ActionMenuPresenter actionMenuPresenter = this.mPresenter;
        return actionMenuPresenter != null && actionMenuPresenter.showOverflowMenu();
    }

    public boolean hideOverflowMenu() {
        ActionMenuPresenter actionMenuPresenter = this.mPresenter;
        return actionMenuPresenter != null && actionMenuPresenter.hideOverflowMenu();
    }

    public boolean isOverflowMenuShowing() {
        ActionMenuPresenter actionMenuPresenter = this.mPresenter;
        return actionMenuPresenter != null && actionMenuPresenter.isOverflowMenuShowing();
    }

    public boolean isOverflowMenuShowPending() {
        ActionMenuPresenter actionMenuPresenter = this.mPresenter;
        return actionMenuPresenter != null && actionMenuPresenter.isOverflowMenuShowPending();
    }

    public void dismissPopupMenus() {
        ActionMenuPresenter actionMenuPresenter = this.mPresenter;
        if (actionMenuPresenter != null) {
            actionMenuPresenter.dismissPopupMenus();
        }
    }

    /* access modifiers changed from: protected */
    public boolean hasSupportDividerBeforeChildAt(int i) {
        boolean z = false;
        if (i == 0) {
            return false;
        }
        View childAt = getChildAt(i - 1);
        View childAt2 = getChildAt(i);
        if (i < getChildCount() && (childAt instanceof ActionMenuChildView)) {
            z = false | ((ActionMenuChildView) childAt).needsDividerAfter();
        }
        if (i > 0 && (childAt2 instanceof ActionMenuChildView)) {
            z |= ((ActionMenuChildView) childAt2).needsDividerBefore();
        }
        return z;
    }

    public void setExpandedActionViewsExclusive(boolean z) {
        this.mPresenter.setExpandedActionViewsExclusive(z);
    }
}
