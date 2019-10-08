<#include "/common_vars.ftl"/>
<!DOCTYPE html>

<html>
<head>
    <meta charset="utf-8">
    <title>${title} - Refresh</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <link rel="stylesheet" href="/css/mindy.css">
    <link rel="stylesheet" href="${customCss}">
    <link rel="stylesheet" href="//maxcdn.bootstrapcdn.com/font-awesome/4.3.0/css/font-awesome.min.css">
</head>

<body>
<#include "/common/header.ftl"/>

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