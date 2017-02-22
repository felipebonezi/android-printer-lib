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
        printlnDoubleEmphasized(printer, helper.center("OCS", width));
        printer.println(helper.center("LOTERIAS ONLINE", width));
        printSeparator(printer, width);

        printlnDoubleEmphasized(printer, helper.center("FEDERAL - 18:00h de 08/02/2017", width));
        printSeparator(printer, width);

        printer.println(helper.left("PDV.....: 004.351.1", width));
        printer.println(helper.left("Cambista: PDVL 004", width));
        printSeparator(printer, width);

        printlnDoubleEmphasized(printer, helper.center("BILHETE N.00043", width));
        printSeparator(printer, width);

        printer.println(helper.center("IMPRESSO ÀS 10:38h DE 08/02/2017", width));

        printSeparator(printer, width);
        printer.println(helper.left("Centena Invertida", width));
        printer.println(helper.left("3.1.5", width));
        printer.println(helper.left("3.1.4", width));
        printer.println(helper.left("1.5.1", width));
        printer.println(helper.left("9.6.4", width));
        printer.println(helper.left("à R$0,50 no 1. prêmio", width));
        printer.println(helper.right("Sub-total: R$12,00", width));

        printSeparator(printer, width);
        printer.println(helper.left("Milhar", width));
        printer.println(helper.left("3151", width));
        printer.println(helper.left("9614", width));
        printer.println(helper.left("à R$0,25 no 1., 2. prêmio", width));
        printer.println(helper.right("Sub-total: R$20,00", width));

        printSeparator(printer, width);
        printer.println(helper.left("Centena", width));
        printer.println(helper.left("110", width));
        printer.println(helper.left("314", width));
        printer.println(helper.left("151", width));
        printer.println(helper.left("964", width));
        printer.println(helper.left("à R$0,01 no 1. prêmio", width));
        printer.println(helper.right("Sub-total: R$1,00", width));

        printSeparator(printer, width);
        printDoubleEmphasized(printer, helper.right("Total: R$33,00", width));

        printSeparator(printer, width);
        printer.println(helper.center("B O A  S O R T E !", width));
        printer.println();
        printer.println(helper.center("VÁLIDO POR 30 DIAS", width));

        printSeparator(printer, width);
        printer.println("Milhares 1308, 1313, 1720, 1820, 1873, 1920, 1965, 2020 estão cotadas em 20%.");
        printer.println();
        printer.println("Milhares 1973, 2933 estão cotadas em 50%.");

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
