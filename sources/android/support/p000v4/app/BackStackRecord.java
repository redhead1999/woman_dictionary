package android.support.p000v4.app;

import android.os.Build.VERSION;
import android.support.p000v4.app.FragmentManager.BackStackEntry;
import android.support.p000v4.app.FragmentTransitionCompat21.EpicenterView;
import android.support.p000v4.app.FragmentTransitionCompat21.ViewRetriever;
import android.support.p000v4.util.ArrayMap;
import android.support.p000v4.util.LogWriter;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;

/* renamed from: android.support.v4.app.BackStackRecord */
final class BackStackRecord extends FragmentTransaction implements BackStackEntry, Runnable {
    static final int OP_ADD = 1;
    static final int OP_ATTACH = 7;
    static final int OP_DETACH = 6;
    static final int OP_HIDE = 4;
    static final int OP_NULL = 0;
    static final int OP_REMOVE = 3;
    static final int OP_REPLACE = 2;
    static final int OP_SHOW = 5;
    static final boolean SUPPORTS_TRANSITIONS = (VERSION.SDK_INT >= 21);
    static final String TAG = "FragmentManager";
    boolean mAddToBackStack;
    boolean mAllowAddToBackStack = true;
    int mBreadCrumbShortTitleRes;
    CharSequence mBreadCrumbShortTitleText;
    int mBreadCrumbTitleRes;
    CharSequence mBreadCrumbTitleText;
    boolean mCommitted;
    int mEnterAnim;
    int mExitAnim;
    C0076Op mHead;
    int mIndex = -1;
    final FragmentManagerImpl mManager;
    String mName;
    int mNumOp;
    int mPopEnterAnim;
    int mPopExitAnim;
    ArrayList<String> mSharedElementSourceNames;
    ArrayList<String> mSharedElementTargetNames;
    C0076Op mTail;
    int mTransition;
    int mTransitionStyle;

    /* renamed from: android.support.v4.app.BackStackRecord$Op */
    static final class C0076Op {
        int cmd;
        int enterAnim;
        int exitAnim;
        Fragment fragment;
        C0076Op next;
        int popEnterAnim;
        int popExitAnim;
        C0076Op prev;
        ArrayList<Fragment> removed;

        C0076Op() {
        }
    }

    /* renamed from: android.support.v4.app.BackStackRecord$TransitionState */
    public class TransitionState {
        public EpicenterView enteringEpicenterView = new EpicenterView();
        public ArrayList<View> hiddenFragmentViews = new ArrayList<>();
        public ArrayMap<String, String> nameOverrides = new ArrayMap<>();
        public View nonExistentView;

        public TransitionState() {
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(128);
        sb.append("BackStackEntry{");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        if (this.mIndex >= 0) {
            sb.append(" #");
            sb.append(this.mIndex);
        }
        if (this.mName != null) {
            sb.append(" ");
            sb.append(this.mName);
        }
        sb.append("}");
        return sb.toString();
    }

    public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        dump(str, printWriter, true);
    }

    public void dump(String str, PrintWriter printWriter, boolean z) {
        String str2;
        if (z) {
            printWriter.print(str);
            printWriter.print("mName=");
            printWriter.print(this.mName);
            printWriter.print(" mIndex=");
            printWriter.print(this.mIndex);
            printWriter.print(" mCommitted=");
            printWriter.println(this.mCommitted);
            if (this.mTransition != 0) {
                printWriter.print(str);
                printWriter.print("mTransition=#");
                printWriter.print(Integer.toHexString(this.mTransition));
                printWriter.print(" mTransitionStyle=#");
                printWriter.println(Integer.toHexString(this.mTransitionStyle));
            }
            if (!(this.mEnterAnim == 0 && this.mExitAnim == 0)) {
                printWriter.print(str);
                printWriter.print("mEnterAnim=#");
                printWriter.print(Integer.toHexString(this.mEnterAnim));
                printWriter.print(" mExitAnim=#");
                printWriter.println(Integer.toHexString(this.mExitAnim));
            }
            if (!(this.mPopEnterAnim == 0 && this.mPopExitAnim == 0)) {
                printWriter.print(str);
                printWriter.print("mPopEnterAnim=#");
                printWriter.print(Integer.toHexString(this.mPopEnterAnim));
                printWriter.print(" mPopExitAnim=#");
                printWriter.println(Integer.toHexString(this.mPopExitAnim));
            }
            if (!(this.mBreadCrumbTitleRes == 0 && this.mBreadCrumbTitleText == null)) {
                printWriter.print(str);
                printWriter.print("mBreadCrumbTitleRes=#");
                printWriter.print(Integer.toHexString(this.mBreadCrumbTitleRes));
                printWriter.print(" mBreadCrumbTitleText=");
                printWriter.println(this.mBreadCrumbTitleText);
            }
            if (!(this.mBreadCrumbShortTitleRes == 0 && this.mBreadCrumbShortTitleText == null)) {
                printWriter.print(str);
                printWriter.print("mBreadCrumbShortTitleRes=#");
                printWriter.print(Integer.toHexString(this.mBreadCrumbShortTitleRes));
                printWriter.print(" mBreadCrumbShortTitleText=");
                printWriter.println(this.mBreadCrumbShortTitleText);
            }
        }
        if (this.mHead != null) {
            printWriter.print(str);
            printWriter.println("Operations:");
            StringBuilder sb = new StringBuilder();
            sb.append(str);
            sb.append("    ");
            String sb2 = sb.toString();
            C0076Op op = this.mHead;
            int i = 0;
            while (op != null) {
                switch (op.cmd) {
                    case 0:
                        str2 = "NULL";
                        break;
                    case 1:
                        str2 = "ADD";
                        break;
                    case 2:
                        str2 = "REPLACE";
                        break;
                    case 3:
                        str2 = "REMOVE";
                        break;
                    case 4:
                        str2 = "HIDE";
                        break;
                    case 5:
                        str2 = "SHOW";
                        break;
                    case 6:
                        str2 = "DETACH";
                        break;
                    case 7:
                        str2 = "ATTACH";
                        break;
                    default:
                        StringBuilder sb3 = new StringBuilder();
                        sb3.append("cmd=");
                        sb3.append(op.cmd);
                        str2 = sb3.toString();
                        break;
                }
                printWriter.print(str);
                printWriter.print("  Op #");
                printWriter.print(i);
                String str3 = ": ";
                printWriter.print(str3);
                printWriter.print(str2);
                printWriter.print(" ");
                printWriter.println(op.fragment);
                if (z) {
                    if (!(op.enterAnim == 0 && op.exitAnim == 0)) {
                        printWriter.print(str);
                        printWriter.print("enterAnim=#");
                        printWriter.print(Integer.toHexString(op.enterAnim));
                        printWriter.print(" exitAnim=#");
                        printWriter.println(Integer.toHexString(op.exitAnim));
                    }
                    if (!(op.popEnterAnim == 0 && op.popExitAnim == 0)) {
                        printWriter.print(str);
                        printWriter.print("popEnterAnim=#");
                        printWriter.print(Integer.toHexString(op.popEnterAnim));
                        printWriter.print(" popExitAnim=#");
                        printWriter.println(Integer.toHexString(op.popExitAnim));
                    }
                }
                if (op.removed != null && op.removed.size() > 0) {
                    for (int i2 = 0; i2 < op.removed.size(); i2++) {
                        printWriter.print(sb2);
                        if (op.removed.size() == 1) {
                            printWriter.print("Removed: ");
                        } else {
                            if (i2 == 0) {
                                printWriter.println("Removed:");
                            }
                            printWriter.print(sb2);
                            printWriter.print("  #");
                            printWriter.print(i2);
                            printWriter.print(str3);
                        }
                        printWriter.println(op.removed.get(i2));
                    }
                }
                op = op.next;
                i++;
            }
        }
    }

