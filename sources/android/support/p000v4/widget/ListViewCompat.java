package android.support.p000v4.widget;

import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.widget.ListView;

/* renamed from: android.support.v4.widget.ListViewCompat */
public final class ListViewCompat {
    public static void scrollListBy(@NonNull ListView listView, int i) {
        if (VERSION.SDK_INT >= 19) {
            ListViewCompatKitKat.scrollListBy(listView, i);
        } else {
            ListViewCompatDonut.scrollListBy(listView, i);
        }
    }

    private ListViewCompat() {
    }
}
