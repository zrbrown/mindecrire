package net.eightlives.mindecrire.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.SdkRequest;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ImageControllerTest extends ControllerTest {

    MockMultipartFile file;
    MockMultipartFile file2;
    MockMultipartFile existingFile;
    MockMultipartFile failedUploadFile;
    MockMultipartFile failedUploadNoMessageFile;
    MockMultipartFile ioExceptionFile;

    @DisplayName("Adding images")
    @Nested
    class Add {

        @BeforeEach
        void beforeEach() throws IOException {
            file = new MockMultipartFile("files", "hello.txt", MediaType.IMAGE_JPEG_VALUE, Files.readAllBytes(Paths.get("src","test","resources","images","test-img.jpg")));
            file2 = new MockMultipartFile("files", "hello2.txt", MediaType.IMAGE_GIF_VALUE, Files.readAllBytes(Paths.get("src","test","resources","images","test-img.gif")));
            existingFile = new MockMultipartFile("files", "hello3.txt", MediaType.IMAGE_PNG_VALUE, Files.readAllBytes(Paths.get("src","test","resources","images","test-img.png")));
            failedUploadFile = new MockMultipartFile("files", "hello4.txt", "image/webp", Files.readAllBytes(Paths.get("src","test","resources","images","test-img.webp")));
            failedUploadNoMessageFile = new MockMultipartFile("files", "hello5.txt", "image/avif", Files.readAllBytes(Paths.get("src","test","resources","images","test-img.avif")));
            ioExceptionFile = spy(new MockMultipartFile("files", "hello6.txt", "image/tiff", Files.readAllBytes(Paths.get("src","test","resources","images","test-img.tiff"))));
            lenient().doThrow(new IOException()).when(ioExceptionFile).getInputStream();

            try (var realClient = S3Client.create()) {
                when(s3Client.utilities()).thenReturn(realClient.utilities());
            }
        }

        @DisplayName("when bucket does not exist")
        @Test
        void bucketNotExist() throws Exception {
            doReturn(HeadBucketResponse.builder()
                    .applyMutation(m -> m.sdkHttpResponse(SdkHttpResponse.builder().statusCode(403).build()))
                    .build()).when(s3Client).headBucket(argThat(new SdkRequestMatcher<>(HeadBucketRequest.builder(),
                    builder -> builder.build().bucket().equals("pics")
            )));

            mvc.perform(multipart("/content/image/add")
                            .file(file)
                            .with(authentication(getOauthAuthenticationFor(createOAuth2User("zrbrown", "Zack Brown", "all-perms"))))
                            .with(csrf()))
                    .andExpect(status().isInternalServerError())
                    .andExpect(status().reason("Server is misconfigured. Cannot upload."));
        }

        @DisplayName("when bucket exists")
        @Nested
        class BucketExists {

            @BeforeEach
            void beforeEach() {
                doReturn(HeadBucketResponse.builder()
                        .applyMutation(m -> m.sdkHttpResponse(SdkHttpResponse.builder().statusCode(200).build()))
                        .build()).when(s3Client).headBucket(argThat(new SdkRequestMatcher<>(HeadBucketRequest.builder(),
                        builder -> builder.build().bucket().equals("pics")
                )));
            }

            @DisplayName("GET /content/image/add with single image")
            @Test
            void addImage() throws Exception {
                doReturn(HeadObjectResponse.builder()
                        .applyMutation(m -> m.sdkHttpResponse(SdkHttpResponse.builder().statusCode(403).build()))
                        .build()).when(s3Client).headObject(argThat(new SdkRequestMatcher<>(HeadObjectRequest.builder(),
                        builder -> builder.build().bucket().equals("pics") && builder.build().key().equals(file.getOriginalFilename())
                )));
                doReturn(PutObjectResponse.builder()
                        .applyMutation(m -> m.sdkHttpResponse(SdkHttpResponse.builder().statusCode(200).build()))
                        .build())
                        .when(s3Client).putObject(
                                argThat(new SdkRequestMatcher<>(PutObjectRequest.builder(),
                                        builder -> builder.build().bucket().equals("pics") &&
                                                builder.build().key().equals(file.getOriginalFilename()) &&
                                                builder.build().contentType().equals(file.getContentType()) &&
                                                builder.build().acl().equals(ObjectCannedACL.PUBLIC_READ))),
                                argThat(new MultipartMatcher(file)));

                mvc.perform(multipart("/content/image/add")
                                .file(file)
                                .with(authentication(getOauthAuthenticationFor(createOAuth2User("zrbrown", "Zack Brown", "all-perms"))))
                                .with(csrf()))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(content().json("{\"successful\":[{\"filename\":\"hello.txt\",\"result\":\"https://pics.s3.us-west-2.amazonaws.com/hello.txt\"}],\"failed\":[]}"));
            }

            @DisplayName("GET /content/image/add with multiple images, some failing")
            @Test
            void addImages() throws Exception {
                doReturn(HeadObjectResponse.builder()
                        .applyMutation(m -> m.sdkHttpResponse(SdkHttpResponse.builder().statusCode(403).build()))
                        .build()).when(s3Client).headObject(argThat(new SdkRequestMatcher<>(HeadObjectRequest.builder(),
                        builder -> builder.build().bucket().equals("pics") && builder.build().key().equals(file.getOriginalFilename())
                )));
                doReturn(HeadObjectResponse.builder()
                        .applyMutation(m -> m.sdkHttpResponse(SdkHttpResponse.builder().statusCode(403).build()))
                        .build()).when(s3Client).headObject(argThat(new SdkRequestMatcher<>(HeadObjectRequest.builder(),
                        builder -> builder.build().bucket().equals("pics") && builder.build().key().equals(file2.getOriginalFilename())
                )));
                doReturn(HeadObjectResponse.builder()
                        .applyMutation(m -> m.sdkHttpResponse(SdkHttpResponse.builder().statusCode(200).build()))
                        .build()).when(s3Client).headObject(argThat(new SdkRequestMatcher<>(HeadObjectRequest.builder(),
                        builder -> builder.build().bucket().equals("pics") && builder.build().key().equals(existingFile.getOriginalFilename())
                )));
                doReturn(HeadObjectResponse.builder()
                        .applyMutation(m -> m.sdkHttpResponse(SdkHttpResponse.builder().statusCode(403).build()))
                        .build()).when(s3Client).headObject(argThat(new SdkRequestMatcher<>(HeadObjectRequest.builder(),
                        builder -> builder.build().bucket().equals("pics") && builder.build().key().equals(failedUploadFile.getOriginalFilename())
                )));
                doReturn(HeadObjectResponse.builder()
                        .applyMutation(m -> m.sdkHttpResponse(SdkHttpResponse.builder().statusCode(403).build()))
                        .build()).when(s3Client).headObject(argThat(new SdkRequestMatcher<>(HeadObjectRequest.builder(),
                        builder -> builder.build().bucket().equals("pics") && builder.build().key().equals(failedUploadNoMessageFile.getOriginalFilename())
                )));
                doReturn(HeadObjectResponse.builder()
                        .applyMutation(m -> m.sdkHttpResponse(SdkHttpResponse.builder().statusCode(403).build()))
                        .build()).when(s3Client).headObject(argThat(new SdkRequestMatcher<>(HeadObjectRequest.builder(),
                        builder -> builder.build().bucket().equals("pics") && builder.build().key().equals(ioExceptionFile.getOriginalFilename())
                )));

                doReturn(PutObjectResponse.builder()
                        .applyMutation(m -> m.sdkHttpResponse(SdkHttpResponse.builder().statusCode(200).build()))
                        .build())
                        .when(s3Client).putObject(
                                argThat(new SdkRequestMatcher<>(PutObjectRequest.builder(),
                                        builder -> builder.build().bucket().equals("pics") &&
                                                builder.build().key().equals(file.getOriginalFilename()) &&
                                                builder.build().contentType().equals(file.getContentType()) &&
                                                builder.build().acl().equals(ObjectCannedACL.PUBLIC_READ))),
                                argThat(new MultipartMatcher(file)));
                doReturn(PutObjectResponse.builder()
                        .applyMutation(m -> m.sdkHttpResponse(SdkHttpResponse.builder().statusCode(200).build()))
                        .build())
                        .when(s3Client).putObject(
                                argThat(new SdkRequestMatcher<>(PutObjectRequest.builder(),
                                        builder -> builder.build().bucket().equals("pics") &&
                                                builder.build().key().equals(file2.getOriginalFilename()) &&
                                                builder.build().contentType().equals(file2.getContentType()) &&
                                                builder.build().acl().equals(ObjectCannedACL.PUBLIC_READ))),
                                argThat(new MultipartMatcher(file2)));
                doReturn(PutObjectResponse.builder()
                        .applyMutation(m -> m.sdkHttpResponse(SdkHttpResponse.builder().statusCode(500).statusText("It failed!").build()))
                        .build())
                        .when(s3Client).putObject(
                                argThat(new SdkRequestMatcher<>(PutObjectRequest.builder(),
                                        builder -> builder.build().bucket().equals("pics") &&
                                                builder.build().key().equals(failedUploadFile.getOriginalFilename()) &&
                                                builder.build().contentType().equals(failedUploadFile.getContentType()) &&
                                                builder.build().acl().equals(ObjectCannedACL.PUBLIC_READ))),
                                argThat(new MultipartMatcher(failedUploadFile)));
                doReturn(PutObjectResponse.builder()
                        .applyMutation(m -> m.sdkHttpResponse(SdkHttpResponse.builder().statusCode(500).build()))
                        .build())
                        .when(s3Client).putObject(
                                argThat(new SdkRequestMatcher<>(PutObjectRequest.builder(),
                                        builder -> builder.build().bucket().equals("pics") &&
                                                builder.build().key().equals(failedUploadNoMessageFile.getOriginalFilename()) &&
                                                builder.build().contentType().equals(failedUploadNoMessageFile.getContentType()) &&
                                                builder.build().acl().equals(ObjectCannedACL.PUBLIC_READ))),
                                argThat(new MultipartMatcher(failedUploadNoMessageFile)));
                doReturn(PutObjectResponse.builder()
                        .applyMutation(m -> m.sdkHttpResponse(SdkHttpResponse.builder().statusCode(500).build()))
                        .build())
                        .when(s3Client).putObject(
                                argThat(new SdkRequestMatcher<>(PutObjectRequest.builder(),
                                        builder -> builder.build().bucket().equals("pics") &&
                                                builder.build().key().equals(ioExceptionFile.getOriginalFilename()) &&
                                                builder.build().contentType().equals(ioExceptionFile.getContentType()) &&
                                                builder.build().acl().equals(ObjectCannedACL.PUBLIC_READ))),
                                argThat(new MultipartMatcher(ioExceptionFile)));

                mvc.perform(multipart("/content/image/add")
                                .file(file)
                                .file(file2)
                                .file(existingFile)
                                .file(failedUploadFile)
                                .file(failedUploadNoMessageFile)
                                .file(ioExceptionFile)
                                .with(authentication(getOauthAuthenticationFor(createOAuth2User("zrbrown", "Zack Brown", "all-perms"))))
                                .with(csrf()))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(content().json("{\"successful\":[{\"filename\":\"hello.txt\",\"result\":\"https://pics.s3.us-west-2.amazonaws.com/hello.txt\"},{\"filename\":\"hello2.txt\",\"result\":\"https://pics.s3.us-west-2.amazonaws.com/hello2.txt\"}],\"failed\":[{\"filename\":\"hello3.txt\",\"result\":\"File hello3.txt already exists\"},{\"filename\":\"hello4.txt\",\"result\":\"500 It failed!\"},{\"filename\":\"hello5.txt\",\"result\":\"500 No error message\"},{\"filename\":\"hello6.txt\",\"result\":\"File hello6.txt could not be read. Try Again.\"}]}"));
            }
        }
    }

    @DisplayName("Deleting an image")
    @Nested
    class Delete {

        @DisplayName("DELETE /content/image/delete/deleteMe.jpg")
        @Test
        void deleteImage() throws Exception {
            doReturn(DeleteObjectResponse.builder().applyMutation(m -> m.requestCharged(RequestCharged.REQUESTER)).build())
                    .when(s3Client).deleteObject(argThat(new SdkRequestMatcher<>(DeleteObjectRequest.builder(),
                            builder -> builder.build().bucket().equals("pics") && builder.build().key().equals("deleteMe.jpg"))));

            mvc.perform(delete("/content/image/delete/deleteMe.jpg")
                            .with(authentication(getOauthAuthenticationFor(createOAuth2User("zrbrown", "Zack Brown", "all-perms"))))
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.TEXT_PLAIN))
                    .andExpect(content().encoding(StandardCharsets.UTF_8.name()))
                    .andExpect(content().string(RequestCharged.REQUESTER.toString()));
        }
    }

    public static Authentication getOauthAuthenticationFor(OAuth2User principal) {
        return new OAuth2AuthenticationToken(principal, principal.getAuthorities(), "github");
    }

    public static OAuth2User createOAuth2User(String subject, String name, String login) {
        Map<String, Object> authorityAttributes = new HashMap<>();
        authorityAttributes.put("key", "value");

        GrantedAuthority authority = new OAuth2UserAuthority(authorityAttributes);

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("sub", subject);
        attributes.put("name", name);
        attributes.put("login", login);

        return new DefaultOAuth2User(List.of(authority), attributes, "sub");
    }

    static class SdkRequestMatcher<T extends SdkRequest.Builder> implements ArgumentMatcher<Consumer<T>> {

        private final T requestBuilder;
        private final Predicate<T> assertions;

        public SdkRequestMatcher(T requestBuilder, Predicate<T> assertions) {
            this.requestBuilder = requestBuilder;
            this.assertions = assertions;
        }

        @Override
        public boolean matches(Consumer<T> argument) {
            argument.accept(requestBuilder);
            return assertions.test(requestBuilder);
        }
    }

    static class MultipartMatcher implements ArgumentMatcher<RequestBody> {

        private final MultipartFile file;

        public MultipartMatcher(MultipartFile file) {
            this.file = file;
        }

        @Override
        public boolean matches(RequestBody argument) {
            try {
                RequestBody fileBody = RequestBody.fromInputStream(file.getInputStream(), file.getSize());
                try (var argStream = argument.contentStreamProvider().newStream();
                     var fileStream = fileBody.contentStreamProvider().newStream()) {
                    return Arrays.equals(argStream.readAllBytes(), fileStream.readAllBytes());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