    public BackStackRecord(FragmentManagerImpl fragmentManagerImpl) {
        this.mManager = fragmentManagerImpl;
    }

    public int getId() {
        return this.mIndex;
    }

    public int getBreadCrumbTitleRes() {
        return this.mBreadCrumbTitleRes;
    }

    public int getBreadCrumbShortTitleRes() {
        return this.mBreadCrumbShortTitleRes;
    }

    public CharSequence getBreadCrumbTitle() {
        if (this.mBreadCrumbTitleRes != 0) {
            return this.mManager.mHost.getContext().getText(this.mBreadCrumbTitleRes);
        }
        return this.mBreadCrumbTitleText;
    }

    public CharSequence getBreadCrumbShortTitle() {
        if (this.mBreadCrumbShortTitleRes != 0) {
            return this.mManager.mHost.getContext().getText(this.mBreadCrumbShortTitleRes);
        }
        return this.mBreadCrumbShortTitleText;
    }

    /* access modifiers changed from: 0000 */
    public void addOp(C0076Op op) {
        if (this.mHead == null) {
            this.mTail = op;
            this.mHead = op;
        } else {
            C0076Op op2 = this.mTail;
            op.prev = op2;
            op2.next = op;
            this.mTail = op;
        }
        op.enterAnim = this.mEnterAnim;
        op.exitAnim = this.mExitAnim;
        op.popEnterAnim = this.mPopEnterAnim;
        op.popExitAnim = this.mPopExitAnim;
        this.mNumOp++;
    }

    public FragmentTransaction add(Fragment fragment, String str) {
        doAddOp(0, fragment, str, 1);
        return this;
    }

    public FragmentTransaction add(int i, Fragment fragment) {
        doAddOp(i, fragment, null, 1);
        return this;
    }

    public FragmentTransaction add(int i, Fragment fragment, String str) {
        doAddOp(i, fragment, str, 1);
        return this;
    }

    private void doAddOp(int i, Fragment fragment, String str, int i2) {
        fragment.mFragmentManager = this.mManager;
        String str2 = " now ";
        String str3 = ": was ";
        if (str != null) {
            if (fragment.mTag == null || str.equals(fragment.mTag)) {
                fragment.mTag = str;
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("Can't change tag of fragment ");
                sb.append(fragment);
                sb.append(str3);
                sb.append(fragment.mTag);
                sb.append(str2);
                sb.append(str);
                throw new IllegalStateException(sb.toString());
            }
        }
        if (i != 0) {
            if (fragment.mFragmentId == 0 || fragment.mFragmentId == i) {
                fragment.mFragmentId = i;
                fragment.mContainerId = i;
            } else {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Can't change container ID of fragment ");
                sb2.append(fragment);
                sb2.append(str3);
                sb2.append(fragment.mFragmentId);
                sb2.append(str2);
                sb2.append(i);
                throw new IllegalStateException(sb2.toString());
            }
        }
        C0076Op op = new C0076Op();
        op.cmd = i2;
        op.fragment = fragment;
        addOp(op);
    }

    public FragmentTransaction replace(int i, Fragment fragment) {
        return replace(i, fragment, null);
    }

    public FragmentTransaction replace(int i, Fragment fragment, String str) {
        if (i != 0) {
            doAddOp(i, fragment, str, 2);
            return this;
        }
        throw new IllegalArgumentException("Must use non-zero containerViewId");
    }

    public FragmentTransaction remove(Fragment fragment) {
        C0076Op op = new C0076Op();
        op.cmd = 3;
        op.fragment = fragment;
        addOp(op);
        return this;
    }

    public FragmentTransaction hide(Fragment fragment) {
        C0076Op op = new C0076Op();
        op.cmd = 4;
        op.fragment = fragment;
        addOp(op);
        return this;
    }

    public FragmentTransaction show(Fragment fragment) {
        C0076Op op = new C0076Op();
        op.cmd = 5;
        op.fragment = fragment;
        addOp(op);
        return this;
    }

    public FragmentTransaction detach(Fragment fragment) {
        C0076Op op = new C0076Op();
        op.cmd = 6;
        op.fragment = fragment;
        addOp(op);
        return this;
    }

    public FragmentTransaction attach(Fragment fragment) {
        C0076Op op = new C0076Op();
        op.cmd = 7;
        op.fragment = fragment;
        addOp(op);
        return this;
    }

    public FragmentTransaction setCustomAnimations(int i, int i2) {
        return setCustomAnimations(i, i2, 0, 0);
    }

    public FragmentTransaction setCustomAnimations(int i, int i2, int i3, int i4) {
        this.mEnterAnim = i;
        this.mExitAnim = i2;
        this.mPopEnterAnim = i3;
        this.mPopExitAnim = i4;
        return this;
    }

