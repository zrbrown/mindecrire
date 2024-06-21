<#import "./common/page.ftlh" as p>

<@p.page title>
<div class="content-container">
    <div class="post-title">${title}</div>
    ${content?no_esc}
</div>
</@p.page>