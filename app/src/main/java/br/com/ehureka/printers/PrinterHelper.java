package br.com.ehureka.printers;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;

import br.com.ehureka.printers.interfaces.IPrinter;
import br.com.ehureka.printers.interfaces.OnPrinterListener;

public class PrinterHelper {

    private static final int REQUEST_ENABLE_BT = 999;
    private static final String TAG = "PrinterHelper";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private static PrinterHelper INSTANCE;

    private IPrinter mPrinter;
    private BluetoothAdapter mBluetoothAdapter;
    private OnPrinterListener mListener;

    private BluetoothDevice mDevice;
    private BluetoothSocket mBTSocket;

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
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mDevices = new ArrayList<>(this.mBluetoothAdapter.getBondedDevices());
    }

    public boolean isCompatible() {
        return this.mBluetoothAdapter != null;
    }

    public boolean isEnabled() {
        return this.mBluetoothAdapter.isEnabled();
    }

    private void initialize(PrinterEnum printerEnum) {
        if (this.mPrinter != null) {
            return;
        }

        switch (printerEnum) {
            case CMP_10BT:
            default:
                this.mPrinter = new CMP10BTPrinter(this, this.mListener);
                break;
        }
    }

    public void connect(PrinterEnum printer) {
        this.mBluetoothAdapter.cancelDiscovery();
        this.mDevices.clear();

        if (this.mDevice == null) {
            BluetoothDevice tmp = null;
            Set<BluetoothDevice> bondedDevices = this.mBluetoothAdapter.getBondedDevices();
            if (bondedDevices != null && !bondedDevices.isEmpty()) {
                for (BluetoothDevice device : bondedDevices) {
                    if (device.getName().contains(printer.getLabel())) {
                        tmp = device;
                        break;
                    }
                }
            }

            if (tmp == null) {
                this.mListener.onError(OnPrinterListener.Error.NO_DEVICE);
                return;
            } else {
                this.mDevice = tmp;
            }
        }

        try {
            this.mBTSocket = this.mDevice.createInsecureRfcommSocketToServiceRecord(MY_UUID);
            this.mBTSocket.connect();

            initialize(printer);
        } catch (IOException e) {
            if (this.mBTSocket != null) {
                try {
                    this.mBTSocket.close();
                } catch (IOException ignored) {}
            }
        }
    }

    public void disconnect() {
        if (this.mBTSocket != null) {
            try {
                OutputStream os = this.mBTSocket.getOutputStream();
                os.flush();
                os.close();

                this.mBTSocket.close();
            } catch (IOException ignored) {}

            this.mBTSocket = null;
        }
    }

    /**
     * Será exibida uma caixa de diálogo que solicitará a permissão do usuário para ativar o Bluetooth como mostrado na Figura 1. Se o usuário responder "Yes", o sistema começará a ativar o Bluetooth e o foco retornará ao aplicativo após a conclusão (ou falha) do processo.
     * A constante REQUEST_ENABLE_BT passada a startActivityForResult() é um inteiro definido localmente (que deve ser maior que 0) passado pelo sistema para a implementação de onActivityResult() como o parâmetro requestCode.
     * Se ativação do Bluetooth for bem-sucedida, a atividade receberá o código de resultado RESULT_OK no retorno de chamada onActivityResult(). Se o Bluetooth não foi ativado devido a um erro (ou à resposta "No" do usuário), o código de resultado será RESULT_CANCELED.
     * @param activity
     */
    public void enableBluetooth(Activity activity) {
        enableBluetooth(activity, REQUEST_ENABLE_BT);
    }

    /**
     * Será exibida uma caixa de diálogo que solicitará a permissão do usuário para ativar o Bluetooth como mostrado na Figura 1. Se o usuário responder "Yes", o sistema começará a ativar o Bluetooth e o foco retornará ao aplicativo após a conclusão (ou falha) do processo.
     * A constante REQUEST_ENABLE_BT passada a startActivityForResult() é um inteiro definido localmente (que deve ser maior que 0) passado pelo sistema para a implementação de onActivityResult() como o parâmetro requestCode.
     * Se ativação do Bluetooth for bem-sucedida, a atividade receberá o código de resultado RESULT_OK no retorno de chamada onActivityResult(). Se o Bluetooth não foi ativado devido a um erro (ou à resposta "No" do usuário), o código de resultado será RESULT_CANCELED.
     * @param activity
     * @param requestCode
     */
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