    public FragmentTransaction setTransition(int i) {
        this.mTransition = i;
        return this;
    }

    public FragmentTransaction addSharedElement(View view, String str) {
        if (SUPPORTS_TRANSITIONS) {
            String transitionName = FragmentTransitionCompat21.getTransitionName(view);
            if (transitionName != null) {
                if (this.mSharedElementSourceNames == null) {
                    this.mSharedElementSourceNames = new ArrayList<>();
                    this.mSharedElementTargetNames = new ArrayList<>();
                }
                this.mSharedElementSourceNames.add(transitionName);
                this.mSharedElementTargetNames.add(str);
            } else {
                throw new IllegalArgumentException("Unique transitionNames are required for all sharedElements");
            }
        }
        return this;
    }

    public FragmentTransaction setTransitionStyle(int i) {
        this.mTransitionStyle = i;
        return this;
    }

    public FragmentTransaction addToBackStack(String str) {
        if (this.mAllowAddToBackStack) {
            this.mAddToBackStack = true;
            this.mName = str;
            return this;
        }
        throw new IllegalStateException("This FragmentTransaction is not allowed to be added to the back stack.");
    }

    public boolean isAddToBackStackAllowed() {
        return this.mAllowAddToBackStack;
    }

    public FragmentTransaction disallowAddToBackStack() {
        if (!this.mAddToBackStack) {
            this.mAllowAddToBackStack = false;
            return this;
        }
        throw new IllegalStateException("This transaction is already being added to the back stack");
    }

    public FragmentTransaction setBreadCrumbTitle(int i) {
        this.mBreadCrumbTitleRes = i;
        this.mBreadCrumbTitleText = null;
        return this;
    }

    public FragmentTransaction setBreadCrumbTitle(CharSequence charSequence) {
        this.mBreadCrumbTitleRes = 0;
        this.mBreadCrumbTitleText = charSequence;
        return this;
    }

    public FragmentTransaction setBreadCrumbShortTitle(int i) {
        this.mBreadCrumbShortTitleRes = i;
        this.mBreadCrumbShortTitleText = null;
        return this;
    }

    public FragmentTransaction setBreadCrumbShortTitle(CharSequence charSequence) {
        this.mBreadCrumbShortTitleRes = 0;
        this.mBreadCrumbShortTitleText = charSequence;
        return this;
    }

    /* access modifiers changed from: 0000 */
    public void bumpBackStackNesting(int i) {
        if (this.mAddToBackStack) {
            boolean z = FragmentManagerImpl.DEBUG;
            String str = TAG;
            if (z) {
                StringBuilder sb = new StringBuilder();
                sb.append("Bump nesting in ");
                sb.append(this);
                sb.append(" by ");
                sb.append(i);
                Log.v(str, sb.toString());
            }
            C0076Op op = this.mHead;
            while (op != null) {
                String str2 = " to ";
                String str3 = "Bump nesting of ";
                if (op.fragment != null) {
                    op.fragment.mBackStackNesting += i;
                    if (FragmentManagerImpl.DEBUG) {
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append(str3);
                        sb2.append(op.fragment);
                        sb2.append(str2);
                        sb2.append(op.fragment.mBackStackNesting);
                        Log.v(str, sb2.toString());
                    }
                }
                if (op.removed != null) {
                    for (int size = op.removed.size() - 1; size >= 0; size--) {
                        Fragment fragment = (Fragment) op.removed.get(size);
                        fragment.mBackStackNesting += i;
                        if (FragmentManagerImpl.DEBUG) {
                            StringBuilder sb3 = new StringBuilder();
                            sb3.append(str3);
                            sb3.append(fragment);
                            sb3.append(str2);
                            sb3.append(fragment.mBackStackNesting);
                            Log.v(str, sb3.toString());
                        }
                    }
                }
                op = op.next;
            }
        }
    }

    public int commit() {
        return commitInternal(false);
    }

    public int commitAllowingStateLoss() {
        return commitInternal(true);
    }

