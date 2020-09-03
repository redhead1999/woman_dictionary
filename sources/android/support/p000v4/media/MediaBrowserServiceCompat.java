package android.support.p000v4.media;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.p000v4.app.BundleCompat;
import android.support.p000v4.media.MediaBrowserCompat.MediaItem;
import android.support.p000v4.media.MediaBrowserServiceCompatApi23.ItemCallback;
import android.support.p000v4.media.session.MediaSessionCompat.Token;
import android.support.p000v4.p002os.ResultReceiver;
import android.support.p000v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/* renamed from: android.support.v4.media.MediaBrowserServiceCompat */
public abstract class MediaBrowserServiceCompat extends Service {
    private static final boolean DBG = false;
    public static final String KEY_MEDIA_ITEM = "media_item";
    private static final int RESULT_FLAG_OPTION_NOT_HANDLED = 1;
    public static final String SERVICE_INTERFACE = "android.media.browse.MediaBrowserService";
    private static final String TAG = "MediaBrowserServiceCompat";
    /* access modifiers changed from: private */
    public final ArrayMap<IBinder, ConnectionRecord> mConnections = new ArrayMap<>();
    /* access modifiers changed from: private */
    public final ServiceHandler mHandler = new ServiceHandler();
    private MediaBrowserServiceImpl mImpl;
    Token mSession;

    /* renamed from: android.support.v4.media.MediaBrowserServiceCompat$BrowserRoot */
    public static final class BrowserRoot {
        public static final String EXTRA_OFFLINE = "android.service.media.extra.OFFLINE";
        public static final String EXTRA_RECENT = "android.service.media.extra.RECENT";
        public static final String EXTRA_SUGGESTED = "android.service.media.extra.SUGGESTED";
        private final Bundle mExtras;
        private final String mRootId;

        public BrowserRoot(@NonNull String str, @Nullable Bundle bundle) {
            if (str != null) {
                this.mRootId = str;
                this.mExtras = bundle;
                return;
            }
            throw new IllegalArgumentException("The root id in BrowserRoot cannot be null. Use null for BrowserRoot instead.");
        }

        public String getRootId() {
            return this.mRootId;
        }

        public Bundle getExtras() {
            return this.mExtras;
        }
    }

    /* renamed from: android.support.v4.media.MediaBrowserServiceCompat$ConnectionRecord */
    private class ConnectionRecord {
        ServiceCallbacks callbacks;
        String pkg;
        BrowserRoot root;
        Bundle rootHints;
        HashMap<String, List<Bundle>> subscriptions;

        private ConnectionRecord() {
            this.subscriptions = new HashMap<>();
        }
    }

    /* renamed from: android.support.v4.media.MediaBrowserServiceCompat$MediaBrowserServiceImpl */
    interface MediaBrowserServiceImpl {
        IBinder onBind(Intent intent);

        void onCreate();
    }

    /* renamed from: android.support.v4.media.MediaBrowserServiceCompat$MediaBrowserServiceImplApi21 */
    class MediaBrowserServiceImplApi21 implements MediaBrowserServiceImpl {
        private Object mServiceObj;

        MediaBrowserServiceImplApi21() {
        }

        public void onCreate() {
            this.mServiceObj = MediaBrowserServiceCompatApi21.createService();
            MediaBrowserServiceCompatApi21.onCreate(this.mServiceObj, new ServiceImplApi21());
        }

        public IBinder onBind(Intent intent) {
            return MediaBrowserServiceCompatApi21.onBind(this.mServiceObj, intent);
        }
    }

    /* renamed from: android.support.v4.media.MediaBrowserServiceCompat$MediaBrowserServiceImplApi23 */
    class MediaBrowserServiceImplApi23 implements MediaBrowserServiceImpl {
        private Object mServiceObj;

        MediaBrowserServiceImplApi23() {
        }

        public void onCreate() {
            this.mServiceObj = MediaBrowserServiceCompatApi23.createService();
            MediaBrowserServiceCompatApi23.onCreate(this.mServiceObj, new ServiceImplApi23());
        }

        public IBinder onBind(Intent intent) {
            return MediaBrowserServiceCompatApi23.onBind(this.mServiceObj, intent);
        }
    }

    /* renamed from: android.support.v4.media.MediaBrowserServiceCompat$MediaBrowserServiceImplBase */
    class MediaBrowserServiceImplBase implements MediaBrowserServiceImpl {
        private Messenger mMessenger;

