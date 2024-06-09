package http;

public enum HttpStatus {
    OK(200), NOT_FOUND(404);

    private final int statusCode;

    HttpStatus(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatus() {
        return this.statusCode + " " + this.name();
    }
}