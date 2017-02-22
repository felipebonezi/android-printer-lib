package br.com.ehureka.printers.interfaces;

public interface OnPrinterListener {

    public final class Result {
        public static final int SUCCESS = 0;
        public static final int ERROR = 1;
    }

    public final class Error {
        public static final int NO_DEVICE = 0;
        public static final int OUTPUT_PROBLEM = 1;
    }

    void onError(int error);
    void onPrint(int result);

}
