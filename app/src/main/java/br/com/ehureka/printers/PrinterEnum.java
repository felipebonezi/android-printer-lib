package br.com.ehureka.printers;

public enum PrinterEnum {
    CMP_10BT("CITIZEN", "EPSON")
    , MTP_3("MTP-3", "CHINA")
    , MHT80("MHT80", "MILESTONE")
    , RP80A("RP80-A", "MILESTONE")
    , RM80A("RM80-A", "MILESTONE")
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
            if (printerEnum.label.contains(name))
                return printerEnum;
        }
        return null;
    }

}
