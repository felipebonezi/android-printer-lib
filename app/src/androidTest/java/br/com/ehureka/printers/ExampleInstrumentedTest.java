package br.com.ehureka.printers;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.nio.charset.Charset;
import java.util.List;

import br.com.ehureka.printers.interfaces.IPrinter;
import br.com.ehureka.printers.interfaces.OnPrinterListener;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest implements OnPrinterListener {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

//        assertEquals("br.com.ehureka.printers", appContext.getPackageName());

        PrinterHelper helper = PrinterHelper.getInstance();
        helper.setOnPrinterListener(this);
        helper.discover(appContext);
    }

    @Override
    public void onError(int error) {
        Log.e("Print", "Code: " + error);
    }

    @Override
    public void onPrint(int result) {
        Log.d("Print", "Code: " + result);
    }

    @Override
    public void onPrinterDiscovered(List<BluetoothDevice> printers) {
        if (printers != null && printers.isEmpty()) {
            Context appContext = InstrumentationRegistry.getTargetContext();

            PrinterHelper helper = PrinterHelper.getInstance();
            helper.connect(appContext, PrinterEnum.CMP_10BT);

            IPrinter printer = helper.getPrinter();
            printer.print("Testando".getBytes(Charset.forName("utf-8")));
        }
    }

}
