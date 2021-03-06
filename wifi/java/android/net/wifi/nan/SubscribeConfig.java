/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.net.wifi.nan;

import android.annotation.IntDef;
import android.annotation.NonNull;
import android.annotation.Nullable;
import android.os.Parcel;
import android.os.Parcelable;

import libcore.util.HexEncoding;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Defines the configuration of a NAN subscribe session. Built using
 * {@link SubscribeConfig.Builder}. Subscribe is done using
 * {@link WifiNanManager#subscribe(SubscribeConfig, WifiNanSessionCallback)} or
 * {@link WifiNanSubscribeSession#updateSubscribe(SubscribeConfig)}.
 *
 * @hide PROPOSED_NAN_API
 */
public final class SubscribeConfig implements Parcelable {
    /** @hide */
    @IntDef({
            SUBSCRIBE_TYPE_PASSIVE, SUBSCRIBE_TYPE_ACTIVE })
    @Retention(RetentionPolicy.SOURCE)
    public @interface SubscribeTypes {
    }

    /**
     * Defines a passive subscribe session - a subscribe session where
     * subscribe packets are not transmitted over-the-air and the device listens
     * and matches to transmitted publish packets. Configuration is done using
     * {@link SubscribeConfig.Builder#setSubscribeType(int)}.
     */
    public static final int SUBSCRIBE_TYPE_PASSIVE = 0;

    /**
     * Defines an active subscribe session - a subscribe session where
     * subscribe packets are transmitted over-the-air. Configuration is done
     * using {@link SubscribeConfig.Builder#setSubscribeType(int)}.
     */
    public static final int SUBSCRIBE_TYPE_ACTIVE = 1;

    /** @hide */
    @IntDef({
            MATCH_STYLE_FIRST_ONLY, MATCH_STYLE_ALL })
    @Retention(RetentionPolicy.SOURCE)
    public @interface MatchStyles {
    }

    /**
     * Specifies that only the first match of a set of identical matches (same
     * publish) will be reported to the subscriber. Configuration is done using
     * {@link SubscribeConfig.Builder#setMatchStyle(int)}.
     */
    public static final int MATCH_STYLE_FIRST_ONLY = 0;

    /**
     * Specifies that all matches of a set of identical matches (same publish)
     * will be reported to the subscriber. Configuration is done using
     * {@link SubscribeConfig.Builder#setMatchStyle(int)}.
     */
    public static final int MATCH_STYLE_ALL = 1;

    /** @hide */
    public final byte[] mServiceName;

    /** @hide */
    public final byte[] mServiceSpecificInfo;

    /** @hide */
    public final byte[] mMatchFilter;

    /** @hide */
    public final int mSubscribeType;

    /** @hide */
    public final int mSubscribeCount;

    /** @hide */
    public final int mTtlSec;

    /** @hide */
    public final int mMatchStyle;

    /** @hide */
    public final boolean mEnableTerminateNotification;

    private SubscribeConfig(byte[] serviceName, byte[] serviceSpecificInfo, byte[] matchFilter,
            int subscribeType, int publichCount, int ttlSec, int matchStyle,
            boolean enableTerminateNotification) {
        mServiceName = serviceName;
        mServiceSpecificInfo = serviceSpecificInfo;
        mMatchFilter = matchFilter;
        mSubscribeType = subscribeType;
        mSubscribeCount = publichCount;
        mTtlSec = ttlSec;
        mMatchStyle = matchStyle;
        mEnableTerminateNotification = enableTerminateNotification;
    }

    @Override
    public String toString() {
        return "SubscribeConfig [mServiceName='" + mServiceName + ", mServiceSpecificInfo='" + (
                (mServiceSpecificInfo == null) ? "null" : HexEncoding.encode(mServiceSpecificInfo))
                + ", mMatchFilter=" + (new LvBufferUtils.LvIterable(1, mMatchFilter)).toString()
                + ", mSubscribeType=" + mSubscribeType + ", mSubscribeCount=" + mSubscribeCount
                + ", mTtlSec=" + mTtlSec + ", mMatchType=" + mMatchStyle
                + ", mEnableTerminateNotification=" + mEnableTerminateNotification + "]";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByteArray(mServiceName);
        dest.writeByteArray(mServiceSpecificInfo);
        dest.writeByteArray(mMatchFilter);
        dest.writeInt(mSubscribeType);
        dest.writeInt(mSubscribeCount);
        dest.writeInt(mTtlSec);
        dest.writeInt(mMatchStyle);
        dest.writeInt(mEnableTerminateNotification ? 1 : 0);
    }

    public static final Creator<SubscribeConfig> CREATOR = new Creator<SubscribeConfig>() {
        @Override
        public SubscribeConfig[] newArray(int size) {
            return new SubscribeConfig[size];
        }

        @Override
        public SubscribeConfig createFromParcel(Parcel in) {
            byte[] serviceName = in.createByteArray();
            byte[] ssi = in.createByteArray();
            byte[] matchFilter = in.createByteArray();
            int subscribeType = in.readInt();
            int subscribeCount = in.readInt();
            int ttlSec = in.readInt();
            int matchStyle = in.readInt();
            boolean enableTerminateNotification = in.readInt() != 0;

            return new SubscribeConfig(serviceName, ssi, matchFilter, subscribeType, subscribeCount,
                    ttlSec, matchStyle, enableTerminateNotification);
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof SubscribeConfig)) {
            return false;
        }

        SubscribeConfig lhs = (SubscribeConfig) o;

        return Arrays.equals(mServiceName, lhs.mServiceName) && Arrays.equals(mServiceSpecificInfo,
                lhs.mServiceSpecificInfo) && Arrays.equals(mMatchFilter, lhs.mMatchFilter)
                && mSubscribeType == lhs.mSubscribeType && mSubscribeCount == lhs.mSubscribeCount
                && mTtlSec == lhs.mTtlSec && mMatchStyle == lhs.mMatchStyle
                && mEnableTerminateNotification == lhs.mEnableTerminateNotification;
    }

    @Override
    public int hashCode() {
        int result = 17;

        result = 31 * result + Arrays.hashCode(mServiceName);
        result = 31 * result + Arrays.hashCode(mServiceSpecificInfo);
        result = 31 * result + Arrays.hashCode(mMatchFilter);
        result = 31 * result + mSubscribeType;
        result = 31 * result + mSubscribeCount;
        result = 31 * result + mTtlSec;
        result = 31 * result + mMatchStyle;
        result = 31 * result + (mEnableTerminateNotification ? 1 : 0);

        return result;
    }

    /**
     * Verifies that the contents of the SubscribeConfig are valid. Otherwise
     * throws an IllegalArgumentException.
     *
     * @hide
     */
    public void validate() throws IllegalArgumentException {
        WifiNanUtils.validateServiceName(mServiceName);

        if (!LvBufferUtils.isValid(mMatchFilter, 1)) {
            throw new IllegalArgumentException(
                    "Invalid matchFilter configuration - LV fields do not match up to length");
        }
        if (mSubscribeType < SUBSCRIBE_TYPE_PASSIVE || mSubscribeType > SUBSCRIBE_TYPE_ACTIVE) {
            throw new IllegalArgumentException("Invalid subscribeType - " + mSubscribeType);
        }
        if (mSubscribeCount < 0) {
            throw new IllegalArgumentException("Invalid subscribeCount - must be non-negative");
        }
        if (mTtlSec < 0) {
            throw new IllegalArgumentException("Invalid ttlSec - must be non-negative");
        }
        if (mMatchStyle != MATCH_STYLE_FIRST_ONLY && mMatchStyle != MATCH_STYLE_ALL) {
            throw new IllegalArgumentException(
                    "Invalid matchType - must be MATCH_FIRST_ONLY or MATCH_ALL");
        }
    }

    /**
     * Builder used to build {@link SubscribeConfig} objects.
     */
    public static final class Builder {
        private byte[] mServiceName;
        private byte[] mServiceSpecificInfo;
        private byte[] mMatchFilter;
        private int mSubscribeType = SUBSCRIBE_TYPE_PASSIVE;
        private int mSubscribeCount = 0;
        private int mTtlSec = 0;
        private int mMatchStyle = MATCH_STYLE_ALL;
        private boolean mEnableTerminateNotification = true;

        /**
         * Specify the service name of the subscribe session. The actual on-air
         * value is a 6 byte hashed representation of this string.
         * <p>
         * The Service Name is a UTF-8 encoded string from 1 to 255 bytes in length.
         * The only acceptable single-byte UTF-8 symbols for a Service Name are alphanumeric
         * values (A-Z, a-z, 0-9), the hyphen ('-'), and the period ('.'). All valid multi-byte
         * UTF-8 characters are acceptable in a Service Name.
         * <p>
         * Must be called - an empty ServiceName is not valid.
         *
         * @param serviceName The service name for the subscribe session.
         *
         * @return The builder to facilitate chaining
         *         {@code builder.setXXX(..).setXXX(..)}.
         */
        public Builder setServiceName(@NonNull String serviceName) {
            if (serviceName == null) {
                throw new IllegalArgumentException("Invalid service name - must be non-null");
            }
            mServiceName = serviceName.getBytes(StandardCharsets.UTF_8);
            return this;
        }

        /**
         * Specify service specific information for the subscribe session. This is
         * a free-form byte array available to the application to send
         * additional information as part of the discovery operation - i.e. it
         * will not be used to determine whether a publish/subscribe match
         * occurs.
         * <p>
         *     Optional. Empty by default.
         *
         * @param serviceSpecificInfo A byte-array for the service-specific
         *            information field.
         *
         * @return The builder to facilitate chaining
         *         {@code builder.setXXX(..).setXXX(..)}.
         */
        public Builder setServiceSpecificInfo(@Nullable byte[] serviceSpecificInfo) {
            mServiceSpecificInfo = serviceSpecificInfo;
            return this;
        }

        /**
         * Specify service specific information for the subscribe session - a simple wrapper
         * of {@link SubscribeConfig.Builder#setServiceSpecificInfo(byte[])}
         * obtaining the data from a String.
         * <p>
         *     Optional. Empty by default.
         *
         * @param serviceSpecificInfoStr The service specific information string
         *            to be included (as a byte array) in the subscribe
         *            information.
         *
         * @return The builder to facilitate chaining
         *         {@code builder.setXXX(..).setXXX(..)}.
         */
        public Builder setServiceSpecificInfo(@NonNull String serviceSpecificInfoStr) {
            mServiceSpecificInfo = serviceSpecificInfoStr.getBytes();
            return this;
        }

        /**
         * The match filter for a subscribe session. Used to determine whether a service
         * discovery occurred - in addition to relying on the service name.
         * <p>
         * Format is an LV byte array: a single byte Length field followed by L bytes (the value of
         * the Length field) of a value blob.
         * <p>
         *     Optional. Empty by default.
         *
         * @param matchFilter The byte-array containing the LV formatted match filter.
         *
         * @return The builder to facilitate chaining
         *         {@code builder.setXXX(..).setXXX(..)}.
         */
        public Builder setMatchFilter(@Nullable byte[] matchFilter) {
            mMatchFilter = matchFilter;
            return this;
        }

        /**
         * Sets the type of the subscribe session: active (subscribe packets are
         * transmitted over-the-air), or passive (no subscribe packets are
         * transmitted, a match is made against a solicited/active publish
         * session whose packets are transmitted over-the-air).
         *
         * @param subscribeType Subscribe session type:
         *            {@link SubscribeConfig#SUBSCRIBE_TYPE_ACTIVE} or
         *            {@link SubscribeConfig#SUBSCRIBE_TYPE_PASSIVE}.
         *
         * @return The builder to facilitate chaining
         *         {@code builder.setXXX(..).setXXX(..)}.
         */
        public Builder setSubscribeType(@SubscribeTypes int subscribeType) {
            if (subscribeType < SUBSCRIBE_TYPE_PASSIVE || subscribeType > SUBSCRIBE_TYPE_ACTIVE) {
                throw new IllegalArgumentException("Invalid subscribeType - " + subscribeType);
            }
            mSubscribeType = subscribeType;
            return this;
        }

        /**
         * Sets the number of times an active (
         * {@link SubscribeConfig.Builder#setSubscribeType(int)}) subscribe session
         * will broadcast. When the count is reached an event will be
         * generated for {@link WifiNanSessionCallback#onSessionTerminated(int)}
         * with {@link WifiNanSessionCallback#TERMINATE_REASON_DONE}.
         * <p>
         *     Optional. 0 by default - indicating the session doesn't terminate on its own.
         *     Session will be terminated when {@link WifiNanSession#terminate()} is called.
         *
         * @param subscribeCount Number of subscribe packets to broadcast.
         *
         * @return The builder to facilitate chaining
         *         {@code builder.setXXX(..).setXXX(..)}.
         */
        public Builder setSubscribeCount(int subscribeCount) {
            if (subscribeCount < 0) {
                throw new IllegalArgumentException("Invalid subscribeCount - must be non-negative");
            }
            mSubscribeCount = subscribeCount;
            return this;
        }

        /**
         * Sets the time interval (in seconds) an active (
         * {@link SubscribeConfig.Builder#setSubscribeType(int)}) subscribe session
         * will be alive - i.e. broadcasting a packet. When the TTL is reached
         * an event will be generated for
         * {@link WifiNanSessionCallback#onSessionTerminated(int)} with
         * {@link WifiNanSessionCallback#TERMINATE_REASON_DONE}.
         * <p>
         *     Optional. 0 by default - indicating the session doesn't terminate on its own.
         *     Session will be terminated when {@link WifiNanSession#terminate()} is called.
         *
         * @param ttlSec Lifetime of a subscribe session in seconds.
         *
         * @return The builder to facilitate chaining
         *         {@code builder.setXXX(..).setXXX(..)}.
         */
        public Builder setTtlSec(int ttlSec) {
            if (ttlSec < 0) {
                throw new IllegalArgumentException("Invalid ttlSec - must be non-negative");
            }
            mTtlSec = ttlSec;
            return this;
        }

        /**
         * Sets the match style of the subscription - how are matches from a
         * single match session (corresponding to the same publish action on the
         * peer) reported to the host (using the
         * {@link WifiNanSessionCallback#onMatch(int, byte[], byte[])}
         * ). The options are: only report the first match and ignore the rest
         * {@link SubscribeConfig#MATCH_STYLE_FIRST_ONLY} or report every single
         * match {@link SubscribeConfig#MATCH_STYLE_ALL} (the default).
         *
         * @param matchStyle The reporting style for the discovery match.
         * @return The builder to facilitate chaining
         *         {@code builder.setXXX(..).setXXX(..)}.
         */
        public Builder setMatchStyle(@MatchStyles int matchStyle) {
            if (matchStyle != MATCH_STYLE_FIRST_ONLY && matchStyle != MATCH_STYLE_ALL) {
                throw new IllegalArgumentException(
                        "Invalid matchType - must be MATCH_FIRST_ONLY or MATCH_ALL");
            }
            mMatchStyle = matchStyle;
            return this;
        }

        /**
         * Configure whether a subscribe terminate notification
         * {@link WifiNanSessionCallback#onSessionTerminated(int)} is reported
         * back to the callback.
         *
         * @param enable If true the terminate callback will be called when the
         *            subscribe is terminated. Otherwise it will not be called.
         *
         * @return The builder to facilitate chaining
         *         {@code builder.setXXX(..).setXXX(..)}.
         */
        public Builder setEnableTerminateNotification(boolean enable) {
            mEnableTerminateNotification = enable;
            return this;
        }

        /**
         * Build {@link SubscribeConfig} given the current requests made on the
         * builder.
         */
        public SubscribeConfig build() {
            return new SubscribeConfig(mServiceName, mServiceSpecificInfo, mMatchFilter,
                    mSubscribeType, mSubscribeCount, mTtlSec, mMatchStyle,
                    mEnableTerminateNotification);
        }
    }
}
