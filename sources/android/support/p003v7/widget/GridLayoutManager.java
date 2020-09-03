package android.support.p003v7.widget;

import android.content.Context;
import android.graphics.Rect;
import android.support.p000v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.p000v4.view.accessibility.AccessibilityNodeInfoCompat.CollectionItemInfoCompat;
import android.support.p003v7.widget.RecyclerView.Recycler;
import android.support.p003v7.widget.RecyclerView.State;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.MarginLayoutParams;
import java.util.Arrays;

/* renamed from: android.support.v7.widget.GridLayoutManager */
public class GridLayoutManager extends LinearLayoutManager {
    private static final boolean DEBUG = false;
    public static final int DEFAULT_SPAN_COUNT = -1;
    private static final String TAG = "GridLayoutManager";
    int[] mCachedBorders;
    final Rect mDecorInsets = new Rect();
    boolean mPendingSpanCountChange = false;
    final SparseIntArray mPreLayoutSpanIndexCache = new SparseIntArray();
    final SparseIntArray mPreLayoutSpanSizeCache = new SparseIntArray();
    View[] mSet;
    int mSpanCount = -1;
    SpanSizeLookup mSpanSizeLookup = new DefaultSpanSizeLookup();

    /* renamed from: android.support.v7.widget.GridLayoutManager$DefaultSpanSizeLookup */
    public static final class DefaultSpanSizeLookup extends SpanSizeLookup {
        public int getSpanSize(int i) {
            return 1;
        }

        public int getSpanIndex(int i, int i2) {
            return i % i2;
        }
    }

    /* renamed from: android.support.v7.widget.GridLayoutManager$LayoutParams */
    public static class LayoutParams extends android.support.p003v7.widget.RecyclerView.LayoutParams {
        public static final int INVALID_SPAN_ID = -1;
        /* access modifiers changed from: private */
        public int mSpanIndex = -1;
        /* access modifiers changed from: private */
        public int mSpanSize = 0;

        public LayoutParams(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
        }

        public LayoutParams(int i, int i2) {
            super(i, i2);
        }

        public LayoutParams(MarginLayoutParams marginLayoutParams) {
            super(marginLayoutParams);
        }

        public LayoutParams(android.view.ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
        }

        public LayoutParams(android.support.p003v7.widget.RecyclerView.LayoutParams layoutParams) {
            super(layoutParams);
        }

        public int getSpanIndex() {
            return this.mSpanIndex;
        }

        public int getSpanSize() {
            return this.mSpanSize;
        }
    }

    /* renamed from: android.support.v7.widget.GridLayoutManager$SpanSizeLookup */
    public static abstract class SpanSizeLookup {
        private boolean mCacheSpanIndices = false;
        final SparseIntArray mSpanIndexCache = new SparseIntArray();

        public abstract int getSpanSize(int i);

        public void setSpanIndexCacheEnabled(boolean z) {
            this.mCacheSpanIndices = z;
        }

        public void invalidateSpanIndexCache() {
            this.mSpanIndexCache.clear();
        }

        public boolean isSpanIndexCacheEnabled() {
            return this.mCacheSpanIndices;
        }

        /* access modifiers changed from: 0000 */
        public int getCachedSpanIndex(int i, int i2) {
            if (!this.mCacheSpanIndices) {
                return getSpanIndex(i, i2);
            }
            int i3 = this.mSpanIndexCache.get(i, -1);
            if (i3 != -1) {
                return i3;
            }
            int spanIndex = getSpanIndex(i, i2);
            this.mSpanIndexCache.put(i, spanIndex);
            return spanIndex;
        }