    /* access modifiers changed from: 0000 */
    public int commitInternal(boolean z) {
        if (!this.mCommitted) {
            if (FragmentManagerImpl.DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("Commit: ");
                sb.append(this);
                String sb2 = sb.toString();
                String str = TAG;
                Log.v(str, sb2);
                dump("  ", null, new PrintWriter(new LogWriter(str)), null);
            }
            this.mCommitted = true;
            if (this.mAddToBackStack) {
                this.mIndex = this.mManager.allocBackStackIndex(this);
            } else {
                this.mIndex = -1;
            }
            this.mManager.enqueueAction(this, z);
            return this.mIndex;
        }
        throw new IllegalStateException("commit already called");
    }

    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void run() {
        /*
            r16 = this;
            r0 = r16
            boolean r1 = android.support.p000v4.app.FragmentManagerImpl.DEBUG
            java.lang.String r2 = "FragmentManager"
            if (r1 == 0) goto L_0x001c
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r3 = "Run: "
            r1.append(r3)
            r1.append(r0)
            java.lang.String r1 = r1.toString()
            android.util.Log.v(r2, r1)
        L_0x001c:
            boolean r1 = r0.mAddToBackStack
            if (r1 == 0) goto L_0x002d
            int r1 = r0.mIndex
            if (r1 < 0) goto L_0x0025
            goto L_0x002d
        L_0x0025:
            java.lang.IllegalStateException r1 = new java.lang.IllegalStateException
            java.lang.String r2 = "addToBackStack() called after commit()"
            r1.<init>(r2)
            throw r1
        L_0x002d:
            r1 = 1
            r0.bumpBackStackNesting(r1)
            boolean r3 = SUPPORTS_TRANSITIONS
            r4 = 0
            r5 = 0
            if (r3 == 0) goto L_0x004f
            android.support.v4.app.FragmentManagerImpl r3 = r0.mManager
            int r3 = r3.mCurState
            if (r3 < r1) goto L_0x004f
            android.util.SparseArray r3 = new android.util.SparseArray
            r3.<init>()
            android.util.SparseArray r6 = new android.util.SparseArray
            r6.<init>()
            r0.calculateFragments(r3, r6)
            android.support.v4.app.BackStackRecord$TransitionState r3 = r0.beginTransition(r3, r6, r5)
            goto L_0x0050
        L_0x004f:
            r3 = r4
        L_0x0050:
            if (r3 == 0) goto L_0x0054
            r6 = 0
            goto L_0x0056
        L_0x0054:
            int r6 = r0.mTransitionStyle
        L_0x0056:
            if (r3 == 0) goto L_0x005a
            r7 = 0
            goto L_0x005c
        L_0x005a:
            int r7 = r0.mTransition
        L_0x005c:
            android.support.v4.app.BackStackRecord$Op r8 = r0.mHead
        L_0x005e:
            if (r8 == 0) goto L_0x0167
            if (r3 == 0) goto L_0x0064
            r9 = 0
            goto L_0x0066
        L_0x0064:
            int r9 = r8.enterAnim
        L_0x0066:
            if (r3 == 0) goto L_0x006a
            r10 = 0
            goto L_0x006c
        L_0x006a:
            int r10 = r8.exitAnim
        L_0x006c:
            int r11 = r8.cmd
            switch(r11) {
                case 1: goto L_0x0158;
                case 2: goto L_0x00be;
                case 3: goto L_0x00b2;
                case 4: goto L_0x00a8;
                case 5: goto L_0x009e;
                case 6: goto L_0x0094;
                case 7: goto L_0x008a;
                default: goto L_0x0071;
            }
        L_0x0071:
            java.lang.IllegalArgumentException r1 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Unknown cmd: "
            r2.append(r3)
            int r3 = r8.cmd
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            r1.<init>(r2)
            throw r1
        L_0x008a:
            android.support.v4.app.Fragment r10 = r8.fragment
            r10.mNextAnim = r9
            android.support.v4.app.FragmentManagerImpl r9 = r0.mManager
            r9.attachFragment(r10, r7, r6)
            goto L_0x00bb
        L_0x0094:
            android.support.v4.app.Fragment r9 = r8.fragment
            r9.mNextAnim = r10
            android.support.v4.app.FragmentManagerImpl r10 = r0.mManager
            r10.detachFragment(r9, r7, r6)
            goto L_0x00bb
        L_0x009e:
            android.support.v4.app.Fragment r10 = r8.fragment
            r10.mNextAnim = r9
            android.support.v4.app.FragmentManagerImpl r9 = r0.mManager
            r9.showFragment(r10, r7, r6)
            goto L_0x00bb
        L_0x00a8:
            android.support.v4.app.Fragment r9 = r8.fragment
            r9.mNextAnim = r10
            android.support.v4.app.FragmentManagerImpl r10 = r0.mManager
            r10.hideFragment(r9, r7, r6)
            goto L_0x00bb
        L_0x00b2:
            android.support.v4.app.Fragment r9 = r8.fragment
            r9.mNextAnim = r10
            android.support.v4.app.FragmentManagerImpl r10 = r0.mManager
            r10.removeFragment(r9, r7, r6)
        L_0x00bb:
            r10 = 0
            goto L_0x0162
        L_0x00be:
            android.support.v4.app.Fragment r11 = r8.fragment
            int r12 = r11.mContainerId
            android.support.v4.app.FragmentManagerImpl r13 = r0.mManager
            java.util.ArrayList<android.support.v4.app.Fragment> r13 = r13.mAdded
            if (r13 == 0) goto L_0x014d
            android.support.v4.app.FragmentManagerImpl r13 = r0.mManager
            java.util.ArrayList<android.support.v4.app.Fragment> r13 = r13.mAdded
            int r13 = r13.size()
            int r13 = r13 - r1
        L_0x00d1:
            if (r13 < 0) goto L_0x014d
            android.support.v4.app.FragmentManagerImpl r14 = r0.mManager
            java.util.ArrayList<android.support.v4.app.Fragment> r14 = r14.mAdded
            java.lang.Object r14 = r14.get(r13)
            android.support.v4.app.Fragment r14 = (android.support.p000v4.app.Fragment) r14
            boolean r15 = android.support.p000v4.app.FragmentManagerImpl.DEBUG
            if (r15 == 0) goto L_0x00fd
            java.lang.StringBuilder r15 = new java.lang.StringBuilder
            r15.<init>()
            java.lang.String r5 = "OP_REPLACE: adding="
            r15.append(r5)
            r15.append(r11)
            java.lang.String r5 = " old="
            r15.append(r5)
            r15.append(r14)
            java.lang.String r5 = r15.toString()
            android.util.Log.v(r2, r5)
        L_0x00fd:
            int r5 = r14.mContainerId
            if (r5 != r12) goto L_0x0149
            if (r14 != r11) goto L_0x0107
            r8.fragment = r4
            r11 = r4
            goto L_0x0149
        L_0x0107:
            java.util.ArrayList<android.support.v4.app.Fragment> r5 = r8.removed
            if (r5 != 0) goto L_0x0112
            java.util.ArrayList r5 = new java.util.ArrayList
            r5.<init>()
            r8.removed = r5
        L_0x0112:
            java.util.ArrayList<android.support.v4.app.Fragment> r5 = r8.removed
            r5.add(r14)
            r14.mNextAnim = r10
            boolean r5 = r0.mAddToBackStack
            if (r5 == 0) goto L_0x0144
            int r5 = r14.mBackStackNesting
            int r5 = r5 + r1
            r14.mBackStackNesting = r5
            boolean r5 = android.support.p000v4.app.FragmentManagerImpl.DEBUG
            if (r5 == 0) goto L_0x0144
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r15 = "Bump nesting of "
            r5.append(r15)
            r5.append(r14)
            java.lang.String r15 = " to "
            r5.append(r15)
            int r15 = r14.mBackStackNesting
            r5.append(r15)
            java.lang.String r5 = r5.toString()
            android.util.Log.v(r2, r5)
        L_0x0144:
            android.support.v4.app.FragmentManagerImpl r5 = r0.mManager
            r5.removeFragment(r14, r7, r6)
        L_0x0149:
            int r13 = r13 + -1
            r5 = 0
            goto L_0x00d1
        L_0x014d:
            if (r11 == 0) goto L_0x00bb
            r11.mNextAnim = r9
            android.support.v4.app.FragmentManagerImpl r5 = r0.mManager
            r10 = 0
            r5.addFragment(r11, r10)
            goto L_0x0162
        L_0x0158:
            r10 = 0
            android.support.v4.app.Fragment r5 = r8.fragment
            r5.mNextAnim = r9
            android.support.v4.app.FragmentManagerImpl r9 = r0.mManager
            r9.addFragment(r5, r10)
        L_0x0162:
            android.support.v4.app.BackStackRecord$Op r8 = r8.next
            r5 = 0
            goto L_0x005e
        L_0x0167:
            android.support.v4.app.FragmentManagerImpl r2 = r0.mManager
            int r3 = r2.mCurState
            r2.moveToState(r3, r7, r6, r1)
            boolean r1 = r0.mAddToBackStack
            if (r1 == 0) goto L_0x0177
            android.support.v4.app.FragmentManagerImpl r1 = r0.mManager
            r1.addBackStackState(r0)
        L_0x0177:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.p000v4.app.BackStackRecord.run():void");
    }

    private static void setFirstOut(SparseArray<Fragment> sparseArray, SparseArray<Fragment> sparseArray2, Fragment fragment) {
        if (fragment != null) {
            int i = fragment.mContainerId;
            if (i != 0 && !fragment.isHidden()) {
                if (fragment.isAdded() && fragment.getView() != null && sparseArray.get(i) == null) {
                    sparseArray.put(i, fragment);
                }
                if (sparseArray2.get(i) == fragment) {
                    sparseArray2.remove(i);
                }
            }
        }
    }

    private void setLastIn(SparseArray<Fragment> sparseArray, SparseArray<Fragment> sparseArray2, Fragment fragment) {
        if (fragment != null) {
            int i = fragment.mContainerId;
            if (i != 0) {
                if (!fragment.isAdded()) {
                    sparseArray2.put(i, fragment);
                }
                if (sparseArray.get(i) == fragment) {
                    sparseArray.remove(i);
                }
            }
            if (fragment.mState < 1 && this.mManager.mCurState >= 1) {
                this.mManager.makeActive(fragment);
                this.mManager.moveToState(fragment, 1, 0, 0, false);
            }
        }
    }

    private void calculateFragments(SparseArray<Fragment> sparseArray, SparseArray<Fragment> sparseArray2) {
        if (this.mManager.mContainer.onHasView()) {
            for (C0076Op op = this.mHead; op != null; op = op.next) {
                switch (op.cmd) {
                    case 1:
                        setLastIn(sparseArray, sparseArray2, op.fragment);
                        break;
                    case 2:
                        Fragment fragment = op.fragment;
                        if (this.mManager.mAdded != null) {
                            for (int i = 0; i < this.mManager.mAdded.size(); i++) {
                                Fragment fragment2 = (Fragment) this.mManager.mAdded.get(i);
                                if (fragment == null || fragment2.mContainerId == fragment.mContainerId) {
                                    if (fragment2 == fragment) {
                                        fragment = null;
                                        sparseArray2.remove(fragment2.mContainerId);
                                    } else {
                                        setFirstOut(sparseArray, sparseArray2, fragment2);
                                    }
                                }
                            }
                        }
                        setLastIn(sparseArray, sparseArray2, op.fragment);
                        break;
                    case 3:
                        setFirstOut(sparseArray, sparseArray2, op.fragment);
                        break;
                    case 4:
                        setFirstOut(sparseArray, sparseArray2, op.fragment);
                        break;
                    case 5:
                        setLastIn(sparseArray, sparseArray2, op.fragment);
                        break;
                    case 6:
                        setFirstOut(sparseArray, sparseArray2, op.fragment);
                        break;
                    case 7:
                        setLastIn(sparseArray, sparseArray2, op.fragment);
                        break;
                }
            }
        }
    }

    public void calculateBackFragments(SparseArray<Fragment> sparseArray, SparseArray<Fragment> sparseArray2) {
        if (this.mManager.mContainer.onHasView()) {
            for (C0076Op op = this.mTail; op != null; op = op.prev) {
                switch (op.cmd) {
                    case 1:
                        setFirstOut(sparseArray, sparseArray2, op.fragment);
                        break;
                    case 2:
                        if (op.removed != null) {
                            for (int size = op.removed.size() - 1; size >= 0; size--) {
                                setLastIn(sparseArray, sparseArray2, (Fragment) op.removed.get(size));
                            }
                        }
                        setFirstOut(sparseArray, sparseArray2, op.fragment);
                        break;
                    case 3:
                        setLastIn(sparseArray, sparseArray2, op.fragment);
                        break;
                    case 4:
                        setLastIn(sparseArray, sparseArray2, op.fragment);
                        break;
                    case 5:
                        setFirstOut(sparseArray, sparseArray2, op.fragment);
                        break;
                    case 6:
                        setLastIn(sparseArray, sparseArray2, op.fragment);
                        break;
                    case 7:
                        setFirstOut(sparseArray, sparseArray2, op.fragment);
                        break;
                }
            }
        }
    }

    public TransitionState popFromBackStack(boolean z, TransitionState transitionState, SparseArray<Fragment> sparseArray, SparseArray<Fragment> sparseArray2) {
        int i;
        int i2;
        int i3;
        int i4;
        if (FragmentManagerImpl.DEBUG) {
            StringBuilder sb = new StringBuilder();
            sb.append("popFromBackStack: ");
            sb.append(this);
            String sb2 = sb.toString();
            String str = TAG;
            Log.v(str, sb2);
            dump("  ", null, new PrintWriter(new LogWriter(str)), null);
        }
        if (SUPPORTS_TRANSITIONS && this.mManager.mCurState >= 1) {
            if (transitionState == null) {
                if (!(sparseArray.size() == 0 && sparseArray2.size() == 0)) {
                    transitionState = beginTransition(sparseArray, sparseArray2, true);
                }
            } else if (!z) {
                setNameOverrides(transitionState, this.mSharedElementTargetNames, this.mSharedElementSourceNames);
            }
        }
        bumpBackStackNesting(-1);
        if (transitionState != null) {
            i = 0;
        } else {
            i = this.mTransitionStyle;
        }
        if (transitionState != null) {
            i2 = 0;
        } else {
            i2 = this.mTransition;
        }
        for (C0076Op op = this.mTail; op != null; op = op.prev) {
            if (transitionState != null) {
                i3 = 0;
            } else {
                i3 = op.popEnterAnim;
            }
            if (transitionState != null) {
                i4 = 0;
            } else {
                i4 = op.popExitAnim;
            }
            switch (op.cmd) {
                case 1:
                    Fragment fragment = op.fragment;
                    fragment.mNextAnim = i4;
                    this.mManager.removeFragment(fragment, FragmentManagerImpl.reverseTransit(i2), i);
                    break;
                case 2:
                    Fragment fragment2 = op.fragment;
                    if (fragment2 != null) {
                        fragment2.mNextAnim = i4;
                        this.mManager.removeFragment(fragment2, FragmentManagerImpl.reverseTransit(i2), i);
                    }
                    if (op.removed == null) {
                        break;
                    } else {
                        for (int i5 = 0; i5 < op.removed.size(); i5++) {
                            Fragment fragment3 = (Fragment) op.removed.get(i5);
                            fragment3.mNextAnim = i3;
                            this.mManager.addFragment(fragment3, false);
                        }
                        break;
                    }
                case 3:
                    Fragment fragment4 = op.fragment;
                    fragment4.mNextAnim = i3;
                    this.mManager.addFragment(fragment4, false);
                    break;
                case 4:
                    Fragment fragment5 = op.fragment;
                    fragment5.mNextAnim = i3;
                    this.mManager.showFragment(fragment5, FragmentManagerImpl.reverseTransit(i2), i);
                    break;
                case 5:
                    Fragment fragment6 = op.fragment;
                    fragment6.mNextAnim = i4;
                    this.mManager.hideFragment(fragment6, FragmentManagerImpl.reverseTransit(i2), i);
                    break;
                case 6:
                    Fragment fragment7 = op.fragment;
                    fragment7.mNextAnim = i3;
                    this.mManager.attachFragment(fragment7, FragmentManagerImpl.reverseTransit(i2), i);
                    break;
                case 7:
                    Fragment fragment8 = op.fragment;
                    fragment8.mNextAnim = i3;
                    this.mManager.detachFragment(fragment8, FragmentManagerImpl.reverseTransit(i2), i);
                    break;
                default:
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append("Unknown cmd: ");
                    sb3.append(op.cmd);
                    throw new IllegalArgumentException(sb3.toString());
            }
        }
        if (z) {
            FragmentManagerImpl fragmentManagerImpl = this.mManager;
            fragmentManagerImpl.moveToState(fragmentManagerImpl.mCurState, FragmentManagerImpl.reverseTransit(i2), i, true);
            transitionState = null;
        }
        int i6 = this.mIndex;
        if (i6 >= 0) {
            this.mManager.freeBackStackIndex(i6);
            this.mIndex = -1;
        }
        return transitionState;
    }

    public String getName() {
        return this.mName;
    }

    public int getTransition() {
        return this.mTransition;
    }

    public int getTransitionStyle() {
        return this.mTransitionStyle;
    }

    public boolean isEmpty() {
        return this.mNumOp == 0;
    }

    private TransitionState beginTransition(SparseArray<Fragment> sparseArray, SparseArray<Fragment> sparseArray2, boolean z) {
        TransitionState transitionState = new TransitionState();
        transitionState.nonExistentView = new View(this.mManager.mHost.getContext());
        boolean z2 = false;
        for (int i = 0; i < sparseArray.size(); i++) {
            if (configureTransitions(sparseArray.keyAt(i), transitionState, z, sparseArray, sparseArray2)) {
                z2 = true;
            }
        }
        for (int i2 = 0; i2 < sparseArray2.size(); i2++) {
            int keyAt = sparseArray2.keyAt(i2);
            if (sparseArray.get(keyAt) == null && configureTransitions(keyAt, transitionState, z, sparseArray, sparseArray2)) {
                z2 = true;
            }
        }
        if (!z2) {
            return null;
        }
        return transitionState;
    }

    private static Object getEnterTransition(Fragment fragment, boolean z) {
        if (fragment == null) {
            return null;
        }
        return FragmentTransitionCompat21.cloneTransition(z ? fragment.getReenterTransition() : fragment.getEnterTransition());
    }

    private static Object getExitTransition(Fragment fragment, boolean z) {
        if (fragment == null) {
            return null;
        }
        return FragmentTransitionCompat21.cloneTransition(z ? fragment.getReturnTransition() : fragment.getExitTransition());
    }

    private static Object getSharedElementTransition(Fragment fragment, Fragment fragment2, boolean z) {
        if (fragment == null || fragment2 == null) {
            return null;
        }
        return FragmentTransitionCompat21.wrapSharedElementTransition(z ? fragment2.getSharedElementReturnTransition() : fragment.getSharedElementEnterTransition());
    }

    private static Object captureExitingViews(Object obj, Fragment fragment, ArrayList<View> arrayList, ArrayMap<String, View> arrayMap, View view) {
        return obj != null ? FragmentTransitionCompat21.captureExitingViews(obj, fragment.getView(), arrayList, arrayMap, view) : obj;
    }

    private ArrayMap<String, View> remapSharedElements(TransitionState transitionState, Fragment fragment, boolean z) {
        ArrayMap<String, View> arrayMap = new ArrayMap<>();
        if (this.mSharedElementSourceNames != null) {
            FragmentTransitionCompat21.findNamedViews(arrayMap, fragment.getView());
            if (z) {
                arrayMap.retainAll(this.mSharedElementTargetNames);
            } else {
                arrayMap = remapNames(this.mSharedElementSourceNames, this.mSharedElementTargetNames, arrayMap);
            }
        }
        if (z) {
            if (fragment.mEnterTransitionCallback != null) {
                fragment.mEnterTransitionCallback.onMapSharedElements(this.mSharedElementTargetNames, arrayMap);
            }
            setBackNameOverrides(transitionState, arrayMap, false);
        } else {
            if (fragment.mExitTransitionCallback != null) {
                fragment.mExitTransitionCallback.onMapSharedElements(this.mSharedElementTargetNames, arrayMap);
            }
            setNameOverrides(transitionState, arrayMap, false);
        }
        return arrayMap;
    }

    private boolean configureTransitions(int i, TransitionState transitionState, boolean z, SparseArray<Fragment> sparseArray, SparseArray<Fragment> sparseArray2) {
        Object obj;
        Object obj2;
        ArrayMap arrayMap;
        Object obj3;
        boolean z2;
        int i2 = i;
        TransitionState transitionState2 = transitionState;
        boolean z3 = z;
        ViewGroup viewGroup = (ViewGroup) this.mManager.mContainer.onFindViewById(i2);
        if (viewGroup == null) {
            return false;
        }
        final Fragment fragment = (Fragment) sparseArray2.get(i2);
        Fragment fragment2 = (Fragment) sparseArray.get(i2);
        Object enterTransition = getEnterTransition(fragment, z3);
        Object sharedElementTransition = getSharedElementTransition(fragment, fragment2, z3);
        Object exitTransition = getExitTransition(fragment2, z3);
        ArrayList arrayList = new ArrayList();
        Object obj4 = null;
        if (sharedElementTransition != null) {
            ArrayMap remapSharedElements = remapSharedElements(transitionState2, fragment2, z3);
            if (remapSharedElements.isEmpty()) {
                arrayMap = null;
                obj = exitTransition;
                obj2 = enterTransition;
            } else {
                SharedElementCallback sharedElementCallback = z3 ? fragment2.mEnterTransitionCallback : fragment.mEnterTransitionCallback;
                if (sharedElementCallback != null) {
                    sharedElementCallback.onSharedElementStart(new ArrayList(remapSharedElements.keySet()), new ArrayList(remapSharedElements.values()), null);
                }
                obj = exitTransition;
                obj2 = enterTransition;
                prepareSharedElementTransition(transitionState, viewGroup, sharedElementTransition, fragment, fragment2, z, arrayList);
                obj4 = sharedElementTransition;
                arrayMap = remapSharedElements;
            }
        } else {
            obj = exitTransition;
            obj2 = enterTransition;
            arrayMap = null;
            obj4 = sharedElementTransition;
        }
        if (obj2 == null && obj4 == null && obj == null) {
            return false;
        }
        ArrayList arrayList2 = new ArrayList();
        Object captureExitingViews = captureExitingViews(obj, fragment2, arrayList2, arrayMap, transitionState2.nonExistentView);
        ArrayList<String> arrayList3 = this.mSharedElementTargetNames;
        if (!(arrayList3 == null || arrayMap == null)) {
            View view = (View) arrayMap.get(arrayList3.get(0));
            if (view != null) {
                if (captureExitingViews != null) {
                    FragmentTransitionCompat21.setEpicenter(captureExitingViews, view);
                }
                if (obj4 != null) {
                    FragmentTransitionCompat21.setEpicenter(obj4, view);
                }
            }
        }
        C00731 r14 = new ViewRetriever() {
            public View getView() {
                return fragment.getView();
            }
        };
        ArrayList arrayList4 = new ArrayList();
        ArrayMap arrayMap2 = new ArrayMap();
        boolean z4 = fragment != null ? z3 ? fragment.getAllowReturnTransitionOverlap() : fragment.getAllowEnterTransitionOverlap() : true;
        Object mergeTransitions = FragmentTransitionCompat21.mergeTransitions(obj2, captureExitingViews, obj4, z4);
        if (mergeTransitions != null) {
            obj3 = mergeTransitions;
            Object obj5 = obj2;
            ViewGroup viewGroup2 = viewGroup;
            FragmentTransitionCompat21.addTransitionTargets(obj2, obj4, viewGroup, r14, transitionState2.nonExistentView, transitionState2.enteringEpicenterView, transitionState2.nameOverrides, arrayList4, arrayMap, arrayMap2, arrayList);
            int i3 = i;
            Object obj6 = obj5;
            excludeHiddenFragmentsAfterEnter(viewGroup2, transitionState2, i3, obj3);
            FragmentTransitionCompat21.excludeTarget(obj3, transitionState2.nonExistentView, true);
            excludeHiddenFragments(transitionState2, i3, obj3);
            FragmentTransitionCompat21.beginDelayedTransition(viewGroup2, obj3);
            z2 = true;
            FragmentTransitionCompat21.cleanupTransitions(viewGroup2, transitionState2.nonExistentView, obj6, arrayList4, captureExitingViews, arrayList2, obj4, arrayList, obj3, transitionState2.hiddenFragmentViews, arrayMap2);
        } else {
            obj3 = mergeTransitions;
            z2 = true;
        }
        if (obj3 == null) {
            z2 = false;
        }
        return z2;
    }

    private void prepareSharedElementTransition(TransitionState transitionState, View view, Object obj, Fragment fragment, Fragment fragment2, boolean z, ArrayList<View> arrayList) {
        ViewTreeObserver viewTreeObserver = view.getViewTreeObserver();
        final View view2 = view;
        final Object obj2 = obj;
        final ArrayList<View> arrayList2 = arrayList;
        final TransitionState transitionState2 = transitionState;
        final boolean z2 = z;
        final Fragment fragment3 = fragment;
        final Fragment fragment4 = fragment2;
        C00742 r1 = new OnPreDrawListener() {
            public boolean onPreDraw() {
                view2.getViewTreeObserver().removeOnPreDrawListener(this);
                Object obj = obj2;
                if (obj != null) {
                    FragmentTransitionCompat21.removeTargets(obj, arrayList2);
                    arrayList2.clear();
                    ArrayMap access$000 = BackStackRecord.this.mapSharedElementsIn(transitionState2, z2, fragment3);
                    FragmentTransitionCompat21.setSharedElementTargets(obj2, transitionState2.nonExistentView, access$000, arrayList2);
                    BackStackRecord.this.setEpicenterIn(access$000, transitionState2);
                    BackStackRecord.this.callSharedElementEnd(transitionState2, fragment3, fragment4, z2, access$000);
                }
                return true;
            }
        };
        viewTreeObserver.addOnPreDrawListener(r1);
    }

    /* access modifiers changed from: private */
    public void callSharedElementEnd(TransitionState transitionState, Fragment fragment, Fragment fragment2, boolean z, ArrayMap<String, View> arrayMap) {
        SharedElementCallback sharedElementCallback = z ? fragment2.mEnterTransitionCallback : fragment.mEnterTransitionCallback;
        if (sharedElementCallback != null) {
            sharedElementCallback.onSharedElementEnd(new ArrayList(arrayMap.keySet()), new ArrayList(arrayMap.values()), null);
        }
    }

    /* access modifiers changed from: private */
    public void setEpicenterIn(ArrayMap<String, View> arrayMap, TransitionState transitionState) {
        if (this.mSharedElementTargetNames != null && !arrayMap.isEmpty()) {
            View view = (View) arrayMap.get(this.mSharedElementTargetNames.get(0));
            if (view != null) {
                transitionState.enteringEpicenterView.epicenter = view;
            }
        }
    }

    /* access modifiers changed from: private */
    public ArrayMap<String, View> mapSharedElementsIn(TransitionState transitionState, boolean z, Fragment fragment) {
        ArrayMap<String, View> mapEnteringSharedElements = mapEnteringSharedElements(transitionState, fragment, z);
        if (z) {
            if (fragment.mExitTransitionCallback != null) {
                fragment.mExitTransitionCallback.onMapSharedElements(this.mSharedElementTargetNames, mapEnteringSharedElements);
            }
            setBackNameOverrides(transitionState, mapEnteringSharedElements, true);
        } else {
            if (fragment.mEnterTransitionCallback != null) {
                fragment.mEnterTransitionCallback.onMapSharedElements(this.mSharedElementTargetNames, mapEnteringSharedElements);
            }
            setNameOverrides(transitionState, mapEnteringSharedElements, true);
        }
        return mapEnteringSharedElements;
    }

    private static ArrayMap<String, View> remapNames(ArrayList<String> arrayList, ArrayList<String> arrayList2, ArrayMap<String, View> arrayMap) {
        if (arrayMap.isEmpty()) {
            return arrayMap;
        }
        ArrayMap<String, View> arrayMap2 = new ArrayMap<>();
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            View view = (View) arrayMap.get(arrayList.get(i));
            if (view != null) {
                arrayMap2.put(arrayList2.get(i), view);
            }
        }
        return arrayMap2;
    }

