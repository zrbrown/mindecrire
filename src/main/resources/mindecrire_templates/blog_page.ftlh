<#import "./common/page.ftlh" as p>

<@p.page postTitle>
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
            <div class="previous-post"><a href="/blog/post/${previousPost}">&larr; Older</a></div>
        </#if>
        <#if showNext?? && showNext>
            <div class="next-post"><a href="/blog/post/${nextPost}">Newer &rarr;</a></div>
        </#if>
    </div>
</div>
</@p.page>