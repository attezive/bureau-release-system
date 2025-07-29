package bureau.release.system.service.impl;

import bureau.release.system.config.OciRegistryProperties;
import bureau.release.system.dal.FirmwareDao;
import bureau.release.system.model.Firmware;
import bureau.release.system.network.OciRegistryClient;
import bureau.release.system.service.ArtifactDownloader;
import bureau.release.system.service.dto.FirmwareVersionDto;
import bureau.release.system.service.dto.ReleaseDto;
import bureau.release.system.exception.ReleaseStreamException;
import bureau.release.system.service.dto.client.Artifact;
import bureau.release.system.exception.ClientNotFoundException;
import bureau.release.system.service.dto.client.Manifest;
import bureau.release.system.service.dto.client.ManifestLayer;
import feign.Response;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class OciArtifactDownloader implements ArtifactDownloader {
    private final OciRegistryClient ociClient;
    private final OciRegistryProperties properties;
    private final FirmwareDao firmwareDao;

    @Override
    public Manifest getManifest(String repositoryName, String reference) {
        Manifest manifest = ociClient.getManifest(
                repositoryName,
                reference,
                getBasicAuthHeader());
        manifest.setName(repositoryName);
        manifest.setReference(reference);
        return manifest;
    }

    @Override
    public void loadReleaseContent(ReleaseDto release, OutputStream outputStream) {
        try (TarArchiveOutputStream tarOut = new TarArchiveOutputStream(outputStream)) {
            tarOut.setLongFileMode(TarArchiveOutputStream.LONGFILE_POSIX);
            tarOut.setBigNumberMode(TarArchiveOutputStream.BIGNUMBER_POSIX);

            createDirectoriesAndFiles(tarOut, release.getFirmwareVersions());

            tarOut.finish();
        } catch (IOException e) {
            throw new ReleaseStreamException(e.getMessage());
        }
    }

    private void createDirectoriesAndFiles(TarArchiveOutputStream tarOut,
                                           List<FirmwareVersionDto> firmwareVersionDtoList) throws IOException {
        Set<String> createdDirectories = new HashSet<>();
        for (FirmwareVersionDto firmwareVersionDto : firmwareVersionDtoList) {
            Firmware firmware = firmwareDao.findById(firmwareVersionDto.getFirmwareId())
                    .orElseThrow(() -> new EntityNotFoundException("Firmware not found"));

            String dirEntryName = firmware.getName();
            if (createdDirectories.contains(dirEntryName)) continue;

            TarArchiveEntry dirEntry = new TarArchiveEntry(dirEntryName+"/");
            dirEntry.setMode(TarArchiveEntry.DEFAULT_DIR_MODE);
            tarOut.putArchiveEntry(dirEntry);
            tarOut.closeArchiveEntry();
            createdDirectories.add(dirEntryName);

            Manifest manifest = getManifest(firmware.getOciName(), firmwareVersionDto.getFirmwareVersion());
            for (ManifestLayer manifestLayer : manifest.getLayers()) {
                addFileToTar(tarOut, manifestLayer, firmware.getName(), firmware.getOciName());
            }
        }
    }

    private void addFileToTar(TarArchiveOutputStream tarOut,
                              ManifestLayer manifestLayer,
                              String dirName,
                              String repositoryName) throws IOException {

        try (Response response = ociClient.getBlob(repositoryName, manifestLayer.getDigest(), getBasicAuthHeader());
             InputStream fileStream = response.body().asInputStream()) {
            TarArchiveEntry entry = new TarArchiveEntry(dirName + "/" + manifestLayer.getAnnotations().getTitle());
            String contentLength = response.headers().get("Content-Length").stream()
                    .findFirst()
                    .orElse(String.valueOf(manifestLayer.getSize()));
            entry.setSize(Long.parseLong(contentLength));

            tarOut.putArchiveEntry(entry);
            fileStream.transferTo(tarOut);
            tarOut.closeArchiveEntry();
        }
    }

    @Override
    public List<Artifact> getArtifacts(String harborProjectName, String harborRepositoryName) {
        ResponseEntity<List<Artifact>> response = ociClient
                .getArtifacts(
                        harborProjectName,
                        harborRepositoryName,
                        getBasicAuthHeader()
                );
        List<Artifact> artifacts = response.getBody();
        if (artifacts == null || artifacts.isEmpty()) {
            throw new ClientNotFoundException("No repository artifacts found: " + harborProjectName + "/" + harborRepositoryName);
        }
        return artifacts;
    }

    private String getBasicAuthHeader() {
        String credentials = properties.ecrUsername() + ":" + properties.ecrPassword();
        String encoded = Base64.getEncoder().encodeToString(credentials.getBytes());
        return "Basic " + encoded;
    }
}
