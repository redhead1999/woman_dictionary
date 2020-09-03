package android.support.p003v7.widget;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.p000v4.view.accessibility.AccessibilityEventCompat;
import android.support.p000v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.p000v4.view.accessibility.AccessibilityNodeInfoCompat.CollectionItemInfoCompat;
import android.support.p000v4.view.accessibility.AccessibilityRecordCompat;
import android.support.p003v7.widget.RecyclerView.LayoutManager;
import android.support.p003v7.widget.RecyclerView.LayoutManager.Properties;
import android.support.p003v7.widget.RecyclerView.Recycler;
import android.support.p003v7.widget.RecyclerView.State;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.accessibility.AccessibilityEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

/* renamed from: android.support.v7.widget.StaggeredGridLayoutManager */
public class StaggeredGridLayoutManager extends LayoutManager {
    private static final boolean DEBUG = false;
    @Deprecated
    public static final int GAP_HANDLING_LAZY = 1;
    public static final int GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS = 2;
    public static final int GAP_HANDLING_NONE = 0;
    public static final int HORIZONTAL = 0;
    private static final int INVALID_OFFSET = Integer.MIN_VALUE;
    private static final float MAX_SCROLL_FACTOR = 0.33333334f;
    public static final String TAG = "StaggeredGridLayoutManager";
    public static final int VERTICAL = 1;
    private final AnchorInfo mAnchorInfo = new AnchorInfo();
    private final Runnable mCheckForGapsRunnable;
    private int mFullSizeSpec;
    private int mGapStrategy = 2;
    private boolean mLaidOutInvalidFullSpan = false;
    private boolean mLastLayoutFromEnd;
    private boolean mLastLayoutRTL;
    @NonNull
    private final LayoutState mLayoutState;
    LazySpanLookup mLazySpanLookup = new LazySpanLookup();
    /* access modifiers changed from: private */
    public int mOrientation;
    private SavedState mPendingSavedState;
    int mPendingScrollPosition = -1;
    int mPendingScrollPositionOffset = Integer.MIN_VALUE;
    @NonNull
    OrientationHelper mPrimaryOrientation;
    private BitSet mRemainingSpans;
    /* access modifiers changed from: private */
    public boolean mReverseLayout = false;
    @NonNull
    OrientationHelper mSecondaryOrientation;
    boolean mShouldReverseLayout = false;
    private int mSizePerSpan;
    private boolean mSmoothScrollbarEnabled;
    private int mSpanCount = -1;
    private Span[] mSpans;
    private final Rect mTmpRect = new Rect();

    /* renamed from: android.support.v7.widget.StaggeredGridLayoutManager$AnchorInfo */
    private class AnchorInfo {
        boolean mInvalidateOffsets;
        boolean mLayoutFromEnd;
        int mOffset;
        int mPosition;

        private AnchorInfo() {
        }

        /* access modifiers changed from: 0000 */
        public void reset() {
            this.mPosition = -1;
            this.mOffset = Integer.MIN_VALUE;
            this.mLayoutFromEnd = false;
            this.mInvalidateOffsets = false;
        }

        /* access modifiers changed from: 0000 */
        public void assignCoordinateFromPadding() {
            this.mOffset = this.mLayoutFromEnd ? StaggeredGridLayoutManager.this.mPrimaryOrientation.getEndAfterPadding() : StaggeredGridLayoutManager.this.mPrimaryOrientation.getStartAfterPadding();
        }

        /* access modifiers changed from: 0000 */
        public void assignCoordinateFromPadding(int i) {
            if (this.mLayoutFromEnd) {
                this.mOffset = StaggeredGridLayoutManager.this.mPrimaryOrientation.getEndAfterPadding() - i;
            } else {
                this.mOffset = StaggeredGridLayoutManager.this.mPrimaryOrientation.getStartAfterPadding() + i;
            }
        }
    }

    /* renamed from: android.support.v7.widget.StaggeredGridLayoutManager$LayoutParams */
    public static class LayoutParams extends android.support.p003v7.widget.RecyclerView.LayoutParams {
        public static final int INVALID_SPAN_ID = -1;
        boolean mFullSpan;
        Span mSpan;

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

        public void setFullSpan(boolean z) {
            this.mFullSpan = z;
        }

        public boolean isFullSpan() {
            return this.mFullSpan;
        }

        public final int getSpanIndex() {
            Span span = this.mSpan;
            if (span == null) {
                return -1;
            }
            return span.mIndex;
        }
    }

    /* renamed from: android.support.v7.widget.StaggeredGridLayoutManager$LazySpanLookup */
    static class LazySpanLookup {
        private static final int MIN_SIZE = 10;
        int[] mData;
        List<FullSpanItem> mFullSpanItems;

        /* renamed from: android.support.v7.widget.StaggeredGridLayoutManager$LazySpanLookup$FullSpanItem */
        static class FullSpanItem implements Parcelable {
            public static final Creator<FullSpanItem> CREATOR = new Creator<FullSpanItem>() {
                public FullSpanItem createFromParcel(Parcel parcel) {
                    return new FullSpanItem(parcel);
                }

                public FullSpanItem[] newArray(int i) {
                    return new FullSpanItem[i];
                }
            };
            int mGapDir;
            int[] mGapPerSpan;
            boolean mHasUnwantedGapAfter;
            int mPosition;

            public int describeContents() {
                return 0;
            }

            public FullSpanItem(Parcel parcel) {
                this.mPosition = parcel.readInt();
                this.mGapDir = parcel.readInt();
                boolean z = true;
                if (parcel.readInt() != 1) {
                    z = false;
                }
                this.mHasUnwantedGapAfter = z;
                int readInt = parcel.readInt();
                if (readInt > 0) {
                    this.mGapPerSpan = new int[readInt];
                    parcel.readIntArray(this.mGapPerSpan);
                }
            }

            public FullSpanItem() {
            }

            /* access modifiers changed from: 0000 */
            public int getGapForSpan(int i) {
                int[] iArr = this.mGapPerSpan;
                if (iArr == null) {
                    return 0;
                }
                return iArr[i];
            }

            public void writeToParcel(Parcel parcel, int i) {
                parcel.writeInt(this.mPosition);
                parcel.writeInt(this.mGapDir);
                parcel.writeInt(this.mHasUnwantedGapAfter ? 1 : 0);
                int[] iArr = this.mGapPerSpan;
                if (iArr == null || iArr.length <= 0) {
                    parcel.writeInt(0);
                    return;
                }
                parcel.writeInt(iArr.length);
                parcel.writeIntArray(this.mGapPerSpan);
            }

            public String toString() {
                StringBuilder sb = new StringBuilder();
                sb.append("FullSpanItem{mPosition=");
                sb.append(this.mPosition);
                sb.append(", mGapDir=");
                sb.append(this.mGapDir);
                sb.append(", mHasUnwantedGapAfter=");
                sb.append(this.mHasUnwantedGapAfter);
                sb.append(", mGapPerSpan=");
                sb.append(Arrays.toString(this.mGapPerSpan));
                sb.append('}');
                return sb.toString();
            }
        }

        LazySpanLookup() {
        }

        /* access modifiers changed from: 0000 */
        public int forceInvalidateAfter(int i) {
            List<FullSpanItem> list = this.mFullSpanItems;
            if (list != null) {
                for (int size = list.size() - 1; size >= 0; size--) {
                    if (((FullSpanItem) this.mFullSpanItems.get(size)).mPosition >= i) {
                        this.mFullSpanItems.remove(size);
                    }
                }
            }
            return invalidateAfter(i);
        }

        /* access modifiers changed from: 0000 */
        public int invalidateAfter(int i) {
            int[] iArr = this.mData;
            if (iArr == null || i >= iArr.length) {
                return -1;
            }
            int invalidateFullSpansAfter = invalidateFullSpansAfter(i);
            if (invalidateFullSpansAfter == -1) {
                int[] iArr2 = this.mData;
                Arrays.fill(iArr2, i, iArr2.length, -1);
                return this.mData.length;
            }
            int i2 = invalidateFullSpansAfter + 1;
            Arrays.fill(this.mData, i, i2, -1);
            return i2;
        }

        /* access modifiers changed from: 0000 */
        public int getSpan(int i) {
            int[] iArr = this.mData;
            if (iArr == null || i >= iArr.length) {
                return -1;
            }
            return iArr[i];
        }

        /* access modifiers changed from: 0000 */
        public void setSpan(int i, Span span) {
            ensureSize(i);
            this.mData[i] = span.mIndex;
        }

        /* access modifiers changed from: 0000 */
        public int sizeForPosition(int i) {
            int length = this.mData.length;
            while (length <= i) {
                length *= 2;
            }
            return length;
        }

        /* access modifiers changed from: 0000 */
        public void ensureSize(int i) {
            int[] iArr = this.mData;
            if (iArr == null) {
                this.mData = new int[(Math.max(i, 10) + 1)];
                Arrays.fill(this.mData, -1);
            } else if (i >= iArr.length) {
                this.mData = new int[sizeForPosition(i)];
                System.arraycopy(iArr, 0, this.mData, 0, iArr.length);
                int[] iArr2 = this.mData;
                Arrays.fill(iArr2, iArr.length, iArr2.length, -1);
            }
        }

        /* access modifiers changed from: 0000 */
        public void clear() {
            int[] iArr = this.mData;
            if (iArr != null) {
                Arrays.fill(iArr, -1);
            }
            this.mFullSpanItems = null;
        }

        /* access modifiers changed from: 0000 */
        public void offsetForRemoval(int i, int i2) {
            int[] iArr = this.mData;
            if (iArr != null && i < iArr.length) {
                int i3 = i + i2;
                ensureSize(i3);
                int[] iArr2 = this.mData;
                System.arraycopy(iArr2, i3, iArr2, i, (iArr2.length - i) - i2);
                int[] iArr3 = this.mData;
                Arrays.fill(iArr3, iArr3.length - i2, iArr3.length, -1);
                offsetFullSpansForRemoval(i, i2);
            }
        }

        private void offsetFullSpansForRemoval(int i, int i2) {
            List<FullSpanItem> list = this.mFullSpanItems;
            if (list != null) {
                int i3 = i + i2;
                for (int size = list.size() - 1; size >= 0; size--) {
                    FullSpanItem fullSpanItem = (FullSpanItem) this.mFullSpanItems.get(size);
                    if (fullSpanItem.mPosition >= i) {
                        if (fullSpanItem.mPosition < i3) {
                            this.mFullSpanItems.remove(size);
                        } else {
                            fullSpanItem.mPosition -= i2;
                        }
                    }
                }
            }
        }

        /* access modifiers changed from: 0000 */
        public void offsetForAddition(int i, int i2) {
            int[] iArr = this.mData;
            if (iArr != null && i < iArr.length) {
                int i3 = i + i2;
                ensureSize(i3);
                int[] iArr2 = this.mData;
                System.arraycopy(iArr2, i, iArr2, i3, (iArr2.length - i) - i2);
                Arrays.fill(this.mData, i, i3, -1);
                offsetFullSpansForAddition(i, i2);
            }
        }

        private void offsetFullSpansForAddition(int i, int i2) {
            List<FullSpanItem> list = this.mFullSpanItems;
            if (list != null) {
                for (int size = list.size() - 1; size >= 0; size--) {
                    FullSpanItem fullSpanItem = (FullSpanItem) this.mFullSpanItems.get(size);
                    if (fullSpanItem.mPosition >= i) {
                        fullSpanItem.mPosition += i2;
                    }
                }
            }
        }

        private int invalidateFullSpansAfter(int i) {
            if (this.mFullSpanItems == null) {
                return -1;
            }
            FullSpanItem fullSpanItem = getFullSpanItem(i);
            if (fullSpanItem != null) {
                this.mFullSpanItems.remove(fullSpanItem);
            }
            int size = this.mFullSpanItems.size();
            int i2 = 0;
            while (true) {
                if (i2 >= size) {
                    i2 = -1;
                    break;
                } else if (((FullSpanItem) this.mFullSpanItems.get(i2)).mPosition >= i) {
                    break;
                } else {
                    i2++;
                }
            }
            if (i2 == -1) {
                return -1;
            }
            FullSpanItem fullSpanItem2 = (FullSpanItem) this.mFullSpanItems.get(i2);
            this.mFullSpanItems.remove(i2);
            return fullSpanItem2.mPosition;
        }

        public void addFullSpanItem(FullSpanItem fullSpanItem) {
            if (this.mFullSpanItems == null) {
                this.mFullSpanItems = new ArrayList();
            }
            int size = this.mFullSpanItems.size();
            for (int i = 0; i < size; i++) {
                FullSpanItem fullSpanItem2 = (FullSpanItem) this.mFullSpanItems.get(i);
                if (fullSpanItem2.mPosition == fullSpanItem.mPosition) {
                    this.mFullSpanItems.remove(i);
                }
                if (fullSpanItem2.mPosition >= fullSpanItem.mPosition) {
                    this.mFullSpanItems.add(i, fullSpanItem);
                    return;
                }
            }
            this.mFullSpanItems.add(fullSpanItem);
        }

        public FullSpanItem getFullSpanItem(int i) {
            List<FullSpanItem> list = this.mFullSpanItems;
            if (list == null) {
                return null;
            }
            for (int size = list.size() - 1; size >= 0; size--) {
                FullSpanItem fullSpanItem = (FullSpanItem) this.mFullSpanItems.get(size);
                if (fullSpanItem.mPosition == i) {
                    return fullSpanItem;
                }
            }
            return null;
        }

