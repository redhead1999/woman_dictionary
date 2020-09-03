package android.support.p000v4.media.session;

import android.media.Rating;
import android.media.RemoteControlClient;
import android.media.RemoteControlClient.MetadataEditor;
import android.os.Bundle;

/* renamed from: android.support.v4.media.session.MediaSessionCompatApi19 */
class MediaSessionCompatApi19 {
    private static final long ACTION_SET_RATING = 128;
    private static final String METADATA_KEY_RATING = "android.media.metadata.RATING";
    private static final String METADATA_KEY_USER_RATING = "android.media.metadata.USER_RATING";
    private static final String METADATA_KEY_YEAR = "android.media.metadata.YEAR";

    /* renamed from: android.support.v4.media.session.MediaSessionCompatApi19$Callback */
    interface Callback extends Callback {
        void onSetRating(Object obj);
    }

    /* renamed from: android.support.v4.media.session.MediaSessionCompatApi19$OnMetadataUpdateListener */
    static class OnMetadataUpdateListener<T extends Callback> implements android.media.RemoteControlClient.OnMetadataUpdateListener {
        protected final T mCallback;

        public OnMetadataUpdateListener(T t) {
            this.mCallback = t;
        }

        public void onMetadataUpdate(int i, Object obj) {
            if (i == 268435457 && (obj instanceof Rating)) {
                this.mCallback.onSetRating(obj);
            }
        }
    }

    MediaSessionCompatApi19() {
    }

    public static void setTransportControlFlags(Object obj, long j) {
        ((RemoteControlClient) obj).setTransportControlFlags(getRccTransportControlFlagsFromActions(j));
    }

    public static Object createMetadataUpdateListener(Callback callback) {
        return new OnMetadataUpdateListener(callback);
    }

    public static void setMetadata(Object obj, Bundle bundle, long j) {
        MetadataEditor editMetadata = ((RemoteControlClient) obj).editMetadata(true);
        MediaSessionCompatApi14.buildOldMetadata(bundle, editMetadata);
        addNewMetadata(bundle, editMetadata);
        if ((j & 128) != 0) {
            editMetadata.addEditableKey(268435457);
        }
        editMetadata.apply();
    }

    public static void setOnMetadataUpdateListener(Object obj, Object obj2) {
        ((RemoteControlClient) obj).setMetadataUpdateListener((android.media.RemoteControlClient.OnMetadataUpdateListener) obj2);
    }

    static int getRccTransportControlFlagsFromActions(long j) {
        int rccTransportControlFlagsFromActions = MediaSessionCompatApi18.getRccTransportControlFlagsFromActions(j);
        return (j & 128) != 0 ? rccTransportControlFlagsFromActions | 512 : rccTransportControlFlagsFromActions;
    }

    static void addNewMetadata(Bundle bundle, MetadataEditor metadataEditor) {
        if (bundle != null) {
            String str = "android.media.metadata.YEAR";
            if (bundle.containsKey(str)) {
                metadataEditor.putLong(8, bundle.getLong(str));
            }
            String str2 = "android.media.metadata.RATING";
            if (bundle.containsKey(str2)) {
                metadataEditor.putObject(101, bundle.getParcelable(str2));
            }
            String str3 = "android.media.metadata.USER_RATING";
            if (bundle.containsKey(str3)) {
                metadataEditor.putObject(268435457, bundle.getParcelable(str3));
            }
        }
    }
}
