package android.support.design.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.graphics.drawable.DrawableContainer.DrawableContainerState;
import android.graphics.drawable.InsetDrawable;
import android.os.Build.VERSION;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.design.C0007R;
import android.support.p000v4.content.ContextCompat;
import android.support.p000v4.graphics.drawable.DrawableWrapper;
import android.support.p000v4.view.AccessibilityDelegateCompat;
import android.support.p000v4.view.ViewCompat;
import android.support.p000v4.view.ViewPropertyAnimatorListenerAdapter;
import android.support.p000v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.p000v4.widget.Space;
import android.support.p003v7.widget.AppCompatDrawableManager;
import android.support.p003v7.widget.DrawableUtils;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.BaseSavedState;
import android.view.ViewGroup.LayoutParams;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.AccelerateInterpolator;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TextInputLayout extends LinearLayout {
    private static final int ANIMATION_DURATION = 200;
    private static final int INVALID_MAX_LENGTH = -1;
    private static final String LOG_TAG = "TextInputLayout";
    private ValueAnimatorCompat mAnimator;
    /* access modifiers changed from: private */
    public final CollapsingTextHelper mCollapsingTextHelper;
    /* access modifiers changed from: private */
    public boolean mCounterEnabled;
    private int mCounterMaxLength;
    private int mCounterOverflowTextAppearance;
    private boolean mCounterOverflowed;
    private int mCounterTextAppearance;
    private TextView mCounterView;
    private ColorStateList mDefaultTextColor;
    /* access modifiers changed from: private */
    public EditText mEditText;
    private CharSequence mError;
    private boolean mErrorEnabled;
    private boolean mErrorShown;
    private int mErrorTextAppearance;
    /* access modifiers changed from: private */
    public TextView mErrorView;
    private ColorStateList mFocusedTextColor;
    private boolean mHasReconstructedEditTextBackground;
    private CharSequence mHint;
    private boolean mHintAnimationEnabled;
    private boolean mHintEnabled;
    private LinearLayout mIndicatorArea;
    private int mIndicatorsAdded;
    private Paint mTmpPaint;

    static class SavedState extends BaseSavedState {
        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        CharSequence error;

        SavedState(Parcelable parcelable) {
            super(parcelable);
        }

        public SavedState(Parcel parcel) {
            super(parcel);
            this.error = (CharSequence) TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
        }

        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            TextUtils.writeToParcel(this.error, parcel, i);
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("TextInputLayout.SavedState{");
            sb.append(Integer.toHexString(System.identityHashCode(this)));
            sb.append(" error=");
            sb.append(this.error);
            sb.append("}");
            return sb.toString();
        }
    }

    private class TextInputAccessibilityDelegate extends AccessibilityDelegateCompat {
        private TextInputAccessibilityDelegate() {
        }

        public void onInitializeAccessibilityEvent(View view, AccessibilityEvent accessibilityEvent) {
            super.onInitializeAccessibilityEvent(view, accessibilityEvent);
            accessibilityEvent.setClassName(TextInputLayout.class.getSimpleName());
        }

        public void onPopulateAccessibilityEvent(View view, AccessibilityEvent accessibilityEvent) {
            super.onPopulateAccessibilityEvent(view, accessibilityEvent);
            CharSequence text = TextInputLayout.this.mCollapsingTextHelper.getText();
            if (!TextUtils.isEmpty(text)) {
                accessibilityEvent.getText().add(text);
            }
        }

        public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfoCompat);
            accessibilityNodeInfoCompat.setClassName(TextInputLayout.class.getSimpleName());
            CharSequence text = TextInputLayout.this.mCollapsingTextHelper.getText();
            if (!TextUtils.isEmpty(text)) {
                accessibilityNodeInfoCompat.setText(text);
            }
            if (TextInputLayout.this.mEditText != null) {
                accessibilityNodeInfoCompat.setLabelFor(TextInputLayout.this.mEditText);
            }
            CharSequence text2 = TextInputLayout.this.mErrorView != null ? TextInputLayout.this.mErrorView.getText() : null;
            if (!TextUtils.isEmpty(text2)) {
                accessibilityNodeInfoCompat.setContentInvalid(true);
                accessibilityNodeInfoCompat.setError(text2);
            }
        }
    }

    public TextInputLayout(Context context) {
        this(context, null);
    }

    public TextInputLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public TextInputLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet);
        this.mCollapsingTextHelper = new CollapsingTextHelper(this);
        ThemeUtils.checkAppCompatTheme(context);
        setOrientation(1);
        setWillNotDraw(false);
        setAddStatesFromChildren(true);
        this.mCollapsingTextHelper.setTextSizeInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
        this.mCollapsingTextHelper.setPositionInterpolator(new AccelerateInterpolator());
        this.mCollapsingTextHelper.setCollapsedTextGravity(8388659);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, C0007R.styleable.TextInputLayout, i, C0007R.style.Widget_Design_TextInputLayout);
        this.mHintEnabled = obtainStyledAttributes.getBoolean(C0007R.styleable.TextInputLayout_hintEnabled, true);
        setHint(obtainStyledAttributes.getText(C0007R.styleable.TextInputLayout_android_hint));
        this.mHintAnimationEnabled = obtainStyledAttributes.getBoolean(C0007R.styleable.TextInputLayout_hintAnimationEnabled, true);
        if (obtainStyledAttributes.hasValue(C0007R.styleable.TextInputLayout_android_textColorHint)) {
            ColorStateList colorStateList = obtainStyledAttributes.getColorStateList(C0007R.styleable.TextInputLayout_android_textColorHint);
            this.mFocusedTextColor = colorStateList;
            this.mDefaultTextColor = colorStateList;
        }
        if (obtainStyledAttributes.getResourceId(C0007R.styleable.TextInputLayout_hintTextAppearance, -1) != -1) {
            setHintTextAppearance(obtainStyledAttributes.getResourceId(C0007R.styleable.TextInputLayout_hintTextAppearance, 0));
        }
        this.mErrorTextAppearance = obtainStyledAttributes.getResourceId(C0007R.styleable.TextInputLayout_errorTextAppearance, 0);
        boolean z = obtainStyledAttributes.getBoolean(C0007R.styleable.TextInputLayout_errorEnabled, false);
        boolean z2 = obtainStyledAttributes.getBoolean(C0007R.styleable.TextInputLayout_counterEnabled, false);
        setCounterMaxLength(obtainStyledAttributes.getInt(C0007R.styleable.TextInputLayout_counterMaxLength, -1));
        this.mCounterTextAppearance = obtainStyledAttributes.getResourceId(C0007R.styleable.TextInputLayout_counterTextAppearance, 0);
        this.mCounterOverflowTextAppearance = obtainStyledAttributes.getResourceId(C0007R.styleable.TextInputLayout_counterOverflowTextAppearance, 0);
        obtainStyledAttributes.recycle();
        setErrorEnabled(z);
        setCounterEnabled(z2);
        if (ViewCompat.getImportantForAccessibility(this) == 0) {
            ViewCompat.setImportantForAccessibility(this, 1);
        }
        ViewCompat.setAccessibilityDelegate(this, new TextInputAccessibilityDelegate());
    }

    public void addView(View view, int i, LayoutParams layoutParams) {
        if (view instanceof EditText) {
            setEditText((EditText) view);
            super.addView(view, 0, updateEditTextMargin(layoutParams));
            return;
        }
        super.addView(view, i, layoutParams);
    }

    public void setTypeface(@Nullable Typeface typeface) {
        this.mCollapsingTextHelper.setTypefaces(typeface);
    }

    @NonNull
    public Typeface getTypeface() {
        return this.mCollapsingTextHelper.getCollapsedTypeface();
    }

    private void setEditText(EditText editText) {
        if (this.mEditText == null) {
            if (!(editText instanceof TextInputEditText)) {
                Log.i(LOG_TAG, "EditText added is not a TextInputEditText. Please switch to using that class instead.");
            }
            this.mEditText = editText;
            this.mCollapsingTextHelper.setTypefaces(this.mEditText.getTypeface());
            this.mCollapsingTextHelper.setExpandedTextSize(this.mEditText.getTextSize());
            int gravity = this.mEditText.getGravity();
            this.mCollapsingTextHelper.setCollapsedTextGravity((8388615 & gravity) | 48);
            this.mCollapsingTextHelper.setExpandedTextGravity(gravity);
            this.mEditText.addTextChangedListener(new TextWatcher() {
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void afterTextChanged(Editable editable) {
                    TextInputLayout.this.updateLabelState(true);
                    if (TextInputLayout.this.mCounterEnabled) {
                        TextInputLayout.this.updateCounter(editable.length());
                    }
                }
            });
            if (this.mDefaultTextColor == null) {
                this.mDefaultTextColor = this.mEditText.getHintTextColors();
            }
            if (this.mHintEnabled && TextUtils.isEmpty(this.mHint)) {
                setHint(this.mEditText.getHint());
                this.mEditText.setHint(null);
            }
            if (this.mCounterView != null) {
                updateCounter(this.mEditText.getText().length());
            }
            if (this.mIndicatorArea != null) {
                adjustIndicatorPadding();
            }
            updateLabelState(false);
            return;
        }
        throw new IllegalArgumentException("We already have an EditText, can only have one");
    }

    private LinearLayout.LayoutParams updateEditTextMargin(LayoutParams layoutParams) {
        LinearLayout.LayoutParams layoutParams2 = layoutParams instanceof LinearLayout.LayoutParams ? (LinearLayout.LayoutParams) layoutParams : new LinearLayout.LayoutParams(layoutParams);
        if (this.mHintEnabled) {
            if (this.mTmpPaint == null) {
                this.mTmpPaint = new Paint();
            }
            this.mTmpPaint.setTypeface(this.mCollapsingTextHelper.getCollapsedTypeface());
            this.mTmpPaint.setTextSize(this.mCollapsingTextHelper.getCollapsedTextSize());
            layoutParams2.topMargin = (int) (-this.mTmpPaint.ascent());
        } else {
            layoutParams2.topMargin = 0;
        }
        return layoutParams2;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0064 A[ADDED_TO_REGION] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateLabelState(boolean r6) {
        /*
            r5 = this;
            android.widget.EditText r0 = r5.mEditText
            r1 = 1
            if (r0 == 0) goto L_0x0011
            android.text.Editable r0 = r0.getText()
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0011
            r0 = 1
            goto L_0x0012
        L_0x0011:
            r0 = 0
        L_0x0012:
            int[] r2 = r5.getDrawableState()
            r3 = 16842908(0x101009c, float:2.3693995E-38)
            boolean r2 = arrayContains(r2, r3)
            java.lang.CharSequence r3 = r5.getError()
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            r1 = r1 ^ r3
            android.content.res.ColorStateList r3 = r5.mDefaultTextColor
            if (r3 == 0) goto L_0x0033
            android.support.design.widget.CollapsingTextHelper r4 = r5.mCollapsingTextHelper
            int r3 = r3.getDefaultColor()
            r4.setExpandedTextColor(r3)
        L_0x0033:
            boolean r3 = r5.mCounterOverflowed
            if (r3 == 0) goto L_0x0045
            android.widget.TextView r3 = r5.mCounterView
            if (r3 == 0) goto L_0x0045
            android.support.design.widget.CollapsingTextHelper r4 = r5.mCollapsingTextHelper
            int r3 = r3.getCurrentTextColor()
            r4.setCollapsedTextColor(r3)
            goto L_0x0062
        L_0x0045:
            if (r2 == 0) goto L_0x0055
            android.content.res.ColorStateList r3 = r5.mFocusedTextColor
            if (r3 == 0) goto L_0x0055
            android.support.design.widget.CollapsingTextHelper r4 = r5.mCollapsingTextHelper
            int r3 = r3.getDefaultColor()
            r4.setCollapsedTextColor(r3)
            goto L_0x0062
        L_0x0055:
            android.content.res.ColorStateList r3 = r5.mDefaultTextColor
            if (r3 == 0) goto L_0x0062
            android.support.design.widget.CollapsingTextHelper r4 = r5.mCollapsingTextHelper
            int r3 = r3.getDefaultColor()
            r4.setCollapsedTextColor(r3)
        L_0x0062:
            if (r0 != 0) goto L_0x006d
            if (r2 != 0) goto L_0x006d
            if (r1 == 0) goto L_0x0069
            goto L_0x006d
        L_0x0069:
            r5.expandHint(r6)
            goto L_0x0070
        L_0x006d:
            r5.collapseHint(r6)
        L_0x0070:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.design.widget.TextInputLayout.updateLabelState(boolean):void");
    }

    @Nullable
    public EditText getEditText() {
        return this.mEditText;
    }

    public void setHint(@Nullable CharSequence charSequence) {
        if (this.mHintEnabled) {
            setHintInternal(charSequence);
            sendAccessibilityEvent(2048);
        }
    }

    private void setHintInternal(CharSequence charSequence) {
        this.mHint = charSequence;
        this.mCollapsingTextHelper.setText(charSequence);
    }

    @Nullable
    public CharSequence getHint() {
        if (this.mHintEnabled) {
            return this.mHint;
        }
        return null;
    }

    public void setHintEnabled(boolean z) {
        if (z != this.mHintEnabled) {
            this.mHintEnabled = z;
            CharSequence hint = this.mEditText.getHint();
            if (!this.mHintEnabled) {
                if (!TextUtils.isEmpty(this.mHint) && TextUtils.isEmpty(hint)) {
                    this.mEditText.setHint(this.mHint);
                }
                setHintInternal(null);
            } else if (!TextUtils.isEmpty(hint)) {
                if (TextUtils.isEmpty(this.mHint)) {
                    setHint(hint);
                }
                this.mEditText.setHint(null);
            }
            EditText editText = this.mEditText;
            if (editText != null) {
                this.mEditText.setLayoutParams(updateEditTextMargin(editText.getLayoutParams()));
            }
        }
    }

    public boolean isHintEnabled() {
        return this.mHintEnabled;
    }

    public void setHintTextAppearance(@StyleRes int i) {
        this.mCollapsingTextHelper.setCollapsedTextAppearance(i);
        this.mFocusedTextColor = ColorStateList.valueOf(this.mCollapsingTextHelper.getCollapsedTextColor());
        if (this.mEditText != null) {
            updateLabelState(false);
            this.mEditText.setLayoutParams(updateEditTextMargin(this.mEditText.getLayoutParams()));
            this.mEditText.requestLayout();
        }
    }

    private void addIndicator(TextView textView, int i) {
        if (this.mIndicatorArea == null) {
            this.mIndicatorArea = new LinearLayout(getContext());
            this.mIndicatorArea.setOrientation(0);
            addView(this.mIndicatorArea, -1, -2);
            this.mIndicatorArea.addView(new Space(getContext()), new LinearLayout.LayoutParams(0, 0, 1.0f));
            if (this.mEditText != null) {
                adjustIndicatorPadding();
            }
        }
        this.mIndicatorArea.setVisibility(0);
        this.mIndicatorArea.addView(textView, i);
        this.mIndicatorsAdded++;
    }

    private void adjustIndicatorPadding() {
        ViewCompat.setPaddingRelative(this.mIndicatorArea, ViewCompat.getPaddingStart(this.mEditText), 0, ViewCompat.getPaddingEnd(this.mEditText), this.mEditText.getPaddingBottom());
    }

    private void removeIndicator(TextView textView) {
        LinearLayout linearLayout = this.mIndicatorArea;
        if (linearLayout != null) {
            linearLayout.removeView(textView);
            int i = this.mIndicatorsAdded - 1;
            this.mIndicatorsAdded = i;
            if (i == 0) {
                this.mIndicatorArea.setVisibility(8);
            }
        }
    }

    public void setErrorEnabled(boolean z) {
        if (this.mErrorEnabled != z) {
            TextView textView = this.mErrorView;
            if (textView != null) {
                ViewCompat.animate(textView).cancel();
            }
            if (z) {
                this.mErrorView = new TextView(getContext());
                try {
                    this.mErrorView.setTextAppearance(getContext(), this.mErrorTextAppearance);
                } catch (Exception unused) {
                    this.mErrorView.setTextAppearance(getContext(), C0007R.style.TextAppearance_AppCompat_Caption);
                    this.mErrorView.setTextColor(ContextCompat.getColor(getContext(), C0007R.color.design_textinput_error_color_light));
                }
                this.mErrorView.setVisibility(4);
                ViewCompat.setAccessibilityLiveRegion(this.mErrorView, 1);
                addIndicator(this.mErrorView, 0);
            } else {
                this.mErrorShown = false;
                updateEditTextBackground();
                removeIndicator(this.mErrorView);
                this.mErrorView = null;
            }
            this.mErrorEnabled = z;
        }
    }

    public boolean isErrorEnabled() {
        return this.mErrorEnabled;
    }

    public void setError(@Nullable final CharSequence charSequence) {
        if (!TextUtils.equals(this.mError, charSequence)) {
            this.mError = charSequence;
            if (!this.mErrorEnabled) {
                if (!TextUtils.isEmpty(charSequence)) {
                    setErrorEnabled(true);
                } else {
                    return;
                }
            }
            boolean isLaidOut = ViewCompat.isLaidOut(this);
            this.mErrorShown = !TextUtils.isEmpty(charSequence);
            ViewCompat.animate(this.mErrorView).cancel();
            if (this.mErrorShown) {
                this.mErrorView.setText(charSequence);
                this.mErrorView.setVisibility(0);
                if (isLaidOut) {
                    if (ViewCompat.getAlpha(this.mErrorView) == 1.0f) {
                        ViewCompat.setAlpha(this.mErrorView, 0.0f);
                    }
                    ViewCompat.animate(this.mErrorView).alpha(1.0f).setDuration(200).setInterpolator(AnimationUtils.LINEAR_OUT_SLOW_IN_INTERPOLATOR).setListener(new ViewPropertyAnimatorListenerAdapter() {
                        public void onAnimationStart(View view) {
                            view.setVisibility(0);
                        }
                    }).start();
                }
            } else if (this.mErrorView.getVisibility() == 0) {
                if (isLaidOut) {
                    ViewCompat.animate(this.mErrorView).alpha(0.0f).setDuration(200).setInterpolator(AnimationUtils.FAST_OUT_LINEAR_IN_INTERPOLATOR).setListener(new ViewPropertyAnimatorListenerAdapter() {
                        public void onAnimationEnd(View view) {
                            TextInputLayout.this.mErrorView.setText(charSequence);
                            view.setVisibility(4);
                        }
                    }).start();
                } else {
                    this.mErrorView.setVisibility(4);
                }
            }
            updateEditTextBackground();
            updateLabelState(true);
        }
    }

    public void setCounterEnabled(boolean z) {
        if (this.mCounterEnabled != z) {
            if (z) {
                this.mCounterView = new TextView(getContext());
                this.mCounterView.setMaxLines(1);
                try {
                    this.mCounterView.setTextAppearance(getContext(), this.mCounterTextAppearance);
                } catch (Exception unused) {
                    this.mCounterView.setTextAppearance(getContext(), C0007R.style.TextAppearance_AppCompat_Caption);
                    this.mCounterView.setTextColor(ContextCompat.getColor(getContext(), C0007R.color.design_textinput_error_color_light));
                }
                addIndicator(this.mCounterView, -1);
                EditText editText = this.mEditText;
                if (editText == null) {
                    updateCounter(0);
                } else {
                    updateCounter(editText.getText().length());
                }
            } else {
                removeIndicator(this.mCounterView);
                this.mCounterView = null;
            }
            this.mCounterEnabled = z;
        }
    }

    public boolean isCounterEnabled() {
        return this.mCounterEnabled;
    }

    public void setCounterMaxLength(int i) {
        if (this.mCounterMaxLength != i) {
            if (i > 0) {
                this.mCounterMaxLength = i;
            } else {
                this.mCounterMaxLength = -1;
            }
            if (this.mCounterEnabled) {
                EditText editText = this.mEditText;
                updateCounter(editText == null ? 0 : editText.getText().length());
            }
        }
    }

    public int getCounterMaxLength() {
        return this.mCounterMaxLength;
    }

    /* access modifiers changed from: private */
    public void updateCounter(int i) {
        boolean z = this.mCounterOverflowed;
        int i2 = this.mCounterMaxLength;
        if (i2 == -1) {
            this.mCounterView.setText(String.valueOf(i));
            this.mCounterOverflowed = false;
        } else {
            this.mCounterOverflowed = i > i2;
            if (z != this.mCounterOverflowed) {
                this.mCounterView.setTextAppearance(getContext(), this.mCounterOverflowed ? this.mCounterOverflowTextAppearance : this.mCounterTextAppearance);
            }
            this.mCounterView.setText(getContext().getString(C0007R.string.character_counter_pattern, new Object[]{Integer.valueOf(i), Integer.valueOf(this.mCounterMaxLength)}));
        }
        if (this.mEditText != null && z != this.mCounterOverflowed) {
            updateLabelState(false);
            updateEditTextBackground();
        }
    }

    private void updateEditTextBackground() {
        ensureBackgroundDrawableStateWorkaround();
        Drawable background = this.mEditText.getBackground();
        if (background != null) {
            if (DrawableUtils.canSafelyMutateDrawable(background)) {
                background = background.mutate();
            }
            if (this.mErrorShown) {
                TextView textView = this.mErrorView;
                if (textView != null) {
                    background.setColorFilter(AppCompatDrawableManager.getPorterDuffColorFilter(textView.getCurrentTextColor(), Mode.SRC_IN));
                }
            }
            if (this.mCounterOverflowed) {
                TextView textView2 = this.mCounterView;
                if (textView2 != null) {
                    background.setColorFilter(AppCompatDrawableManager.getPorterDuffColorFilter(textView2.getCurrentTextColor(), Mode.SRC_IN));
                }
            }
            clearColorFilter(background);
            this.mEditText.refreshDrawableState();
        }
    }

    private static void clearColorFilter(@NonNull Drawable drawable) {
        drawable.clearColorFilter();
        if (VERSION.SDK_INT != 21 && VERSION.SDK_INT != 22) {
            return;
        }
        if (drawable instanceof InsetDrawable) {
            clearColorFilter(((InsetDrawable) drawable).getDrawable());
        } else if (drawable instanceof DrawableWrapper) {
            clearColorFilter(((DrawableWrapper) drawable).getWrappedDrawable());
        } else if (drawable instanceof DrawableContainer) {
            DrawableContainerState drawableContainerState = (DrawableContainerState) ((DrawableContainer) drawable).getConstantState();
            if (drawableContainerState != null) {
                int childCount = drawableContainerState.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    clearColorFilter(drawableContainerState.getChild(i));
                }
            }
        }
    }

    private void ensureBackgroundDrawableStateWorkaround() {
        int i = VERSION.SDK_INT;
        if (i == 21 || i == 22) {
            Drawable background = this.mEditText.getBackground();
            if (background != null && !this.mHasReconstructedEditTextBackground) {
                Drawable newDrawable = background.getConstantState().newDrawable();
                if (background instanceof DrawableContainer) {
                    this.mHasReconstructedEditTextBackground = DrawableUtils.setContainerConstantState((DrawableContainer) background, newDrawable.getConstantState());
                }
                if (!this.mHasReconstructedEditTextBackground) {
                    this.mEditText.setBackgroundDrawable(newDrawable);
                    this.mHasReconstructedEditTextBackground = true;
                }
            }
        }
    }

    public Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        if (this.mErrorShown) {
            savedState.error = getError();
        }
        return savedState;
    }

    /* access modifiers changed from: protected */
    public void onRestoreInstanceState(Parcelable parcelable) {
        if (!(parcelable instanceof SavedState)) {
            super.onRestoreInstanceState(parcelable);
            return;
        }
        SavedState savedState = (SavedState) parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        setError(savedState.error);
        requestLayout();
    }

    @Nullable
    public CharSequence getError() {
        if (this.mErrorEnabled) {
            return this.mError;
        }
        return null;
    }

    public boolean isHintAnimationEnabled() {
        return this.mHintAnimationEnabled;
    }

    public void setHintAnimationEnabled(boolean z) {
        this.mHintAnimationEnabled = z;
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (this.mHintEnabled) {
            this.mCollapsingTextHelper.draw(canvas);
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        if (this.mHintEnabled) {
            EditText editText = this.mEditText;
            if (editText != null) {
                int left = editText.getLeft() + this.mEditText.getCompoundPaddingLeft();
                int right = this.mEditText.getRight() - this.mEditText.getCompoundPaddingRight();
                this.mCollapsingTextHelper.setExpandedBounds(left, this.mEditText.getTop() + this.mEditText.getCompoundPaddingTop(), right, this.mEditText.getBottom() - this.mEditText.getCompoundPaddingBottom());
                this.mCollapsingTextHelper.setCollapsedBounds(left, getPaddingTop(), right, (i4 - i2) - getPaddingBottom());
                this.mCollapsingTextHelper.recalculate();
            }
        }
    }

    public void refreshDrawableState() {
        super.refreshDrawableState();
        updateLabelState(ViewCompat.isLaidOut(this));
    }

    private void collapseHint(boolean z) {
        ValueAnimatorCompat valueAnimatorCompat = this.mAnimator;
        if (valueAnimatorCompat != null && valueAnimatorCompat.isRunning()) {
            this.mAnimator.cancel();
        }
        if (!z || !this.mHintAnimationEnabled) {
            this.mCollapsingTextHelper.setExpansionFraction(1.0f);
        } else {
            animateToExpansionFraction(1.0f);
        }
    }

    private void expandHint(boolean z) {
        ValueAnimatorCompat valueAnimatorCompat = this.mAnimator;
        if (valueAnimatorCompat != null && valueAnimatorCompat.isRunning()) {
            this.mAnimator.cancel();
        }
        if (!z || !this.mHintAnimationEnabled) {
            this.mCollapsingTextHelper.setExpansionFraction(0.0f);
        } else {
            animateToExpansionFraction(0.0f);
        }
    }

    private void animateToExpansionFraction(float f) {
        if (this.mCollapsingTextHelper.getExpansionFraction() != f) {
            if (this.mAnimator == null) {
                this.mAnimator = ViewUtils.createAnimator();
                this.mAnimator.setInterpolator(AnimationUtils.LINEAR_INTERPOLATOR);
                this.mAnimator.setDuration(200);
                this.mAnimator.setUpdateListener(new AnimatorUpdateListener() {
                    public void onAnimationUpdate(ValueAnimatorCompat valueAnimatorCompat) {
                        TextInputLayout.this.mCollapsingTextHelper.setExpansionFraction(valueAnimatorCompat.getAnimatedFloatValue());
                    }
                });
            }
            this.mAnimator.setFloatValues(this.mCollapsingTextHelper.getExpansionFraction(), f);
            this.mAnimator.start();
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
}
