package android.support.p003v7.app;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.p003v7.app.ActionBarDrawerToggle.Delegate;
import android.support.p003v7.appcompat.C0254R;
import android.support.p003v7.view.ActionMode;
import android.support.p003v7.view.SupportMenuInflater;
import android.support.p003v7.view.WindowCallbackWrapper;
import android.support.p003v7.view.menu.MenuBuilder;
import android.support.p003v7.widget.TintTypedArray;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.Window;
import android.view.Window.Callback;

/* renamed from: android.support.v7.app.AppCompatDelegateImplBase */
abstract class AppCompatDelegateImplBase extends AppCompatDelegate {
    ActionBar mActionBar;
    final AppCompatCallback mAppCompatCallback;
    final Callback mAppCompatWindowCallback;
    final Context mContext;
    boolean mHasActionBar;
    private boolean mIsDestroyed;
    boolean mIsFloating;
    MenuInflater mMenuInflater;
    final Callback mOriginalWindowCallback = this.mWindow.getCallback();
    boolean mOverlayActionBar;
    boolean mOverlayActionMode;
    boolean mThemeRead;
    private CharSequence mTitle;
    final Window mWindow;
    boolean mWindowNoTitle;

    /* renamed from: android.support.v7.app.AppCompatDelegateImplBase$ActionBarDrawableToggleImpl */
    private class ActionBarDrawableToggleImpl implements Delegate {
        private ActionBarDrawableToggleImpl() {
        }

        public Drawable getThemeUpIndicator() {
            TintTypedArray obtainStyledAttributes = TintTypedArray.obtainStyledAttributes(getActionBarThemedContext(), null, new int[]{C0254R.attr.homeAsUpIndicator});
            Drawable drawable = obtainStyledAttributes.getDrawable(0);
            obtainStyledAttributes.recycle();
            return drawable;
        }

        public Context getActionBarThemedContext() {
            return AppCompatDelegateImplBase.this.getActionBarThemedContext();
        }

        public boolean isNavigationVisible() {
            ActionBar supportActionBar = AppCompatDelegateImplBase.this.getSupportActionBar();
            return (supportActionBar == null || (supportActionBar.getDisplayOptions() & 4) == 0) ? false : true;
        }

        public void setActionBarUpIndicator(Drawable drawable, int i) {
            ActionBar supportActionBar = AppCompatDelegateImplBase.this.getSupportActionBar();
            if (supportActionBar != null) {
                supportActionBar.setHomeAsUpIndicator(drawable);
                supportActionBar.setHomeActionContentDescription(i);
            }
        }

        public void setActionBarDescription(int i) {
            ActionBar supportActionBar = AppCompatDelegateImplBase.this.getSupportActionBar();
            if (supportActionBar != null) {
                supportActionBar.setHomeActionContentDescription(i);
            }
        }
    }

    /* renamed from: android.support.v7.app.AppCompatDelegateImplBase$AppCompatWindowCallbackBase */
    class AppCompatWindowCallbackBase extends WindowCallbackWrapper {
        public void onContentChanged() {
        }

        AppCompatWindowCallbackBase(Callback callback) {
            super(callback);
        }

        public boolean dispatchKeyEvent(KeyEvent keyEvent) {
            return AppCompatDelegateImplBase.this.dispatchKeyEvent(keyEvent) || super.dispatchKeyEvent(keyEvent);
        }

        public boolean dispatchKeyShortcutEvent(KeyEvent keyEvent) {
            return super.dispatchKeyShortcutEvent(keyEvent) || AppCompatDelegateImplBase.this.onKeyShortcut(keyEvent.getKeyCode(), keyEvent);
        }

        public boolean onCreatePanelMenu(int i, Menu menu) {
            if (i != 0 || (menu instanceof MenuBuilder)) {
                return super.onCreatePanelMenu(i, menu);
            }
            return false;
        }

        public boolean onPreparePanel(int i, View view, Menu menu) {
            MenuBuilder menuBuilder = menu instanceof MenuBuilder ? (MenuBuilder) menu : null;
            if (i == 0 && menuBuilder == null) {
                return false;
            }
            if (menuBuilder != null) {
                menuBuilder.setOverrideVisibleItems(true);
            }
            boolean onPreparePanel = super.onPreparePanel(i, view, menu);
            if (menuBuilder != null) {
                menuBuilder.setOverrideVisibleItems(false);
            }
            return onPreparePanel;
        }

