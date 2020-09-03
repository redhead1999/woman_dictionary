package android.support.design.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.C0007R;
import android.support.p000v4.util.Pools.Pool;
import android.support.p000v4.util.Pools.SimplePool;
import android.support.p000v4.util.Pools.SynchronizedPool;
import android.support.p000v4.view.GravityCompat;
import android.support.p000v4.view.PagerAdapter;
import android.support.p000v4.view.ViewCompat;
import android.support.p000v4.view.ViewPager;
import android.support.p000v4.view.ViewPager.OnPageChangeListener;
import android.support.p000v4.widget.TextViewCompat;
import android.support.p003v7.widget.AppCompatDrawableManager;
import android.text.Layout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;

public class TabLayout extends HorizontalScrollView {
    private static final int ANIMATION_DURATION = 300;
    private static final int DEFAULT_GAP_TEXT_ICON = 8;
    private static final int DEFAULT_HEIGHT = 48;
    private static final int DEFAULT_HEIGHT_WITH_TEXT_ICON = 72;
    private static final int FIXED_WRAP_GUTTER_MIN = 16;
    public static final int GRAVITY_CENTER = 1;
    public static final int GRAVITY_FILL = 0;
    private static final int INVALID_WIDTH = -1;
    public static final int MODE_FIXED = 1;
    public static final int MODE_SCROLLABLE = 0;
    private static final int MOTION_NON_ADJACENT_OFFSET = 24;
    private static final int TAB_MIN_WIDTH_MARGIN = 56;
    private static final Pool<Tab> sTabPool = new SynchronizedPool(16);
    private int mContentInsetStart;
    /* access modifiers changed from: private */
    public int mMode;
    private OnTabSelectedListener mOnTabSelectedListener;
    private TabLayoutOnPageChangeListener mPageChangeListener;
    private PagerAdapter mPagerAdapter;
    private DataSetObserver mPagerAdapterObserver;
    private final int mRequestedTabMaxWidth;
    private final int mRequestedTabMinWidth;
    private ValueAnimatorCompat mScrollAnimator;
    private final int mScrollableTabMinWidth;
    private Tab mSelectedTab;
    /* access modifiers changed from: private */
    public final int mTabBackgroundResId;
    /* access modifiers changed from: private */
    public int mTabGravity;
    /* access modifiers changed from: private */
    public int mTabMaxWidth;
    /* access modifiers changed from: private */
    public int mTabPaddingBottom;
    /* access modifiers changed from: private */
    public int mTabPaddingEnd;
    /* access modifiers changed from: private */
    public int mTabPaddingStart;
    /* access modifiers changed from: private */
    public int mTabPaddingTop;
    private final SlidingTabStrip mTabStrip;
    /* access modifiers changed from: private */
    public int mTabTextAppearance;
    /* access modifiers changed from: private */
    public ColorStateList mTabTextColors;
    /* access modifiers changed from: private */
    public float mTabTextMultiLineSize;
    /* access modifiers changed from: private */
    public float mTabTextSize;
    private final Pool<TabView> mTabViewPool;
    private final ArrayList<Tab> mTabs;
    private ViewPager mViewPager;

    @Retention(RetentionPolicy.SOURCE)
    public @interface Mode {
    }

    public interface OnTabSelectedListener {
        void onTabReselected(Tab tab);

        void onTabSelected(Tab tab);

        void onTabUnselected(Tab tab);
    }

    private class PagerAdapterObserver extends DataSetObserver {
        private PagerAdapterObserver() {
        }

        public void onChanged() {
            TabLayout.this.populateFromPagerAdapter();
        }

        public void onInvalidated() {
            TabLayout.this.populateFromPagerAdapter();
        }
    }

    private class SlidingTabStrip extends LinearLayout {
        private ValueAnimatorCompat mIndicatorAnimator;
        private int mIndicatorLeft = -1;
        private int mIndicatorRight = -1;
        private int mSelectedIndicatorHeight;
        private final Paint mSelectedIndicatorPaint;
        /* access modifiers changed from: private */
        public int mSelectedPosition = -1;
        /* access modifiers changed from: private */
        public float mSelectionOffset;

        SlidingTabStrip(Context context) {
            super(context);
            setWillNotDraw(false);
            this.mSelectedIndicatorPaint = new Paint();
        }

        /* access modifiers changed from: 0000 */
        public void setSelectedIndicatorColor(int i) {
            if (this.mSelectedIndicatorPaint.getColor() != i) {
                this.mSelectedIndicatorPaint.setColor(i);
                ViewCompat.postInvalidateOnAnimation(this);
            }
        }

        /* access modifiers changed from: 0000 */
        public void setSelectedIndicatorHeight(int i) {
            if (this.mSelectedIndicatorHeight != i) {
                this.mSelectedIndicatorHeight = i;
                ViewCompat.postInvalidateOnAnimation(this);
            }
        }

        /* access modifiers changed from: 0000 */
        public boolean childrenNeedLayout() {
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                if (getChildAt(i).getWidth() <= 0) {
                    return true;
                }
            }
            return false;
        }

        /* access modifiers changed from: 0000 */
        public void setIndicatorPositionFromTabPosition(int i, float f) {
            ValueAnimatorCompat valueAnimatorCompat = this.mIndicatorAnimator;
            if (valueAnimatorCompat != null && valueAnimatorCompat.isRunning()) {
                this.mIndicatorAnimator.cancel();
            }
            this.mSelectedPosition = i;
            this.mSelectionOffset = f;
            updateIndicatorPosition();
        }

