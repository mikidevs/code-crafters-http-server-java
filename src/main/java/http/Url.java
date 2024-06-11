package http;

import java.util.Arrays;
import java.util.Objects;

public class Url {
    private final String url;

    public Url(String url) {
        this.url = url;
    }

    public String initial() {
        // since urls start with a /, initial element will be empty string
        String[] split = url.split("/");
        return switch (split.length) {
            case 0 -> "/";
            case 1 -> "/" + split[0];
            default -> String.join("/", Arrays.copyOfRange(split, 0, split.length));
        };
    }

    public String last() {
        String[] split = url.split("/");
        return switch (split.length) {
            case 0 -> "/";
            case 1 -> "/" + split[0];
            default -> split[split.length - 1];
        };
    }

    @Override
    public String toString() {
        return this.url;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.url);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || this.getClass() != obj.getClass()) return false;
        Url o = (Url) obj;
        return this.url.equals(o.url);
    }
}
