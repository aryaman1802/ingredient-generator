package view;

import java.awt.*;
import javax.swing.JFrame;

public interface SignupView {
    /** Display a successful signup message (e.g. “Account created!”). */
    void showSignupSuccess(String message);
    /** Display an error (e.g. “Username already taken.”). */
    void showSignupFailure(String message);

    /** Expose the frame so callers can do frame.setVisible(true/false). */
    JFrame getFrame();
}


