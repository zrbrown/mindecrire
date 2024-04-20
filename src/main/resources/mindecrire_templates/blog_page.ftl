<#include "/common_vars.ftl"/>
<!DOCTYPE html>

<html>
<head>
    <meta charset="utf-8">
    <title>${title}</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <link rel="stylesheet" href="/css/mindecrire.css">
    <link rel="stylesheet" href="${customCss}">
    <link rel="stylesheet" href="//maxcdn.bootstrapcdn.com/font-awesome/4.3.0/css/font-awesome.min.css">
</head>

<body>
<#include "/common/header.ftl"/>

<div class="content-container">
    <div class="post-title">${postTitle!}</div>
    <#if postUpdates??>
        <#list postUpdates as postUpdate>
            <div class="post-update-container">
                <span class="post-update-date">Updated ${postUpdate.date}</span>
                <div>${postUpdate.content}</div>
            </div>
        </#list>
    </#if>
    <span class="post-date">${postDate!}</span>
    <br/>
    <span class="post-author">${postAuthor!}</span>
    <div>${postContent!}</div>
    <#if tags??>
        <#list tags>
            <div class="tag-container">
                <#items as tag>
                    <div class="tag-wrapper"><a href="" class="tag">${tag}</a>
                    </div>
                </#items>
            </div>
        </#list>
    </#if>
    <div class="navigation-buttons">
        <#if showPrevious?? && showPrevious>
            <div class="previous-post"><a href="/blog/${previousPost}">&larr; Older</a></div>
        </#if>
        <#if showNext?? && showNext>
            <div class="next-post"><a href="/blog/${nextPost}">Newer &rarr;</a></div>
        </#if>
    </div>
</div>

</body>
</html>