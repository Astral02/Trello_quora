package com.upgrad.quora.service.common;

import java.util.HashMap;
import java.util.Map;

/**
 * Enum used for defining the role of the user.
 * For admin user we have admin and for non-admin user we have nonadmin.
 */
public enum RoleType {

    admin(0), nonadmin(1);

    private static final Map<Integer, RoleType> Lookup = new HashMap<>();

    static {
        for (RoleType userStatus : RoleType.values()) {
            Lookup.put(userStatus.getCode(), userStatus);
        }
    }

    private final int code;

    private RoleType(final int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static RoleType getEnum(final int code) {
        return Lookup.get(code);
    }

    public static void main(String[] args) {
        System.out.println(RoleType.getEnum(1).toString());
    }
}