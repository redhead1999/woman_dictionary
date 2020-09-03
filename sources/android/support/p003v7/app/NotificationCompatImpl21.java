package android.support.p003v7.app;

import android.app.Notification.MediaStyle;
import android.media.session.MediaSession.Token;
import android.support.p000v4.app.NotificationBuilderWithBuilderAccessor;

/* renamed from: android.support.v7.app.NotificationCompatImpl21 */
class NotificationCompatImpl21 {
    NotificationCompatImpl21() {
    }

    public static void addMediaStyle(NotificationBuilderWithBuilderAccessor notificationBuilderWithBuilderAccessor, int[] iArr, Object obj) {
        MediaStyle mediaStyle = new MediaStyle(notificationBuilderWithBuilderAccessor.getBuilder());
        if (iArr != null) {
            mediaStyle.setShowActionsInCompactView(iArr);
        }
        if (obj != null) {
            mediaStyle.setMediaSession((Token) obj);
        }
    }
}
