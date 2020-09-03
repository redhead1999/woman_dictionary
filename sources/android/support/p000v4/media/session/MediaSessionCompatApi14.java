package android.support.p000v4.media.session;

import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.RemoteControlClient;
import android.media.RemoteControlClient.MetadataEditor;
import android.os.Bundle;

/* renamed from: android.support.v4.media.session.MediaSessionCompatApi14 */
class MediaSessionCompatApi14 {
    private static final long ACTION_FAST_FORWARD = 64;
    private static final long ACTION_PAUSE = 2;
    private static final long ACTION_PLAY = 4;
    private static final long ACTION_PLAY_PAUSE = 512;
    private static final long ACTION_REWIND = 8;
    private static final long ACTION_SKIP_TO_NEXT = 32;
    private static final long ACTION_SKIP_TO_PREVIOUS = 16;
    private static final long ACTION_STOP = 1;
    private static final String METADATA_KEY_ALBUM = "android.media.metadata.ALBUM";
    private static final String METADATA_KEY_ALBUM_ART = "android.media.metadata.ALBUM_ART";
    private static final String METADATA_KEY_ALBUM_ARTIST = "android.media.metadata.ALBUM_ARTIST";
    private static final String METADATA_KEY_ART = "android.media.metadata.ART";
    private static final String METADATA_KEY_ARTIST = "android.media.metadata.ARTIST";
    private static final String METADATA_KEY_AUTHOR = "android.media.metadata.AUTHOR";
    private static final String METADATA_KEY_COMPILATION = "android.media.metadata.COMPILATION";
    private static final String METADATA_KEY_COMPOSER = "android.media.metadata.COMPOSER";
    private static final String METADATA_KEY_DATE = "android.media.metadata.DATE";
    private static final String METADATA_KEY_DISC_NUMBER = "android.media.metadata.DISC_NUMBER";
    private static final String METADATA_KEY_DURATION = "android.media.metadata.DURATION";
    private static final String METADATA_KEY_GENRE = "android.media.metadata.GENRE";
    private static final String METADATA_KEY_TITLE = "android.media.metadata.TITLE";
    private static final String METADATA_KEY_TRACK_NUMBER = "android.media.metadata.TRACK_NUMBER";
    private static final String METADATA_KEY_WRITER = "android.media.metadata.WRITER";
    static final int RCC_PLAYSTATE_NONE = 0;
    static final int STATE_BUFFERING = 6;
    static final int STATE_CONNECTING = 8;
    static final int STATE_ERROR = 7;
    static final int STATE_FAST_FORWARDING = 4;
    static final int STATE_NONE = 0;
    static final int STATE_PAUSED = 2;
    static final int STATE_PLAYING = 3;
    static final int STATE_REWINDING = 5;
    static final int STATE_SKIPPING_TO_NEXT = 10;
    static final int STATE_SKIPPING_TO_PREVIOUS = 9;
    static final int STATE_SKIPPING_TO_QUEUE_ITEM = 11;
    static final int STATE_STOPPED = 1;

    static int getRccStateFromState(int i) {
        switch (i) {
            case 0:
                return 0;
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 3;
            case 4:
                return 4;
            case 5:
                return 5;
            case 6:
            case 8:
                return 8;
            case 7:
                return 9;
            case 9:
                return 7;
            case 10:
            case 11:
                return 6;
            default:
                return -1;
        }
    }

    static int getRccTransportControlFlagsFromActions(long j) {
        int i = (1 & j) != 0 ? 32 : 0;
        if ((2 & j) != 0) {
            i |= 16;
        }
        if ((4 & j) != 0) {
            i |= 4;
        }
        if ((8 & j) != 0) {
            i |= 2;
        }
        if ((16 & j) != 0) {
            i |= 1;
        }
        if ((32 & j) != 0) {
            i |= 128;
        }
        if ((64 & j) != 0) {
            i |= 64;
        }
        return (j & 512) != 0 ? i | 8 : i;
    }

