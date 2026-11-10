import ui.MainFrame;

import javax.swing.SwingUtilities;

/** Entry point of the Image Triangulation Art Generator. */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
