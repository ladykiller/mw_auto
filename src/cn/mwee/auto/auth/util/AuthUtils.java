package cn.mwee.auto.auth.util;

import org.apache.shiro.SecurityUtils;

/**
 * Created by Administrator on 2016/7/29.
 */
public class AuthUtils {
    public static String getCurrUserName() {
        Object principal = SecurityUtils.getSubject().getPrincipal();
        return principal == null ? "" : principal.toString();
    }
}
