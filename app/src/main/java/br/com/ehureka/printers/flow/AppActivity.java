package br.com.ehureka.printers.flow;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import br.com.ehureka.printers.PrinterEnum;
import br.com.ehureka.printers.PrinterHelper;
import br.com.ehureka.printers.interfaces.IPrinter;
import br.com.ehureka.printers.interfaces.OnPrinterListener;

public class AppActivity extends Activity implements OnPrinterListener {

    private PrinterHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        helper = PrinterHelper.getInstance();
        helper.setOnPrinterListener(this);
        helper.connect(PrinterEnum.CMP_10BT);

        IPrinter printer = helper.getPrinter();
        printer.reset();

        int width = printer.getWidth();

        printSeparator(printer, width);
        printlnDoubleEmphasized(printer, helper.center("Olá, meu caro!", width));
        printer.println(helper.center("Tudo bem?", width));
        printSeparator(printer, width);

        printlnDoubleEmphasized(printer, helper.center("Data - 18:00h de 08/02/2017", width));
        printSeparator(printer, width);

        printer.println(helper.left("Exemplo#1: 004.351.1", width));
        printer.println(helper.left("Exemplo#2: PDVL 004", width));
        printSeparator(printer, width);

        printlnDoubleEmphasized(printer, helper.center("IMPRESSÃO N.00043", width));
        printSeparator(printer, width);

        printer.println(helper.center("IMPRESSO ÀS 10:38h DE 08/02/2017", width));

        printSeparator(printer, width);
        printer.println(helper.center("AJUDE A COMUNIDADE!", width));

        printSeparator(printer, width);
        String barcode = "000430043511218001702080";
        printer.println(helper.center(barcode, width));
        printer.barcode(barcode.toCharArray(), 2, 80);

        printer.println();
        printer.println();
        printer.println();
        printer.flush();

        helper.disconnect();
    }

    private void printSeparator(IPrinter printer, int width) {
        printer.println(helper.fillBuffer((byte) '-', width));
    }

    private void printDoubleEmphasized(IPrinter printer, String text) {
        printer.doubleHeight(true);
        printer.emphasized(true);
        printer.print(text);
        printer.emphasized(false);
        printer.doubleHeight(false);
    }

    private void printlnDoubleEmphasized(IPrinter printer, String text) {
        printDoubleEmphasized(printer, text);
        printer.println();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        helper.disconnect();
    }

    @Override
    public void onError(int error) {
        Log.e("Print", "Code: " + error);
    }

    @Override
    public void onPrint(int result) {
        Log.d("Print", "Code: " + result);
    }

}
