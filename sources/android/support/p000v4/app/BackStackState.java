package android.support.p000v4.app;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import android.util.Log;
import java.util.ArrayList;

/* renamed from: android.support.v4.app.BackStackState */
/* compiled from: BackStackRecord */
final class BackStackState implements Parcelable {
    public static final Creator<BackStackState> CREATOR = new Creator<BackStackState>() {
        public BackStackState createFromParcel(Parcel parcel) {
            return new BackStackState(parcel);
        }

        public BackStackState[] newArray(int i) {
            return new BackStackState[i];
        }
    };
    final int mBreadCrumbShortTitleRes;
    final CharSequence mBreadCrumbShortTitleText;
    final int mBreadCrumbTitleRes;
    final CharSequence mBreadCrumbTitleText;
    final int mIndex;
    final String mName;
    final int[] mOps;
    final ArrayList<String> mSharedElementSourceNames;
    final ArrayList<String> mSharedElementTargetNames;
    final int mTransition;
    final int mTransitionStyle;

    public int describeContents() {
        return 0;
    }

    public BackStackState(BackStackRecord backStackRecord) {
        int i = 0;
        for (C0076Op op = backStackRecord.mHead; op != null; op = op.next) {
            if (op.removed != null) {
                i += op.removed.size();
            }
        }
        this.mOps = new int[((backStackRecord.mNumOp * 7) + i)];
        if (backStackRecord.mAddToBackStack) {
            int i2 = 0;
            for (C0076Op op2 = backStackRecord.mHead; op2 != null; op2 = op2.next) {
                int i3 = i2 + 1;
                this.mOps[i2] = op2.cmd;
                int i4 = i3 + 1;
                this.mOps[i3] = op2.fragment != null ? op2.fragment.mIndex : -1;
                int i5 = i4 + 1;
                this.mOps[i4] = op2.enterAnim;
                int i6 = i5 + 1;
                this.mOps[i5] = op2.exitAnim;
                int i7 = i6 + 1;
                this.mOps[i6] = op2.popEnterAnim;
                int i8 = i7 + 1;
                this.mOps[i7] = op2.popExitAnim;
                if (op2.removed != null) {
                    int size = op2.removed.size();
                    int i9 = i8 + 1;
                    this.mOps[i8] = size;
                    int i10 = 0;
                    while (i10 < size) {
                        int i11 = i9 + 1;
                        this.mOps[i9] = ((Fragment) op2.removed.get(i10)).mIndex;
                        i10++;
                        i9 = i11;
                    }
                    i2 = i9;
                } else {
                    int i12 = i8 + 1;
                    this.mOps[i8] = 0;
                    i2 = i12;
                }
            }
            this.mTransition = backStackRecord.mTransition;
            this.mTransitionStyle = backStackRecord.mTransitionStyle;
            this.mName = backStackRecord.mName;
            this.mIndex = backStackRecord.mIndex;
            this.mBreadCrumbTitleRes = backStackRecord.mBreadCrumbTitleRes;
            this.mBreadCrumbTitleText = backStackRecord.mBreadCrumbTitleText;
            this.mBreadCrumbShortTitleRes = backStackRecord.mBreadCrumbShortTitleRes;
            this.mBreadCrumbShortTitleText = backStackRecord.mBreadCrumbShortTitleText;
            this.mSharedElementSourceNames = backStackRecord.mSharedElementSourceNames;
            this.mSharedElementTargetNames = backStackRecord.mSharedElementTargetNames;
            return;
        }
        throw new IllegalStateException("Not on back stack");
    }

    public BackStackState(Parcel parcel) {
        this.mOps = parcel.createIntArray();
        this.mTransition = parcel.readInt();
        this.mTransitionStyle = parcel.readInt();
        this.mName = parcel.readString();
        this.mIndex = parcel.readInt();
        this.mBreadCrumbTitleRes = parcel.readInt();
        this.mBreadCrumbTitleText = (CharSequence) TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
        this.mBreadCrumbShortTitleRes = parcel.readInt();
        this.mBreadCrumbShortTitleText = (CharSequence) TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
        this.mSharedElementSourceNames = parcel.createStringArrayList();
        this.mSharedElementTargetNames = parcel.createStringArrayList();
    }

