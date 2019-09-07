package net.eightlives.mindy.controller;

import net.eightlives.mindy.config.BaseConfig;
import net.eightlives.mindy.dao.model.Post;
import net.eightlives.mindy.model.FormBlogPost;
import net.eightlives.mindy.model.FormBlogPostUpdate;
import net.eightlives.mindy.service.PostService;
import net.eightlives.mindy.service.PostUpdateService;
import net.eightlives.mindy.service.TagService;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Controller
@RequestMapping("/blog")
public class BlogController {

    private final BaseConfig config;
    private final PostService postService;
    private final PostUpdateService postUpdateService;
    private final TagService tagService;

    public BlogController(BaseConfig config, PostService postService, PostUpdateService postUpdateService,
                          TagService tagService) {
        this.config = config;
        this.postService = postService;
        this.postUpdateService = postUpdateService;
        this.tagService = tagService;
    }

    @GetMapping
    public String latestBlog(Model model) {
        Optional<Post> latestPost = postService.getLatestPost();
        latestPost.ifPresent(post -> applyPostToModel(post, model, true, false));

        return "blog";
    }

    @GetMapping("/{postUrlName}")
    public String blog(@PathVariable String postUrlName, Model model) {
        Optional<Post> requestedPost = postService.getPostByUrlName(postUrlName);

        if (requestedPost.isPresent()) {
            applyPostToModel(requestedPost.get(), model, true, true);
            return "blog";
        }

        return "redirect:/blog";
    }

    private void applyPostToModel(Post requestedPost, Model model, boolean showPreviousButton,
                                  boolean showNextButton) {
        model.addAttribute("postUpdates", postUpdateService.getFormattedPostUpdates(requestedPost));

        Set<String> tags = tagService.getTags(requestedPost);
        renderPost(model, requestedPost, tags);

        if (showPreviousButton) {
            Optional<Post> previousPost = postService.getPreviousPost(requestedPost);
            model.addAttribute("showPrevious", previousPost.isPresent());
            previousPost.ifPresent(p -> model.addAttribute("previousPost", p.getUrlName()));
        } else {
            model.addAttribute("showPrevious", false);
        }

        if (showNextButton) {
            Optional<Post> nextPost = postService.getNextPost(requestedPost);
            model.addAttribute("showNext", nextPost.isPresent());
            nextPost.ifPresent(p -> model.addAttribute("nextPost", p.getUrlName()));
        } else {
            model.addAttribute("showNext", false);
        }
    }

    private void renderPost(Model model, Post post, Set<String> tags) {
        model.addAttribute("postTitle", post.getTitle());
        model.addAttribute("postDate", post.getCreatedDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE));

        Parser parser = Parser.builder().build();
        Node document = parser.parse(post.getContent());
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        String renderedContent = renderer.render(document);
        model.addAttribute("postContent", renderedContent);

        model.addAttribute("tags", tags);
    }

    @GetMapping("/{postUrlName}/edit")
    @PreAuthorize("@baseConfig.authorizedUsers.contains(authentication.userAuthentication.principal)")
    public String editPost(@PathVariable String postUrlName, Model model) {
        Optional<Post> post = postService.getPostByUrlName(postUrlName);

        if (!post.isPresent()) {
            return "redirect:/blog";
        }

        model.addAttribute("postTitle", post.get().getTitle());
        model.addAttribute("postContent", post.get().getContent());
        model.addAttribute("submitPath", "/blog/" + postUrlName + "/edit");
        model.addAttribute("ajaxBaseUrl", config.getUrl());

        Set<String> tags =  tagService.getTags(post.get());
        model.addAttribute("tags", tags);

        return "admin/blog_edit";
    }

    @PostMapping("/{postUrlName}/edit")
    @PreAuthorize("@baseConfig.authorizedUsers.contains(authentication.userAuthentication.principal)")
    public String submitPostEdit(@PathVariable String postUrlName, FormBlogPost blogPost) {
        Optional<Post> postOptional = postService.getPostByUrlName(postUrlName);

        if (!postOptional.isPresent()) {
            return "redirect:/blog/{postUrlName}";
        }

        postService.editPost(postOptional.get(), blogPost.getPostTitle(), blogPost.getPostContent(),
                blogPost.getAddedTags());

        return "redirect:/blog/{postUrlName}";
    }

    @GetMapping("/{postUrlName}/update")
    @PreAuthorize("@baseConfig.authorizedUsers.contains(authentication.userAuthentication.principal)")
    public String updatePost(@PathVariable String postUrlName, Model model) {
        Optional<Post> post = postService.getPostByUrlName(postUrlName);

        if (!post.isPresent()) {
            return "redirect:/blog";
        }

        Set<String> tags = tagService.getTags(post.get());

        renderPost(model, post.get(), tags);

        model.addAttribute("submitPath", "/blog/" + postUrlName + "/update");

        return "admin/blog_update";
    }

    @PostMapping("/{postUrlName}/update")
    @PreAuthorize("@baseConfig.authorizedUsers.contains(authentication.userAuthentication.principal)")
    public String submitPostUpdate(@PathVariable String postUrlName, FormBlogPostUpdate blogPostUpdate) {
        Optional<Post> postOptional = postService.getPostByUrlName(postUrlName);

        if (!postOptional.isPresent()) {
            return "redirect:/blog/{postUrlName}";
        }

        postUpdateService.addPostUpdate(postOptional.get(), blogPostUpdate.getPostContent(), LocalDateTime.now());

        return "redirect:/blog/{postUrlName}";
    }

    @GetMapping("/add")
    @PreAuthorize("@baseConfig.authorizedUsers.contains(authentication.userAuthentication.principal)")
    public String addPost(Model model) {
        model.addAttribute("submitPath", "/blog/add");
        model.addAttribute("ajaxBaseUrl", config.getUrl());

        return "admin/blog_edit";
    }

    // TODO use RETHROW in production
    @PostMapping("/add")
    @PreAuthorize("@baseConfig.authorizedUsers.contains(authentication.userAuthentication.principal)")
    public String submitPost(FormBlogPost blogPost) {
        postService.addPost(UUID.randomUUID(), blogPost.getPostTitle(), blogPost.getPostContent(), LocalDateTime.now(),
                blogPost.getAddedTags());

        return "redirect:/blog";
    }
}
