package net.eightlives.mindecrire.controller;

import net.eightlives.mindecrire.config.custom.ImageBucketConfig;
import net.eightlives.mindecrire.model.ImageUploadResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static net.eightlives.mindecrire.model.ImageUploadResponse.ImageUploadResult;

@RestController
@CrossOrigin
@RequestMapping("/content/image")
public class ImageController {

    private static final Logger LOG = LoggerFactory.getLogger(ImageController.class);

    private final ImageBucketConfig imageBucketConfig;
    private final S3Client uploadClient;

    public ImageController(ImageBucketConfig imageBucketConfig, S3Client uploadClient) {
        this.imageBucketConfig = imageBucketConfig;
        this.uploadClient = uploadClient;
    }

    @PostMapping("/add")
    public ImageUploadResponse addImages(@RequestParam("files") List<MultipartFile> files) {
        HeadBucketResponse bucketResponse = uploadClient.headBucket(req -> req.bucket(imageBucketConfig.getName()));
        if (!bucketResponse.sdkHttpResponse().isSuccessful()) {
            LOG.error("Object storage bucket {} is misconfigured: {}", imageBucketConfig.getName(), bucketResponse.sdkHttpResponse().statusText());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Server is misconfigured. Cannot upload.");
        }

        ImageUploadResponse uploadResponse = new ImageUploadResponse();

        for (MultipartFile file : files) {
            HeadObjectResponse headResponse = uploadClient.headObject(req -> req
                    .bucket(imageBucketConfig.getName())
                    .key(file.getOriginalFilename()));

            if (headResponse.sdkHttpResponse().isSuccessful()) {
                uploadResponse.getFailed().add(new ImageUploadResult(file.getOriginalFilename(),
                        "File " + file.getOriginalFilename() + " already exists"));
            } else {
                try {
                    PutObjectResponse response = uploadClient.putObject(req -> req
                                    .bucket(imageBucketConfig.getName())
                                    .key(file.getOriginalFilename())
                                    .contentType(file.getContentType())
                                    .acl(ObjectCannedACL.PUBLIC_READ),
                            RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

                    if (response.sdkHttpResponse().isSuccessful()) {
                        uploadResponse.getSuccessful().add(new ImageUploadResult(file.getOriginalFilename(),
                                uploadClient.utilities().getUrl(r -> r.bucket(imageBucketConfig.getName()).key(file.getOriginalFilename())).toExternalForm()));
                    } else {
                        uploadResponse.getFailed().add(new ImageUploadResult(file.getOriginalFilename(),
                                response.sdkHttpResponse().statusCode() + " " + response.sdkHttpResponse().statusText().orElse("No error message")));
                    }
                } catch (IOException e) {
                    LOG.error("Error reading file to upload", e);
                    uploadResponse.getFailed().add(new ImageUploadResult(file.getOriginalFilename(),
                            "File " + file.getOriginalFilename() + " could not be read. Try Again."));
                }
            }
        }

        return uploadResponse;
    }

    @DeleteMapping("/delete/{imageKey}")
    public String deleteImage(@PathVariable String imageKey, HttpServletResponse response) {
        DeleteObjectResponse deleteObjectResponse = uploadClient.deleteObject(req -> req
                .bucket(imageBucketConfig.getName())
                .key(imageKey));
        response.setContentType(MediaType.TEXT_PLAIN_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        return deleteObjectResponse.requestChargedAsString();
    }
}
