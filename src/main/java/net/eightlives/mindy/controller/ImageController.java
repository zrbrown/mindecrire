package net.eightlives.mindy.controller;

import net.eightlives.mindy.config.custom.ImageBucketConfig;
import net.eightlives.mindy.model.ImageUploadResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;

import static net.eightlives.mindy.model.ImageUploadResponse.ImageUploadResult;

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
        ImageUploadResponse uploadResponse = new ImageUploadResponse();

        for (MultipartFile file : files) {
            String path = imageBucketConfig.getUrl() + "/" + imageBucketConfig.getName() + "/" + file.getOriginalFilename();
            int responseCode;

            try {
                URL url = new URL(path);
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                responseCode = connection.getResponseCode();
            } catch (MalformedURLException e) {
                LOG.error("Invalid image upload URL. Configuration is likely incorrect. URL: " + path, e);
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid image upload URL. Configuration is likely incorrect.");
            } catch (ProtocolException e) {
                LOG.error("Error while setting up image upload connection.", e);
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while setting up image upload connection");
            } catch (IOException e) {
                LOG.error("Error connecting to image upload server.", e);
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error connecting to image upload server");
            }

            if (responseCode == HttpStatus.FORBIDDEN.value()) {
                PutObjectRequest request = PutObjectRequest.builder()
                        .bucket(imageBucketConfig.getName())
                        .key(file.getOriginalFilename())
                        .contentType(file.getContentType())
                        .acl("public-read")
                        .build();

                try {
                    RequestBody body = RequestBody.fromInputStream(file.getInputStream(), file.getSize());

                    PutObjectResponse response = uploadClient.putObject(request, body);

                    if (response.sdkHttpResponse().isSuccessful()) {
                        uploadResponse.getSuccessful().add(new ImageUploadResult(file.getOriginalFilename(),
                                path));
                    } else {
                        uploadResponse.getFailed().add(new ImageUploadResult(file.getOriginalFilename(),
                                response.sdkHttpResponse().statusCode() + " " + response.sdkHttpResponse().statusText()));
                    }
                } catch (IOException e) {
                    LOG.error("Error reading file to upload", e);
                    uploadResponse.getFailed().add(new ImageUploadResult(file.getOriginalFilename(),
                            "File " + file.getOriginalFilename() + " could not be read. Try Again."));
                }
            } else {
                uploadResponse.getFailed().add(new ImageUploadResult(file.getOriginalFilename(),
                        "File " + file.getOriginalFilename() + " already exists"));
            }
        }

        return uploadResponse;
    }

    @DeleteMapping("/delete/{imageKey}")
    public String deleteImage(@PathVariable String imageKey, HttpServletResponse response) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(imageBucketConfig.getName())
                .key(imageKey)
                .build();

        DeleteObjectResponse deleteObjectResponse = uploadClient.deleteObject(request);

        response.setContentType("text/plain;charset=UTF-8");
        return deleteObjectResponse.requestChargedAsString();
    }
}