        /* JADX WARNING: Removed duplicated region for block: B:12:0x002a  */
        /* JADX WARNING: Removed duplicated region for block: B:20:0x003c A[RETURN] */
        /* JADX WARNING: Removed duplicated region for block: B:21:0x003d A[RETURN] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public int getSpanIndex(int r6, int r7) {
            /*
                r5 = this;
                int r0 = r5.getSpanSize(r6)
                r1 = 0
                if (r0 != r7) goto L_0x0008
                return r1
            L_0x0008:
                boolean r2 = r5.mCacheSpanIndices
                if (r2 == 0) goto L_0x0026
                android.util.SparseIntArray r2 = r5.mSpanIndexCache
                int r2 = r2.size()
                if (r2 <= 0) goto L_0x0026
                int r2 = r5.findReferenceIndexFromCache(r6)
                if (r2 < 0) goto L_0x0026
                android.util.SparseIntArray r3 = r5.mSpanIndexCache
                int r3 = r3.get(r2)
                int r4 = r5.getSpanSize(r2)
                int r3 = r3 + r4
                goto L_0x0036
            L_0x0026:
                r2 = 0
                r3 = 0
            L_0x0028:
                if (r2 >= r6) goto L_0x0039
                int r4 = r5.getSpanSize(r2)
                int r3 = r3 + r4
                if (r3 != r7) goto L_0x0033
                r3 = 0
                goto L_0x0036
            L_0x0033:
                if (r3 <= r7) goto L_0x0036
                r3 = r4
            L_0x0036:
                int r2 = r2 + 1
                goto L_0x0028
            L_0x0039:
                int r0 = r0 + r3
                if (r0 > r7) goto L_0x003d
                return r3
            L_0x003d:
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: android.support.p003v7.widget.GridLayoutManager.SpanSizeLookup.getSpanIndex(int, int):int");
        }

        /* access modifiers changed from: 0000 */
        public int findReferenceIndexFromCache(int i) {
            int size = this.mSpanIndexCache.size() - 1;
            int i2 = 0;
            while (i2 <= size) {
                int i3 = (i2 + size) >>> 1;
                if (this.mSpanIndexCache.keyAt(i3) < i) {
                    i2 = i3 + 1;
                } else {
                    size = i3 - 1;
                }
            }
            int i4 = i2 - 1;
            if (i4 < 0 || i4 >= this.mSpanIndexCache.size()) {
                return -1;
            }
            return this.mSpanIndexCache.keyAt(i4);
        }

        public int getSpanGroupIndex(int i, int i2) {
            int spanSize = getSpanSize(i);
            int i3 = 0;
            int i4 = 0;
            for (int i5 = 0; i5 < i; i5++) {
                int spanSize2 = getSpanSize(i5);
                i3 += spanSize2;
                if (i3 == i2) {
                    i4++;
                    i3 = 0;
                } else if (i3 > i2) {
                    i4++;
                    i3 = spanSize2;
                }
            }
            return i3 + spanSize > i2 ? i4 + 1 : i4;
        }
    }

    public GridLayoutManager(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        setSpanCount(getProperties(context, attributeSet, i, i2).spanCount);
    }

    public GridLayoutManager(Context context, int i) {
        super(context);
        setSpanCount(i);
    }

    public GridLayoutManager(Context context, int i, int i2, boolean z) {
        super(context, i2, z);
        setSpanCount(i);
    }

    public void setStackFromEnd(boolean z) {
        if (!z) {
            super.setStackFromEnd(false);
            return;
        }
        throw new UnsupportedOperationException("GridLayoutManager does not support stack from end. Consider using reverse layout");
    }

    public int getRowCountForAccessibility(Recycler recycler, State state) {
        if (this.mOrientation == 0) {
            return this.mSpanCount;
        }
        if (state.getItemCount() < 1) {
            return 0;
        }
        return getSpanGroupIndex(recycler, state, state.getItemCount() - 1) + 1;
    }

    public int getColumnCountForAccessibility(Recycler recycler, State state) {
        if (this.mOrientation == 1) {
            return this.mSpanCount;
        }
        if (state.getItemCount() < 1) {
            return 0;
        }
        return getSpanGroupIndex(recycler, state, state.getItemCount() - 1) + 1;
    }

    public void onInitializeAccessibilityNodeInfoForItem(Recycler recycler, State state, View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
        android.view.ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (!(layoutParams instanceof LayoutParams)) {
            super.onInitializeAccessibilityNodeInfoForItem(view, accessibilityNodeInfoCompat);
            return;
        }
        LayoutParams layoutParams2 = (LayoutParams) layoutParams;
        int spanGroupIndex = getSpanGroupIndex(recycler, state, layoutParams2.getViewLayoutPosition());
        if (this.mOrientation == 0) {
            accessibilityNodeInfoCompat.setCollectionItemInfo(CollectionItemInfoCompat.obtain(layoutParams2.getSpanIndex(), layoutParams2.getSpanSize(), spanGroupIndex, 1, this.mSpanCount > 1 && layoutParams2.getSpanSize() == this.mSpanCount, false));
        } else {
            accessibilityNodeInfoCompat.setCollectionItemInfo(CollectionItemInfoCompat.obtain(spanGroupIndex, 1, layoutParams2.getSpanIndex(), layoutParams2.getSpanSize(), this.mSpanCount > 1 && layoutParams2.getSpanSize() == this.mSpanCount, false));
        }
    }

    public void onLayoutChildren(Recycler recycler, State state) {
        if (state.isPreLayout()) {
            cachePreLayoutSpanMapping();
        }
        super.onLayoutChildren(recycler, state);
        clearPreLayoutSpanMappingCache();
        if (!state.isPreLayout()) {
            this.mPendingSpanCountChange = false;
        }
    }

