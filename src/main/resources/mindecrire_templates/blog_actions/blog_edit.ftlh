<#import "../common/page.ftlh" as p>

<@p.page title="Add/Edit Post"
stylesheets=["https://cdn.jsdelivr.net/simplemde/latest/simplemde.min.css"]
scripts=["https://cdn.jsdelivr.net/simplemde/latest/simplemde.min.js", "https://unpkg.com/axios/dist/axios.min.js"]>
<div class="content-container">
    <form action="${submitPath}" method="post">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

        <div class="content-title-edit-container">
            <div class="validation-text">${validationMessage!}</div>
            <input title="Post title" type="text" class="content-title-edit" name="postTitle" value="${postTitle!}"/>
        </div>
        <br/>
        <textarea title="Post content" id="editor" name="postContent">${postContent!}</textarea>
        <input id="addImageFileInput" class="file-input" title="Add Images" type="file" name="files[]"
               onchange="handleFileSelect(this, '${_csrf.parameterName}', '${_csrf.headerName}', '${_csrf.token}')"
               multiple/>
        <label for="addImageFileInput">Add Images</label>
        <ul id="uploadedImagesList" title="Uploaded Images">
        </ul>
        <input title="Tag name entry" type="text" class="add-tag-text" name="addTag" id="addTagInput"/>
        <button title="Add tag" type="button" onclick="addTagToTags()">Add tag</button>
        <div class="tag-container" id="tagContainer">
            <#if tags??>
                <#list tags as tag>
                    <div class="tag-wrapper">
                        <span class="tag">${tag}</span><a href="" class="tag tag-remove">&#9447;</a>
                        <#if resubmit??>
                            <input type="hidden" name="addedTags" value="${tag}"/>
                        </#if>
                    </div>
                </#list>
            </#if>
        </div>
        <div class="post-action-container">
            <button type="submit" class="post-action">Post</button>
        </div>
    </form>
</div>

<script>
    let simplemde = new SimpleMDE({element: document.getElementById("editor")});

    const ajaxBaseUrl = "${ajaxBaseUrl}";
</script>
<script src="/js/blog_edit.js"></script>
</@p.page>