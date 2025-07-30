package bureau.release.system.service.impl;

import bureau.release.system.config.OciRegistryProperties;
import bureau.release.system.service.ArtifactUploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrasArtifactUploader implements ArtifactUploader {
    private final OciRegistryProperties properties;

    public void login() throws IOException {
        String command = String.format("oras login %s -u %s -p %s",
                properties.url().replace("http://", ""),
                properties.ecrUsername(),
                properties.ecrPassword());
        log.debug("Execute command login: {}", command);
        Process process = Runtime.getRuntime().exec(command);
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
    }

    @Override
    public String uploadArtifact(ByteArrayOutputStream artifactBody, String artifactName, String ociName, String reference) {
        createFile(artifactBody, artifactName);
        log.debug("File created: {}", artifactName);

        String command = String.format("oras push %s/%s:%s %s",
                properties.url().replace("http://", ""),
                ociName,
                reference,
                artifactName);

        String digest;
        log.debug("Execute command upload: {}", command);
        try {
            digest = executeUploadCommand(command);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (deleteFile(artifactName)) {
                log.debug("File deleted: {}", artifactName);
            } else {
                log.error("Delete file failed: {}", artifactName);
            }
        }
        return digest;
    }

    private String executeUploadCommand(String command) throws IOException {
        Process process = Runtime.getRuntime().exec(command);
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        String digest = "";
        while ((line = reader.readLine()) != null) {
            log.debug("Artifact Oras Upload : {}", line);
            if (line.contains("Digest")) {
                digest = line.replace("Digest: ", "");
                log.info("Artifact Digest: {}", digest);
            }
        }
        reader.close();
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        while ((line = errorReader.readLine()) != null) {
            log.error("Artifact Oras Upload: {}", line);
            if (line.contains("Error: basic credential not found")){
                login();
                digest = executeUploadCommand(command);
            }
        }
        return digest;
    }

    private void createFile(ByteArrayOutputStream artifactBody, String artifactName) {
        log.debug("Creating file: {}", artifactName);
        File file = new File(artifactName);
        try (OutputStream out = new FileOutputStream(file)) {
            artifactBody.writeTo(out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean deleteFile(String artifactName) {
        File file = new File(artifactName);
        return file.delete();
    }
}
