package android.support.p000v4.media.session;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.p000v4.media.MediaBrowserServiceCompat;
import android.view.KeyEvent;
import java.util.List;

/* renamed from: android.support.v4.media.session.MediaButtonReceiver */
public class MediaButtonReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        Intent intent2 = new Intent("android.intent.action.MEDIA_BUTTON");
        intent2.setPackage(context.getPackageName());
        PackageManager packageManager = context.getPackageManager();
        List queryIntentServices = packageManager.queryIntentServices(intent2, 0);
        if (queryIntentServices.isEmpty()) {
            intent2.setAction(MediaBrowserServiceCompat.SERVICE_INTERFACE);
            queryIntentServices = packageManager.queryIntentServices(intent2, 0);
        }
        if (queryIntentServices.isEmpty()) {
            throw new IllegalStateException("Could not find any Service that handles android.intent.action.MEDIA_BUTTON or a media browser service implementation");
        } else if (queryIntentServices.size() == 1) {
            ResolveInfo resolveInfo = (ResolveInfo) queryIntentServices.get(0);
            intent.setComponent(new ComponentName(resolveInfo.serviceInfo.packageName, resolveInfo.serviceInfo.name));
            context.startService(intent);
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("Expected 1 Service that handles ");
            sb.append(intent2.getAction());
            sb.append(", found ");
            sb.append(queryIntentServices.size());
            throw new IllegalStateException(sb.toString());
        }
    }

    public static KeyEvent handleIntent(MediaSessionCompat mediaSessionCompat, Intent intent) {
        if (!(mediaSessionCompat == null || intent == null)) {
            if ("android.intent.action.MEDIA_BUTTON".equals(intent.getAction())) {
                String str = "android.intent.extra.KEY_EVENT";
                if (intent.hasExtra(str)) {
                    KeyEvent keyEvent = (KeyEvent) intent.getParcelableExtra(str);
                    mediaSessionCompat.getController().dispatchMediaButtonEvent(keyEvent);
                    return keyEvent;
                }
            }
        }
        return null;
    }
}
