package android.support.p003v7.util;

import android.os.Handler;
import android.os.Looper;
import android.support.p000v4.content.ParallelExecutorCompat;
import android.support.p003v7.util.ThreadUtil.BackgroundCallback;
import android.support.p003v7.util.ThreadUtil.MainThreadCallback;
import android.support.p003v7.util.TileList.Tile;
import android.util.Log;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

/* renamed from: android.support.v7.util.MessageThreadUtil */
class MessageThreadUtil<T> implements ThreadUtil<T> {

    /* renamed from: android.support.v7.util.MessageThreadUtil$MessageQueue */
    static class MessageQueue {
        private SyncQueueItem mRoot;

        MessageQueue() {
        }

        /* access modifiers changed from: 0000 */
        public synchronized SyncQueueItem next() {
            if (this.mRoot == null) {
                return null;
            }
            SyncQueueItem syncQueueItem = this.mRoot;
            this.mRoot = this.mRoot.next;
            return syncQueueItem;
        }

        /* access modifiers changed from: 0000 */
        public synchronized void sendMessageAtFrontOfQueue(SyncQueueItem syncQueueItem) {
            syncQueueItem.next = this.mRoot;
            this.mRoot = syncQueueItem;
        }

        /* access modifiers changed from: 0000 */
        public synchronized void sendMessage(SyncQueueItem syncQueueItem) {
            if (this.mRoot == null) {
                this.mRoot = syncQueueItem;
                return;
            }
            SyncQueueItem syncQueueItem2 = this.mRoot;
            while (syncQueueItem2.next != null) {
                syncQueueItem2 = syncQueueItem2.next;
            }
            syncQueueItem2.next = syncQueueItem;
        }

        /* access modifiers changed from: 0000 */
        public synchronized void removeMessages(int i) {
            while (this.mRoot != null && this.mRoot.what == i) {
                SyncQueueItem syncQueueItem = this.mRoot;
                this.mRoot = this.mRoot.next;
                syncQueueItem.recycle();
            }
            if (this.mRoot != null) {
                SyncQueueItem syncQueueItem2 = this.mRoot;
                SyncQueueItem access$200 = syncQueueItem2.next;
                while (access$200 != null) {
                    SyncQueueItem access$2002 = access$200.next;
                    if (access$200.what == i) {
                        syncQueueItem2.next = access$2002;
                        access$200.recycle();
                    } else {
                        syncQueueItem2 = access$200;
                    }
                    access$200 = access$2002;
                }
            }
        }
    }

    /* renamed from: android.support.v7.util.MessageThreadUtil$SyncQueueItem */
    static class SyncQueueItem {
        private static SyncQueueItem sPool;
        private static final Object sPoolLock = new Object();
        public int arg1;
        public int arg2;
        public int arg3;
        public int arg4;
        public int arg5;
        public Object data;
        /* access modifiers changed from: private */
        public SyncQueueItem next;
        public int what;

        SyncQueueItem() {
        }

        /* access modifiers changed from: 0000 */
        public void recycle() {
            this.next = null;
            this.arg5 = 0;
            this.arg4 = 0;
            this.arg3 = 0;
            this.arg2 = 0;
            this.arg1 = 0;
            this.what = 0;
            this.data = null;
            synchronized (sPoolLock) {
                if (sPool != null) {
                    this.next = sPool;
                }
                sPool = this;
            }
        }

        static SyncQueueItem obtainMessage(int i, int i2, int i3, int i4, int i5, int i6, Object obj) {
            SyncQueueItem syncQueueItem;
            synchronized (sPoolLock) {
                if (sPool == null) {
                    syncQueueItem = new SyncQueueItem();
                } else {
                    syncQueueItem = sPool;
                    sPool = sPool.next;
                    syncQueueItem.next = null;
                }
                syncQueueItem.what = i;
                syncQueueItem.arg1 = i2;
                syncQueueItem.arg2 = i3;
                syncQueueItem.arg3 = i4;
                syncQueueItem.arg4 = i5;
                syncQueueItem.arg5 = i6;
                syncQueueItem.data = obj;
            }
            return syncQueueItem;
        }

        static SyncQueueItem obtainMessage(int i, int i2, int i3) {
            return obtainMessage(i, i2, i3, 0, 0, 0, null);
        }

        static SyncQueueItem obtainMessage(int i, int i2, Object obj) {
            return obtainMessage(i, i2, 0, 0, 0, 0, obj);
        }
    }

    MessageThreadUtil() {
    }

