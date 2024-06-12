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