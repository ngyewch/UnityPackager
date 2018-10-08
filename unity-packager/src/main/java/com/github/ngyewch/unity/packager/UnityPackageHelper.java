package com.github.ngyewch.unity.packager;

import java.util.UUID;

public class UnityPackageHelper {

    public static final String META_PROPERTY_NAME_FILE_FORMAT_VERSION = "fileFormatVersion";
    public static final String META_PROPERTY_NAME_GUID = "guid";
    public static final int META_FILE_FORMAT_VERSION = 2;

    public static String generateGuid() {
        final StringBuilder sb = new StringBuilder(32);
        final UUID uuid = UUID.randomUUID();
        toHexString(sb, uuid.getLeastSignificantBits());
        toHexString(sb, uuid.getMostSignificantBits());
        return sb.toString();
    }

    private static void toHexString(StringBuilder sb, long bits) {
        long v = bits;
        for (int i = 0; i < 8; i++) {
            sb.insert(0, String.format("%02x", (v & 0xff)));
            v >>= 8;
        }
    }
}
