package com.meronmks.zimitta.Datas;

import android.os.Parcel;
import android.os.Parcelable;

import twitter4j.Status;

/**
 * Created by meron on 2016/09/16.
 */
public class ParcelStatus implements Parcelable {
    public Status status;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.status);
    }

    public ParcelStatus() {
    }

    protected ParcelStatus(Parcel in) {
        this.status = (Status) in.readSerializable();
    }

    public static final Parcelable.Creator<ParcelStatus> CREATOR = new Parcelable.Creator<ParcelStatus>() {
        @Override
        public ParcelStatus createFromParcel(Parcel source) {
            return new ParcelStatus(source);
        }

        @Override
        public ParcelStatus[] newArray(int size) {
            return new ParcelStatus[size];
        }
    };
}
