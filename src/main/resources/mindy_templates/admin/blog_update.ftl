<!DOCTYPE html>

<html>
<head>
    <meta charset="utf-8">
    <title>Update Post</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <link rel="stylesheet" href="/css/mindy.css">
    <#include "/custom_css_link.ftl"/>
    <link rel="stylesheet" href="//maxcdn.bootstrapcdn.com/font-awesome/4.3.0/css/font-awesome.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/simplemde/latest/simplemde.min.css">

    <script src="https://cdn.jsdelivr.net/simplemde/latest/simplemde.min.js"></script>
</head>

<body>
<#include "/common/header.ftl"/>

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
    <p class="post-content">${postContent}</p>
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

</body>
</html>