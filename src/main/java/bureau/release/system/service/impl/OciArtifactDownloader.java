package bureau.release.system.service.impl;

import bureau.release.system.config.OciRegistryProperties;
import bureau.release.system.model.Firmware;
import bureau.release.system.model.FirmwareVersion;
import bureau.release.system.model.Hardware;
import bureau.release.system.model.Release;
import bureau.release.system.network.OciRegistryClient;
import bureau.release.system.service.ArtifactDownloader;
import bureau.release.system.exception.ReleaseStreamException;
import bureau.release.system.service.dto.client.Manifest;
import bureau.release.system.service.dto.client.ManifestLayer;
import bureau.release.system.service.dto.client.TagList;
import feign.Response;
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

    @Override
    public Manifest getManifest(String repositoryName, String reference) {
        log.debug("Getting Manifest for Reference {} from Repository {}", reference,  repositoryName);
        Manifest manifest = ociClient.getManifest(
                repositoryName,
                reference,
                getBasicAuthHeader());
        manifest.setName(repositoryName);
        manifest.setReference(reference);
        return manifest;
    }

    //TODO Add duplicate files loading per one time
    @Override
    public void loadReleaseContent(Release release, OutputStream outputStream) {
        log.debug("Loading Release Content: releaseId={}", release.getId());
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
                                           List<FirmwareVersion> firmwareVersionList) throws IOException {
        log.debug("Creating Directories and Files");
        Set<String> createdDirectories = new HashSet<>();
        for (FirmwareVersion firmwareVersion : firmwareVersionList) {
            Firmware firmware = firmwareVersion.getFirmware();
            Hardware hardware = firmwareVersion.getHardware();
            String dirEntryName = hardware.getName();
            if (!createdDirectories.contains(dirEntryName)) {
                TarArchiveEntry dirEntry = new TarArchiveEntry(dirEntryName+"/"+firmware.getName());
                dirEntry.setMode(TarArchiveEntry.DEFAULT_DIR_MODE);
                tarOut.putArchiveEntry(dirEntry);
                tarOut.closeArchiveEntry();
                createdDirectories.add(dirEntryName);
            }

            Manifest manifest = getManifest(firmware.getOciName(), firmwareVersion.getFirmwareVersion());
            for (ManifestLayer manifestLayer : manifest.getLayers()) {
                addFileToTar(tarOut, manifestLayer, hardware.getName()+"/"+firmware.getName(), firmware.getOciName());
            }
        }
    }

    private void addFileToTar(TarArchiveOutputStream tarOut,
                              ManifestLayer manifestLayer,
                              String dirName,
                              String repositoryName) throws IOException {
        String fileName = dirName + "/" + manifestLayer.getAnnotations().getTitle();
        log.debug("Adding File {} to Tar", fileName);
        try (Response response = ociClient.getBlob(repositoryName, manifestLayer.getDigest(), getBasicAuthHeader());
             InputStream fileStream = response.body().asInputStream()) {
            TarArchiveEntry entry = new TarArchiveEntry(fileName);
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
    public List<Manifest> getArtifacts(String repositoryName) {
        log.debug("Getting Artifacts for Repository {}", repositoryName);

        ResponseEntity<TagList> response = ociClient.getArtifactTagList(repositoryName, getBasicAuthHeader());
        TagList tagList = response.getBody();
        log.debug("TagList: {}", tagList);
        if (tagList == null) {
            return Collections.emptyList();
        }

        return tagList.getTags().stream().map(tag -> getManifest(repositoryName, tag)).toList();
    }

    private String getBasicAuthHeader() {
        String credentials = properties.ecrUsername() + ":" + properties.ecrPassword();
        String encoded = Base64.getEncoder().encodeToString(credentials.getBytes());
        return "Basic " + encoded;
    }
}