        MediaBrowserServiceImplBase() {
        }

        public void onCreate() {
            this.mMessenger = new Messenger(MediaBrowserServiceCompat.this.mHandler);
        }

        public IBinder onBind(Intent intent) {
            if (MediaBrowserServiceCompat.SERVICE_INTERFACE.equals(intent.getAction())) {
                return this.mMessenger.getBinder();
            }
            return null;
        }
    }

    /* renamed from: android.support.v4.media.MediaBrowserServiceCompat$Result */
    public static class Result<T> {
        private Object mDebug;
        private boolean mDetachCalled;
        private int mFlags;
        private boolean mSendResultCalled;

        /* access modifiers changed from: 0000 */
        public void onResultSent(T t, int i) {
        }

        Result(Object obj) {
            this.mDebug = obj;
        }

        public void sendResult(T t) {
            if (!this.mSendResultCalled) {
                this.mSendResultCalled = true;
                onResultSent(t, this.mFlags);
                return;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("sendResult() called twice for: ");
            sb.append(this.mDebug);
            throw new IllegalStateException(sb.toString());
        }

        public void detach() {
            if (this.mDetachCalled) {
                StringBuilder sb = new StringBuilder();
                sb.append("detach() called when detach() had already been called for: ");
                sb.append(this.mDebug);
                throw new IllegalStateException(sb.toString());
            } else if (!this.mSendResultCalled) {
                this.mDetachCalled = true;
            } else {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("detach() called when sendResult() had already been called for: ");
                sb2.append(this.mDebug);
                throw new IllegalStateException(sb2.toString());
            }
        }

        /* access modifiers changed from: 0000 */
        public boolean isDone() {
            return this.mDetachCalled || this.mSendResultCalled;
        }

        /* access modifiers changed from: 0000 */
        public void setFlags(int i) {
            this.mFlags = i;
        }
    }

    /* renamed from: android.support.v4.media.MediaBrowserServiceCompat$ServiceCallbacks */
    private interface ServiceCallbacks {
        IBinder asBinder();

        void onConnect(String str, Token token, Bundle bundle) throws RemoteException;

        void onConnectFailed() throws RemoteException;

        void onLoadChildren(String str, List<MediaItem> list, Bundle bundle) throws RemoteException;
    }

    /* renamed from: android.support.v4.media.MediaBrowserServiceCompat$ServiceCallbacksApi21 */
    private class ServiceCallbacksApi21 implements ServiceCallbacks {
        final android.support.p000v4.media.MediaBrowserServiceCompatApi21.ServiceCallbacks mCallbacks;
        Messenger mMessenger;

        ServiceCallbacksApi21(android.support.p000v4.media.MediaBrowserServiceCompatApi21.ServiceCallbacks serviceCallbacks) {
            this.mCallbacks = serviceCallbacks;
        }

        public IBinder asBinder() {
            return this.mCallbacks.asBinder();
        }

        public void onConnect(String str, Token token, Bundle bundle) throws RemoteException {
            if (bundle == null) {
                bundle = new Bundle();
            }
            this.mMessenger = new Messenger(MediaBrowserServiceCompat.this.mHandler);
            BundleCompat.putBinder(bundle, MediaBrowserProtocol.EXTRA_MESSENGER_BINDER, this.mMessenger.getBinder());
            bundle.putInt(MediaBrowserProtocol.EXTRA_SERVICE_VERSION, 1);
            this.mCallbacks.onConnect(str, token.getToken(), bundle);
        }

        public void onConnectFailed() throws RemoteException {
            this.mCallbacks.onConnectFailed();
        }

        public void onLoadChildren(String str, List<MediaItem> list, Bundle bundle) throws RemoteException {
            ArrayList arrayList;
            if (list != null) {
                arrayList = new ArrayList();
                for (MediaItem mediaItem : list) {
                    Parcel obtain = Parcel.obtain();
                    mediaItem.writeToParcel(obtain, 0);
                    arrayList.add(obtain);
                }
            } else {
                arrayList = null;
            }
            this.mCallbacks.onLoadChildren(str, arrayList);
        }
    }

    /* renamed from: android.support.v4.media.MediaBrowserServiceCompat$ServiceCallbacksCompat */
    private class ServiceCallbacksCompat implements ServiceCallbacks {
        final Messenger mCallbacks;

