package br.com.ehureka.printers.interfaces;

public interface IPrinter {

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

}
