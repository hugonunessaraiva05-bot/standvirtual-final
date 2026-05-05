package standvirtual.model;

public enum Categoria {
    SUV("SUV"),
    SEDAN("Sedan"),
    DESPORTIVO("Desportivo"),
    HIBRIDO("Hibrido"),
    ELETRICO("Eletrico");

    private final String label;

    Categoria(String label) {
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
