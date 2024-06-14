<#import "../common/page.ftlh" as p>

<@p.page title="Update Post"
stylesheets=["https://cdn.jsdelivr.net/simplemde/latest/simplemde.min.css"]
scripts=["https://cdn.jsdelivr.net/simplemde/latest/simplemde.min.js"]>
<div class="content-container">
    <div class="post-title">${postTitle}</div>
    <span class="post-date">${postDate}</span>
    <br>
    <br>
    <form action="${submitPath}" method="post">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

        <textarea title="Post content" id="editor" name="postContent"></textarea>
        <br>
        <div class="post-action-container">
            <button type="submit" class="post-action">Update</button>
        </div>
    </form>
    <p class="post-content">${postContent?no_esc}</p>
    <#list tags>
        <div class="tag-container">
            <#items as tag>
                <div class="tag-wrapper"><a href="" class="tag">${tag}</a>
                </div>
            </#items>
        </div>
    </#list>
</div>

<script>
    var simplemde = new SimpleMDE({element: document.getElementById("editor")});
</script>
</@p.page>