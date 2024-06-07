public enum HTTPStatus {
    OK(200);

    private final int statusCode;

    HTTPStatus(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatus() {
        return this.statusCode + " " + this.name();
    }
}