    public BackStackRecord instantiate(FragmentManagerImpl fragmentManagerImpl) {
        BackStackRecord backStackRecord = new BackStackRecord(fragmentManagerImpl);
        int i = 0;
        int i2 = 0;
        while (i < this.mOps.length) {
            C0076Op op = new C0076Op();
            int i3 = i + 1;
            op.cmd = this.mOps[i];
            String str = "Instantiate ";
            String str2 = "FragmentManager";
            if (FragmentManagerImpl.DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append(str);
                sb.append(backStackRecord);
                sb.append(" op #");
                sb.append(i2);
                sb.append(" base fragment #");
                sb.append(this.mOps[i3]);
                Log.v(str2, sb.toString());
            }
            int i4 = i3 + 1;
            int i5 = this.mOps[i3];
            if (i5 >= 0) {
                op.fragment = (Fragment) fragmentManagerImpl.mActive.get(i5);
            } else {
                op.fragment = null;
            }
            int[] iArr = this.mOps;
            int i6 = i4 + 1;
            op.enterAnim = iArr[i4];
            int i7 = i6 + 1;
            op.exitAnim = iArr[i6];
            int i8 = i7 + 1;
            op.popEnterAnim = iArr[i7];
            int i9 = i8 + 1;
            op.popExitAnim = iArr[i8];
            int i10 = i9 + 1;
            int i11 = iArr[i9];
            if (i11 > 0) {
                op.removed = new ArrayList<>(i11);
                int i12 = i10;
                int i13 = 0;
                while (i13 < i11) {
                    if (FragmentManagerImpl.DEBUG) {
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append(str);
                        sb2.append(backStackRecord);
                        sb2.append(" set remove fragment #");
                        sb2.append(this.mOps[i12]);
                        Log.v(str2, sb2.toString());
                    }
                    int i14 = i12 + 1;
                    op.removed.add((Fragment) fragmentManagerImpl.mActive.get(this.mOps[i12]));
                    i13++;
                    i12 = i14;
                }
                i = i12;
            } else {
                i = i10;
            }
            backStackRecord.mEnterAnim = op.enterAnim;
            backStackRecord.mExitAnim = op.exitAnim;
            backStackRecord.mPopEnterAnim = op.popEnterAnim;
            backStackRecord.mPopExitAnim = op.popExitAnim;
            backStackRecord.addOp(op);
            i2++;
        }
        backStackRecord.mTransition = this.mTransition;
        backStackRecord.mTransitionStyle = this.mTransitionStyle;
        backStackRecord.mName = this.mName;
        backStackRecord.mIndex = this.mIndex;
        backStackRecord.mAddToBackStack = true;
        backStackRecord.mBreadCrumbTitleRes = this.mBreadCrumbTitleRes;
        backStackRecord.mBreadCrumbTitleText = this.mBreadCrumbTitleText;
        backStackRecord.mBreadCrumbShortTitleRes = this.mBreadCrumbShortTitleRes;
        backStackRecord.mBreadCrumbShortTitleText = this.mBreadCrumbShortTitleText;
        backStackRecord.mSharedElementSourceNames = this.mSharedElementSourceNames;
        backStackRecord.mSharedElementTargetNames = this.mSharedElementTargetNames;
        backStackRecord.bumpBackStackNesting(1);
        return backStackRecord;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeIntArray(this.mOps);
        parcel.writeInt(this.mTransition);
        parcel.writeInt(this.mTransitionStyle);
        parcel.writeString(this.mName);
        parcel.writeInt(this.mIndex);
        parcel.writeInt(this.mBreadCrumbTitleRes);
        TextUtils.writeToParcel(this.mBreadCrumbTitleText, parcel, 0);
        parcel.writeInt(this.mBreadCrumbShortTitleRes);
        TextUtils.writeToParcel(this.mBreadCrumbShortTitleText, parcel, 0);
        parcel.writeStringList(this.mSharedElementSourceNames);
        parcel.writeStringList(this.mSharedElementTargetNames);
    }
}
