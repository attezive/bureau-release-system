package bureau.release.system.service.dto;

public record RepositoryReference(String host, String name, String reference) {
    public static RepositoryReference parse(String url, String reference) {
        String[] parts = url.split("/", 2);
        String host = parts[0];
        String name = parts[1];

        return new RepositoryReference(host, name, reference);
    }

    public static RepositoryReference parse(String url) {
        String[] parts = url.split("/");
        String host = parts[0];
        String name = parts[1];
        String reference = parts[parts.length-1];

        return new RepositoryReference(host, name, reference);
    }
}
