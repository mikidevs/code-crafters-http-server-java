package http.constants;

public enum HttpStatus {
    OK(200, "OK"), NOT_FOUND(404, "Not Found");

    private final int statusCode;
    private final String statusText;

    HttpStatus(int statusCode, String statusText) {
        this.statusCode = statusCode;
        this.statusText = statusText;
    }

    public String getStatus() {
        return this.statusCode + " " + this.statusText;
    }
}