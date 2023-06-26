package br.com.ehureka.printers;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.Vector;

import br.com.ehureka.printers.interfaces.IPrinter;
import br.com.ehureka.printers.interfaces.OnPrinterListener;

public class PrinterHelper {

    private static final int REQUEST_ENABLE_BT = 999;
    private static final String TAG = "PrinterHelper";
    public static final String PRINTER_UUID = "00001101-0000-1000-8000-00805F9B34FB";
    private static final UUID MY_UUID = UUID.fromString(PRINTER_UUID);

    private static PrinterHelper INSTANCE;

    private IPrinter mPrinter;
    private BluetoothAdapter mBluetoothAdapter;
    private OnPrinterListener mListener;

    private BluetoothDevice mDevice;
    private BluetoothSocket mBTSocket;

    private Timer mTimer;
    private List<BluetoothDevice> mDevices;

    public static PrinterHelper getInstance() {
        synchronized (TAG) {
            if (INSTANCE == null) {
                INSTANCE = new PrinterHelper();
            }
        }
        return INSTANCE;
    }

    private PrinterHelper() {
        refreshBoundedDevices();
    }

    @SuppressLint("MissingPermission")
    public void refreshBoundedDevices() {
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (this.mBluetoothAdapter != null) {
            this.mDevices = new ArrayList<>(this.mBluetoothAdapter.getBondedDevices());
        } else {
            this.mDevices = new ArrayList<>();
        }
    }

    public boolean isCompatible() {
        return this.mBluetoothAdapter != null;
    }

    public boolean isEnabled() {
        return this.mBluetoothAdapter.isEnabled();
    }

