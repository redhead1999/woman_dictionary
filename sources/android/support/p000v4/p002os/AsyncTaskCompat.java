package android.support.p000v4.p002os;

import android.os.AsyncTask;
import android.os.Build.VERSION;

/* renamed from: android.support.v4.os.AsyncTaskCompat */
public final class AsyncTaskCompat {
    public static <Params, Progress, Result> AsyncTask<Params, Progress, Result> executeParallel(AsyncTask<Params, Progress, Result> asyncTask, Params... paramsArr) {
        if (asyncTask != null) {
            if (VERSION.SDK_INT >= 11) {
                AsyncTaskCompatHoneycomb.executeParallel(asyncTask, paramsArr);
            } else {
                asyncTask.execute(paramsArr);
            }
            return asyncTask;
        }
        throw new IllegalArgumentException("task can not be null");
    }

    private AsyncTaskCompat() {
    }
}
