package standvirtual.service;

import standvirtual.model.Carro;

import java.text.NumberFormat;
import java.util.Locale;

public final class ResumoCatalogo {
    private static final NumberFormat EURO =
            NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-PT"));

    private final int totalResultados;
    private final double precoMedio;
    private final double precoMinimo;
    private final double precoMaximo;
    private final Carro carroMaisAcessivel;
    private final Carro carroMaisRecente;

    public ResumoCatalogo(
            int totalResultados,
            double precoMedio,
            double precoMinimo,
            double precoMaximo,
            Carro carroMaisAcessivel,
            Carro carroMaisRecente) {
        this.totalResultados = totalResultados;
        this.precoMedio = precoMedio;
        this.precoMinimo = precoMinimo;
        this.precoMaximo = precoMaximo;
        this.carroMaisAcessivel = carroMaisAcessivel;
        this.carroMaisRecente = carroMaisRecente;
    }

    public static ResumoCatalogo vazio() {
        return new ResumoCatalogo(0, 0.0, 0.0, 0.0, null, null);
    }

    public int getTotalResultados() {
        return totalResultados;
    }

    public Carro getCarroMaisRecente() {
        return carroMaisRecente;
    }

    public Carro getCarroMaisAcessivel() {
        return carroMaisAcessivel;
    }

    public String getPrecoMedioFormatado() {
        return totalResultados == 0 ? "--" : EURO.format(precoMedio);
    }

    public String getFaixaPrecoFormatada() {
        if (totalResultados == 0) {
            return "--";
        }
        return EURO.format(precoMinimo) + " - " + EURO.format(precoMaximo);
    }

    public String getPrecoEntradaFormatado() {
        return totalResultados == 0 ? "--" : EURO.format(precoMinimo);
    }

    public String getDescricaoMaisRecente() {
        if (carroMaisRecente == null) {
            return "--";
        }
        return carroMaisRecente.getNomeCompleto() + " (" + carroMaisRecente.getAno() + ")";
    }
}
