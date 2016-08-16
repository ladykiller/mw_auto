package cn.mwee.auto.auth.util;

import cn.mwee.auto.auth.model.AuthUser;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.DefaultSessionKey;
import org.apache.shiro.subject.Subject;

/**
 * Created by Administrator on 2016/7/29.
 */
public class AuthUtils {
    public static String getCurrUserName() {
        Object principal = SecurityUtils.getSubject().getPrincipal();
        return principal == null ? "" : principal.toString();
    }

    public static AuthUser getCurrentUser(String token) {
        AuthUser user = null;
        try {
            Session session = SecurityUtils.getSecurityManager().getSession(new DefaultSessionKey(token));
            user = (AuthUser)session.getAttribute("currentUser");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }
}
