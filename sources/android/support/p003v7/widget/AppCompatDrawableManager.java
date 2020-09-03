package android.support.p003v7.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources.Theme;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.ConstantState;
import android.graphics.drawable.LayerDrawable;
import android.os.Build.VERSION;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.p000v4.content.ContextCompat;
import android.support.p000v4.graphics.ColorUtils;
import android.support.p000v4.graphics.drawable.DrawableCompat;
import android.support.p000v4.util.ArrayMap;
import android.support.p000v4.util.LongSparseArray;
import android.support.p000v4.util.LruCache;
import android.support.p003v7.appcompat.C0254R;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import java.lang.ref.WeakReference;
import java.util.WeakHashMap;
import org.xmlpull.v1.XmlPullParser;

/* renamed from: android.support.v7.widget.AppCompatDrawableManager */
public final class AppCompatDrawableManager {
    private static final int[] COLORFILTER_COLOR_BACKGROUND_MULTIPLY = {C0254R.C0255drawable.abc_popup_background_mtrl_mult, C0254R.C0255drawable.abc_cab_background_internal_bg, C0254R.C0255drawable.abc_menu_hardkey_panel_mtrl_mult};
    private static final int[] COLORFILTER_COLOR_CONTROL_ACTIVATED = {C0254R.C0255drawable.abc_textfield_activated_mtrl_alpha, C0254R.C0255drawable.abc_textfield_search_activated_mtrl_alpha, C0254R.C0255drawable.abc_cab_background_top_mtrl_alpha, C0254R.C0255drawable.abc_text_cursor_material};
    private static final int[] COLORFILTER_TINT_COLOR_CONTROL_NORMAL = {C0254R.C0255drawable.abc_textfield_search_default_mtrl_alpha, C0254R.C0255drawable.abc_textfield_default_mtrl_alpha, C0254R.C0255drawable.abc_ab_share_pack_mtrl_alpha};
    private static final ColorFilterLruCache COLOR_FILTER_CACHE = new ColorFilterLruCache(6);
    private static final boolean DEBUG = false;
    private static final Mode DEFAULT_MODE = Mode.SRC_IN;
    private static AppCompatDrawableManager INSTANCE = null;
    private static final String PLATFORM_VD_CLAZZ = "android.graphics.drawable.VectorDrawable";
    private static final String SKIP_DRAWABLE_TAG = "appcompat_skip_skip";
    private static final String TAG = "AppCompatDrawableManager";
    private static final int[] TINT_CHECKABLE_BUTTON_LIST = {C0254R.C0255drawable.abc_btn_check_material, C0254R.C0255drawable.abc_btn_radio_material};
    private static final int[] TINT_COLOR_CONTROL_NORMAL = {C0254R.C0255drawable.abc_ic_ab_back_mtrl_am_alpha, C0254R.C0255drawable.abc_ic_go_search_api_mtrl_alpha, C0254R.C0255drawable.abc_ic_search_api_mtrl_alpha, C0254R.C0255drawable.abc_ic_commit_search_api_mtrl_alpha, C0254R.C0255drawable.abc_ic_clear_mtrl_alpha, C0254R.C0255drawable.abc_ic_menu_share_mtrl_alpha, C0254R.C0255drawable.abc_ic_menu_copy_mtrl_am_alpha, C0254R.C0255drawable.abc_ic_menu_cut_mtrl_alpha, C0254R.C0255drawable.abc_ic_menu_selectall_mtrl_alpha, C0254R.C0255drawable.abc_ic_menu_paste_mtrl_am_alpha, C0254R.C0255drawable.abc_ic_menu_moreoverflow_mtrl_alpha, C0254R.C0255drawable.abc_ic_voice_search_api_mtrl_alpha};
    private static final int[] TINT_COLOR_CONTROL_STATE_LIST = {C0254R.C0255drawable.abc_edit_text_material, C0254R.C0255drawable.abc_tab_indicator_material, C0254R.C0255drawable.abc_textfield_search_material, C0254R.C0255drawable.abc_spinner_mtrl_am_alpha, C0254R.C0255drawable.abc_spinner_textfield_background_material, C0254R.C0255drawable.abc_ratingbar_full_material, C0254R.C0255drawable.abc_switch_track_mtrl_alpha, C0254R.C0255drawable.abc_switch_thumb_material, C0254R.C0255drawable.abc_btn_default_mtrl_shape, C0254R.C0255drawable.abc_btn_borderless_material};
    private ArrayMap<String, InflateDelegate> mDelegates;
    private final Object mDrawableCacheLock = new Object();
    private final WeakHashMap<Context, LongSparseArray<WeakReference<ConstantState>>> mDrawableCaches = new WeakHashMap<>(0);
    private boolean mHasCheckedVectorDrawableSetup;
    private SparseArray<String> mKnownDrawableIdTags;
    private WeakHashMap<Context, SparseArray<ColorStateList>> mTintLists;
    private TypedValue mTypedValue;