        ServiceCallbacksCompat(Messenger messenger) {
            this.mCallbacks = messenger;
        }

        public IBinder asBinder() {
            return this.mCallbacks.getBinder();
        }

        public void onConnect(String str, Token token, Bundle bundle) throws RemoteException {
            if (bundle == null) {
                bundle = new Bundle();
            }
            bundle.putInt(MediaBrowserProtocol.EXTRA_SERVICE_VERSION, 1);
            Bundle bundle2 = new Bundle();
            bundle2.putString(MediaBrowserProtocol.DATA_MEDIA_ITEM_ID, str);
            bundle2.putParcelable(MediaBrowserProtocol.DATA_MEDIA_SESSION_TOKEN, token);
            bundle2.putBundle(MediaBrowserProtocol.DATA_ROOT_HINTS, bundle);
            sendRequest(1, bundle2);
        }

        public void onConnectFailed() throws RemoteException {
            sendRequest(2, null);
        }

        public void onLoadChildren(String str, List<MediaItem> list, Bundle bundle) throws RemoteException {
            Bundle bundle2 = new Bundle();
            bundle2.putString(MediaBrowserProtocol.DATA_MEDIA_ITEM_ID, str);
            bundle2.putBundle(MediaBrowserProtocol.DATA_OPTIONS, bundle);
            if (list != null) {
                bundle2.putParcelableArrayList(MediaBrowserProtocol.DATA_MEDIA_ITEM_LIST, list instanceof ArrayList ? (ArrayList) list : new ArrayList(list));
            }
            sendRequest(3, bundle2);
        }

        private void sendRequest(int i, Bundle bundle) throws RemoteException {
            Message obtain = Message.obtain();
            obtain.what = i;
            obtain.arg1 = 1;
            obtain.setData(bundle);
            this.mCallbacks.send(obtain);
        }
    }

    /* renamed from: android.support.v4.media.MediaBrowserServiceCompat$ServiceHandler */
    private final class ServiceHandler extends Handler {
        private final ServiceImpl mServiceImpl;

        private ServiceHandler() {
            this.mServiceImpl = new ServiceImpl();
        }

        public void handleMessage(Message message) {
            Bundle data = message.getData();
            int i = message.what;
            String str = MediaBrowserProtocol.DATA_OPTIONS;
            String str2 = MediaBrowserProtocol.DATA_MEDIA_ITEM_ID;
            switch (i) {
                case 1:
                    this.mServiceImpl.connect(data.getString(MediaBrowserProtocol.DATA_PACKAGE_NAME), data.getInt(MediaBrowserProtocol.DATA_CALLING_UID), data.getBundle(MediaBrowserProtocol.DATA_ROOT_HINTS), new ServiceCallbacksCompat(message.replyTo));
                    return;
                case 2:
                    this.mServiceImpl.disconnect(new ServiceCallbacksCompat(message.replyTo));
                    return;
                case 3:
                    this.mServiceImpl.addSubscription(data.getString(str2), data.getBundle(str), new ServiceCallbacksCompat(message.replyTo));
                    return;
                case 4:
                    this.mServiceImpl.removeSubscription(data.getString(str2), data.getBundle(str), new ServiceCallbacksCompat(message.replyTo));
                    return;
                case 5:
                    this.mServiceImpl.getMediaItem(data.getString(str2), (ResultReceiver) data.getParcelable(MediaBrowserProtocol.DATA_RESULT_RECEIVER));
                    return;
                case 6:
                    this.mServiceImpl.registerCallbacks(new ServiceCallbacksCompat(message.replyTo));
                    return;
                case 7:
                    this.mServiceImpl.unregisterCallbacks(new ServiceCallbacksCompat(message.replyTo));
                    return;
                default:
                    StringBuilder sb = new StringBuilder();
                    sb.append("Unhandled message: ");
                    sb.append(message);
                    sb.append("\n  Service version: ");
                    sb.append(1);
                    sb.append("\n  Client version: ");
                    sb.append(message.arg1);
                    Log.w(MediaBrowserServiceCompat.TAG, sb.toString());
                    return;
            }
        }

        public boolean sendMessageAtTime(Message message, long j) {
            Bundle data = message.getData();
            data.setClassLoader(MediaBrowserCompat.class.getClassLoader());
            data.putInt(MediaBrowserProtocol.DATA_CALLING_UID, Binder.getCallingUid());
            return super.sendMessageAtTime(message, j);
        }

