package com.example.identitylab;
import java.util.regex.Pattern;
public final class IdentityPolicy {
    private static final Pattern HEX16 = Pattern.compile("[0-9a-f]{16}");
    private IdentityPolicy() {}
    public static boolean isValidCustomMid(String value) { return value != null && HEX16.matcher(value.trim()).matches(); }
}