    private void initialize(PrinterEnum printerEnum) {
        this.mTimer = new Timer();
        this.mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                checkPrinter();
            }
        }, 120000, 120000);

        if (printerEnum == null) {
            this.mPrinter = new GenericPrinter(this, this.mListener);
            return;
        }

        switch (printerEnum) {
            case CMP_10BT:
                this.mPrinter = new CMP10BTPrinter(this, this.mListener);
                break;

            case MHT80:
                this.mPrinter = new MHT80Printer(this, this.mListener);
                break;

            case RP80A:
                this.mPrinter = new RP80APrinter(this, this.mListener);
                break;

            case RM80A:
                this.mPrinter = new RM80APrinter(this, this.mListener);
                break;

            case MTP_3:
                this.mPrinter = new MTP3Printer(this, this.mListener);
                break;

            case IPOS:
                this.mPrinter = new IPOSPrinter(this, this.mListener);
                break;

            case GENERIC:
            default:
                this.mPrinter = new GenericPrinter(this, this.mListener);
                break;
        }
    }

    private void checkPrinter() {
        if (this.mPrinter != null && !this.mPrinter.isPrinting()) {
            disconnect();
        }
    }

    private void cancelTimer() {
        if (this.mTimer == null)
            return;

        this.mTimer.cancel();
        this.mTimer = null;
    }

    public void connect(Context context, PrinterEnum printer) {
        connect(context, printer, null, this.mListener);
    }

    public void connect(Context context, PrinterEnum printer, String macAddress) {
        connect(context, printer, macAddress, this.mListener);
    }

    @SuppressLint("MissingPermission")
    public void connect(Context context, PrinterEnum printer, String macAddress, OnPrinterListener listener) {
        this.mBluetoothAdapter.cancelDiscovery();

        if (this.mDevice == null || !this.mDevice.getAddress().equals(macAddress)) {
            BluetoothDevice tmp = getPrinterDevice(printer, macAddress);

            if (tmp == null) {
                listener.onError(OnPrinterListener.Error.NO_DEVICE.getMessage());
                return;
            } else {
                this.mDevice = tmp;
            }
        }

        if (this.mDevice == null) {
            listener.onError(OnPrinterListener.Error.NO_DEVICE.getMessage());
            return;
        }

        if (isConnected()) {
            disconnect();
        }

        try {
            this.mBTSocket = this.mDevice.createInsecureRfcommSocketToServiceRecord(MY_UUID);
            this.mBTSocket.connect();

            initialize(printer);
            listener.onConnected();
        } catch (IOException e) {
            e.printStackTrace();

            disconnect();
            listener.onError(OnPrinterListener.Error.NO_DEVICE.getMessage());
        }
    }

    public boolean isConnected() {
        return this.mBTSocket != null && this.mBTSocket.isConnected();
    }

    public boolean hasAnyPrinter() {
        return hasAnyPrinter(null);
    }

    public boolean hasAnyPrinter(String macAddress) {
        if (this.mDevice != null)
            return true;
        BluetoothDevice device = getBluetoothDevice(macAddress);
        return device != null;
    }

    public BluetoothDevice getBluetoothDevice() {
        return getBluetoothDevice(null);
    }

    @SuppressLint("MissingPermission")
    public PrinterEnum getBondedPrinterEnum(Context context) {
        if (this.mDevices != null && !this.mDevices.isEmpty()) {
            for (PrinterEnum printer : PrinterEnum.values()) {
                for (BluetoothDevice device : this.mDevices) {
                    String name = device.getName();
                    if (name != null && !name.isEmpty() && name.contains(printer.getLabel())) {
                        return printer;
                    }
                }
            }
        }
        return null;
    }

    public BluetoothDevice getBluetoothDevice(String macAddress) {
        BluetoothDevice device = null;
        for (PrinterEnum printer : PrinterEnum.values()) {
            device = getPrinterDevice(printer, macAddress);
            if (device != null)
                break;
        }
        return device;
    }

    public boolean isPrinting() {
        return this.mPrinter != null && this.mPrinter.isPrinting();
    }

    @SuppressLint("MissingPermission")
    public BluetoothDevice getPrinterDevice(PrinterEnum printer, String macAddress) {
        BluetoothDevice tmp = null;
        if (this.mDevices != null && !this.mDevices.isEmpty()) {
            for (BluetoothDevice device : this.mDevices) {
                if (macAddress == null) {
                    String name = device.getName();
                    if ((name != null && !name.isEmpty() && name.contains(printer.getLabel())) || printer == PrinterEnum.GENERIC) {
                        tmp = device;
                        break;
                    }
                } else {
                    String address = device.getAddress();
                    if (address != null && !address.isEmpty() && address.equalsIgnoreCase(macAddress)) {
                        tmp = device;
                        break;
                    }
                }
            }
        }
        return tmp;
    }

    public void disconnect() {
        this.cancelTimer();
        if (this.mBTSocket != null && this.mBTSocket.isConnected()) {
            try {
                OutputStream os = this.mBTSocket.getOutputStream();
                if (os != null) {
                    os.flush();
                    os.close();
                }

                this.mBTSocket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        this.mBTSocket = null;
        this.mPrinter = null;
    }

    /**
     * Será exibida uma caixa de diálogo que solicitará a permissão do usuário para ativar o Bluetooth como mostrado na Figura 1. Se o usuário responder "Yes", o sistema começará a ativar o Bluetooth e o foco retornará ao aplicativo após a conclusão (ou falha) do processo.
     * A constante REQUEST_ENABLE_BT passada a startActivityForResult() é um inteiro definido localmente (que deve ser maior que 0) passado pelo sistema para a implementação de onActivityResult() como o parâmetro requestCode.
     * Se ativação do Bluetooth for bem-sucedida, a atividade receberá o código de resultado RESULT_OK no retorno de chamada onActivityResult(). Se o Bluetooth não foi ativado devido a um erro (ou à resposta "No" do usuário), o código de resultado será RESULT_CANCELED.
     *
     * @param activity
     */
    public void enableBluetooth(Activity activity) {
        enableBluetooth(activity, REQUEST_ENABLE_BT);
    }

    /**
     * Será exibida uma caixa de diálogo que solicitará a permissão do usuário para ativar o Bluetooth como mostrado na Figura 1. Se o usuário responder "Yes", o sistema começará a ativar o Bluetooth e o foco retornará ao aplicativo após a conclusão (ou falha) do processo.
     * A constante REQUEST_ENABLE_BT passada a startActivityForResult() é um inteiro definido localmente (que deve ser maior que 0) passado pelo sistema para a implementação de onActivityResult() como o parâmetro requestCode.
     * Se ativação do Bluetooth for bem-sucedida, a atividade receberá o código de resultado RESULT_OK no retorno de chamada onActivityResult(). Se o Bluetooth não foi ativado devido a um erro (ou à resposta "No" do usuário), o código de resultado será RESULT_CANCELED.
     *
     * @param activity
     * @param requestCode
     */
    @SuppressLint("MissingPermission")
    public void enableBluetooth(Activity activity, int requestCode) {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(enableBtIntent, requestCode);
    }

    public IPrinter getPrinter() {
        return this.mPrinter;
    }

    public void setOnPrinterListener(OnPrinterListener onPrinterListener) {
        this.mListener = onPrinterListener;
    }

    BluetoothSocket getBTSocket() {
        return mBTSocket;
    }

    public BluetoothDevice getDevice() {
        return mDevice;
    }

    public String format(int n, int length) {
        String str = Integer.toString(n);
        StringBuilder buffer = new StringBuilder();
        for (int i = str.length(); i < length; i++) {
            buffer.append('0');
        }
        buffer.append(str);
        return buffer.toString();
    }

    public String nextToken(StringBuffer buffer, String sep, boolean useSep) {
        int i = 0;
        // Pula o espaco inicial
        while (i < buffer.length() && sep.indexOf(buffer.charAt(i)) >= 0) {
            i++;
        }
        // Acumula o token
        StringBuilder token = new StringBuilder();
        while (i < buffer.length() && sep.indexOf(buffer.charAt(i)) < 0) {
            token.append(buffer.charAt(i++));
        }
        // Adiciona o ultimo separador
        if (useSep && i < buffer.length()) {
            token.append(buffer.charAt(i++));
        }
        char[] oldBuffer = buffer.toString().toCharArray();
        buffer.setLength(0);
        if (i < oldBuffer.length) {
            buffer.append(oldBuffer, i, oldBuffer.length - i);
        }
        return token.toString();
    }

    public String[] split(String str, String sep, int size) {
        Vector<String> lst = new Vector<>();
        StringBuffer strToken = new StringBuffer(str);
        StringBuilder line = new StringBuilder();
        while (strToken.length() > 0) {
            String token = nextToken(strToken, sep, true);
            // Testa se a uniao da linha corrente com o token cabe
            if (line.length() + token.length() > size) {
                lst.addElement(line.toString().trim());
                line.setLength(0);
            }
            // Se o token for maior que o tamanho maximo, parte em pedacos.
            if (token.length() > size) {
                for (int offset = 0; offset < token.length(); ) {
                    int len = Math.min(size, token.length() - offset);
                    line.append(token.substring(offset, len));
                    offset += len;
                }
            } else {
                line.append(token);
            }
        }
        if (line.length() > 0) {
            lst.addElement(line.toString().trim());
        }
        String[] arr = new String[lst.size()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = lst.elementAt(i);
        }
        return arr;
    }

    public String center(String str, int size) {
        StringBuilder buffer = new StringBuilder();
        int len = (size - str.length()) / 2;
        // Espaco em branco a esquerda
        for (int j = len; j > 0; j--) {
            buffer.append(' ');
        }
        // Conteudo da linha
        buffer.append(str);
        // Espaco em branco a direita
        for (int j = len; j > 0; j--) {
            buffer.append(' ');
        }
        if ((str.length() & 1) == 1) {
            buffer.append(' ');
        }
        return buffer.toString();
    }

    public String left(String str, int size) {
        StringBuilder buffer = new StringBuilder();
        // Conteudo da linha
        buffer.append(str);
        // Espaco em branco a direita
        for (int j = size - str.length(); j > 0; j--) {
            buffer.append(' ');
        }
        return buffer.toString();
    }

    public String right(String str, int size) {
        StringBuilder buffer = new StringBuilder();
        // Espaco em branco a esquerda
        for (int j = size - str.length(); j > 0; j--) {
            buffer.append(' ');
        }
        // Conteudo da linha
        buffer.append(str);
        return buffer.toString();
    }

    public byte[] fillBuffer(byte cnt, int size) {
        byte[] buffer = new byte[size];
        while (size-- > 0) {
            buffer[size] = cnt;
        }
        return buffer;
    }

}