    /* renamed from: android.support.v7.widget.AppCompatDrawableManager$AvdcInflateDelegate */
    private static class AvdcInflateDelegate implements InflateDelegate {
        private AvdcInflateDelegate() {
        }

        public Drawable createFromXmlInner(@NonNull Context context, @NonNull XmlPullParser xmlPullParser, @NonNull AttributeSet attributeSet, @Nullable Theme theme) {
            try {
                return AnimatedVectorDrawableCompat.createFromXmlInner(context, context.getResources(), xmlPullParser, attributeSet, theme);
            } catch (Exception e) {
                Log.e("AvdcInflateDelegate", "Exception while inflating <animated-vector>", e);
                return null;
            }
        }
    }

    /* renamed from: android.support.v7.widget.AppCompatDrawableManager$ColorFilterLruCache */
    private static class ColorFilterLruCache extends LruCache<Integer, PorterDuffColorFilter> {
        public ColorFilterLruCache(int i) {
            super(i);
        }

        /* access modifiers changed from: 0000 */
        public PorterDuffColorFilter get(int i, Mode mode) {
            return (PorterDuffColorFilter) get(Integer.valueOf(generateCacheKey(i, mode)));
        }

        /* access modifiers changed from: 0000 */
        public PorterDuffColorFilter put(int i, Mode mode, PorterDuffColorFilter porterDuffColorFilter) {
            return (PorterDuffColorFilter) put(Integer.valueOf(generateCacheKey(i, mode)), porterDuffColorFilter);
        }

        private static int generateCacheKey(int i, Mode mode) {
            return ((i + 31) * 31) + mode.hashCode();
        }
    }

    /* renamed from: android.support.v7.widget.AppCompatDrawableManager$InflateDelegate */
    private interface InflateDelegate {
        Drawable createFromXmlInner(@NonNull Context context, @NonNull XmlPullParser xmlPullParser, @NonNull AttributeSet attributeSet, @Nullable Theme theme);
    }

    /* renamed from: android.support.v7.widget.AppCompatDrawableManager$VdcInflateDelegate */
    private static class VdcInflateDelegate implements InflateDelegate {
        private VdcInflateDelegate() {
        }

        public Drawable createFromXmlInner(@NonNull Context context, @NonNull XmlPullParser xmlPullParser, @NonNull AttributeSet attributeSet, @Nullable Theme theme) {
            try {
                return VectorDrawableCompat.createFromXmlInner(context.getResources(), xmlPullParser, attributeSet, theme);
            } catch (Exception e) {
                Log.e("VdcInflateDelegate", "Exception while inflating <vector>", e);
                return null;
            }
        }
    }

    public static AppCompatDrawableManager get() {
        if (INSTANCE == null) {
            INSTANCE = new AppCompatDrawableManager();
            installDefaultInflateDelegates(INSTANCE);
        }
        return INSTANCE;
    }

    private static void installDefaultInflateDelegates(@NonNull AppCompatDrawableManager appCompatDrawableManager) {
        int i = VERSION.SDK_INT;
        if (i < 23) {
            appCompatDrawableManager.addDelegate("vector", new VdcInflateDelegate());
            if (i >= 11) {
                appCompatDrawableManager.addDelegate("animated-vector", new AvdcInflateDelegate());
            }
        }
    }

    public Drawable getDrawable(@NonNull Context context, @DrawableRes int i) {
        return getDrawable(context, i, false);
    }

    public Drawable getDrawable(@NonNull Context context, @DrawableRes int i, boolean z) {
        Drawable loadDrawableFromDelegates = loadDrawableFromDelegates(context, i);
        if (loadDrawableFromDelegates == null) {
            loadDrawableFromDelegates = createDrawableIfNeeded(context, i);
        }
        if (loadDrawableFromDelegates == null) {
            loadDrawableFromDelegates = ContextCompat.getDrawable(context, i);
        }
        if (loadDrawableFromDelegates != null) {
            loadDrawableFromDelegates = tintDrawable(context, i, z, loadDrawableFromDelegates);
        }
        if (loadDrawableFromDelegates != null) {
            DrawableUtils.fixDrawable(loadDrawableFromDelegates);
        }
        return loadDrawableFromDelegates;
    }

    private static long createCacheKey(TypedValue typedValue) {
        return (((long) typedValue.assetCookie) << 32) | ((long) typedValue.data);
    }

