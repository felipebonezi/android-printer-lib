package br.com.ehureka.printers;

public enum PrinterEnum {
    GENERIC("Genérica", "Genérica", PrinterRoll.MM_80)
    , CMP_10BT("CITIZEN", "EPSON", PrinterRoll.MM_60)
    , MTP_3("MTP-3", "CHINA", PrinterRoll.MM_80)
    , MHT80("MHT80", "MILESTONE", PrinterRoll.MM_80)
    , RP80A("RP80-A", "MILESTONE", PrinterRoll.MM_80)
    , RM80A("RM80-A", "MILESTONE", PrinterRoll.MM_80)
    , IPOS("IPOSPrinter", "CHINA", PrinterRoll.MM_60)
    ;

    private final String label;
    private final String manufacturer;
    private final PrinterRoll roll;

    PrinterEnum(String label, String manufacturer, PrinterRoll roll) {
        this.label = label;
        this.manufacturer = manufacturer;
        this.roll = roll;
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

    public PrinterRoll getRoll() {
        return roll;
    }
}