        public boolean onMenuOpened(int i, Menu menu) {
            super.onMenuOpened(i, menu);
            AppCompatDelegateImplBase.this.onMenuOpened(i, menu);
            return true;
        }

        public void onPanelClosed(int i, Menu menu) {
            super.onPanelClosed(i, menu);
            AppCompatDelegateImplBase.this.onPanelClosed(i, menu);
        }
    }

    public boolean applyDayNight() {
        return false;
    }

    /* access modifiers changed from: 0000 */
    public abstract boolean dispatchKeyEvent(KeyEvent keyEvent);

    /* access modifiers changed from: 0000 */
    public abstract void initWindowDecorActionBar();

    public boolean isHandleNativeActionModesEnabled() {
        return false;
    }

    /* access modifiers changed from: 0000 */
    public abstract boolean onKeyShortcut(int i, KeyEvent keyEvent);

    /* access modifiers changed from: 0000 */
    public abstract boolean onMenuOpened(int i, Menu menu);

    /* access modifiers changed from: 0000 */
    public abstract void onPanelClosed(int i, Menu menu);

    public void onSaveInstanceState(Bundle bundle) {
    }

    /* access modifiers changed from: 0000 */
    public abstract void onTitleChanged(CharSequence charSequence);

    public void setHandleNativeActionModesEnabled(boolean z) {
    }

    public void setLocalNightMode(int i) {
    }

    /* access modifiers changed from: 0000 */
    public abstract ActionMode startSupportActionModeFromWindow(ActionMode.Callback callback);

    AppCompatDelegateImplBase(Context context, Window window, AppCompatCallback appCompatCallback) {
        this.mContext = context;
        this.mWindow = window;
        this.mAppCompatCallback = appCompatCallback;
        Callback callback = this.mOriginalWindowCallback;
        if (!(callback instanceof AppCompatWindowCallbackBase)) {
            this.mAppCompatWindowCallback = wrapWindowCallback(callback);
            this.mWindow.setCallback(this.mAppCompatWindowCallback);
            return;
        }
        throw new IllegalStateException("AppCompat has already installed itself into the Window");
    }

    /* access modifiers changed from: 0000 */
    public Callback wrapWindowCallback(Callback callback) {
        return new AppCompatWindowCallbackBase(callback);
    }

    public ActionBar getSupportActionBar() {
        initWindowDecorActionBar();
        return this.mActionBar;
    }

    /* access modifiers changed from: 0000 */
    public final ActionBar peekSupportActionBar() {
        return this.mActionBar;
    }

    public MenuInflater getMenuInflater() {
        if (this.mMenuInflater == null) {
            initWindowDecorActionBar();
            ActionBar actionBar = this.mActionBar;
            this.mMenuInflater = new SupportMenuInflater(actionBar != null ? actionBar.getThemedContext() : this.mContext);
        }
        return this.mMenuInflater;
    }

    public final Delegate getDrawerToggleDelegate() {
        return new ActionBarDrawableToggleImpl();
    }

    /* access modifiers changed from: 0000 */
    public final Context getActionBarThemedContext() {
        ActionBar supportActionBar = getSupportActionBar();
        Context themedContext = supportActionBar != null ? supportActionBar.getThemedContext() : null;
        return themedContext == null ? this.mContext : themedContext;
    }

    public void onDestroy() {
        this.mIsDestroyed = true;
    }

    /* access modifiers changed from: 0000 */
    public final boolean isDestroyed() {
        return this.mIsDestroyed;
    }

    /* access modifiers changed from: 0000 */
    public final Callback getWindowCallback() {
        return this.mWindow.getCallback();
    }

    public final void setTitle(CharSequence charSequence) {
        this.mTitle = charSequence;
        onTitleChanged(charSequence);
    }

    /* access modifiers changed from: 0000 */
    public final CharSequence getTitle() {
        Callback callback = this.mOriginalWindowCallback;
        if (callback instanceof Activity) {
            return ((Activity) callback).getTitle();
        }
        return this.mTitle;
    }
}
