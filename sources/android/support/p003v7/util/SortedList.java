package android.support.p003v7.util;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

/* renamed from: android.support.v7.util.SortedList */
public class SortedList<T> {
    private static final int CAPACITY_GROWTH = 10;
    private static final int DELETION = 2;
    private static final int INSERTION = 1;
    public static final int INVALID_POSITION = -1;
    private static final int LOOKUP = 4;
    private static final int MIN_CAPACITY = 10;
    private BatchedCallback mBatchedCallback;
    private Callback mCallback;
    T[] mData;
    private int mMergedSize;
    private T[] mOldData;
    private int mOldDataSize;
    private int mOldDataStart;
    private int mSize;
    private final Class<T> mTClass;

    /* renamed from: android.support.v7.util.SortedList$BatchedCallback */
    public static class BatchedCallback<T2> extends Callback<T2> {
        static final int TYPE_ADD = 1;
        static final int TYPE_CHANGE = 3;
        static final int TYPE_MOVE = 4;
        static final int TYPE_NONE = 0;
        static final int TYPE_REMOVE = 2;
        int mLastEventCount = -1;
        int mLastEventPosition = -1;
        int mLastEventType = 0;
        /* access modifiers changed from: private */
        public final Callback<T2> mWrappedCallback;

        public BatchedCallback(Callback<T2> callback) {
            this.mWrappedCallback = callback;
        }

        public int compare(T2 t2, T2 t22) {
            return this.mWrappedCallback.compare(t2, t22);
        }

        public void onInserted(int i, int i2) {
            if (this.mLastEventType == 1) {
                int i3 = this.mLastEventPosition;
                if (i >= i3) {
                    int i4 = this.mLastEventCount;
                    if (i <= i3 + i4) {
                        this.mLastEventCount = i4 + i2;
                        this.mLastEventPosition = Math.min(i, i3);
                        return;
                    }
                }
            }
            dispatchLastEvent();
            this.mLastEventPosition = i;
            this.mLastEventCount = i2;
            this.mLastEventType = 1;
        }

        public void onRemoved(int i, int i2) {
            if (this.mLastEventType == 2 && this.mLastEventPosition == i) {
                this.mLastEventCount += i2;
                return;
            }
            dispatchLastEvent();
            this.mLastEventPosition = i;
            this.mLastEventCount = i2;
            this.mLastEventType = 2;
        }

        public void onMoved(int i, int i2) {
            dispatchLastEvent();
            this.mWrappedCallback.onMoved(i, i2);
        }

        public void onChanged(int i, int i2) {
            if (this.mLastEventType == 3) {
                int i3 = this.mLastEventPosition;
                int i4 = this.mLastEventCount;
                if (i <= i3 + i4) {
                    int i5 = i + i2;
                    if (i5 >= i3) {
                        int i6 = i4 + i3;
                        this.mLastEventPosition = Math.min(i, i3);
                        this.mLastEventCount = Math.max(i6, i5) - this.mLastEventPosition;
                        return;
                    }
                }
            }
            dispatchLastEvent();
            this.mLastEventPosition = i;
            this.mLastEventCount = i2;
            this.mLastEventType = 3;
        }

        public boolean areContentsTheSame(T2 t2, T2 t22) {
            return this.mWrappedCallback.areContentsTheSame(t2, t22);
        }

        public boolean areItemsTheSame(T2 t2, T2 t22) {
            return this.mWrappedCallback.areItemsTheSame(t2, t22);
        }

        public void dispatchLastEvent() {
            int i = this.mLastEventType;
            if (i != 0) {
                if (i == 1) {
                    this.mWrappedCallback.onInserted(this.mLastEventPosition, this.mLastEventCount);
                } else if (i == 2) {
                    this.mWrappedCallback.onRemoved(this.mLastEventPosition, this.mLastEventCount);
                } else if (i == 3) {
                    this.mWrappedCallback.onChanged(this.mLastEventPosition, this.mLastEventCount);
                }
                this.mLastEventType = 0;
            }
        }
    }

    /* renamed from: android.support.v7.util.SortedList$Callback */
    public static abstract class Callback<T2> implements Comparator<T2> {
        public abstract boolean areContentsTheSame(T2 t2, T2 t22);

