package standvirtual.service;

import java.text.NumberFormat;
import java.util.Locale;

public final class SimulacaoCredito {
    private static final NumberFormat EURO =
            NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-PT"));

    private final double entrada;
    private final double montanteFinanciado;
    private final double mensalidade;
    private final double totalPago;
    private final int meses;
    private final double taxaAnual;
    private final double percentagemEntrada;

    public SimulacaoCredito(
            double entrada,
            double montanteFinanciado,
            double mensalidade,
            double totalPago,
            int meses,
            double taxaAnual,
            double percentagemEntrada) {
        this.entrada = entrada;
        this.montanteFinanciado = montanteFinanciado;
        this.mensalidade = mensalidade;
        this.totalPago = totalPago;
        this.meses = meses;
        this.taxaAnual = taxaAnual;
        this.percentagemEntrada = percentagemEntrada;
    }

    public double getEntrada() {
        return entrada;
    }

    public double getMontanteFinanciado() {
        return montanteFinanciado;
    }

    public double getMensalidade() {
        return mensalidade;
    }

    public double getTotalPago() {
        return totalPago;
    }

    public int getMeses() {
        return meses;
    }

    public double getTaxaAnual() {
        return taxaAnual;
    }

    public double getPercentagemEntrada() {
        return percentagemEntrada;
    }

    public String getEntradaFormatada() {
        return EURO.format(entrada);
    }

    public String getMontanteFinanciadoFormatado() {
        return EURO.format(montanteFinanciado);
    }

    public String getMensalidadeFormatada() {
        return EURO.format(mensalidade);
    }

    public String getTotalPagoFormatado() {
        return EURO.format(totalPago);
    }

    public String getPrazoFormatado() {
        return meses + " meses";
    }

    public String getTaxaFormatada() {
        return String.format(Locale.US, "%.1f%% TAEG", taxaAnual * 100.0);
    }

    public String getPercentagemEntradaFormatada() {
        return String.format(Locale.US, "%.0f%%", percentagemEntrada * 100.0);
    }
}
