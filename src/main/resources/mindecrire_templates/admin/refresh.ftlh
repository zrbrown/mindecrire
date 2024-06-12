<#include "/common_vars.ftlh"/>
<!DOCTYPE html>

<html>
<head>
    <meta charset="utf-8">
    <title>${title!headTitle!"Mindecrire Site"} - Refresh</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <link rel="stylesheet" href="/css/mindecrire.css">
    <link rel="stylesheet" href="${customCss}">
</head>

<body>
<#include "/common/header.ftlh"/>

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

</body>
</html>