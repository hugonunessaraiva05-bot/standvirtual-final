package standvirtual.model;

public enum Combustivel {
    GASOLINA("Gasolina"),
    DIESEL("Diesel"),
    HIBRIDO("Hibrido"),
    ELETRICO("Eletrico");

    private final String label;

    Combustivel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
