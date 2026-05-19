package standvirtual.ui;

import standvirtual.model.Carro;
import standvirtual.model.Categoria;
import standvirtual.model.Combustivel;
import standvirtual.service.CatalogoStand;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StandFrame extends JFrame {
    private static final int IMAGEM_LARGURA = 430;
    private static final int IMAGEM_ALTURA = 240;
    private static final int MAX_REDIRECIONAMENTOS_IMAGEM = 5;
    private static final Map<String, ImageIcon> CACHE_IMAGENS = new HashMap<String, ImageIcon>();
    private static final NumberFormat EURO = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-PT"));
    private static final NumberFormat INTEIROS = NumberFormat.getIntegerInstance(Locale.forLanguageTag("pt-PT"));
    private static final double TAXA_CREDITO_ANUAL = 0.069;
    private static final String BASE_UPLOAD_WIKIMEDIA = "https://upload.wikimedia.org/wikipedia/commons";
    private static final Pattern PADRAO_IMAGEM_WIKIPEDIA = Pattern.compile(
            "\"(?:originalimage|thumbnail)\"\\s*:\\s*\\{.*?\"source\"\\s*:\\s*\"([^\"]+)\"",
            Pattern.DOTALL);
    private static final Pattern PADRAO_IMAGEM_COMMONS_THUMB = Pattern.compile(
            "\"thumburl\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern PADRAO_IMAGEM_COMMONS_URL = Pattern.compile(
            "\"url\"\\s*:\\s*\"([^\"]+)\"");

    private final CatalogoStand catalogo = new CatalogoStand();
    private final JTextField pesquisaField = new JTextField();
    private final JComboBox<Categoria> categoriaCombo = new JComboBox<Categoria>();
    private final JComboBox<Combustivel> combustivelCombo = new JComboBox<Combustivel>();
    private final JComboBox<CatalogoStand.Ordenacao> ordenacaoCombo =
            new JComboBox<CatalogoStand.Ordenacao>(CatalogoStand.Ordenacao.values());
    private final DefaultListModel<Carro> carrosModel = new DefaultListModel<Carro>();
    private final JList<Carro> carrosList = new JList<Carro>(carrosModel);

    private final JLabel tituloDetalhe = new JLabel("Selecione um veiculo");
    private final JLabel precoDetalhe = new JLabel("--");
    private final JLabel etiquetaDetalhe = criarBadge();
    private final JLabel specsDetalhe = new JLabel("Use os filtros para explorar o catalogo.");
    private final JTextArea descricaoDetalhe = new JTextArea();
    private final JLabel anoValorLabel = criarValorInfo("--");
    private final JLabel potenciaValorLabel = criarValorInfo("--");
    private final JLabel kilometrosValorLabel = criarValorInfo("--");
    private final JLabel transmissaoValorLabel = criarValorInfo("--");
    private final JLabel entradaValorLabel = criarValorInfo("--");
    private final JLabel mensalidadeValorLabel = criarValorInfo("--");
    private final JLabel prazoValorLabel = criarValorInfo("--");
    private final JLabel taxaValorLabel = criarValorInfo("--");
    private final JLabel imagemLabel = new JLabel("Selecione um carro para carregar a imagem.", SwingConstants.CENTER);
    private final JLabel fonteImagemLabel = new JLabel("Fonte da imagem");
    private final JComboBox<Integer> prazoCreditoCombo = new JComboBox<Integer>(new Integer[]{24, 36, 48, 60, 72, 84});
    private final JButton comprarProntoButton = new JButton("Comprar a pronto");
    private final JButton comprarCreditoButton = new JButton("Comprar com credito");
    private final JTextArea resumoCreditoArea = new JTextArea();
    private JScrollPane listaScroll;
    private TitledBorder listaBorder;
    private Carro carroSelecionado;

    public StandFrame() {
        super("Velocity Showroom");
        configurarJanela();
        setContentPane(criarLayoutPrincipal());
        aplicarFiltros();
    }

    private void configurarJanela() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1320, 820));
        setSize(new Dimension(1400, 880));
        setLocationRelativeTo(null);
    }

    private JPanel criarLayoutPrincipal() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(241, 243, 248));
        root.add(criarHeader(), BorderLayout.NORTH);
        root.add(criarConteudo(), BorderLayout.CENTER);
        return root;
    }

    private JPanel criarHeader() {
        JPanel header = new JPanel(new BorderLayout(24, 18));
        header.setBackground(new Color(12, 18, 31));
        header.setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));

        JPanel texto = new JPanel();
        texto.setOpaque(false);
        texto.setLayout(new BoxLayout(texto, BoxLayout.Y_AXIS));

        JLabel eyebrow = new JLabel("STAND VIRTUAL PREMIUM");
        eyebrow.setForeground(new Color(255, 211, 121));
        eyebrow.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 13));

        JLabel titulo = new JLabel("Escolha o carro certo com uma experiencia clara e moderna.");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Segoe UI Bold", Font.PLAIN, 31));

        JLabel subtitulo = new JLabel("Pesquisa rapida, galeria online e detalhe completo num unico painel.");
        subtitulo.setForeground(new Color(194, 201, 218));
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        texto.add(eyebrow);
        texto.add(Box.createRigidArea(new Dimension(0, 10)));
        texto.add(titulo);
        texto.add(Box.createRigidArea(new Dimension(0, 10)));
        texto.add(subtitulo);

        JPanel destaques = new JPanel(new GridLayout(1, 3, 12, 0));
        destaques.setOpaque(false);
        for (Carro carro : catalogo.getDestaques()) {
            destaques.add(criarMiniCard(carro));
        }

        header.add(texto, BorderLayout.NORTH);
        header.add(destaques, BorderLayout.CENTER);
        return header;
    }

    private JPanel criarMiniCard(Carro carro) {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setOpaque(true);
        card.setBackground(new Color(18, 28, 46));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(3, 0, 0, 0, new Color(255, 211, 121)),
                        BorderFactory.createLineBorder(new Color(42, 56, 84))),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)));

        JPanel texto = new JPanel();
        texto.setOpaque(false);
        texto.setLayout(new BoxLayout(texto, BoxLayout.Y_AXIS));

        JLabel selo = new JLabel("Selecao em destaque");
        selo.setForeground(new Color(255, 211, 121));
        selo.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 11));

        JLabel nome = new JLabel(carro.getNomeCompleto());
        nome.setForeground(Color.WHITE);
        nome.setFont(new Font("Segoe UI Bold", Font.PLAIN, 16));

        JLabel destaque = new JLabel(carro.getDestaque());
        destaque.setForeground(new Color(194, 201, 218));
        destaque.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JLabel preco = new JLabel(carro.getPrecoFormatado());
        preco.setForeground(new Color(255, 211, 121));
        preco.setFont(new Font("Segoe UI Bold", Font.PLAIN, 18));

        JLabel categoria = new JLabel(carro.getCategoria().getLabel(), SwingConstants.RIGHT);
        categoria.setForeground(new Color(194, 201, 218));
        categoria.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 11));

        JPanel rodape = new JPanel(new BorderLayout(12, 0));
        rodape.setOpaque(false);
        rodape.add(preco, BorderLayout.WEST);
        rodape.add(categoria, BorderLayout.EAST);

        texto.add(selo);
        texto.add(Box.createRigidArea(new Dimension(0, 8)));
        texto.add(nome);
        texto.add(Box.createRigidArea(new Dimension(0, 6)));
        texto.add(destaque);

        card.add(texto, BorderLayout.CENTER);
        card.add(rodape, BorderLayout.SOUTH);
        return card;
    }

    private JPanel criarConteudo() {
        JPanel content = new JPanel(new BorderLayout(18, 18));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        content.add(criarFiltros(), BorderLayout.NORTH);
        content.add(criarSplitPane(), BorderLayout.CENTER);
        return content;
    }

    private JPanel criarFiltros() {
        JPanel filtros = new JPanel(new GridLayout(1, 5, 12, 0));
        filtros.setOpaque(true);
        filtros.setBackground(Color.WHITE);
        filtros.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(224, 227, 236)),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));

        categoriaCombo.setModel(new DefaultComboBoxModel<Categoria>(Categoria.values()));
        categoriaCombo.insertItemAt(null, 0);
        categoriaCombo.setSelectedIndex(0);
        categoriaCombo.setRenderer(criarRendererCombo(Categoria::toString, "Todas"));

        combustivelCombo.setModel(new DefaultComboBoxModel<Combustivel>(Combustivel.values()));
        combustivelCombo.insertItemAt(null, 0);
        combustivelCombo.setSelectedIndex(0);
        combustivelCombo.setRenderer(criarRendererCombo(Combustivel::toString, "Todos"));

        ordenacaoCombo.setRenderer(criarRendererCombo(CatalogoStand.Ordenacao::toString, "Ordenacao"));
        adicionarPesquisaReativa();

        JButton limpar = new JButton("Limpar filtros");
        estilizarBotaoSecundario(limpar);
        limpar.addActionListener(event -> {
            pesquisaField.setText("");
            categoriaCombo.setSelectedIndex(0);
            combustivelCombo.setSelectedIndex(0);
            ordenacaoCombo.setSelectedItem(CatalogoStand.Ordenacao.PRECO_ASC);
            aplicarFiltros();
        });

        pesquisaField.addActionListener(event -> aplicarFiltros());
        categoriaCombo.addActionListener(event -> reagirAFiltroAlterado());
        combustivelCombo.addActionListener(event -> reagirAFiltroAlterado());
        ordenacaoCombo.addActionListener(event -> reagirAFiltroAlterado());

        filtros.add(criarCampoFiltro("Pesquisar", pesquisaField));
        filtros.add(criarCampoFiltro("Categoria", categoriaCombo));
        filtros.add(criarCampoFiltro("Combustivel", combustivelCombo));
        filtros.add(criarCampoFiltro("Ordenar por", ordenacaoCombo));
        filtros.add(criarCampoFiltro("Acao", limpar));
        return filtros;
    }

    private void adicionarPesquisaReativa() {
        pesquisaField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent event) {
                aplicarFiltros();
            }

            @Override
            public void removeUpdate(DocumentEvent event) {
                aplicarFiltros();
            }

            @Override
            public void changedUpdate(DocumentEvent event) {
                aplicarFiltros();
            }
        });
    }

    private JPanel criarCampoFiltro(String titulo, Component componente) {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);

        JLabel label = new JLabel(titulo);
        label.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 13));
        label.setForeground(new Color(70, 77, 94));

        estilizarComponenteCampo(componente);

        panel.add(label, BorderLayout.NORTH);
        panel.add(componente, BorderLayout.CENTER);
        return panel;
    }

    private void estilizarComponenteCampo(Component componente) {
        if (componente instanceof JTextField) {
            JTextField textField = (JTextField) componente;
            textField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(205, 210, 222)),
                    BorderFactory.createEmptyBorder(10, 12, 10, 12)));
            textField.setBackground(Color.WHITE);
            textField.setForeground(new Color(27, 33, 47));
            textField.setCaretColor(new Color(20, 28, 45));
        } else if (componente instanceof JComboBox) {
            JComboBox<?> comboBox = (JComboBox<?>) componente;
            comboBox.setFocusable(false);
            comboBox.setBackground(Color.WHITE);
            comboBox.setBorder(BorderFactory.createLineBorder(new Color(205, 210, 222)));
        } else if (componente instanceof JButton) {
            JButton button = (JButton) componente;
            button.setFocusable(false);
            button.setBackground(Color.WHITE);
            button.setForeground(new Color(27, 33, 47));
            button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(205, 210, 222)),
                    BorderFactory.createEmptyBorder(10, 14, 10, 14)));
        }

        if (componente instanceof JComponent) {
            JComponent componenteVisual = (JComponent) componente;
            componenteVisual.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            Dimension tamanho = componenteVisual.getPreferredSize();
            componenteVisual.setPreferredSize(new Dimension(Math.max(tamanho.width, 120), 42));
        }
    }

    private JSplitPane criarSplitPane() {
        carrosList.setCellRenderer(new CarroListCellRenderer());
        carrosList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        carrosList.setFixedCellHeight(104);
        carrosList.setBackground(new Color(241, 243, 248));
        carrosList.setSelectionBackground(new Color(20, 28, 45));
        carrosList.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        carrosList.addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                atualizarDetalhe(carrosList.getSelectedValue());
            }
        });

        listaBorder = BorderFactory.createTitledBorder("Catalogo");
        listaBorder.setTitleColor(new Color(70, 77, 94));
        listaBorder.setTitleFont(new Font("Segoe UI Semibold", Font.PLAIN, 13));
        listaScroll = new JScrollPane(carrosList);
        listaScroll.setBorder(listaBorder);
        listaScroll.getViewport().setBackground(new Color(241, 243, 248));
        listaScroll.getVerticalScrollBar().setUnitIncrement(16);

        JPanel detalhe = criarPainelDetalhe();
        JScrollPane detalheScroll = new JScrollPane(detalhe);
        detalheScroll.setBorder(null);
        detalheScroll.getViewport().setBackground(new Color(241, 243, 248));
        detalheScroll.getVerticalScrollBar().setUnitIncrement(16);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listaScroll, detalheScroll);
        splitPane.setResizeWeight(0.50);
        splitPane.setBorder(null);
        splitPane.setDividerSize(10);
        return splitPane;
    }

    private JPanel criarPainelDetalhe() {
        JPanel painel = new JPanel(new BorderLayout(0, 18));
        painel.setBackground(Color.WHITE);
        painel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(224, 227, 236)),
                BorderFactory.createEmptyBorder(26, 26, 26, 26)));

        imagemLabel.setOpaque(true);
        imagemLabel.setBackground(new Color(245, 247, 250));
        imagemLabel.setForeground(new Color(92, 100, 118));
        imagemLabel.setPreferredSize(new Dimension(IMAGEM_LARGURA, IMAGEM_ALTURA));
        imagemLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(227, 231, 239)),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));

        fonteImagemLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        fonteImagemLabel.setForeground(new Color(72, 99, 167));
        fonteImagemLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        fonteImagemLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                abrirPaginaImagem();
            }
        });

        JPanel topo = new JPanel(new BorderLayout(0, 18));
        topo.setOpaque(false);

        JPanel nomes = new JPanel();
        nomes.setOpaque(false);
        nomes.setLayout(new BoxLayout(nomes, BoxLayout.Y_AXIS));

        tituloDetalhe.setFont(new Font("Segoe UI Bold", Font.PLAIN, 28));
        tituloDetalhe.setForeground(new Color(20, 28, 45));

        precoDetalhe.setFont(new Font("Segoe UI Bold", Font.PLAIN, 24));
        precoDetalhe.setForeground(new Color(197, 131, 32));

        nomes.add(etiquetaDetalhe);
        nomes.add(Box.createRigidArea(new Dimension(0, 12)));
        nomes.add(tituloDetalhe);
        nomes.add(Box.createRigidArea(new Dimension(0, 10)));
        nomes.add(precoDetalhe);

        topo.add(nomes, BorderLayout.NORTH);
        topo.add(imagemLabel, BorderLayout.CENTER);
        topo.add(fonteImagemLabel, BorderLayout.SOUTH);

        JPanel grelha = new JPanel(new GridLayout(2, 2, 12, 12));
        grelha.setOpaque(false);
        grelha.add(criarInfoCard("Ano", anoValorLabel));
        grelha.add(criarInfoCard("Potencia", potenciaValorLabel));
        grelha.add(criarInfoCard("Kilometragem", kilometrosValorLabel));
        grelha.add(criarInfoCard("Transmissao", transmissaoValorLabel));

        specsDetalhe.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
        specsDetalhe.setForeground(new Color(92, 100, 118));

        descricaoDetalhe.setLineWrap(true);
        descricaoDetalhe.setWrapStyleWord(true);
        descricaoDetalhe.setEditable(false);
        descricaoDetalhe.setOpaque(false);
        descricaoDetalhe.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        descricaoDetalhe.setForeground(new Color(58, 65, 81));
        descricaoDetalhe.setText("Selecione um modelo para ver a descricao comercial e os detalhes tecnicos.");

        JPanel blocosTexto = new JPanel();
        blocosTexto.setOpaque(false);
        blocosTexto.setLayout(new BoxLayout(blocosTexto, BoxLayout.Y_AXIS));
        blocosTexto.add(criarPainelTextoDetalhe("Visao geral", specsDetalhe));
        blocosTexto.add(Box.createRigidArea(new Dimension(0, 14)));
        blocosTexto.add(criarPainelTextoDetalhe("Descricao", descricaoDetalhe));

        JPanel corpo = new JPanel(new BorderLayout(0, 18));
        corpo.setOpaque(false);
        corpo.add(grelha, BorderLayout.NORTH);
        corpo.add(blocosTexto, BorderLayout.CENTER);

        painel.add(topo, BorderLayout.NORTH);
        painel.add(corpo, BorderLayout.CENTER);
        painel.add(criarPainelCompra(), BorderLayout.SOUTH);
        return painel;
    }

    private JPanel criarPainelCompra() {
        JPanel painelCompra = new JPanel(new BorderLayout(0, 16));
        painelCompra.setBackground(new Color(250, 245, 235));
        painelCompra.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(232, 220, 197)),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)));

        JLabel titulo = new JLabel("Comprar este carro");
        titulo.setFont(new Font("Segoe UI Bold", Font.PLAIN, 20));
        titulo.setForeground(new Color(20, 28, 45));

        JLabel subtitulo = new JLabel("Veja a simulacao de credito e escolha como pretende avancar.");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitulo.setForeground(new Color(92, 100, 118));

        JPanel topo = new JPanel();
        topo.setOpaque(false);
        topo.setLayout(new BoxLayout(topo, BoxLayout.Y_AXIS));
        topo.add(titulo);
        topo.add(Box.createRigidArea(new Dimension(0, 6)));
        topo.add(subtitulo);

        prazoCreditoCombo.setSelectedItem(Integer.valueOf(84));
        prazoCreditoCombo.setRenderer(criarRendererCombo(value -> value + " meses", "Prazo"));
        prazoCreditoCombo.setEnabled(false);
        prazoCreditoCombo.addActionListener(event -> atualizarResumoCredito());

        JPanel grelha = new JPanel(new GridLayout(2, 2, 12, 12));
        grelha.setOpaque(false);
        grelha.add(criarInfoCard("Entrada (10%)", entradaValorLabel));
        grelha.add(criarInfoCard("Prestacao mensal", mensalidadeValorLabel));
        grelha.add(criarInfoCard("Prazo", prazoValorLabel));
        grelha.add(criarInfoCard("Taxa estimada", taxaValorLabel));

        resumoCreditoArea.setLineWrap(true);
        resumoCreditoArea.setWrapStyleWord(true);
        resumoCreditoArea.setEditable(false);
        resumoCreditoArea.setOpaque(false);
        resumoCreditoArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        resumoCreditoArea.setForeground(new Color(58, 65, 81));
        resumoCreditoArea.setText("Selecione um carro para ver as condicoes de compra.");

        estilizarBotaoAcao(comprarProntoButton, new Color(20, 28, 45), Color.WHITE);
        comprarProntoButton.setEnabled(false);
        comprarProntoButton.addActionListener(event -> finalizarCompra(false));

        estilizarBotaoAcao(comprarCreditoButton, new Color(197, 131, 32), Color.WHITE);
        comprarCreditoButton.setEnabled(false);
        comprarCreditoButton.addActionListener(event -> finalizarCompra(true));

        JPanel controlos = new JPanel(new BorderLayout(12, 0));
        controlos.setOpaque(false);
        controlos.add(criarCampoFiltro("Prazo do credito", prazoCreditoCombo), BorderLayout.WEST);

        JPanel botoes = new JPanel(new GridLayout(1, 2, 12, 0));
        botoes.setOpaque(false);
        botoes.add(comprarProntoButton);
        botoes.add(comprarCreditoButton);
        controlos.add(botoes, BorderLayout.CENTER);

        painelCompra.add(topo, BorderLayout.NORTH);
        painelCompra.add(grelha, BorderLayout.CENTER);
        painelCompra.add(resumoCreditoArea, BorderLayout.SOUTH);

        JPanel wrapper = new JPanel(new BorderLayout(0, 14));
        wrapper.setOpaque(false);
        wrapper.add(painelCompra, BorderLayout.CENTER);
        wrapper.add(controlos, BorderLayout.SOUTH);
        return wrapper;
    }

    private void estilizarBotaoAcao(JButton botao, Color fundo, Color texto) {
        botao.setFocusable(false);
        botao.setFocusPainted(false);
        botao.setBorderPainted(false);
        botao.setBackground(fundo);
        botao.setForeground(texto);
        botao.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
    }

    private void estilizarBotaoSecundario(JButton botao) {
        botao.setFocusable(false);
        botao.setFocusPainted(false);
        botao.setBackground(new Color(248, 249, 252));
        botao.setForeground(new Color(27, 33, 47));
        botao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(205, 210, 222)),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)));
        botao.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 13));
    }

    private JLabel criarBadge() {
        JLabel badge = new JLabel("Showroom");
        badge.setOpaque(true);
        badge.setBackground(new Color(255, 241, 213));
        badge.setForeground(new Color(156, 98, 8));
        badge.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 12));
        badge.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        return badge;
    }

    private JPanel criarPainelTextoDetalhe(String titulo, JComponent conteudo) {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(new Color(248, 250, 253));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(228, 232, 240)),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)));

        JLabel tituloLabel = new JLabel(titulo);
        tituloLabel.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 13));
        tituloLabel.setForeground(new Color(70, 77, 94));

        card.add(tituloLabel, BorderLayout.NORTH);
        card.add(conteudo, BorderLayout.CENTER);
        return card;
    }

    private JPanel criarInfoCard(String titulo, JLabel valorLabel) {
        JPanel card = new JPanel(new BorderLayout(0, 6));
        card.setBackground(new Color(245, 247, 250));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(228, 232, 240)),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)));

        JLabel tituloLabel = new JLabel(titulo);
        tituloLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tituloLabel.setForeground(new Color(111, 118, 135));

        card.add(tituloLabel, BorderLayout.NORTH);
        card.add(valorLabel, BorderLayout.CENTER);
        return card;
    }

    private JLabel criarValorInfo(String valor) {
        JLabel label = new JLabel(valor);
        label.setFont(new Font("Segoe UI Bold", Font.PLAIN, 19));
        label.setForeground(new Color(20, 28, 45));
        return label;
    }

    private <T> ListCellRenderer<? super T> criarRendererCombo(
            final Function<T, String> formatador,
            final String opcaoVazia) {
        return (list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel(value == null ? opcaoVazia : formatador.apply(value));
            label.setOpaque(true);
            label.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
            label.setBackground(isSelected ? new Color(20, 28, 45) : Color.WHITE);
            label.setForeground(isSelected ? Color.WHITE : new Color(27, 33, 47));
            return label;
        };
    }

    private void reagirAFiltroAlterado() {
        aplicarFiltros();
    }

    private void aplicarFiltros() {
        Carro selecaoAtual = carroSelecionado;
        carrosModel.clear();
        List<Carro> resultado = catalogo.pesquisar(
                pesquisaField.getText(),
                (Categoria) categoriaCombo.getSelectedItem(),
                (Combustivel) combustivelCombo.getSelectedItem(),
                null,
                null,
                (CatalogoStand.Ordenacao) ordenacaoCombo.getSelectedItem());

        for (Carro carro : resultado) {
            carrosModel.addElement(carro);
        }

        atualizarTituloLista(resultado.size());

        if (resultado.isEmpty()) {
            carrosList.clearSelection();
            atualizarDetalhe(null);
            return;
        }

        if (selecaoAtual != null && selecionarCarroNaLista(selecaoAtual)) {
            return;
        }

        carrosList.setSelectedIndex(0);
        carrosList.ensureIndexIsVisible(0);
    }

    private void atualizarTituloLista(int totalResultados) {
        if (listaBorder == null || listaScroll == null) {
            return;
        }

        String titulo = totalResultados == 1
                ? "Catalogo (1 resultado)"
                : "Catalogo (" + totalResultados + " resultados)";
        listaBorder.setTitle(titulo);
        listaScroll.repaint();
    }

    private boolean selecionarCarroNaLista(Carro carro) {
        for (int indice = 0; indice < carrosModel.size(); indice++) {
            if (carrosModel.getElementAt(indice) == carro) {
                carrosList.setSelectedIndex(indice);
                carrosList.ensureIndexIsVisible(indice);
                return true;
            }
        }
        return false;
    }

    private void atualizarDetalhe(Carro carro) {
        carroSelecionado = carro;
        if (carro == null) {
            tituloDetalhe.setText("Nenhum carro encontrado");
            precoDetalhe.setText("--");
            etiquetaDetalhe.setText("Sem resultados");
            specsDetalhe.setText("Ajuste os filtros para voltar a encontrar veiculos.");
            descricaoDetalhe.setText("Experimente pesquisar por marca, modelo ou destaque comercial.");
            anoValorLabel.setText("--");
            potenciaValorLabel.setText("--");
            kilometrosValorLabel.setText("--");
            transmissaoValorLabel.setText("--");
            entradaValorLabel.setText("--");
            mensalidadeValorLabel.setText("--");
            prazoValorLabel.setText("--");
            taxaValorLabel.setText("--");
            imagemLabel.setIcon(null);
            imagemLabel.setText("Sem imagem disponivel para a pesquisa atual.");
            fonteImagemLabel.setText("Fonte da imagem");
            fonteImagemLabel.setEnabled(false);
            resumoCreditoArea.setText("Selecione um carro para ver as condicoes de compra.");
            prazoCreditoCombo.setEnabled(false);
            comprarProntoButton.setEnabled(false);
            comprarCreditoButton.setEnabled(false);
            return;
        }

        tituloDetalhe.setText(carro.getNomeCompleto());
        precoDetalhe.setText(carro.getPrecoFormatado());
        etiquetaDetalhe.setText(carro.getCategoria().getLabel() + "  |  " + carro.getCombustivel().getLabel());
        specsDetalhe.setText(carro.getResumo());
        descricaoDetalhe.setText(carro.getDescricao());
        anoValorLabel.setText(String.valueOf(carro.getAno()));
        potenciaValorLabel.setText(carro.getCavalos() + " cv");
        kilometrosValorLabel.setText(INTEIROS.format(carro.getQuilometros()) + " km");
        transmissaoValorLabel.setText(carro.getTransmissao());
        fonteImagemLabel.setText(carro.getImagemFonte() == null
                ? "Fonte da imagem"
                : "<html><u>" + carro.getImagemFonte() + "</u></html>");
        fonteImagemLabel.setEnabled(true);
        prazoCreditoCombo.setEnabled(true);
        comprarProntoButton.setEnabled(true);
        comprarCreditoButton.setEnabled(true);
        atualizarResumoCredito();
        carregarImagem(carro);
    }

    private void atualizarResumoCredito() {
        if (carroSelecionado == null) {
            return;
        }

        int meses = ((Integer) prazoCreditoCombo.getSelectedItem()).intValue();
        double entrada = carroSelecionado.getPreco() * 0.10;
        double montanteFinanciado = carroSelecionado.getPreco() - entrada;
        double taxaMensal = TAXA_CREDITO_ANUAL / 12.0;
        double mensalidade = calcularMensalidade(montanteFinanciado, taxaMensal, meses);

        entradaValorLabel.setText(EURO.format(entrada));
        mensalidadeValorLabel.setText(EURO.format(mensalidade));
        prazoValorLabel.setText(meses + " meses");
        taxaValorLabel.setText(String.format(Locale.forLanguageTag("pt-PT"), "%.1f%% TAEG", TAXA_CREDITO_ANUAL * 100.0));
        resumoCreditoArea.setText(
                "Entrada inicial de " + EURO.format(entrada)
                        + " e financiamento de " + EURO.format(montanteFinanciado)
                        + " em " + meses + " meses. Prestacao estimada: "
                        + EURO.format(mensalidade)
                        + "/mes. Simulacao informativa sujeita a aprovacao.");
    }

    private double calcularMensalidade(double valorFinanciado, double taxaMensal, int meses) {
        if (taxaMensal <= 0.0) {
            return valorFinanciado / meses;
        }

        double fator = Math.pow(1.0 + taxaMensal, meses);
        return valorFinanciado * (taxaMensal * fator) / (fator - 1.0);
    }

    private void finalizarCompra(boolean comCredito) {
        if (carroSelecionado == null) {
            return;
        }

        if (comCredito) {
            atualizarResumoCredito();
            JOptionPane.showMessageDialog(
                    this,
                    "Pedido de credito iniciado para " + carroSelecionado.getNomeCompleto()
                            + ".\nPrestacao estimada: " + mensalidadeValorLabel.getText()
                            + " durante " + prazoValorLabel.getText() + ".",
                    "Compra com credito",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(
                this,
                "Compra a pronto iniciada para " + carroSelecionado.getNomeCompleto()
                        + " no valor de " + carroSelecionado.getPrecoFormatado() + ".",
                "Compra a pronto",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void carregarImagem(final Carro carro) {
        String imagemLocal = obterCaminhoImagemLocal(carro);
        final String origemImagem = imagemLocal != null ? imagemLocal : carro.getImagemUrl();

        if (origemImagem == null || origemImagem.isEmpty()) {
            aplicarImagemFallback(carro, "Imagem nao configurada.");
            return;
        }

        ImageIcon emCache = CACHE_IMAGENS.get(origemImagem);
        if (emCache != null) {
            aplicarImagem(carro, emCache);
            return;
        }

        aplicarImagemFallback(carro, imagemLocal != null
                ? "A carregar imagem local..."
                : "A carregar imagem online...");

        SwingWorker<ImageIcon, Void> worker = new SwingWorker<ImageIcon, Void>() {
            @Override
            protected ImageIcon doInBackground() throws Exception {
                BufferedImage imagemOriginal = descarregarImagem(origemImagem);
                if (imagemOriginal == null) {
                    imagemOriginal = descarregarImagemDaPagina(carro.getImagemPaginaUrl());
                }
                if (imagemOriginal == null) {
                    return null;
                }

                return new ImageIcon(escalarImagem(imagemOriginal));
            }

            @Override
            protected void done() {
                try {
                    ImageIcon imagem = get();
                    if (imagem != null) {
                        CACHE_IMAGENS.put(origemImagem, imagem);
                        aplicarImagem(carro, imagem);
                    } else if (carro == carroSelecionado) {
                        aplicarImagemFallback(carro, imagemLocal != null
                                ? "Nao foi possivel carregar a imagem local."
                                : "Nao foi possivel carregar a imagem online.");
                    }
                } catch (Exception ignored) {
                    if (carro == carroSelecionado) {
                        aplicarImagemFallback(carro, imagemLocal != null
                                ? "Falha ao carregar imagem local."
                                : "Falha ao carregar imagem da internet.");
                    }
                }
            }
        };
        worker.execute();
    }

    private void aplicarImagem(Carro carro, Icon imagem) {
        if (carro != carroSelecionado) {
            return;
        }
        imagemLabel.setText("");
        imagemLabel.setIcon(imagem);
    }

    private BufferedImage descarregarImagem(String imagemUrl) throws Exception {
        File ficheiroLocal = new File(imagemUrl);
        if (ficheiroLocal.isFile()) {
            return ImageIO.read(ficheiroLocal);
        }

        String[] tentativas = criarTentativasImagem(imagemUrl);
        for (String tentativa : tentativas) {
            BufferedImage imagem = descarregarImagemTentativa(tentativa);
            if (imagem != null) {
                return imagem;
            }
        }
        return null;
    }

    private String obterCaminhoImagemLocal(Carro carro) {
        String nomeBase = carro.getNomeCompleto()
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-+|-+$", "");
        File ficheiro = new File("assets/carros/" + nomeBase + ".jpg");
        return ficheiro.isFile() ? ficheiro.getPath() : null;
    }

    private String[] criarTentativasImagem(String imagemUrl) {
        if (imagemUrl.contains("/wikipedia/commons/thumb/")) {
            return criarTentativasUploadWikimedia(imagemUrl);
        }
        if (imagemUrl.contains("commons.wikimedia.org/wiki/Special:FilePath/")) {
            return criarTentativasWikimedia(imagemUrl, "/wiki/Special:FilePath/");
        }
        if (imagemUrl.contains("commons.wikimedia.org/wiki/File:")) {
            return criarTentativasWikimedia(imagemUrl, "/wiki/File:");
        }
        return new String[]{imagemUrl};
    }

    private String[] criarTentativasUploadWikimedia(String imagemUrl) {
        int indiceThumb = imagemUrl.indexOf("/thumb/");
        if (indiceThumb < 0) {
            return new String[]{imagemUrl};
        }

        String caminhoRelativo = imagemUrl.substring(indiceThumb + "/thumb/".length());
        int ultimaBarra = caminhoRelativo.lastIndexOf('/');
        if (ultimaBarra < 0) {
            return new String[]{imagemUrl};
        }

        String caminhoFicheiro = caminhoRelativo.substring(0, ultimaBarra);
        int penultimaBarra = caminhoFicheiro.lastIndexOf('/');
        if (penultimaBarra < 0) {
            return new String[]{imagemUrl};
        }

        String pastasHash = caminhoFicheiro.substring(0, penultimaBarra);
        String nomeFicheiro = caminhoFicheiro.substring(penultimaBarra + 1);
        if (pastasHash.isEmpty() || nomeFicheiro.isEmpty()) {
            return new String[]{imagemUrl};
        }

        Set<String> tentativas = new LinkedHashSet<String>();
        tentativas.add(imagemUrl);
        tentativas.add(BASE_UPLOAD_WIKIMEDIA + "/thumb/" + pastasHash + "/" + nomeFicheiro + "/320px-" + nomeFicheiro);
        tentativas.add(BASE_UPLOAD_WIKIMEDIA + "/thumb/" + pastasHash + "/" + nomeFicheiro + "/640px-" + nomeFicheiro);
        tentativas.add(BASE_UPLOAD_WIKIMEDIA + "/thumb/" + pastasHash + "/" + nomeFicheiro + "/1280px-" + nomeFicheiro);
        tentativas.add(BASE_UPLOAD_WIKIMEDIA + "/" + pastasHash + "/" + nomeFicheiro);
        return tentativas.toArray(new String[0]);
    }

    private String[] criarTentativasWikimedia(String imagemUrl, String marcador) {
        String nomeFicheiro = extrairNomeFicheiroWikimedia(imagemUrl, marcador);
        if (nomeFicheiro == null) {
            return new String[]{imagemUrl};
        }

        String nomeCodificado = URLEncoder.encode(nomeFicheiro, StandardCharsets.UTF_8)
                .replace("+", "%20");
        String redirectBase = "https://commons.wikimedia.org/wiki/Special:Redirect/file/" + nomeCodificado;

        Set<String> tentativas = new LinkedHashSet<String>();
        tentativas.add(redirectBase + "?width=640");
        tentativas.add(redirectBase + "?width=1280");
        tentativas.add(redirectBase);
        tentativas.add(imagemUrl + (imagemUrl.contains("?") ? "&width=640" : "?width=640"));
        tentativas.add(imagemUrl + (imagemUrl.contains("?") ? "&download=1" : "?download=1"));
        tentativas.add(imagemUrl);

        return tentativas.toArray(new String[0]);
    }

    private String extrairNomeFicheiroWikimedia(String imagemUrl, String marcador) {
        int indice = imagemUrl.indexOf(marcador);
        if (indice < 0) {
            return null;
        }

        String nomeFicheiro = imagemUrl.substring(indice + marcador.length());
        int queryIndex = nomeFicheiro.indexOf('?');
        if (queryIndex >= 0) {
            nomeFicheiro = nomeFicheiro.substring(0, queryIndex);
        }

        nomeFicheiro = URLDecoder.decode(nomeFicheiro, StandardCharsets.UTF_8).trim();
        return nomeFicheiro.isEmpty() ? null : nomeFicheiro;
    }

    private BufferedImage descarregarImagemTentativa(String imagemUrl) throws Exception {
        String urlAtual = imagemUrl;
        for (int tentativa = 0; tentativa <= MAX_REDIRECIONAMENTOS_IMAGEM; tentativa++) {
            HttpURLConnection ligacao = abrirLigacaoImagem(urlAtual);
            try {
                int codigo = ligacao.getResponseCode();
                if (codigo >= 300 && codigo < 400) {
                    String destino = ligacao.getHeaderField("Location");
                    if (destino == null || destino.trim().isEmpty()) {
                        return null;
                    }
                    urlAtual = new URL(new URL(urlAtual), destino).toExternalForm();
                    continue;
                }

                if (codigo < 200 || codigo >= 300) {
                    return null;
                }

                InputStream stream = ligacao.getInputStream();
                try {
                    byte[] bytes = lerBytes(stream);
                    BufferedImage imagem = ImageIO.read(new java.io.ByteArrayInputStream(bytes));
                    if (imagem != null) {
                        return imagem;
                    }

                    ImageIcon icon = new ImageIcon(bytes);
                    if (icon.getIconWidth() > 0 && icon.getIconHeight() > 0) {
                        BufferedImage buffer = new BufferedImage(
                                icon.getIconWidth(),
                                icon.getIconHeight(),
                                BufferedImage.TYPE_INT_ARGB);
                        Graphics2D graphics = buffer.createGraphics();
                        try {
                            icon.paintIcon(null, graphics, 0, 0);
                        } finally {
                            graphics.dispose();
                        }
                        return buffer;
                    }

                    return null;
                } finally {
                    stream.close();
                }
            } finally {
                ligacao.disconnect();
            }
        }

        return null;
    }

    private HttpURLConnection abrirLigacaoImagem(String imagemUrl) throws Exception {
        HttpURLConnection ligacao = (HttpURLConnection) new URL(imagemUrl).openConnection();
        ligacao.setConnectTimeout(8000);
        ligacao.setReadTimeout(8000);
        ligacao.setUseCaches(false);
        ligacao.setInstanceFollowRedirects(false);
        ligacao.setRequestProperty("User-Agent", "Mozilla/5.0");
        ligacao.setRequestProperty("Accept", "image/jpeg,image/png,image/gif,image/*;q=0.8,*/*;q=0.5");
        return ligacao;
    }

    private BufferedImage descarregarImagemDaPagina(String paginaUrl) throws Exception {
        if (paginaUrl == null || paginaUrl.trim().isEmpty()) {
            return null;
        }

        if (paginaUrl.contains("commons.wikimedia.org/wiki/File:")) {
            return descarregarImagemCommons(paginaUrl);
        }
        if (paginaUrl.contains("en.wikipedia.org/wiki/")) {
            return descarregarThumbnailWikipedia(paginaUrl);
        }
        return null;
    }

    private BufferedImage descarregarThumbnailWikipedia(String paginaUrl) throws Exception {
        String titulo = extrairTituloDaPaginaWiki(paginaUrl);
        if (titulo.trim().isEmpty()) {
            return null;
        }

        String tituloCodificado = URLEncoder.encode(titulo, StandardCharsets.UTF_8).replace("+", "%20");
        String apiUrl = "https://en.wikipedia.org/api/rest_v1/page/summary/" + tituloCodificado;
        HttpURLConnection ligacao = (HttpURLConnection) new URL(apiUrl).openConnection();
        ligacao.setConnectTimeout(8000);
        ligacao.setReadTimeout(8000);
        ligacao.setUseCaches(false);
        ligacao.setInstanceFollowRedirects(true);
        ligacao.setRequestProperty("User-Agent", "Mozilla/5.0");
        ligacao.setRequestProperty("Accept", "application/json");
        try {
            if (ligacao.getResponseCode() < 200 || ligacao.getResponseCode() >= 300) {
                return null;
            }

            InputStream stream = ligacao.getInputStream();
            try {
                String json = new String(lerBytes(stream), StandardCharsets.UTF_8);
                Matcher matcher = PADRAO_IMAGEM_WIKIPEDIA.matcher(json);
                if (!matcher.find()) {
                    return null;
                }

                String imagemUrl = normalizarUrlJson(matcher.group(1));
                return descarregarImagem(imagemUrl);
            } finally {
                stream.close();
            }
        } finally {
            ligacao.disconnect();
        }
    }

    private BufferedImage descarregarImagemCommons(String paginaUrl) throws Exception {
        String titulo = extrairTituloDaPaginaWiki(paginaUrl);
        if (titulo.trim().isEmpty()) {
            return null;
        }

        String tituloCodificado = URLEncoder.encode(titulo, StandardCharsets.UTF_8).replace("+", "%20");
        String apiUrl =
                "https://commons.wikimedia.org/w/api.php?action=query&prop=imageinfo&iiprop=url"
                        + "&iiurlwidth=1280&format=json&titles=" + tituloCodificado;
        HttpURLConnection ligacao = (HttpURLConnection) new URL(apiUrl).openConnection();
        ligacao.setConnectTimeout(8000);
        ligacao.setReadTimeout(8000);
        ligacao.setUseCaches(false);
        ligacao.setInstanceFollowRedirects(true);
        ligacao.setRequestProperty("User-Agent", "Mozilla/5.0");
        ligacao.setRequestProperty("Accept", "application/json");
        try {
            if (ligacao.getResponseCode() < 200 || ligacao.getResponseCode() >= 300) {
                return null;
            }

            InputStream stream = ligacao.getInputStream();
            try {
                String json = new String(lerBytes(stream), StandardCharsets.UTF_8);
                String imagemUrl = extrairImagemCommonsDoJson(json);
                return imagemUrl == null ? null : descarregarImagem(imagemUrl);
            } finally {
                stream.close();
            }
        } finally {
            ligacao.disconnect();
        }
    }

    private String extrairTituloDaPaginaWiki(String paginaUrl) {
        int indice = paginaUrl.indexOf("/wiki/");
        if (indice < 0) {
            return "";
        }

        String titulo = paginaUrl.substring(indice + 6);
        int queryIndex = titulo.indexOf('?');
        if (queryIndex >= 0) {
            titulo = titulo.substring(0, queryIndex);
        }
        return URLDecoder.decode(titulo, StandardCharsets.UTF_8).trim();
    }

    private String extrairImagemCommonsDoJson(String json) {
        Matcher thumbMatcher = PADRAO_IMAGEM_COMMONS_THUMB.matcher(json);
        if (thumbMatcher.find()) {
            return normalizarUrlJson(thumbMatcher.group(1));
        }

        Matcher urlMatcher = PADRAO_IMAGEM_COMMONS_URL.matcher(json);
        if (urlMatcher.find()) {
            return normalizarUrlJson(urlMatcher.group(1));
        }
        return null;
    }

    private String normalizarUrlJson(String valor) {
        return valor == null ? null : valor.replace("\\/", "/");
    }

    private byte[] lerBytes(InputStream stream) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int lidos;
        while ((lidos = stream.read(buffer)) != -1) {
            output.write(buffer, 0, lidos);
        }
        return output.toByteArray();
    }

    private Image escalarImagem(BufferedImage original) {
        BufferedImage canvas = new BufferedImage(IMAGEM_LARGURA, IMAGEM_ALTURA, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = canvas.createGraphics();
        try {
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            graphics.setColor(new Color(238, 241, 246));
            graphics.fillRect(0, 0, IMAGEM_LARGURA, IMAGEM_ALTURA);

            double escala = Math.min(
                    (double) IMAGEM_LARGURA / (double) original.getWidth(),
                    (double) IMAGEM_ALTURA / (double) original.getHeight());
            int largura = (int) Math.round(original.getWidth() * escala);
            int altura = (int) Math.round(original.getHeight() * escala);
            int x = (IMAGEM_LARGURA - largura) / 2;
            int y = (IMAGEM_ALTURA - altura) / 2;
            graphics.drawImage(original, x, y, largura, altura, null);
            return canvas;
        } finally {
            graphics.dispose();
        }
    }

    private void aplicarImagemFallback(Carro carro, String mensagem) {
        if (carro != null && carro != carroSelecionado) {
            return;
        }

        BufferedImage placeholder = new BufferedImage(IMAGEM_LARGURA, IMAGEM_ALTURA, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = placeholder.createGraphics();
        try {
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setColor(new Color(242, 244, 248));
            graphics.fillRect(0, 0, IMAGEM_LARGURA, IMAGEM_ALTURA);

            graphics.setColor(new Color(28, 38, 60));
            graphics.fillRoundRect(40, 145, 350, 36, 20, 20);
            graphics.setColor(new Color(197, 131, 32));
            graphics.fillRoundRect(90, 105, 220, 52, 28, 28);
            graphics.fillRoundRect(145, 88, 90, 30, 18, 18);
            graphics.setColor(new Color(241, 243, 248));
            graphics.fillRoundRect(160, 93, 58, 18, 10, 10);
            graphics.setColor(new Color(28, 38, 60));
            graphics.fillOval(90, 155, 56, 56);
            graphics.fillOval(282, 155, 56, 56);

            graphics.setColor(new Color(92, 100, 118));
            graphics.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 18));
            graphics.drawString(carro == null ? "Sem imagem" : carro.getNomeCompleto(), 28, 32);
            graphics.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            graphics.drawString(mensagem, 28, 56);
        } finally {
            graphics.dispose();
        }

        imagemLabel.setText("");
        imagemLabel.setIcon(new ImageIcon(placeholder));
    }

    private void abrirPaginaImagem() {
        if (carroSelecionado == null || carroSelecionado.getImagemPaginaUrl() == null) {
            return;
        }

        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(carroSelecionado.getImagemPaginaUrl()));
            }
        } catch (Exception ignored) {
            fonteImagemLabel.setText("Fonte: " + carroSelecionado.getImagemPaginaUrl());
        }
    }
}