        public void postOrRun(Runnable runnable) {
            if (Thread.currentThread() == getLooper().getThread()) {
                runnable.run();
            } else {
                post(runnable);
            }
        }

        public ServiceImpl getServiceImpl() {
            return this.mServiceImpl;
        }
    }

    /* renamed from: android.support.v4.media.MediaBrowserServiceCompat$ServiceImpl */
    private class ServiceImpl {
        private ServiceImpl() {
        }

        public void connect(String str, int i, Bundle bundle, ServiceCallbacks serviceCallbacks) {
            if (MediaBrowserServiceCompat.this.isValidPackage(str, i)) {
                ServiceHandler access$100 = MediaBrowserServiceCompat.this.mHandler;
                final ServiceCallbacks serviceCallbacks2 = serviceCallbacks;
                final String str2 = str;
                final Bundle bundle2 = bundle;
                final int i2 = i;
                C01251 r1 = new Runnable() {
                    public void run() {
                        IBinder asBinder = serviceCallbacks2.asBinder();
                        MediaBrowserServiceCompat.this.mConnections.remove(asBinder);
                        ConnectionRecord connectionRecord = new ConnectionRecord();
                        connectionRecord.pkg = str2;
                        connectionRecord.rootHints = bundle2;
                        connectionRecord.callbacks = serviceCallbacks2;
                        connectionRecord.root = MediaBrowserServiceCompat.this.onGetRoot(str2, i2, bundle2);
                        BrowserRoot browserRoot = connectionRecord.root;
                        String str = MediaBrowserServiceCompat.TAG;
                        if (browserRoot == null) {
                            StringBuilder sb = new StringBuilder();
                            sb.append("No root for client ");
                            sb.append(str2);
                            sb.append(" from service ");
                            sb.append(getClass().getName());
                            Log.i(str, sb.toString());
                            try {
                                serviceCallbacks2.onConnectFailed();
                            } catch (RemoteException unused) {
                                StringBuilder sb2 = new StringBuilder();
                                sb2.append("Calling onConnectFailed() failed. Ignoring. pkg=");
                                sb2.append(str2);
                                Log.w(str, sb2.toString());
                            }
                        } else {
                            try {
                                MediaBrowserServiceCompat.this.mConnections.put(asBinder, connectionRecord);
                                if (MediaBrowserServiceCompat.this.mSession != null) {
                                    serviceCallbacks2.onConnect(connectionRecord.root.getRootId(), MediaBrowserServiceCompat.this.mSession, connectionRecord.root.getExtras());
                                }
                            } catch (RemoteException unused2) {
                                StringBuilder sb3 = new StringBuilder();
                                sb3.append("Calling onConnect() failed. Dropping client. pkg=");
                                sb3.append(str2);
                                Log.w(str, sb3.toString());
                                MediaBrowserServiceCompat.this.mConnections.remove(asBinder);
                            }
                        }
                    }
                };
                access$100.postOrRun(r1);
                return;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("Package/uid mismatch: uid=");
            sb.append(i);
            sb.append(" package=");
            sb.append(str);
            throw new IllegalArgumentException(sb.toString());
        }

        public void disconnect(final ServiceCallbacks serviceCallbacks) {
            MediaBrowserServiceCompat.this.mHandler.postOrRun(new Runnable() {
                public void run() {
                    ConnectionRecord connectionRecord = (ConnectionRecord) MediaBrowserServiceCompat.this.mConnections.remove(serviceCallbacks.asBinder());
                }
            });
        }

        public void addSubscription(final String str, final Bundle bundle, final ServiceCallbacks serviceCallbacks) {
            MediaBrowserServiceCompat.this.mHandler.postOrRun(new Runnable() {
                public void run() {
                    ConnectionRecord connectionRecord = (ConnectionRecord) MediaBrowserServiceCompat.this.mConnections.get(serviceCallbacks.asBinder());
                    if (connectionRecord == null) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("addSubscription for callback that isn't registered id=");
                        sb.append(str);
                        Log.w(MediaBrowserServiceCompat.TAG, sb.toString());
                        return;
                    }
                    MediaBrowserServiceCompat.this.addSubscription(str, connectionRecord, bundle);
                }
            });
        }