    private ArrayMap<String, View> mapEnteringSharedElements(TransitionState transitionState, Fragment fragment, boolean z) {
        ArrayMap<String, View> arrayMap = new ArrayMap<>();
        View view = fragment.getView();
        if (view == null || this.mSharedElementSourceNames == null) {
            return arrayMap;
        }
        FragmentTransitionCompat21.findNamedViews(arrayMap, view);
        if (z) {
            return remapNames(this.mSharedElementSourceNames, this.mSharedElementTargetNames, arrayMap);
        }
        arrayMap.retainAll(this.mSharedElementTargetNames);
        return arrayMap;
    }

    private void excludeHiddenFragmentsAfterEnter(View view, TransitionState transitionState, int i, Object obj) {
        ViewTreeObserver viewTreeObserver = view.getViewTreeObserver();
        final View view2 = view;
        final TransitionState transitionState2 = transitionState;
        final int i2 = i;
        final Object obj2 = obj;
        C00753 r1 = new OnPreDrawListener() {
            public boolean onPreDraw() {
                view2.getViewTreeObserver().removeOnPreDrawListener(this);
                BackStackRecord.this.excludeHiddenFragments(transitionState2, i2, obj2);
                return true;
            }
        };
        viewTreeObserver.addOnPreDrawListener(r1);
    }

