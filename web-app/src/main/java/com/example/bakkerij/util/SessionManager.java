package com.example.bakkerij.util;

import io.javalin.http.Context;

import java.util.UUID;

public class SessionManager {
    private static final String SESSION_ID_COOKIE = "sessionId";
    private static final int COOKIE_MAX_AGE = 86400 * 30; // 30 days

    public String getOrCreateSessionId(Context ctx) {
        String sessionId = ctx.cookie(SESSION_ID_COOKIE);
        if (sessionId == null) {
            sessionId = UUID.randomUUID().toString();
            ctx.cookie(SESSION_ID_COOKIE, sessionId, COOKIE_MAX_AGE);
        }
        return sessionId;
    }

    public String getSessionId(Context ctx) {
        return ctx.cookie(SESSION_ID_COOKIE);
    }
}
