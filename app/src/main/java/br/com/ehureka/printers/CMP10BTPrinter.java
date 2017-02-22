package br.com.ehureka.printers;

import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.OutputStream;

import br.com.ehureka.printers.interfaces.IPrinter;
import br.com.ehureka.printers.interfaces.OnPrinterListener;

class CMP10BTPrinter implements IPrinter {

    private static final int FONT_A = 0;
    private static final int FONT_B = 1;

    private static final int LEFT_ALIGN = 0;
    private static final int CENTER_ALIGN = 1;
    private static final int RIGHT_ALIGN = 2;

    private byte mode;
    private static final byte[] UNIQUE_BYTE = new byte[1];

    private final PrinterHelper mBTHelper;
    private final OnPrinterListener mListener;

    CMP10BTPrinter(PrinterHelper helper, OnPrinterListener listener) {
        this.mBTHelper = helper;
        this.mListener = listener;
    }

    @Override
    public void print(byte[] data) {
        BluetoothSocket btSocket = this.mBTHelper.getBTSocket();
        try {
            OutputStream os = btSocket.getOutputStream();
            os.write(data);
        } catch (IOException e) {
            e.printStackTrace();
            this.mListener.onError(OnPrinterListener.Error.OUTPUT_PROBLEM);
        }
    }

    @Override
    public void print(int data) {
        UNIQUE_BYTE[0] = (byte) data;
        print(UNIQUE_BYTE);
    }

    @Override
    public void print(String text) {
        byte[] data = translate(text);
        print(data);
    }

    @Override
    public void println(byte[] data) {
        print(data);
        println();
    }

    @Override
    public void println(int data) {
        print(data);
        println();
    }

    @Override
    public void println(String text) {
        print(text);
        println();
    }

    @Override
    public void println() {
        print(new byte[]{0x0A});
    }

    @Override
    public void flush() {
        BluetoothSocket btSocket = this.mBTHelper.getBTSocket();
        try {
            OutputStream os = btSocket.getOutputStream();
            os.flush();
            this.mListener.onPrint();
        } catch (IOException e) {
            e.printStackTrace();
            this.mListener.onError(OnPrinterListener.Error.PRINT_PROBLEM);
        }
    }

    @Override
    public void reset() {
        // Inicializa a impressora
        print(new byte[] { 0x1B, 0x40 });

        // Coloca em modo padrao
        this.mode = 0;
        print(new byte[] { 0x1B, 0x21, this.mode });
    }

    @Override
    public int getWidth() {
        if ((this.mode & 0x01) == 1) {
            return 41; // FONTE B
        }
        return 31; // FONTE A
    }

    @Override
    public void setFont(int font) {
        switch (font) {
            case FONT_A:
                this.mode &= ~0x01;
                break;

            case FONT_B:
                this.mode |= 0x01;
                break;
        }

        print(new byte[] { 0x1B, 0x21, this.mode });
    }

    @Override
    public void emphasized(boolean activate) {
        if (activate) {
            this.mode |= 0x08;
        } else {
            this.mode &= ~0x08;
        }

        print(new byte[] { 0x1B, 0x21, this.mode });
    }

    @Override
    public void doubleHeight(boolean activate) {
        if (activate) {
            this.mode |= 0x10;
        } else {
            this.mode &= ~0x10;
        }

        print(new byte[] { 0x1B, 0x21, this.mode });
    }

    @Override
    public void setAlign(int align) {
        byte n = -1;
        switch (align) {
            case LEFT_ALIGN:
                n = 0x00;
                break;

            case CENTER_ALIGN:
                n = 0x01;
                break;

            case RIGHT_ALIGN:
                n = 0x02;
                break;
        }

        if (n != -1) {
            print(new byte[] { 0x1B, 0x61, n });
        }
    }

    @Override
    public void barcode(char[] data, int width, int height) {
        // Altura
        print(new byte[] { 0x1D, 0x68, (byte) (height & 0xFF) });

        // Largura
        print(new byte[] { 0x1D, 0x77, (byte) (width % 6) });

        // Tipo do codigo de barras (ITF)
        print(new byte[] { 0x1D, 0x6B, 0x05 });

        // Dados (converte pra bytes)
        int size = (data.length & 1) == 1 ? data.length + 1 : data.length;
        byte[] bData = new byte[size];
        for (int i = 0; i < data.length; i++) {
            bData[i] = (byte) data[i];
        }

        if ((data.length & 1) == 1) {
            bData[data.length] = '0';
        }
        print(bData);

        // Termina
        print(0x00);

        // Imprime e vai pra linha seguinte
        println();
    }

    private byte[] translate(String text) {
        char[] chars = text.toCharArray();
        byte[] data = new byte[chars.length];

        for (int i = 0;i < chars.length;i++) {
            data[i] = translate(chars[i]);
        }

        return data;
    }

    private byte translate(char ch) {
        //http://htmlhelp.com/reference/charset/iso192-223.html
        switch (ch) {
            // A
            case 192://'À'
            case 193://'Á'
            case 194://'Â'
            case 195://'Ã'
            case 196://
            case 197://
                return (byte) 'A';
            case 224://'à':
            case 225://'á':
            case 226://'â':
            case 227://'ã':
            case 228:
            case 229:
                return (byte) 'a';
            // Cedil
            case 199://'Ç'
                return (byte) 'C';
            case 231://'ç':
                return (byte) 'c';
            // E
            case 200://'È':
            case 201://'É':
            case 202://'Ê':
            case 203://
                return (byte) 'E';
            case 232://'è':
            case 233://'é':
            case 234://'ê':
            case 235:
                return (byte) 'e';
            // I
            case 204://'Ì':
            case 205://'Í':
            case 206://'Î':
            case 207:
                return (byte) 'I';
            case 236://'ì':
            case 237://'í':
            case 238://'î':
            case 239:
                return (byte) 'i';
            // N
            case 209://'Ñ':
                return (byte) 'N';
            case 241://'ñ':
                return (byte) 'n';
            // O
            case 210://'Ò':
            case 211://'Ó':
            case 212://'Ô':
            case 213://'Õ':
            case 214:
                return (byte) 'O';
            case 242://'ò':
            case 243://'ó':
            case 244://'ô':
            case 245://'õ':
            case 246:
                return (byte) 'o';
            // U
            case 217://'Ù':
            case 218://'Ú':
            case 219://'Û':
            case 220://'Ü':
                return (byte) 'U';
            case 249://'ù':
            case 250://'ú':
            case 251://'û':
            case 252://'ü':
                return (byte) 'u';
            // Caracteres especiais
            case 170://'ª':
                return (byte) 0xAA;
            case 186://'º'
            case 176://'°'
                return (byte) 0xBA;
            case 183://'·'
                return (byte) '.';
            case '0':
                return (byte) 'O';
        }
        return (byte) ch;
    }

}