    public MainThreadCallback<T> getMainThreadProxy(final MainThreadCallback<T> mainThreadCallback) {
        return new MainThreadCallback<T>() {
            private static final int ADD_TILE = 2;
            private static final int REMOVE_TILE = 3;
            private static final int UPDATE_ITEM_COUNT = 1;
            private final Handler mMainThreadHandler = new Handler(Looper.getMainLooper());
            private Runnable mMainThreadRunnable = new Runnable() {
                public void run() {
                    SyncQueueItem next = C02621.this.mQueue.next();
                    while (next != null) {
                        int i = next.what;
                        if (i == 1) {
                            mainThreadCallback.updateItemCount(next.arg1, next.arg2);
                        } else if (i == 2) {
                            mainThreadCallback.addTile(next.arg1, (Tile) next.data);
                        } else if (i != 3) {
                            StringBuilder sb = new StringBuilder();
                            sb.append("Unsupported message, what=");
                            sb.append(next.what);
                            Log.e("ThreadUtil", sb.toString());
                        } else {
                            mainThreadCallback.removeTile(next.arg1, next.arg2);
                        }
                        next = C02621.this.mQueue.next();
                    }
                }
            };
            /* access modifiers changed from: private */
            public final MessageQueue mQueue = new MessageQueue();

            public void updateItemCount(int i, int i2) {
                sendMessage(SyncQueueItem.obtainMessage(1, i, i2));
            }

            public void addTile(int i, Tile<T> tile) {
                sendMessage(SyncQueueItem.obtainMessage(2, i, (Object) tile));
            }

            public void removeTile(int i, int i2) {
                sendMessage(SyncQueueItem.obtainMessage(3, i, i2));
            }

            private void sendMessage(SyncQueueItem syncQueueItem) {
                this.mQueue.sendMessage(syncQueueItem);
                this.mMainThreadHandler.post(this.mMainThreadRunnable);
            }
        };
    }

    public BackgroundCallback<T> getBackgroundProxy(final BackgroundCallback<T> backgroundCallback) {
        return new BackgroundCallback<T>() {
            private static final int LOAD_TILE = 3;
            private static final int RECYCLE_TILE = 4;
            private static final int REFRESH = 1;
            private static final int UPDATE_RANGE = 2;
            private Runnable mBackgroundRunnable = new Runnable() {
                public void run() {
                    while (true) {
                        SyncQueueItem next = C02642.this.mQueue.next();
                        if (next == null) {
                            C02642.this.mBackgroundRunning.set(false);
                            return;
                        }
                        int i = next.what;
                        if (i == 1) {
                            C02642.this.mQueue.removeMessages(1);
                            backgroundCallback.refresh(next.arg1);
                        } else if (i == 2) {
                            C02642.this.mQueue.removeMessages(2);
                            C02642.this.mQueue.removeMessages(3);
                            backgroundCallback.updateRange(next.arg1, next.arg2, next.arg3, next.arg4, next.arg5);
                        } else if (i == 3) {
                            backgroundCallback.loadTile(next.arg1, next.arg2);
                        } else if (i != 4) {
                            StringBuilder sb = new StringBuilder();
                            sb.append("Unsupported message, what=");
                            sb.append(next.what);
                            Log.e("ThreadUtil", sb.toString());
                        } else {
                            backgroundCallback.recycleTile((Tile) next.data);
                        }
                    }
                }
            };
            AtomicBoolean mBackgroundRunning = new AtomicBoolean(false);
            private final Executor mExecutor = ParallelExecutorCompat.getParallelExecutor();
            /* access modifiers changed from: private */
            public final MessageQueue mQueue = new MessageQueue();

            public void refresh(int i) {
                sendMessageAtFrontOfQueue(SyncQueueItem.obtainMessage(1, i, (Object) null));
            }

            public void updateRange(int i, int i2, int i3, int i4, int i5) {
                sendMessageAtFrontOfQueue(SyncQueueItem.obtainMessage(2, i, i2, i3, i4, i5, null));
            }

            public void loadTile(int i, int i2) {
                sendMessage(SyncQueueItem.obtainMessage(3, i, i2));
            }

            public void recycleTile(Tile<T> tile) {
                sendMessage(SyncQueueItem.obtainMessage(4, 0, (Object) tile));
            }

            private void sendMessage(SyncQueueItem syncQueueItem) {
                this.mQueue.sendMessage(syncQueueItem);
                maybeExecuteBackgroundRunnable();
            }

            private void sendMessageAtFrontOfQueue(SyncQueueItem syncQueueItem) {
                this.mQueue.sendMessageAtFrontOfQueue(syncQueueItem);
                maybeExecuteBackgroundRunnable();
            }

            private void maybeExecuteBackgroundRunnable() {
                if (this.mBackgroundRunning.compareAndSet(false, true)) {
                    this.mExecutor.execute(this.mBackgroundRunnable);
                }
            }
        };
    }
}
