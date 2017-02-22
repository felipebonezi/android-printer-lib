package br.com.ehureka.printers.interfaces;

public interface OnPrinterListener {

    enum Error {
        PRINT_PROBLEM("We've some issues to print.")
        ,NO_DEVICE("No device was found.")
        ,OUTPUT_PROBLEM("We've some issues with your connection to the printer.")
        ;

        private String message;

        Error(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

    }

    void onError(Error error);
    void onPrint();

}