        /* access modifiers changed from: 0000 */
        public float getIndicatorPosition() {
            return ((float) this.mSelectedPosition) + this.mSelectionOffset;
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, i2);
            if (MeasureSpec.getMode(i) == 1073741824) {
                boolean z = true;
                if (TabLayout.this.mMode == 1 && TabLayout.this.mTabGravity == 1) {
                    int childCount = getChildCount();
                    int i3 = 0;
                    for (int i4 = 0; i4 < childCount; i4++) {
                        View childAt = getChildAt(i4);
                        if (childAt.getVisibility() == 0) {
                            i3 = Math.max(i3, childAt.getMeasuredWidth());
                        }
                    }
                    if (i3 > 0) {
                        if (i3 * childCount <= getMeasuredWidth() - (TabLayout.this.dpToPx(16) * 2)) {
                            boolean z2 = false;
                            for (int i5 = 0; i5 < childCount; i5++) {
                                LayoutParams layoutParams = (LayoutParams) getChildAt(i5).getLayoutParams();
                                if (layoutParams.width != i3 || layoutParams.weight != 0.0f) {
                                    layoutParams.width = i3;
                                    layoutParams.weight = 0.0f;
                                    z2 = true;
                                }
                            }
                            z = z2;
                        } else {
                            TabLayout.this.mTabGravity = 0;
                            TabLayout.this.updateTabViews(false);
                        }
                        if (z) {
                            super.onMeasure(i, i2);
                        }
                    }
                }
            }
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            ValueAnimatorCompat valueAnimatorCompat = this.mIndicatorAnimator;
            if (valueAnimatorCompat == null || !valueAnimatorCompat.isRunning()) {
                updateIndicatorPosition();
                return;
            }
            this.mIndicatorAnimator.cancel();
            animateIndicatorToPosition(this.mSelectedPosition, Math.round((1.0f - this.mIndicatorAnimator.getAnimatedFraction()) * ((float) this.mIndicatorAnimator.getDuration())));
        }

        private void updateIndicatorPosition() {
            int i;
            int i2;
            View childAt = getChildAt(this.mSelectedPosition);
            if (childAt == null || childAt.getWidth() <= 0) {
                i = -1;
                i2 = -1;
            } else {
                i = childAt.getLeft();
                i2 = childAt.getRight();
                if (this.mSelectionOffset > 0.0f && this.mSelectedPosition < getChildCount() - 1) {
                    View childAt2 = getChildAt(this.mSelectedPosition + 1);
                    float left = this.mSelectionOffset * ((float) childAt2.getLeft());
                    float f = this.mSelectionOffset;
                    i = (int) (left + ((1.0f - f) * ((float) i)));
                    i2 = (int) ((f * ((float) childAt2.getRight())) + ((1.0f - this.mSelectionOffset) * ((float) i2)));
                }
            }
            setIndicatorPosition(i, i2);
        }

        /* access modifiers changed from: private */
        public void setIndicatorPosition(int i, int i2) {
            if (i != this.mIndicatorLeft || i2 != this.mIndicatorRight) {
                this.mIndicatorLeft = i;
                this.mIndicatorRight = i2;
                ViewCompat.postInvalidateOnAnimation(this);
            }
        }

        /* access modifiers changed from: 0000 */
        public void animateIndicatorToPosition(final int i, int i2) {
            final int i3;
            final int i4;
            ValueAnimatorCompat valueAnimatorCompat = this.mIndicatorAnimator;
            if (valueAnimatorCompat != null && valueAnimatorCompat.isRunning()) {
                this.mIndicatorAnimator.cancel();
            }
            boolean z = ViewCompat.getLayoutDirection(this) == 1;
            View childAt = getChildAt(i);
            if (childAt == null) {
                updateIndicatorPosition();
                return;
            }
            final int left = childAt.getLeft();
            final int right = childAt.getRight();
            if (Math.abs(i - this.mSelectedPosition) <= 1) {
                i4 = this.mIndicatorLeft;
                i3 = this.mIndicatorRight;
            } else {
                int access$2100 = TabLayout.this.dpToPx(24);
                i4 = (i >= this.mSelectedPosition ? !z : z) ? left - access$2100 : access$2100 + right;
                i3 = i4;
            }
            if (!(i4 == left && i3 == right)) {
                ValueAnimatorCompat createAnimator = ViewUtils.createAnimator();
                this.mIndicatorAnimator = createAnimator;
                createAnimator.setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
                createAnimator.setDuration(i2);
                createAnimator.setFloatValues(0.0f, 1.0f);
                C00481 r3 = new AnimatorUpdateListener() {
                    public void onAnimationUpdate(ValueAnimatorCompat valueAnimatorCompat) {
                        float animatedFraction = valueAnimatorCompat.getAnimatedFraction();
                        SlidingTabStrip.this.setIndicatorPosition(AnimationUtils.lerp(i4, left, animatedFraction), AnimationUtils.lerp(i3, right, animatedFraction));
                    }
                };
                createAnimator.setUpdateListener(r3);
                createAnimator.setListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(ValueAnimatorCompat valueAnimatorCompat) {
                        SlidingTabStrip.this.mSelectedPosition = i;
                        SlidingTabStrip.this.mSelectionOffset = 0.0f;
                    }
                });
                createAnimator.start();
            }
        }

        public void draw(Canvas canvas) {
            super.draw(canvas);
            int i = this.mIndicatorLeft;
            if (i >= 0 && this.mIndicatorRight > i) {
                canvas.drawRect((float) i, (float) (getHeight() - this.mSelectedIndicatorHeight), (float) this.mIndicatorRight, (float) getHeight(), this.mSelectedIndicatorPaint);
            }
        }
    }

    public static final class Tab {
        public static final int INVALID_POSITION = -1;
        private CharSequence mContentDesc;
        private View mCustomView;
        private Drawable mIcon;
        /* access modifiers changed from: private */
        public TabLayout mParent;
        private int mPosition;
        private Object mTag;
        private CharSequence mText;
        /* access modifiers changed from: private */
        public TabView mView;

        private Tab() {
            this.mPosition = -1;
        }

        @Nullable
        public Object getTag() {
            return this.mTag;
        }

        @NonNull
        public Tab setTag(@Nullable Object obj) {
            this.mTag = obj;
            return this;
        }

        @Nullable
        public View getCustomView() {
            return this.mCustomView;
        }

        @NonNull
        public Tab setCustomView(@Nullable View view) {
            this.mCustomView = view;
            updateView();
            return this;
        }

        @NonNull
        public Tab setCustomView(@LayoutRes int i) {
            return setCustomView(LayoutInflater.from(this.mView.getContext()).inflate(i, this.mView, false));
        }

        @Nullable
        public Drawable getIcon() {
            return this.mIcon;
        }

        public int getPosition() {
            return this.mPosition;
        }

        /* access modifiers changed from: 0000 */
        public void setPosition(int i) {
            this.mPosition = i;
        }

        @Nullable
        public CharSequence getText() {
            return this.mText;
        }

        @NonNull
        public Tab setIcon(@Nullable Drawable drawable) {
            this.mIcon = drawable;
            updateView();
            return this;
        }

        @NonNull
        public Tab setIcon(@DrawableRes int i) {
            if (this.mParent != null) {
                return setIcon(AppCompatDrawableManager.get().getDrawable(this.mParent.getContext(), i));
            }
            throw new IllegalArgumentException("Tab not attached to a TabLayout");
        }

        @NonNull
        public Tab setText(@Nullable CharSequence charSequence) {
            this.mText = charSequence;
            updateView();
            return this;
        }

        @NonNull
        public Tab setText(@StringRes int i) {
            TabLayout tabLayout = this.mParent;
            if (tabLayout != null) {
                return setText(tabLayout.getResources().getText(i));
            }
            throw new IllegalArgumentException("Tab not attached to a TabLayout");
        }

        public void select() {
            TabLayout tabLayout = this.mParent;
            if (tabLayout != null) {
                tabLayout.selectTab(this);
                return;
            }
            throw new IllegalArgumentException("Tab not attached to a TabLayout");
        }

        public boolean isSelected() {
            TabLayout tabLayout = this.mParent;
            if (tabLayout != null) {
                return tabLayout.getSelectedTabPosition() == this.mPosition;
            }
            throw new IllegalArgumentException("Tab not attached to a TabLayout");
        }

        @NonNull
        public Tab setContentDescription(@StringRes int i) {
            TabLayout tabLayout = this.mParent;
            if (tabLayout != null) {
                return setContentDescription(tabLayout.getResources().getText(i));
            }
            throw new IllegalArgumentException("Tab not attached to a TabLayout");
        }

        @NonNull
        public Tab setContentDescription(@Nullable CharSequence charSequence) {
            this.mContentDesc = charSequence;
            updateView();
            return this;
        }

        @Nullable
        public CharSequence getContentDescription() {
            return this.mContentDesc;
        }

        /* access modifiers changed from: private */
        public void updateView() {
            TabView tabView = this.mView;
            if (tabView != null) {
                tabView.update();
            }
        }

        /* access modifiers changed from: private */
        public void reset() {
            this.mParent = null;
            this.mView = null;
            this.mTag = null;
            this.mIcon = null;
            this.mText = null;
            this.mContentDesc = null;
            this.mPosition = -1;
            this.mCustomView = null;
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface TabGravity {
    }

    public static class TabLayoutOnPageChangeListener implements OnPageChangeListener {
        private int mPreviousScrollState;
        private int mScrollState;
        private final WeakReference<TabLayout> mTabLayoutRef;

        public TabLayoutOnPageChangeListener(TabLayout tabLayout) {
            this.mTabLayoutRef = new WeakReference<>(tabLayout);
        }

        public void onPageScrollStateChanged(int i) {
            this.mPreviousScrollState = this.mScrollState;
            this.mScrollState = i;
        }

        public void onPageScrolled(int i, float f, int i2) {
            TabLayout tabLayout = (TabLayout) this.mTabLayoutRef.get();
            if (tabLayout != null) {
                boolean z = false;
                boolean z2 = this.mScrollState != 2 || this.mPreviousScrollState == 1;
                if (!(this.mScrollState == 2 && this.mPreviousScrollState == 0)) {
                    z = true;
                }
                tabLayout.setScrollPosition(i, f, z2, z);
            }
        }

        public void onPageSelected(int i) {
            TabLayout tabLayout = (TabLayout) this.mTabLayoutRef.get();
            if (tabLayout != null && tabLayout.getSelectedTabPosition() != i) {
                int i2 = this.mScrollState;
                tabLayout.selectTab(tabLayout.getTabAt(i), i2 == 0 || (i2 == 2 && this.mPreviousScrollState == 0));
            }
        }

        /* access modifiers changed from: private */
        public void reset() {
            this.mScrollState = 0;
            this.mPreviousScrollState = 0;
        }
    }

    class TabView extends LinearLayout implements OnLongClickListener {
        private ImageView mCustomIconView;
        private TextView mCustomTextView;
        private View mCustomView;
        private int mDefaultMaxLines = 2;
        private ImageView mIconView;
        private Tab mTab;
        private TextView mTextView;

        public TabView(Context context) {
            super(context);
            if (TabLayout.this.mTabBackgroundResId != 0) {
                setBackgroundDrawable(AppCompatDrawableManager.get().getDrawable(context, TabLayout.this.mTabBackgroundResId));
            }
            ViewCompat.setPaddingRelative(this, TabLayout.this.mTabPaddingStart, TabLayout.this.mTabPaddingTop, TabLayout.this.mTabPaddingEnd, TabLayout.this.mTabPaddingBottom);
            setGravity(17);
            setOrientation(1);
            setClickable(true);
        }

        public boolean performClick() {
            boolean performClick = super.performClick();
            Tab tab = this.mTab;
            if (tab == null) {
                return performClick;
            }
            tab.select();
            return true;
        }

        public void setSelected(boolean z) {
            boolean z2 = isSelected() != z;
            super.setSelected(z);
            if (z2 && z) {
                sendAccessibilityEvent(4);
                TextView textView = this.mTextView;
                if (textView != null) {
                    textView.setSelected(z);
                }
                ImageView imageView = this.mIconView;
                if (imageView != null) {
                    imageView.setSelected(z);
                }
            }
        }

        @TargetApi(14)
        public void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
            super.onInitializeAccessibilityEvent(accessibilityEvent);
            accessibilityEvent.setClassName(android.support.p003v7.app.ActionBar.Tab.class.getName());
        }

        @TargetApi(14)
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            accessibilityNodeInfo.setClassName(android.support.p003v7.app.ActionBar.Tab.class.getName());
        }

        public void onMeasure(int i, int i2) {
            int size = MeasureSpec.getSize(i);
            int mode = MeasureSpec.getMode(i);
            int access$1400 = TabLayout.this.getTabMaxWidth();
            if (access$1400 > 0 && (mode == 0 || size > access$1400)) {
                i = MeasureSpec.makeMeasureSpec(TabLayout.this.mTabMaxWidth, Integer.MIN_VALUE);
            }
            super.onMeasure(i, i2);
            if (this.mTextView != null) {
                getResources();
                float access$1600 = TabLayout.this.mTabTextSize;
                int i3 = this.mDefaultMaxLines;
                ImageView imageView = this.mIconView;
                boolean z = true;
                if (imageView == null || imageView.getVisibility() != 0) {
                    TextView textView = this.mTextView;
                    if (textView != null && textView.getLineCount() > 1) {
                        access$1600 = TabLayout.this.mTabTextMultiLineSize;
                    }
                } else {
                    i3 = 1;
                }
                float textSize = this.mTextView.getTextSize();
                int lineCount = this.mTextView.getLineCount();
                int maxLines = TextViewCompat.getMaxLines(this.mTextView);
                if (access$1600 != textSize || (maxLines >= 0 && i3 != maxLines)) {
                    if (TabLayout.this.mMode == 1 && access$1600 > textSize && lineCount == 1) {
                        Layout layout = this.mTextView.getLayout();
                        if (layout == null || approximateLineWidth(layout, 0, access$1600) > ((float) layout.getWidth())) {
                            z = false;
                        }
                    }
                    if (z) {
                        this.mTextView.setTextSize(0, access$1600);
                        this.mTextView.setMaxLines(i3);
                        super.onMeasure(i, i2);
                    }
                }
            }
        }

        /* access modifiers changed from: private */
        public void setTab(@Nullable Tab tab) {
            if (tab != this.mTab) {
                this.mTab = tab;
                update();
            }
        }

        /* access modifiers changed from: private */
        public void reset() {
            setTab(null);
            setSelected(false);
        }

        /* access modifiers changed from: 0000 */
        public final void update() {
            Tab tab = this.mTab;
            View customView = tab != null ? tab.getCustomView() : null;
            if (customView != null) {
                ViewParent parent = customView.getParent();
                if (parent != this) {
                    if (parent != null) {
                        ((ViewGroup) parent).removeView(customView);
                    }
                    addView(customView);
                }
                this.mCustomView = customView;
                TextView textView = this.mTextView;
                if (textView != null) {
                    textView.setVisibility(8);
                }
                ImageView imageView = this.mIconView;
                if (imageView != null) {
                    imageView.setVisibility(8);
                    this.mIconView.setImageDrawable(null);
                }
                this.mCustomTextView = (TextView) customView.findViewById(16908308);
                TextView textView2 = this.mCustomTextView;
                if (textView2 != null) {
                    this.mDefaultMaxLines = TextViewCompat.getMaxLines(textView2);
                }
                this.mCustomIconView = (ImageView) customView.findViewById(16908294);
            } else {
                View view = this.mCustomView;
                if (view != null) {
                    removeView(view);
                    this.mCustomView = null;
                }
                this.mCustomTextView = null;
                this.mCustomIconView = null;
            }
            if (this.mCustomView == null) {
                if (this.mIconView == null) {
                    ImageView imageView2 = (ImageView) LayoutInflater.from(getContext()).inflate(C0007R.layout.design_layout_tab_icon, this, false);
                    addView(imageView2, 0);
                    this.mIconView = imageView2;
                }
                if (this.mTextView == null) {
                    TextView textView3 = (TextView) LayoutInflater.from(getContext()).inflate(C0007R.layout.design_layout_tab_text, this, false);
                    addView(textView3);
                    this.mTextView = textView3;
                    this.mDefaultMaxLines = TextViewCompat.getMaxLines(this.mTextView);
                }
                this.mTextView.setTextAppearance(getContext(), TabLayout.this.mTabTextAppearance);
                if (TabLayout.this.mTabTextColors != null) {
                    this.mTextView.setTextColor(TabLayout.this.mTabTextColors);
                }
                updateTextAndIcon(this.mTextView, this.mIconView);
            } else if (this.mCustomTextView != null || this.mCustomIconView != null) {
                updateTextAndIcon(this.mCustomTextView, this.mCustomIconView);
            }
        }

        private void updateTextAndIcon(@Nullable TextView textView, @Nullable ImageView imageView) {
            Tab tab = this.mTab;
            Drawable icon = tab != null ? tab.getIcon() : null;
            Tab tab2 = this.mTab;
            CharSequence text = tab2 != null ? tab2.getText() : null;
            Tab tab3 = this.mTab;
            CharSequence contentDescription = tab3 != null ? tab3.getContentDescription() : null;
            if (imageView != null) {
                if (icon != null) {
                    imageView.setImageDrawable(icon);
                    imageView.setVisibility(0);
                    setVisibility(0);
                } else {
                    imageView.setVisibility(8);
                    imageView.setImageDrawable(null);
                }
                imageView.setContentDescription(contentDescription);
            }
            boolean z = !TextUtils.isEmpty(text);
            if (textView != null) {
                if (z) {
                    textView.setText(text);
                    textView.setVisibility(0);
                    setVisibility(0);
                } else {
                    textView.setVisibility(8);
                    textView.setText(null);
                }
                textView.setContentDescription(contentDescription);
            }
            if (imageView != null) {
                MarginLayoutParams marginLayoutParams = (MarginLayoutParams) imageView.getLayoutParams();
                int access$2100 = (!z || imageView.getVisibility() != 0) ? 0 : TabLayout.this.dpToPx(8);
                if (access$2100 != marginLayoutParams.bottomMargin) {
                    marginLayoutParams.bottomMargin = access$2100;
                    imageView.requestLayout();
                }
            }
            if (z || TextUtils.isEmpty(contentDescription)) {
                setOnLongClickListener(null);
                setLongClickable(false);
                return;
            }
            setOnLongClickListener(this);
        }

        public boolean onLongClick(View view) {
            int[] iArr = new int[2];
            getLocationOnScreen(iArr);
            Context context = getContext();
            int width = getWidth();
            int height = getHeight();
            int i = context.getResources().getDisplayMetrics().widthPixels;
            Toast makeText = Toast.makeText(context, this.mTab.getContentDescription(), 0);
            makeText.setGravity(49, (iArr[0] + (width / 2)) - (i / 2), height);
            makeText.show();
            return true;
        }

        public Tab getTab() {
            return this.mTab;
        }

        private float approximateLineWidth(Layout layout, int i, float f) {
            return layout.getLineWidth(i) * (f / layout.getPaint().getTextSize());
        }
    }

    public static class ViewPagerOnTabSelectedListener implements OnTabSelectedListener {
        private final ViewPager mViewPager;

        public void onTabReselected(Tab tab) {
        }

        public void onTabUnselected(Tab tab) {
        }

        public ViewPagerOnTabSelectedListener(ViewPager viewPager) {
            this.mViewPager = viewPager;
        }

        public void onTabSelected(Tab tab) {
            this.mViewPager.setCurrentItem(tab.getPosition());
        }
    }

    public TabLayout(Context context) {
        this(context, null);
    }

    public TabLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    /* JADX INFO: finally extract failed */
    public TabLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mTabs = new ArrayList<>();
        this.mTabMaxWidth = ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        this.mTabViewPool = new SimplePool(12);
        ThemeUtils.checkAppCompatTheme(context);
        setHorizontalScrollBarEnabled(false);
        this.mTabStrip = new SlidingTabStrip(context);
        super.addView(this.mTabStrip, 0, new FrameLayout.LayoutParams(-2, -1));
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, C0007R.styleable.TabLayout, i, C0007R.style.Widget_Design_TabLayout);
        this.mTabStrip.setSelectedIndicatorHeight(obtainStyledAttributes.getDimensionPixelSize(C0007R.styleable.TabLayout_tabIndicatorHeight, 0));
        this.mTabStrip.setSelectedIndicatorColor(obtainStyledAttributes.getColor(C0007R.styleable.TabLayout_tabIndicatorColor, 0));
        int dimensionPixelSize = obtainStyledAttributes.getDimensionPixelSize(C0007R.styleable.TabLayout_tabPadding, 0);
        this.mTabPaddingBottom = dimensionPixelSize;
        this.mTabPaddingEnd = dimensionPixelSize;
        this.mTabPaddingTop = dimensionPixelSize;
        this.mTabPaddingStart = dimensionPixelSize;
        this.mTabPaddingStart = obtainStyledAttributes.getDimensionPixelSize(C0007R.styleable.TabLayout_tabPaddingStart, this.mTabPaddingStart);
        this.mTabPaddingTop = obtainStyledAttributes.getDimensionPixelSize(C0007R.styleable.TabLayout_tabPaddingTop, this.mTabPaddingTop);
        this.mTabPaddingEnd = obtainStyledAttributes.getDimensionPixelSize(C0007R.styleable.TabLayout_tabPaddingEnd, this.mTabPaddingEnd);
        this.mTabPaddingBottom = obtainStyledAttributes.getDimensionPixelSize(C0007R.styleable.TabLayout_tabPaddingBottom, this.mTabPaddingBottom);
        this.mTabTextAppearance = obtainStyledAttributes.getResourceId(C0007R.styleable.TabLayout_tabTextAppearance, C0007R.style.TextAppearance_Design_Tab);
        TypedArray obtainStyledAttributes2 = context.obtainStyledAttributes(this.mTabTextAppearance, C0007R.styleable.TextAppearance);
        try {
            this.mTabTextSize = (float) obtainStyledAttributes2.getDimensionPixelSize(C0007R.styleable.TextAppearance_android_textSize, 0);
            this.mTabTextColors = obtainStyledAttributes2.getColorStateList(C0007R.styleable.TextAppearance_android_textColor);
            obtainStyledAttributes2.recycle();
            if (obtainStyledAttributes.hasValue(C0007R.styleable.TabLayout_tabTextColor)) {
                this.mTabTextColors = obtainStyledAttributes.getColorStateList(C0007R.styleable.TabLayout_tabTextColor);
            }
            if (obtainStyledAttributes.hasValue(C0007R.styleable.TabLayout_tabSelectedTextColor)) {
                this.mTabTextColors = createColorStateList(this.mTabTextColors.getDefaultColor(), obtainStyledAttributes.getColor(C0007R.styleable.TabLayout_tabSelectedTextColor, 0));
            }
            this.mRequestedTabMinWidth = obtainStyledAttributes.getDimensionPixelSize(C0007R.styleable.TabLayout_tabMinWidth, -1);
            this.mRequestedTabMaxWidth = obtainStyledAttributes.getDimensionPixelSize(C0007R.styleable.TabLayout_tabMaxWidth, -1);
            this.mTabBackgroundResId = obtainStyledAttributes.getResourceId(C0007R.styleable.TabLayout_tabBackground, 0);
            this.mContentInsetStart = obtainStyledAttributes.getDimensionPixelSize(C0007R.styleable.TabLayout_tabContentStart, 0);
            this.mMode = obtainStyledAttributes.getInt(C0007R.styleable.TabLayout_tabMode, 1);
            this.mTabGravity = obtainStyledAttributes.getInt(C0007R.styleable.TabLayout_tabGravity, 0);
            obtainStyledAttributes.recycle();
            Resources resources = getResources();
            this.mTabTextMultiLineSize = (float) resources.getDimensionPixelSize(C0007R.dimen.design_tab_text_size_2line);
            this.mScrollableTabMinWidth = resources.getDimensionPixelSize(C0007R.dimen.design_tab_scrollable_min_width);
            applyModeAndGravity();
        } catch (Throwable th) {
            obtainStyledAttributes2.recycle();
            throw th;
        }
    }

    public void setSelectedTabIndicatorColor(@ColorInt int i) {
        this.mTabStrip.setSelectedIndicatorColor(i);
    }

    public void setSelectedTabIndicatorHeight(int i) {
        this.mTabStrip.setSelectedIndicatorHeight(i);
    }

    public void setScrollPosition(int i, float f, boolean z) {
        setScrollPosition(i, f, z, true);
    }

    /* access modifiers changed from: private */
    public void setScrollPosition(int i, float f, boolean z, boolean z2) {
        int round = Math.round(((float) i) + f);
        if (round >= 0 && round < this.mTabStrip.getChildCount()) {
            if (z2) {
                this.mTabStrip.setIndicatorPositionFromTabPosition(i, f);
            }
            ValueAnimatorCompat valueAnimatorCompat = this.mScrollAnimator;
            if (valueAnimatorCompat != null && valueAnimatorCompat.isRunning()) {
                this.mScrollAnimator.cancel();
            }
            scrollTo(calculateScrollXForTab(i, f), 0);
            if (z) {
                setSelectedTabView(round);
            }
        }
    }

    private float getScrollPosition() {
        return this.mTabStrip.getIndicatorPosition();
    }

    public void addTab(@NonNull Tab tab) {
        addTab(tab, this.mTabs.isEmpty());
    }

    public void addTab(@NonNull Tab tab, int i) {
        addTab(tab, i, this.mTabs.isEmpty());
    }

    public void addTab(@NonNull Tab tab, boolean z) {
        if (tab.mParent == this) {
            addTabView(tab, z);
            configureTab(tab, this.mTabs.size());
            if (z) {
                tab.select();
                return;
            }
            return;
        }
        throw new IllegalArgumentException("Tab belongs to a different TabLayout.");
    }

    public void addTab(@NonNull Tab tab, int i, boolean z) {
        if (tab.mParent == this) {
            addTabView(tab, i, z);
            configureTab(tab, i);
            if (z) {
                tab.select();
                return;
            }
            return;
        }
        throw new IllegalArgumentException("Tab belongs to a different TabLayout.");
    }

    private void addTabFromItemView(@NonNull TabItem tabItem) {
        Tab newTab = newTab();
        if (tabItem.mText != null) {
            newTab.setText(tabItem.mText);
        }
        if (tabItem.mIcon != null) {
            newTab.setIcon(tabItem.mIcon);
        }
        if (tabItem.mCustomLayout != 0) {
            newTab.setCustomView(tabItem.mCustomLayout);
        }
        addTab(newTab);
    }

    public void setOnTabSelectedListener(OnTabSelectedListener onTabSelectedListener) {
        this.mOnTabSelectedListener = onTabSelectedListener;
    }

    @NonNull
    public Tab newTab() {
        Tab tab = (Tab) sTabPool.acquire();
        if (tab == null) {
            tab = new Tab();
        }
        tab.mParent = this;
        tab.mView = createTabView(tab);
        return tab;
    }

    public int getTabCount() {
        return this.mTabs.size();
    }

    @Nullable
    public Tab getTabAt(int i) {
        return (Tab) this.mTabs.get(i);
    }

    public int getSelectedTabPosition() {
        Tab tab = this.mSelectedTab;
        if (tab != null) {
            return tab.getPosition();
        }
        return -1;
    }

    public void removeTab(Tab tab) {
        if (tab.mParent == this) {
            removeTabAt(tab.getPosition());
            return;
        }
        throw new IllegalArgumentException("Tab does not belong to this TabLayout.");
    }

    public void removeTabAt(int i) {
        Tab tab = this.mSelectedTab;
        int position = tab != null ? tab.getPosition() : 0;
        removeTabViewAt(i);
        Tab tab2 = (Tab) this.mTabs.remove(i);
        if (tab2 != null) {
            tab2.reset();
            sTabPool.release(tab2);
        }
        int size = this.mTabs.size();
        for (int i2 = i; i2 < size; i2++) {
            ((Tab) this.mTabs.get(i2)).setPosition(i2);
        }
        if (position == i) {
            selectTab(this.mTabs.isEmpty() ? null : (Tab) this.mTabs.get(Math.max(0, i - 1)));
        }
    }

    public void removeAllTabs() {
        for (int childCount = this.mTabStrip.getChildCount() - 1; childCount >= 0; childCount--) {
            removeTabViewAt(childCount);
        }
        Iterator it = this.mTabs.iterator();
        while (it.hasNext()) {
            Tab tab = (Tab) it.next();
            it.remove();
            tab.reset();
            sTabPool.release(tab);
        }
        this.mSelectedTab = null;
    }

    public void setTabMode(int i) {
        if (i != this.mMode) {
            this.mMode = i;
            applyModeAndGravity();
        }
    }

    public int getTabMode() {
        return this.mMode;
    }

    public void setTabGravity(int i) {
        if (this.mTabGravity != i) {
            this.mTabGravity = i;
            applyModeAndGravity();
        }
    }

    public int getTabGravity() {
        return this.mTabGravity;
    }

    public void setTabTextColors(@Nullable ColorStateList colorStateList) {
        if (this.mTabTextColors != colorStateList) {
            this.mTabTextColors = colorStateList;
            updateAllTabs();
        }
    }

    @Nullable
    public ColorStateList getTabTextColors() {
        return this.mTabTextColors;
    }

    public void setTabTextColors(int i, int i2) {
        setTabTextColors(createColorStateList(i, i2));
    }

    public void setupWithViewPager(@Nullable ViewPager viewPager) {
        ViewPager viewPager2 = this.mViewPager;
        if (viewPager2 != null) {
            TabLayoutOnPageChangeListener tabLayoutOnPageChangeListener = this.mPageChangeListener;
            if (tabLayoutOnPageChangeListener != null) {
                viewPager2.removeOnPageChangeListener(tabLayoutOnPageChangeListener);
            }
        }
        if (viewPager != null) {
            PagerAdapter adapter = viewPager.getAdapter();
            if (adapter != null) {
                this.mViewPager = viewPager;
                if (this.mPageChangeListener == null) {
                    this.mPageChangeListener = new TabLayoutOnPageChangeListener(this);
                }
                this.mPageChangeListener.reset();
                viewPager.addOnPageChangeListener(this.mPageChangeListener);
                setOnTabSelectedListener(new ViewPagerOnTabSelectedListener(viewPager));
                setPagerAdapter(adapter, true);
                return;
            }
            throw new IllegalArgumentException("ViewPager does not have a PagerAdapter set");
        }
        this.mViewPager = null;
        setOnTabSelectedListener(null);
        setPagerAdapter(null, true);
    }

    @Deprecated
    public void setTabsFromPagerAdapter(@Nullable PagerAdapter pagerAdapter) {
        setPagerAdapter(pagerAdapter, false);
    }

    public boolean shouldDelayChildPressedState() {
        return getTabScrollRange() > 0;
    }

    private int getTabScrollRange() {
        return Math.max(0, ((this.mTabStrip.getWidth() - getWidth()) - getPaddingLeft()) - getPaddingRight());
    }

    private void setPagerAdapter(@Nullable PagerAdapter pagerAdapter, boolean z) {
        PagerAdapter pagerAdapter2 = this.mPagerAdapter;
        if (pagerAdapter2 != null) {
            DataSetObserver dataSetObserver = this.mPagerAdapterObserver;
            if (dataSetObserver != null) {
                pagerAdapter2.unregisterDataSetObserver(dataSetObserver);
            }
        }
        this.mPagerAdapter = pagerAdapter;
        if (z && pagerAdapter != null) {
            if (this.mPagerAdapterObserver == null) {
                this.mPagerAdapterObserver = new PagerAdapterObserver();
            }
            pagerAdapter.registerDataSetObserver(this.mPagerAdapterObserver);
        }
        populateFromPagerAdapter();
    }

    /* access modifiers changed from: private */
    public void populateFromPagerAdapter() {
        removeAllTabs();
        PagerAdapter pagerAdapter = this.mPagerAdapter;
        if (pagerAdapter != null) {
            int count = pagerAdapter.getCount();
            for (int i = 0; i < count; i++) {
                addTab(newTab().setText(this.mPagerAdapter.getPageTitle(i)), false);
            }
            ViewPager viewPager = this.mViewPager;
            if (viewPager != null && count > 0) {
                int currentItem = viewPager.getCurrentItem();
                if (currentItem != getSelectedTabPosition() && currentItem < getTabCount()) {
                    selectTab(getTabAt(currentItem));
                    return;
                }
                return;
            }
            return;
        }
        removeAllTabs();
    }

    private void updateAllTabs() {
        int size = this.mTabs.size();
        for (int i = 0; i < size; i++) {
            ((Tab) this.mTabs.get(i)).updateView();
        }
    }

    private TabView createTabView(@NonNull Tab tab) {
        Pool<TabView> pool = this.mTabViewPool;
        TabView tabView = pool != null ? (TabView) pool.acquire() : null;
        if (tabView == null) {
            tabView = new TabView(getContext());
        }
        tabView.setTab(tab);
        tabView.setFocusable(true);
        tabView.setMinimumWidth(getTabMinWidth());
        return tabView;
    }

    private void configureTab(Tab tab, int i) {
        tab.setPosition(i);
        this.mTabs.add(i, tab);
        int size = this.mTabs.size();
        while (true) {
            i++;
            if (i < size) {
                ((Tab) this.mTabs.get(i)).setPosition(i);
            } else {
                return;
            }
        }
    }

    private void addTabView(Tab tab, boolean z) {
        TabView access$200 = tab.mView;
        this.mTabStrip.addView(access$200, createLayoutParamsForTabs());
        if (z) {
            access$200.setSelected(true);
        }
    }

    private void addTabView(Tab tab, int i, boolean z) {
        TabView access$200 = tab.mView;
        this.mTabStrip.addView(access$200, i, createLayoutParamsForTabs());
        if (z) {
            access$200.setSelected(true);
        }
    }

    public void addView(View view) {
        addViewInternal(view);
    }

    public void addView(View view, int i) {
        addViewInternal(view);
    }

    public void addView(View view, ViewGroup.LayoutParams layoutParams) {
        addViewInternal(view);
    }

    public void addView(View view, int i, ViewGroup.LayoutParams layoutParams) {
        addViewInternal(view);
    }

    private void addViewInternal(View view) {
        if (view instanceof TabItem) {
            addTabFromItemView((TabItem) view);
            return;
        }
        throw new IllegalArgumentException("Only TabItem instances can be added to TabLayout");
    }

    private LayoutParams createLayoutParamsForTabs() {
        LayoutParams layoutParams = new LayoutParams(-2, -1);
        updateTabViewLayoutParams(layoutParams);
        return layoutParams;
    }

    private void updateTabViewLayoutParams(LayoutParams layoutParams) {
        if (this.mMode == 1 && this.mTabGravity == 0) {
            layoutParams.width = 0;
            layoutParams.weight = 1.0f;
            return;
        }
        layoutParams.width = -2;
        layoutParams.weight = 0.0f;
    }

    /* access modifiers changed from: private */
    public int dpToPx(int i) {
        return Math.round(getResources().getDisplayMetrics().density * ((float) i));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int dpToPx = dpToPx(getDefaultHeight()) + getPaddingTop() + getPaddingBottom();
        int mode = MeasureSpec.getMode(i2);
        if (mode == Integer.MIN_VALUE) {
            i2 = MeasureSpec.makeMeasureSpec(Math.min(dpToPx, MeasureSpec.getSize(i2)), 1073741824);
        } else if (mode == 0) {
            i2 = MeasureSpec.makeMeasureSpec(dpToPx, 1073741824);
        }
        int size = MeasureSpec.getSize(i);
        if (MeasureSpec.getMode(i) != 0) {
            int i3 = this.mRequestedTabMaxWidth;
            if (i3 <= 0) {
                i3 = size - dpToPx(56);
            }
            this.mTabMaxWidth = i3;
        }
        super.onMeasure(i, i2);
        if (getChildCount() == 1) {
            boolean z = false;
            View childAt = getChildAt(0);
            int i4 = this.mMode;
            if (i4 == 0 ? childAt.getMeasuredWidth() < getMeasuredWidth() : !(i4 != 1 || childAt.getMeasuredWidth() == getMeasuredWidth())) {
                z = true;
            }
            if (z) {
                childAt.measure(MeasureSpec.makeMeasureSpec(getMeasuredWidth(), 1073741824), getChildMeasureSpec(i2, getPaddingTop() + getPaddingBottom(), childAt.getLayoutParams().height));
            }
        }
    }

    private void removeTabViewAt(int i) {
        TabView tabView = (TabView) this.mTabStrip.getChildAt(i);
        this.mTabStrip.removeViewAt(i);
        if (tabView != null) {
            tabView.reset();
            this.mTabViewPool.release(tabView);
        }
        requestLayout();
    }

    private void animateToTab(int i) {
        if (i != -1) {
            if (getWindowToken() == null || !ViewCompat.isLaidOut(this) || this.mTabStrip.childrenNeedLayout()) {
                setScrollPosition(i, 0.0f, true);
                return;
            }
            int scrollX = getScrollX();
            int calculateScrollXForTab = calculateScrollXForTab(i, 0.0f);
            if (scrollX != calculateScrollXForTab) {
                if (this.mScrollAnimator == null) {
                    this.mScrollAnimator = ViewUtils.createAnimator();
                    this.mScrollAnimator.setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
                    this.mScrollAnimator.setDuration(ANIMATION_DURATION);
                    this.mScrollAnimator.setUpdateListener(new AnimatorUpdateListener() {
                        public void onAnimationUpdate(ValueAnimatorCompat valueAnimatorCompat) {
                            TabLayout.this.scrollTo(valueAnimatorCompat.getAnimatedIntValue(), 0);
                        }
                    });
                }
                this.mScrollAnimator.setIntValues(scrollX, calculateScrollXForTab);
                this.mScrollAnimator.start();
            }
            this.mTabStrip.animateIndicatorToPosition(i, ANIMATION_DURATION);
        }
    }

    private void setSelectedTabView(int i) {
        int childCount = this.mTabStrip.getChildCount();
        if (i < childCount && !this.mTabStrip.getChildAt(i).isSelected()) {
            int i2 = 0;
            while (i2 < childCount) {
                this.mTabStrip.getChildAt(i2).setSelected(i2 == i);
                i2++;
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void selectTab(Tab tab) {
        selectTab(tab, true);
    }

    /* access modifiers changed from: 0000 */
    public void selectTab(Tab tab, boolean z) {
        Tab tab2 = this.mSelectedTab;
        if (tab2 != tab) {
            if (z) {
                int position = tab != null ? tab.getPosition() : -1;
                if (position != -1) {
                    setSelectedTabView(position);
                }
                Tab tab3 = this.mSelectedTab;
                if ((tab3 == null || tab3.getPosition() == -1) && position != -1) {
                    setScrollPosition(position, 0.0f, true);
                } else {
                    animateToTab(position);
                }
            }
            Tab tab4 = this.mSelectedTab;
            if (tab4 != null) {
                OnTabSelectedListener onTabSelectedListener = this.mOnTabSelectedListener;
                if (onTabSelectedListener != null) {
                    onTabSelectedListener.onTabUnselected(tab4);
                }
            }
            this.mSelectedTab = tab;
            Tab tab5 = this.mSelectedTab;
            if (tab5 != null) {
                OnTabSelectedListener onTabSelectedListener2 = this.mOnTabSelectedListener;
                if (onTabSelectedListener2 != null) {
                    onTabSelectedListener2.onTabSelected(tab5);
                }
            }
        } else if (tab2 != null) {
            OnTabSelectedListener onTabSelectedListener3 = this.mOnTabSelectedListener;
            if (onTabSelectedListener3 != null) {
                onTabSelectedListener3.onTabReselected(tab2);
            }
            animateToTab(tab.getPosition());
        }
    }

    private int calculateScrollXForTab(int i, float f) {
        int i2 = 0;
        if (this.mMode != 0) {
            return 0;
        }
        View childAt = this.mTabStrip.getChildAt(i);
        int i3 = i + 1;
        View childAt2 = i3 < this.mTabStrip.getChildCount() ? this.mTabStrip.getChildAt(i3) : null;
        int width = childAt != null ? childAt.getWidth() : 0;
        if (childAt2 != null) {
            i2 = childAt2.getWidth();
        }
        return ((childAt.getLeft() + ((int) ((((float) (width + i2)) * f) * 0.5f))) + (childAt.getWidth() / 2)) - (getWidth() / 2);
    }

    private void applyModeAndGravity() {
        ViewCompat.setPaddingRelative(this.mTabStrip, this.mMode == 0 ? Math.max(0, this.mContentInsetStart - this.mTabPaddingStart) : 0, 0, 0, 0);
        int i = this.mMode;
        if (i == 0) {
            this.mTabStrip.setGravity(GravityCompat.START);
        } else if (i == 1) {
            this.mTabStrip.setGravity(1);
        }
        updateTabViews(true);
    }

    /* access modifiers changed from: private */
    public void updateTabViews(boolean z) {
        for (int i = 0; i < this.mTabStrip.getChildCount(); i++) {
            View childAt = this.mTabStrip.getChildAt(i);
            childAt.setMinimumWidth(getTabMinWidth());
            updateTabViewLayoutParams((LayoutParams) childAt.getLayoutParams());
            if (z) {
                childAt.requestLayout();
            }
        }
    }

    private static ColorStateList createColorStateList(int i, int i2) {
        return new ColorStateList(new int[][]{SELECTED_STATE_SET, EMPTY_STATE_SET}, new int[]{i2, i});
    }

    private int getDefaultHeight() {
        int size = this.mTabs.size();
        boolean z = false;
        int i = 0;
        while (true) {
            if (i >= size) {
                break;
            }
            Tab tab = (Tab) this.mTabs.get(i);
            if (tab != null && tab.getIcon() != null && !TextUtils.isEmpty(tab.getText())) {
                z = true;
                break;
            }
            i++;
        }
        return z ? 72 : 48;
    }

    private int getTabMinWidth() {
        int i = this.mRequestedTabMinWidth;
        if (i != -1) {
            return i;
        }
        return this.mMode == 0 ? this.mScrollableTabMinWidth : 0;
    }

    public FrameLayout.LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return generateDefaultLayoutParams();
    }

    /* access modifiers changed from: private */
    public int getTabMaxWidth() {
        return this.mTabMaxWidth;
    }
}
