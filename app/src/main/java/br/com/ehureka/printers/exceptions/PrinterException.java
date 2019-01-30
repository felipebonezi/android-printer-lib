package br.com.ehureka.printers.exceptions;

public class PrinterException extends Exception {

    private final String message;

    public PrinterException() {
        this.message = "PrinterException default message";
    }

    public PrinterException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