    private Drawable createDrawableIfNeeded(@NonNull Context context, @DrawableRes int i) {
        if (this.mTypedValue == null) {
            this.mTypedValue = new TypedValue();
        }
        TypedValue typedValue = this.mTypedValue;
        context.getResources().getValue(i, typedValue, true);
        long createCacheKey = createCacheKey(typedValue);
        Drawable cachedDrawable = getCachedDrawable(context, createCacheKey);
        if (cachedDrawable != null) {
            return cachedDrawable;
        }
        if (i == C0254R.C0255drawable.abc_cab_background_top_material) {
            cachedDrawable = new LayerDrawable(new Drawable[]{getDrawable(context, C0254R.C0255drawable.abc_cab_background_internal_bg), getDrawable(context, C0254R.C0255drawable.abc_cab_background_top_mtrl_alpha)});
        }
        if (cachedDrawable != null) {
            cachedDrawable.setChangingConfigurations(typedValue.changingConfigurations);
            addDrawableToCache(context, createCacheKey, cachedDrawable);
        }
        return cachedDrawable;
    }

    private Drawable tintDrawable(@NonNull Context context, @DrawableRes int i, boolean z, @NonNull Drawable drawable) {
        ColorStateList tintList = getTintList(context, i);
        if (tintList != null) {
            if (DrawableUtils.canSafelyMutateDrawable(drawable)) {
                drawable = drawable.mutate();
            }
            Drawable wrap = DrawableCompat.wrap(drawable);
            DrawableCompat.setTintList(wrap, tintList);
            Mode tintMode = getTintMode(i);
            if (tintMode == null) {
                return wrap;
            }
            DrawableCompat.setTintMode(wrap, tintMode);
            return wrap;
        } else if (i == C0254R.C0255drawable.abc_seekbar_track_material) {
            LayerDrawable layerDrawable = (LayerDrawable) drawable;
            setPorterDuffColorFilter(layerDrawable.findDrawableByLayerId(16908288), ThemeUtils.getThemeAttrColor(context, C0254R.attr.colorControlNormal), DEFAULT_MODE);
            setPorterDuffColorFilter(layerDrawable.findDrawableByLayerId(16908303), ThemeUtils.getThemeAttrColor(context, C0254R.attr.colorControlNormal), DEFAULT_MODE);
            setPorterDuffColorFilter(layerDrawable.findDrawableByLayerId(16908301), ThemeUtils.getThemeAttrColor(context, C0254R.attr.colorControlActivated), DEFAULT_MODE);
            return drawable;
        } else if (i == C0254R.C0255drawable.abc_ratingbar_indicator_material || i == C0254R.C0255drawable.abc_ratingbar_small_material) {
            LayerDrawable layerDrawable2 = (LayerDrawable) drawable;
            setPorterDuffColorFilter(layerDrawable2.findDrawableByLayerId(16908288), ThemeUtils.getDisabledThemeAttrColor(context, C0254R.attr.colorControlNormal), DEFAULT_MODE);
            setPorterDuffColorFilter(layerDrawable2.findDrawableByLayerId(16908303), ThemeUtils.getThemeAttrColor(context, C0254R.attr.colorControlActivated), DEFAULT_MODE);
            setPorterDuffColorFilter(layerDrawable2.findDrawableByLayerId(16908301), ThemeUtils.getThemeAttrColor(context, C0254R.attr.colorControlActivated), DEFAULT_MODE);
            return drawable;
        } else if (tintDrawableUsingColorFilter(context, i, drawable) || !z) {
            return drawable;
        } else {
            return null;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:30:0x0075 A[Catch:{ Exception -> 0x00a4 }] */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x009c A[Catch:{ Exception -> 0x00a4 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private android.graphics.drawable.Drawable loadDrawableFromDelegates(@android.support.annotation.NonNull android.content.Context r11, @android.support.annotation.DrawableRes int r12) {
        /*
            r10 = this;
            android.support.v4.util.ArrayMap<java.lang.String, android.support.v7.widget.AppCompatDrawableManager$InflateDelegate> r0 = r10.mDelegates
            r1 = 0
            if (r0 == 0) goto L_0x00b4
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x00b4
            android.util.SparseArray<java.lang.String> r0 = r10.mKnownDrawableIdTags
            java.lang.String r2 = "appcompat_skip_skip"
            if (r0 == 0) goto L_0x0028
            java.lang.Object r0 = r0.get(r12)
            java.lang.String r0 = (java.lang.String) r0
            boolean r3 = r2.equals(r0)
            if (r3 != 0) goto L_0x0027
            if (r0 == 0) goto L_0x002f
            android.support.v4.util.ArrayMap<java.lang.String, android.support.v7.widget.AppCompatDrawableManager$InflateDelegate> r3 = r10.mDelegates
            java.lang.Object r0 = r3.get(r0)
            if (r0 != 0) goto L_0x002f
        L_0x0027:
            return r1
        L_0x0028:
            android.util.SparseArray r0 = new android.util.SparseArray
            r0.<init>()
            r10.mKnownDrawableIdTags = r0
        L_0x002f:
            android.util.TypedValue r0 = r10.mTypedValue
            if (r0 != 0) goto L_0x003a
            android.util.TypedValue r0 = new android.util.TypedValue
            r0.<init>()
            r10.mTypedValue = r0
        L_0x003a:
            android.util.TypedValue r0 = r10.mTypedValue
            android.content.res.Resources r1 = r11.getResources()
            r3 = 1
            r1.getValue(r12, r0, r3)
            long r4 = createCacheKey(r0)
            android.graphics.drawable.Drawable r6 = r10.getCachedDrawable(r11, r4)
            if (r6 == 0) goto L_0x004f
            return r6
        L_0x004f:
            java.lang.CharSequence r7 = r0.string
            if (r7 == 0) goto L_0x00ac
            java.lang.CharSequence r7 = r0.string
            java.lang.String r7 = r7.toString()
            java.lang.String r8 = ".xml"
            boolean r7 = r7.endsWith(r8)
            if (r7 == 0) goto L_0x00ac
            android.content.res.XmlResourceParser r1 = r1.getXml(r12)     // Catch:{ Exception -> 0x00a4 }
            android.util.AttributeSet r7 = android.util.Xml.asAttributeSet(r1)     // Catch:{ Exception -> 0x00a4 }
        L_0x0069:
            int r8 = r1.next()     // Catch:{ Exception -> 0x00a4 }
            r9 = 2
            if (r8 == r9) goto L_0x0073
            if (r8 == r3) goto L_0x0073
            goto L_0x0069
        L_0x0073:
            if (r8 != r9) goto L_0x009c
            java.lang.String r3 = r1.getName()     // Catch:{ Exception -> 0x00a4 }
            android.util.SparseArray<java.lang.String> r8 = r10.mKnownDrawableIdTags     // Catch:{ Exception -> 0x00a4 }
            r8.append(r12, r3)     // Catch:{ Exception -> 0x00a4 }
            android.support.v4.util.ArrayMap<java.lang.String, android.support.v7.widget.AppCompatDrawableManager$InflateDelegate> r8 = r10.mDelegates     // Catch:{ Exception -> 0x00a4 }
            java.lang.Object r3 = r8.get(r3)     // Catch:{ Exception -> 0x00a4 }
            android.support.v7.widget.AppCompatDrawableManager$InflateDelegate r3 = (android.support.p003v7.widget.AppCompatDrawableManager.InflateDelegate) r3     // Catch:{ Exception -> 0x00a4 }
            if (r3 == 0) goto L_0x0091
            android.content.res.Resources$Theme r8 = r11.getTheme()     // Catch:{ Exception -> 0x00a4 }
            android.graphics.drawable.Drawable r1 = r3.createFromXmlInner(r11, r1, r7, r8)     // Catch:{ Exception -> 0x00a4 }
            r6 = r1
        L_0x0091:
            if (r6 == 0) goto L_0x00ac
            int r0 = r0.changingConfigurations     // Catch:{ Exception -> 0x00a4 }
            r6.setChangingConfigurations(r0)     // Catch:{ Exception -> 0x00a4 }
            r10.addDrawableToCache(r11, r4, r6)     // Catch:{ Exception -> 0x00a4 }
            goto L_0x00ac
        L_0x009c:
            org.xmlpull.v1.XmlPullParserException r11 = new org.xmlpull.v1.XmlPullParserException     // Catch:{ Exception -> 0x00a4 }
            java.lang.String r0 = "No start tag found"
            r11.<init>(r0)     // Catch:{ Exception -> 0x00a4 }
            throw r11     // Catch:{ Exception -> 0x00a4 }
        L_0x00a4:
            r11 = move-exception
            java.lang.String r0 = "AppCompatDrawableManager"
            java.lang.String r1 = "Exception while inflating drawable"
            android.util.Log.e(r0, r1, r11)
        L_0x00ac:
            if (r6 != 0) goto L_0x00b3
            android.util.SparseArray<java.lang.String> r11 = r10.mKnownDrawableIdTags
            r11.append(r12, r2)
        L_0x00b3:
            return r6
        L_0x00b4:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.p003v7.widget.AppCompatDrawableManager.loadDrawableFromDelegates(android.content.Context, int):android.graphics.drawable.Drawable");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:16:0x002e, code lost:
        return null;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private android.graphics.drawable.Drawable getCachedDrawable(@android.support.annotation.NonNull android.content.Context r5, long r6) {
        /*
            r4 = this;
            java.lang.Object r0 = r4.mDrawableCacheLock
            monitor-enter(r0)
            java.util.WeakHashMap<android.content.Context, android.support.v4.util.LongSparseArray<java.lang.ref.WeakReference<android.graphics.drawable.Drawable$ConstantState>>> r1 = r4.mDrawableCaches     // Catch:{ all -> 0x002f }
            java.lang.Object r1 = r1.get(r5)     // Catch:{ all -> 0x002f }
            android.support.v4.util.LongSparseArray r1 = (android.support.p000v4.util.LongSparseArray) r1     // Catch:{ all -> 0x002f }
            r2 = 0
            if (r1 != 0) goto L_0x0010
            monitor-exit(r0)     // Catch:{ all -> 0x002f }
            return r2
        L_0x0010:
            java.lang.Object r3 = r1.get(r6)     // Catch:{ all -> 0x002f }
            java.lang.ref.WeakReference r3 = (java.lang.ref.WeakReference) r3     // Catch:{ all -> 0x002f }
            if (r3 == 0) goto L_0x002d
            java.lang.Object r3 = r3.get()     // Catch:{ all -> 0x002f }
            android.graphics.drawable.Drawable$ConstantState r3 = (android.graphics.drawable.Drawable.ConstantState) r3     // Catch:{ all -> 0x002f }
            if (r3 == 0) goto L_0x002a
            android.content.res.Resources r5 = r5.getResources()     // Catch:{ all -> 0x002f }
            android.graphics.drawable.Drawable r5 = r3.newDrawable(r5)     // Catch:{ all -> 0x002f }
            monitor-exit(r0)     // Catch:{ all -> 0x002f }
            return r5
        L_0x002a:
            r1.delete(r6)     // Catch:{ all -> 0x002f }
        L_0x002d:
            monitor-exit(r0)     // Catch:{ all -> 0x002f }
            return r2
        L_0x002f:
            r5 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x002f }
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.p003v7.widget.AppCompatDrawableManager.getCachedDrawable(android.content.Context, long):android.graphics.drawable.Drawable");
    }

    private boolean addDrawableToCache(@NonNull Context context, long j, @NonNull Drawable drawable) {
        ConstantState constantState = drawable.getConstantState();
        if (constantState == null) {
            return false;
        }
        synchronized (this.mDrawableCacheLock) {
            LongSparseArray longSparseArray = (LongSparseArray) this.mDrawableCaches.get(context);
            if (longSparseArray == null) {
                longSparseArray = new LongSparseArray();
                this.mDrawableCaches.put(context, longSparseArray);
            }
            longSparseArray.put(j, new WeakReference(constantState));
        }
        return true;
    }

    public final Drawable onDrawableLoadedFromResources(@NonNull Context context, @NonNull VectorEnabledTintResources vectorEnabledTintResources, @DrawableRes int i) {
        Drawable loadDrawableFromDelegates = loadDrawableFromDelegates(context, i);
        if (loadDrawableFromDelegates == null) {
            loadDrawableFromDelegates = vectorEnabledTintResources.superGetDrawable(i);
        }
        if (loadDrawableFromDelegates != null) {
            return tintDrawable(context, i, false, loadDrawableFromDelegates);
        }
        return null;
    }

    /* JADX WARNING: Removed duplicated region for block: B:16:0x0049  */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x0064 A[RETURN] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static boolean tintDrawableUsingColorFilter(@android.support.annotation.NonNull android.content.Context r6, @android.support.annotation.DrawableRes int r7, @android.support.annotation.NonNull android.graphics.drawable.Drawable r8) {
        /*
            android.graphics.PorterDuff$Mode r0 = DEFAULT_MODE
            int[] r1 = COLORFILTER_TINT_COLOR_CONTROL_NORMAL
            boolean r1 = arrayContains(r1, r7)
            r2 = -1
            r3 = 0
            r4 = 1
            if (r1 == 0) goto L_0x0013
            int r7 = android.support.p003v7.appcompat.C0254R.attr.colorControlNormal
        L_0x000f:
            r1 = r7
            r7 = 1
        L_0x0011:
            r5 = -1
            goto L_0x0047
        L_0x0013:
            int[] r1 = COLORFILTER_COLOR_CONTROL_ACTIVATED
            boolean r1 = arrayContains(r1, r7)
            if (r1 == 0) goto L_0x001e
            int r7 = android.support.p003v7.appcompat.C0254R.attr.colorControlActivated
            goto L_0x000f
        L_0x001e:
            int[] r1 = COLORFILTER_COLOR_BACKGROUND_MULTIPLY
            boolean r1 = arrayContains(r1, r7)
            if (r1 == 0) goto L_0x0030
            r7 = 16842801(0x1010031, float:2.3693695E-38)
            android.graphics.PorterDuff$Mode r0 = android.graphics.PorterDuff.Mode.MULTIPLY
            r7 = 1
            r1 = 16842801(0x1010031, float:2.3693695E-38)
            goto L_0x0011
        L_0x0030:
            int r1 = android.support.p003v7.appcompat.C0254R.C0255drawable.abc_list_divider_mtrl_alpha
            if (r7 != r1) goto L_0x0044
            r7 = 16842800(0x1010030, float:2.3693693E-38)
            r1 = 1109603123(0x42233333, float:40.8)
            int r1 = java.lang.Math.round(r1)
            r5 = r1
            r7 = 1
            r1 = 16842800(0x1010030, float:2.3693693E-38)
            goto L_0x0047
        L_0x0044:
            r7 = 0
            r1 = 0
            goto L_0x0011
        L_0x0047:
            if (r7 == 0) goto L_0x0064
            boolean r7 = android.support.p003v7.widget.DrawableUtils.canSafelyMutateDrawable(r8)
            if (r7 == 0) goto L_0x0053
            android.graphics.drawable.Drawable r8 = r8.mutate()
        L_0x0053:
            int r6 = android.support.p003v7.widget.ThemeUtils.getThemeAttrColor(r6, r1)
            android.graphics.PorterDuffColorFilter r6 = getPorterDuffColorFilter(r6, r0)
            r8.setColorFilter(r6)
            if (r5 == r2) goto L_0x0063
            r8.setAlpha(r5)
        L_0x0063:
            return r4
        L_0x0064:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.p003v7.widget.AppCompatDrawableManager.tintDrawableUsingColorFilter(android.content.Context, int, android.graphics.drawable.Drawable):boolean");
    }

    private void addDelegate(@NonNull String str, @NonNull InflateDelegate inflateDelegate) {
        if (this.mDelegates == null) {
            this.mDelegates = new ArrayMap<>();
        }
        this.mDelegates.put(str, inflateDelegate);
    }

    private void removeDelegate(@NonNull String str, @NonNull InflateDelegate inflateDelegate) {
        ArrayMap<String, InflateDelegate> arrayMap = this.mDelegates;
        if (arrayMap != null && arrayMap.get(str) == inflateDelegate) {
            this.mDelegates.remove(str);
        }
    }

    private static boolean arrayContains(int[] iArr, int i) {
        for (int i2 : iArr) {
            if (i2 == i) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: 0000 */
    public final Mode getTintMode(int i) {
        if (i == C0254R.C0255drawable.abc_switch_thumb_material) {
            return Mode.MULTIPLY;
        }
        return null;
    }

    public final ColorStateList getTintList(@NonNull Context context, @DrawableRes int i) {
        ColorStateList tintListFromCache = getTintListFromCache(context, i);
        if (tintListFromCache == null) {
            if (i == C0254R.C0255drawable.abc_edit_text_material) {
                tintListFromCache = createEditTextColorStateList(context);
            } else if (i == C0254R.C0255drawable.abc_switch_track_mtrl_alpha) {
                tintListFromCache = createSwitchTrackColorStateList(context);
            } else if (i == C0254R.C0255drawable.abc_switch_thumb_material) {
                tintListFromCache = createSwitchThumbColorStateList(context);
            } else if (i == C0254R.C0255drawable.abc_btn_default_mtrl_shape) {
                tintListFromCache = createDefaultButtonColorStateList(context);
            } else if (i == C0254R.C0255drawable.abc_btn_borderless_material) {
                tintListFromCache = createBorderlessButtonColorStateList(context);
            } else if (i == C0254R.C0255drawable.abc_btn_colored_material) {
                tintListFromCache = createColoredButtonColorStateList(context);
            } else if (i == C0254R.C0255drawable.abc_spinner_mtrl_am_alpha || i == C0254R.C0255drawable.abc_spinner_textfield_background_material) {
                tintListFromCache = createSpinnerColorStateList(context);
            } else if (arrayContains(TINT_COLOR_CONTROL_NORMAL, i)) {
                tintListFromCache = ThemeUtils.getThemeAttrColorStateList(context, C0254R.attr.colorControlNormal);
            } else if (arrayContains(TINT_COLOR_CONTROL_STATE_LIST, i)) {
                tintListFromCache = createDefaultColorStateList(context);
            } else if (arrayContains(TINT_CHECKABLE_BUTTON_LIST, i)) {
                tintListFromCache = createCheckableButtonColorStateList(context);
            } else if (i == C0254R.C0255drawable.abc_seekbar_thumb_material) {
                tintListFromCache = createSeekbarThumbColorStateList(context);
            }
            if (tintListFromCache != null) {
                addTintListToCache(context, i, tintListFromCache);
            }
        }
        return tintListFromCache;
    }

    private ColorStateList getTintListFromCache(@NonNull Context context, @DrawableRes int i) {
        WeakHashMap<Context, SparseArray<ColorStateList>> weakHashMap = this.mTintLists;
        if (weakHashMap == null) {
            return null;
        }
        SparseArray sparseArray = (SparseArray) weakHashMap.get(context);
        if (sparseArray != null) {
            return (ColorStateList) sparseArray.get(i);
        }
        return null;
    }

    private void addTintListToCache(@NonNull Context context, @DrawableRes int i, @NonNull ColorStateList colorStateList) {
        if (this.mTintLists == null) {
            this.mTintLists = new WeakHashMap<>();
        }
        SparseArray sparseArray = (SparseArray) this.mTintLists.get(context);
        if (sparseArray == null) {
            sparseArray = new SparseArray();
            this.mTintLists.put(context, sparseArray);
        }
        sparseArray.append(i, colorStateList);
    }

    private ColorStateList createDefaultColorStateList(Context context) {
        int themeAttrColor = ThemeUtils.getThemeAttrColor(context, C0254R.attr.colorControlNormal);
        int themeAttrColor2 = ThemeUtils.getThemeAttrColor(context, C0254R.attr.colorControlActivated);
        return new ColorStateList(new int[][]{ThemeUtils.DISABLED_STATE_SET, ThemeUtils.FOCUSED_STATE_SET, ThemeUtils.ACTIVATED_STATE_SET, ThemeUtils.PRESSED_STATE_SET, ThemeUtils.CHECKED_STATE_SET, ThemeUtils.SELECTED_STATE_SET, ThemeUtils.EMPTY_STATE_SET}, new int[]{ThemeUtils.getDisabledThemeAttrColor(context, C0254R.attr.colorControlNormal), themeAttrColor2, themeAttrColor2, themeAttrColor2, themeAttrColor2, themeAttrColor2, themeAttrColor});
    }

    private ColorStateList createCheckableButtonColorStateList(Context context) {
        return new ColorStateList(new int[][]{ThemeUtils.DISABLED_STATE_SET, ThemeUtils.CHECKED_STATE_SET, ThemeUtils.EMPTY_STATE_SET}, new int[]{ThemeUtils.getDisabledThemeAttrColor(context, C0254R.attr.colorControlNormal), ThemeUtils.getThemeAttrColor(context, C0254R.attr.colorControlActivated), ThemeUtils.getThemeAttrColor(context, C0254R.attr.colorControlNormal)});
    }

    private ColorStateList createSwitchTrackColorStateList(Context context) {
        return new ColorStateList(new int[][]{ThemeUtils.DISABLED_STATE_SET, ThemeUtils.CHECKED_STATE_SET, ThemeUtils.EMPTY_STATE_SET}, new int[]{ThemeUtils.getThemeAttrColor(context, 16842800, 0.1f), ThemeUtils.getThemeAttrColor(context, C0254R.attr.colorControlActivated, 0.3f), ThemeUtils.getThemeAttrColor(context, 16842800, 0.3f)});
    }

    private ColorStateList createSwitchThumbColorStateList(Context context) {
        int[][] iArr = new int[3][];
        int[] iArr2 = new int[3];
        ColorStateList themeAttrColorStateList = ThemeUtils.getThemeAttrColorStateList(context, C0254R.attr.colorSwitchThumbNormal);
        if (themeAttrColorStateList == null || !themeAttrColorStateList.isStateful()) {
            iArr[0] = ThemeUtils.DISABLED_STATE_SET;
            iArr2[0] = ThemeUtils.getDisabledThemeAttrColor(context, C0254R.attr.colorSwitchThumbNormal);
            iArr[1] = ThemeUtils.CHECKED_STATE_SET;
            iArr2[1] = ThemeUtils.getThemeAttrColor(context, C0254R.attr.colorControlActivated);
            iArr[2] = ThemeUtils.EMPTY_STATE_SET;
            iArr2[2] = ThemeUtils.getThemeAttrColor(context, C0254R.attr.colorSwitchThumbNormal);
        } else {
            iArr[0] = ThemeUtils.DISABLED_STATE_SET;
            iArr2[0] = themeAttrColorStateList.getColorForState(iArr[0], 0);
            iArr[1] = ThemeUtils.CHECKED_STATE_SET;
            iArr2[1] = ThemeUtils.getThemeAttrColor(context, C0254R.attr.colorControlActivated);
            iArr[2] = ThemeUtils.EMPTY_STATE_SET;
            iArr2[2] = themeAttrColorStateList.getDefaultColor();
        }
        return new ColorStateList(iArr, iArr2);
    }

    private ColorStateList createEditTextColorStateList(Context context) {
        return new ColorStateList(new int[][]{ThemeUtils.DISABLED_STATE_SET, ThemeUtils.NOT_PRESSED_OR_FOCUSED_STATE_SET, ThemeUtils.EMPTY_STATE_SET}, new int[]{ThemeUtils.getDisabledThemeAttrColor(context, C0254R.attr.colorControlNormal), ThemeUtils.getThemeAttrColor(context, C0254R.attr.colorControlNormal), ThemeUtils.getThemeAttrColor(context, C0254R.attr.colorControlActivated)});
    }

    private ColorStateList createDefaultButtonColorStateList(Context context) {
        return createButtonColorStateList(context, ThemeUtils.getThemeAttrColor(context, C0254R.attr.colorButtonNormal));
    }

    private ColorStateList createBorderlessButtonColorStateList(Context context) {
        return createButtonColorStateList(context, 0);
    }

    private ColorStateList createColoredButtonColorStateList(Context context) {
        return createButtonColorStateList(context, ThemeUtils.getThemeAttrColor(context, C0254R.attr.colorAccent));
    }

    private ColorStateList createButtonColorStateList(Context context, @ColorInt int i) {
        int themeAttrColor = ThemeUtils.getThemeAttrColor(context, C0254R.attr.colorControlHighlight);
        return new ColorStateList(new int[][]{ThemeUtils.DISABLED_STATE_SET, ThemeUtils.PRESSED_STATE_SET, ThemeUtils.FOCUSED_STATE_SET, ThemeUtils.EMPTY_STATE_SET}, new int[]{ThemeUtils.getDisabledThemeAttrColor(context, C0254R.attr.colorButtonNormal), ColorUtils.compositeColors(themeAttrColor, i), ColorUtils.compositeColors(themeAttrColor, i), i});
    }

    private ColorStateList createSpinnerColorStateList(Context context) {
        return new ColorStateList(new int[][]{ThemeUtils.DISABLED_STATE_SET, ThemeUtils.NOT_PRESSED_OR_FOCUSED_STATE_SET, ThemeUtils.EMPTY_STATE_SET}, new int[]{ThemeUtils.getDisabledThemeAttrColor(context, C0254R.attr.colorControlNormal), ThemeUtils.getThemeAttrColor(context, C0254R.attr.colorControlNormal), ThemeUtils.getThemeAttrColor(context, C0254R.attr.colorControlActivated)});
    }

    private ColorStateList createSeekbarThumbColorStateList(Context context) {
        return new ColorStateList(new int[][]{ThemeUtils.DISABLED_STATE_SET, ThemeUtils.EMPTY_STATE_SET}, new int[]{ThemeUtils.getDisabledThemeAttrColor(context, C0254R.attr.colorControlActivated), ThemeUtils.getThemeAttrColor(context, C0254R.attr.colorControlActivated)});
    }

    public static void tintDrawable(Drawable drawable, TintInfo tintInfo, int[] iArr) {
        if (!DrawableUtils.canSafelyMutateDrawable(drawable) || drawable.mutate() == drawable) {
            if (tintInfo.mHasTintList || tintInfo.mHasTintMode) {
                drawable.setColorFilter(createTintFilter(tintInfo.mHasTintList ? tintInfo.mTintList : null, tintInfo.mHasTintMode ? tintInfo.mTintMode : DEFAULT_MODE, iArr));
            } else {
                drawable.clearColorFilter();
            }
            if (VERSION.SDK_INT <= 23) {
                drawable.invalidateSelf();
            }
            return;
        }
        Log.d(TAG, "Mutated drawable is not the same instance as the input.");
    }

    private static PorterDuffColorFilter createTintFilter(ColorStateList colorStateList, Mode mode, int[] iArr) {
        if (colorStateList == null || mode == null) {
            return null;
        }
        return getPorterDuffColorFilter(colorStateList.getColorForState(iArr, 0), mode);
    }

    public static PorterDuffColorFilter getPorterDuffColorFilter(int i, Mode mode) {
        PorterDuffColorFilter porterDuffColorFilter = COLOR_FILTER_CACHE.get(i, mode);
        if (porterDuffColorFilter != null) {
            return porterDuffColorFilter;
        }
        PorterDuffColorFilter porterDuffColorFilter2 = new PorterDuffColorFilter(i, mode);
        COLOR_FILTER_CACHE.put(i, mode, porterDuffColorFilter2);
        return porterDuffColorFilter2;
    }

    private static void setPorterDuffColorFilter(Drawable drawable, int i, Mode mode) {
        if (DrawableUtils.canSafelyMutateDrawable(drawable)) {
            drawable = drawable.mutate();
        }
        if (mode == null) {
            mode = DEFAULT_MODE;
        }
        drawable.setColorFilter(getPorterDuffColorFilter(i, mode));
    }

    private static boolean isVectorDrawable(@NonNull Drawable drawable) {
        if (!(drawable instanceof VectorDrawableCompat)) {
            if (!PLATFORM_VD_CLAZZ.equals(drawable.getClass().getName())) {
                return false;
            }
        }
        return true;
    }
}
