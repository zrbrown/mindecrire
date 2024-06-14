package net.eightlives.mindecrire.controller;

import net.eightlives.mindecrire.dao.AuthorDetailsRepository;
import net.eightlives.mindecrire.dao.PostRepository;
import net.eightlives.mindecrire.dao.PostUpdateRepository;
import net.eightlives.mindecrire.dao.TagRepository;
import net.eightlives.mindecrire.dao.model.AuthorDetails;
import net.eightlives.mindecrire.dao.model.Post;
import net.eightlives.mindecrire.dao.model.PostUpdate;
import net.eightlives.mindecrire.dao.model.Tag;
import net.eightlives.mindecrire.model.FormattedPostUpdate;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.comparator.ComparatorMatcherBuilder.comparedBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class BlogControllerTest extends ControllerTest {

    @Autowired
    AuthorDetailsRepository authorDetailsRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    PostUpdateRepository postUpdateRepository;
    @Autowired
    TagRepository tagRepository;

    @MockBean
    Clock clock;

    private static final LocalDateTime LAST_YEAR = LocalDateTime.of(2023, Month.FEBRUARY, 3, 6, 45);
    private static final LocalDateTime YESTERDAY = LocalDateTime.of(2024, Month.FEBRUARY, 2, 6, 45);
    private static final LocalDateTime TODAY = LocalDateTime.of(2024, Month.FEBRUARY, 3, 6, 45);
    private static final LocalDateTime NOW = LocalDateTime.of(2024, Month.FEBRUARY, 3, 8, 59, 20);

    AuthorDetails author;

    @BeforeEach
    void beforeEach() {
        author = authorDetailsRepository.save(new AuthorDetails(UUID.randomUUID(), "github-zrbrown", "Zack Brown"));

        when(clock.instant()).thenReturn(NOW.atZone(ZoneId.of("-05:00")).toInstant());
        when(clock.getZone()).thenReturn(ZoneId.of("-05:00"));
    }

    @AfterEach
    void afterEach() {
        tagRepository.deleteAllInBatch();
        postUpdateRepository.deleteAllInBatch();
        postRepository.deleteAllInBatch();
        authorDetailsRepository.deleteAllInBatch();
    }

    @DisplayName("Retrieving Posts")
    @Nested
    class GetPosts {

        @DisplayName("when zero posts exist")
        @Nested
        class PostMissing {

            @DisplayName("GET /blog")
            @Test
            void latestPostMissing() throws Exception {
                mvc.perform(get("/blog"))
                        .andExpect(status().isOk())
                        .andExpect(view().name("blog_page"));
            }

            @DisplayName("GET /blog/test-post")
            @Test
            void postMissing() throws Exception {
                mvc.perform(get("/blog/test-post"))
                        .andExpect(status().isFound())
                        .andExpect(view().name("redirect:/blog"));
            }
        }

        @DisplayName("when at least one post exists")
        @Nested
        class PostExists {

            Post todayPost;

            @BeforeEach
            void beforeEach() {
                todayPost = postRepository.save(new Post(UUID.randomUUID(), "test-post", "Test Post", "nothing here", TODAY, Set.of(), author));
            }

            @DisplayName("GET /blog")
            @Test
            void latestPost() throws Exception {
                mvc.perform(get("/blog"))
                        .andExpect(status().isFound())
                        .andExpect(view().name("redirect:/blog/test-post"));
            }

            @DisplayName("GET /blog/test-post")
            @Test
            void post() throws Exception {
                mvc.perform(get("/blog/test-post"))
                        .andExpect(status().isOk())
                        .andExpect(view().name("blog_page"))
                        .andExpect(model().attribute("postUpdates", hasSize(0)))
                        .andExpect(model().attribute("postTitle", "Test Post"))
                        .andExpect(model().attribute("postDate", DateTimeFormatter.ISO_LOCAL_DATE.withZone(ZoneId.of("UTC")).format(TODAY)))
                        .andExpect(model().attribute("postAuthor", "Zack Brown"))
                        .andExpect(model().attribute("postContent", "<p class=\"mindecrire-md-paragraph\">nothing here</p>\n"))
                        .andExpect(model().attribute("tags", hasSize(0)))
                        .andExpect(model().attribute("showPrevious", false))
                        .andExpect(model().attribute("showNext", false));
            }

            @DisplayName("when other posts exist")
            @ParameterizedTest(name = "GET /blog/{0} (previous: {1}) (next: {2})")
            @CsvSource({"test-post,true,false", "test-post-kinda-old,true,true", "test-post-old,false,true"})
            void postWithOtherPosts(String urlName, boolean showPrevious, boolean showNext) throws Exception {
                postRepository.save(new Post(UUID.randomUUID(), "test-post-old", "Test Post Old", "A long long time ago...", LAST_YEAR, Set.of(), author));
                postRepository.save(new Post(UUID.randomUUID(), "test-post-kinda-old", "Test Post Kinda Old", "Today, it was sunny.", YESTERDAY, Set.of(), author));

                mvc.perform(get("/blog/" + urlName))
                        .andExpect(status().isOk())
                        .andExpect(view().name("blog_page"))
                        .andExpect(model().attribute("showPrevious", showPrevious))
                        .andExpect(model().attribute("showNext", showNext));
            }

            @DisplayName("GET /blog/test-post when post has tags")
            @Test
            void postWithTags() throws Exception {
                List<Tag> tags = tagRepository.saveAll(List.of(
                        createTag("cool", todayPost),
                        createTag("Programming", todayPost)));

                mvc.perform(get("/blog/test-post"))
                        .andExpect(status().isOk())
                        .andExpect(view().name("blog_page"))
                        .andExpect(model().attribute("postUpdates", hasSize(0)))
                        .andExpect(model().attribute("postTitle", "Test Post"))
                        .andExpect(model().attribute("postDate", DateTimeFormatter.ISO_LOCAL_DATE.withZone(ZoneId.of("UTC")).format(TODAY)))
                        .andExpect(model().attribute("postAuthor", "Zack Brown"))
                        .andExpect(model().attribute("postContent", "<p class=\"mindecrire-md-paragraph\">nothing here</p>\n"))
                        .andExpect(model().attribute("tags", containsInAnyOrder(tags.stream().map(Tag::getName).toArray())))
                        .andExpect(model().attribute("showPrevious", false))
                        .andExpect(model().attribute("showNext", false));
            }

            @DisplayName("GET /blog/test-post when post has updates")
            @Test
            void postWithUpdates() throws Exception {
                List<PostUpdate> postUpdates = List.of(
                        createPostUpdate("UPDATE: stuff", todayPost.getCreatedDateTime().plusHours(1), todayPost),
                        createPostUpdate("Updating with more", todayPost.getCreatedDateTime().plusHours(2), todayPost),
                        createPostUpdate("Correction!", todayPost.getCreatedDateTime().plusHours(3), todayPost)
                );
                postUpdateRepository.saveAll(postUpdates);

                List<FormattedPostUpdate> expectedUpdates = List.of(
                        new FormattedPostUpdate("<p>Correction!</p>\n", DateTimeFormatter.ISO_LOCAL_DATE.format(postUpdates.get(2).getUpdatedDateTime())),
                        new FormattedPostUpdate("<p>Updating with more</p>\n", DateTimeFormatter.ISO_LOCAL_DATE.format(postUpdates.get(1).getUpdatedDateTime())),
                        new FormattedPostUpdate("<p>UPDATE: stuff</p>\n", DateTimeFormatter.ISO_LOCAL_DATE.format(postUpdates.get(0).getUpdatedDateTime()))
                );

                mvc.perform(get("/blog/test-post"))
                        .andExpect(status().isOk())
                        .andExpect(view().name("blog_page"))
                        .andExpect(model().attribute("postUpdates", contains(
                                comparedBy(Comparator.comparing(FormattedPostUpdate::getContent).thenComparing(FormattedPostUpdate::getDate)).comparesEqualTo(expectedUpdates.get(0)),
                                comparedBy(Comparator.comparing(FormattedPostUpdate::getContent).thenComparing(FormattedPostUpdate::getDate)).comparesEqualTo(expectedUpdates.get(1)),
                                comparedBy(Comparator.comparing(FormattedPostUpdate::getContent).thenComparing(FormattedPostUpdate::getDate)).comparesEqualTo(expectedUpdates.get(2))
                        )))
                        .andExpect(model().attribute("postTitle", "Test Post"))
                        .andExpect(model().attribute("postDate", DateTimeFormatter.ISO_LOCAL_DATE.withZone(ZoneId.of("UTC")).format(TODAY)))
                        .andExpect(model().attribute("postAuthor", "Zack Brown"))
                        .andExpect(model().attribute("postContent", "<p class=\"mindecrire-md-paragraph\">nothing here</p>\n"))
                        .andExpect(model().attribute("tags", hasSize(0)))
                        .andExpect(model().attribute("showPrevious", false))
                        .andExpect(model().attribute("showNext", false));
            }
        }
    }

    @DisplayName("Adding a post")
    @Nested
    class AddPost {

        @DisplayName("unauthenticated")
        @Nested
        class Unauthenticated {

            @DisplayName("GET /blog/add")
            @Test
            void addPost() throws Exception {
                mvc.perform(get("/blog/add"))
                        .andExpect(status().isFound())
                        .andExpect(header().string("Location", "http://localhost/oauth2/authorization/github"));
            }

            @DisplayName("POST /blog/add")
            @Test
            void submitPost() throws Exception {
                mvc.perform(post("/blog/add")
                                .with(csrf())
                                .param("postTitle", "New Test Post!")
                                .param("postContent", "Stuff about stuff."))
                        .andExpect(status().isFound())
                        .andExpect(header().string("Location", "http://localhost/oauth2/authorization/github"));
            }
        }

        @DisplayName("unauthorized (does not have POST_ADD permission)")
        @Nested
        class Unauthorized {

            @DisplayName("GET /blog/add")
            @Test
            void addPost() throws Exception {
                mvc.perform(get("/blog/add")
                                .with(authentication(getOauthAuthenticationFor(createOAuth2User("zrbrown", "Zack Brown", "no-add")))))
                        .andExpect(status().isForbidden());
            }

            @DisplayName("POST /blog/add")
            @Test
            void submitPost() throws Exception {
                mvc.perform(post("/blog/add")
                                .param("postTitle", "Edited Title")
                                .param("postContent", "Edited content.")
                                .with(authentication(getOauthAuthenticationFor(createOAuth2User("zrbrown", "Zack Brown", "no-add"))))
                                .with(csrf()))
                        .andExpect(status().isForbidden());
            }
        }

        @DisplayName("authenticated and authorized")
        @Nested
        class Authorized {

            @DisplayName("GET /blog/add")
            @Test
            void addPost() throws Exception {
                mvc.perform(get("/blog/add")
                                .with(authentication(getOauthAuthenticationFor(createOAuth2User("zrbrown", "Zack Brown", "add"))))
                                .with(csrf()))
                        .andExpect(status().isOk())
                        .andExpect(view().name("blog_actions/blog_edit"))
                        .andExpect(model().attribute("submitPath", "/blog/add"))
                        .andExpect(model().attribute("ajaxBaseUrl", "https://myblog.com"));
            }

            @DisplayName("POST /blog/add")
            @Test
            void submitPost() throws Exception {
                mvc.perform(post("/blog/add")
                                .with(authentication(getOauthAuthenticationFor(createOAuth2User("zrbrown", "Zack Brown", "add"))))
                                .with(csrf())
                                .param("postTitle", "New Test Post!")
                                .param("postContent", "Stuff about stuff."))
                        .andExpect(status().isFound())
                        .andExpect(view().name("redirect:/blog"));

                Post post = postRepository.getByUrlName("New-Test-Post!").orElseThrow();
                assertEquals("New-Test-Post!", post.getUrlName());
                assertEquals("New Test Post!", post.getTitle());
                assertEquals("Stuff about stuff.", post.getContent());
                assertEquals(NOW, post.getCreatedDateTime());
            }

            @DisplayName("POST /blog/add when adding tags")
            @Test
            void submitPostWithTags() throws Exception {
                tagRepository.save(createTag("games"));

                mvc.perform(post("/blog/add")
                                .with(authentication(getOauthAuthenticationFor(createOAuth2User("zrbrown", "Zack Brown", "add"))))
                                .with(csrf())
                                .param("postTitle", "New Test Post!")
                                .param("postContent", "Stuff about stuff.")
                                .param("addedTags", "food", "games"))
                        .andExpect(status().isFound())
                        .andExpect(view().name("redirect:/blog"));

                Post post = postRepository.getByUrlName("New-Test-Post!").orElseThrow();
                assertEquals("New-Test-Post!", post.getUrlName());
                assertEquals("New Test Post!", post.getTitle());
                assertEquals("Stuff about stuff.", post.getContent());

                List<String> tags = tagRepository.findAll().stream().map(Tag::getName).sorted(Comparator.naturalOrder()).collect(Collectors.toList());
                assertEquals(List.of("food", "games"), tags);
            }

            @DisplayName("POST /blog/add when edited post title already exists")
            @Test
            void submitPostDuplicatePost() throws Exception {
                postRepository.save(new Post(UUID.randomUUID(), "New-Test-Post!", "Some Other Title",
                        "Some other content.", LAST_YEAR, Set.of(), author));

                mvc.perform(post("/blog/add")
                                .with(authentication(getOauthAuthenticationFor(createOAuth2User("zrbrown", "Zack Brown", "add"))))
                                .with(csrf())
                                .param("postTitle", "New Test Post!")
                                .param("postContent", "Stuff about stuff.")
                                .param("addedTags", "food", "games"))
                        .andExpect(status().isConflict())
                        .andExpect(view().name("blog_actions/blog_edit"))
                        .andExpect(model().attribute("postTitle", "New Test Post!"))
                        .andExpect(model().attribute("postContent", "Stuff about stuff."))
                        .andExpect(model().attribute("tags", List.of("food", "games")))
                        .andExpect(model().attribute("resubmit", true))
                        .andExpect(model().attribute("submitPath", "/blog/add"))
                        .andExpect(model().attribute("ajaxBaseUrl", "https://myblog.com"))
                        .andExpect(model().attribute("validationMessage", "A post with this title already exists. Use a different title."));
            }

            @DisplayName("POST /blog/add when title is blank")
            @Test
            void submitPostBlankTitle() throws Exception {
                mvc.perform(post("/blog/add")
                                .with(authentication(getOauthAuthenticationFor(createOAuth2User("zrbrown", "Zack Brown", "add"))))
                                .with(csrf())
                                .param("postTitle", "")
                                .param("postContent", "Stuff about stuff."))
                        .andExpect(status().isBadRequest());
            }

            @DisplayName("POST /blog/add when content is blank")
            @Test
            void submitPostBlankContent() throws Exception {
                mvc.perform(post("/blog/add")
                                .with(authentication(getOauthAuthenticationFor(createOAuth2User("zrbrown", "Zack Brown", "add"))))
                                .with(csrf())
                                .param("postTitle", "New Test Post!")
                                .param("postContent", ""))
                        .andExpect(status().isBadRequest());
            }

            @DisplayName("POST /blog/add when author does not exist")
            @Test
            void submitPostNewAuthor() throws Exception {
                mvc.perform(post("/blog/add")
                                .with(authentication(getOauthAuthenticationFor(createOAuth2User("otherzack", "Zack Notbrown", "add"))))
                                .with(csrf())
                                .param("postTitle", "New Test Post!")
                                .param("postContent", "Stuff about stuff."))
                        .andExpect(status().isFound())
                        .andExpect(view().name("redirect:/blog"));

                Post post = postRepository.getByUrlName("New-Test-Post!").orElseThrow();
                assertEquals("New-Test-Post!", post.getUrlName());
                assertEquals("New Test Post!", post.getTitle());
                assertEquals("Stuff about stuff.", post.getContent());

                AuthorDetails author = authorDetailsRepository.findByAuthor("github-otherzack").orElseThrow();
                assertEquals("Zack Notbrown", author.getDisplayName());
            }

            @DisplayName("POST /blog/add when author does not exist and OAuth token does not have a name")
            @Test
            void submitPostNewAuthorMissingName() throws Exception {
                mvc.perform(post("/blog/add")
                                .with(authentication(getOauthAuthenticationFor(createOAuth2User("otherzack", null, "add"))))
                                .with(csrf())
                                .param("postTitle", "New Test Post!")
                                .param("postContent", "Stuff about stuff."))
                        .andExpect(status().isFound())
                        .andExpect(view().name("redirect:/blog"));

                Post post = postRepository.getByUrlName("New-Test-Post!").orElseThrow();
                assertEquals("New-Test-Post!", post.getUrlName());
                assertEquals("New Test Post!", post.getTitle());
                assertEquals("Stuff about stuff.", post.getContent());

                AuthorDetails author = authorDetailsRepository.findByAuthor("github-otherzack").orElseThrow();
                assertEquals("add", author.getDisplayName());
            }
        }
    }

    @Nested
    @DisplayName("Editing a post")
    class EditPost {

        AuthorDetails author;
        Post todayPost;

        @BeforeEach
        void beforeEach() {
            author = authorDetailsRepository.save(new AuthorDetails(UUID.randomUUID(), "edit", "Zack Brown"));
            todayPost = postRepository.save(new Post(UUID.randomUUID(), "test-post", "Test Post", "nothing here", TODAY, Set.of(), author));
        }

        @DisplayName("unauthenticated")
        @Nested
        class Unauthenticated {

            @DisplayName("GET /blog/test-post/edit")
            @Test
            void editPost() throws Exception {
                mvc.perform(get("/blog/" + todayPost.getUrlName() + "/edit"))
                        .andExpect(status().isFound());
            }

            @DisplayName("POST /blog/test-post/edit")
            @Test
            void submitPostEdit() throws Exception {
                mvc.perform(post("/blog/" + todayPost.getUrlName() + "/edit")
                                .with(csrf()))
                        .andExpect(status().isFound());
            }
        }

        @DisplayName("unauthorized")
        @Nested
        class Unauthorized {

            @DisplayName("GET /blog/test-post/edit")
            @Test
            void editPost() throws Exception {
                mvc.perform(get("/blog/" + todayPost.getUrlName() + "/edit")
                                .with(authentication(getOauthAuthenticationFor(createOAuth2User("zrbrown", "Zack Brown", "no-edit-or-post-admin")))))
                        .andExpect(status().isForbidden());
            }

            @DisplayName("POST /blog/test-post/edit")
            @Test
            void submitPostEdit() throws Exception {
                mvc.perform(post("/blog/" + todayPost.getUrlName() + "/edit")
                                .param("postTitle", "Edited Title")
                                .param("postContent", "Edited content.")
                                .with(authentication(getOauthAuthenticationFor(createOAuth2User("zrbrown", "Zack Brown", "no-edit-or-post-admin"))))
                                .with(csrf()))
                        .andExpect(status().isForbidden());
            }
        }

        @DisplayName("authenticated and authorized")
        @Nested
        class Authorized {

            @DisplayName("GET /blog/test-post/edit")
            @ParameterizedTest(name = "with authorization {0}")
            @CsvSource({"edit", "post-admin"})
            void editPost(String login) throws Exception {
                mvc.perform(get("/blog/" + todayPost.getUrlName() + "/edit")
                                .with(authentication(getOauthAuthenticationFor(createOAuth2User("zrbrown", "Zack Brown", login)))))
                        .andExpect(status().isOk())
                        .andExpect(model().attribute("postTitle", todayPost.getTitle()))
                        .andExpect(model().attribute("postContent", todayPost.getContent()))
                        .andExpect(model().attribute("submitPath", "/blog/" + todayPost.getUrlName() + "/edit"))
                        .andExpect(model().attribute("ajaxBaseUrl", "https://myblog.com"))
                        .andExpect(model().attribute("tags", hasSize(0)))
                        .andExpect(view().name("blog_actions/blog_edit"));
            }

            @DisplayName("GET /blog/test-post/edit when post has tags")
            @ParameterizedTest(name = "with authorization {0}")
            @CsvSource({"edit", "post-admin"})
            void editPostWithTags(String login) throws Exception {
                List<Tag> tags = tagRepository.saveAll(List.of(createTag("programming", todayPost), createTag("Java", todayPost)));

                mvc.perform(get("/blog/" + todayPost.getUrlName() + "/edit")
                                .with(authentication(getOauthAuthenticationFor(createOAuth2User("zrbrown", "Zack Brown", login)))))
                        .andExpect(status().isOk())
                        .andExpect(model().attribute("postTitle", todayPost.getTitle()))
                        .andExpect(model().attribute("postContent", todayPost.getContent()))
                        .andExpect(model().attribute("submitPath", "/blog/" + todayPost.getUrlName() + "/edit"))
                        .andExpect(model().attribute("ajaxBaseUrl", "https://myblog.com"))
                        .andExpect(model().attribute("tags", containsInAnyOrder(tags.stream().map(Tag::getName).toArray())))
                        .andExpect(view().name("blog_actions/blog_edit"));
            }

            @DisplayName("GET /blog/not-real/edit when post is missing with authorization edit")
            @Test
            void editPostMissing() throws Exception {
                mvc.perform(get("/blog/not-real/edit")
                                .with(authentication(getOauthAuthenticationFor(createOAuth2User("zrbrown", "Zack Brown", "edit")))))
                        .andExpect(status().isForbidden());
            }

            @DisplayName("GET /blog/not-real/edit when post is missing with authorization post-admin")
            @Test
            void editPostMissingAdmin() throws Exception {
                mvc.perform(get("/blog/not-real/edit")
                                .with(authentication(getOauthAuthenticationFor(createOAuth2User("zrbrown", "Zack Brown", "post-admin")))))
                        .andExpect(status().isFound())
                        .andExpect(view().name("redirect:/blog"));
            }

            @DisplayName("POST /blog/test-post/edit")
            @ParameterizedTest(name = "with authorization {0}")
            @CsvSource({"edit", "post-admin"})
            void submitPostEdit(String login) throws Exception {
                tagRepository.save(createTag("recipes", todayPost));

                mvc.perform(post("/blog/" + todayPost.getUrlName() + "/edit")
                                .with(authentication(getOauthAuthenticationFor(createOAuth2User("zrbrown", "Zack Brown", login))))
                                .with(csrf())
                                .param("postTitle", "Edited Title")
                                .param("postContent", "Edited content."))
                        .andExpect(status().isFound())
                        .andExpect(view().name("redirect:/blog/" + todayPost.getUrlName()));

                Post post = postRepository.findById(todayPost.getId()).orElseThrow();
                assertEquals(todayPost.getUrlName(), post.getUrlName());
                assertEquals("Edited Title", post.getTitle());
                assertEquals("Edited content.", post.getContent());

                Set<Tag> tags = tagRepository.getAllByPostsIn(Set.of(post));
                assertEquals(1, tags.size());
                assertTrue(tags.stream().map(Tag::getName).collect(Collectors.toList()).contains("recipes"));
            }

            @DisplayName("POST /blog/test-post/edit when adding tags")
            @ParameterizedTest(name = "with authorization {0}")
            @CsvSource({"edit", "post-admin"})
            void submitPostEditWithTags(String login) throws Exception {
                tagRepository.save(createTag("recipes", todayPost));
                tagRepository.save(createTag("games"));

                mvc.perform(post("/blog/" + todayPost.getUrlName() + "/edit")
                                .with(authentication(getOauthAuthenticationFor(createOAuth2User("zrbrown", "Zack Brown", login))))
                                .with(csrf())
                                .param("postTitle", "Edited Title")
                                .param("postContent", "Edited content.")
                                .param("addedTags", "food", "games"))
                        .andExpect(status().isFound())
                        .andExpect(view().name("redirect:/blog/" + todayPost.getUrlName()));

                Post post = postRepository.findById(todayPost.getId()).orElseThrow();
                assertEquals(todayPost.getUrlName(), post.getUrlName());
                assertEquals("Edited Title", post.getTitle());
                assertEquals("Edited content.", post.getContent());

                Set<Tag> tags = tagRepository.getAllByPostsIn(Set.of(post));
                assertEquals(3, tags.size());
                assertTrue(tags.stream().map(Tag::getName).collect(Collectors.toList()).contains("recipes"));
                assertTrue(tags.stream().map(Tag::getName).collect(Collectors.toList()).contains("food"));
                assertTrue(tags.stream().map(Tag::getName).collect(Collectors.toList()).contains("games"));
            }

            @DisplayName("POST /blog/test-post/edit with blank title")
            @ParameterizedTest(name = "with authorization {0}")
            @CsvSource({"edit", "post-admin"})
            void submitPostEditBlankTitle(String login) throws Exception {
                tagRepository.save(createTag("recipes", todayPost));

                mvc.perform(post("/blog/" + todayPost.getUrlName() + "/edit")
                                .with(authentication(getOauthAuthenticationFor(createOAuth2User("zrbrown", "Zack Brown", login))))
                                .with(csrf())
                                .param("postTitle", "")
                                .param("postContent", "Edited content."))
                        .andExpect(status().isBadRequest());
            }

            @DisplayName("POST /blog/test-post/edit with blank content")
            @ParameterizedTest(name = "with authorization {0}")
            @CsvSource({"edit", "post-admin"})
            void submitPostEditBlankContent(String login) throws Exception {
                tagRepository.save(createTag("recipes", todayPost));

                mvc.perform(post("/blog/" + todayPost.getUrlName() + "/edit")
                                .with(authentication(getOauthAuthenticationFor(createOAuth2User("zrbrown", "Zack Brown", login))))
                                .with(csrf())
                                .param("postTitle", "Edited Title")
                                .param("postContent", ""))
                        .andExpect(status().isBadRequest());
            }

            @DisplayName("POST /blog/not-real/edit when post is missing with authorization edit")
            @Test
            void submitPostEditMissing() throws Exception {
                mvc.perform(post("/blog/not-real/edit")
                                .with(authentication(getOauthAuthenticationFor(createOAuth2User("zrbrown", "Zack Brown", "edit"))))
                                .with(csrf())
                                .param("postTitle", "New Test Post!")
                                .param("postContent", "Stuff about stuff."))
                        .andExpect(status().isForbidden());
            }

            @DisplayName("POST /blog/not-real/edit when post is missing with authorization post-admin")
            @Test
            void submitPostEditMissingPostAdmin() throws Exception {
                mvc.perform(post("/blog/not-real/edit")
                                .with(authentication(getOauthAuthenticationFor(createOAuth2User("zrbrown", "Zack Brown", "post-admin"))))
                                .with(csrf())
                                .param("postTitle", "New Test Post!")
                                .param("postContent", "Stuff about stuff."))
                        .andExpect(status().isFound())
                        .andExpect(view().name("redirect:/blog/not-real"));
            }
        }
    }

    @DisplayName("Updating a post")
    @Nested
    class UpdatePost {

        AuthorDetails author;
        Post todayPost;

        @BeforeEach
        void beforeEach() {
            author = authorDetailsRepository.save(new AuthorDetails(UUID.randomUUID(), "update", "Zack Brown"));
            todayPost = postRepository.save(new Post(UUID.randomUUID(), "test-post", "Test Post", "nothing here", TODAY, Set.of(), author));
        }

        @DisplayName("unauthenticated")
        @Nested
        class Unauthenticated {

            @DisplayName("GET /blog/test-post/update")
            @Test
            void updatePost() throws Exception {
                mvc.perform(get("/blog/" + todayPost.getUrlName() + "/update"))
                        .andExpect(status().isFound());
            }

            @DisplayName("POST /blog/test-post/update")
            @Test
            void submitPostUpdate() throws Exception {
                mvc.perform(post("/blog/" + todayPost.getUrlName() + "/update")
                                .with(csrf()))
                        .andExpect(status().isFound());
            }
        }

        @DisplayName("unauthorized")
        @Nested
        class Unauthorized {

            @DisplayName("GET /blog/test-post/update")
            @Test
            void editPost() throws Exception {
                mvc.perform(get("/blog/" + todayPost.getUrlName() + "/update")
                                .with(authentication(getOauthAuthenticationFor(createOAuth2User("zrbrown", "Zack Brown", "no-update-or-post-admin")))))
                        .andExpect(status().isForbidden());
            }

            @DisplayName("POST /blog/test-post/update")
            @Test
            void submitPostEdit() throws Exception {
                mvc.perform(post("/blog/" + todayPost.getUrlName() + "/update")
                                .param("postContent", "Edited content.")
                                .with(authentication(getOauthAuthenticationFor(createOAuth2User("zrbrown", "Zack Brown", "no-update-or-post-admin"))))
                                .with(csrf()))
                        .andExpect(status().isForbidden());
            }
        }

        @DisplayName("authenticated and authorized")
        @Nested
        class Authorized {

            @DisplayName("GET /blog/test-post/update when post is missing with authorization update")
            @Test
            void updatePostMissing() throws Exception {
                mvc.perform(get("/blog/not-real/update")
                                .with(authentication(getOauthAuthenticationFor(createOAuth2User("zrbrown", "Zack Brown", "update")))))
                        .andExpect(status().isForbidden());
            }

            @DisplayName("GET /blog/test-post/update when post is missing with authorization post-admin")
            @Test
            void updatePostMissingAdmin() throws Exception {
                mvc.perform(get("/blog/not-real/update")
                                .with(authentication(getOauthAuthenticationFor(createOAuth2User("zrbrown", "Zack Brown", "post-admin")))))
                        .andExpect(status().isFound())
                        .andExpect(view().name("redirect:/blog"));
            }

            @DisplayName("GET /blog/test-post/update")
            @ParameterizedTest(name = "with authorization {0}")
            @CsvSource({"update", "post-admin"})
            void updatePost(String login) throws Exception {
                mvc.perform(get("/blog/" + todayPost.getUrlName() + "/update")
                                .with(authentication(getOauthAuthenticationFor(createOAuth2User("zrbrown", "Zack Brown", login)))))
                        .andExpect(status().isOk())
                        .andExpect(model().attribute("postTitle", todayPost.getTitle()))
                        .andExpect(model().attribute("postDate", DateTimeFormatter.ISO_LOCAL_DATE.withZone(ZoneId.of("UTC")).format(TODAY)))
                        .andExpect(model().attribute("postAuthor", author.getDisplayName()))
                        .andExpect(model().attribute("postContent", "<p class=\"mindecrire-md-paragraph\">nothing here</p>\n"))
                        .andExpect(model().attribute("tags", hasSize(0)))
                        .andExpect(view().name("blog_actions/blog_update"));
            }

            @DisplayName("GET /blog/test-post/update when post has tags")
            @ParameterizedTest(name = "with authorization {0}")
            @CsvSource({"update", "post-admin"})
            void updatePostWithTags(String login) throws Exception {
                List<Tag> tags = tagRepository.saveAll(List.of(createTag("board games", todayPost), createTag("games", todayPost)));

                mvc.perform(get("/blog/" + todayPost.getUrlName() + "/update")
                                .with(authentication(getOauthAuthenticationFor(createOAuth2User("zrbrown", "Zack Brown", login)))))
                        .andExpect(status().isOk())
                        .andExpect(model().attribute("postTitle", todayPost.getTitle()))
                        .andExpect(model().attribute("postDate", DateTimeFormatter.ISO_LOCAL_DATE.withZone(ZoneId.of("UTC")).format(TODAY)))
                        .andExpect(model().attribute("postAuthor", author.getDisplayName()))
                        .andExpect(model().attribute("postContent", "<p class=\"mindecrire-md-paragraph\">nothing here</p>\n"))
                        .andExpect(model().attribute("tags", containsInAnyOrder(tags.stream().map(Tag::getName).toArray())))
                        .andExpect(model().attribute("submitPath", "/blog/" + todayPost.getUrlName() + "/update"))
                        .andExpect(view().name("blog_actions/blog_update"));
            }

            @DisplayName("POST /blog/test-post/update when post is missing with authorization update")
            @Test
            void submitPostUpdateMissing() throws Exception {
                mvc.perform(post("/blog/not-real/update")
                                .with(authentication(getOauthAuthenticationFor(createOAuth2User("zrbrown", "Zack Brown", "update"))))
                                .with(csrf())
                                .param("postContent", "This is an update to the post!"))
                        .andExpect(status().isForbidden());
            }

            @DisplayName("POST /blog/test-post/update when post is missing with authorization post-admin")
            @Test
            void submitPostUpdateMissingPostAdmin() throws Exception {
                mvc.perform(post("/blog/not-real/update")
                                .with(authentication(getOauthAuthenticationFor(createOAuth2User("zrbrown", "Zack Brown", "post-admin"))))
                                .with(csrf())
                                .param("postContent", "This is an update to the post!"))
                        .andExpect(status().isFound())
                        .andExpect(view().name("redirect:/blog/not-real"));
            }

            @DisplayName("POST /blog/test-post/update")
            @ParameterizedTest(name = "with authorization {0}")
            @CsvSource({"update", "post-admin"})
            void submitPostUpdate(String login) throws Exception {
                mvc.perform(post("/blog/" + todayPost.getUrlName() + "/update")
                                .with(authentication(getOauthAuthenticationFor(createOAuth2User("zrbrown", "Zack Brown", login))))
                                .with(csrf())
                                .param("postContent", "This is an update to the post!"))
                        .andExpect(status().isFound())
                        .andExpect(view().name("redirect:/blog/" + todayPost.getUrlName()));

                List<PostUpdate> updates = postUpdateRepository.findAllByPost(todayPost, Sort.by(Sort.Direction.ASC, "updatedDateTime"));
                assertEquals(1, updates.size());
                assertEquals("This is an update to the post!", updates.get(0).getContent());
                assertEquals(NOW, updates.get(0).getUpdatedDateTime());
            }

            @DisplayName("POST /blog/test-post/update with blank content")
            @ParameterizedTest(name = "with authorization {0}")
            @CsvSource({"update", "post-admin"})
            void submitPostUpdateBlankContent(String login) throws Exception {
                mvc.perform(post("/blog/" + todayPost.getUrlName() + "/update")
                                .with(authentication(getOauthAuthenticationFor(createOAuth2User("zrbrown", "Zack Brown", login))))
                                .with(csrf())
                                .param("postContent", ""))
                        .andExpect(status().isBadRequest());
            }
        }
    }

    private PostUpdate createPostUpdate(String content, LocalDateTime updatedDateTime, Post post) {
        PostUpdate postUpdate = new PostUpdate();
        postUpdate.setId(UUID.randomUUID());
        postUpdate.setContent(content);
        postUpdate.setUpdatedDateTime(updatedDateTime);
        postUpdate.setPost(post);
        return postUpdate;
    }

    private Tag createTag(String name) {
        Tag tag = new Tag();
        tag.setId(UUID.randomUUID());
        tag.setName(name);
        return tag;
    }

    private Tag createTag(String name, Post post) {
        Tag tag = new Tag();
        tag.setId(UUID.randomUUID());
        tag.setName(name);
        tag.setPosts(Set.of(post));
        return tag;
    }
}
