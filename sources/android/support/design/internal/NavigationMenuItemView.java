package android.support.design.internal;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.ConstantState;
import android.graphics.drawable.StateListDrawable;
import android.support.design.C0007R;
import android.support.p000v4.graphics.drawable.DrawableCompat;
import android.support.p000v4.widget.TextViewCompat;
import android.support.p003v7.view.menu.MenuItemImpl;
import android.support.p003v7.view.menu.MenuView.ItemView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.CheckedTextView;
import android.widget.FrameLayout;

public class NavigationMenuItemView extends ForegroundLinearLayout implements ItemView {
    private static final int[] CHECKED_STATE_SET = {16842912};
    private FrameLayout mActionArea;
    private final int mIconSize;
    private ColorStateList mIconTintList;
    private MenuItemImpl mItemData;
    private final CheckedTextView mTextView;

    public boolean prefersCondensedTitle() {
        return false;
    }

    public void setShortcut(boolean z, char c) {
    }

    public boolean showsIcon() {
        return true;
    }

    public NavigationMenuItemView(Context context) {
        this(context, null);
    }

    public NavigationMenuItemView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public NavigationMenuItemView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setOrientation(0);
        LayoutInflater.from(context).inflate(C0007R.layout.design_navigation_menu_item, this, true);
        this.mIconSize = context.getResources().getDimensionPixelSize(C0007R.dimen.design_navigation_icon_size);
        this.mTextView = (CheckedTextView) findViewById(C0007R.C0009id.design_menu_item_text);
        this.mTextView.setDuplicateParentStateEnabled(true);
    }

    public void initialize(MenuItemImpl menuItemImpl, int i) {
        this.mItemData = menuItemImpl;
        setVisibility(menuItemImpl.isVisible() ? 0 : 8);
        if (getBackground() == null) {
            setBackgroundDrawable(createDefaultBackground());
        }
        setCheckable(menuItemImpl.isCheckable());
        setChecked(menuItemImpl.isChecked());
        setEnabled(menuItemImpl.isEnabled());
        setTitle(menuItemImpl.getTitle());
        setIcon(menuItemImpl.getIcon());
        setActionView(menuItemImpl.getActionView());
    }

    public void recycle() {
        FrameLayout frameLayout = this.mActionArea;
        if (frameLayout != null) {
            frameLayout.removeAllViews();
        }
        this.mTextView.setCompoundDrawables(null, null, null, null);
    }

    private void setActionView(View view) {
        if (this.mActionArea == null) {
            this.mActionArea = (FrameLayout) ((ViewStub) findViewById(C0007R.C0009id.design_menu_item_action_area_stub)).inflate();
        }
        this.mActionArea.removeAllViews();
        if (view != null) {
            this.mActionArea.addView(view);
        }
    }

    private StateListDrawable createDefaultBackground() {
        TypedValue typedValue = new TypedValue();
        if (!getContext().getTheme().resolveAttribute(C0007R.attr.colorControlHighlight, typedValue, true)) {
            return null;
        }
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(CHECKED_STATE_SET, new ColorDrawable(typedValue.data));
        stateListDrawable.addState(EMPTY_STATE_SET, new ColorDrawable(0));
        return stateListDrawable;
    }

    public MenuItemImpl getItemData() {
        return this.mItemData;
    }

    public void setTitle(CharSequence charSequence) {
        this.mTextView.setText(charSequence);
    }

    public void setCheckable(boolean z) {
        refreshDrawableState();
    }

    public void setChecked(boolean z) {
        refreshDrawableState();
        this.mTextView.setChecked(z);
    }

    public void setIcon(Drawable drawable) {
        if (drawable != null) {
            ConstantState constantState = drawable.getConstantState();
            if (constantState != null) {
                drawable = constantState.newDrawable();
            }
            drawable = DrawableCompat.wrap(drawable).mutate();
            int i = this.mIconSize;
            drawable.setBounds(0, 0, i, i);
            DrawableCompat.setTintList(drawable, this.mIconTintList);
        }
        TextViewCompat.setCompoundDrawablesRelative(this.mTextView, drawable, null, null, null);
    }

    /* access modifiers changed from: protected */
    public int[] onCreateDrawableState(int i) {
        int[] onCreateDrawableState = super.onCreateDrawableState(i + 1);
        MenuItemImpl menuItemImpl = this.mItemData;
        if (menuItemImpl != null && menuItemImpl.isCheckable() && this.mItemData.isChecked()) {
            mergeDrawableStates(onCreateDrawableState, CHECKED_STATE_SET);
        }
        return onCreateDrawableState;
    }

    /* access modifiers changed from: 0000 */
    public void setIconTintList(ColorStateList colorStateList) {
        this.mIconTintList = colorStateList;
        MenuItemImpl menuItemImpl = this.mItemData;
        if (menuItemImpl != null) {
            setIcon(menuItemImpl.getIcon());
        }
    }

    public void setTextAppearance(Context context, int i) {
        this.mTextView.setTextAppearance(context, i);
    }

    public void setTextColor(ColorStateList colorStateList) {
        this.mTextView.setTextColor(colorStateList);
    }
}
