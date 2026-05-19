package standvirtual.service;

public final class SimuladorCredito {
    private final double taxaCreditoAnual;

    public SimuladorCredito(double taxaCreditoAnual) {
        this.taxaCreditoAnual = taxaCreditoAnual;
    }

    public SimulacaoCredito simular(double preco, int meses, double percentagemEntrada) {
        double percentagemNormalizada = Math.max(0.0, Math.min(percentagemEntrada, 0.90));
        double entrada = preco * percentagemNormalizada;
        double montanteFinanciado = Math.max(0.0, preco - entrada);
        double taxaMensal = taxaCreditoAnual / 12.0;
        double mensalidade = calcularMensalidade(montanteFinanciado, taxaMensal, meses);
        double totalPago = entrada + (mensalidade * meses);

        return new SimulacaoCredito(
                entrada,
                montanteFinanciado,
                mensalidade,
                totalPago,
                meses,
                taxaCreditoAnual,
                percentagemNormalizada);
    }

    private double calcularMensalidade(double valorFinanciado, double taxaMensal, int meses) {
        if (meses <= 0) {
            return 0.0;
        }
        if (taxaMensal <= 0.0) {
            return valorFinanciado / meses;
        }

        double fator = Math.pow(1.0 + taxaMensal, meses);
        return valorFinanciado * (taxaMensal * fator) / (fator - 1.0);
    }
}
