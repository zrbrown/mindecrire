<!DOCTYPE html>

<html>
<head>
    <meta charset="utf-8">
    <title>Edit Post</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <link rel="stylesheet" href="/css/mindy.css">
    <#include "/custom_css_link.ftl"/>
    <link rel="stylesheet" href="//maxcdn.bootstrapcdn.com/font-awesome/4.3.0/css/font-awesome.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/simplemde/latest/simplemde.min.css">

    <script src="https://cdn.jsdelivr.net/simplemde/latest/simplemde.min.js"></script>
    <script src="https://unpkg.com/axios/dist/axios.min.js"></script>
</head>

<body>
<#include "/common/header.ftl"/>

<div class="content-container">
    <form action="${submitPath}" method="post">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

        <div class="content-title-edit-container">
            <input title="Post title" type="text" class="content-title-edit" name="postTitle" value="${postTitle!}"/>
        </div>
        <br/>
        <textarea title="Post content" id="editor" name="postContent">${postContent!}</textarea>
        <input id="addImageFileInput" class="file-input" title="Add Images" type="file" name="files[]" onchange="handleFileSelect(this)" multiple/>
        <label for="addImageFileInput">Add Images</label>
        <ul id="uploadedImagesList" title="Uploaded Images">
        </ul>
        <input title="Tag name entry" type="text" class="add-tag-text" name="addTag" id="addTagInput"/>
        <button title="Add tag" type="button" onclick="addTagToTags()">Add tag</button>
        <div class="tag-container" id="tagContainer">
            <#if tags??>
                <#list tags>
                    <#items as tag>
                        <div class="tag-wrapper">
                            <span class="tag">${tag}</span><a href="" class="tag tag-remove">&#9447;</a>
                        </div>
                    </#items>
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

</body>
</html>