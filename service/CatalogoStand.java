package standvirtual.service;

import standvirtual.model.Carro;
import standvirtual.model.Categoria;
import standvirtual.model.Combustivel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public final class CatalogoStand {
    public enum Ordenacao {
        PRECO_ASC("Preco crescente", Comparator.comparingDouble(Carro::getPreco)),
        PRECO_DESC("Preco decrescente", Comparator.comparingDouble(Carro::getPreco).reversed()),
        ANO_DESC("Mais recentes", Comparator.comparingInt(Carro::getAno).reversed()),
        POTENCIA_DESC("Mais potentes", Comparator.comparingInt(Carro::getCavalos).reversed());

        private final String label;
        private final Comparator<Carro> comparator;

        Ordenacao(String label, Comparator<Carro> comparator) {
            this.label = label;
            this.comparator = comparator;
        }

        public Comparator<Carro> getComparator() {
            return comparator;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    private final List<Carro> stock;

    public CatalogoStand() {
        this.stock = criarStockInicial();
    }

    public List<Carro> pesquisar(String termo, Categoria categoria, Combustivel combustivel, Ordenacao ordenacao) {
        String filtro = termo == null ? "" : termo.trim().toLowerCase(Locale.ROOT);

        return stock.stream()
                .filter(carro -> filtro.isEmpty()
                        || carro.getNomeCompleto().toLowerCase(Locale.ROOT).contains(filtro)
                        || carro.getDestaque().toLowerCase(Locale.ROOT).contains(filtro))
                .filter(carro -> categoria == null || carro.getCategoria() == categoria)
                .filter(carro -> combustivel == null || carro.getCombustivel() == combustivel)
                .sorted(ordenacao.getComparator())
                .collect(Collectors.toList());
    }

    public List<Carro> getDestaques() {
        return stock.stream().limit(3).collect(Collectors.toList());
    }

    private List<Carro> criarStockInicial() {
        List<Carro> carros = new ArrayList<Carro>();
        carros.add(new Carro(
                "Porsche", "Taycan 4S", Categoria.ELETRICO, Combustivel.ELETRICO,
                2025, 530, 8000, 104900, "Automatica", "Entrega imediata",
                "Gran turismo eletrico com aceleracao brutal, autonomia equilibrada e acabamento premium.",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/d/dc/2020_Porsche_Taycan_4S_79kWh_Front.jpg/330px-2020_Porsche_Taycan_4S_79kWh_Front.jpg",
                "Imagem online via Wikipedia / Wikimedia Commons",
                "https://en.wikipedia.org/wiki/Porsche_Taycan"));
        carros.add(new Carro(
                "BMW", "X5 xDrive50e", Categoria.HIBRIDO, Combustivel.HIBRIDO,
                2024, 489, 12000, 89900, "Automatica", "SUV executivo",
                "SUV familiar com postura premium, conforto em viagem e modo eletrico para uso diario.",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b3/2019_BMW_X5_xDrive30d_M_Sport_Automatic_3.0_Front.jpg/250px-2019_BMW_X5_xDrive30d_M_Sport_Automatic_3.0_Front.jpg",
                "Imagem online via Wikipedia / Wikimedia Commons",
                "https://en.wikipedia.org/wiki/BMW_X5"));
        carros.add(new Carro(
                "Land Rover", "Range Rover Vogue", Categoria.SUV, Combustivel.DIESEL,
                2025, 300, 22000, 119900, "Automatica", "Luxo e presenca",
                "SUV de luxo com conforto soberbo, acabamento requintado e forte imagem de exclusividade.",
                "https://upload.wikimedia.org/wikipedia/commons/7/7b/Land_Rover_RANGE_ROVER_VOGUE_%28ABA-LG3SB%29_front.jpg",
                "Imagem online via Wikimedia Commons",
                "https://en.wikipedia.org/wiki/Range_Rover"));
        carros.add(new Carro(
                "Land Rover", "Range Rover Sport", Categoria.SUV, Combustivel.HIBRIDO,
                2025, 460, 15000, 109500, "Automatica", "SUV premium dinamico",
                "Versao mais desportiva do Range Rover, com performance forte, conforto premium e imagem moderna.",
                "https://upload.wikimedia.org/wikipedia/commons/8/86/2017_Land_Rover_Range_Rover_%28L494_MY16.5%29_HSE_SDV6_wagon_%282018-08-31%29_01.jpg",
                "Imagem online via Wikimedia Commons",
                "https://en.wikipedia.org/wiki/Range_Rover_Sport"));
        carros.add(new Carro(
                "Mercedes-Benz", "C 220 d AMG Line", Categoria.SEDAN, Combustivel.DIESEL,
                2023, 200, 28000, 47900, "Automatica", "Historico completo",
                "Sedan elegante com excelente insonorizacao, consumos baixos e interior digital.",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/2/24/Mercedes-Benz_W206_IMG_5796.jpg/330px-Mercedes-Benz_W206_IMG_5796.jpg",
                "Imagem online via Wikipedia / Wikimedia Commons",
                "https://en.wikipedia.org/wiki/Mercedes-Benz_C-Class"));
        carros.add(new Carro(
                "Mercedes-Benz", "CLA 180 Break", Categoria.SEDAN, Combustivel.GASOLINA,
                2024, 136, 14000, 42900, "Automatica", "Carrinha compacta premium",
                "Break elegante com perfil desportivo, boa bagageira e interior moderno para uso diario.",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b5/Mercedes-Benz_CLA_180_Shooting_Brake_%28X118%29_rear.jpg/330px-Mercedes-Benz_CLA_180_Shooting_Brake_%28X118%29_rear.jpg",
                "Imagem online via Wikimedia Commons",
                "https://commons.wikimedia.org/wiki/File:Mercedes-Benz_CLA_180_Shooting_Brake_(X118)_rear.jpg"));
        carros.add(new Carro(
                "Cupra", "Formentor VZ", Categoria.SUV, Combustivel.GASOLINA,
                2024, 310, 17000, 52900, "Automatica", "Conducao desportiva",
                "Crossover com chassis afinado, resposta rapida e imagem distinta.",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/9/97/Cupra_Formentor_VZ5_1X7A7000.jpg/250px-Cupra_Formentor_VZ5_1X7A7000.jpg",
                "Imagem online via Wikipedia / Wikimedia Commons",
                "https://en.wikipedia.org/wiki/Cupra_Formentor"));
        carros.add(new Carro(
                "Tesla", "Model 3 Long Range", Categoria.ELETRICO, Combustivel.ELETRICO,
                2025, 498, 6000, 46990, "Automatica", "Autonomia alargada",
                "Berlina eletrica minimalista com excelente eficiencia e forte integracao de software.",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/9/91/2019_Tesla_Model_3_Performance_AWD_Front.jpg/330px-2019_Tesla_Model_3_Performance_AWD_Front.jpg",
                "Imagem online via Wikipedia / Wikimedia Commons",
                "https://en.wikipedia.org/wiki/Tesla_Model_3"));
        carros.add(new Carro(
                "Lexus", "NX 450h+", Categoria.HIBRIDO, Combustivel.HIBRIDO,
                2025, 309, 9000, 68900, "Automatica", "SUV hibrido premium",
                "SUV sofisticado com bom conforto, eficiencia plug-in e interior muito bem construido.",
                "https://upload.wikimedia.org/wikipedia/commons/c/ca/2024_Lexus_NX.jpg",
                "Imagem online via Wikimedia Commons",
                "https://en.wikipedia.org/wiki/Lexus_NX"));
        carros.add(new Carro(
                "Volvo", "XC60 B5", Categoria.SUV, Combustivel.HIBRIDO,
                2025, 250, 11000, 62900, "Automatica", "Conforto escandinavo",
                "SUV equilibrado com design discreto, excelente seguranca e ambiente premium.",
                "https://upload.wikimedia.org/wikipedia/commons/0/02/Volvo_XC60_%28SPA%29_FL_IMG_2103.jpg",
                "Imagem online via Wikimedia Commons",
                "https://en.wikipedia.org/wiki/Volvo_XC60"));
        carros.add(new Carro(
                "Kia", "EV6 GT-Line", Categoria.ELETRICO, Combustivel.ELETRICO,
                2025, 325, 7000, 57900, "Automatica", "Crossover eletrico",
                "Eletrico com visual futurista, boa autonomia e carregamento rapido para uso diario.",
                "https://upload.wikimedia.org/wikipedia/commons/6/6c/Kia_EV6.jpg",
                "Imagem online via Wikimedia Commons",
                "https://en.wikipedia.org/wiki/Kia_EV6"));
        carros.add(new Carro(
                "Alfa Romeo", "Giulia Veloce", Categoria.SEDAN, Combustivel.GASOLINA,
                2024, 280, 18000, 55900, "Automatica", "Sedan italiano",
                "Berlina desportiva com estilo marcante, boa dinamica e caracter premium.",
                "https://upload.wikimedia.org/wikipedia/commons/1/11/Alfa_Romeo_Giulia_%282023%29_IMG_8308.jpg",
                "Imagem online via Wikimedia Commons",
                "https://en.wikipedia.org/wiki/Alfa_Romeo_Giulia_(952)"));
        carros.add(new Carro(
                "Toyota", "GR Supra", Categoria.DESPORTIVO, Combustivel.GASOLINA,
                2024, 387, 8000, 74900, "Automatica", "Coupe puro prazer",
                "Desportivo de tracao traseira com linhas agressivas e foco claro na conducao.",
                "https://upload.wikimedia.org/wikipedia/commons/e/e4/2024_Toyota_Supra.jpg",
                "Imagem online via Wikimedia Commons",
                "https://en.wikipedia.org/wiki/Toyota_Supra"));
        carros.add(new Carro(
                "Audi", "RS 5 Sportback", Categoria.DESPORTIVO, Combustivel.GASOLINA,
                2022, 450, 24000, 78900, "Automatica", "Performance premium",
                "Desportivo utilizavel todos os dias, com tracao integral e comportamento solido.",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/9/9f/Audi_RS5_Sportback_5F_FL_IMG_8131.jpg/250px-Audi_RS5_Sportback_5F_FL_IMG_8131.jpg",
                "Imagem online via Wikipedia / Wikimedia Commons",
                "https://en.wikipedia.org/wiki/Audi_A5"));
        carros.add(new Carro(
                "BMW", "330e Touring", Categoria.HIBRIDO, Combustivel.HIBRIDO,
                2024, 292, 14000, 49900, "Automatica", "Carrinha premium eficiente",
                "Carrinha hibrida plug-in com boa dinamica, espaco familiar e imagem executiva.",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/3/35/BMW_G21_IMG_4355.jpg/330px-BMW_G21_IMG_4355.jpg",
                "Imagem online via Wikipedia / Wikimedia Commons",
                "https://en.wikipedia.org/wiki/BMW_3_Series_(G20)"));
        carros.add(new Carro(
                "Mercedes-Benz", "GLC 300e", Categoria.SUV, Combustivel.HIBRIDO,
                2024, 313, 16000, 61900, "Automatica", "SUV familiar premium",
                "SUV equilibrado com conforto elevado, motorizacao plug-in e interior refinado.",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/8/8c/Mercedes-Benz_X254_1X7A1944.jpg/330px-Mercedes-Benz_X254_1X7A1944.jpg",
                "Imagem online via Wikipedia / Wikimedia Commons",
                "https://en.wikipedia.org/wiki/Mercedes-Benz_GLC-Class"));
        carros.add(new Carro(
                "Audi", "Q8 e-tron", Categoria.ELETRICO, Combustivel.ELETRICO,
                2025, 408, 5000, 83900, "Automatica", "SUV eletrico de luxo",
                "SUV eletrico com isolamento exemplar, imagem premium e conforto de topo.",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/4/47/Audi_Q8_55_e-tron_quattro_S-line_%28GE%29_%E2%80%93_f_16042023.jpg/330px-Audi_Q8_55_e-tron_quattro_S-line_%28GE%29_%E2%80%93_f_16042023.jpg",
                "Imagem online via Wikipedia / Wikimedia Commons",
                "https://en.wikipedia.org/wiki/Audi_e-tron_(2018)"));
        carros.add(new Carro(
                "Ford", "Mustang GT", Categoria.DESPORTIVO, Combustivel.GASOLINA,
                2023, 450, 19000, 69900, "Automatica", "Muscle car iconico",
                "Coupe emocional com motor V8, postura classica e experiencia de conducao envolvente.",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/1/1e/2018_Ford_Mustang_GT_5.0_Front.jpg/330px-2018_Ford_Mustang_GT_5.0_Front.jpg",
                "Imagem online via Wikipedia / Wikimedia Commons",
                "https://en.wikipedia.org/wiki/Ford_Mustang_(sixth_generation)"));
        carros.add(new Carro(
                "Peugeot", "508 SW Hybrid", Categoria.HIBRIDO, Combustivel.HIBRIDO,
                2024, 225, 13000, 42900, "Automatica", "Carrinha distinta",
                "Carrinha elegante com design marcante, conforto em viagem e boa eficiencia.",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/3/37/Peugeot_508_II_SW_HYBRID_GT_Pack_2021.jpg/330px-Peugeot_508_II_SW_HYBRID_GT_Pack_2021.jpg",
                "Imagem online via Wikipedia / Wikimedia Commons",
                "https://en.wikipedia.org/wiki/Peugeot_508"));
        carros.add(new Carro(
                "Skoda", "Octavia RS", Categoria.SEDAN, Combustivel.GASOLINA,
                2024, 245, 15000, 38900, "Automatica", "Espaco com desportivismo",
                "Berlina pratica com muito espaco, boa performance e custo competitivo.",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e3/2020_Skoda_Octavia_vRS_TSi_S-A_2.0_Front.jpg/330px-2020_Skoda_Octavia_vRS_TSi_S-A_2.0_Front.jpg",
                "Imagem online via Wikipedia / Wikimedia Commons",
                "https://en.wikipedia.org/wiki/%C5%A0koda_Octavia"));
        carros.add(new Carro(
                "Hyundai", "IONIQ 5", Categoria.ELETRICO, Combustivel.ELETRICO,
                2025, 325, 4000, 51900, "Automatica", "Eletrico futurista",
                "Crossover eletrico com habitaculo amplo, design retro-futurista e carregamento rapido.",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/7/77/Hyundai_Ioniq_5_IMG_4339.jpg/330px-Hyundai_Ioniq_5_IMG_4339.jpg",
                "Imagem online via Wikipedia / Wikimedia Commons",
                "https://en.wikipedia.org/wiki/Hyundai_Ioniq_5"));
        carros.add(new Carro(
                "Mazda", "CX-60 e-Skyactiv PHEV", Categoria.SUV, Combustivel.HIBRIDO,
                2025, 327, 6000, 56900, "Automatica", "SUV japones premium",
                "SUV sofisticado com boa qualidade interior, motorizacao plug-in e conforto refinado.",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/1/17/Mazda_CX-60_PHEV_IMG_8118.jpg/330px-Mazda_CX-60_PHEV_IMG_8118.jpg",
                "Imagem online via Wikipedia / Wikimedia Commons",
                "https://en.wikipedia.org/wiki/Mazda_CX-60"));
        carros.add(new Carro(
                "Volkswagen", "Golf R", Categoria.DESPORTIVO, Combustivel.GASOLINA,
                2024, 320, 9000, 54900, "Automatica", "Hot hatch integral",
                "Compacto muito rapido com tracao integral, utilizacao diaria e acabamento solido.",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/7/74/VW_Golf_8_R_1X7A5604.jpg/330px-VW_Golf_8_R_1X7A5604.jpg",
                "Imagem online via Wikipedia / Wikimedia Commons",
                "https://en.wikipedia.org/wiki/Volkswagen_Golf_Mk8"));
        carros.add(new Carro(
                "Renault", "Austral E-Tech", Categoria.SUV, Combustivel.HIBRIDO,
                2025, 200, 7000, 37900, "Automatica", "SUV tecnologico",
                "SUV moderno com boa tecnologia a bordo, conforto e eficiencia para familia.",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e1/Renault_Austral_E-Tech_Full_Hybrid_2023.jpg/330px-Renault_Austral_E-Tech_Full_Hybrid_2023.jpg",
                "Imagem online via Wikipedia / Wikimedia Commons",
                "https://en.wikipedia.org/wiki/Renault_Austral"));
        carros.add(new Carro(
                "Nissan", "Qashqai e-Power", Categoria.SUV, Combustivel.HIBRIDO,
                2024, 190, 12500, 35900, "Automatica", "SUV urbano eficiente",
                "SUV popular com motorizacao eletrificada, conforto consistente e boa versatilidade.",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/6/6f/2022_Nissan_Qashqai_Tekna%2B_DiG-T_MHEV_1.3_Front.jpg/330px-2022_Nissan_Qashqai_Tekna%2B_DiG-T_MHEV_1.3_Front.jpg",
                "Imagem online via Wikipedia / Wikimedia Commons",
                "https://en.wikipedia.org/wiki/Nissan_Qashqai"));
        carros.add(new Carro(
                "Porsche", "911 Carrera", Categoria.DESPORTIVO, Combustivel.GASOLINA,
                2025, 394, 3000, 144900, "Automatica", "Icone absoluto",
                "Coupe lendario com performance muito elevada, qualidade premium e usabilidade real.",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/6/61/2021_Porsche_911_Carrera_S_Automatic_3.0.jpg/330px-2021_Porsche_911_Carrera_S_Automatic_3.0.jpg",
                "Imagem online via Wikipedia / Wikimedia Commons",
                "https://commons.wikimedia.org/wiki/File:2021_Porsche_911_Carrera_S_Automatic_3.0.jpg"));
        carros.add(new Carro(
                "Porsche", "Boxster 986", Categoria.DESPORTIVO, Combustivel.GASOLINA,
                2003, 228, 98000, 23900, "Manual", "Roadster iconico",
                "Roadster leve e envolvente com motor central, capota em lona e conducao muito pura.",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/6/63/Porsche_986_Boxster_%281999%29_-_9939163836.jpg/330px-Porsche_986_Boxster_%281999%29_-_9939163836.jpg",
                "Imagem online via Wikimedia Commons",
                "https://commons.wikimedia.org/wiki/File:Porsche_986_Boxster_(1999)_-_9939163836.jpg"));
        carros.add(new Carro(
                "Porsche", "911 GT3 RS", Categoria.DESPORTIVO, Combustivel.GASOLINA,
                2025, 525, 1200, 289900, "Automatica", "Pista homologada para estrada",
                "Versao extrema do 911 com aerodinamica agressiva, motor atmosferico e foco absoluto em performance.",
                "https://upload.wikimedia.org/wikipedia/commons/3/3e/Porsche_911_GT3_RS_%2813867%29.jpg",
                "Imagem online via Wikimedia Commons",
                "https://en.wikipedia.org/wiki/Porsche_911_GT3"));
        carros.add(new Carro(
                "Volvo", "V60 Recharge", Categoria.HIBRIDO, Combustivel.HIBRIDO,
                2024, 455, 10000, 58900, "Automatica", "Carrinha segura e rapida",
                "Carrinha premium com foco em seguranca, conforto e motorizacao plug-in potente.",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/7/7d/Volvo_V60_T8_R-Design.jpg/330px-Volvo_V60_T8_R-Design.jpg",
                "Imagem online via Wikipedia / Wikimedia Commons",
                "https://en.wikipedia.org/wiki/Volvo_V60"));
        carros.add(new Carro(
                "BYD", "Seal", Categoria.ELETRICO, Combustivel.ELETRICO,
                2025, 313, 3500, 45900, "Automatica", "Berlina eletrica moderna",
                "Eletrico competitivo com autonomia interessante, equipamento rico e design limpo.",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/6/68/BYD_Seal_001.jpg/330px-BYD_Seal_001.jpg",
                "Imagem online via Wikipedia / Wikimedia Commons",
                "https://en.wikipedia.org/wiki/BYD_Seal"));
        carros.add(new Carro(
                "Honda", "Civic e:HEV", Categoria.SEDAN, Combustivel.HIBRIDO,
                2024, 184, 15000, 34900, "Automatica", "Fiabilidade e eficiencia",
                "Berlina hibrida com consumos baixos, boa ergonomia e reputacao de fiabilidade.",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/7/71/Honda_Civic_Hybrid_%282022%2C_Europe%29_1X7A6151_%28cropped%29.jpg/330px-Honda_Civic_Hybrid_%282022%2C_Europe%29_1X7A6151_%28cropped%29.jpg",
                "Imagem online via Wikipedia / Wikimedia Commons",
                "https://commons.wikimedia.org/wiki/File:Honda_Civic_Hybrid_(2022,_Europe)_1X7A6151_(cropped).jpg"));
        return carros;
    }
}
