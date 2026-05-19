package standvirtual.model;

import java.text.NumberFormat;
import java.util.Locale;

public final class Carro {
    private static final NumberFormat EURO =
            NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-PT"));
    private static final NumberFormat INTEIROS =
            NumberFormat.getIntegerInstance(Locale.forLanguageTag("pt-PT"));

    private final String marca;
    private final String modelo;
    private final Categoria categoria;
    private final Combustivel combustivel;
    private final int ano;
    private final int cavalos;
    private final int quilometros;
    private final double preco;
    private final String transmissao;
    private final String destaque;
    private final String descricao;
    private final String imagemUrl;
    private final String imagemFonte;
    private final String imagemPaginaUrl;

    public Carro(
            String marca,
            String modelo,
            Categoria categoria,
            Combustivel combustivel,
            int ano,
            int cavalos,
            int quilometros,
            double preco,
            String transmissao,
            String destaque,
            String descricao,
            String imagemUrl,
            String imagemFonte,
            String imagemPaginaUrl) {
        this.marca = marca;
        this.modelo = modelo;
        this.categoria = categoria;
        this.combustivel = combustivel;
        this.ano = ano;
        this.cavalos = cavalos;
        this.quilometros = quilometros;
        this.preco = preco;
        this.transmissao = transmissao;
        this.destaque = destaque;
        this.descricao = descricao;
        this.imagemUrl = imagemUrl;
        this.imagemFonte = imagemFonte;
        this.imagemPaginaUrl = imagemPaginaUrl;
    }

    public String getMarca() {
        return marca;
    }

    public String getModelo() {
        return modelo;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public Combustivel getCombustivel() {
        return combustivel;
    }

    public int getAno() {
        return ano;
    }

    public int getCavalos() {
        return cavalos;
    }

    public int getQuilometros() {
        return quilometros;
    }

    public double getPreco() {
        return preco;
    }

    public String getTransmissao() {
        return transmissao;
    }

    public String getDestaque() {
        return destaque;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getImagemUrl() {
        return imagemUrl;
    }

    public String getImagemFonte() {
        return imagemFonte;
    }

    public String getImagemPaginaUrl() {
        return imagemPaginaUrl;
    }

    public String getNomeCompleto() {
        return marca + " " + modelo;
    }

    public String getPrecoFormatado() {
        return EURO.format(preco);
    }

    public String getResumo() {
        return ano + "  |  " + INTEIROS.format(quilometros) + " km  |  " + combustivel.getLabel();
    }
}
