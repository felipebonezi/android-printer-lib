package br.com.ehureka.printers.flow;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import br.com.ehureka.printers.PrinterEnum;
import br.com.ehureka.printers.PrinterHelper;
import br.com.ehureka.printers.interfaces.IPrinter;
import br.com.ehureka.printers.interfaces.OnPrinterListener;

public class AppActivity extends Activity implements OnPrinterListener {

    private PrinterHelper mHelper;

    @Override
    protected void onResume() {
        super.onResume();
        this.mHelper = PrinterHelper.getInstance();
        if (!this.mHelper.isCompatible()) {
            Toast.makeText(this, "Your device is not compatible with Bluetooth.", Toast.LENGTH_SHORT).show();
        } else if (!this.mHelper.isEnabled()) {
            Toast.makeText(this, "Your Bluetooth isn't enable.", Toast.LENGTH_SHORT).show();
            this.mHelper.enableBluetooth(this);
        }
//        else {
//            BluetoothDevice pairedDevice = this.mHelper.getDevice();
//            if (pairedDevice == null) {
//                Toast.makeText(this, "Your printer isn't paired.", Toast.LENGTH_SHORT).show();
//                 TODO Pair device over a custom dialog.
//            } else {
                printSample();
//            }
//        }
    }

    private void printSample() {
        this.mHelper.setOnPrinterListener(this);
        this.mHelper.connect(PrinterEnum.MTP_3);

        IPrinter printer = this.mHelper.getPrinter();
        printer.reset();

        int width = printer.getWidth();

        printSeparator(printer, width);
        printlnDoubleEmphasized(printer, this.mHelper.center("Olá, meu caro!", width));
        printer.println(this.mHelper.center("Tudo bem?", width));
        printSeparator(printer, width);

        printlnDoubleEmphasized(printer, this.mHelper.center("Data - 18:00h de 08/02/2017", width));
        printSeparator(printer, width);

        printer.println(this.mHelper.left("Exemplo#1: 004.351.1", width));
        printer.println(this.mHelper.left("Exemplo#2: PDVL 004", width));
        printSeparator(printer, width);

        printlnDoubleEmphasized(printer, this.mHelper.center("IMPRESSÃO N.00043", width));
        printSeparator(printer, width);

        printer.println(this.mHelper.center("IMPRESSO ÀS 10:38h DE 08/02/2017", width));

        printSeparator(printer, width);
        printer.println(this.mHelper.center("AJUDE A COMUNIDADE!", width));

        printSeparator(printer, width);
        String barcode = "000430043511218001702080";
        printer.println(this.mHelper.center(barcode, width));
        printer.barcode(barcode.toCharArray(), 3, 80);

        printer.println();
        printer.println();
        printer.println();
        printer.flush();

        this.mHelper.disconnect();
    }

    private void printSeparator(IPrinter printer, int width) {
        printer.println(this.mHelper.fillBuffer((byte) '-', width));
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
        this.mHelper.disconnect();
    }

    @Override
    public void onConnected() {
        Log.i("Print", "Conected");
    }

    @Override
    public void onError(String error) {
        Log.e("Print", String.format("Code: %s", error));
        Log.e("Print", String.format("Message: %s", error));
    }

    @Override
    public void onPrint() {
        Log.d("Print", "Code: Success");
    }

    @Override
    public void onPrintFinished() {
        Log.d("Print", "Finished!");
    }

}