    /* access modifiers changed from: private */
    public void excludeHiddenFragments(TransitionState transitionState, int i, Object obj) {
        if (this.mManager.mAdded != null) {
            for (int i2 = 0; i2 < this.mManager.mAdded.size(); i2++) {
                Fragment fragment = (Fragment) this.mManager.mAdded.get(i2);
                if (!(fragment.mView == null || fragment.mContainer == null || fragment.mContainerId != i)) {
                    if (!fragment.mHidden) {
                        FragmentTransitionCompat21.excludeTarget(obj, fragment.mView, false);
                        transitionState.hiddenFragmentViews.remove(fragment.mView);
                    } else if (!transitionState.hiddenFragmentViews.contains(fragment.mView)) {
                        FragmentTransitionCompat21.excludeTarget(obj, fragment.mView, true);
                        transitionState.hiddenFragmentViews.add(fragment.mView);
                    }
                }
            }
        }
    }

    private static void setNameOverride(ArrayMap<String, String> arrayMap, String str, String str2) {
        if (!(str == null || str2 == null)) {
            for (int i = 0; i < arrayMap.size(); i++) {
                if (str.equals(arrayMap.valueAt(i))) {
                    arrayMap.setValueAt(i, str2);
                    return;
                }
            }
            arrayMap.put(str, str2);
        }
    }