        public abstract boolean areItemsTheSame(T2 t2, T2 t22);

        public abstract int compare(T2 t2, T2 t22);

        public abstract void onChanged(int i, int i2);

        public abstract void onInserted(int i, int i2);

        public abstract void onMoved(int i, int i2);

        public abstract void onRemoved(int i, int i2);
    }

    public SortedList(Class<T> cls, Callback<T> callback) {
        this(cls, callback, 10);
    }

    public SortedList(Class<T> cls, Callback<T> callback, int i) {
        this.mTClass = cls;
        this.mData = (Object[]) Array.newInstance(cls, i);
        this.mCallback = callback;
        this.mSize = 0;
    }

    public int size() {
        return this.mSize;
    }

    public int add(T t) {
        throwIfMerging();
        return add(t, true);
    }

    public void addAll(T[] tArr, boolean z) {
        throwIfMerging();
        if (tArr.length != 0) {
            if (z) {
                addAllInternal(tArr);
            } else {
                Object[] objArr = (Object[]) Array.newInstance(this.mTClass, tArr.length);
                System.arraycopy(tArr, 0, objArr, 0, tArr.length);
                addAllInternal(objArr);
            }
        }
    }

    public void addAll(T... tArr) {
        addAll(tArr, false);
    }

    public void addAll(Collection<T> collection) {
        addAll(collection.toArray((Object[]) Array.newInstance(this.mTClass, collection.size())), true);
    }

    private void addAllInternal(T[] tArr) {
        boolean z = !(this.mCallback instanceof BatchedCallback);
        if (z) {
            beginBatchedUpdates();
        }
        this.mOldData = this.mData;
        this.mOldDataStart = 0;
        this.mOldDataSize = this.mSize;
        Arrays.sort(tArr, this.mCallback);
        int deduplicate = deduplicate(tArr);
        if (this.mSize == 0) {
            this.mData = tArr;
            this.mSize = deduplicate;
            this.mMergedSize = deduplicate;
            this.mCallback.onInserted(0, deduplicate);
        } else {
            merge(tArr, deduplicate);
        }
        this.mOldData = null;
        if (z) {
            endBatchedUpdates();
        }
    }

    private int deduplicate(T[] tArr) {
        if (tArr.length != 0) {
            int i = 1;
            int i2 = 1;
            int i3 = 0;
            while (i < tArr.length) {
                T t = tArr[i];
                int compare = this.mCallback.compare(tArr[i3], t);
                if (compare <= 0) {
                    if (compare == 0) {
                        int findSameItem = findSameItem(t, tArr, i3, i2);
                        if (findSameItem != -1) {
                            tArr[findSameItem] = t;
                        } else {
                            if (i2 != i) {
                                tArr[i2] = t;
                            }
                            i2++;
                        }
                    } else {
                        if (i2 != i) {
                            tArr[i2] = t;
                        }
                        i3 = i2;
                        i2++;
                    }
                    i++;
                } else {
                    throw new IllegalArgumentException("Input must be sorted in ascending order.");
                }
            }
            return i2;
        }
        throw new IllegalArgumentException("Input array must be non-empty");
    }

