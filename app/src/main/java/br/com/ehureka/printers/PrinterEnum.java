package br.com.ehureka.printers;

public enum PrinterEnum {
    CMP_10BT("CITIZEN")
    , MTP_3("MTP-3")
    ;

    private String label;

    PrinterEnum(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