        public void removeSubscription(final String str, final Bundle bundle, final ServiceCallbacks serviceCallbacks) {
            MediaBrowserServiceCompat.this.mHandler.postOrRun(new Runnable() {
                public void run() {
                    ConnectionRecord connectionRecord = (ConnectionRecord) MediaBrowserServiceCompat.this.mConnections.get(serviceCallbacks.asBinder());
                    String str = MediaBrowserServiceCompat.TAG;
                    if (connectionRecord == null) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("removeSubscription for callback that isn't registered id=");
                        sb.append(str);
                        Log.w(str, sb.toString());
                        return;
                    }
                    if (!MediaBrowserServiceCompat.this.removeSubscription(str, connectionRecord, bundle)) {
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append("removeSubscription called for ");
                        sb2.append(str);
                        sb2.append(" which is not subscribed");
                        Log.w(str, sb2.toString());
                    }
                }
            });
        }

        public void getMediaItem(final String str, final ResultReceiver resultReceiver) {
            if (!TextUtils.isEmpty(str) && resultReceiver != null) {
                MediaBrowserServiceCompat.this.mHandler.postOrRun(new Runnable() {
                    public void run() {
                        MediaBrowserServiceCompat.this.performLoadItem(str, resultReceiver);
                    }
                });
            }
        }

        public void registerCallbacks(final ServiceCallbacks serviceCallbacks) {
            MediaBrowserServiceCompat.this.mHandler.postOrRun(new Runnable() {
                public void run() {
                    IBinder asBinder = serviceCallbacks.asBinder();
                    MediaBrowserServiceCompat.this.mConnections.remove(asBinder);
                    ConnectionRecord connectionRecord = new ConnectionRecord();
                    connectionRecord.callbacks = serviceCallbacks;
                    MediaBrowserServiceCompat.this.mConnections.put(asBinder, connectionRecord);
                }
            });
        }

        public void unregisterCallbacks(final ServiceCallbacks serviceCallbacks) {
            MediaBrowserServiceCompat.this.mHandler.postOrRun(new Runnable() {
                public void run() {
                    MediaBrowserServiceCompat.this.mConnections.remove(serviceCallbacks.asBinder());
                }
            });
        }
    }

    /* renamed from: android.support.v4.media.MediaBrowserServiceCompat$ServiceImplApi21 */
    private class ServiceImplApi21 implements android.support.p000v4.media.MediaBrowserServiceCompatApi21.ServiceImplApi21 {
        final ServiceImpl mServiceImpl;

        ServiceImplApi21() {
            this.mServiceImpl = MediaBrowserServiceCompat.this.mHandler.getServiceImpl();
        }

        public void connect(String str, Bundle bundle, android.support.p000v4.media.MediaBrowserServiceCompatApi21.ServiceCallbacks serviceCallbacks) {
            this.mServiceImpl.connect(str, Binder.getCallingUid(), bundle, new ServiceCallbacksApi21(serviceCallbacks));
        }

        public void disconnect(android.support.p000v4.media.MediaBrowserServiceCompatApi21.ServiceCallbacks serviceCallbacks) {
            this.mServiceImpl.disconnect(new ServiceCallbacksApi21(serviceCallbacks));
        }

        public void addSubscription(String str, android.support.p000v4.media.MediaBrowserServiceCompatApi21.ServiceCallbacks serviceCallbacks) {
            this.mServiceImpl.addSubscription(str, null, new ServiceCallbacksApi21(serviceCallbacks));
        }

        public void removeSubscription(String str, android.support.p000v4.media.MediaBrowserServiceCompatApi21.ServiceCallbacks serviceCallbacks) {
            this.mServiceImpl.removeSubscription(str, null, new ServiceCallbacksApi21(serviceCallbacks));
        }
    }

    /* renamed from: android.support.v4.media.MediaBrowserServiceCompat$ServiceImplApi23 */
    private class ServiceImplApi23 extends ServiceImplApi21 implements android.support.p000v4.media.MediaBrowserServiceCompatApi23.ServiceImplApi23 {
        private ServiceImplApi23() {
            super();
        }