        public FullSpanItem getFirstFullSpanItemInRange(int i, int i2, int i3, boolean z) {
            List<FullSpanItem> list = this.mFullSpanItems;
            if (list == null) {
                return null;
            }
            int size = list.size();
            for (int i4 = 0; i4 < size; i4++) {
                FullSpanItem fullSpanItem = (FullSpanItem) this.mFullSpanItems.get(i4);
                if (fullSpanItem.mPosition >= i2) {
                    return null;
                }
                if (fullSpanItem.mPosition >= i && (i3 == 0 || fullSpanItem.mGapDir == i3 || (z && fullSpanItem.mHasUnwantedGapAfter))) {
                    return fullSpanItem;
                }
            }
            return null;
        }
    }

    /* renamed from: android.support.v7.widget.StaggeredGridLayoutManager$SavedState */
    public static class SavedState implements Parcelable {
        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        boolean mAnchorLayoutFromEnd;
        int mAnchorPosition;
        List<FullSpanItem> mFullSpanItems;
        boolean mLastLayoutRTL;
        boolean mReverseLayout;
        int[] mSpanLookup;
        int mSpanLookupSize;
        int[] mSpanOffsets;
        int mSpanOffsetsSize;
        int mVisibleAnchorPosition;

        public int describeContents() {
            return 0;
        }

        public SavedState() {
        }

        SavedState(Parcel parcel) {
            this.mAnchorPosition = parcel.readInt();
            this.mVisibleAnchorPosition = parcel.readInt();
            this.mSpanOffsetsSize = parcel.readInt();
            int i = this.mSpanOffsetsSize;
            if (i > 0) {
                this.mSpanOffsets = new int[i];
                parcel.readIntArray(this.mSpanOffsets);
            }
            this.mSpanLookupSize = parcel.readInt();
            int i2 = this.mSpanLookupSize;
            if (i2 > 0) {
                this.mSpanLookup = new int[i2];
                parcel.readIntArray(this.mSpanLookup);
            }
            boolean z = false;
            this.mReverseLayout = parcel.readInt() == 1;
            this.mAnchorLayoutFromEnd = parcel.readInt() == 1;
            if (parcel.readInt() == 1) {
                z = true;
            }
            this.mLastLayoutRTL = z;
            this.mFullSpanItems = parcel.readArrayList(FullSpanItem.class.getClassLoader());
        }

        public SavedState(SavedState savedState) {
            this.mSpanOffsetsSize = savedState.mSpanOffsetsSize;
            this.mAnchorPosition = savedState.mAnchorPosition;
            this.mVisibleAnchorPosition = savedState.mVisibleAnchorPosition;
            this.mSpanOffsets = savedState.mSpanOffsets;
            this.mSpanLookupSize = savedState.mSpanLookupSize;
            this.mSpanLookup = savedState.mSpanLookup;
            this.mReverseLayout = savedState.mReverseLayout;
            this.mAnchorLayoutFromEnd = savedState.mAnchorLayoutFromEnd;
            this.mLastLayoutRTL = savedState.mLastLayoutRTL;
            this.mFullSpanItems = savedState.mFullSpanItems;
        }

        /* access modifiers changed from: 0000 */
        public void invalidateSpanInfo() {
            this.mSpanOffsets = null;
            this.mSpanOffsetsSize = 0;
            this.mSpanLookupSize = 0;
            this.mSpanLookup = null;
            this.mFullSpanItems = null;
        }

