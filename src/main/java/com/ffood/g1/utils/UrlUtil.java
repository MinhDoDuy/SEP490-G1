package com.ffood.g1.utils;

import javax.servlet.http.HttpServletRequest;

public class UrlUtil {
    public static String getBaseUrl(HttpServletRequest request) {
        String scheme = request.getScheme();             // http or https
        String serverName = request.getServerName();     // example.com
        int serverPort = request.getServerPort();        // 80, 443, etc.
        String contextPath = request.getContextPath();   // /your-context-path

        // Xây dựng URL cơ sở
        if ((serverPort == 80 || serverPort == 443)) {
            return scheme + "://" + serverName + contextPath;
        } else {
            return scheme + "://" + serverName + ":" + serverPort + contextPath;
        }
    }
}
