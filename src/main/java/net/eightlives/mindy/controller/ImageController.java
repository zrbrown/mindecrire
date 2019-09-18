package net.eightlives.mindy.controller;

import net.eightlives.mindy.config.ImageBucketConfig;
import net.eightlives.mindy.model.ImageUploadResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import static net.eightlives.mindy.model.ImageUploadResponse.ImageUploadResult;

@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/content/image")
public class ImageController {

    private final ImageBucketConfig imageBucketConfig;
    private final S3Client uploadClient;

    @Autowired
    public ImageController(ImageBucketConfig imageBucketConfig, S3Client uploadClient) {
        this.imageBucketConfig = imageBucketConfig;
        this.uploadClient = uploadClient;
    }

    @PostMapping("/add")
    public ImageUploadResponse addImages(@RequestParam("files") List<MultipartFile> files) {
        ImageUploadResponse uploadResponse = new ImageUploadResponse();

        for (MultipartFile file : files) {
            try {
                String path = imageBucketConfig.getUrl() + "/" + imageBucketConfig.getName() + "/" + file.getOriginalFilename();
                URL url = new URL(path);
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                if (connection.getResponseCode() == 403) {
                    PutObjectRequest request = PutObjectRequest.builder()
                            .bucket(imageBucketConfig.getName())
                            .key(file.getOriginalFilename())
                            .contentType(file.getContentType())
                            .acl("public-read")
                            .build();
                    RequestBody body = RequestBody.fromInputStream(file.getInputStream(), file.getSize());

                    PutObjectResponse response = uploadClient.putObject(request, body);

                    if (response.sdkHttpResponse().isSuccessful()) {
                        uploadResponse.getSuccessful().add(new ImageUploadResult(file.getOriginalFilename(),
                                path));
                    } else {
                        uploadResponse.getFailed().add(new ImageUploadResult(file.getOriginalFilename(),
                                response.sdkHttpResponse().statusCode() + " " + response.sdkHttpResponse().statusText()));
                    }
                } else {
                    uploadResponse.getFailed().add(new ImageUploadResult(file.getOriginalFilename(),
                            "File " + file.getOriginalFilename() + " already exists"));
                }
            } catch (IOException e) {
                log.error("Error reading file", e);
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
