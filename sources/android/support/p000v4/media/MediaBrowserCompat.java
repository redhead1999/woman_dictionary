package android.support.p000v4.media;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.p000v4.app.BundleCompat;
import android.support.p000v4.media.session.MediaSessionCompat;
import android.support.p000v4.media.session.MediaSessionCompat.Token;
import android.support.p000v4.p002os.ResultReceiver;
import android.support.p000v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

/* renamed from: android.support.v4.media.MediaBrowserCompat */
public final class MediaBrowserCompat {
    public static final String EXTRA_PAGE = "android.media.browse.extra.PAGE";
    public static final String EXTRA_PAGE_SIZE = "android.media.browse.extra.PAGE_SIZE";
    private static final String TAG = "MediaBrowserCompat";
    private final MediaBrowserImpl mImpl;

    /* renamed from: android.support.v4.media.MediaBrowserCompat$CallbackHandler */
    private static class CallbackHandler extends Handler {
        private final MediaBrowserServiceCallbackImpl mCallbackImpl;
        private WeakReference<Messenger> mCallbacksMessengerRef;

        CallbackHandler(MediaBrowserServiceCallbackImpl mediaBrowserServiceCallbackImpl) {
            this.mCallbackImpl = mediaBrowserServiceCallbackImpl;
        }

        public void handleMessage(Message message) {
            if (this.mCallbacksMessengerRef != null) {
                Bundle data = message.getData();
                data.setClassLoader(MediaSessionCompat.class.getClassLoader());
                int i = message.what;
                String str = MediaBrowserProtocol.DATA_MEDIA_ITEM_ID;
                if (i == 1) {
                    this.mCallbackImpl.onServiceConnected((Messenger) this.mCallbacksMessengerRef.get(), data.getString(str), (Token) data.getParcelable(MediaBrowserProtocol.DATA_MEDIA_SESSION_TOKEN), data.getBundle(MediaBrowserProtocol.DATA_ROOT_HINTS));
                } else if (i == 2) {
                    this.mCallbackImpl.onConnectionFailed((Messenger) this.mCallbacksMessengerRef.get());
                } else if (i != 3) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Unhandled message: ");
                    sb.append(message);
                    sb.append("\n  Client version: ");
                    sb.append(1);
                    sb.append("\n  Service version: ");
                    sb.append(message.arg1);
                    Log.w(MediaBrowserCompat.TAG, sb.toString());
                } else {
                    this.mCallbackImpl.onLoadChildren((Messenger) this.mCallbacksMessengerRef.get(), data.getString(str), data.getParcelableArrayList(MediaBrowserProtocol.DATA_MEDIA_ITEM_LIST), data.getBundle(MediaBrowserProtocol.DATA_OPTIONS));
                }
            }
        }

