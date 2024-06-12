<#include "/common_vars.ftlh"/>
<!DOCTYPE html>

<html>
<head>
    <meta charset="utf-8">
    <title>${title!headTitle!"Mindecrire Site"}</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <link rel="stylesheet" href="/css/mindecrire.css">
    <link rel="stylesheet" href="${customCss}">
</head>

<body>
<#include "/common/header.ftlh"/>

<div class="content-container">
    <div class="post-title">${postTitle!}</div>
    <#if postUpdates??>
        <#list postUpdates as postUpdate>
            <div class="post-update-container">
                <span class="post-update-date">Updated ${postUpdate.date}</span>
                <div>${postUpdate.content?no_esc}</div>
            </div>
        </#list>
    </#if>
    <span class="post-date">${postDate!}</span>
    <br/>
    <span class="post-author">${postAuthor!}</span>
    <div>${postContent!?no_esc}</div>
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