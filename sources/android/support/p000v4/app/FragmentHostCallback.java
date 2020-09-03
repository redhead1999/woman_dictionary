package android.support.p000v4.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.p000v4.util.SimpleArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import java.io.FileDescriptor;
import java.io.PrintWriter;

/* renamed from: android.support.v4.app.FragmentHostCallback */
public abstract class FragmentHostCallback<E> extends FragmentContainer {
    private final Activity mActivity;
    private SimpleArrayMap<String, LoaderManager> mAllLoaderManagers;
    private boolean mCheckedForLoaderManager;
    final Context mContext;
    final FragmentManagerImpl mFragmentManager;
    private final Handler mHandler;
    private LoaderManagerImpl mLoaderManager;
    private boolean mLoadersStarted;
    private boolean mRetainLoaders;
    final int mWindowAnimations;

    /* access modifiers changed from: 0000 */
    public void onAttachFragment(Fragment fragment) {
    }

    public void onDump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
    }

    @Nullable
    public View onFindViewById(int i) {
        return null;
    }

    @Nullable
    public abstract E onGetHost();

    public boolean onHasView() {
        return true;
    }

    public boolean onHasWindowAnimations() {
        return true;
    }

    public void onRequestPermissionsFromFragment(@NonNull Fragment fragment, @NonNull String[] strArr, int i) {
    }

    public boolean onShouldSaveFragmentState(Fragment fragment) {
        return true;
    }

    public boolean onShouldShowRequestPermissionRationale(@NonNull String str) {
        return false;
    }

    public void onSupportInvalidateOptionsMenu() {
    }

    public FragmentHostCallback(Context context, Handler handler, int i) {
        this(null, context, handler, i);
    }

    FragmentHostCallback(FragmentActivity fragmentActivity) {
        this(fragmentActivity, fragmentActivity, fragmentActivity.mHandler, 0);
    }

    FragmentHostCallback(Activity activity, Context context, Handler handler, int i) {
        this.mFragmentManager = new FragmentManagerImpl();
        this.mActivity = activity;
        this.mContext = context;
        this.mHandler = handler;
        this.mWindowAnimations = i;
    }

    public LayoutInflater onGetLayoutInflater() {
        return (LayoutInflater) this.mContext.getSystemService("layout_inflater");
    }

    public void onStartActivityFromFragment(Fragment fragment, Intent intent, int i) {
        onStartActivityFromFragment(fragment, intent, i, null);
    }

    public void onStartActivityFromFragment(Fragment fragment, Intent intent, int i, @Nullable Bundle bundle) {
        if (i == -1) {
            this.mContext.startActivity(intent);
            return;
        }
        throw new IllegalStateException("Starting activity with a requestCode requires a FragmentActivity host");
    }

    public int onGetWindowAnimations() {
        return this.mWindowAnimations;
    }

    /* access modifiers changed from: 0000 */
    public Activity getActivity() {
        return this.mActivity;
    }

    /* access modifiers changed from: 0000 */
    public Context getContext() {
        return this.mContext;
    }

    /* access modifiers changed from: 0000 */
    public Handler getHandler() {
        return this.mHandler;
    }

    /* access modifiers changed from: 0000 */
    public FragmentManagerImpl getFragmentManagerImpl() {
        return this.mFragmentManager;
    }

    /* access modifiers changed from: 0000 */
    public LoaderManagerImpl getLoaderManagerImpl() {
        LoaderManagerImpl loaderManagerImpl = this.mLoaderManager;
        if (loaderManagerImpl != null) {
            return loaderManagerImpl;
        }
        this.mCheckedForLoaderManager = true;
        this.mLoaderManager = getLoaderManager("(root)", this.mLoadersStarted, true);
        return this.mLoaderManager;
    }

    /* access modifiers changed from: 0000 */
    public void inactivateFragment(String str) {
        SimpleArrayMap<String, LoaderManager> simpleArrayMap = this.mAllLoaderManagers;
        if (simpleArrayMap != null) {
            LoaderManagerImpl loaderManagerImpl = (LoaderManagerImpl) simpleArrayMap.get(str);
            if (loaderManagerImpl != null && !loaderManagerImpl.mRetaining) {
                loaderManagerImpl.doDestroy();
                this.mAllLoaderManagers.remove(str);
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public boolean getRetainLoaders() {
        return this.mRetainLoaders;
    }

    /* access modifiers changed from: 0000 */
    public void doLoaderStart() {
        if (!this.mLoadersStarted) {
            this.mLoadersStarted = true;
            LoaderManagerImpl loaderManagerImpl = this.mLoaderManager;
            if (loaderManagerImpl != null) {
                loaderManagerImpl.doStart();
            } else if (!this.mCheckedForLoaderManager) {
                this.mLoaderManager = getLoaderManager("(root)", this.mLoadersStarted, false);
                LoaderManagerImpl loaderManagerImpl2 = this.mLoaderManager;
                if (loaderManagerImpl2 != null && !loaderManagerImpl2.mStarted) {
                    this.mLoaderManager.doStart();
                }
            }
            this.mCheckedForLoaderManager = true;
        }
    }

    /* access modifiers changed from: 0000 */
    public void doLoaderStop(boolean z) {
        this.mRetainLoaders = z;
        LoaderManagerImpl loaderManagerImpl = this.mLoaderManager;
        if (loaderManagerImpl != null && this.mLoadersStarted) {
            this.mLoadersStarted = false;
            if (z) {
                loaderManagerImpl.doRetain();
            } else {
                loaderManagerImpl.doStop();
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void doLoaderRetain() {
        LoaderManagerImpl loaderManagerImpl = this.mLoaderManager;
        if (loaderManagerImpl != null) {
            loaderManagerImpl.doRetain();
        }
    }

    /* access modifiers changed from: 0000 */
    public void doLoaderDestroy() {
        LoaderManagerImpl loaderManagerImpl = this.mLoaderManager;
        if (loaderManagerImpl != null) {
            loaderManagerImpl.doDestroy();
        }
    }

    /* access modifiers changed from: 0000 */
    public void reportLoaderStart() {
        SimpleArrayMap<String, LoaderManager> simpleArrayMap = this.mAllLoaderManagers;
        if (simpleArrayMap != null) {
            int size = simpleArrayMap.size();
            LoaderManagerImpl[] loaderManagerImplArr = new LoaderManagerImpl[size];
            for (int i = size - 1; i >= 0; i--) {
                loaderManagerImplArr[i] = (LoaderManagerImpl) this.mAllLoaderManagers.valueAt(i);
            }
            for (int i2 = 0; i2 < size; i2++) {
                LoaderManagerImpl loaderManagerImpl = loaderManagerImplArr[i2];
                loaderManagerImpl.finishRetain();
                loaderManagerImpl.doReportStart();
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public LoaderManagerImpl getLoaderManager(String str, boolean z, boolean z2) {
        if (this.mAllLoaderManagers == null) {
            this.mAllLoaderManagers = new SimpleArrayMap<>();
        }
        LoaderManagerImpl loaderManagerImpl = (LoaderManagerImpl) this.mAllLoaderManagers.get(str);
        if (loaderManagerImpl != null) {
            loaderManagerImpl.updateHostController(this);
            return loaderManagerImpl;
        } else if (!z2) {
            return loaderManagerImpl;
        } else {
            LoaderManagerImpl loaderManagerImpl2 = new LoaderManagerImpl(str, this, z);
            this.mAllLoaderManagers.put(str, loaderManagerImpl2);
            return loaderManagerImpl2;
        }
    }

    /* access modifiers changed from: 0000 */
    public SimpleArrayMap<String, LoaderManager> retainLoaderNonConfig() {
        SimpleArrayMap<String, LoaderManager> simpleArrayMap = this.mAllLoaderManagers;
        int i = 0;
        if (simpleArrayMap != null) {
            int size = simpleArrayMap.size();
            LoaderManagerImpl[] loaderManagerImplArr = new LoaderManagerImpl[size];
            for (int i2 = size - 1; i2 >= 0; i2--) {
                loaderManagerImplArr[i2] = (LoaderManagerImpl) this.mAllLoaderManagers.valueAt(i2);
            }
            int i3 = 0;
            while (i < size) {
                LoaderManagerImpl loaderManagerImpl = loaderManagerImplArr[i];
                if (loaderManagerImpl.mRetaining) {
                    i3 = 1;
                } else {
                    loaderManagerImpl.doDestroy();
                    this.mAllLoaderManagers.remove(loaderManagerImpl.mWho);
                }
                i++;
            }
            i = i3;
        }
        if (i != 0) {
            return this.mAllLoaderManagers;
        }
        return null;
    }

    /* access modifiers changed from: 0000 */
    public void restoreLoaderNonConfig(SimpleArrayMap<String, LoaderManager> simpleArrayMap) {
        this.mAllLoaderManagers = simpleArrayMap;
    }

    /* access modifiers changed from: 0000 */
    public void dumpLoaders(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.print(str);
        printWriter.print("mLoadersStarted=");
        printWriter.println(this.mLoadersStarted);
        if (this.mLoaderManager != null) {
            printWriter.print(str);
            printWriter.print("Loader Manager ");
            printWriter.print(Integer.toHexString(System.identityHashCode(this.mLoaderManager)));
            printWriter.println(":");
            LoaderManagerImpl loaderManagerImpl = this.mLoaderManager;
            StringBuilder sb = new StringBuilder();
            sb.append(str);
            sb.append("  ");
            loaderManagerImpl.dump(sb.toString(), fileDescriptor, printWriter, strArr);
        }
    }
}
