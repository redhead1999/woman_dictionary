package android.support.design.widget;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import java.lang.ref.WeakReference;

class SnackbarManager {
    private static final int LONG_DURATION_MS = 2750;
    private static final int MSG_TIMEOUT = 0;
    private static final int SHORT_DURATION_MS = 1500;
    private static SnackbarManager sSnackbarManager;
    private SnackbarRecord mCurrentSnackbar;
    private final Handler mHandler = new Handler(Looper.getMainLooper(), new android.os.Handler.Callback() {
        public boolean handleMessage(Message message) {
            if (message.what != 0) {
                return false;
            }
            SnackbarManager.this.handleTimeout((SnackbarRecord) message.obj);
            return true;
        }
    });
    private final Object mLock = new Object();
    private SnackbarRecord mNextSnackbar;

    interface Callback {
        void dismiss(int i);

        void show();
    }

    private static class SnackbarRecord {
        /* access modifiers changed from: private */
        public final WeakReference<Callback> callback;
        /* access modifiers changed from: private */
        public int duration;

        SnackbarRecord(int i, Callback callback2) {
            this.callback = new WeakReference<>(callback2);
            this.duration = i;
        }

        /* access modifiers changed from: 0000 */
        public boolean isSnackbar(Callback callback2) {
            return callback2 != null && this.callback.get() == callback2;
        }
    }

    static SnackbarManager getInstance() {
        if (sSnackbarManager == null) {
            sSnackbarManager = new SnackbarManager();
        }
        return sSnackbarManager;
    }

    private SnackbarManager() {
    }

    public void show(int i, Callback callback) {
        synchronized (this.mLock) {
            if (isCurrentSnackbarLocked(callback)) {
                this.mCurrentSnackbar.duration = i;
                this.mHandler.removeCallbacksAndMessages(this.mCurrentSnackbar);
                scheduleTimeoutLocked(this.mCurrentSnackbar);
                return;
            }
            if (isNextSnackbarLocked(callback)) {
                this.mNextSnackbar.duration = i;
            } else {
                this.mNextSnackbar = new SnackbarRecord(i, callback);
            }
            if (this.mCurrentSnackbar == null || !cancelSnackbarLocked(this.mCurrentSnackbar, 4)) {
                this.mCurrentSnackbar = null;
                showNextSnackbarLocked();
            }
        }
    }

    public void dismiss(Callback callback, int i) {
        synchronized (this.mLock) {
            if (isCurrentSnackbarLocked(callback)) {
                cancelSnackbarLocked(this.mCurrentSnackbar, i);
            } else if (isNextSnackbarLocked(callback)) {
                cancelSnackbarLocked(this.mNextSnackbar, i);
            }
        }
    }

    public void onDismissed(Callback callback) {
        synchronized (this.mLock) {
            if (isCurrentSnackbarLocked(callback)) {
                this.mCurrentSnackbar = null;
                if (this.mNextSnackbar != null) {
                    showNextSnackbarLocked();
                }
            }
        }
    }

    public void onShown(Callback callback) {
        synchronized (this.mLock) {
            if (isCurrentSnackbarLocked(callback)) {
                scheduleTimeoutLocked(this.mCurrentSnackbar);
            }
        }
    }

    public void cancelTimeout(Callback callback) {
        synchronized (this.mLock) {
            if (isCurrentSnackbarLocked(callback)) {
                this.mHandler.removeCallbacksAndMessages(this.mCurrentSnackbar);
            }
        }
    }

    public void restoreTimeout(Callback callback) {
        synchronized (this.mLock) {
            if (isCurrentSnackbarLocked(callback)) {
                scheduleTimeoutLocked(this.mCurrentSnackbar);
            }
        }
    }

    public boolean isCurrent(Callback callback) {
        boolean isCurrentSnackbarLocked;
        synchronized (this.mLock) {
            isCurrentSnackbarLocked = isCurrentSnackbarLocked(callback);
        }
        return isCurrentSnackbarLocked;
    }

    public boolean isCurrentOrNext(Callback callback) {
        boolean z;
        synchronized (this.mLock) {
            if (!isCurrentSnackbarLocked(callback)) {
                if (!isNextSnackbarLocked(callback)) {
                    z = false;
                }
            }
            z = true;
        }
        return z;
    }

    private void showNextSnackbarLocked() {
        SnackbarRecord snackbarRecord = this.mNextSnackbar;
        if (snackbarRecord != null) {
            this.mCurrentSnackbar = snackbarRecord;
            this.mNextSnackbar = null;
            Callback callback = (Callback) this.mCurrentSnackbar.callback.get();
            if (callback != null) {
                callback.show();
            } else {
                this.mCurrentSnackbar = null;
            }
        }
    }

    private boolean cancelSnackbarLocked(SnackbarRecord snackbarRecord, int i) {
        Callback callback = (Callback) snackbarRecord.callback.get();
        if (callback == null) {
            return false;
        }
        this.mHandler.removeCallbacksAndMessages(snackbarRecord);
        callback.dismiss(i);
        return true;
    }

    private boolean isCurrentSnackbarLocked(Callback callback) {
        SnackbarRecord snackbarRecord = this.mCurrentSnackbar;
        return snackbarRecord != null && snackbarRecord.isSnackbar(callback);
    }

    private boolean isNextSnackbarLocked(Callback callback) {
        SnackbarRecord snackbarRecord = this.mNextSnackbar;
        return snackbarRecord != null && snackbarRecord.isSnackbar(callback);
    }

    private void scheduleTimeoutLocked(SnackbarRecord snackbarRecord) {
        if (snackbarRecord.duration != -2) {
            int i = LONG_DURATION_MS;
            if (snackbarRecord.duration > 0) {
                i = snackbarRecord.duration;
            } else if (snackbarRecord.duration == -1) {
                i = SHORT_DURATION_MS;
            }
            this.mHandler.removeCallbacksAndMessages(snackbarRecord);
            Handler handler = this.mHandler;
            handler.sendMessageDelayed(Message.obtain(handler, 0, snackbarRecord), (long) i);
        }
    }

    /* access modifiers changed from: private */
    public void handleTimeout(SnackbarRecord snackbarRecord) {
        synchronized (this.mLock) {
            if (this.mCurrentSnackbar == snackbarRecord || this.mNextSnackbar == snackbarRecord) {
                cancelSnackbarLocked(snackbarRecord, 2);
            }
        }
    }
}
