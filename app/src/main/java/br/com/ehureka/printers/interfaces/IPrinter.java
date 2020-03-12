package br.com.ehureka.printers.interfaces;

import br.com.ehureka.printers.PrinterEnum;

public interface IPrinter {

    int FONT_A = 0;
    int FONT_B = 1;

    int LEFT_ALIGN = 0;
    int CENTER_ALIGN = 1;
    int RIGHT_ALIGN = 2;

    PrinterEnum getEnum();

    void print(byte[] data);
    void print(int data);
    void print(String text);
    void println(byte[] data);
    void println(int data);
    void println(String text);

    void println();
    void flush();
    void reset();

    int getWidth();
    void setFont(int font);
    void emphasized(boolean activate);
    void doubleHeight(boolean activate);
    void setAlign(int align);
    void barcode(char[] data, int width, int height);

    void setPrinting(boolean value);
    boolean isPrinting();
    void setPrinterListener(OnPrinterListener listener);

}
