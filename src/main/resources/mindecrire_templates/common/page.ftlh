<#macro page title="Mindecrire Site" stylesheets=[] scripts=[]>
<#include "/common_vars.ftlh"/>
<!DOCTYPE html>

<html lang="en">
<head>
    <meta charset="utf-8">
    <title>${title!headTitle!"Mindecrire Site"}</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <link rel="stylesheet" href="/css/mindecrire.css">
    <link rel="stylesheet" href="${customCss}">
    <#list stylesheets as stylesheet>
        <link rel="stylesheet" href="${stylesheet}">
    </#list>

    <#list scripts as script>
        <script src="${script}"></script>
    </#list>
</head>

<body>
<div class="header">
    <div id="header-logo-container">
        <a href="/blog"><img id="header-logo" src="${headerImage}" alt="Header logo"/></a>
    </div>
    <div class="headerLinkContainer">
        <div class="headerNavContainer">
            <a href="/blog" title="Blog">Blog</a>
            <#list navLinks as navLink>
                <a href="${navLink[0]}" title="${navLink[1]}">${navLink[1]}</a>
            </#list>
        </div>
        <div class="headerIconContainer">
            <#list headerIcons as headerIcon>
                <a href="${headerIcon[0]}" class="headerIconLink" title="${headerIcon[1]}"><img src="${headerIcon[2]}" alt="${headerIcon[1]}" class="headerIcon"/></a>
            </#list>
        </div>
    </div>
</div>
<#nested/>
</body>
</html>
</#macro>