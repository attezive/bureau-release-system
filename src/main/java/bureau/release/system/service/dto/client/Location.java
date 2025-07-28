package bureau.release.system.service.dto.client;

public record Location(String uuid, String state) {
    public static Location parse(String locationUrl) {
        String[] parts = locationUrl.split("/");
        String uploadLink = parts[parts.length - 1];
        String[] uploadParts = uploadLink.split("\\?");
        String uploadUuid = uploadParts[0];
        String uploadState = uploadParts[1].split("=")[1];
        return new Location(uploadUuid, uploadState);
    }
}
