/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xu;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author xBerWise
 */
public class sessioncookie {

    private final List<String> SessionCookie = new LinkedList<>();

    public static sessioncookie getInstance() {
        return sessioncookieHolder.INSTANCE;
    }

    private static class sessioncookieHolder {

        private static final sessioncookie INSTANCE = new sessioncookie();
    }

    public void setSessionCookie(String EmployeeName, String EmployeeId, String dateandtime) {
        SessionCookie.add(0, EmployeeName);
        SessionCookie.add(1, EmployeeId);
        SessionCookie.add(2, dateandtime);

    }

    public List getSessionCookie() {
        return SessionCookie;
    }

    public void DestroySession() {
        while (!SessionCookie.isEmpty()) {
            SessionCookie.remove(SessionCookie.size() - 1);
        }
    }
}
