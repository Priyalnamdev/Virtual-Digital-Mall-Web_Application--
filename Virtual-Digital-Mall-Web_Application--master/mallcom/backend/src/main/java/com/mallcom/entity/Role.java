package com.mallcom.entity;

/**
 * Application roles.
 * USER  -> assigned automatically on self-registration.
 * ADMIN -> never created through the public API, only inserted directly in the database.
 */
public enum Role {
    ADMIN,
    USER
}
