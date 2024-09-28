package ru.tensor.sbis.common.util;

import android.os.ParcelUuid;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import androidx.annotation.Nullable;
import timber.log.Timber;

/**
 * Utility class for converting UUID and String
 */
public class UUIDUtils {

    public static final String UUID_PATTERN = "[0-9a-fA-F]{8}(?:-[0-9a-fA-F]{4}){3}-[0-9a-fA-F]{12}";
    public static final UUID NIL_UUID = new UUID(0, 0);

    public static List<String> toStrings(@Nullable Collection<UUID> uuidsList) {
        List<String> stringUuids = new ArrayList<>();
        if (uuidsList != null) {
            for (UUID uuid : uuidsList) {
                stringUuids.add(toString(uuid));
            }
        }
        return stringUuids;
    }

    public static List<UUID> fromStrings(@Nullable Collection<String> stringUuidsList) {
        List<UUID> uuids = new ArrayList<>();
        if (stringUuidsList != null) {
            for (String uuid : stringUuidsList) {
                uuids.add(fromString(uuid));
            }
        }
        return uuids;
    }

    public static UUID validateUuid(@Nullable String uuid) {
        if (uuid != null && uuid.matches(UUID_PATTERN)) {
            UUID result = fromString(uuid);
            return result != null ? result : NIL_UUID;
        }
        return NIL_UUID;
    }

    public static UUID fromString(String uuid) {
        try {
            return uuid == null ? null : UUID.fromString(uuid);
        } catch (IllegalArgumentException exception) {
            Timber.e(exception, "Exception occurred on trying to create UUID from the invalid string. Inform controller or server.");
            return null;
        }
    }

    public static String toString(UUID uuid) {
        return uuid == null ? null : uuid.toString();
    }


    public static List<ParcelUuid> toParcelUuids(Collection<UUID> uuids) {
        List<ParcelUuid> result = new ArrayList<>();
        if (uuids != null && !uuids.isEmpty()) {
            for (UUID uuid : uuids) {
                result.add(new ParcelUuid(uuid));
            }
        }
        return result;
    }

    public static List<UUID> fromParcelUuids(Collection<ParcelUuid> uuids) {
        List<UUID> result = new ArrayList<>();
        if (uuids != null && !uuids.isEmpty()) {
            for (ParcelUuid uuid : uuids) {
                result.add(uuid.getUuid());
            }
        }
        return result;
    }

    public static boolean equals(@Nullable UUID a, @Nullable UUID b) {
        return Objects.equals(a, b);
    }

    public static boolean equals(@Nullable String stringUuid, @Nullable UUID uuid) {
        UUID b = UUIDUtils.fromString(stringUuid);
        return UUIDUtils.equals(uuid, b);
    }

    public static boolean equals(@Nullable UUID uuid, @Nullable String stringUuid) {
        return UUIDUtils.equals(stringUuid, uuid);
    }

    public static boolean isNilUuid(@Nullable UUID uuid) {
        return UUIDUtils.equals(uuid, NIL_UUID);
    }

    private UUIDUtils() {
    }
}
