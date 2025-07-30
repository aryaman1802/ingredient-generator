package view;

import javax.swing.JFrame;
import entity.RegularUser;

/**
 * View interface for the login screen.
 */
public interface LoginView {
    /**
     * Called when login is successful. The user object can be used to prefill user-specific data.
     */
    void showLoginSuccess(RegularUser user);

    /**
     * Called when login fails. Displays an error message.
     */
    void showLoginFailure(String message);

    /**
     * Prefill the username field, e.g. after sign-up.
     */
    void prefillUsername(String username);

    /**
     * Expose the frame so the controller can switch visibility.
     */
    JFrame getFrame();
}


//package view;
//
//import entity.RegularUser;
//
//public interface LoginView {
//    void showLoginSuccess(RegularUser user);
//    void showLoginFailure(String message);
//}
