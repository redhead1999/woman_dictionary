package android.support.design.widget;

import android.content.Context;
import android.support.p000v4.view.ViewCompat;
import android.support.p000v4.widget.ScrollerCompat;
import android.util.AttributeSet;
import android.view.VelocityTracker;
import android.view.View;

abstract class HeaderBehavior<V extends View> extends ViewOffsetBehavior<V> {
    private static final int INVALID_POINTER = -1;
    private int mActivePointerId = -1;
    private Runnable mFlingRunnable;
    private boolean mIsBeingDragged;
    private int mLastMotionY;
    /* access modifiers changed from: private */
    public ScrollerCompat mScroller;
    private int mTouchSlop = -1;
    private VelocityTracker mVelocityTracker;

    private class FlingRunnable implements Runnable {
        private final V mLayout;
        private final CoordinatorLayout mParent;

        FlingRunnable(CoordinatorLayout coordinatorLayout, V v) {
            this.mParent = coordinatorLayout;
            this.mLayout = v;
        }

        public void run() {
            if (this.mLayout != null && HeaderBehavior.this.mScroller != null) {
                if (HeaderBehavior.this.mScroller.computeScrollOffset()) {
                    HeaderBehavior headerBehavior = HeaderBehavior.this;
                    headerBehavior.setHeaderTopBottomOffset(this.mParent, this.mLayout, headerBehavior.mScroller.getCurrY());
                    ViewCompat.postOnAnimation(this.mLayout, this);
                    return;
                }
                HeaderBehavior.this.onFlingFinished(this.mParent, this.mLayout);
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public boolean canDragView(V v) {
        return false;
    }

    /* access modifiers changed from: 0000 */
    public void onFlingFinished(CoordinatorLayout coordinatorLayout, V v) {
    }

    public HeaderBehavior() {
    }

    public HeaderBehavior(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x002c, code lost:
        if (r0 != 3) goto L_0x0083;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onInterceptTouchEvent(android.support.design.widget.CoordinatorLayout r5, V r6, android.view.MotionEvent r7) {
        /*
            r4 = this;
            int r0 = r4.mTouchSlop
            if (r0 >= 0) goto L_0x0012
            android.content.Context r0 = r5.getContext()
            android.view.ViewConfiguration r0 = android.view.ViewConfiguration.get(r0)
            int r0 = r0.getScaledTouchSlop()
            r4.mTouchSlop = r0
        L_0x0012:
            int r0 = r7.getAction()
            r1 = 2
            r2 = 1
            if (r0 != r1) goto L_0x001f
            boolean r0 = r4.mIsBeingDragged
            if (r0 == 0) goto L_0x001f
            return r2
        L_0x001f:
            int r0 = android.support.p000v4.view.MotionEventCompat.getActionMasked(r7)
            r3 = 0
            if (r0 == 0) goto L_0x0060
            r5 = -1
            if (r0 == r2) goto L_0x0051
            if (r0 == r1) goto L_0x002f
            r6 = 3
            if (r0 == r6) goto L_0x0051
            goto L_0x0083
        L_0x002f:
            int r6 = r4.mActivePointerId
            if (r6 != r5) goto L_0x0034
            goto L_0x0083
        L_0x0034:
            int r6 = android.support.p000v4.view.MotionEventCompat.findPointerIndex(r7, r6)
            if (r6 != r5) goto L_0x003b
            goto L_0x0083
        L_0x003b:
            float r5 = android.support.p000v4.view.MotionEventCompat.getY(r7, r6)
            int r5 = (int) r5
            int r6 = r4.mLastMotionY
            int r6 = r5 - r6
            int r6 = java.lang.Math.abs(r6)
            int r0 = r4.mTouchSlop
            if (r6 <= r0) goto L_0x0083
            r4.mIsBeingDragged = r2
            r4.mLastMotionY = r5
            goto L_0x0083
        L_0x0051:
            r4.mIsBeingDragged = r3
            r4.mActivePointerId = r5
            android.view.VelocityTracker r5 = r4.mVelocityTracker
            if (r5 == 0) goto L_0x0083
            r5.recycle()
            r5 = 0
            r4.mVelocityTracker = r5
            goto L_0x0083
        L_0x0060:
            r4.mIsBeingDragged = r3
            float r0 = r7.getX()
            int r0 = (int) r0
            float r1 = r7.getY()
            int r1 = (int) r1
            boolean r2 = r4.canDragView(r6)
            if (r2 == 0) goto L_0x0083
            boolean r5 = r5.isPointInChildBounds(r6, r0, r1)
            if (r5 == 0) goto L_0x0083
            r4.mLastMotionY = r1
            int r5 = android.support.p000v4.view.MotionEventCompat.getPointerId(r7, r3)
            r4.mActivePointerId = r5
            r4.ensureVelocityTracker()
        L_0x0083:
            android.view.VelocityTracker r5 = r4.mVelocityTracker
            if (r5 == 0) goto L_0x008a
            r5.addMovement(r7)
        L_0x008a:
            boolean r5 = r4.mIsBeingDragged
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.design.widget.HeaderBehavior.onInterceptTouchEvent(android.support.design.widget.CoordinatorLayout, android.view.View, android.view.MotionEvent):boolean");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0021, code lost:
        if (r0 != 3) goto L_0x00ae;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.support.design.widget.CoordinatorLayout r12, V r13, android.view.MotionEvent r14) {
        /*
            r11 = this;
            int r0 = r11.mTouchSlop
            if (r0 >= 0) goto L_0x0012
            android.content.Context r0 = r12.getContext()
            android.view.ViewConfiguration r0 = android.view.ViewConfiguration.get(r0)
            int r0 = r0.getScaledTouchSlop()
            r11.mTouchSlop = r0
        L_0x0012:
            int r0 = android.support.p000v4.view.MotionEventCompat.getActionMasked(r14)
            r1 = 1
            r2 = 0
            if (r0 == 0) goto L_0x008d
            r3 = -1
            if (r0 == r1) goto L_0x005c
            r4 = 2
            if (r0 == r4) goto L_0x0025
            r12 = 3
            if (r0 == r12) goto L_0x007e
            goto L_0x00ae
        L_0x0025:
            int r0 = r11.mActivePointerId
            int r0 = android.support.p000v4.view.MotionEventCompat.findPointerIndex(r14, r0)
            if (r0 != r3) goto L_0x002e
            return r2
        L_0x002e:
            float r0 = android.support.p000v4.view.MotionEventCompat.getY(r14, r0)
            int r0 = (int) r0
            int r2 = r11.mLastMotionY
            int r2 = r2 - r0
            boolean r3 = r11.mIsBeingDragged
            if (r3 != 0) goto L_0x0049
            int r3 = java.lang.Math.abs(r2)
            int r4 = r11.mTouchSlop
            if (r3 <= r4) goto L_0x0049
            r11.mIsBeingDragged = r1
            if (r2 <= 0) goto L_0x0048
            int r2 = r2 - r4
            goto L_0x0049
        L_0x0048:
            int r2 = r2 + r4
        L_0x0049:
            r6 = r2
            boolean r2 = r11.mIsBeingDragged
            if (r2 == 0) goto L_0x00ae
            r11.mLastMotionY = r0
            int r7 = r11.getMaxDragOffset(r13)
            r8 = 0
            r3 = r11
            r4 = r12
            r5 = r13
            r3.scroll(r4, r5, r6, r7, r8)
            goto L_0x00ae
        L_0x005c:
            android.view.VelocityTracker r0 = r11.mVelocityTracker
            if (r0 == 0) goto L_0x007e
            r0.addMovement(r14)
            android.view.VelocityTracker r0 = r11.mVelocityTracker
            r4 = 1000(0x3e8, float:1.401E-42)
            r0.computeCurrentVelocity(r4)
            android.view.VelocityTracker r0 = r11.mVelocityTracker
            int r4 = r11.mActivePointerId
            float r10 = android.support.p000v4.view.VelocityTrackerCompat.getYVelocity(r0, r4)
            int r0 = r11.getScrollRangeForDragFling(r13)
            int r8 = -r0
            r9 = 0
            r5 = r11
            r6 = r12
            r7 = r13
            r5.fling(r6, r7, r8, r9, r10)
        L_0x007e:
            r11.mIsBeingDragged = r2
            r11.mActivePointerId = r3
            android.view.VelocityTracker r12 = r11.mVelocityTracker
            if (r12 == 0) goto L_0x00ae
            r12.recycle()
            r12 = 0
            r11.mVelocityTracker = r12
            goto L_0x00ae
        L_0x008d:
            float r0 = r14.getX()
            int r0 = (int) r0
            float r3 = r14.getY()
            int r3 = (int) r3
            boolean r12 = r12.isPointInChildBounds(r13, r0, r3)
            if (r12 == 0) goto L_0x00b6
            boolean r12 = r11.canDragView(r13)
            if (r12 == 0) goto L_0x00b6
            r11.mLastMotionY = r3
            int r12 = android.support.p000v4.view.MotionEventCompat.getPointerId(r14, r2)
            r11.mActivePointerId = r12
            r11.ensureVelocityTracker()
        L_0x00ae:
            android.view.VelocityTracker r12 = r11.mVelocityTracker
            if (r12 == 0) goto L_0x00b5
            r12.addMovement(r14)
        L_0x00b5:
            return r1
        L_0x00b6:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.design.widget.HeaderBehavior.onTouchEvent(android.support.design.widget.CoordinatorLayout, android.view.View, android.view.MotionEvent):boolean");
    }

    /* access modifiers changed from: 0000 */
    public int setHeaderTopBottomOffset(CoordinatorLayout coordinatorLayout, V v, int i) {
        return setHeaderTopBottomOffset(coordinatorLayout, v, i, Integer.MIN_VALUE, ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED);
    }

    /* access modifiers changed from: 0000 */
    public int setHeaderTopBottomOffset(CoordinatorLayout coordinatorLayout, V v, int i, int i2, int i3) {
        int topAndBottomOffset = getTopAndBottomOffset();
        if (i2 != 0 && topAndBottomOffset >= i2 && topAndBottomOffset <= i3) {
            int constrain = MathUtils.constrain(i, i2, i3);
            if (topAndBottomOffset != constrain) {
                setTopAndBottomOffset(constrain);
                return topAndBottomOffset - constrain;
            }
        }
        return 0;
    }

    /* access modifiers changed from: 0000 */
    public int getTopBottomOffsetForScrollingSibling() {
        return getTopAndBottomOffset();
    }

    /* access modifiers changed from: 0000 */
    public final int scroll(CoordinatorLayout coordinatorLayout, V v, int i, int i2, int i3) {
        return setHeaderTopBottomOffset(coordinatorLayout, v, getTopBottomOffsetForScrollingSibling() - i, i2, i3);
    }

    /* access modifiers changed from: 0000 */
    public final boolean fling(CoordinatorLayout coordinatorLayout, V v, int i, int i2, float f) {
        Runnable runnable = this.mFlingRunnable;
        if (runnable != null) {
            v.removeCallbacks(runnable);
            this.mFlingRunnable = null;
        }
        if (this.mScroller == null) {
            this.mScroller = ScrollerCompat.create(v.getContext());
        }
        this.mScroller.fling(0, getTopAndBottomOffset(), 0, Math.round(f), 0, 0, i, i2);
        if (this.mScroller.computeScrollOffset()) {
            this.mFlingRunnable = new FlingRunnable(coordinatorLayout, v);
            ViewCompat.postOnAnimation(v, this.mFlingRunnable);
            return true;
        }
        onFlingFinished(coordinatorLayout, v);
        return false;
    }

    /* access modifiers changed from: 0000 */
    public int getMaxDragOffset(V v) {
        return -v.getHeight();
    }

    /* access modifiers changed from: 0000 */
    public int getScrollRangeForDragFling(V v) {
        return v.getHeight();
    }

    private void ensureVelocityTracker() {
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
    }
}