        /* access modifiers changed from: 0000 */
        public void setCallbacksMessenger(Messenger messenger) {
            this.mCallbacksMessengerRef = new WeakReference<>(messenger);
        }
    }

    /* renamed from: android.support.v4.media.MediaBrowserCompat$ConnectionCallback */
    public static class ConnectionCallback {
        /* access modifiers changed from: private */
        public ConnectionCallbackInternal mConnectionCallbackInternal;
        final Object mConnectionCallbackObj;

        /* renamed from: android.support.v4.media.MediaBrowserCompat$ConnectionCallback$ConnectionCallbackInternal */
        interface ConnectionCallbackInternal {
            void onConnected();

            void onConnectionFailed();

            void onConnectionSuspended();
        }

        /* renamed from: android.support.v4.media.MediaBrowserCompat$ConnectionCallback$StubApi21 */
        private class StubApi21 implements ConnectionCallback {
            private StubApi21() {
            }

            public void onConnected() {
                if (ConnectionCallback.this.mConnectionCallbackInternal != null) {
                    ConnectionCallback.this.mConnectionCallbackInternal.onConnected();
                }
                ConnectionCallback.this.onConnected();
            }

            public void onConnectionSuspended() {
                if (ConnectionCallback.this.mConnectionCallbackInternal != null) {
                    ConnectionCallback.this.mConnectionCallbackInternal.onConnectionSuspended();
                }
                ConnectionCallback.this.onConnectionSuspended();
            }

            public void onConnectionFailed() {
                if (ConnectionCallback.this.mConnectionCallbackInternal != null) {
                    ConnectionCallback.this.mConnectionCallbackInternal.onConnectionFailed();
                }
                ConnectionCallback.this.onConnectionFailed();
            }
        }

        public void onConnected() {
        }

        public void onConnectionFailed() {
        }

        public void onConnectionSuspended() {
        }

        public ConnectionCallback() {
            if (VERSION.SDK_INT >= 21) {
                this.mConnectionCallbackObj = MediaBrowserCompatApi21.createConnectionCallback(new StubApi21());
            } else {
                this.mConnectionCallbackObj = null;
            }
        }

        /* access modifiers changed from: 0000 */
        public void setInternalConnectionCallback(ConnectionCallbackInternal connectionCallbackInternal) {
            this.mConnectionCallbackInternal = connectionCallbackInternal;
        }
    }

    /* renamed from: android.support.v4.media.MediaBrowserCompat$ItemCallback */
    public static abstract class ItemCallback {
        final Object mItemCallbackObj;

        /* renamed from: android.support.v4.media.MediaBrowserCompat$ItemCallback$StubApi23 */
        private class StubApi23 implements ItemCallback {
            private StubApi23() {
            }

            public void onItemLoaded(Parcel parcel) {
                parcel.setDataPosition(0);
                MediaItem mediaItem = (MediaItem) MediaItem.CREATOR.createFromParcel(parcel);
                parcel.recycle();
                ItemCallback.this.onItemLoaded(mediaItem);
            }

            public void onError(@NonNull String str) {
                ItemCallback.this.onError(str);
            }
        }

        public void onError(@NonNull String str) {
        }

        public void onItemLoaded(MediaItem mediaItem) {
        }

        public ItemCallback() {
            if (VERSION.SDK_INT >= 23) {
                this.mItemCallbackObj = MediaBrowserCompatApi23.createItemCallback(new StubApi23());
            } else {
                this.mItemCallbackObj = null;
            }
        }
    }

    /* renamed from: android.support.v4.media.MediaBrowserCompat$ItemReceiver */
    private static class ItemReceiver extends ResultReceiver {
        private final ItemCallback mCallback;
        private final String mMediaId;

        ItemReceiver(String str, ItemCallback itemCallback, Handler handler) {
            super(handler);
            this.mMediaId = str;
            this.mCallback = itemCallback;
        }

        /* access modifiers changed from: protected */
        public void onReceiveResult(int i, Bundle bundle) {
            bundle.setClassLoader(MediaBrowserCompat.class.getClassLoader());
            if (i == 0 && bundle != null) {
                String str = MediaBrowserServiceCompat.KEY_MEDIA_ITEM;
                if (bundle.containsKey(str)) {
                    Parcelable parcelable = bundle.getParcelable(str);
                    if (parcelable instanceof MediaItem) {
                        this.mCallback.onItemLoaded((MediaItem) parcelable);
                    } else {
                        this.mCallback.onError(this.mMediaId);
                    }
                    return;
                }
            }
            this.mCallback.onError(this.mMediaId);
        }
    }

    /* renamed from: android.support.v4.media.MediaBrowserCompat$MediaBrowserImpl */
    interface MediaBrowserImpl {
        void connect();

        void disconnect();

        @Nullable
        Bundle getExtras();

        void getItem(@NonNull String str, @NonNull ItemCallback itemCallback);

        @NonNull
        String getRoot();

        ComponentName getServiceComponent();

        @NonNull
        Token getSessionToken();

        boolean isConnected();

        void subscribe(@NonNull String str, Bundle bundle, @NonNull SubscriptionCallback subscriptionCallback);

        void unsubscribe(@NonNull String str, Bundle bundle);
    }

    /* renamed from: android.support.v4.media.MediaBrowserCompat$MediaBrowserImplApi21 */
    static class MediaBrowserImplApi21 implements MediaBrowserImpl, MediaBrowserServiceCallbackImpl, ConnectionCallbackInternal {
        private static final boolean DBG = false;
        protected Object mBrowserObj;
        private Messenger mCallbacksMessenger;
        private final CallbackHandler mHandler = new CallbackHandler(this);
        private ServiceBinderWrapper mServiceBinderWrapper;
        private final ComponentName mServiceComponent;
        private final ArrayMap<String, Subscription> mSubscriptions = new ArrayMap<>();

        public void onConnectionFailed() {
        }

        public void onConnectionFailed(Messenger messenger) {
        }

        public MediaBrowserImplApi21(Context context, ComponentName componentName, ConnectionCallback connectionCallback, Bundle bundle) {
            this.mServiceComponent = componentName;
            connectionCallback.setInternalConnectionCallback(this);
            this.mBrowserObj = MediaBrowserCompatApi21.createBrowser(context, componentName, connectionCallback.mConnectionCallbackObj, bundle);
        }

        public void connect() {
            MediaBrowserCompatApi21.connect(this.mBrowserObj);
        }

        public void disconnect() {
            ServiceBinderWrapper serviceBinderWrapper = this.mServiceBinderWrapper;
            if (serviceBinderWrapper != null) {
                Messenger messenger = this.mCallbacksMessenger;
                if (messenger != null) {
                    try {
                        serviceBinderWrapper.unregisterCallbackMessenger(messenger);
                    } catch (RemoteException unused) {
                        Log.i(MediaBrowserCompat.TAG, "Remote error unregistering client messenger.");
                    }
                }
            }
            MediaBrowserCompatApi21.disconnect(this.mBrowserObj);
        }

        public boolean isConnected() {
            return MediaBrowserCompatApi21.isConnected(this.mBrowserObj);
        }

        public ComponentName getServiceComponent() {
            return MediaBrowserCompatApi21.getServiceComponent(this.mBrowserObj);
        }

        @NonNull
        public String getRoot() {
            return MediaBrowserCompatApi21.getRoot(this.mBrowserObj);
        }

        @Nullable
        public Bundle getExtras() {
            return MediaBrowserCompatApi21.getExtras(this.mBrowserObj);
        }

        @NonNull
        public Token getSessionToken() {
            return Token.fromToken(MediaBrowserCompatApi21.getSessionToken(this.mBrowserObj));
        }

        public void subscribe(@NonNull String str, Bundle bundle, @NonNull SubscriptionCallback subscriptionCallback) {
            SubscriptionCallbackApi21 subscriptionCallbackApi21 = new SubscriptionCallbackApi21(subscriptionCallback, bundle);
            Subscription subscription = (Subscription) this.mSubscriptions.get(str);
            if (subscription == null) {
                subscription = new Subscription();
                this.mSubscriptions.put(str, subscription);
            }
            subscription.setCallbackForOptions(subscriptionCallbackApi21, bundle);
            if (MediaBrowserCompatApi21.isConnected(this.mBrowserObj)) {
                if (bundle != null) {
                    ServiceBinderWrapper serviceBinderWrapper = this.mServiceBinderWrapper;
                    if (serviceBinderWrapper != null) {
                        try {
                            serviceBinderWrapper.addSubscription(str, bundle, this.mCallbacksMessenger);
                            return;
                        } catch (RemoteException unused) {
                            StringBuilder sb = new StringBuilder();
                            sb.append("Remote error subscribing media item: ");
                            sb.append(str);
                            Log.i(MediaBrowserCompat.TAG, sb.toString());
                            return;
                        }
                    }
                }
                MediaBrowserCompatApi21.subscribe(this.mBrowserObj, str, subscriptionCallbackApi21.mSubscriptionCallbackObj);
            }
        }

        public void unsubscribe(@NonNull String str, Bundle bundle) {
            if (!TextUtils.isEmpty(str)) {
                Subscription subscription = (Subscription) this.mSubscriptions.get(str);
                if (subscription != null && subscription.remove(bundle)) {
                    if (bundle != null) {
                        ServiceBinderWrapper serviceBinderWrapper = this.mServiceBinderWrapper;
                        if (serviceBinderWrapper != null) {
                            if (serviceBinderWrapper == null) {
                                try {
                                    serviceBinderWrapper.removeSubscription(str, bundle, this.mCallbacksMessenger);
                                } catch (RemoteException unused) {
                                    StringBuilder sb = new StringBuilder();
                                    sb.append("removeSubscription failed with RemoteException parentId=");
                                    sb.append(str);
                                    Log.d(MediaBrowserCompat.TAG, sb.toString());
                                }
                            }
                        }
                    }
                    if (this.mServiceBinderWrapper != null || subscription.isEmpty()) {
                        MediaBrowserCompatApi21.unsubscribe(this.mBrowserObj, str);
                    }
                }
                if (subscription != null && subscription.isEmpty()) {
                    this.mSubscriptions.remove(str);
                    return;
                }
                return;
            }
            throw new IllegalArgumentException("parentId is empty.");
        }

        public void getItem(@NonNull final String str, @NonNull final ItemCallback itemCallback) {
            if (TextUtils.isEmpty(str)) {
                throw new IllegalArgumentException("mediaId is empty.");
            } else if (itemCallback != null) {
                boolean isConnected = MediaBrowserCompatApi21.isConnected(this.mBrowserObj);
                String str2 = MediaBrowserCompat.TAG;
                if (!isConnected) {
                    Log.i(str2, "Not connected, unable to retrieve the MediaItem.");
                    this.mHandler.post(new Runnable() {
                        public void run() {
                            itemCallback.onError(str);
                        }
                    });
                } else if (this.mServiceBinderWrapper == null) {
                    this.mHandler.post(new Runnable() {
                        public void run() {
                            itemCallback.onItemLoaded(null);
                        }
                    });
                } else {
                    try {
                        this.mServiceBinderWrapper.getMediaItem(str, new ItemReceiver(str, itemCallback, this.mHandler));
                    } catch (RemoteException unused) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("Remote error getting media item: ");
                        sb.append(str);
                        Log.i(str2, sb.toString());
                        this.mHandler.post(new Runnable() {
                            public void run() {
                                itemCallback.onError(str);
                            }
                        });
                    }
                }
            } else {
                throw new IllegalArgumentException("cb is null.");
            }
        }

        public void onConnected() {
            Bundle extras = MediaBrowserCompatApi21.getExtras(this.mBrowserObj);
            if (extras != null) {
                IBinder binder = BundleCompat.getBinder(extras, MediaBrowserProtocol.EXTRA_MESSENGER_BINDER);
                if (binder != null) {
                    this.mServiceBinderWrapper = new ServiceBinderWrapper(binder);
                    this.mCallbacksMessenger = new Messenger(this.mHandler);
                    this.mHandler.setCallbacksMessenger(this.mCallbacksMessenger);
                    try {
                        this.mServiceBinderWrapper.registerCallbackMessenger(this.mCallbacksMessenger);
                    } catch (RemoteException unused) {
                        Log.i(MediaBrowserCompat.TAG, "Remote error registering client messenger.");
                    }
                    onServiceConnected(this.mCallbacksMessenger, null, null, null);
                }
            }
        }

        public void onConnectionSuspended() {
            this.mServiceBinderWrapper = null;
            this.mCallbacksMessenger = null;
            this.mHandler.setCallbacksMessenger(null);
        }

        public void onServiceConnected(Messenger messenger, String str, Token token, Bundle bundle) {
            for (Entry entry : this.mSubscriptions.entrySet()) {
                String str2 = (String) entry.getKey();
                Subscription subscription = (Subscription) entry.getValue();
                List optionsList = subscription.getOptionsList();
                List callbacks = subscription.getCallbacks();
                for (int i = 0; i < optionsList.size(); i++) {
                    if (optionsList.get(i) == null) {
                        MediaBrowserCompatApi21.subscribe(this.mBrowserObj, str2, ((SubscriptionCallbackApi21) callbacks.get(i)).mSubscriptionCallbackObj);
                    } else {
                        try {
                            this.mServiceBinderWrapper.addSubscription(str2, (Bundle) optionsList.get(i), this.mCallbacksMessenger);
                        } catch (RemoteException unused) {
                            StringBuilder sb = new StringBuilder();
                            sb.append("addSubscription failed with RemoteException parentId=");
                            sb.append(str2);
                            Log.d(MediaBrowserCompat.TAG, sb.toString());
                        }
                    }
                }
            }
        }

        public void onLoadChildren(Messenger messenger, String str, List list, @NonNull Bundle bundle) {
            if (this.mCallbacksMessenger == messenger) {
                Subscription subscription = (Subscription) this.mSubscriptions.get(str);
                if (subscription != null) {
                    subscription.getCallback(bundle).onChildrenLoaded(str, list, bundle);
                }
            }
        }
    }

    /* renamed from: android.support.v4.media.MediaBrowserCompat$MediaBrowserImplApi23 */
    static class MediaBrowserImplApi23 extends MediaBrowserImplApi21 {
        public MediaBrowserImplApi23(Context context, ComponentName componentName, ConnectionCallback connectionCallback, Bundle bundle) {
            super(context, componentName, connectionCallback, bundle);
        }

        public void getItem(@NonNull String str, @NonNull ItemCallback itemCallback) {
            MediaBrowserCompatApi23.getItem(this.mBrowserObj, str, itemCallback.mItemCallbackObj);
        }
    }

    /* renamed from: android.support.v4.media.MediaBrowserCompat$MediaBrowserImplBase */
    static class MediaBrowserImplBase implements MediaBrowserImpl, MediaBrowserServiceCallbackImpl {
        private static final int CONNECT_STATE_CONNECTED = 2;
        private static final int CONNECT_STATE_CONNECTING = 1;
        private static final int CONNECT_STATE_DISCONNECTED = 0;
        private static final int CONNECT_STATE_SUSPENDED = 3;
        private static final boolean DBG = false;
        /* access modifiers changed from: private */
        public final ConnectionCallback mCallback;
        /* access modifiers changed from: private */
        public Messenger mCallbacksMessenger;
        /* access modifiers changed from: private */
        public final Context mContext;
        private Bundle mExtras;
        /* access modifiers changed from: private */
        public final CallbackHandler mHandler = new CallbackHandler(this);
        private Token mMediaSessionToken;
        /* access modifiers changed from: private */
        public final Bundle mRootHints;
        private String mRootId;
        /* access modifiers changed from: private */
        public ServiceBinderWrapper mServiceBinderWrapper;
        /* access modifiers changed from: private */
        public final ComponentName mServiceComponent;
        /* access modifiers changed from: private */
        public MediaServiceConnection mServiceConnection;
        /* access modifiers changed from: private */
        public int mState = 0;
        private final ArrayMap<String, Subscription> mSubscriptions = new ArrayMap<>();

        /* renamed from: android.support.v4.media.MediaBrowserCompat$MediaBrowserImplBase$MediaServiceConnection */
        private class MediaServiceConnection implements ServiceConnection {
            private MediaServiceConnection() {
            }

            public void onServiceConnected(final ComponentName componentName, final IBinder iBinder) {
                postOrRun(new Runnable() {
                    public void run() {
                        if (MediaServiceConnection.this.isCurrent("onServiceConnected")) {
                            MediaBrowserImplBase.this.mServiceBinderWrapper = new ServiceBinderWrapper(iBinder);
                            MediaBrowserImplBase.this.mCallbacksMessenger = new Messenger(MediaBrowserImplBase.this.mHandler);
                            MediaBrowserImplBase.this.mHandler.setCallbacksMessenger(MediaBrowserImplBase.this.mCallbacksMessenger);
                            MediaBrowserImplBase.this.mState = 1;
                            try {
                                MediaBrowserImplBase.this.mServiceBinderWrapper.connect(MediaBrowserImplBase.this.mContext, MediaBrowserImplBase.this.mRootHints, MediaBrowserImplBase.this.mCallbacksMessenger);
                            } catch (RemoteException unused) {
                                StringBuilder sb = new StringBuilder();
                                sb.append("RemoteException during connect for ");
                                sb.append(MediaBrowserImplBase.this.mServiceComponent);
                                Log.w(MediaBrowserCompat.TAG, sb.toString());
                            }
                        }
                    }
                });
            }

            public void onServiceDisconnected(final ComponentName componentName) {
                postOrRun(new Runnable() {
                    public void run() {
                        if (MediaServiceConnection.this.isCurrent("onServiceDisconnected")) {
                            MediaBrowserImplBase.this.mServiceBinderWrapper = null;
                            MediaBrowserImplBase.this.mCallbacksMessenger = null;
                            MediaBrowserImplBase.this.mHandler.setCallbacksMessenger(null);
                            MediaBrowserImplBase.this.mState = 3;
                            MediaBrowserImplBase.this.mCallback.onConnectionSuspended();
                        }
                    }
                });
            }

            private void postOrRun(Runnable runnable) {
                if (Thread.currentThread() == MediaBrowserImplBase.this.mHandler.getLooper().getThread()) {
                    runnable.run();
                } else {
                    MediaBrowserImplBase.this.mHandler.post(runnable);
                }
            }

            /* access modifiers changed from: private */
            public boolean isCurrent(String str) {
                if (MediaBrowserImplBase.this.mServiceConnection == this) {
                    return true;
                }
                if (MediaBrowserImplBase.this.mState != 0) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(str);
                    sb.append(" for ");
                    sb.append(MediaBrowserImplBase.this.mServiceComponent);
                    sb.append(" with mServiceConnection=");
                    sb.append(MediaBrowserImplBase.this.mServiceConnection);
                    sb.append(" this=");
                    sb.append(this);
                    Log.i(MediaBrowserCompat.TAG, sb.toString());
                }
                return false;
            }
        }

        public MediaBrowserImplBase(Context context, ComponentName componentName, ConnectionCallback connectionCallback, Bundle bundle) {
            if (context == null) {
                throw new IllegalArgumentException("context must not be null");
            } else if (componentName == null) {
                throw new IllegalArgumentException("service component must not be null");
            } else if (connectionCallback != null) {
                this.mContext = context;
                this.mServiceComponent = componentName;
                this.mCallback = connectionCallback;
                this.mRootHints = bundle;
            } else {
                throw new IllegalArgumentException("connection callback must not be null");
            }
        }

        public void connect() {
            if (this.mState != 0) {
                StringBuilder sb = new StringBuilder();
                sb.append("connect() called while not disconnected (state=");
                sb.append(getStateLabel(this.mState));
                sb.append(")");
                throw new IllegalStateException(sb.toString());
            } else if (this.mServiceBinderWrapper != null) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("mServiceBinderWrapper should be null. Instead it is ");
                sb2.append(this.mServiceBinderWrapper);
                throw new RuntimeException(sb2.toString());
            } else if (this.mCallbacksMessenger == null) {
                this.mState = 1;
                Intent intent = new Intent(MediaBrowserServiceCompat.SERVICE_INTERFACE);
                intent.setComponent(this.mServiceComponent);
                final MediaServiceConnection mediaServiceConnection = new MediaServiceConnection();
                this.mServiceConnection = mediaServiceConnection;
                boolean z = false;
                try {
                    z = this.mContext.bindService(intent, this.mServiceConnection, 1);
                } catch (Exception unused) {
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append("Failed binding to service ");
                    sb3.append(this.mServiceComponent);
                    Log.e(MediaBrowserCompat.TAG, sb3.toString());
                }
                if (!z) {
                    this.mHandler.post(new Runnable() {
                        public void run() {
                            if (mediaServiceConnection == MediaBrowserImplBase.this.mServiceConnection) {
                                MediaBrowserImplBase.this.forceCloseConnection();
                                MediaBrowserImplBase.this.mCallback.onConnectionFailed();
                            }
                        }
                    });
                }
            } else {
                StringBuilder sb4 = new StringBuilder();
                sb4.append("mCallbacksMessenger should be null. Instead it is ");
                sb4.append(this.mCallbacksMessenger);
                throw new RuntimeException(sb4.toString());
            }
        }

        public void disconnect() {
            Messenger messenger = this.mCallbacksMessenger;
            if (messenger != null) {
                try {
                    this.mServiceBinderWrapper.disconnect(messenger);
                } catch (RemoteException unused) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("RemoteException during connect for ");
                    sb.append(this.mServiceComponent);
                    Log.w(MediaBrowserCompat.TAG, sb.toString());
                }
            }
            forceCloseConnection();
        }

        /* access modifiers changed from: private */
        public void forceCloseConnection() {
            MediaServiceConnection mediaServiceConnection = this.mServiceConnection;
            if (mediaServiceConnection != null) {
                this.mContext.unbindService(mediaServiceConnection);
            }
            this.mState = 0;
            this.mServiceConnection = null;
            this.mServiceBinderWrapper = null;
            this.mCallbacksMessenger = null;
            this.mHandler.setCallbacksMessenger(null);
            this.mRootId = null;
            this.mMediaSessionToken = null;
        }

        public boolean isConnected() {
            return this.mState == 2;
        }

        @NonNull
        public ComponentName getServiceComponent() {
            if (isConnected()) {
                return this.mServiceComponent;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("getServiceComponent() called while not connected (state=");
            sb.append(this.mState);
            sb.append(")");
            throw new IllegalStateException(sb.toString());
        }

        @NonNull
        public String getRoot() {
            if (isConnected()) {
                return this.mRootId;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("getRoot() called while not connected(state=");
            sb.append(getStateLabel(this.mState));
            sb.append(")");
            throw new IllegalStateException(sb.toString());
        }

        @Nullable
        public Bundle getExtras() {
            if (isConnected()) {
                return this.mExtras;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("getExtras() called while not connected (state=");
            sb.append(getStateLabel(this.mState));
            sb.append(")");
            throw new IllegalStateException(sb.toString());
        }

        @NonNull
        public Token getSessionToken() {
            if (isConnected()) {
                return this.mMediaSessionToken;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("getSessionToken() called while not connected(state=");
            sb.append(this.mState);
            sb.append(")");
            throw new IllegalStateException(sb.toString());
        }

        public void subscribe(@NonNull String str, Bundle bundle, @NonNull SubscriptionCallback subscriptionCallback) {
            if (TextUtils.isEmpty(str)) {
                throw new IllegalArgumentException("parentId is empty.");
            } else if (subscriptionCallback != null) {
                Subscription subscription = (Subscription) this.mSubscriptions.get(str);
                if (subscription == null) {
                    subscription = new Subscription();
                    this.mSubscriptions.put(str, subscription);
                }
                subscription.setCallbackForOptions(subscriptionCallback, bundle);
                if (this.mState == 2) {
                    try {
                        this.mServiceBinderWrapper.addSubscription(str, bundle, this.mCallbacksMessenger);
                    } catch (RemoteException unused) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("addSubscription failed with RemoteException parentId=");
                        sb.append(str);
                        Log.d(MediaBrowserCompat.TAG, sb.toString());
                    }
                }
            } else {
                throw new IllegalArgumentException("callback is null");
            }
        }

        public void unsubscribe(@NonNull String str, Bundle bundle) {
            if (!TextUtils.isEmpty(str)) {
                Subscription subscription = (Subscription) this.mSubscriptions.get(str);
                if (subscription != null && subscription.remove(bundle) && this.mState == 2) {
                    try {
                        this.mServiceBinderWrapper.removeSubscription(str, bundle, this.mCallbacksMessenger);
                    } catch (RemoteException unused) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("removeSubscription failed with RemoteException parentId=");
                        sb.append(str);
                        Log.d(MediaBrowserCompat.TAG, sb.toString());
                    }
                }
                if (subscription != null && subscription.isEmpty()) {
                    this.mSubscriptions.remove(str);
                    return;
                }
                return;
            }
            throw new IllegalArgumentException("parentId is empty.");
        }

        public void getItem(@NonNull final String str, @NonNull final ItemCallback itemCallback) {
            if (TextUtils.isEmpty(str)) {
                throw new IllegalArgumentException("mediaId is empty.");
            } else if (itemCallback != null) {
                int i = this.mState;
                String str2 = MediaBrowserCompat.TAG;
                if (i != 2) {
                    Log.i(str2, "Not connected, unable to retrieve the MediaItem.");
                    this.mHandler.post(new Runnable() {
                        public void run() {
                            itemCallback.onError(str);
                        }
                    });
                    return;
                }
                try {
                    this.mServiceBinderWrapper.getMediaItem(str, new ItemReceiver(str, itemCallback, this.mHandler));
                } catch (RemoteException unused) {
                    Log.i(str2, "Remote error getting media item.");
                    this.mHandler.post(new Runnable() {
                        public void run() {
                            itemCallback.onError(str);
                        }
                    });
                }
            } else {
                throw new IllegalArgumentException("cb is null.");
            }
        }

        public void onServiceConnected(Messenger messenger, String str, Token token, Bundle bundle) {
            if (isCurrent(messenger, "onConnect")) {
                int i = this.mState;
                String str2 = MediaBrowserCompat.TAG;
                if (i != 1) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("onConnect from service while mState=");
                    sb.append(getStateLabel(this.mState));
                    sb.append("... ignoring");
                    Log.w(str2, sb.toString());
                    return;
                }
                this.mRootId = str;
                this.mMediaSessionToken = token;
                this.mExtras = bundle;
                this.mState = 2;
                this.mCallback.onConnected();
                try {
                    for (Entry entry : this.mSubscriptions.entrySet()) {
                        String str3 = (String) entry.getKey();
                        for (Bundle addSubscription : ((Subscription) entry.getValue()).getOptionsList()) {
                            this.mServiceBinderWrapper.addSubscription(str3, addSubscription, this.mCallbacksMessenger);
                        }
                    }
                } catch (RemoteException unused) {
                    Log.d(str2, "addSubscription failed with RemoteException.");
                }
            }
        }

        public void onConnectionFailed(Messenger messenger) {
            StringBuilder sb = new StringBuilder();
            sb.append("onConnectFailed for ");
            sb.append(this.mServiceComponent);
            String sb2 = sb.toString();
            String str = MediaBrowserCompat.TAG;
            Log.e(str, sb2);
            if (isCurrent(messenger, "onConnectFailed")) {
                if (this.mState != 1) {
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append("onConnect from service while mState=");
                    sb3.append(getStateLabel(this.mState));
                    sb3.append("... ignoring");
                    Log.w(str, sb3.toString());
                    return;
                }
                forceCloseConnection();
                this.mCallback.onConnectionFailed();
            }
        }

        public void onLoadChildren(Messenger messenger, String str, List list, Bundle bundle) {
            if (isCurrent(messenger, "onLoadChildren")) {
                Subscription subscription = (Subscription) this.mSubscriptions.get(str);
                if (subscription != null) {
                    SubscriptionCallback callback = subscription.getCallback(bundle);
                    if (callback != null) {
                        if (bundle == null) {
                            callback.onChildrenLoaded(str, list);
                        } else {
                            callback.onChildrenLoaded(str, list, bundle);
                        }
                    }
                }
            }
        }

        private static String getStateLabel(int i) {
            if (i == 0) {
                return "CONNECT_STATE_DISCONNECTED";
            }
            if (i == 1) {
                return "CONNECT_STATE_CONNECTING";
            }
            if (i == 2) {
                return "CONNECT_STATE_CONNECTED";
            }
            if (i == 3) {
                return "CONNECT_STATE_SUSPENDED";
            }
            StringBuilder sb = new StringBuilder();
            sb.append("UNKNOWN/");
            sb.append(i);
            return sb.toString();
        }

        private boolean isCurrent(Messenger messenger, String str) {
            if (this.mCallbacksMessenger == messenger) {
                return true;
            }
            if (this.mState != 0) {
                StringBuilder sb = new StringBuilder();
                sb.append(str);
                sb.append(" for ");
                sb.append(this.mServiceComponent);
                sb.append(" with mCallbacksMessenger=");
                sb.append(this.mCallbacksMessenger);
                sb.append(" this=");
                sb.append(this);
                Log.i(MediaBrowserCompat.TAG, sb.toString());
            }
            return false;
        }

        /* access modifiers changed from: 0000 */
        public void dump() {
            String str = MediaBrowserCompat.TAG;
            Log.d(str, "MediaBrowserCompat...");
            StringBuilder sb = new StringBuilder();
            sb.append("  mServiceComponent=");
            sb.append(this.mServiceComponent);
            Log.d(str, sb.toString());
            StringBuilder sb2 = new StringBuilder();
            sb2.append("  mCallback=");
            sb2.append(this.mCallback);
            Log.d(str, sb2.toString());
            StringBuilder sb3 = new StringBuilder();
            sb3.append("  mRootHints=");
            sb3.append(this.mRootHints);
            Log.d(str, sb3.toString());
            StringBuilder sb4 = new StringBuilder();
            sb4.append("  mState=");
            sb4.append(getStateLabel(this.mState));
            Log.d(str, sb4.toString());
            StringBuilder sb5 = new StringBuilder();
            sb5.append("  mServiceConnection=");
            sb5.append(this.mServiceConnection);
            Log.d(str, sb5.toString());
            StringBuilder sb6 = new StringBuilder();
            sb6.append("  mServiceBinderWrapper=");
            sb6.append(this.mServiceBinderWrapper);
            Log.d(str, sb6.toString());
            StringBuilder sb7 = new StringBuilder();
            sb7.append("  mCallbacksMessenger=");
            sb7.append(this.mCallbacksMessenger);
            Log.d(str, sb7.toString());
            StringBuilder sb8 = new StringBuilder();
            sb8.append("  mRootId=");
            sb8.append(this.mRootId);
            Log.d(str, sb8.toString());
            StringBuilder sb9 = new StringBuilder();
            sb9.append("  mMediaSessionToken=");
            sb9.append(this.mMediaSessionToken);
            Log.d(str, sb9.toString());
        }
    }

    /* renamed from: android.support.v4.media.MediaBrowserCompat$MediaBrowserServiceCallbackImpl */
    interface MediaBrowserServiceCallbackImpl {
        void onConnectionFailed(Messenger messenger);

        void onLoadChildren(Messenger messenger, String str, List list, Bundle bundle);

        void onServiceConnected(Messenger messenger, String str, Token token, Bundle bundle);
    }

    /* renamed from: android.support.v4.media.MediaBrowserCompat$MediaItem */
    public static class MediaItem implements Parcelable {
        public static final Creator<MediaItem> CREATOR = new Creator<MediaItem>() {
            public MediaItem createFromParcel(Parcel parcel) {
                return new MediaItem(parcel);
            }

            public MediaItem[] newArray(int i) {
                return new MediaItem[i];
            }
        };
        public static final int FLAG_BROWSABLE = 1;
        public static final int FLAG_PLAYABLE = 2;
        private final MediaDescriptionCompat mDescription;
        private final int mFlags;

        @Retention(RetentionPolicy.SOURCE)
        /* renamed from: android.support.v4.media.MediaBrowserCompat$MediaItem$Flags */
        public @interface Flags {
        }

        public int describeContents() {
            return 0;
        }

        public MediaItem(@NonNull MediaDescriptionCompat mediaDescriptionCompat, int i) {
            if (mediaDescriptionCompat == null) {
                throw new IllegalArgumentException("description cannot be null");
            } else if (!TextUtils.isEmpty(mediaDescriptionCompat.getMediaId())) {
                this.mFlags = i;
                this.mDescription = mediaDescriptionCompat;
            } else {
                throw new IllegalArgumentException("description must have a non-empty media id");
            }
        }

        private MediaItem(Parcel parcel) {
            this.mFlags = parcel.readInt();
            this.mDescription = (MediaDescriptionCompat) MediaDescriptionCompat.CREATOR.createFromParcel(parcel);
        }

        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(this.mFlags);
            this.mDescription.writeToParcel(parcel, i);
        }

        public String toString() {
            StringBuilder sb = new StringBuilder("MediaItem{");
            sb.append("mFlags=");
            sb.append(this.mFlags);
            sb.append(", mDescription=");
            sb.append(this.mDescription);
            sb.append('}');
            return sb.toString();
        }

        public int getFlags() {
            return this.mFlags;
        }

        public boolean isBrowsable() {
            return (this.mFlags & 1) != 0;
        }

        public boolean isPlayable() {
            return (this.mFlags & 2) != 0;
        }

        @NonNull
        public MediaDescriptionCompat getDescription() {
            return this.mDescription;
        }

        @NonNull
        public String getMediaId() {
            return this.mDescription.getMediaId();
        }
    }

    /* renamed from: android.support.v4.media.MediaBrowserCompat$ServiceBinderWrapper */
    private static class ServiceBinderWrapper {
        private Messenger mMessenger;

        public ServiceBinderWrapper(IBinder iBinder) {
            this.mMessenger = new Messenger(iBinder);
        }

        /* access modifiers changed from: 0000 */
        public void connect(Context context, Bundle bundle, Messenger messenger) throws RemoteException {
            Bundle bundle2 = new Bundle();
            bundle2.putString(MediaBrowserProtocol.DATA_PACKAGE_NAME, context.getPackageName());
            bundle2.putBundle(MediaBrowserProtocol.DATA_ROOT_HINTS, bundle);
            sendRequest(1, bundle2, messenger);
        }

        /* access modifiers changed from: 0000 */
        public void disconnect(Messenger messenger) throws RemoteException {
            sendRequest(2, null, messenger);
        }

        /* access modifiers changed from: 0000 */
        public void addSubscription(String str, Bundle bundle, Messenger messenger) throws RemoteException {
            Bundle bundle2 = new Bundle();
            bundle2.putString(MediaBrowserProtocol.DATA_MEDIA_ITEM_ID, str);
            bundle2.putBundle(MediaBrowserProtocol.DATA_OPTIONS, bundle);
            sendRequest(3, bundle2, messenger);
        }

        /* access modifiers changed from: 0000 */
        public void removeSubscription(String str, Bundle bundle, Messenger messenger) throws RemoteException {
            Bundle bundle2 = new Bundle();
            bundle2.putString(MediaBrowserProtocol.DATA_MEDIA_ITEM_ID, str);
            bundle2.putBundle(MediaBrowserProtocol.DATA_OPTIONS, bundle);
            sendRequest(4, bundle2, messenger);
        }

        /* access modifiers changed from: 0000 */
        public void getMediaItem(String str, ResultReceiver resultReceiver) throws RemoteException {
            Bundle bundle = new Bundle();
            bundle.putString(MediaBrowserProtocol.DATA_MEDIA_ITEM_ID, str);
            bundle.putParcelable(MediaBrowserProtocol.DATA_RESULT_RECEIVER, resultReceiver);
            sendRequest(5, bundle, null);
        }

        /* access modifiers changed from: 0000 */
        public void registerCallbackMessenger(Messenger messenger) throws RemoteException {
            sendRequest(6, null, messenger);
        }

        /* access modifiers changed from: 0000 */
        public void unregisterCallbackMessenger(Messenger messenger) throws RemoteException {
            sendRequest(7, null, messenger);
        }

        private void sendRequest(int i, Bundle bundle, Messenger messenger) throws RemoteException {
            Message obtain = Message.obtain();
            obtain.what = i;
            obtain.arg1 = 1;
            obtain.setData(bundle);
            obtain.replyTo = messenger;
            this.mMessenger.send(obtain);
        }
    }

    /* renamed from: android.support.v4.media.MediaBrowserCompat$Subscription */
    private static class Subscription {
        private final List<SubscriptionCallback> mCallbacks = new ArrayList();
        private final List<Bundle> mOptionsList = new ArrayList();

        public boolean isEmpty() {
            return this.mCallbacks.isEmpty();
        }

        public List<Bundle> getOptionsList() {
            return this.mOptionsList;
        }

        public List<SubscriptionCallback> getCallbacks() {
            return this.mCallbacks;
        }

        public void setCallbackForOptions(SubscriptionCallback subscriptionCallback, Bundle bundle) {
            for (int i = 0; i < this.mOptionsList.size(); i++) {
                if (MediaBrowserCompatUtils.areSameOptions((Bundle) this.mOptionsList.get(i), bundle)) {
                    this.mCallbacks.set(i, subscriptionCallback);
                    return;
                }
            }
            this.mCallbacks.add(subscriptionCallback);
            this.mOptionsList.add(bundle);
        }

        public boolean remove(Bundle bundle) {
            for (int i = 0; i < this.mOptionsList.size(); i++) {
                if (MediaBrowserCompatUtils.areSameOptions((Bundle) this.mOptionsList.get(i), bundle)) {
                    this.mCallbacks.remove(i);
                    this.mOptionsList.remove(i);
                    return true;
                }
            }
            return false;
        }

        public SubscriptionCallback getCallback(Bundle bundle) {
            for (int i = 0; i < this.mOptionsList.size(); i++) {
                if (MediaBrowserCompatUtils.areSameOptions((Bundle) this.mOptionsList.get(i), bundle)) {
                    return (SubscriptionCallback) this.mCallbacks.get(i);
                }
            }
            return null;
        }
    }

    /* renamed from: android.support.v4.media.MediaBrowserCompat$SubscriptionCallback */
    public static abstract class SubscriptionCallback {
        public void onChildrenLoaded(@NonNull String str, List<MediaItem> list) {
        }

        public void onChildrenLoaded(@NonNull String str, List<MediaItem> list, @NonNull Bundle bundle) {
        }

        public void onError(@NonNull String str) {
        }

        public void onError(@NonNull String str, @NonNull Bundle bundle) {
        }
    }

    /* renamed from: android.support.v4.media.MediaBrowserCompat$SubscriptionCallbackApi21 */
    static class SubscriptionCallbackApi21 extends SubscriptionCallback {
        /* access modifiers changed from: private */
        public Bundle mOptions;
        SubscriptionCallback mSubscriptionCallback;
        /* access modifiers changed from: private */
        public final Object mSubscriptionCallbackObj = MediaBrowserCompatApi21.createSubscriptionCallback(new StubApi21());

        /* renamed from: android.support.v4.media.MediaBrowserCompat$SubscriptionCallbackApi21$StubApi21 */
        private class StubApi21 implements SubscriptionCallback {
            private StubApi21() {
            }

            public void onChildrenLoaded(@NonNull String str, List<Parcel> list) {
                ArrayList arrayList;
                if (list != null) {
                    arrayList = new ArrayList();
                    for (Parcel parcel : list) {
                        parcel.setDataPosition(0);
                        arrayList.add(MediaItem.CREATOR.createFromParcel(parcel));
                        parcel.recycle();
                    }
                } else {
                    arrayList = null;
                }
                if (SubscriptionCallbackApi21.this.mOptions != null) {
                    SubscriptionCallbackApi21 subscriptionCallbackApi21 = SubscriptionCallbackApi21.this;
                    subscriptionCallbackApi21.onChildrenLoaded(str, MediaBrowserCompatUtils.applyOptions(arrayList, subscriptionCallbackApi21.mOptions), SubscriptionCallbackApi21.this.mOptions);
                    return;
                }
                SubscriptionCallbackApi21.this.onChildrenLoaded(str, arrayList);
            }

            public void onError(@NonNull String str) {
                if (SubscriptionCallbackApi21.this.mOptions != null) {
                    SubscriptionCallbackApi21 subscriptionCallbackApi21 = SubscriptionCallbackApi21.this;
                    subscriptionCallbackApi21.onError(str, subscriptionCallbackApi21.mOptions);
                    return;
                }
                SubscriptionCallbackApi21.this.onError(str);
            }
        }

        public SubscriptionCallbackApi21(SubscriptionCallback subscriptionCallback, Bundle bundle) {
            this.mSubscriptionCallback = subscriptionCallback;
            this.mOptions = bundle;
        }

        public void onChildrenLoaded(@NonNull String str, List<MediaItem> list) {
            this.mSubscriptionCallback.onChildrenLoaded(str, list);
        }

        public void onChildrenLoaded(@NonNull String str, List<MediaItem> list, @NonNull Bundle bundle) {
            this.mSubscriptionCallback.onChildrenLoaded(str, list, bundle);
        }

        public void onError(@NonNull String str) {
            this.mSubscriptionCallback.onError(str);
        }

        public void onError(@NonNull String str, @NonNull Bundle bundle) {
            this.mSubscriptionCallback.onError(str, bundle);
        }
    }

    public MediaBrowserCompat(Context context, ComponentName componentName, ConnectionCallback connectionCallback, Bundle bundle) {
        if (VERSION.SDK_INT >= 23) {
            this.mImpl = new MediaBrowserImplApi23(context, componentName, connectionCallback, bundle);
        } else if (VERSION.SDK_INT >= 21) {
            this.mImpl = new MediaBrowserImplApi21(context, componentName, connectionCallback, bundle);
        } else {
            this.mImpl = new MediaBrowserImplBase(context, componentName, connectionCallback, bundle);
        }
    }

    public void connect() {
        this.mImpl.connect();
    }

    public void disconnect() {
        this.mImpl.disconnect();
    }

    public boolean isConnected() {
        return this.mImpl.isConnected();
    }

    @NonNull
    public ComponentName getServiceComponent() {
        return this.mImpl.getServiceComponent();
    }

    @NonNull
    public String getRoot() {
        return this.mImpl.getRoot();
    }

    @Nullable
    public Bundle getExtras() {
        return this.mImpl.getExtras();
    }

    @NonNull
    public Token getSessionToken() {
        return this.mImpl.getSessionToken();
    }

    public void subscribe(@NonNull String str, @NonNull SubscriptionCallback subscriptionCallback) {
        this.mImpl.subscribe(str, null, subscriptionCallback);
    }

    public void subscribe(@NonNull String str, @NonNull Bundle bundle, @NonNull SubscriptionCallback subscriptionCallback) {
        if (bundle != null) {
            this.mImpl.subscribe(str, bundle, subscriptionCallback);
            return;
        }
        throw new IllegalArgumentException("options are null");
    }

    public void unsubscribe(@NonNull String str) {
        this.mImpl.unsubscribe(str, null);
    }

    public void unsubscribe(@NonNull String str, @NonNull Bundle bundle) {
        if (bundle != null) {
            this.mImpl.unsubscribe(str, bundle);
            return;
        }
        throw new IllegalArgumentException("options are null");
    }

    public void getItem(@NonNull String str, @NonNull ItemCallback itemCallback) {
        this.mImpl.getItem(str, itemCallback);
    }
}
