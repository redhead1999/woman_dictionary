package android.support.p003v7.app;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build.VERSION;
import android.support.p000v4.app.NotificationBuilderWithBuilderAccessor;
import android.support.p000v4.app.NotificationCompat.Style;
import android.support.p000v4.media.session.MediaSessionCompat.Token;

/* renamed from: android.support.v7.app.NotificationCompat */
public class NotificationCompat extends android.support.p000v4.app.NotificationCompat {

    /* renamed from: android.support.v7.app.NotificationCompat$Builder */
    public static class Builder extends android.support.p000v4.app.NotificationCompat.Builder {
        public Builder(Context context) {
            super(context);
        }

        /* access modifiers changed from: protected */
        public BuilderExtender getExtender() {
            if (VERSION.SDK_INT >= 21) {
                return new LollipopExtender();
            }
            if (VERSION.SDK_INT >= 16) {
                return new JellybeanExtender();
            }
            if (VERSION.SDK_INT >= 14) {
                return new IceCreamSandwichExtender();
            }
            return super.getExtender();
        }
    }

    /* renamed from: android.support.v7.app.NotificationCompat$IceCreamSandwichExtender */
    private static class IceCreamSandwichExtender extends BuilderExtender {
        private IceCreamSandwichExtender() {
        }

        public Notification build(android.support.p000v4.app.NotificationCompat.Builder builder, NotificationBuilderWithBuilderAccessor notificationBuilderWithBuilderAccessor) {
            NotificationCompat.addMediaStyleToBuilderIcs(notificationBuilderWithBuilderAccessor, builder);
            return notificationBuilderWithBuilderAccessor.build();
        }
    }

    /* renamed from: android.support.v7.app.NotificationCompat$JellybeanExtender */
    private static class JellybeanExtender extends BuilderExtender {
        private JellybeanExtender() {
        }

        public Notification build(android.support.p000v4.app.NotificationCompat.Builder builder, NotificationBuilderWithBuilderAccessor notificationBuilderWithBuilderAccessor) {
            NotificationCompat.addMediaStyleToBuilderIcs(notificationBuilderWithBuilderAccessor, builder);
            Notification build = notificationBuilderWithBuilderAccessor.build();
            NotificationCompat.addBigMediaStyleToBuilderJellybean(build, builder);
            return build;
        }
    }

    /* renamed from: android.support.v7.app.NotificationCompat$LollipopExtender */
    private static class LollipopExtender extends BuilderExtender {
        private LollipopExtender() {
        }

        public Notification build(android.support.p000v4.app.NotificationCompat.Builder builder, NotificationBuilderWithBuilderAccessor notificationBuilderWithBuilderAccessor) {
            NotificationCompat.addMediaStyleToBuilderLollipop(notificationBuilderWithBuilderAccessor, builder.mStyle);
            return notificationBuilderWithBuilderAccessor.build();
        }
    }

    /* renamed from: android.support.v7.app.NotificationCompat$MediaStyle */
    public static class MediaStyle extends Style {
        int[] mActionsToShowInCompact = null;
        PendingIntent mCancelButtonIntent;
        boolean mShowCancelButton;
        Token mToken;

        public MediaStyle() {
        }

        public MediaStyle(android.support.p000v4.app.NotificationCompat.Builder builder) {
            setBuilder(builder);
        }

        public MediaStyle setShowActionsInCompactView(int... iArr) {
            this.mActionsToShowInCompact = iArr;
            return this;
        }

        public MediaStyle setMediaSession(Token token) {
            this.mToken = token;
            return this;
        }

        public MediaStyle setShowCancelButton(boolean z) {
            this.mShowCancelButton = z;
            return this;
        }

        public MediaStyle setCancelButtonIntent(PendingIntent pendingIntent) {
            this.mCancelButtonIntent = pendingIntent;
            return this;
        }
    }

    /* access modifiers changed from: private */
    public static void addMediaStyleToBuilderLollipop(NotificationBuilderWithBuilderAccessor notificationBuilderWithBuilderAccessor, Style style) {
        if (style instanceof MediaStyle) {
            MediaStyle mediaStyle = (MediaStyle) style;
            NotificationCompatImpl21.addMediaStyle(notificationBuilderWithBuilderAccessor, mediaStyle.mActionsToShowInCompact, mediaStyle.mToken != null ? mediaStyle.mToken.getToken() : null);
        }
    }

    /* access modifiers changed from: private */
    public static void addMediaStyleToBuilderIcs(NotificationBuilderWithBuilderAccessor notificationBuilderWithBuilderAccessor, android.support.p000v4.app.NotificationCompat.Builder builder) {
        android.support.p000v4.app.NotificationCompat.Builder builder2 = builder;
        if (builder2.mStyle instanceof MediaStyle) {
            MediaStyle mediaStyle = (MediaStyle) builder2.mStyle;
            NotificationCompatImplBase.overrideContentView(notificationBuilderWithBuilderAccessor, builder2.mContext, builder2.mContentTitle, builder2.mContentText, builder2.mContentInfo, builder2.mNumber, builder2.mLargeIcon, builder2.mSubText, builder2.mUseChronometer, builder2.mNotification.when, builder2.mActions, mediaStyle.mActionsToShowInCompact, mediaStyle.mShowCancelButton, mediaStyle.mCancelButtonIntent);
        }
    }

    /* access modifiers changed from: private */
    public static void addBigMediaStyleToBuilderJellybean(Notification notification, android.support.p000v4.app.NotificationCompat.Builder builder) {
        android.support.p000v4.app.NotificationCompat.Builder builder2 = builder;
        if (builder2.mStyle instanceof MediaStyle) {
            MediaStyle mediaStyle = (MediaStyle) builder2.mStyle;
            NotificationCompatImplBase.overrideBigContentView(notification, builder2.mContext, builder2.mContentTitle, builder2.mContentText, builder2.mContentInfo, builder2.mNumber, builder2.mLargeIcon, builder2.mSubText, builder2.mUseChronometer, builder2.mNotification.when, builder2.mActions, mediaStyle.mShowCancelButton, mediaStyle.mCancelButtonIntent);
        }
    }
}
