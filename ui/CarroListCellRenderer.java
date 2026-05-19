package standvirtual.ui;

import standvirtual.model.Carro;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

@SuppressWarnings("serial")
public final class CarroListCellRenderer extends JPanel implements ListCellRenderer<Carro> {
    private static final long serialVersionUID = 1L;

    private final JLabel nomeLabel = new JLabel();
    private final JLabel resumoLabel = new JLabel();
    private final JLabel destaqueLabel = new JLabel();
    private final JLabel precoLabel = new JLabel();

    public CarroListCellRenderer() {
        setOpaque(true);
        setLayout(new BorderLayout(18, 0));
        setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        JPanel texto = new JPanel();
        texto.setOpaque(false);
        texto.setLayout(new BoxLayout(texto, BoxLayout.Y_AXIS));

        nomeLabel.setFont(new Font("Segoe UI Bold", Font.PLAIN, 18));
        resumoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        resumoLabel.setForeground(new Color(100, 109, 126));
        destaqueLabel.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 12));
        destaqueLabel.setForeground(new Color(198, 142, 63));

        precoLabel.setFont(new Font("Segoe UI Bold", Font.PLAIN, 20));
        precoLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        precoLabel.setVerticalAlignment(SwingConstants.TOP);

        texto.add(nomeLabel);
        texto.add(Box.createRigidArea(new Dimension(0, 6)));
        texto.add(resumoLabel);
        texto.add(Box.createRigidArea(new Dimension(0, 6)));
        texto.add(destaqueLabel);

        add(texto, BorderLayout.CENTER);
        add(precoLabel, BorderLayout.EAST);
    }

    @Override
    public Component getListCellRendererComponent(
            JList<? extends Carro> list,
            Carro carro,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {
        nomeLabel.setText(carro.getNomeCompleto());
        resumoLabel.setText(carro.getResumo());
        destaqueLabel.setText(carro.getCategoria().getLabel() + "  |  " + carro.getDestaque());
        precoLabel.setText(carro.getPrecoFormatado());

        Color fundo = isSelected ? new Color(20, 28, 45) : Color.WHITE;
        Color texto = isSelected ? Color.WHITE : new Color(27, 33, 47);
        Color subtitulo = isSelected ? new Color(204, 213, 240) : new Color(100, 109, 126);

        setBackground(fundo);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 4, 0, 0,
                                isSelected ? new Color(255, 211, 121) : Color.WHITE),
                        BorderFactory.createLineBorder(isSelected ? new Color(20, 28, 45) : new Color(226, 230, 238))),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)));
        nomeLabel.setForeground(texto);
        precoLabel.setForeground(isSelected ? new Color(255, 211, 121) : new Color(20, 28, 45));
        resumoLabel.setForeground(subtitulo);
        destaqueLabel.setForeground(isSelected ? new Color(255, 211, 121) : new Color(198, 142, 63));

        return this;
    }
}