        public void getMediaItem(String str, final ItemCallback itemCallback) {
            this.mServiceImpl.getMediaItem(str, new ResultReceiver(MediaBrowserServiceCompat.this.mHandler) {
                /* access modifiers changed from: protected */
                public void onReceiveResult(int i, Bundle bundle) {
                    Parcel parcel;
                    MediaItem mediaItem = (MediaItem) bundle.getParcelable(MediaBrowserServiceCompat.KEY_MEDIA_ITEM);
                    if (mediaItem != null) {
                        parcel = Parcel.obtain();
                        mediaItem.writeToParcel(parcel, 0);
                    } else {
                        parcel = null;
                    }
                    itemCallback.onItemLoaded(i, bundle, parcel);
                }
            });
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
    }

    @Nullable
    public abstract BrowserRoot onGetRoot(@NonNull String str, int i, @Nullable Bundle bundle);

    public abstract void onLoadChildren(@NonNull String str, @NonNull Result<List<MediaItem>> result);

    public void onCreate() {
        super.onCreate();
        if (VERSION.SDK_INT >= 23) {
            this.mImpl = new MediaBrowserServiceImplApi23();
        } else if (VERSION.SDK_INT >= 21) {
            this.mImpl = new MediaBrowserServiceImplApi21();
        } else {
            this.mImpl = new MediaBrowserServiceImplBase();
        }
        this.mImpl.onCreate();
    }

    public IBinder onBind(Intent intent) {
        return this.mImpl.onBind(intent);
    }

    public void onLoadChildren(@NonNull String str, @NonNull Result<List<MediaItem>> result, @NonNull Bundle bundle) {
        result.setFlags(1);
        onLoadChildren(str, result);
    }

    public void onLoadItem(String str, Result<MediaItem> result) {
        result.sendResult(null);
    }

    public void setSessionToken(final Token token) {
        if (token == null) {
            throw new IllegalArgumentException("Session token may not be null.");
        } else if (this.mSession == null) {
            this.mSession = token;
            this.mHandler.post(new Runnable() {
                public void run() {
                    for (IBinder iBinder : MediaBrowserServiceCompat.this.mConnections.keySet()) {
                        ConnectionRecord connectionRecord = (ConnectionRecord) MediaBrowserServiceCompat.this.mConnections.get(iBinder);
                        try {
                            connectionRecord.callbacks.onConnect(connectionRecord.root.getRootId(), token, connectionRecord.root.getExtras());
                        } catch (RemoteException unused) {
                            StringBuilder sb = new StringBuilder();
                            sb.append("Connection for ");
                            sb.append(connectionRecord.pkg);
                            sb.append(" is no longer valid.");
                            Log.w(MediaBrowserServiceCompat.TAG, sb.toString());
                            MediaBrowserServiceCompat.this.mConnections.remove(iBinder);
                        }
                    }
                }
            });
        } else {
            throw new IllegalStateException("The session token has already been set.");
        }
    }

    @Nullable
    public Token getSessionToken() {
        return this.mSession;
    }

    public void notifyChildrenChanged(@NonNull String str) {
        notifyChildrenChangedInternal(str, null);
    }

    public void notifyChildrenChanged(@NonNull String str, @NonNull Bundle bundle) {
        if (bundle != null) {
            notifyChildrenChangedInternal(str, bundle);
            return;
        }
        throw new IllegalArgumentException("options cannot be null in notifyChildrenChanged");
    }

    private void notifyChildrenChangedInternal(final String str, final Bundle bundle) {
        if (str != null) {
            this.mHandler.post(new Runnable() {
                public void run() {
                    for (IBinder iBinder : MediaBrowserServiceCompat.this.mConnections.keySet()) {
                        ConnectionRecord connectionRecord = (ConnectionRecord) MediaBrowserServiceCompat.this.mConnections.get(iBinder);
                        List list = (List) connectionRecord.subscriptions.get(str);
                        if (list != null) {
                            Iterator it = list.iterator();
                            while (true) {
                                if (!it.hasNext()) {
                                    break;
                                }
                                Bundle bundle = (Bundle) it.next();
                                if (MediaBrowserCompatUtils.hasDuplicatedItems(bundle, bundle)) {
                                    MediaBrowserServiceCompat.this.performLoadChildren(str, connectionRecord, bundle);
                                    break;
                                }
                            }
                        }
                    }
                }
            });
            return;
        }
        throw new IllegalArgumentException("parentId cannot be null in notifyChildrenChanged");
    }

