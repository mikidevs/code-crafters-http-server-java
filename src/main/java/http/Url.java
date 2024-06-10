package http;

import java.util.Arrays;

public class Url {
    private final String[] parts;

    public Url(String url) {
        this.parts = url.split("/");
    }

    public String[] parts() {
        return this.parts;
    }

    public String initial() {
        if (parts.length > 1) {
            return String.join("/", Arrays.copyOfRange(parts, 0, parts.length - 1));
        } else {
            return parts[0];
        }
    }

    public String last() {
        return this.parts[parts.length - 1];
    }

    @Override
    public String toString() {
        return String.join("/", parts);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(parts);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || this.getClass() != obj.getClass()) return false;
        Url o = (Url) obj;
        return Arrays.equals(this.parts, o.parts);
    }
}
