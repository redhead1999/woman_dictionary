package android.support.p003v7.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build.VERSION;
import android.support.p000v4.view.ViewConfigurationCompat;
import android.support.p003v7.appcompat.C0254R;
import android.view.ViewConfiguration;

/* renamed from: android.support.v7.view.ActionBarPolicy */
public class ActionBarPolicy {
    private Context mContext;

    public static ActionBarPolicy get(Context context) {
        return new ActionBarPolicy(context);
    }

    private ActionBarPolicy(Context context) {
        this.mContext = context;
    }

    public int getMaxActionButtons() {
        return this.mContext.getResources().getInteger(C0254R.integer.abc_max_action_buttons);
    }

    public boolean showsOverflowMenuButton() {
        if (VERSION.SDK_INT >= 19) {
            return true;
        }
        return !ViewConfigurationCompat.hasPermanentMenuKey(ViewConfiguration.get(this.mContext));
    }

    public int getEmbeddedMenuWidthLimit() {
        return this.mContext.getResources().getDisplayMetrics().widthPixels / 2;
    }

    public boolean hasEmbeddedTabs() {
        if (this.mContext.getApplicationInfo().targetSdkVersion >= 16) {
            return this.mContext.getResources().getBoolean(C0254R.bool.abc_action_bar_embed_tabs);
        }
        return this.mContext.getResources().getBoolean(C0254R.bool.abc_action_bar_embed_tabs_pre_jb);
    }

    public int getTabContainerHeight() {
        TypedArray obtainStyledAttributes = this.mContext.obtainStyledAttributes(null, C0254R.styleable.ActionBar, C0254R.attr.actionBarStyle, 0);
        int layoutDimension = obtainStyledAttributes.getLayoutDimension(C0254R.styleable.ActionBar_height, 0);
        Resources resources = this.mContext.getResources();
        if (!hasEmbeddedTabs()) {
            layoutDimension = Math.min(layoutDimension, resources.getDimensionPixelSize(C0254R.dimen.abc_action_bar_stacked_max_height));
        }
        obtainStyledAttributes.recycle();
        return layoutDimension;
    }

    public boolean enableHomeButtonByDefault() {
        return this.mContext.getApplicationInfo().targetSdkVersion < 14;
    }

    public int getStackedTabMaxWidth() {
        return this.mContext.getResources().getDimensionPixelSize(C0254R.dimen.abc_action_bar_stacked_tab_max_width);
    }
}
