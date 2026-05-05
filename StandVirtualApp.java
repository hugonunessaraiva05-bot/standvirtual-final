package standvirtual;

import standvirtual.ui.StandFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public final class StandVirtualApp {
    private StandVirtualApp() {
    }

    public static void main(String[] args) {
        configurarLookAndFeel();
        SwingUtilities.invokeLater(() -> new StandFrame().setVisible(true));
    }

    private static void configurarLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Fallback silencioso para o look and feel por defeito.
        }
    }
}
