package android.support.p000v4.view;

import android.view.MenuItem;
import android.view.MenuItem.OnActionExpandListener;

/* renamed from: android.support.v4.view.MenuItemCompatIcs */
class MenuItemCompatIcs {

    /* renamed from: android.support.v4.view.MenuItemCompatIcs$OnActionExpandListenerWrapper */
    static class OnActionExpandListenerWrapper implements OnActionExpandListener {
        private SupportActionExpandProxy mWrapped;

        public OnActionExpandListenerWrapper(SupportActionExpandProxy supportActionExpandProxy) {
            this.mWrapped = supportActionExpandProxy;
        }

        public boolean onMenuItemActionExpand(MenuItem menuItem) {
            return this.mWrapped.onMenuItemActionExpand(menuItem);
        }

        public boolean onMenuItemActionCollapse(MenuItem menuItem) {
            return this.mWrapped.onMenuItemActionCollapse(menuItem);
        }
    }

    /* renamed from: android.support.v4.view.MenuItemCompatIcs$SupportActionExpandProxy */
    interface SupportActionExpandProxy {
        boolean onMenuItemActionCollapse(MenuItem menuItem);

        boolean onMenuItemActionExpand(MenuItem menuItem);
    }

    MenuItemCompatIcs() {
    }

    public static boolean expandActionView(MenuItem menuItem) {
        return menuItem.expandActionView();
    }

    public static boolean collapseActionView(MenuItem menuItem) {
        return menuItem.collapseActionView();
    }

    public static boolean isActionViewExpanded(MenuItem menuItem) {
        return menuItem.isActionViewExpanded();
    }

    public static MenuItem setOnActionExpandListener(MenuItem menuItem, SupportActionExpandProxy supportActionExpandProxy) {
        return menuItem.setOnActionExpandListener(new OnActionExpandListenerWrapper(supportActionExpandProxy));
    }
}
