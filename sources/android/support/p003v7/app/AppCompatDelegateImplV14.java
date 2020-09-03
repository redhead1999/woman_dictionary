package android.support.p003v7.app;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.p003v7.view.SupportActionModeWrapper.CallbackWrapper;
import android.util.Log;
import android.view.ActionMode;
import android.view.Window;
import android.view.Window.Callback;

/* renamed from: android.support.v7.app.AppCompatDelegateImplV14 */
class AppCompatDelegateImplV14 extends AppCompatDelegateImplV11 {
    private static final String KEY_LOCAL_NIGHT_MODE = "appcompat:local_night_mode";
    private static TwilightManager sTwilightManager;
    private boolean mApplyDayNightCalled;
    private boolean mHandleNativeActionModes = true;
    private int mLocalNightMode = -100;

    /* renamed from: android.support.v7.app.AppCompatDelegateImplV14$AppCompatWindowCallbackV14 */
    class AppCompatWindowCallbackV14 extends AppCompatWindowCallbackBase {
        AppCompatWindowCallbackV14(Callback callback) {
            super(callback);
        }

        public ActionMode onWindowStartingActionMode(ActionMode.Callback callback) {
            if (AppCompatDelegateImplV14.this.isHandleNativeActionModesEnabled()) {
                return startAsSupportActionMode(callback);
            }
            return super.onWindowStartingActionMode(callback);
        }

        /* access modifiers changed from: 0000 */
        public final ActionMode startAsSupportActionMode(ActionMode.Callback callback) {
            CallbackWrapper callbackWrapper = new CallbackWrapper(AppCompatDelegateImplV14.this.mContext, callback);
            android.support.p003v7.view.ActionMode startSupportActionMode = AppCompatDelegateImplV14.this.startSupportActionMode(callbackWrapper);
            if (startSupportActionMode != null) {
                return callbackWrapper.getActionModeWrapper(startSupportActionMode);
            }
            return null;
        }
    }

    AppCompatDelegateImplV14(Context context, Window window, AppCompatCallback appCompatCallback) {
        super(context, window, appCompatCallback);
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle != null && this.mLocalNightMode == -100) {
            this.mLocalNightMode = bundle.getInt(KEY_LOCAL_NIGHT_MODE, -100);
        }
    }

    /* access modifiers changed from: 0000 */
    public Callback wrapWindowCallback(Callback callback) {
        return new AppCompatWindowCallbackV14(callback);
    }

    public void setHandleNativeActionModesEnabled(boolean z) {
        this.mHandleNativeActionModes = z;
    }

    public boolean isHandleNativeActionModesEnabled() {
        return this.mHandleNativeActionModes;
    }

    public boolean applyDayNight() {
        this.mApplyDayNightCalled = true;
        int i = this.mLocalNightMode;
        if (i == -100) {
            i = getDefaultNightMode();
        }
        int mapNightMode = mapNightMode(i);
        if (mapNightMode != -1) {
            return updateConfigurationForNightMode(mapNightMode);
        }
        return false;
    }

    public void setLocalNightMode(int i) {
        if (i != -1 && i != 0 && i != 1 && i != 2) {
            Log.d("AppCompatDelegate", "setLocalNightMode() called with an unknown mode");
        } else if (this.mLocalNightMode != i) {
            this.mLocalNightMode = i;
            if (this.mApplyDayNightCalled) {
                applyDayNight();
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public int mapNightMode(int i) {
        if (i == -100) {
            return -1;
        }
        if (i != 0) {
            return i;
        }
        return getTwilightManager().isNight() ? 2 : 1;
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        int i = this.mLocalNightMode;
        if (i != -100) {
            bundle.putInt(KEY_LOCAL_NIGHT_MODE, i);
        }
    }

    private boolean updateConfigurationForNightMode(int i) {
        Resources resources = this.mContext.getResources();
        Configuration configuration = resources.getConfiguration();
        int i2 = configuration.uiMode & 48;
        int i3 = i == 2 ? 32 : 16;
        if (i2 == i3) {
            return false;
        }
        Configuration configuration2 = new Configuration(configuration);
        configuration2.uiMode = i3 | (configuration2.uiMode & -49);
        resources.updateConfiguration(configuration2, null);
        return true;
    }

    private TwilightManager getTwilightManager() {
        if (sTwilightManager == null) {
            sTwilightManager = new TwilightManager(this.mContext.getApplicationContext());
        }
        return sTwilightManager;
    }
}