    private int findSameItem(T t, T[] tArr, int i, int i2) {
        while (i < i2) {
            if (this.mCallback.areItemsTheSame(tArr[i], t)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    private void merge(T[] tArr, int i) {
        this.mData = (Object[]) Array.newInstance(this.mTClass, this.mSize + i + 10);
        int i2 = 0;
        this.mMergedSize = 0;
        while (true) {
            if (this.mOldDataStart < this.mOldDataSize || i2 < i) {
                int i3 = this.mOldDataStart;
                int i4 = this.mOldDataSize;
                if (i3 == i4) {
                    int i5 = i - i2;
                    System.arraycopy(tArr, i2, this.mData, this.mMergedSize, i5);
                    this.mMergedSize += i5;
                    this.mSize += i5;
                    this.mCallback.onInserted(this.mMergedSize - i5, i5);
                    return;
                } else if (i2 == i) {
                    int i6 = i4 - i3;
                    System.arraycopy(this.mOldData, i3, this.mData, this.mMergedSize, i6);
                    this.mMergedSize += i6;
                    return;
                } else {
                    T t = this.mOldData[i3];
                    T t2 = tArr[i2];
                    int compare = this.mCallback.compare(t, t2);
                    if (compare > 0) {
                        T[] tArr2 = this.mData;
                        int i7 = this.mMergedSize;
                        this.mMergedSize = i7 + 1;
                        tArr2[i7] = t2;
                        this.mSize++;
                        i2++;
                        this.mCallback.onInserted(this.mMergedSize - 1, 1);
                    } else if (compare != 0 || !this.mCallback.areItemsTheSame(t, t2)) {
                        T[] tArr3 = this.mData;
                        int i8 = this.mMergedSize;
                        this.mMergedSize = i8 + 1;
                        tArr3[i8] = t;
                        this.mOldDataStart++;
                    } else {
                        T[] tArr4 = this.mData;
                        int i9 = this.mMergedSize;
                        this.mMergedSize = i9 + 1;
                        tArr4[i9] = t2;
                        i2++;
                        this.mOldDataStart++;
                        if (!this.mCallback.areContentsTheSame(t, t2)) {
                            this.mCallback.onChanged(this.mMergedSize - 1, 1);
                        }
                    }
                }
            } else {
                return;
            }
        }
    }

    private void throwIfMerging() {
        if (this.mOldData != null) {
            throw new IllegalStateException("Cannot call this method from within addAll");
        }
    }

    public void beginBatchedUpdates() {
        throwIfMerging();
        Callback callback = this.mCallback;
        if (!(callback instanceof BatchedCallback)) {
            if (this.mBatchedCallback == null) {
                this.mBatchedCallback = new BatchedCallback(callback);
            }
            this.mCallback = this.mBatchedCallback;
        }
    }

    public void endBatchedUpdates() {
        throwIfMerging();
        Callback callback = this.mCallback;
        if (callback instanceof BatchedCallback) {
            ((BatchedCallback) callback).dispatchLastEvent();
        }
        Callback callback2 = this.mCallback;
        BatchedCallback batchedCallback = this.mBatchedCallback;
        if (callback2 == batchedCallback) {
            this.mCallback = batchedCallback.mWrappedCallback;
        }
    }

    private int add(T t, boolean z) {
        int findIndexOf = findIndexOf(t, this.mData, 0, this.mSize, 1);
        if (findIndexOf == -1) {
            findIndexOf = 0;
        } else if (findIndexOf < this.mSize) {
            T t2 = this.mData[findIndexOf];
            if (this.mCallback.areItemsTheSame(t2, t)) {
                if (this.mCallback.areContentsTheSame(t2, t)) {
                    this.mData[findIndexOf] = t;
                    return findIndexOf;
                }
                this.mData[findIndexOf] = t;
                this.mCallback.onChanged(findIndexOf, 1);
                return findIndexOf;
            }
        }
        addToData(findIndexOf, t);
        if (z) {
            this.mCallback.onInserted(findIndexOf, 1);
        }
        return findIndexOf;
    }

    public boolean remove(T t) {
        throwIfMerging();
        return remove(t, true);
    }

    public T removeItemAt(int i) {
        throwIfMerging();
        T t = get(i);
        removeItemAtIndex(i, true);
        return t;
    }

    private boolean remove(T t, boolean z) {
        int findIndexOf = findIndexOf(t, this.mData, 0, this.mSize, 2);
        if (findIndexOf == -1) {
            return false;
        }
        removeItemAtIndex(findIndexOf, z);
        return true;
    }

    private void removeItemAtIndex(int i, boolean z) {
        T[] tArr = this.mData;
        System.arraycopy(tArr, i + 1, tArr, i, (this.mSize - i) - 1);
        this.mSize--;
        this.mData[this.mSize] = null;
        if (z) {
            this.mCallback.onRemoved(i, 1);
        }
    }

    public void updateItemAt(int i, T t) {
        throwIfMerging();
        T t2 = get(i);
        boolean z = t2 == t || !this.mCallback.areContentsTheSame(t2, t);
        if (t2 == t || this.mCallback.compare(t2, t) != 0) {
            if (z) {
                this.mCallback.onChanged(i, 1);
            }
            removeItemAtIndex(i, false);
            int add = add(t, false);
            if (i != add) {
                this.mCallback.onMoved(i, add);
            }
            return;
        }
        this.mData[i] = t;
        if (z) {
            this.mCallback.onChanged(i, 1);
        }
    }

    public void recalculatePositionOfItemAt(int i) {
        throwIfMerging();
        Object obj = get(i);
        removeItemAtIndex(i, false);
        int add = add(obj, false);
        if (i != add) {
            this.mCallback.onMoved(i, add);
        }
    }

    public T get(int i) throws IndexOutOfBoundsException {
        if (i >= this.mSize || i < 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("Asked to get item at ");
            sb.append(i);
            sb.append(" but size is ");
            sb.append(this.mSize);
            throw new IndexOutOfBoundsException(sb.toString());
        }
        T[] tArr = this.mOldData;
        if (tArr != null) {
            int i2 = this.mMergedSize;
            if (i >= i2) {
                return tArr[(i - i2) + this.mOldDataStart];
            }
        }
        return this.mData[i];
    }

    public int indexOf(T t) {
        if (this.mOldData != null) {
            int findIndexOf = findIndexOf(t, this.mData, 0, this.mMergedSize, 4);
            if (findIndexOf != -1) {
                return findIndexOf;
            }
            int findIndexOf2 = findIndexOf(t, this.mOldData, this.mOldDataStart, this.mOldDataSize, 4);
            if (findIndexOf2 != -1) {
                return (findIndexOf2 - this.mOldDataStart) + this.mMergedSize;
            }
            return -1;
        }
        return findIndexOf(t, this.mData, 0, this.mSize, 4);
    }

    private int findIndexOf(T t, T[] tArr, int i, int i2, int i3) {
        while (i < i2) {
            int i4 = (i + i2) / 2;
            T t2 = tArr[i4];
            int compare = this.mCallback.compare(t2, t);
            if (compare < 0) {
                i = i4 + 1;
            } else if (compare != 0) {
                i2 = i4;
            } else if (this.mCallback.areItemsTheSame(t2, t)) {
                return i4;
            } else {
                int linearEqualitySearch = linearEqualitySearch(t, i4, i, i2);
                if (i3 == 1 && linearEqualitySearch == -1) {
                    linearEqualitySearch = i4;
                }
                return linearEqualitySearch;
            }
        }
        if (i3 != 1) {
            i = -1;
        }
        return i;
    }

    private int linearEqualitySearch(T t, int i, int i2, int i3) {
        T t2;
        int i4 = i - 1;
        while (i4 >= i2) {
            T t3 = this.mData[i4];
            if (this.mCallback.compare(t3, t) != 0) {
                break;
            } else if (this.mCallback.areItemsTheSame(t3, t)) {
                return i4;
            } else {
                i4--;
            }
        }
        do {
            i++;
            if (i < i3) {
                t2 = this.mData[i];
                if (this.mCallback.compare(t2, t) != 0) {
                }
            }
            return -1;
        } while (!this.mCallback.areItemsTheSame(t2, t));
        return i;
    }

    private void addToData(int i, T t) {
        int i2 = this.mSize;
        if (i <= i2) {
            T[] tArr = this.mData;
            if (i2 == tArr.length) {
                T[] tArr2 = (Object[]) Array.newInstance(this.mTClass, tArr.length + 10);
                System.arraycopy(this.mData, 0, tArr2, 0, i);
                tArr2[i] = t;
                System.arraycopy(this.mData, i, tArr2, i + 1, this.mSize - i);
                this.mData = tArr2;
            } else {
                System.arraycopy(tArr, i, tArr, i + 1, i2 - i);
                this.mData[i] = t;
            }
            this.mSize++;
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("cannot add item to ");
        sb.append(i);
        sb.append(" because size is ");
        sb.append(this.mSize);
        throw new IndexOutOfBoundsException(sb.toString());
    }

    public void clear() {
        throwIfMerging();
        int i = this.mSize;
        if (i != 0) {
            Arrays.fill(this.mData, 0, i, null);
            this.mSize = 0;
            this.mCallback.onRemoved(0, i);
        }
    }
}
