package br.com.ehureka.printers;

public enum PrinterEnum {
    GENERIC("Genérica", "Genérica")
    , CMP_10BT("CITIZEN", "EPSON")
    , MTP_3("MTP-3", "CHINA")
    , MHT80("MHT80", "MILESTONE")
    , RP80A("RP80-A", "MILESTONE")
    , RM80A("RM80-A", "MILESTONE")
    , IPOS("IPOSPrinter", "CHINA")
    ;

    private String label;
    private String manufacturer;

    PrinterEnum(String label, String manufacturer) {
        this.label = label;
        this.manufacturer = manufacturer;
    }

    public String getLabel() {
        return label;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public static PrinterEnum getByModel(String name) {
        for (PrinterEnum printerEnum : values()) {
            if (printerEnum.label.toLowerCase().contains(name.toLowerCase()))
                return printerEnum;
        }
        return PrinterEnum.GENERIC;
    }

}
