package android.support.p000v4.media;

import android.os.Bundle;
import android.support.p000v4.media.MediaBrowserCompat.MediaItem;
import java.util.List;

/* renamed from: android.support.v4.media.MediaBrowserCompatUtils */
public class MediaBrowserCompatUtils {
    public static boolean areSameOptions(Bundle bundle, Bundle bundle2) {
        boolean z = true;
        if (bundle == bundle2) {
            return true;
        }
        String str = MediaBrowserCompat.EXTRA_PAGE_SIZE;
        String str2 = MediaBrowserCompat.EXTRA_PAGE;
        if (bundle == null) {
            if (!(bundle2.getInt(str2, -1) == -1 && bundle2.getInt(str, -1) == -1)) {
                z = false;
            }
            return z;
        } else if (bundle2 == null) {
            if (!(bundle.getInt(str2, -1) == -1 && bundle.getInt(str, -1) == -1)) {
                z = false;
            }
            return z;
        } else {
            if (!(bundle.getInt(str2, -1) == bundle2.getInt(str2, -1) && bundle.getInt(str, -1) == bundle2.getInt(str, -1))) {
                z = false;
            }
            return z;
        }
    }

    public static boolean hasDuplicatedItems(Bundle bundle, Bundle bundle2) {
        int i;
        int i2;
        int i3;
        int i4;
        int i5;
        int i6;
        String str = MediaBrowserCompat.EXTRA_PAGE;
        int i7 = bundle == null ? -1 : bundle.getInt(str, -1);
        if (bundle2 == null) {
            i = -1;
        } else {
            i = bundle2.getInt(str, -1);
        }
        String str2 = MediaBrowserCompat.EXTRA_PAGE_SIZE;
        if (bundle == null) {
            i2 = -1;
        } else {
            i2 = bundle.getInt(str2, -1);
        }
        if (bundle2 == null) {
            i3 = -1;
        } else {
            i3 = bundle2.getInt(str2, -1);
        }
        int i8 = ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        if (i7 == -1 || i2 == -1) {
            i4 = ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
            i5 = 0;
        } else {
            i5 = (i7 - 1) * i2;
            i4 = (i2 + i5) - 1;
        }
        if (i == -1 || i3 == -1) {
            i6 = 0;
        } else {
            i6 = (i - 1) * i3;
            i8 = (i3 + i6) - 1;
        }
        if (i5 > i6 || i6 > i4) {
            return i5 <= i8 && i8 <= i4;
        }
        return true;
    }

    public static List<MediaItem> applyOptions(List<MediaItem> list, Bundle bundle) {
        int i = bundle.getInt(MediaBrowserCompat.EXTRA_PAGE, -1);
        int i2 = bundle.getInt(MediaBrowserCompat.EXTRA_PAGE_SIZE, -1);
        if (i == -1 && i2 == -1) {
            return list;
        }
        int i3 = (i - 1) * i2;
        int i4 = i3 + i2;
        if (i < 1 || i2 < 1 || i3 >= list.size()) {
            return null;
        }
        if (i4 > list.size()) {
            i4 = list.size();
        }
        return list.subList(i3, i4);
    }
}
