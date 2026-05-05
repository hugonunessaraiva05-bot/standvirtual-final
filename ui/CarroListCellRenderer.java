package standvirtual.ui;

import standvirtual.model.Carro;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

public final class CarroListCellRenderer extends JPanel implements ListCellRenderer<Carro> {
    private final JLabel nomeLabel = new JLabel();
    private final JLabel resumoLabel = new JLabel();
    private final JLabel destaqueLabel = new JLabel();
    private final JLabel precoLabel = new JLabel();

    public CarroListCellRenderer() {
        setLayout(new BorderLayout(16, 0));
        setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        JPanel texto = new JPanel();
        texto.setOpaque(false);
        texto.setLayout(new BoxLayout(texto, BoxLayout.Y_AXIS));

        nomeLabel.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 18));
        resumoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        resumoLabel.setForeground(new Color(100, 109, 126));
        destaqueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        destaqueLabel.setForeground(new Color(198, 142, 63));

        precoLabel.setFont(new Font("Segoe UI Bold", Font.PLAIN, 18));

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

        Color fundo = isSelected ? new Color(20, 28, 45) : new Color(248, 249, 252);
        Color texto = isSelected ? Color.WHITE : new Color(27, 33, 47);
        Color subtitulo = isSelected ? new Color(204, 213, 240) : new Color(100, 109, 126);

        setBackground(fundo);
        nomeLabel.setForeground(texto);
        precoLabel.setForeground(isSelected ? new Color(255, 211, 121) : new Color(20, 28, 45));
        resumoLabel.setForeground(subtitulo);
        destaqueLabel.setForeground(isSelected ? new Color(255, 211, 121) : new Color(198, 142, 63));

        return this;
    }
}
