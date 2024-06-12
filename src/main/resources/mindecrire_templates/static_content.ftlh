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
    <div class="post-title">${title}</div>
    ${content?no_esc}
</div>

</body>
</html>