<#import "../common/page.ftlh" as p>

<@p.page "Refresh">
<div class="content-container">
    <div class="refresh-button-container">
        <a href="" onclick="postRefresh()" class="refresh-button">Refresh</a>
    </div>
</div>

<script>
    function postRefresh() {
        const xmlHttp = new XMLHttpRequest();
        xmlHttp.open("POST", "/actuator/refresh", false);
        xmlHttp.send(null);
    }
</script>
</@p.page>