        /* access modifiers changed from: 0000 */
        public void invalidateAnchorPositionInfo() {
            this.mSpanOffsets = null;
            this.mSpanOffsetsSize = 0;
            this.mAnchorPosition = -1;
            this.mVisibleAnchorPosition = -1;
        }

        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(this.mAnchorPosition);
            parcel.writeInt(this.mVisibleAnchorPosition);
            parcel.writeInt(this.mSpanOffsetsSize);
            if (this.mSpanOffsetsSize > 0) {
                parcel.writeIntArray(this.mSpanOffsets);
            }
            parcel.writeInt(this.mSpanLookupSize);
            if (this.mSpanLookupSize > 0) {
                parcel.writeIntArray(this.mSpanLookup);
            }
            parcel.writeInt(this.mReverseLayout ? 1 : 0);
            parcel.writeInt(this.mAnchorLayoutFromEnd ? 1 : 0);
            parcel.writeInt(this.mLastLayoutRTL ? 1 : 0);
            parcel.writeList(this.mFullSpanItems);
        }
    }

    /* renamed from: android.support.v7.widget.StaggeredGridLayoutManager$Span */
    class Span {
        static final int INVALID_LINE = Integer.MIN_VALUE;
        int mCachedEnd;
        int mCachedStart;
        int mDeletedSize;
        final int mIndex;
        /* access modifiers changed from: private */
        public ArrayList<View> mViews;

        private Span(int i) {
            this.mViews = new ArrayList<>();
            this.mCachedStart = Integer.MIN_VALUE;
            this.mCachedEnd = Integer.MIN_VALUE;
            this.mDeletedSize = 0;
            this.mIndex = i;
        }

        /* access modifiers changed from: 0000 */
        public int getStartLine(int i) {
            int i2 = this.mCachedStart;
            if (i2 != Integer.MIN_VALUE) {
                return i2;
            }
            if (this.mViews.size() == 0) {
                return i;
            }
            calculateCachedStart();
            return this.mCachedStart;
        }

        /* access modifiers changed from: 0000 */
        public void calculateCachedStart() {
            View view = (View) this.mViews.get(0);
            LayoutParams layoutParams = getLayoutParams(view);
            this.mCachedStart = StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedStart(view);
            if (layoutParams.mFullSpan) {
                FullSpanItem fullSpanItem = StaggeredGridLayoutManager.this.mLazySpanLookup.getFullSpanItem(layoutParams.getViewLayoutPosition());
                if (fullSpanItem != null && fullSpanItem.mGapDir == -1) {
                    this.mCachedStart -= fullSpanItem.getGapForSpan(this.mIndex);
                }
            }
        }

        /* access modifiers changed from: 0000 */
        public int getStartLine() {
            int i = this.mCachedStart;
            if (i != Integer.MIN_VALUE) {
                return i;
            }
            calculateCachedStart();
            return this.mCachedStart;
        }

        /* access modifiers changed from: 0000 */
        public int getEndLine(int i) {
            int i2 = this.mCachedEnd;
            if (i2 != Integer.MIN_VALUE) {
                return i2;
            }
            if (this.mViews.size() == 0) {
                return i;
            }
            calculateCachedEnd();
            return this.mCachedEnd;
        }

        /* access modifiers changed from: 0000 */
        public void calculateCachedEnd() {
            ArrayList<View> arrayList = this.mViews;
            View view = (View) arrayList.get(arrayList.size() - 1);
            LayoutParams layoutParams = getLayoutParams(view);
            this.mCachedEnd = StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedEnd(view);
            if (layoutParams.mFullSpan) {
                FullSpanItem fullSpanItem = StaggeredGridLayoutManager.this.mLazySpanLookup.getFullSpanItem(layoutParams.getViewLayoutPosition());
                if (fullSpanItem != null && fullSpanItem.mGapDir == 1) {
                    this.mCachedEnd += fullSpanItem.getGapForSpan(this.mIndex);
                }
            }
        }

        /* access modifiers changed from: 0000 */
        public int getEndLine() {
            int i = this.mCachedEnd;
            if (i != Integer.MIN_VALUE) {
                return i;
            }
            calculateCachedEnd();
            return this.mCachedEnd;
        }

        /* access modifiers changed from: 0000 */
        public void prependToSpan(View view) {
            LayoutParams layoutParams = getLayoutParams(view);
            layoutParams.mSpan = this;
            this.mViews.add(0, view);
            this.mCachedStart = Integer.MIN_VALUE;
            if (this.mViews.size() == 1) {
                this.mCachedEnd = Integer.MIN_VALUE;
            }
            if (layoutParams.isItemRemoved() || layoutParams.isItemChanged()) {
                this.mDeletedSize += StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedMeasurement(view);
            }
        }

        /* access modifiers changed from: 0000 */
        public void appendToSpan(View view) {
            LayoutParams layoutParams = getLayoutParams(view);
            layoutParams.mSpan = this;
            this.mViews.add(view);
            this.mCachedEnd = Integer.MIN_VALUE;
            if (this.mViews.size() == 1) {
                this.mCachedStart = Integer.MIN_VALUE;
            }
            if (layoutParams.isItemRemoved() || layoutParams.isItemChanged()) {
                this.mDeletedSize += StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedMeasurement(view);
            }
        }

        /* access modifiers changed from: 0000 */
        public void cacheReferenceLineAndClear(boolean z, int i) {
            int i2;
            if (z) {
                i2 = getEndLine(Integer.MIN_VALUE);
            } else {
                i2 = getStartLine(Integer.MIN_VALUE);
            }
            clear();
            if (i2 != Integer.MIN_VALUE) {
                if ((!z || i2 >= StaggeredGridLayoutManager.this.mPrimaryOrientation.getEndAfterPadding()) && (z || i2 <= StaggeredGridLayoutManager.this.mPrimaryOrientation.getStartAfterPadding())) {
                    if (i != Integer.MIN_VALUE) {
                        i2 += i;
                    }
                    this.mCachedEnd = i2;
                    this.mCachedStart = i2;
                }
            }
        }

        /* access modifiers changed from: 0000 */
        public void clear() {
            this.mViews.clear();
            invalidateCache();
            this.mDeletedSize = 0;
        }

        /* access modifiers changed from: 0000 */
        public void invalidateCache() {
            this.mCachedStart = Integer.MIN_VALUE;
            this.mCachedEnd = Integer.MIN_VALUE;
        }

        /* access modifiers changed from: 0000 */
        public void setLine(int i) {
            this.mCachedStart = i;
            this.mCachedEnd = i;
        }

        /* access modifiers changed from: 0000 */
        public void popEnd() {
            int size = this.mViews.size();
            View view = (View) this.mViews.remove(size - 1);
            LayoutParams layoutParams = getLayoutParams(view);
            layoutParams.mSpan = null;
            if (layoutParams.isItemRemoved() || layoutParams.isItemChanged()) {
                this.mDeletedSize -= StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedMeasurement(view);
            }
            if (size == 1) {
                this.mCachedStart = Integer.MIN_VALUE;
            }
            this.mCachedEnd = Integer.MIN_VALUE;
        }

        /* access modifiers changed from: 0000 */
        public void popStart() {
            View view = (View) this.mViews.remove(0);
            LayoutParams layoutParams = getLayoutParams(view);
            layoutParams.mSpan = null;
            if (this.mViews.size() == 0) {
                this.mCachedEnd = Integer.MIN_VALUE;
            }
            if (layoutParams.isItemRemoved() || layoutParams.isItemChanged()) {
                this.mDeletedSize -= StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedMeasurement(view);
            }
            this.mCachedStart = Integer.MIN_VALUE;
        }

        public int getDeletedSize() {
            return this.mDeletedSize;
        }

        /* access modifiers changed from: 0000 */
        public LayoutParams getLayoutParams(View view) {
            return (LayoutParams) view.getLayoutParams();
        }

        /* access modifiers changed from: 0000 */
        public void onOffset(int i) {
            int i2 = this.mCachedStart;
            if (i2 != Integer.MIN_VALUE) {
                this.mCachedStart = i2 + i;
            }
            int i3 = this.mCachedEnd;
            if (i3 != Integer.MIN_VALUE) {
                this.mCachedEnd = i3 + i;
            }
        }

        public int findFirstVisibleItemPosition() {
            return StaggeredGridLayoutManager.this.mReverseLayout ? findOneVisibleChild(this.mViews.size() - 1, -1, false) : findOneVisibleChild(0, this.mViews.size(), false);
        }

        public int findFirstCompletelyVisibleItemPosition() {
            int i;
            int i2;
            if (StaggeredGridLayoutManager.this.mReverseLayout) {
                i2 = this.mViews.size() - 1;
                i = -1;
            } else {
                i2 = 0;
                i = this.mViews.size();
            }
            return findOneVisibleChild(i2, i, true);
        }

        public int findLastVisibleItemPosition() {
            return StaggeredGridLayoutManager.this.mReverseLayout ? findOneVisibleChild(0, this.mViews.size(), false) : findOneVisibleChild(this.mViews.size() - 1, -1, false);
        }

        public int findLastCompletelyVisibleItemPosition() {
            int i;
            int i2;
            if (StaggeredGridLayoutManager.this.mReverseLayout) {
                i2 = 0;
                i = this.mViews.size();
            } else {
                i2 = this.mViews.size() - 1;
                i = -1;
            }
            return findOneVisibleChild(i2, i, true);
        }

        /* access modifiers changed from: 0000 */
        public int findOneVisibleChild(int i, int i2, boolean z) {
            int startAfterPadding = StaggeredGridLayoutManager.this.mPrimaryOrientation.getStartAfterPadding();
            int endAfterPadding = StaggeredGridLayoutManager.this.mPrimaryOrientation.getEndAfterPadding();
            int i3 = i2 > i ? 1 : -1;
            while (i != i2) {
                View view = (View) this.mViews.get(i);
                int decoratedStart = StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedStart(view);
                int decoratedEnd = StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedEnd(view);
                if (decoratedStart < endAfterPadding && decoratedEnd > startAfterPadding) {
                    if (!z) {
                        return StaggeredGridLayoutManager.this.getPosition(view);
                    }
                    if (decoratedStart >= startAfterPadding && decoratedEnd <= endAfterPadding) {
                        return StaggeredGridLayoutManager.this.getPosition(view);
                    }
                }
                i += i3;
            }
            return -1;
        }

        public View getFocusableViewAfter(int i, int i2) {
            View view = null;
            if (i2 != -1) {
                int size = this.mViews.size() - 1;
                while (size >= 0) {
                    View view2 = (View) this.mViews.get(size);
                    if (view2.isFocusable()) {
                        if ((StaggeredGridLayoutManager.this.getPosition(view2) > i) != (!StaggeredGridLayoutManager.this.mReverseLayout)) {
                            break;
                        }
                        size--;
                        view = view2;
                    } else {
                        break;
                    }
                }
            } else {
                int size2 = this.mViews.size();
                int i3 = 0;
                while (i3 < size2) {
                    View view3 = (View) this.mViews.get(i3);
                    if (view3.isFocusable()) {
                        if ((StaggeredGridLayoutManager.this.getPosition(view3) > i) != StaggeredGridLayoutManager.this.mReverseLayout) {
                            break;
                        }
                        i3++;
                        view = view3;
                    } else {
                        break;
                    }
                }
            }
            return view;
        }
    }

    public StaggeredGridLayoutManager(Context context, AttributeSet attributeSet, int i, int i2) {
        boolean z = true;
        this.mSmoothScrollbarEnabled = true;
        this.mCheckForGapsRunnable = new Runnable() {
            public void run() {
                StaggeredGridLayoutManager.this.checkForGaps();
            }
        };
        Properties properties = getProperties(context, attributeSet, i, i2);
        setOrientation(properties.orientation);
        setSpanCount(properties.spanCount);
        setReverseLayout(properties.reverseLayout);
        if (this.mGapStrategy == 0) {
            z = false;
        }
        setAutoMeasureEnabled(z);
        this.mLayoutState = new LayoutState();
        createOrientationHelpers();
    }

    public StaggeredGridLayoutManager(int i, int i2) {
        boolean z = true;
        this.mSmoothScrollbarEnabled = true;
        this.mCheckForGapsRunnable = new Runnable() {
            public void run() {
                StaggeredGridLayoutManager.this.checkForGaps();
            }
        };
        this.mOrientation = i2;
        setSpanCount(i);
        if (this.mGapStrategy == 0) {
            z = false;
        }
        setAutoMeasureEnabled(z);
        this.mLayoutState = new LayoutState();
        createOrientationHelpers();
    }

    private void createOrientationHelpers() {
        this.mPrimaryOrientation = OrientationHelper.createOrientationHelper(this, this.mOrientation);
        this.mSecondaryOrientation = OrientationHelper.createOrientationHelper(this, 1 - this.mOrientation);
    }

    /* access modifiers changed from: private */
    public boolean checkForGaps() {
        int i;
        int i2;
        if (getChildCount() == 0 || this.mGapStrategy == 0 || !isAttachedToWindow()) {
            return false;
        }
        if (this.mShouldReverseLayout) {
            i2 = getLastChildPosition();
            i = getFirstChildPosition();
        } else {
            i2 = getFirstChildPosition();
            i = getLastChildPosition();
        }
        if (i2 == 0 && hasGapsToFix() != null) {
            this.mLazySpanLookup.clear();
            requestSimpleAnimationsInNextLayout();
            requestLayout();
            return true;
        } else if (!this.mLaidOutInvalidFullSpan) {
            return false;
        } else {
            int i3 = this.mShouldReverseLayout ? -1 : 1;
            int i4 = i + 1;
            FullSpanItem firstFullSpanItemInRange = this.mLazySpanLookup.getFirstFullSpanItemInRange(i2, i4, i3, true);
            if (firstFullSpanItemInRange == null) {
                this.mLaidOutInvalidFullSpan = false;
                this.mLazySpanLookup.forceInvalidateAfter(i4);
                return false;
            }
            FullSpanItem firstFullSpanItemInRange2 = this.mLazySpanLookup.getFirstFullSpanItemInRange(i2, firstFullSpanItemInRange.mPosition, i3 * -1, true);
            if (firstFullSpanItemInRange2 == null) {
                this.mLazySpanLookup.forceInvalidateAfter(firstFullSpanItemInRange.mPosition);
            } else {
                this.mLazySpanLookup.forceInvalidateAfter(firstFullSpanItemInRange2.mPosition + 1);
            }
            requestSimpleAnimationsInNextLayout();
            requestLayout();
            return true;
        }
    }

    public void onScrollStateChanged(int i) {
        if (i == 0) {
            checkForGaps();
        }
    }

    public void onDetachedFromWindow(RecyclerView recyclerView, Recycler recycler) {
        removeCallbacks(this.mCheckForGapsRunnable);
        for (int i = 0; i < this.mSpanCount; i++) {
            this.mSpans[i].clear();
        }
    }

    /* access modifiers changed from: 0000 */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x0074, code lost:
        if (r10 == r11) goto L_0x0088;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x0086, code lost:
        if (r10 == r11) goto L_0x0088;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x008a, code lost:
        r10 = false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.view.View hasGapsToFix() {
        /*
            r12 = this;
            int r0 = r12.getChildCount()
            r1 = 1
            int r0 = r0 - r1
            java.util.BitSet r2 = new java.util.BitSet
            int r3 = r12.mSpanCount
            r2.<init>(r3)
            int r3 = r12.mSpanCount
            r4 = 0
            r2.set(r4, r3, r1)
            int r3 = r12.mOrientation
            r5 = -1
            if (r3 != r1) goto L_0x0020
            boolean r3 = r12.isLayoutRTL()
            if (r3 == 0) goto L_0x0020
            r3 = 1
            goto L_0x0021
        L_0x0020:
            r3 = -1
        L_0x0021:
            boolean r6 = r12.mShouldReverseLayout
            if (r6 == 0) goto L_0x0027
            r6 = -1
            goto L_0x002b
        L_0x0027:
            int r0 = r0 + 1
            r6 = r0
            r0 = 0
        L_0x002b:
            if (r0 >= r6) goto L_0x002e
            r5 = 1
        L_0x002e:
            if (r0 == r6) goto L_0x00ab
            android.view.View r7 = r12.getChildAt(r0)
            android.view.ViewGroup$LayoutParams r8 = r7.getLayoutParams()
            android.support.v7.widget.StaggeredGridLayoutManager$LayoutParams r8 = (android.support.p003v7.widget.StaggeredGridLayoutManager.LayoutParams) r8
            android.support.v7.widget.StaggeredGridLayoutManager$Span r9 = r8.mSpan
            int r9 = r9.mIndex
            boolean r9 = r2.get(r9)
            if (r9 == 0) goto L_0x0054
            android.support.v7.widget.StaggeredGridLayoutManager$Span r9 = r8.mSpan
            boolean r9 = r12.checkSpanForGap(r9)
            if (r9 == 0) goto L_0x004d
            return r7
        L_0x004d:
            android.support.v7.widget.StaggeredGridLayoutManager$Span r9 = r8.mSpan
            int r9 = r9.mIndex
            r2.clear(r9)
        L_0x0054:
            boolean r9 = r8.mFullSpan
            if (r9 == 0) goto L_0x0059
            goto L_0x00a9
        L_0x0059:
            int r9 = r0 + r5
            if (r9 == r6) goto L_0x00a9
            android.view.View r9 = r12.getChildAt(r9)
            boolean r10 = r12.mShouldReverseLayout
            if (r10 == 0) goto L_0x0077
            android.support.v7.widget.OrientationHelper r10 = r12.mPrimaryOrientation
            int r10 = r10.getDecoratedEnd(r7)
            android.support.v7.widget.OrientationHelper r11 = r12.mPrimaryOrientation
            int r11 = r11.getDecoratedEnd(r9)
            if (r10 >= r11) goto L_0x0074
            return r7
        L_0x0074:
            if (r10 != r11) goto L_0x008a
            goto L_0x0088
        L_0x0077:
            android.support.v7.widget.OrientationHelper r10 = r12.mPrimaryOrientation
            int r10 = r10.getDecoratedStart(r7)
            android.support.v7.widget.OrientationHelper r11 = r12.mPrimaryOrientation
            int r11 = r11.getDecoratedStart(r9)
            if (r10 <= r11) goto L_0x0086
            return r7
        L_0x0086:
            if (r10 != r11) goto L_0x008a
        L_0x0088:
            r10 = 1
            goto L_0x008b
        L_0x008a:
            r10 = 0
        L_0x008b:
            if (r10 == 0) goto L_0x00a9
            android.view.ViewGroup$LayoutParams r9 = r9.getLayoutParams()
            android.support.v7.widget.StaggeredGridLayoutManager$LayoutParams r9 = (android.support.p003v7.widget.StaggeredGridLayoutManager.LayoutParams) r9
            android.support.v7.widget.StaggeredGridLayoutManager$Span r8 = r8.mSpan
            int r8 = r8.mIndex
            android.support.v7.widget.StaggeredGridLayoutManager$Span r9 = r9.mSpan
            int r9 = r9.mIndex
            int r8 = r8 - r9
            if (r8 >= 0) goto L_0x00a0
            r8 = 1
            goto L_0x00a1
        L_0x00a0:
            r8 = 0
        L_0x00a1:
            if (r3 >= 0) goto L_0x00a5
            r9 = 1
            goto L_0x00a6
        L_0x00a5:
            r9 = 0
        L_0x00a6:
            if (r8 == r9) goto L_0x00a9
            return r7
        L_0x00a9:
            int r0 = r0 + r5
            goto L_0x002e
        L_0x00ab:
            r0 = 0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.p003v7.widget.StaggeredGridLayoutManager.hasGapsToFix():android.view.View");
    }

    private boolean checkSpanForGap(Span span) {
        if (this.mShouldReverseLayout) {
            if (span.getEndLine() < this.mPrimaryOrientation.getEndAfterPadding()) {
                return !span.getLayoutParams((View) span.mViews.get(span.mViews.size() - 1)).mFullSpan;
            }
        } else if (span.getStartLine() > this.mPrimaryOrientation.getStartAfterPadding()) {
            return !span.getLayoutParams((View) span.mViews.get(0)).mFullSpan;
        }
        return false;
    }

    public void setSpanCount(int i) {
        assertNotInLayoutOrScroll(null);
        if (i != this.mSpanCount) {
            invalidateSpanAssignments();
            this.mSpanCount = i;
            this.mRemainingSpans = new BitSet(this.mSpanCount);
            this.mSpans = new Span[this.mSpanCount];
            for (int i2 = 0; i2 < this.mSpanCount; i2++) {
                this.mSpans[i2] = new Span(i2);
            }
            requestLayout();
        }
    }

    public void setOrientation(int i) {
        if (i == 0 || i == 1) {
            assertNotInLayoutOrScroll(null);
            if (i != this.mOrientation) {
                this.mOrientation = i;
                OrientationHelper orientationHelper = this.mPrimaryOrientation;
                this.mPrimaryOrientation = this.mSecondaryOrientation;
                this.mSecondaryOrientation = orientationHelper;
                requestLayout();
                return;
            }
            return;
        }
        throw new IllegalArgumentException("invalid orientation.");
    }

    public void setReverseLayout(boolean z) {
        assertNotInLayoutOrScroll(null);
        SavedState savedState = this.mPendingSavedState;
        if (!(savedState == null || savedState.mReverseLayout == z)) {
            this.mPendingSavedState.mReverseLayout = z;
        }
        this.mReverseLayout = z;
        requestLayout();
    }

    public int getGapStrategy() {
        return this.mGapStrategy;
    }

    public void setGapStrategy(int i) {
        assertNotInLayoutOrScroll(null);
        if (i != this.mGapStrategy) {
            if (i == 0 || i == 2) {
                this.mGapStrategy = i;
                setAutoMeasureEnabled(this.mGapStrategy != 0);
                requestLayout();
                return;
            }
            throw new IllegalArgumentException("invalid gap strategy. Must be GAP_HANDLING_NONE or GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS");
        }
    }

    public void assertNotInLayoutOrScroll(String str) {
        if (this.mPendingSavedState == null) {
            super.assertNotInLayoutOrScroll(str);
        }
    }

    public int getSpanCount() {
        return this.mSpanCount;
    }

    public void invalidateSpanAssignments() {
        this.mLazySpanLookup.clear();
        requestLayout();
    }

    private void resolveShouldLayoutReverse() {
        if (this.mOrientation == 1 || !isLayoutRTL()) {
            this.mShouldReverseLayout = this.mReverseLayout;
        } else {
            this.mShouldReverseLayout = !this.mReverseLayout;
        }
    }

    /* access modifiers changed from: 0000 */
    public boolean isLayoutRTL() {
        return getLayoutDirection() == 1;
    }

    public boolean getReverseLayout() {
        return this.mReverseLayout;
    }

    public void setMeasuredDimension(Rect rect, int i, int i2) {
        int i3;
        int i4;
        int paddingLeft = getPaddingLeft() + getPaddingRight();
        int paddingTop = getPaddingTop() + getPaddingBottom();
        if (this.mOrientation == 1) {
            i4 = chooseSize(i2, rect.height() + paddingTop, getMinimumHeight());
            i3 = chooseSize(i, (this.mSizePerSpan * this.mSpanCount) + paddingLeft, getMinimumWidth());
        } else {
            i3 = chooseSize(i, rect.width() + paddingLeft, getMinimumWidth());
            i4 = chooseSize(i2, (this.mSizePerSpan * this.mSpanCount) + paddingTop, getMinimumHeight());
        }
        setMeasuredDimension(i3, i4);
    }

    public void onLayoutChildren(Recycler recycler, State state) {
        onLayoutChildren(recycler, state, true);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:63:0x011e, code lost:
        if (checkForGaps() != false) goto L_0x0122;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void onLayoutChildren(android.support.p003v7.widget.RecyclerView.Recycler r10, android.support.p003v7.widget.RecyclerView.State r11, boolean r12) {
        /*
            r9 = this;
            android.support.v7.widget.StaggeredGridLayoutManager$AnchorInfo r0 = r9.mAnchorInfo
            r0.reset()
            android.support.v7.widget.StaggeredGridLayoutManager$SavedState r1 = r9.mPendingSavedState
            r2 = -1
            if (r1 != 0) goto L_0x000e
            int r1 = r9.mPendingScrollPosition
            if (r1 == r2) goto L_0x0018
        L_0x000e:
            int r1 = r11.getItemCount()
            if (r1 != 0) goto L_0x0018
            r9.removeAndRecycleAllViews(r10)
            return
        L_0x0018:
            android.support.v7.widget.StaggeredGridLayoutManager$SavedState r1 = r9.mPendingSavedState
            if (r1 == 0) goto L_0x0020
            r9.applyPendingSavedState(r0)
            goto L_0x0027
        L_0x0020:
            r9.resolveShouldLayoutReverse()
            boolean r1 = r9.mShouldReverseLayout
            r0.mLayoutFromEnd = r1
        L_0x0027:
            r9.updateAnchorInfoForLayout(r11, r0)
            android.support.v7.widget.StaggeredGridLayoutManager$SavedState r1 = r9.mPendingSavedState
            r3 = 1
            if (r1 != 0) goto L_0x0044
            boolean r1 = r0.mLayoutFromEnd
            boolean r4 = r9.mLastLayoutFromEnd
            if (r1 != r4) goto L_0x003d
            boolean r1 = r9.isLayoutRTL()
            boolean r4 = r9.mLastLayoutRTL
            if (r1 == r4) goto L_0x0044
        L_0x003d:
            android.support.v7.widget.StaggeredGridLayoutManager$LazySpanLookup r1 = r9.mLazySpanLookup
            r1.clear()
            r0.mInvalidateOffsets = r3
        L_0x0044:
            int r1 = r9.getChildCount()
            r4 = -2147483648(0xffffffff80000000, float:-0.0)
            r5 = 0
            if (r1 <= 0) goto L_0x0088
            android.support.v7.widget.StaggeredGridLayoutManager$SavedState r1 = r9.mPendingSavedState
            if (r1 == 0) goto L_0x0055
            int r1 = r1.mSpanOffsetsSize
            if (r1 >= r3) goto L_0x0088
        L_0x0055:
            boolean r1 = r0.mInvalidateOffsets
            if (r1 == 0) goto L_0x0075
            r1 = 0
        L_0x005a:
            int r6 = r9.mSpanCount
            if (r1 >= r6) goto L_0x0088
            android.support.v7.widget.StaggeredGridLayoutManager$Span[] r6 = r9.mSpans
            r6 = r6[r1]
            r6.clear()
            int r6 = r0.mOffset
            if (r6 == r4) goto L_0x0072
            android.support.v7.widget.StaggeredGridLayoutManager$Span[] r6 = r9.mSpans
            r6 = r6[r1]
            int r7 = r0.mOffset
            r6.setLine(r7)
        L_0x0072:
            int r1 = r1 + 1
            goto L_0x005a
        L_0x0075:
            r1 = 0
        L_0x0076:
            int r6 = r9.mSpanCount
            if (r1 >= r6) goto L_0x0088
            android.support.v7.widget.StaggeredGridLayoutManager$Span[] r6 = r9.mSpans
            r6 = r6[r1]
            boolean r7 = r9.mShouldReverseLayout
            int r8 = r0.mOffset
            r6.cacheReferenceLineAndClear(r7, r8)
            int r1 = r1 + 1
            goto L_0x0076
        L_0x0088:
            r9.detachAndScrapAttachedViews(r10)
            android.support.v7.widget.LayoutState r1 = r9.mLayoutState
            r1.mRecycle = r5
            r9.mLaidOutInvalidFullSpan = r5
            android.support.v7.widget.OrientationHelper r1 = r9.mSecondaryOrientation
            int r1 = r1.getTotalSpace()
            r9.updateMeasureSpecs(r1)
            int r1 = r0.mPosition
            r9.updateLayoutState(r1, r11)
            boolean r1 = r0.mLayoutFromEnd
            if (r1 == 0) goto L_0x00bf
            r9.setLayoutStateDirection(r2)
            android.support.v7.widget.LayoutState r1 = r9.mLayoutState
            r9.fill(r10, r1, r11)
            r9.setLayoutStateDirection(r3)
            android.support.v7.widget.LayoutState r1 = r9.mLayoutState
            int r6 = r0.mPosition
            android.support.v7.widget.LayoutState r7 = r9.mLayoutState
            int r7 = r7.mItemDirection
            int r6 = r6 + r7
            r1.mCurrentPosition = r6
            android.support.v7.widget.LayoutState r1 = r9.mLayoutState
            r9.fill(r10, r1, r11)
            goto L_0x00da
        L_0x00bf:
            r9.setLayoutStateDirection(r3)
            android.support.v7.widget.LayoutState r1 = r9.mLayoutState
            r9.fill(r10, r1, r11)
            r9.setLayoutStateDirection(r2)
            android.support.v7.widget.LayoutState r1 = r9.mLayoutState
            int r6 = r0.mPosition
            android.support.v7.widget.LayoutState r7 = r9.mLayoutState
            int r7 = r7.mItemDirection
            int r6 = r6 + r7
            r1.mCurrentPosition = r6
            android.support.v7.widget.LayoutState r1 = r9.mLayoutState
            r9.fill(r10, r1, r11)
        L_0x00da:
            r9.repositionToWrapContentIfNecessary()
            int r1 = r9.getChildCount()
            if (r1 <= 0) goto L_0x00f4
            boolean r1 = r9.mShouldReverseLayout
            if (r1 == 0) goto L_0x00ee
            r9.fixEndGap(r10, r11, r3)
            r9.fixStartGap(r10, r11, r5)
            goto L_0x00f4
        L_0x00ee:
            r9.fixStartGap(r10, r11, r3)
            r9.fixEndGap(r10, r11, r5)
        L_0x00f4:
            if (r12 == 0) goto L_0x0127
            boolean r12 = r11.isPreLayout()
            if (r12 != 0) goto L_0x0127
            int r12 = r9.mGapStrategy
            if (r12 == 0) goto L_0x0112
            int r12 = r9.getChildCount()
            if (r12 <= 0) goto L_0x0112
            boolean r12 = r9.mLaidOutInvalidFullSpan
            if (r12 != 0) goto L_0x0110
            android.view.View r12 = r9.hasGapsToFix()
            if (r12 == 0) goto L_0x0112
        L_0x0110:
            r12 = 1
            goto L_0x0113
        L_0x0112:
            r12 = 0
        L_0x0113:
            if (r12 == 0) goto L_0x0121
            java.lang.Runnable r12 = r9.mCheckForGapsRunnable
            r9.removeCallbacks(r12)
            boolean r12 = r9.checkForGaps()
            if (r12 == 0) goto L_0x0121
            goto L_0x0122
        L_0x0121:
            r3 = 0
        L_0x0122:
            r9.mPendingScrollPosition = r2
            r9.mPendingScrollPositionOffset = r4
            goto L_0x0128
        L_0x0127:
            r3 = 0
        L_0x0128:
            boolean r12 = r0.mLayoutFromEnd
            r9.mLastLayoutFromEnd = r12
            boolean r12 = r9.isLayoutRTL()
            r9.mLastLayoutRTL = r12
            r12 = 0
            r9.mPendingSavedState = r12
            if (r3 == 0) goto L_0x013a
            r9.onLayoutChildren(r10, r11, r5)
        L_0x013a:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.p003v7.widget.StaggeredGridLayoutManager.onLayoutChildren(android.support.v7.widget.RecyclerView$Recycler, android.support.v7.widget.RecyclerView$State, boolean):void");
    }

    private void repositionToWrapContentIfNecessary() {
        if (this.mSecondaryOrientation.getMode() != 1073741824) {
            int childCount = getChildCount();
            float f = 0.0f;
            for (int i = 0; i < childCount; i++) {
                View childAt = getChildAt(i);
                float decoratedMeasurement = (float) this.mSecondaryOrientation.getDecoratedMeasurement(childAt);
                if (decoratedMeasurement >= f) {
                    if (((LayoutParams) childAt.getLayoutParams()).isFullSpan()) {
                        decoratedMeasurement = (decoratedMeasurement * 1.0f) / ((float) this.mSpanCount);
                    }
                    f = Math.max(f, decoratedMeasurement);
                }
            }
            int i2 = this.mSizePerSpan;
            int round = Math.round(f * ((float) this.mSpanCount));
            if (this.mSecondaryOrientation.getMode() == Integer.MIN_VALUE) {
                round = Math.min(round, this.mSecondaryOrientation.getTotalSpace());
            }
            updateMeasureSpecs(round);
            if (this.mSizePerSpan != i2) {
                for (int i3 = 0; i3 < childCount; i3++) {
                    View childAt2 = getChildAt(i3);
                    LayoutParams layoutParams = (LayoutParams) childAt2.getLayoutParams();
                    if (!layoutParams.mFullSpan) {
                        if (!isLayoutRTL() || this.mOrientation != 1) {
                            int i4 = layoutParams.mSpan.mIndex * this.mSizePerSpan;
                            int i5 = layoutParams.mSpan.mIndex * i2;
                            if (this.mOrientation == 1) {
                                childAt2.offsetLeftAndRight(i4 - i5);
                            } else {
                                childAt2.offsetTopAndBottom(i4 - i5);
                            }
                        } else {
                            childAt2.offsetLeftAndRight(((-((this.mSpanCount - 1) - layoutParams.mSpan.mIndex)) * this.mSizePerSpan) - ((-((this.mSpanCount - 1) - layoutParams.mSpan.mIndex)) * i2));
                        }
                    }
                }
            }
        }
    }

    private void applyPendingSavedState(AnchorInfo anchorInfo) {
        int i;
        if (this.mPendingSavedState.mSpanOffsetsSize > 0) {
            if (this.mPendingSavedState.mSpanOffsetsSize == this.mSpanCount) {
                for (int i2 = 0; i2 < this.mSpanCount; i2++) {
                    this.mSpans[i2].clear();
                    int i3 = this.mPendingSavedState.mSpanOffsets[i2];
                    if (i3 != Integer.MIN_VALUE) {
                        if (this.mPendingSavedState.mAnchorLayoutFromEnd) {
                            i = this.mPrimaryOrientation.getEndAfterPadding();
                        } else {
                            i = this.mPrimaryOrientation.getStartAfterPadding();
                        }
                        i3 += i;
                    }
                    this.mSpans[i2].setLine(i3);
                }
            } else {
                this.mPendingSavedState.invalidateSpanInfo();
                SavedState savedState = this.mPendingSavedState;
                savedState.mAnchorPosition = savedState.mVisibleAnchorPosition;
            }
        }
        this.mLastLayoutRTL = this.mPendingSavedState.mLastLayoutRTL;
        setReverseLayout(this.mPendingSavedState.mReverseLayout);
        resolveShouldLayoutReverse();
        if (this.mPendingSavedState.mAnchorPosition != -1) {
            this.mPendingScrollPosition = this.mPendingSavedState.mAnchorPosition;
            anchorInfo.mLayoutFromEnd = this.mPendingSavedState.mAnchorLayoutFromEnd;
        } else {
            anchorInfo.mLayoutFromEnd = this.mShouldReverseLayout;
        }
        if (this.mPendingSavedState.mSpanLookupSize > 1) {
            this.mLazySpanLookup.mData = this.mPendingSavedState.mSpanLookup;
            this.mLazySpanLookup.mFullSpanItems = this.mPendingSavedState.mFullSpanItems;
        }
    }

    /* access modifiers changed from: 0000 */
    public void updateAnchorInfoForLayout(State state, AnchorInfo anchorInfo) {
        if (!updateAnchorFromPendingData(state, anchorInfo) && !updateAnchorFromChildren(state, anchorInfo)) {
            anchorInfo.assignCoordinateFromPadding();
            anchorInfo.mPosition = 0;
        }
    }

    private boolean updateAnchorFromChildren(State state, AnchorInfo anchorInfo) {
        anchorInfo.mPosition = this.mLastLayoutFromEnd ? findLastReferenceChildPosition(state.getItemCount()) : findFirstReferenceChildPosition(state.getItemCount());
        anchorInfo.mOffset = Integer.MIN_VALUE;
        return true;
    }

    /* access modifiers changed from: 0000 */
    public boolean updateAnchorFromPendingData(State state, AnchorInfo anchorInfo) {
        boolean z = false;
        if (!state.isPreLayout()) {
            int i = this.mPendingScrollPosition;
            if (i != -1) {
                if (i < 0 || i >= state.getItemCount()) {
                    this.mPendingScrollPosition = -1;
                    this.mPendingScrollPositionOffset = Integer.MIN_VALUE;
                } else {
                    SavedState savedState = this.mPendingSavedState;
                    if (savedState == null || savedState.mAnchorPosition == -1 || this.mPendingSavedState.mSpanOffsetsSize < 1) {
                        View findViewByPosition = findViewByPosition(this.mPendingScrollPosition);
                        if (findViewByPosition != null) {
                            anchorInfo.mPosition = this.mShouldReverseLayout ? getLastChildPosition() : getFirstChildPosition();
                            if (this.mPendingScrollPositionOffset != Integer.MIN_VALUE) {
                                if (anchorInfo.mLayoutFromEnd) {
                                    anchorInfo.mOffset = (this.mPrimaryOrientation.getEndAfterPadding() - this.mPendingScrollPositionOffset) - this.mPrimaryOrientation.getDecoratedEnd(findViewByPosition);
                                } else {
                                    anchorInfo.mOffset = (this.mPrimaryOrientation.getStartAfterPadding() + this.mPendingScrollPositionOffset) - this.mPrimaryOrientation.getDecoratedStart(findViewByPosition);
                                }
                                return true;
                            } else if (this.mPrimaryOrientation.getDecoratedMeasurement(findViewByPosition) > this.mPrimaryOrientation.getTotalSpace()) {
                                anchorInfo.mOffset = anchorInfo.mLayoutFromEnd ? this.mPrimaryOrientation.getEndAfterPadding() : this.mPrimaryOrientation.getStartAfterPadding();
                                return true;
                            } else {
                                int decoratedStart = this.mPrimaryOrientation.getDecoratedStart(findViewByPosition) - this.mPrimaryOrientation.getStartAfterPadding();
                                if (decoratedStart < 0) {
                                    anchorInfo.mOffset = -decoratedStart;
                                    return true;
                                }
                                int endAfterPadding = this.mPrimaryOrientation.getEndAfterPadding() - this.mPrimaryOrientation.getDecoratedEnd(findViewByPosition);
                                if (endAfterPadding < 0) {
                                    anchorInfo.mOffset = endAfterPadding;
                                    return true;
                                }
                                anchorInfo.mOffset = Integer.MIN_VALUE;
                            }
                        } else {
                            anchorInfo.mPosition = this.mPendingScrollPosition;
                            int i2 = this.mPendingScrollPositionOffset;
                            if (i2 == Integer.MIN_VALUE) {
                                if (calculateScrollDirectionForPosition(anchorInfo.mPosition) == 1) {
                                    z = true;
                                }
                                anchorInfo.mLayoutFromEnd = z;
                                anchorInfo.assignCoordinateFromPadding();
                            } else {
                                anchorInfo.assignCoordinateFromPadding(i2);
                            }
                            anchorInfo.mInvalidateOffsets = true;
                        }
                    } else {
                        anchorInfo.mOffset = Integer.MIN_VALUE;
                        anchorInfo.mPosition = this.mPendingScrollPosition;
                    }
                    return true;
                }
            }
        }
        return false;
    }

    /* access modifiers changed from: 0000 */
    public void updateMeasureSpecs(int i) {
        this.mSizePerSpan = i / this.mSpanCount;
        this.mFullSizeSpec = MeasureSpec.makeMeasureSpec(i, this.mSecondaryOrientation.getMode());
    }

    public boolean supportsPredictiveItemAnimations() {
        return this.mPendingSavedState == null;
    }

    public int[] findFirstVisibleItemPositions(int[] iArr) {
        if (iArr == null) {
            iArr = new int[this.mSpanCount];
        } else if (iArr.length < this.mSpanCount) {
            StringBuilder sb = new StringBuilder();
            sb.append("Provided int[]'s size must be more than or equal to span count. Expected:");
            sb.append(this.mSpanCount);
            sb.append(", array size:");
            sb.append(iArr.length);
            throw new IllegalArgumentException(sb.toString());
        }
        for (int i = 0; i < this.mSpanCount; i++) {
            iArr[i] = this.mSpans[i].findFirstVisibleItemPosition();
        }
        return iArr;
    }

    public int[] findFirstCompletelyVisibleItemPositions(int[] iArr) {
        if (iArr == null) {
            iArr = new int[this.mSpanCount];
        } else if (iArr.length < this.mSpanCount) {
            StringBuilder sb = new StringBuilder();
            sb.append("Provided int[]'s size must be more than or equal to span count. Expected:");
            sb.append(this.mSpanCount);
            sb.append(", array size:");
            sb.append(iArr.length);
            throw new IllegalArgumentException(sb.toString());
        }
        for (int i = 0; i < this.mSpanCount; i++) {
            iArr[i] = this.mSpans[i].findFirstCompletelyVisibleItemPosition();
        }
        return iArr;
    }

    public int[] findLastVisibleItemPositions(int[] iArr) {
        if (iArr == null) {
            iArr = new int[this.mSpanCount];
        } else if (iArr.length < this.mSpanCount) {
            StringBuilder sb = new StringBuilder();
            sb.append("Provided int[]'s size must be more than or equal to span count. Expected:");
            sb.append(this.mSpanCount);
            sb.append(", array size:");
            sb.append(iArr.length);
            throw new IllegalArgumentException(sb.toString());
        }
        for (int i = 0; i < this.mSpanCount; i++) {
            iArr[i] = this.mSpans[i].findLastVisibleItemPosition();
        }
        return iArr;
    }

    public int[] findLastCompletelyVisibleItemPositions(int[] iArr) {
        if (iArr == null) {
            iArr = new int[this.mSpanCount];
        } else if (iArr.length < this.mSpanCount) {
            StringBuilder sb = new StringBuilder();
            sb.append("Provided int[]'s size must be more than or equal to span count. Expected:");
            sb.append(this.mSpanCount);
            sb.append(", array size:");
            sb.append(iArr.length);
            throw new IllegalArgumentException(sb.toString());
        }
        for (int i = 0; i < this.mSpanCount; i++) {
            iArr[i] = this.mSpans[i].findLastCompletelyVisibleItemPosition();
        }
        return iArr;
    }

    public int computeHorizontalScrollOffset(State state) {
        return computeScrollOffset(state);
    }

    private int computeScrollOffset(State state) {
        if (getChildCount() == 0) {
            return 0;
        }
        OrientationHelper orientationHelper = this.mPrimaryOrientation;
        View findFirstVisibleItemClosestToStart = findFirstVisibleItemClosestToStart(!this.mSmoothScrollbarEnabled, true);
        return ScrollbarHelper.computeScrollOffset(state, orientationHelper, findFirstVisibleItemClosestToStart, findFirstVisibleItemClosestToEnd(!this.mSmoothScrollbarEnabled, true), this, this.mSmoothScrollbarEnabled, this.mShouldReverseLayout);
    }

    public int computeVerticalScrollOffset(State state) {
        return computeScrollOffset(state);
    }

    public int computeHorizontalScrollExtent(State state) {
        return computeScrollExtent(state);
    }

    private int computeScrollExtent(State state) {
        if (getChildCount() == 0) {
            return 0;
        }
        OrientationHelper orientationHelper = this.mPrimaryOrientation;
        View findFirstVisibleItemClosestToStart = findFirstVisibleItemClosestToStart(!this.mSmoothScrollbarEnabled, true);
        return ScrollbarHelper.computeScrollExtent(state, orientationHelper, findFirstVisibleItemClosestToStart, findFirstVisibleItemClosestToEnd(!this.mSmoothScrollbarEnabled, true), this, this.mSmoothScrollbarEnabled);
    }

    public int computeVerticalScrollExtent(State state) {
        return computeScrollExtent(state);
    }

    public int computeHorizontalScrollRange(State state) {
        return computeScrollRange(state);
    }

    private int computeScrollRange(State state) {
        if (getChildCount() == 0) {
            return 0;
        }
        OrientationHelper orientationHelper = this.mPrimaryOrientation;
        View findFirstVisibleItemClosestToStart = findFirstVisibleItemClosestToStart(!this.mSmoothScrollbarEnabled, true);
        return ScrollbarHelper.computeScrollRange(state, orientationHelper, findFirstVisibleItemClosestToStart, findFirstVisibleItemClosestToEnd(!this.mSmoothScrollbarEnabled, true), this, this.mSmoothScrollbarEnabled);
    }

    public int computeVerticalScrollRange(State state) {
        return computeScrollRange(state);
    }

    private void measureChildWithDecorationsAndMargin(View view, LayoutParams layoutParams, boolean z) {
        if (layoutParams.mFullSpan) {
            if (this.mOrientation == 1) {
                measureChildWithDecorationsAndMargin(view, this.mFullSizeSpec, getChildMeasureSpec(getHeight(), getHeightMode(), 0, layoutParams.height, true), z);
            } else {
                measureChildWithDecorationsAndMargin(view, getChildMeasureSpec(getWidth(), getWidthMode(), 0, layoutParams.width, true), this.mFullSizeSpec, z);
            }
        } else if (this.mOrientation == 1) {
            measureChildWithDecorationsAndMargin(view, getChildMeasureSpec(this.mSizePerSpan, getWidthMode(), 0, layoutParams.width, false), getChildMeasureSpec(getHeight(), getHeightMode(), 0, layoutParams.height, true), z);
        } else {
            measureChildWithDecorationsAndMargin(view, getChildMeasureSpec(getWidth(), getWidthMode(), 0, layoutParams.width, true), getChildMeasureSpec(this.mSizePerSpan, getHeightMode(), 0, layoutParams.height, false), z);
        }
    }

    private void measureChildWithDecorationsAndMargin(View view, int i, int i2, boolean z) {
        calculateItemDecorationsForChild(view, this.mTmpRect);
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        int updateSpecWithExtra = updateSpecWithExtra(i, layoutParams.leftMargin + this.mTmpRect.left, layoutParams.rightMargin + this.mTmpRect.right);
        int updateSpecWithExtra2 = updateSpecWithExtra(i2, layoutParams.topMargin + this.mTmpRect.top, layoutParams.bottomMargin + this.mTmpRect.bottom);
        if (z ? shouldReMeasureChild(view, updateSpecWithExtra, updateSpecWithExtra2, layoutParams) : shouldMeasureChild(view, updateSpecWithExtra, updateSpecWithExtra2, layoutParams)) {
            view.measure(updateSpecWithExtra, updateSpecWithExtra2);
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

    public void onRestoreInstanceState(Parcelable parcelable) {
        if (parcelable instanceof SavedState) {
            this.mPendingSavedState = (SavedState) parcelable;
            requestLayout();
        }
    }

    public Parcelable onSaveInstanceState() {
        int i;
        int i2;
        SavedState savedState = this.mPendingSavedState;
        if (savedState != null) {
            return new SavedState(savedState);
        }
        SavedState savedState2 = new SavedState();
        savedState2.mReverseLayout = this.mReverseLayout;
        savedState2.mAnchorLayoutFromEnd = this.mLastLayoutFromEnd;
        savedState2.mLastLayoutRTL = this.mLastLayoutRTL;
        LazySpanLookup lazySpanLookup = this.mLazySpanLookup;
        if (lazySpanLookup == null || lazySpanLookup.mData == null) {
            savedState2.mSpanLookupSize = 0;
        } else {
            savedState2.mSpanLookup = this.mLazySpanLookup.mData;
            savedState2.mSpanLookupSize = savedState2.mSpanLookup.length;
            savedState2.mFullSpanItems = this.mLazySpanLookup.mFullSpanItems;
        }
        if (getChildCount() > 0) {
            savedState2.mAnchorPosition = this.mLastLayoutFromEnd ? getLastChildPosition() : getFirstChildPosition();
            savedState2.mVisibleAnchorPosition = findFirstVisibleItemPositionInt();
            int i3 = this.mSpanCount;
            savedState2.mSpanOffsetsSize = i3;
            savedState2.mSpanOffsets = new int[i3];
            for (int i4 = 0; i4 < this.mSpanCount; i4++) {
                if (this.mLastLayoutFromEnd) {
                    i = this.mSpans[i4].getEndLine(Integer.MIN_VALUE);
                    if (i != Integer.MIN_VALUE) {
                        i2 = this.mPrimaryOrientation.getEndAfterPadding();
                    } else {
                        savedState2.mSpanOffsets[i4] = i;
                    }
                } else {
                    i = this.mSpans[i4].getStartLine(Integer.MIN_VALUE);
                    if (i != Integer.MIN_VALUE) {
                        i2 = this.mPrimaryOrientation.getStartAfterPadding();
                    } else {
                        savedState2.mSpanOffsets[i4] = i;
                    }
                }
                i -= i2;
                savedState2.mSpanOffsets[i4] = i;
            }
        } else {
            savedState2.mAnchorPosition = -1;
            savedState2.mVisibleAnchorPosition = -1;
            savedState2.mSpanOffsetsSize = 0;
        }
        return savedState2;
    }

    public void onInitializeAccessibilityNodeInfoForItem(Recycler recycler, State state, View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
        android.view.ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (!(layoutParams instanceof LayoutParams)) {
            super.onInitializeAccessibilityNodeInfoForItem(view, accessibilityNodeInfoCompat);
            return;
        }
        LayoutParams layoutParams2 = (LayoutParams) layoutParams;
        if (this.mOrientation == 0) {
            accessibilityNodeInfoCompat.setCollectionItemInfo(CollectionItemInfoCompat.obtain(layoutParams2.getSpanIndex(), layoutParams2.mFullSpan ? this.mSpanCount : 1, -1, -1, layoutParams2.mFullSpan, false));
        } else {
            accessibilityNodeInfoCompat.setCollectionItemInfo(CollectionItemInfoCompat.obtain(-1, -1, layoutParams2.getSpanIndex(), layoutParams2.mFullSpan ? this.mSpanCount : 1, layoutParams2.mFullSpan, false));
        }
    }

    public void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(accessibilityEvent);
        if (getChildCount() > 0) {
            AccessibilityRecordCompat asRecord = AccessibilityEventCompat.asRecord(accessibilityEvent);
            View findFirstVisibleItemClosestToStart = findFirstVisibleItemClosestToStart(false, true);
            View findFirstVisibleItemClosestToEnd = findFirstVisibleItemClosestToEnd(false, true);
            if (findFirstVisibleItemClosestToStart != null && findFirstVisibleItemClosestToEnd != null) {
                int position = getPosition(findFirstVisibleItemClosestToStart);
                int position2 = getPosition(findFirstVisibleItemClosestToEnd);
                if (position < position2) {
                    asRecord.setFromIndex(position);
                    asRecord.setToIndex(position2);
                    return;
                }
                asRecord.setFromIndex(position2);
                asRecord.setToIndex(position);
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public int findFirstVisibleItemPositionInt() {
        View findFirstVisibleItemClosestToEnd = this.mShouldReverseLayout ? findFirstVisibleItemClosestToEnd(true, true) : findFirstVisibleItemClosestToStart(true, true);
        if (findFirstVisibleItemClosestToEnd == null) {
            return -1;
        }
        return getPosition(findFirstVisibleItemClosestToEnd);
    }

    public int getRowCountForAccessibility(Recycler recycler, State state) {
        if (this.mOrientation == 0) {
            return this.mSpanCount;
        }
        return super.getRowCountForAccessibility(recycler, state);
    }

    public int getColumnCountForAccessibility(Recycler recycler, State state) {
        if (this.mOrientation == 1) {
            return this.mSpanCount;
        }
        return super.getColumnCountForAccessibility(recycler, state);
    }

    /* access modifiers changed from: 0000 */
    public View findFirstVisibleItemClosestToStart(boolean z, boolean z2) {
        int startAfterPadding = this.mPrimaryOrientation.getStartAfterPadding();
        int endAfterPadding = this.mPrimaryOrientation.getEndAfterPadding();
        int childCount = getChildCount();
        View view = null;
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            int decoratedStart = this.mPrimaryOrientation.getDecoratedStart(childAt);
            if (this.mPrimaryOrientation.getDecoratedEnd(childAt) > startAfterPadding && decoratedStart < endAfterPadding) {
                if (decoratedStart >= startAfterPadding || !z) {
                    return childAt;
                }
                if (z2 && view == null) {
                    view = childAt;
                }
            }
        }
        return view;
    }

    /* access modifiers changed from: 0000 */
    public View findFirstVisibleItemClosestToEnd(boolean z, boolean z2) {
        int startAfterPadding = this.mPrimaryOrientation.getStartAfterPadding();
        int endAfterPadding = this.mPrimaryOrientation.getEndAfterPadding();
        View view = null;
        for (int childCount = getChildCount() - 1; childCount >= 0; childCount--) {
            View childAt = getChildAt(childCount);
            int decoratedStart = this.mPrimaryOrientation.getDecoratedStart(childAt);
            int decoratedEnd = this.mPrimaryOrientation.getDecoratedEnd(childAt);
            if (decoratedEnd > startAfterPadding && decoratedStart < endAfterPadding) {
                if (decoratedEnd <= endAfterPadding || !z) {
                    return childAt;
                }
                if (z2 && view == null) {
                    view = childAt;
                }
            }
        }
        return view;
    }

    private void fixEndGap(Recycler recycler, State state, boolean z) {
        int maxEnd = getMaxEnd(Integer.MIN_VALUE);
        if (maxEnd != Integer.MIN_VALUE) {
            int endAfterPadding = this.mPrimaryOrientation.getEndAfterPadding() - maxEnd;
            if (endAfterPadding > 0) {
                int i = endAfterPadding - (-scrollBy(-endAfterPadding, recycler, state));
                if (z && i > 0) {
                    this.mPrimaryOrientation.offsetChildren(i);
                }
            }
        }
    }

    private void fixStartGap(Recycler recycler, State state, boolean z) {
        int minStart = getMinStart(ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED);
        if (minStart != Integer.MAX_VALUE) {
            int startAfterPadding = minStart - this.mPrimaryOrientation.getStartAfterPadding();
            if (startAfterPadding > 0) {
                int scrollBy = startAfterPadding - scrollBy(startAfterPadding, recycler, state);
                if (z && scrollBy > 0) {
                    this.mPrimaryOrientation.offsetChildren(-scrollBy);
                }
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:15:0x0036  */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x004d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateLayoutState(int r5, android.support.p003v7.widget.RecyclerView.State r6) {
        /*
            r4 = this;
            android.support.v7.widget.LayoutState r0 = r4.mLayoutState
            r1 = 0
            r0.mAvailable = r1
            r0.mCurrentPosition = r5
            boolean r0 = r4.isSmoothScrolling()
            r2 = 1
            if (r0 == 0) goto L_0x002e
            int r6 = r6.getTargetScrollPosition()
            r0 = -1
            if (r6 == r0) goto L_0x002e
            boolean r0 = r4.mShouldReverseLayout
            if (r6 >= r5) goto L_0x001b
            r5 = 1
            goto L_0x001c
        L_0x001b:
            r5 = 0
        L_0x001c:
            if (r0 != r5) goto L_0x0025
            android.support.v7.widget.OrientationHelper r5 = r4.mPrimaryOrientation
            int r5 = r5.getTotalSpace()
            goto L_0x002f
        L_0x0025:
            android.support.v7.widget.OrientationHelper r5 = r4.mPrimaryOrientation
            int r5 = r5.getTotalSpace()
            r6 = r5
            r5 = 0
            goto L_0x0030
        L_0x002e:
            r5 = 0
        L_0x002f:
            r6 = 0
        L_0x0030:
            boolean r0 = r4.getClipToPadding()
            if (r0 == 0) goto L_0x004d
            android.support.v7.widget.LayoutState r0 = r4.mLayoutState
            android.support.v7.widget.OrientationHelper r3 = r4.mPrimaryOrientation
            int r3 = r3.getStartAfterPadding()
            int r3 = r3 - r6
            r0.mStartLine = r3
            android.support.v7.widget.LayoutState r6 = r4.mLayoutState
            android.support.v7.widget.OrientationHelper r0 = r4.mPrimaryOrientation
            int r0 = r0.getEndAfterPadding()
            int r0 = r0 + r5
            r6.mEndLine = r0
            goto L_0x005d
        L_0x004d:
            android.support.v7.widget.LayoutState r0 = r4.mLayoutState
            android.support.v7.widget.OrientationHelper r3 = r4.mPrimaryOrientation
            int r3 = r3.getEnd()
            int r3 = r3 + r5
            r0.mEndLine = r3
            android.support.v7.widget.LayoutState r5 = r4.mLayoutState
            int r6 = -r6
            r5.mStartLine = r6
        L_0x005d:
            android.support.v7.widget.LayoutState r5 = r4.mLayoutState
            r5.mStopInFocusable = r1
            r5.mRecycle = r2
            android.support.v7.widget.OrientationHelper r6 = r4.mPrimaryOrientation
            int r6 = r6.getMode()
            if (r6 != 0) goto L_0x0074
            android.support.v7.widget.OrientationHelper r6 = r4.mPrimaryOrientation
            int r6 = r6.getEnd()
            if (r6 != 0) goto L_0x0074
            r1 = 1
        L_0x0074:
            r5.mInfinite = r1
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.p003v7.widget.StaggeredGridLayoutManager.updateLayoutState(int, android.support.v7.widget.RecyclerView$State):void");
    }

    private void setLayoutStateDirection(int i) {
        LayoutState layoutState = this.mLayoutState;
        layoutState.mLayoutDirection = i;
        int i2 = 1;
        if (this.mShouldReverseLayout != (i == -1)) {
            i2 = -1;
        }
        layoutState.mItemDirection = i2;
    }

    public void offsetChildrenHorizontal(int i) {
        super.offsetChildrenHorizontal(i);
        for (int i2 = 0; i2 < this.mSpanCount; i2++) {
            this.mSpans[i2].onOffset(i);
        }
    }

    public void offsetChildrenVertical(int i) {
        super.offsetChildrenVertical(i);
        for (int i2 = 0; i2 < this.mSpanCount; i2++) {
            this.mSpans[i2].onOffset(i);
        }
    }

    public void onItemsRemoved(RecyclerView recyclerView, int i, int i2) {
        handleUpdate(i, i2, 2);
    }

    public void onItemsAdded(RecyclerView recyclerView, int i, int i2) {
        handleUpdate(i, i2, 1);
    }

    public void onItemsChanged(RecyclerView recyclerView) {
        this.mLazySpanLookup.clear();
        requestLayout();
    }

    public void onItemsMoved(RecyclerView recyclerView, int i, int i2, int i3) {
        handleUpdate(i, i2, 8);
    }

    public void onItemsUpdated(RecyclerView recyclerView, int i, int i2, Object obj) {
        handleUpdate(i, i2, 4);
    }

    /* JADX WARNING: Removed duplicated region for block: B:13:0x0027  */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x003e  */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x0045 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x0046  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void handleUpdate(int r7, int r8, int r9) {
        /*
            r6 = this;
            boolean r0 = r6.mShouldReverseLayout
            if (r0 == 0) goto L_0x0009
            int r0 = r6.getLastChildPosition()
            goto L_0x000d
        L_0x0009:
            int r0 = r6.getFirstChildPosition()
        L_0x000d:
            r1 = 8
            if (r9 != r1) goto L_0x001b
            if (r7 >= r8) goto L_0x0016
            int r2 = r8 + 1
            goto L_0x001d
        L_0x0016:
            int r2 = r7 + 1
            r3 = r2
            r2 = r8
            goto L_0x001f
        L_0x001b:
            int r2 = r7 + r8
        L_0x001d:
            r3 = r2
            r2 = r7
        L_0x001f:
            android.support.v7.widget.StaggeredGridLayoutManager$LazySpanLookup r4 = r6.mLazySpanLookup
            r4.invalidateAfter(r2)
            r4 = 1
            if (r9 == r4) goto L_0x003e
            r5 = 2
            if (r9 == r5) goto L_0x0038
            if (r9 == r1) goto L_0x002d
            goto L_0x0043
        L_0x002d:
            android.support.v7.widget.StaggeredGridLayoutManager$LazySpanLookup r9 = r6.mLazySpanLookup
            r9.offsetForRemoval(r7, r4)
            android.support.v7.widget.StaggeredGridLayoutManager$LazySpanLookup r7 = r6.mLazySpanLookup
            r7.offsetForAddition(r8, r4)
            goto L_0x0043
        L_0x0038:
            android.support.v7.widget.StaggeredGridLayoutManager$LazySpanLookup r9 = r6.mLazySpanLookup
            r9.offsetForRemoval(r7, r8)
            goto L_0x0043
        L_0x003e:
            android.support.v7.widget.StaggeredGridLayoutManager$LazySpanLookup r9 = r6.mLazySpanLookup
            r9.offsetForAddition(r7, r8)
        L_0x0043:
            if (r3 > r0) goto L_0x0046
            return
        L_0x0046:
            boolean r7 = r6.mShouldReverseLayout
            if (r7 == 0) goto L_0x004f
            int r7 = r6.getFirstChildPosition()
            goto L_0x0053
        L_0x004f:
            int r7 = r6.getLastChildPosition()
        L_0x0053:
            if (r2 > r7) goto L_0x0058
            r6.requestLayout()
        L_0x0058:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.p003v7.widget.StaggeredGridLayoutManager.handleUpdate(int, int, int):void");
    }

    /* JADX WARNING: type inference failed for: r9v0 */
    /* JADX WARNING: type inference failed for: r9v1, types: [int, boolean] */
    /* JADX WARNING: type inference failed for: r9v4 */
    /* JADX WARNING: type inference failed for: r9v9 */
    /* JADX WARNING: type inference failed for: r9v10 */
    /* JADX WARNING: Multi-variable type inference failed. Error: jadx.core.utils.exceptions.JadxRuntimeException: No candidate types for var: r9v1, types: [int, boolean]
      assigns: []
      uses: [boolean, int, ?[int, short, byte, char]]
      mth insns count: 222
    	at jadx.core.dex.visitors.typeinference.TypeSearch.fillTypeCandidates(TypeSearch.java:237)
    	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
    	at jadx.core.dex.visitors.typeinference.TypeSearch.run(TypeSearch.java:53)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runMultiVariableSearch(TypeInferenceVisitor.java:99)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:92)
    	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
    	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$1(DepthTraversal.java:14)
    	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
    	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
    	at jadx.core.ProcessClass.process(ProcessClass.java:30)
    	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:311)
    	at jadx.api.JavaClass.decompile(JavaClass.java:62)
    	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:217)
     */
    /* JADX WARNING: Unknown variable types count: 3 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int fill(android.support.p003v7.widget.RecyclerView.Recycler r17, android.support.p003v7.widget.LayoutState r18, android.support.p003v7.widget.RecyclerView.State r19) {
        /*
            r16 = this;
            r6 = r16
            r7 = r17
            r8 = r18
            java.util.BitSet r0 = r6.mRemainingSpans
            int r1 = r6.mSpanCount
            r9 = 0
            r10 = 1
            r0.set(r9, r1, r10)
            android.support.v7.widget.LayoutState r0 = r6.mLayoutState
            boolean r0 = r0.mInfinite
            if (r0 == 0) goto L_0x0025
            int r0 = r8.mLayoutDirection
            if (r0 != r10) goto L_0x0020
            r0 = 2147483647(0x7fffffff, float:NaN)
            r11 = 2147483647(0x7fffffff, float:NaN)
            goto L_0x0035
        L_0x0020:
            r0 = -2147483648(0xffffffff80000000, float:-0.0)
            r11 = -2147483648(0xffffffff80000000, float:-0.0)
            goto L_0x0035
        L_0x0025:
            int r0 = r8.mLayoutDirection
            if (r0 != r10) goto L_0x002f
            int r0 = r8.mEndLine
            int r1 = r8.mAvailable
            int r0 = r0 + r1
            goto L_0x0034
        L_0x002f:
            int r0 = r8.mStartLine
            int r1 = r8.mAvailable
            int r0 = r0 - r1
        L_0x0034:
            r11 = r0
        L_0x0035:
            int r0 = r8.mLayoutDirection
            r6.updateAllRemainingSpans(r0, r11)
            boolean r0 = r6.mShouldReverseLayout
            if (r0 == 0) goto L_0x0045
            android.support.v7.widget.OrientationHelper r0 = r6.mPrimaryOrientation
            int r0 = r0.getEndAfterPadding()
            goto L_0x004b
        L_0x0045:
            android.support.v7.widget.OrientationHelper r0 = r6.mPrimaryOrientation
            int r0 = r0.getStartAfterPadding()
        L_0x004b:
            r12 = r0
            r0 = 0
        L_0x004d:
            boolean r1 = r18.hasMore(r19)
            r2 = -1
            if (r1 == 0) goto L_0x01ce
            android.support.v7.widget.LayoutState r1 = r6.mLayoutState
            boolean r1 = r1.mInfinite
            if (r1 != 0) goto L_0x0062
            java.util.BitSet r1 = r6.mRemainingSpans
            boolean r1 = r1.isEmpty()
            if (r1 != 0) goto L_0x01ce
        L_0x0062:
            android.view.View r13 = r8.next(r7)
            android.view.ViewGroup$LayoutParams r0 = r13.getLayoutParams()
            r14 = r0
            android.support.v7.widget.StaggeredGridLayoutManager$LayoutParams r14 = (android.support.p003v7.widget.StaggeredGridLayoutManager.LayoutParams) r14
            int r0 = r14.getViewLayoutPosition()
            android.support.v7.widget.StaggeredGridLayoutManager$LazySpanLookup r1 = r6.mLazySpanLookup
            int r1 = r1.getSpan(r0)
            if (r1 != r2) goto L_0x007b
            r3 = 1
            goto L_0x007c
        L_0x007b:
            r3 = 0
        L_0x007c:
            if (r3 == 0) goto L_0x0091
            boolean r1 = r14.mFullSpan
            if (r1 == 0) goto L_0x0087
            android.support.v7.widget.StaggeredGridLayoutManager$Span[] r1 = r6.mSpans
            r1 = r1[r9]
            goto L_0x008b
        L_0x0087:
            android.support.v7.widget.StaggeredGridLayoutManager$Span r1 = r6.getNextSpan(r8)
        L_0x008b:
            android.support.v7.widget.StaggeredGridLayoutManager$LazySpanLookup r4 = r6.mLazySpanLookup
            r4.setSpan(r0, r1)
            goto L_0x0095
        L_0x0091:
            android.support.v7.widget.StaggeredGridLayoutManager$Span[] r4 = r6.mSpans
            r1 = r4[r1]
        L_0x0095:
            r15 = r1
            r14.mSpan = r15
            int r1 = r8.mLayoutDirection
            if (r1 != r10) goto L_0x00a0
            r6.addView(r13)
            goto L_0x00a3
        L_0x00a0:
            r6.addView(r13, r9)
        L_0x00a3:
            r6.measureChildWithDecorationsAndMargin(r13, r14, r9)
            int r1 = r8.mLayoutDirection
            if (r1 != r10) goto L_0x00d4
            boolean r1 = r14.mFullSpan
            if (r1 == 0) goto L_0x00b3
            int r1 = r6.getMaxEnd(r12)
            goto L_0x00b7
        L_0x00b3:
            int r1 = r15.getEndLine(r12)
        L_0x00b7:
            android.support.v7.widget.OrientationHelper r4 = r6.mPrimaryOrientation
            int r4 = r4.getDecoratedMeasurement(r13)
            int r4 = r4 + r1
            if (r3 == 0) goto L_0x00d1
            boolean r5 = r14.mFullSpan
            if (r5 == 0) goto L_0x00d1
            android.support.v7.widget.StaggeredGridLayoutManager$LazySpanLookup$FullSpanItem r5 = r6.createFullSpanItemFromEnd(r1)
            r5.mGapDir = r2
            r5.mPosition = r0
            android.support.v7.widget.StaggeredGridLayoutManager$LazySpanLookup r9 = r6.mLazySpanLookup
            r9.addFullSpanItem(r5)
        L_0x00d1:
            r5 = r4
            r4 = r1
            goto L_0x00fd
        L_0x00d4:
            boolean r1 = r14.mFullSpan
            if (r1 == 0) goto L_0x00dd
            int r1 = r6.getMinStart(r12)
            goto L_0x00e1
        L_0x00dd:
            int r1 = r15.getStartLine(r12)
        L_0x00e1:
            android.support.v7.widget.OrientationHelper r4 = r6.mPrimaryOrientation
            int r4 = r4.getDecoratedMeasurement(r13)
            int r4 = r1 - r4
            if (r3 == 0) goto L_0x00fc
            boolean r5 = r14.mFullSpan
            if (r5 == 0) goto L_0x00fc
            android.support.v7.widget.StaggeredGridLayoutManager$LazySpanLookup$FullSpanItem r5 = r6.createFullSpanItemFromStart(r1)
            r5.mGapDir = r10
            r5.mPosition = r0
            android.support.v7.widget.StaggeredGridLayoutManager$LazySpanLookup r9 = r6.mLazySpanLookup
            r9.addFullSpanItem(r5)
        L_0x00fc:
            r5 = r1
        L_0x00fd:
            boolean r1 = r14.mFullSpan
            if (r1 == 0) goto L_0x0126
            int r1 = r8.mItemDirection
            if (r1 != r2) goto L_0x0126
            if (r3 == 0) goto L_0x010a
            r6.mLaidOutInvalidFullSpan = r10
            goto L_0x0126
        L_0x010a:
            int r1 = r8.mLayoutDirection
            if (r1 != r10) goto L_0x0113
            boolean r1 = r16.areAllEndsEqual()
            goto L_0x0117
        L_0x0113:
            boolean r1 = r16.areAllStartsEqual()
        L_0x0117:
            r1 = r1 ^ r10
            if (r1 == 0) goto L_0x0126
            android.support.v7.widget.StaggeredGridLayoutManager$LazySpanLookup r1 = r6.mLazySpanLookup
            android.support.v7.widget.StaggeredGridLayoutManager$LazySpanLookup$FullSpanItem r0 = r1.getFullSpanItem(r0)
            if (r0 == 0) goto L_0x0124
            r0.mHasUnwantedGapAfter = r10
        L_0x0124:
            r6.mLaidOutInvalidFullSpan = r10
        L_0x0126:
            r6.attachViewToSpans(r13, r14, r8)
            boolean r0 = r16.isLayoutRTL()
            if (r0 == 0) goto L_0x015a
            int r0 = r6.mOrientation
            if (r0 != r10) goto L_0x015a
            boolean r0 = r14.mFullSpan
            if (r0 == 0) goto L_0x013e
            android.support.v7.widget.OrientationHelper r0 = r6.mSecondaryOrientation
            int r0 = r0.getEndAfterPadding()
            goto L_0x014f
        L_0x013e:
            android.support.v7.widget.OrientationHelper r0 = r6.mSecondaryOrientation
            int r0 = r0.getEndAfterPadding()
            int r1 = r6.mSpanCount
            int r1 = r1 - r10
            int r2 = r15.mIndex
            int r1 = r1 - r2
            int r2 = r6.mSizePerSpan
            int r1 = r1 * r2
            int r0 = r0 - r1
        L_0x014f:
            android.support.v7.widget.OrientationHelper r1 = r6.mSecondaryOrientation
            int r1 = r1.getDecoratedMeasurement(r13)
            int r1 = r0 - r1
            r9 = r0
            r3 = r1
            goto L_0x017b
        L_0x015a:
            boolean r0 = r14.mFullSpan
            if (r0 == 0) goto L_0x0165
            android.support.v7.widget.OrientationHelper r0 = r6.mSecondaryOrientation
            int r0 = r0.getStartAfterPadding()
            goto L_0x0172
        L_0x0165:
            int r0 = r15.mIndex
            int r1 = r6.mSizePerSpan
            int r0 = r0 * r1
            android.support.v7.widget.OrientationHelper r1 = r6.mSecondaryOrientation
            int r1 = r1.getStartAfterPadding()
            int r0 = r0 + r1
        L_0x0172:
            android.support.v7.widget.OrientationHelper r1 = r6.mSecondaryOrientation
            int r1 = r1.getDecoratedMeasurement(r13)
            int r1 = r1 + r0
            r3 = r0
            r9 = r1
        L_0x017b:
            int r0 = r6.mOrientation
            if (r0 != r10) goto L_0x0189
            r0 = r16
            r1 = r13
            r2 = r3
            r3 = r4
            r4 = r9
            r0.layoutDecoratedWithMargins(r1, r2, r3, r4, r5)
            goto L_0x0192
        L_0x0189:
            r0 = r16
            r1 = r13
            r2 = r4
            r4 = r5
            r5 = r9
            r0.layoutDecoratedWithMargins(r1, r2, r3, r4, r5)
        L_0x0192:
            boolean r0 = r14.mFullSpan
            if (r0 == 0) goto L_0x019e
            android.support.v7.widget.LayoutState r0 = r6.mLayoutState
            int r0 = r0.mLayoutDirection
            r6.updateAllRemainingSpans(r0, r11)
            goto L_0x01a5
        L_0x019e:
            android.support.v7.widget.LayoutState r0 = r6.mLayoutState
            int r0 = r0.mLayoutDirection
            r6.updateRemainingSpans(r15, r0, r11)
        L_0x01a5:
            android.support.v7.widget.LayoutState r0 = r6.mLayoutState
            r6.recycle(r7, r0)
            android.support.v7.widget.LayoutState r0 = r6.mLayoutState
            boolean r0 = r0.mStopInFocusable
            if (r0 == 0) goto L_0x01c9
            boolean r0 = r13.isFocusable()
            if (r0 == 0) goto L_0x01c9
            boolean r0 = r14.mFullSpan
            if (r0 == 0) goto L_0x01c0
            java.util.BitSet r0 = r6.mRemainingSpans
            r0.clear()
            goto L_0x01c9
        L_0x01c0:
            java.util.BitSet r0 = r6.mRemainingSpans
            int r1 = r15.mIndex
            r3 = 0
            r0.set(r1, r3)
            goto L_0x01ca
        L_0x01c9:
            r3 = 0
        L_0x01ca:
            r0 = 1
            r9 = 0
            goto L_0x004d
        L_0x01ce:
            r3 = 0
            if (r0 != 0) goto L_0x01d6
            android.support.v7.widget.LayoutState r0 = r6.mLayoutState
            r6.recycle(r7, r0)
        L_0x01d6:
            android.support.v7.widget.LayoutState r0 = r6.mLayoutState
            int r0 = r0.mLayoutDirection
            if (r0 != r2) goto L_0x01ee
            android.support.v7.widget.OrientationHelper r0 = r6.mPrimaryOrientation
            int r0 = r0.getStartAfterPadding()
            int r0 = r6.getMinStart(r0)
            android.support.v7.widget.OrientationHelper r1 = r6.mPrimaryOrientation
            int r1 = r1.getStartAfterPadding()
            int r1 = r1 - r0
            goto L_0x0200
        L_0x01ee:
            android.support.v7.widget.OrientationHelper r0 = r6.mPrimaryOrientation
            int r0 = r0.getEndAfterPadding()
            int r0 = r6.getMaxEnd(r0)
            android.support.v7.widget.OrientationHelper r1 = r6.mPrimaryOrientation
            int r1 = r1.getEndAfterPadding()
            int r1 = r0 - r1
        L_0x0200:
            if (r1 <= 0) goto L_0x0209
            int r0 = r8.mAvailable
            int r9 = java.lang.Math.min(r0, r1)
            r3 = r9
        L_0x0209:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.p003v7.widget.StaggeredGridLayoutManager.fill(android.support.v7.widget.RecyclerView$Recycler, android.support.v7.widget.LayoutState, android.support.v7.widget.RecyclerView$State):int");
    }

    private FullSpanItem createFullSpanItemFromEnd(int i) {
        FullSpanItem fullSpanItem = new FullSpanItem();
        fullSpanItem.mGapPerSpan = new int[this.mSpanCount];
        for (int i2 = 0; i2 < this.mSpanCount; i2++) {
            fullSpanItem.mGapPerSpan[i2] = i - this.mSpans[i2].getEndLine(i);
        }
        return fullSpanItem;
    }

    private FullSpanItem createFullSpanItemFromStart(int i) {
        FullSpanItem fullSpanItem = new FullSpanItem();
        fullSpanItem.mGapPerSpan = new int[this.mSpanCount];
        for (int i2 = 0; i2 < this.mSpanCount; i2++) {
            fullSpanItem.mGapPerSpan[i2] = this.mSpans[i2].getStartLine(i) - i;
        }
        return fullSpanItem;
    }

    private void attachViewToSpans(View view, LayoutParams layoutParams, LayoutState layoutState) {
        if (layoutState.mLayoutDirection == 1) {
            if (layoutParams.mFullSpan) {
                appendViewToAllSpans(view);
            } else {
                layoutParams.mSpan.appendToSpan(view);
            }
        } else if (layoutParams.mFullSpan) {
            prependViewToAllSpans(view);
        } else {
            layoutParams.mSpan.prependToSpan(view);
        }
    }

    private void recycle(Recycler recycler, LayoutState layoutState) {
        int i;
        int i2;
        if (layoutState.mRecycle && !layoutState.mInfinite) {
            if (layoutState.mAvailable == 0) {
                if (layoutState.mLayoutDirection == -1) {
                    recycleFromEnd(recycler, layoutState.mEndLine);
                } else {
                    recycleFromStart(recycler, layoutState.mStartLine);
                }
            } else if (layoutState.mLayoutDirection == -1) {
                int maxStart = layoutState.mStartLine - getMaxStart(layoutState.mStartLine);
                if (maxStart < 0) {
                    i2 = layoutState.mEndLine;
                } else {
                    i2 = layoutState.mEndLine - Math.min(maxStart, layoutState.mAvailable);
                }
                recycleFromEnd(recycler, i2);
            } else {
                int minEnd = getMinEnd(layoutState.mEndLine) - layoutState.mEndLine;
                if (minEnd < 0) {
                    i = layoutState.mStartLine;
                } else {
                    i = Math.min(minEnd, layoutState.mAvailable) + layoutState.mStartLine;
                }
                recycleFromStart(recycler, i);
            }
        }
    }

    private void appendViewToAllSpans(View view) {
        for (int i = this.mSpanCount - 1; i >= 0; i--) {
            this.mSpans[i].appendToSpan(view);
        }
    }

    private void prependViewToAllSpans(View view) {
        for (int i = this.mSpanCount - 1; i >= 0; i--) {
            this.mSpans[i].prependToSpan(view);
        }
    }

    private void layoutDecoratedWithMargins(View view, int i, int i2, int i3, int i4) {
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        layoutDecorated(view, i + layoutParams.leftMargin, i2 + layoutParams.topMargin, i3 - layoutParams.rightMargin, i4 - layoutParams.bottomMargin);
    }

    private void updateAllRemainingSpans(int i, int i2) {
        for (int i3 = 0; i3 < this.mSpanCount; i3++) {
            if (!this.mSpans[i3].mViews.isEmpty()) {
                updateRemainingSpans(this.mSpans[i3], i, i2);
            }
        }
    }

    private void updateRemainingSpans(Span span, int i, int i2) {
        int deletedSize = span.getDeletedSize();
        if (i == -1) {
            if (span.getStartLine() + deletedSize <= i2) {
                this.mRemainingSpans.set(span.mIndex, false);
            }
        } else if (span.getEndLine() - deletedSize >= i2) {
            this.mRemainingSpans.set(span.mIndex, false);
        }
    }

    private int getMaxStart(int i) {
        int startLine = this.mSpans[0].getStartLine(i);
        for (int i2 = 1; i2 < this.mSpanCount; i2++) {
            int startLine2 = this.mSpans[i2].getStartLine(i);
            if (startLine2 > startLine) {
                startLine = startLine2;
            }
        }
        return startLine;
    }

    private int getMinStart(int i) {
        int startLine = this.mSpans[0].getStartLine(i);
        for (int i2 = 1; i2 < this.mSpanCount; i2++) {
            int startLine2 = this.mSpans[i2].getStartLine(i);
            if (startLine2 < startLine) {
                startLine = startLine2;
            }
        }
        return startLine;
    }

    /* access modifiers changed from: 0000 */
    public boolean areAllEndsEqual() {
        int endLine = this.mSpans[0].getEndLine(Integer.MIN_VALUE);
        for (int i = 1; i < this.mSpanCount; i++) {
            if (this.mSpans[i].getEndLine(Integer.MIN_VALUE) != endLine) {
                return false;
            }
        }
        return true;
    }

    /* access modifiers changed from: 0000 */
    public boolean areAllStartsEqual() {
        int startLine = this.mSpans[0].getStartLine(Integer.MIN_VALUE);
        for (int i = 1; i < this.mSpanCount; i++) {
            if (this.mSpans[i].getStartLine(Integer.MIN_VALUE) != startLine) {
                return false;
            }
        }
        return true;
    }

    private int getMaxEnd(int i) {
        int endLine = this.mSpans[0].getEndLine(i);
        for (int i2 = 1; i2 < this.mSpanCount; i2++) {
            int endLine2 = this.mSpans[i2].getEndLine(i);
            if (endLine2 > endLine) {
                endLine = endLine2;
            }
        }
        return endLine;
    }

    private int getMinEnd(int i) {
        int endLine = this.mSpans[0].getEndLine(i);
        for (int i2 = 1; i2 < this.mSpanCount; i2++) {
            int endLine2 = this.mSpans[i2].getEndLine(i);
            if (endLine2 < endLine) {
                endLine = endLine2;
            }
        }
        return endLine;
    }

    private void recycleFromStart(Recycler recycler, int i) {
        while (getChildCount() > 0) {
            View childAt = getChildAt(0);
            if (this.mPrimaryOrientation.getDecoratedEnd(childAt) > i) {
                break;
            }
            LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
            if (layoutParams.mFullSpan) {
                int i2 = 0;
                while (i2 < this.mSpanCount) {
                    if (this.mSpans[i2].mViews.size() != 1) {
                        i2++;
                    } else {
                        return;
                    }
                }
                for (int i3 = 0; i3 < this.mSpanCount; i3++) {
                    this.mSpans[i3].popStart();
                }
            } else if (layoutParams.mSpan.mViews.size() != 1) {
                layoutParams.mSpan.popStart();
            } else {
                return;
            }
            removeAndRecycleView(childAt, recycler);
        }
    }

    private void recycleFromEnd(Recycler recycler, int i) {
        for (int childCount = getChildCount() - 1; childCount >= 0; childCount--) {
            View childAt = getChildAt(childCount);
            if (this.mPrimaryOrientation.getDecoratedStart(childAt) < i) {
                break;
            }
            LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
            if (layoutParams.mFullSpan) {
                int i2 = 0;
                while (i2 < this.mSpanCount) {
                    if (this.mSpans[i2].mViews.size() != 1) {
                        i2++;
                    } else {
                        return;
                    }
                }
                for (int i3 = 0; i3 < this.mSpanCount; i3++) {
                    this.mSpans[i3].popEnd();
                }
            } else if (layoutParams.mSpan.mViews.size() != 1) {
                layoutParams.mSpan.popEnd();
            } else {
                return;
            }
            removeAndRecycleView(childAt, recycler);
        }
    }

    private boolean preferLastSpan(int i) {
        boolean z = true;
        if (this.mOrientation == 0) {
            if ((i == -1) == this.mShouldReverseLayout) {
                z = false;
            }
            return z;
        }
        if (((i == -1) == this.mShouldReverseLayout) != isLayoutRTL()) {
            z = false;
        }
        return z;
    }

    private Span getNextSpan(LayoutState layoutState) {
        int i;
        int i2;
        int i3 = -1;
        if (preferLastSpan(layoutState.mLayoutDirection)) {
            i2 = this.mSpanCount - 1;
            i = -1;
        } else {
            i2 = 0;
            i3 = this.mSpanCount;
            i = 1;
        }
        Span span = null;
        if (layoutState.mLayoutDirection == 1) {
            int i4 = ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
            int startAfterPadding = this.mPrimaryOrientation.getStartAfterPadding();
            while (i2 != i3) {
                Span span2 = this.mSpans[i2];
                int endLine = span2.getEndLine(startAfterPadding);
                if (endLine < i4) {
                    span = span2;
                    i4 = endLine;
                }
                i2 += i;
            }
            return span;
        }
        int i5 = Integer.MIN_VALUE;
        int endAfterPadding = this.mPrimaryOrientation.getEndAfterPadding();
        while (i2 != i3) {
            Span span3 = this.mSpans[i2];
            int startLine = span3.getStartLine(endAfterPadding);
            if (startLine > i5) {
                span = span3;
                i5 = startLine;
            }
            i2 += i;
        }
        return span;
    }

    public boolean canScrollVertically() {
        return this.mOrientation == 1;
    }

    public boolean canScrollHorizontally() {
        return this.mOrientation == 0;
    }

    public int scrollHorizontallyBy(int i, Recycler recycler, State state) {
        return scrollBy(i, recycler, state);
    }

    public int scrollVerticallyBy(int i, Recycler recycler, State state) {
        return scrollBy(i, recycler, state);
    }

    /* access modifiers changed from: private */
    public int calculateScrollDirectionForPosition(int i) {
        int i2 = -1;
        if (getChildCount() == 0) {
            if (this.mShouldReverseLayout) {
                i2 = 1;
            }
            return i2;
        }
        if ((i < getFirstChildPosition()) == this.mShouldReverseLayout) {
            i2 = 1;
        }
        return i2;
    }

    public void smoothScrollToPosition(RecyclerView recyclerView, State state, int i) {
        C03312 r2 = new LinearSmoothScroller(recyclerView.getContext()) {
            public PointF computeScrollVectorForPosition(int i) {
                int access$400 = StaggeredGridLayoutManager.this.calculateScrollDirectionForPosition(i);
                if (access$400 == 0) {
                    return null;
                }
                if (StaggeredGridLayoutManager.this.mOrientation == 0) {
                    return new PointF((float) access$400, 0.0f);
                }
                return new PointF(0.0f, (float) access$400);
            }
        };
        r2.setTargetPosition(i);
        startSmoothScroll(r2);
    }

    public void scrollToPosition(int i) {
        SavedState savedState = this.mPendingSavedState;
        if (!(savedState == null || savedState.mAnchorPosition == i)) {
            this.mPendingSavedState.invalidateAnchorPositionInfo();
        }
        this.mPendingScrollPosition = i;
        this.mPendingScrollPositionOffset = Integer.MIN_VALUE;
        requestLayout();
    }

    public void scrollToPositionWithOffset(int i, int i2) {
        SavedState savedState = this.mPendingSavedState;
        if (savedState != null) {
            savedState.invalidateAnchorPositionInfo();
        }
        this.mPendingScrollPosition = i;
        this.mPendingScrollPositionOffset = i2;
        requestLayout();
    }

    /* access modifiers changed from: 0000 */
    public int scrollBy(int i, Recycler recycler, State state) {
        int i2;
        int i3;
        if (i > 0) {
            i3 = getLastChildPosition();
            i2 = 1;
        } else {
            i3 = getFirstChildPosition();
            i2 = -1;
        }
        this.mLayoutState.mRecycle = true;
        updateLayoutState(i3, state);
        setLayoutStateDirection(i2);
        LayoutState layoutState = this.mLayoutState;
        layoutState.mCurrentPosition = i3 + layoutState.mItemDirection;
        int abs = Math.abs(i);
        LayoutState layoutState2 = this.mLayoutState;
        layoutState2.mAvailable = abs;
        int fill = fill(recycler, layoutState2, state);
        if (abs >= fill) {
            i = i < 0 ? -fill : fill;
        }
        this.mPrimaryOrientation.offsetChildren(-i);
        this.mLastLayoutFromEnd = this.mShouldReverseLayout;
        return i;
    }

    private int getLastChildPosition() {
        int childCount = getChildCount();
        if (childCount == 0) {
            return 0;
        }
        return getPosition(getChildAt(childCount - 1));
    }

    private int getFirstChildPosition() {
        if (getChildCount() == 0) {
            return 0;
        }
        return getPosition(getChildAt(0));
    }

    private int findFirstReferenceChildPosition(int i) {
        int childCount = getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            int position = getPosition(getChildAt(i2));
            if (position >= 0 && position < i) {
                return position;
            }
        }
        return 0;
    }

    private int findLastReferenceChildPosition(int i) {
        for (int childCount = getChildCount() - 1; childCount >= 0; childCount--) {
            int position = getPosition(getChildAt(childCount));
            if (position >= 0 && position < i) {
                return position;
            }
        }
        return 0;
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

    public int getOrientation() {
        return this.mOrientation;
    }

    @Nullable
    public View onFocusSearchFailed(View view, int i, Recycler recycler, State state) {
        int i2;
        if (getChildCount() == 0) {
            return null;
        }
        View findContainingItemView = findContainingItemView(view);
        if (findContainingItemView == null) {
            return null;
        }
        resolveShouldLayoutReverse();
        int convertFocusDirectionToLayoutDirection = convertFocusDirectionToLayoutDirection(i);
        if (convertFocusDirectionToLayoutDirection == Integer.MIN_VALUE) {
            return null;
        }
        LayoutParams layoutParams = (LayoutParams) findContainingItemView.getLayoutParams();
        boolean z = layoutParams.mFullSpan;
        Span span = layoutParams.mSpan;
        if (convertFocusDirectionToLayoutDirection == 1) {
            i2 = getLastChildPosition();
        } else {
            i2 = getFirstChildPosition();
        }
        updateLayoutState(i2, state);
        setLayoutStateDirection(convertFocusDirectionToLayoutDirection);
        LayoutState layoutState = this.mLayoutState;
        layoutState.mCurrentPosition = layoutState.mItemDirection + i2;
        this.mLayoutState.mAvailable = (int) (((float) this.mPrimaryOrientation.getTotalSpace()) * MAX_SCROLL_FACTOR);
        LayoutState layoutState2 = this.mLayoutState;
        layoutState2.mStopInFocusable = true;
        layoutState2.mRecycle = false;
        fill(recycler, layoutState2, state);
        this.mLastLayoutFromEnd = this.mShouldReverseLayout;
        if (!z) {
            View focusableViewAfter = span.getFocusableViewAfter(i2, convertFocusDirectionToLayoutDirection);
            if (!(focusableViewAfter == null || focusableViewAfter == findContainingItemView)) {
                return focusableViewAfter;
            }
        }
        if (preferLastSpan(convertFocusDirectionToLayoutDirection)) {
            for (int i3 = this.mSpanCount - 1; i3 >= 0; i3--) {
                View focusableViewAfter2 = this.mSpans[i3].getFocusableViewAfter(i2, convertFocusDirectionToLayoutDirection);
                if (focusableViewAfter2 != null && focusableViewAfter2 != findContainingItemView) {
                    return focusableViewAfter2;
                }
            }
        } else {
            for (int i4 = 0; i4 < this.mSpanCount; i4++) {
                View focusableViewAfter3 = this.mSpans[i4].getFocusableViewAfter(i2, convertFocusDirectionToLayoutDirection);
                if (focusableViewAfter3 != null && focusableViewAfter3 != findContainingItemView) {
                    return focusableViewAfter3;
                }
            }
        }
        return null;
    }

    private int convertFocusDirectionToLayoutDirection(int i) {
        int i2 = -1;
        int i3 = 1;
        if (i == 1) {
            return -1;
        }
        if (i == 2) {
            return 1;
        }
        if (i == 17) {
            if (this.mOrientation != 0) {
                i2 = Integer.MIN_VALUE;
            }
            return i2;
        } else if (i == 33) {
            if (this.mOrientation != 1) {
                i2 = Integer.MIN_VALUE;
            }
            return i2;
        } else if (i == 66) {
            if (this.mOrientation != 0) {
                i3 = Integer.MIN_VALUE;
            }
            return i3;
        } else if (i != 130) {
            return Integer.MIN_VALUE;
        } else {
            if (this.mOrientation != 1) {
                i3 = Integer.MIN_VALUE;
            }
            return i3;
        }
    }
}