    /* access modifiers changed from: private */
    public boolean isValidPackage(String str, int i) {
        if (str == null) {
            return false;
        }
        for (String equals : getPackageManager().getPackagesForUid(i)) {
            if (equals.equals(str)) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: private */
    public void addSubscription(String str, ConnectionRecord connectionRecord, Bundle bundle) {
        List<Bundle> list = (List) connectionRecord.subscriptions.get(str);
        if (list == null) {
            list = new ArrayList<>();
        }
        for (Bundle areSameOptions : list) {
            if (MediaBrowserCompatUtils.areSameOptions(bundle, areSameOptions)) {
                return;
            }
        }
        list.add(bundle);
        connectionRecord.subscriptions.put(str, list);
        performLoadChildren(str, connectionRecord, bundle);
    }

    /* access modifiers changed from: private */
    public boolean removeSubscription(String str, ConnectionRecord connectionRecord, Bundle bundle) {
        List list = (List) connectionRecord.subscriptions.get(str);
        boolean z = false;
        if (list != null) {
            Iterator it = list.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                Bundle bundle2 = (Bundle) it.next();
                if (MediaBrowserCompatUtils.areSameOptions(bundle, bundle2)) {
                    list.remove(bundle2);
                    z = true;
                    break;
                }
            }
            if (list.size() == 0) {
                connectionRecord.subscriptions.remove(str);
            }
        }
        return z;
    }

    /* access modifiers changed from: private */
    public void performLoadChildren(String str, ConnectionRecord connectionRecord, Bundle bundle) {
        final ConnectionRecord connectionRecord2 = connectionRecord;
        final String str2 = str;
        final Bundle bundle2 = bundle;
        C01233 r0 = new Result<List<MediaItem>>(str) {
            /* access modifiers changed from: 0000 */
            public void onResultSent(List<MediaItem> list, int i) {
                if (MediaBrowserServiceCompat.this.mConnections.get(connectionRecord2.callbacks.asBinder()) == connectionRecord2) {
                    if ((i & 1) != 0) {
                        list = MediaBrowserCompatUtils.applyOptions(list, bundle2);
                    }
                    try {
                        connectionRecord2.callbacks.onLoadChildren(str2, list, bundle2);
                    } catch (RemoteException unused) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("Calling onLoadChildren() failed for id=");
                        sb.append(str2);
                        sb.append(" package=");
                        sb.append(connectionRecord2.pkg);
                        Log.w(MediaBrowserServiceCompat.TAG, sb.toString());
                    }
                }
            }
        };
        if (bundle == null) {
            onLoadChildren(str, r0);
        } else {
            onLoadChildren(str, r0, bundle);
        }
        if (!r0.isDone()) {
            StringBuilder sb = new StringBuilder();
            sb.append("onLoadChildren must call detach() or sendResult() before returning for package=");
            sb.append(connectionRecord.pkg);
            sb.append(" id=");
            sb.append(str);
            throw new IllegalStateException(sb.toString());
        }
    }

    private List<MediaItem> applyOptions(List<MediaItem> list, Bundle bundle) {
        int i = bundle.getInt(MediaBrowserCompat.EXTRA_PAGE, -1);
        int i2 = bundle.getInt(MediaBrowserCompat.EXTRA_PAGE_SIZE, -1);
        if (i == -1 && i2 == -1) {
            return list;
        }
        int i3 = (i - 1) * i2;
        int i4 = i3 + i2;
        if (i < 1 || i2 < 1 || i3 >= list.size()) {
            return Collections.emptyList();
        }
        if (i4 > list.size()) {
            i4 = list.size();
        }
        return list.subList(i3, i4);
    }

    /* access modifiers changed from: private */
    public void performLoadItem(String str, final ResultReceiver resultReceiver) {
        C01244 r0 = new Result<MediaItem>(str) {
            /* access modifiers changed from: 0000 */
            public void onResultSent(MediaItem mediaItem, int i) {
                Bundle bundle = new Bundle();
                bundle.putParcelable(MediaBrowserServiceCompat.KEY_MEDIA_ITEM, mediaItem);
                resultReceiver.send(0, bundle);
            }
        };
        onLoadItem(str, r0);
        if (!r0.isDone()) {
            StringBuilder sb = new StringBuilder();
            sb.append("onLoadItem must call detach() or sendResult() before returning for id=");
            sb.append(str);
            throw new IllegalStateException(sb.toString());
        }
    }
}