    MediaSessionCompatApi14() {
    }

    public static Object createRemoteControlClient(PendingIntent pendingIntent) {
        return new RemoteControlClient(pendingIntent);
    }

    public static void setState(Object obj, int i) {
        ((RemoteControlClient) obj).setPlaybackState(getRccStateFromState(i));
    }

    public static void setTransportControlFlags(Object obj, long j) {
        ((RemoteControlClient) obj).setTransportControlFlags(getRccTransportControlFlagsFromActions(j));
    }

    public static void setMetadata(Object obj, Bundle bundle) {
        MetadataEditor editMetadata = ((RemoteControlClient) obj).editMetadata(true);
        buildOldMetadata(bundle, editMetadata);
        editMetadata.apply();
    }

    public static void registerRemoteControlClient(Context context, Object obj) {
        ((AudioManager) context.getSystemService("audio")).registerRemoteControlClient((RemoteControlClient) obj);
    }

    public static void unregisterRemoteControlClient(Context context, Object obj) {
        ((AudioManager) context.getSystemService("audio")).unregisterRemoteControlClient((RemoteControlClient) obj);
    }

    static void buildOldMetadata(Bundle bundle, MetadataEditor metadataEditor) {
        if (bundle != null) {
            String str = "android.media.metadata.ART";
            if (bundle.containsKey(str)) {
                metadataEditor.putBitmap(100, (Bitmap) bundle.getParcelable(str));
            } else {
                String str2 = "android.media.metadata.ALBUM_ART";
                if (bundle.containsKey(str2)) {
                    metadataEditor.putBitmap(100, (Bitmap) bundle.getParcelable(str2));
                }
            }
            String str3 = "android.media.metadata.ALBUM";
            if (bundle.containsKey(str3)) {
                metadataEditor.putString(1, bundle.getString(str3));
            }
            String str4 = "android.media.metadata.ALBUM_ARTIST";
            if (bundle.containsKey(str4)) {
                metadataEditor.putString(13, bundle.getString(str4));
            }
            String str5 = "android.media.metadata.ARTIST";
            if (bundle.containsKey(str5)) {
                metadataEditor.putString(2, bundle.getString(str5));
            }
            String str6 = "android.media.metadata.AUTHOR";
            if (bundle.containsKey(str6)) {
                metadataEditor.putString(3, bundle.getString(str6));
            }
            String str7 = "android.media.metadata.COMPILATION";
            if (bundle.containsKey(str7)) {
                metadataEditor.putString(15, bundle.getString(str7));
            }
            String str8 = "android.media.metadata.COMPOSER";
            if (bundle.containsKey(str8)) {
                metadataEditor.putString(4, bundle.getString(str8));
            }
            String str9 = "android.media.metadata.DATE";
            if (bundle.containsKey(str9)) {
                metadataEditor.putString(5, bundle.getString(str9));
            }
            String str10 = "android.media.metadata.DISC_NUMBER";
            if (bundle.containsKey(str10)) {
                metadataEditor.putLong(14, bundle.getLong(str10));
            }
            String str11 = "android.media.metadata.DURATION";
            if (bundle.containsKey(str11)) {
                metadataEditor.putLong(9, bundle.getLong(str11));
            }
            String str12 = "android.media.metadata.GENRE";
            if (bundle.containsKey(str12)) {
                metadataEditor.putString(6, bundle.getString(str12));
            }
            String str13 = "android.media.metadata.TITLE";
            if (bundle.containsKey(str13)) {
                metadataEditor.putString(7, bundle.getString(str13));
            }
            String str14 = "android.media.metadata.TRACK_NUMBER";
            if (bundle.containsKey(str14)) {
                metadataEditor.putLong(0, bundle.getLong(str14));
            }
            if (bundle.containsKey("android.media.metadata.WRITER")) {
                metadataEditor.putString(11, bundle.getString("android.media.metadata.WRITER"));
            }
        }
    }
}
