package android.support.p003v7.app;

import android.support.p003v7.app.ActionBar.OnNavigationListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

/* renamed from: android.support.v7.app.NavItemSelectedListener */
class NavItemSelectedListener implements OnItemSelectedListener {
    private final OnNavigationListener mListener;

    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    public NavItemSelectedListener(OnNavigationListener onNavigationListener) {
        this.mListener = onNavigationListener;
    }

    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
        OnNavigationListener onNavigationListener = this.mListener;
        if (onNavigationListener != null) {
            onNavigationListener.onNavigationItemSelected(i, j);
        }
    }
}