    private void clearPreLayoutSpanMappingCache() {
        this.mPreLayoutSpanSizeCache.clear();
        this.mPreLayoutSpanIndexCache.clear();
    }

    private void cachePreLayoutSpanMapping() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            LayoutParams layoutParams = (LayoutParams) getChildAt(i).getLayoutParams();
            int viewLayoutPosition = layoutParams.getViewLayoutPosition();
            this.mPreLayoutSpanSizeCache.put(viewLayoutPosition, layoutParams.getSpanSize());
            this.mPreLayoutSpanIndexCache.put(viewLayoutPosition, layoutParams.getSpanIndex());
        }
    }

    public void onItemsAdded(RecyclerView recyclerView, int i, int i2) {
        this.mSpanSizeLookup.invalidateSpanIndexCache();
    }

    public void onItemsChanged(RecyclerView recyclerView) {
        this.mSpanSizeLookup.invalidateSpanIndexCache();
    }

    public void onItemsRemoved(RecyclerView recyclerView, int i, int i2) {
        this.mSpanSizeLookup.invalidateSpanIndexCache();
    }

    public void onItemsUpdated(RecyclerView recyclerView, int i, int i2, Object obj) {
        this.mSpanSizeLookup.invalidateSpanIndexCache();
    }

    public void onItemsMoved(RecyclerView recyclerView, int i, int i2, int i3) {
        this.mSpanSizeLookup.invalidateSpanIndexCache();
    }

    public android.support.p003v7.widget.RecyclerView.LayoutParams generateDefaultLayoutParams() {
        if (this.mOrientation == 0) {
            return new LayoutParams(-2, -1);
        }
        return new LayoutParams(-1, -2);
    }

    public android.support.p003v7.widget.RecyclerView.LayoutParams generateLayoutParams(Context context, AttributeSet attributeSet) {
        return new LayoutParams(context, attributeSet);
    }

    public android.support.p003v7.widget.RecyclerView.LayoutParams generateLayoutParams(android.view.ViewGroup.LayoutParams layoutParams) {
        if (layoutParams instanceof MarginLayoutParams) {
            return new LayoutParams((MarginLayoutParams) layoutParams);
        }
        return new LayoutParams(layoutParams);
    }

    public boolean checkLayoutParams(android.support.p003v7.widget.RecyclerView.LayoutParams layoutParams) {
        return layoutParams instanceof LayoutParams;
    }

    public void setSpanSizeLookup(SpanSizeLookup spanSizeLookup) {
        this.mSpanSizeLookup = spanSizeLookup;
    }

    public SpanSizeLookup getSpanSizeLookup() {
        return this.mSpanSizeLookup;
    }

    private void updateMeasurements() {
        int i;
        int i2;
        if (getOrientation() == 1) {
            i2 = getWidth() - getPaddingRight();
            i = getPaddingLeft();
        } else {
            i2 = getHeight() - getPaddingBottom();
            i = getPaddingTop();
        }
        calculateItemBorders(i2 - i);
    }

    public void setMeasuredDimension(Rect rect, int i, int i2) {
        int i3;
        int i4;
        if (this.mCachedBorders == null) {
            super.setMeasuredDimension(rect, i, i2);
        }
        int paddingLeft = getPaddingLeft() + getPaddingRight();
        int paddingTop = getPaddingTop() + getPaddingBottom();
        if (this.mOrientation == 1) {
            i4 = chooseSize(i2, rect.height() + paddingTop, getMinimumHeight());
            int[] iArr = this.mCachedBorders;
            i3 = chooseSize(i, iArr[iArr.length - 1] + paddingLeft, getMinimumWidth());
        } else {
            i3 = chooseSize(i, rect.width() + paddingLeft, getMinimumWidth());
            int[] iArr2 = this.mCachedBorders;
            i4 = chooseSize(i2, iArr2[iArr2.length - 1] + paddingTop, getMinimumHeight());
        }
        setMeasuredDimension(i3, i4);
    }

    private void calculateItemBorders(int i) {
        this.mCachedBorders = calculateItemBorders(this.mCachedBorders, this.mSpanCount, i);
    }

    static int[] calculateItemBorders(int[] iArr, int i, int i2) {
        int i3;
        if (!(iArr != null && iArr.length == i + 1 && iArr[iArr.length - 1] == i2)) {
            iArr = new int[(i + 1)];
        }
        int i4 = 0;
        iArr[0] = 0;
        int i5 = i2 / i;
        int i6 = i2 % i;
        int i7 = 0;
        for (int i8 = 1; i8 <= i; i8++) {
            i4 += i6;
            if (i4 <= 0 || i - i4 >= i6) {
                i3 = i5;
            } else {
                i3 = i5 + 1;
                i4 -= i;
            }
            i7 += i3;
            iArr[i8] = i7;
        }
        return iArr;
    }

    /* access modifiers changed from: 0000 */
    public void onAnchorReady(Recycler recycler, State state, AnchorInfo anchorInfo, int i) {
        super.onAnchorReady(recycler, state, anchorInfo, i);
        updateMeasurements();
        if (state.getItemCount() > 0 && !state.isPreLayout()) {
            ensureAnchorIsInCorrectSpan(recycler, state, anchorInfo, i);
        }
        ensureViewSet();
    }

    private void ensureViewSet() {
        View[] viewArr = this.mSet;
        if (viewArr == null || viewArr.length != this.mSpanCount) {
            this.mSet = new View[this.mSpanCount];
        }
    }

    public int scrollHorizontallyBy(int i, Recycler recycler, State state) {
        updateMeasurements();
        ensureViewSet();
        return super.scrollHorizontallyBy(i, recycler, state);
    }

    public int scrollVerticallyBy(int i, Recycler recycler, State state) {
        updateMeasurements();
        ensureViewSet();
        return super.scrollVerticallyBy(i, recycler, state);
    }

    private void ensureAnchorIsInCorrectSpan(Recycler recycler, State state, AnchorInfo anchorInfo, int i) {
        boolean z = i == 1;
        int spanIndex = getSpanIndex(recycler, state, anchorInfo.mPosition);
        if (z) {
            while (spanIndex > 0 && anchorInfo.mPosition > 0) {
                anchorInfo.mPosition--;
                spanIndex = getSpanIndex(recycler, state, anchorInfo.mPosition);
            }
            return;
        }
        int itemCount = state.getItemCount() - 1;
        int i2 = anchorInfo.mPosition;
        while (i2 < itemCount) {
            int i3 = i2 + 1;
            int spanIndex2 = getSpanIndex(recycler, state, i3);
            if (spanIndex2 <= spanIndex) {
                break;
            }
            i2 = i3;
            spanIndex = spanIndex2;
        }
        anchorInfo.mPosition = i2;
    }

    /* access modifiers changed from: 0000 */
    public View findReferenceChild(Recycler recycler, State state, int i, int i2, int i3) {
        ensureLayoutState();
        int startAfterPadding = this.mOrientationHelper.getStartAfterPadding();
        int endAfterPadding = this.mOrientationHelper.getEndAfterPadding();
        int i4 = i2 > i ? 1 : -1;
        View view = null;
        View view2 = null;
        while (i != i2) {
            View childAt = getChildAt(i);
            int position = getPosition(childAt);
            if (position >= 0 && position < i3 && getSpanIndex(recycler, state, position) == 0) {
                if (((android.support.p003v7.widget.RecyclerView.LayoutParams) childAt.getLayoutParams()).isItemRemoved()) {
                    if (view2 == null) {
                        view2 = childAt;
                    }
                } else if (this.mOrientationHelper.getDecoratedStart(childAt) < endAfterPadding && this.mOrientationHelper.getDecoratedEnd(childAt) >= startAfterPadding) {
                    return childAt;
                } else {
                    if (view == null) {
                        view = childAt;
                    }
                }
            }
            i += i4;
        }
        if (view == null) {
            view = view2;
        }
        return view;
    }

    private int getSpanGroupIndex(Recycler recycler, State state, int i) {
        if (!state.isPreLayout()) {
            return this.mSpanSizeLookup.getSpanGroupIndex(i, this.mSpanCount);
        }
        int convertPreLayoutPositionToPostLayout = recycler.convertPreLayoutPositionToPostLayout(i);
        if (convertPreLayoutPositionToPostLayout != -1) {
            return this.mSpanSizeLookup.getSpanGroupIndex(convertPreLayoutPositionToPostLayout, this.mSpanCount);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Cannot find span size for pre layout position. ");
        sb.append(i);
        Log.w(TAG, sb.toString());
        return 0;
    }

    private int getSpanIndex(Recycler recycler, State state, int i) {
        if (!state.isPreLayout()) {
            return this.mSpanSizeLookup.getCachedSpanIndex(i, this.mSpanCount);
        }
        int i2 = this.mPreLayoutSpanIndexCache.get(i, -1);
        if (i2 != -1) {
            return i2;
        }
        int convertPreLayoutPositionToPostLayout = recycler.convertPreLayoutPositionToPostLayout(i);
        if (convertPreLayoutPositionToPostLayout != -1) {
            return this.mSpanSizeLookup.getCachedSpanIndex(convertPreLayoutPositionToPostLayout, this.mSpanCount);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Cannot find span size for pre layout position. It is not cached, not in the adapter. Pos:");
        sb.append(i);
        Log.w(TAG, sb.toString());
        return 0;
    }

    private int getSpanSize(Recycler recycler, State state, int i) {
        if (!state.isPreLayout()) {
            return this.mSpanSizeLookup.getSpanSize(i);
        }
        int i2 = this.mPreLayoutSpanSizeCache.get(i, -1);
        if (i2 != -1) {
            return i2;
        }
        int convertPreLayoutPositionToPostLayout = recycler.convertPreLayoutPositionToPostLayout(i);
        if (convertPreLayoutPositionToPostLayout != -1) {
            return this.mSpanSizeLookup.getSpanSize(convertPreLayoutPositionToPostLayout);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Cannot find span size for pre layout position. It is not cached, not in the adapter. Pos:");
        sb.append(i);
        Log.w(TAG, sb.toString());
        return 1;
    }

    /* access modifiers changed from: 0000 */
    public void layoutChunk(Recycler recycler, State state, LayoutState layoutState, LayoutChunkResult layoutChunkResult) {
        int i;
        int i2;
        int i3;
        int i4;
        int i5;
        int i6;
        int i7;
        boolean z;
        View view;
        int i8;
        LayoutParams layoutParams;
        float f;
        Recycler recycler2 = recycler;
        State state2 = state;
        LayoutState layoutState2 = layoutState;
        LayoutChunkResult layoutChunkResult2 = layoutChunkResult;
        int modeInOther = this.mOrientationHelper.getModeInOther();
        boolean z2 = modeInOther != 1073741824;
        int i9 = getChildCount() > 0 ? this.mCachedBorders[this.mSpanCount] : 0;
        if (z2) {
            updateMeasurements();
        }
        boolean z3 = layoutState2.mItemDirection == 1;
        int i10 = this.mSpanCount;
        if (!z3) {
            i10 = getSpanIndex(recycler2, state2, layoutState2.mCurrentPosition) + getSpanSize(recycler2, state2, layoutState2.mCurrentPosition);
        }
        int i11 = 0;
        int i12 = 0;
        while (i12 < this.mSpanCount && layoutState2.hasMore(state2) && i10 > 0) {
            int i13 = layoutState2.mCurrentPosition;
            int spanSize = getSpanSize(recycler2, state2, i13);
            if (spanSize <= this.mSpanCount) {
                i10 -= spanSize;
                if (i10 < 0) {
                    break;
                }
                View next = layoutState2.next(recycler2);
                if (next == null) {
                    break;
                }
                i11 += spanSize;
                this.mSet[i12] = next;
                i12++;
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("Item at position ");
                sb.append(i13);
                sb.append(" requires ");
                sb.append(spanSize);
                sb.append(" spans but GridLayoutManager has only ");
                sb.append(this.mSpanCount);
                sb.append(" spans.");
                throw new IllegalArgumentException(sb.toString());
            }
        }
        if (i12 == 0) {
            layoutChunkResult2.mFinished = true;
            return;
        }
        int i14 = i12;
        assignSpans(recycler, state, i12, i11, z3);
        int i15 = 0;
        float f2 = 0.0f;
        int i16 = 0;
        while (i16 < i14) {
            View view2 = this.mSet[i16];
            if (layoutState2.mScrapList == null) {
                if (z3) {
                    addView(view2);
                } else {
                    addView(view2, 0);
                }
            } else if (z3) {
                addDisappearingView(view2);
            } else {
                addDisappearingView(view2, 0);
            }
            LayoutParams layoutParams2 = (LayoutParams) view2.getLayoutParams();
            int childMeasureSpec = getChildMeasureSpec(this.mCachedBorders[layoutParams2.mSpanIndex + layoutParams2.mSpanSize] - this.mCachedBorders[layoutParams2.mSpanIndex], modeInOther, 0, this.mOrientation == 0 ? layoutParams2.height : layoutParams2.width, false);
            int i17 = i15;
            float f3 = f2;
            int i18 = modeInOther;
            int childMeasureSpec2 = getChildMeasureSpec(this.mOrientationHelper.getTotalSpace(), this.mOrientationHelper.getMode(), 0, this.mOrientation == 1 ? layoutParams2.height : layoutParams2.width, true);
            if (this.mOrientation == 1) {
                layoutParams = layoutParams2;
                view = view2;
                z = z3;
                i8 = i17;
                f = f3;
                measureChildWithDecorationsAndMargin(view2, childMeasureSpec, childMeasureSpec2, layoutParams2.height == -1, false);
            } else {
                layoutParams = layoutParams2;
                view = view2;
                z = z3;
                f = f3;
                i8 = i17;
                measureChildWithDecorationsAndMargin(view, childMeasureSpec2, childMeasureSpec, layoutParams.width == -1, false);
            }
            View view3 = view;
            int decoratedMeasurement = this.mOrientationHelper.getDecoratedMeasurement(view3);
            i15 = decoratedMeasurement > i8 ? decoratedMeasurement : i8;
            float decoratedMeasurementInOther = (((float) this.mOrientationHelper.getDecoratedMeasurementInOther(view3)) * 1.0f) / ((float) layoutParams.mSpanSize);
            f2 = decoratedMeasurementInOther > f ? decoratedMeasurementInOther : f;
            i16++;
            layoutState2 = layoutState;
            modeInOther = i18;
            z3 = z;
        }
        int i19 = i15;
        float f4 = f2;
        if (z2) {
            guessMeasurement(f4, i9);
            int i20 = 0;
            for (int i21 = 0; i21 < i14; i21++) {
                View view4 = this.mSet[i21];
                LayoutParams layoutParams3 = (LayoutParams) view4.getLayoutParams();
                int childMeasureSpec3 = getChildMeasureSpec(this.mCachedBorders[layoutParams3.mSpanIndex + layoutParams3.mSpanSize] - this.mCachedBorders[layoutParams3.mSpanIndex], 1073741824, 0, this.mOrientation == 0 ? layoutParams3.height : layoutParams3.width, false);
                int childMeasureSpec4 = getChildMeasureSpec(this.mOrientationHelper.getTotalSpace(), this.mOrientationHelper.getMode(), 0, this.mOrientation == 1 ? layoutParams3.height : layoutParams3.width, true);
                if (this.mOrientation == 1) {
                    measureChildWithDecorationsAndMargin(view4, childMeasureSpec3, childMeasureSpec4, false, true);
                } else {
                    measureChildWithDecorationsAndMargin(view4, childMeasureSpec4, childMeasureSpec3, false, true);
                }
                int decoratedMeasurement2 = this.mOrientationHelper.getDecoratedMeasurement(view4);
                if (decoratedMeasurement2 > i20) {
                    i20 = decoratedMeasurement2;
                }
            }
            i = -1;
            i19 = i20;
        } else {
            i = -1;
        }
        int makeMeasureSpec = MeasureSpec.makeMeasureSpec(i19, 1073741824);
        for (int i22 = 0; i22 < i14; i22++) {
            View view5 = this.mSet[i22];
            if (this.mOrientationHelper.getDecoratedMeasurement(view5) != i19) {
                LayoutParams layoutParams4 = (LayoutParams) view5.getLayoutParams();
                int childMeasureSpec5 = getChildMeasureSpec(this.mCachedBorders[layoutParams4.mSpanIndex + layoutParams4.mSpanSize] - this.mCachedBorders[layoutParams4.mSpanIndex], 1073741824, 0, this.mOrientation == 0 ? layoutParams4.height : layoutParams4.width, false);
                if (this.mOrientation == 1) {
                    measureChildWithDecorationsAndMargin(view5, childMeasureSpec5, makeMeasureSpec, true, true);
                } else {
                    measureChildWithDecorationsAndMargin(view5, makeMeasureSpec, childMeasureSpec5, true, true);
                }
            }
        }
        int i23 = 0;
        layoutChunkResult2.mConsumed = i19;
        if (this.mOrientation == 1) {
            LayoutState layoutState3 = layoutState;
            if (layoutState3.mLayoutDirection == i) {
                int i24 = layoutState3.mOffset;
                i2 = i24;
                i3 = i24 - i19;
            } else {
                int i25 = layoutState3.mOffset;
                i3 = i25;
                i2 = i25 + i19;
            }
            i5 = 0;
            i4 = 0;
        } else {
            LayoutState layoutState4 = layoutState;
            if (layoutState4.mLayoutDirection == i) {
                int i26 = layoutState4.mOffset;
                i3 = 0;
                i2 = 0;
                i4 = i26;
                i5 = i26 - i19;
            } else {
                i5 = layoutState4.mOffset;
                i4 = i5 + i19;
                i3 = 0;
                i2 = 0;
            }
        }
        while (i23 < i14) {
            View view6 = this.mSet[i23];
            LayoutParams layoutParams5 = (LayoutParams) view6.getLayoutParams();
            if (this.mOrientation != 1) {
                i3 = getPaddingTop() + this.mCachedBorders[layoutParams5.mSpanIndex];
                i2 = this.mOrientationHelper.getDecoratedMeasurementInOther(view6) + i3;
            } else if (isLayoutRTL()) {
                int paddingLeft = getPaddingLeft() + this.mCachedBorders[layoutParams5.mSpanIndex + layoutParams5.mSpanSize];
                i6 = paddingLeft;
                i7 = paddingLeft - this.mOrientationHelper.getDecoratedMeasurementInOther(view6);
                int i27 = i3;
                int i28 = i2;
                layoutDecorated(view6, i7 + layoutParams5.leftMargin, i27 + layoutParams5.topMargin, i6 - layoutParams5.rightMargin, i28 - layoutParams5.bottomMargin);
                if (!layoutParams5.isItemRemoved() || layoutParams5.isItemChanged()) {
                    layoutChunkResult2.mIgnoreConsumed = true;
                }
                layoutChunkResult2.mFocusable |= view6.isFocusable();
                i23++;
                i5 = i7;
                i3 = i27;
                i4 = i6;
                i2 = i28;
            } else {
                i5 = getPaddingLeft() + this.mCachedBorders[layoutParams5.mSpanIndex];
                i4 = this.mOrientationHelper.getDecoratedMeasurementInOther(view6) + i5;
            }
            i7 = i5;
            i6 = i4;
            int i272 = i3;
            int i282 = i2;
            layoutDecorated(view6, i7 + layoutParams5.leftMargin, i272 + layoutParams5.topMargin, i6 - layoutParams5.rightMargin, i282 - layoutParams5.bottomMargin);
            if (!layoutParams5.isItemRemoved()) {
            }
            layoutChunkResult2.mIgnoreConsumed = true;
            layoutChunkResult2.mFocusable |= view6.isFocusable();
            i23++;
            i5 = i7;
            i3 = i272;
            i4 = i6;
            i2 = i282;
        }
        Arrays.fill(this.mSet, null);
    }

    private void guessMeasurement(float f, int i) {
        calculateItemBorders(Math.max(Math.round(f * ((float) this.mSpanCount)), i));
    }

    private void measureChildWithDecorationsAndMargin(View view, int i, int i2, boolean z, boolean z2) {
        boolean z3;
        calculateItemDecorationsForChild(view, this.mDecorInsets);
        android.support.p003v7.widget.RecyclerView.LayoutParams layoutParams = (android.support.p003v7.widget.RecyclerView.LayoutParams) view.getLayoutParams();
        if (z || this.mOrientation == 1) {
            i = updateSpecWithExtra(i, layoutParams.leftMargin + this.mDecorInsets.left, layoutParams.rightMargin + this.mDecorInsets.right);
        }
        if (z || this.mOrientation == 0) {
            i2 = updateSpecWithExtra(i2, layoutParams.topMargin + this.mDecorInsets.top, layoutParams.bottomMargin + this.mDecorInsets.bottom);
        }
        if (z2) {
            z3 = shouldReMeasureChild(view, i, i2, layoutParams);
        } else {
            z3 = shouldMeasureChild(view, i, i2, layoutParams);
        }
        if (z3) {
            view.measure(i, i2);
        }
    }

    private int updateSpecWithExtra(int i, int i2, int i3) {
        if (i2 == 0 && i3 == 0) {
            return i;
        }
        int mode = MeasureSpec.getMode(i);
        if (mode == Integer.MIN_VALUE || mode == 1073741824) {
            return MeasureSpec.makeMeasureSpec(Math.max(0, (MeasureSpec.getSize(i) - i2) - i3), mode);
        }
        return i;
    }

    private void assignSpans(Recycler recycler, State state, int i, int i2, boolean z) {
        int i3;
        int i4;
        int i5;
        int i6;
        int i7;
        if (z) {
            i3 = i;
            i4 = 0;
            i5 = 1;
        } else {
            i4 = i - 1;
            i3 = -1;
            i5 = -1;
        }
        if (this.mOrientation != 1 || !isLayoutRTL()) {
            i6 = 1;
            i7 = 0;
        } else {
            i7 = this.mSpanCount - 1;
            i6 = -1;
        }
        while (i4 != i3) {
            View view = this.mSet[i4];
            LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
            layoutParams.mSpanSize = getSpanSize(recycler, state, getPosition(view));
            if (i6 != -1 || layoutParams.mSpanSize <= 1) {
                layoutParams.mSpanIndex = i7;
            } else {
                layoutParams.mSpanIndex = i7 - (layoutParams.mSpanSize - 1);
            }
            i7 += layoutParams.mSpanSize * i6;
            i4 += i5;
        }
    }

    public int getSpanCount() {
        return this.mSpanCount;
    }

    public void setSpanCount(int i) {
        if (i != this.mSpanCount) {
            this.mPendingSpanCountChange = true;
            if (i >= 1) {
                this.mSpanCount = i;
                this.mSpanSizeLookup.invalidateSpanIndexCache();
                return;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("Span count should be at least 1. Provided ");
            sb.append(i);
            throw new IllegalArgumentException(sb.toString());
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:42:0x0099, code lost:
        if (r11 == (r15 > r12)) goto L_0x0090;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.view.View onFocusSearchFailed(android.view.View r19, int r20, android.support.p003v7.widget.RecyclerView.Recycler r21, android.support.p003v7.widget.RecyclerView.State r22) {
        /*
            r18 = this;
            r0 = r18
            android.view.View r1 = r18.findContainingItemView(r19)
            r2 = 0
            if (r1 != 0) goto L_0x000a
            return r2
        L_0x000a:
            android.view.ViewGroup$LayoutParams r3 = r1.getLayoutParams()
            android.support.v7.widget.GridLayoutManager$LayoutParams r3 = (android.support.p003v7.widget.GridLayoutManager.LayoutParams) r3
            int r4 = r3.mSpanIndex
            int r5 = r3.mSpanIndex
            int r3 = r3.mSpanSize
            int r5 = r5 + r3
            android.view.View r3 = super.onFocusSearchFailed(r19, r20, r21, r22)
            if (r3 != 0) goto L_0x0024
            return r2
        L_0x0024:
            r3 = r20
            int r3 = r0.convertFocusDirectionToLayoutDirection(r3)
            r7 = 1
            if (r3 != r7) goto L_0x002f
            r3 = 1
            goto L_0x0030
        L_0x002f:
            r3 = 0
        L_0x0030:
            boolean r8 = r0.mShouldReverseLayout
            if (r3 == r8) goto L_0x0036
            r3 = 1
            goto L_0x0037
        L_0x0036:
            r3 = 0
        L_0x0037:
            r8 = -1
            if (r3 == 0) goto L_0x0042
            int r3 = r18.getChildCount()
            int r3 = r3 - r7
            r9 = -1
            r10 = -1
            goto L_0x0049
        L_0x0042:
            int r3 = r18.getChildCount()
            r9 = r3
            r3 = 0
            r10 = 1
        L_0x0049:
            int r11 = r0.mOrientation
            if (r11 != r7) goto L_0x0055
            boolean r11 = r18.isLayoutRTL()
            if (r11 == 0) goto L_0x0055
            r11 = 1
            goto L_0x0056
        L_0x0055:
            r11 = 0
        L_0x0056:
            r8 = 0
            r12 = -1
        L_0x0058:
            if (r3 == r9) goto L_0x00b2
            android.view.View r13 = r0.getChildAt(r3)
            if (r13 != r1) goto L_0x0061
            goto L_0x00b2
        L_0x0061:
            boolean r14 = r13.isFocusable()
            if (r14 != 0) goto L_0x0068
            goto L_0x00af
        L_0x0068:
            android.view.ViewGroup$LayoutParams r14 = r13.getLayoutParams()
            android.support.v7.widget.GridLayoutManager$LayoutParams r14 = (android.support.p003v7.widget.GridLayoutManager.LayoutParams) r14
            int r15 = r14.mSpanIndex
            int r16 = r14.mSpanIndex
            int r17 = r14.mSpanSize
            int r6 = r16 + r17
            if (r15 != r4) goto L_0x0081
            if (r6 != r5) goto L_0x0081
            return r13
        L_0x0081:
            if (r2 != 0) goto L_0x0084
            goto L_0x009d
        L_0x0084:
            int r16 = java.lang.Math.max(r15, r4)
            int r17 = java.lang.Math.min(r6, r5)
            int r7 = r17 - r16
            if (r7 <= r8) goto L_0x0092
        L_0x0090:
            r7 = 1
            goto L_0x009d
        L_0x0092:
            if (r7 != r8) goto L_0x009c
            if (r15 <= r12) goto L_0x0098
            r7 = 1
            goto L_0x0099
        L_0x0098:
            r7 = 0
        L_0x0099:
            if (r11 != r7) goto L_0x009c
            goto L_0x0090
        L_0x009c:
            r7 = 0
        L_0x009d:
            if (r7 == 0) goto L_0x00af
            int r2 = r14.mSpanIndex
            int r6 = java.lang.Math.min(r6, r5)
            int r7 = java.lang.Math.max(r15, r4)
            int r6 = r6 - r7
            r12 = r2
            r8 = r6
            r2 = r13
        L_0x00af:
            int r3 = r3 + r10
            r7 = 1
            goto L_0x0058
        L_0x00b2:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.p003v7.widget.GridLayoutManager.onFocusSearchFailed(android.view.View, int, android.support.v7.widget.RecyclerView$Recycler, android.support.v7.widget.RecyclerView$State):android.view.View");
    }

    public boolean supportsPredictiveItemAnimations() {
        return this.mPendingSavedState == null && !this.mPendingSpanCountChange;
    }
}
