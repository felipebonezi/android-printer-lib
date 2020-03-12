package br.com.ehureka.printers;

import br.com.ehureka.printers.interfaces.OnPrinterListener;

public class GenericPrinter extends RP80APrinter {

    GenericPrinter(PrinterHelper helper, OnPrinterListener listener) {
        super(helper, listener);
    }

}