    private static void setNameOverrides(TransitionState transitionState, ArrayList<String> arrayList, ArrayList<String> arrayList2) {
        if (arrayList != null) {
            for (int i = 0; i < arrayList.size(); i++) {
                setNameOverride(transitionState.nameOverrides, (String) arrayList.get(i), (String) arrayList2.get(i));
            }
        }
    }

    private void setBackNameOverrides(TransitionState transitionState, ArrayMap<String, View> arrayMap, boolean z) {
        ArrayList<String> arrayList = this.mSharedElementTargetNames;
        int size = arrayList == null ? 0 : arrayList.size();
        for (int i = 0; i < size; i++) {
            String str = (String) this.mSharedElementSourceNames.get(i);
            View view = (View) arrayMap.get((String) this.mSharedElementTargetNames.get(i));
            if (view != null) {
                String transitionName = FragmentTransitionCompat21.getTransitionName(view);
                if (z) {
                    setNameOverride(transitionState.nameOverrides, str, transitionName);
                } else {
                    setNameOverride(transitionState.nameOverrides, transitionName, str);
                }
            }
        }
    }

    private void setNameOverrides(TransitionState transitionState, ArrayMap<String, View> arrayMap, boolean z) {
        int size = arrayMap.size();
        for (int i = 0; i < size; i++) {
            String str = (String) arrayMap.keyAt(i);
            String transitionName = FragmentTransitionCompat21.getTransitionName((View) arrayMap.valueAt(i));
            if (z) {
                setNameOverride(transitionState.nameOverrides, str, transitionName);
            } else {
                setNameOverride(transitionState.nameOverrides, transitionName, str);
            }
        }
    }
}
