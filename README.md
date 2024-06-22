# Mindecrire

A framework for creating a blog site along with static pages.

## How to use

### `/`

Redirects to `/blog`.

### `/blog`

Redirects to the latest blog post. If there are no blog posts, a blank blog page is displayed.

### `/blog/post/post-title`

Display a blog post (here, `post-title`).

### `/blog/add`

Add a new blog post. The markdown editor can be used to create a post. Tags can be added using the "Add Tag" field and
button. Tags can be removed by clicking the "x" next to the tag name. Images can be uploaded to object storage by
clicking the "Add Images" button (one or many can be selected at once). Once uploaded, a link will appear with the file
name. Click the link to copy the location formatted as a markdown image to paste into the editor. Click the trash can
icon to delete the image from object storage. If an image upload fails, its link will appear red with two icons. The
minus icon will cancel the upload (really just removing it from the list on screen). The circular arrow icon will retry
the upload. Hovering over the link will display the error message in a tooltip. An added post can be navigated to by
its title translated to kebab-case (e.g. "Post Title" becomes "post-title").

### `/blog/edit/post-title`

Edit a blog post (here, `post-title`). There will be no indication if a post was updated, so use this to fix things
like typos.

### `/blog/update/post-title`

Update a blog post (here, `post-title`). Updates will appear on a blog post, latest first, in a box at the top.

### `/page-name`

Display a static page (here, `page-name`). All static pages are listed on the left of the top navigation bar.

### `/refresh`

Display a page with a Refresh link that will refresh the Spring context (All this does is call `/actuator/refresh`).
This is useful when TLS/SSL certificates need to be manually reloaded or when doing things like adding a new static page
or adding users and changing permissions (static pages are permissions are included in the refresh scope). When running
locally, don't forget that these files need to be updated in the `target/classes` directory. Updating templates will
take effect instantly without refreshing, so take care if updating templates.

## How to build

### POM

Add Mindecrire as a compile dependency and your database driver as a runtime dependency. If you are using PostgreSQL,
there are migration scripts included that will be run if you also include Flyway - every time the application starts,
Spring's Flyway integration will automatically run any new migrations.

Minimal example using PostgreSQL and Flyway:

```xml
<?xml version="1.0" encoding="UTF-8"?>

<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.orgname</groupId>
    <artifactId>project-name</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>

    <name>My website</name>
    <url>https://my-website.com</url>

    <properties>
        <maven.compiler.source>12</maven.compiler.source>
        <maven.compiler.target>12</maven.compiler.target>
    </properties>

    <dependencies>
        <dependency>
            <groupId>net.eightlives</groupId>
            <artifactId>mindecrire</artifactId>
            <version>1.0.0-SNAPSHOT</version>
        </dependency>
        <!-- runtime  -->
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <version>42.7.3</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.flywaydb</groupId>
            <artifactId>flyway-core</artifactId>
            <version>6.5.7</version>
            <scope>runtime</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <version>2.2.13.RELEASE</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>repackage</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-dependency-plugin</artifactId>
                <executions>
                    <execution>
                        <id>unpack</id>
                        <phase>package</phase>
                        <goals>
                            <goal>unpack</goal>
                        </goals>
                        <configuration>
                            <artifactItems>
                                <artifactItem>
                                    <groupId>${project.groupId}</groupId>
                                    <artifactId>${project.artifactId}</artifactId>
                                    <version>${project.version}</version>
                                </artifactItem>
                            </artifactItems>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

### Properties

| Property                         | Type   | Default | Description                                    |
|----------------------------------|--------|---------|------------------------------------------------|
| objectStorage.imageBucket.name   | string | null    | Bucket name                                    |
| objectStorage.imageBucket.url    | string | null    | Storage service URL                            |
| objectStorage.imageBucket.region | string | null    | Storage service region                         |
| site.url                         | string | null    | This site's URL, used for AJAX calls from page |

#### Database

- `spring.flyway.locations`: If you are using Flyway (see [POM](#pom)) but don't want to use the Mindecrire migration
scripts (or are using a database other than PostgreSQL and need to use a different dialect), set this to the location.

#### User Permissions

User permissions are configured statically with usernames as entries mapped to a list of permissions for that user:

```yaml
userAuthorization.userPermissions:
  bob: POST_ADD, POST_EDIT, POST_UPDATE
  alice: ADMIN, POST_ADMIN
```

User permissions:

| Permission  | Description                                                               |
|-------------|---------------------------------------------------------------------------|
| ADMIN       | Can refresh application context (when manually renewing TLS Certificate). |
| POST_ADMIN  | Can edit or update any post by any user.                                  |
| POST_ADD    | Can add a post.                                                           |
| POST_EDIT   | Can edit posts that the user themselves has added.                        |
| POST_UPDATE | Can update posts that the user themselves has added.                      |

It may be useful to keep this configuration in a separate file in `/etc/mindecrire/config`,
(e.g. `/etc/mindecrire/config/user-authorization.yml`) then used as a config source when starting the application
(See [Docker](#docker) for profile usage).

#### Static Pages

Static pages can be added by creating a markdown (`.md`) file in `src/main/resources/static/markdown` and
adding the filename without the extension as the key and title to display on the page as the value, e.g.:

```yaml
staticContent.markdownToName:
  projects: My Projects
  about: About
```

This would create a page at `site.com/projects` with the title "My Projects" and a page at `site.com/about` with the title "About".

#### Friendly SSL

Mindecrire uses [Friendly SSL](https://github.com/zrbrown/friendly-ssl) to automatically manage the site's TLS/SSL
certificate. At a minimum, the domain and account email must be provided:

```yaml
friendly-ssl:
  domain: my-website.com
  account-email: youremail@provider.com
```

#### Production

In production, copy below into `production.yml` (or equivalent `production.properties`) in the project root directory and enter secrets:

```yaml
spring:
  datasource:
    url: x
    username: x
    password: x
  spring.security.oauth2.client.registration.github.client-id: x
  security.oauth2.client.registration.github.client-secret: x

objectStorage:
  imageBucket:
    accessKeyId: x
    secretAccessKey: x

friendly-ssl:
  acme-session-url: acme://letsencrypt.org
  auto-renew-enabled: true

spring.profiles.include:
  - ssl-redirect
```

The `ssl-redirect` profile redirects HTTP traffic to HTTPS.

`spring.security.oauth2.client.registration.github.*`: While `github` is used here, any of Spring's supported
OAuth2 providers can be used. If you use another authorization provider, ensure it is changed here. Using Github
requires creating a Github App and generating a client secret.

##### Friendly SSL

`friendly-ssl.auto-renew-enabled` will configure Friendly SSL to automatically renew the TLS/SSL certificate at the
default time before its expiration. If you don't want this, remove that property or set it to `false`.

See [Friendly SSL](https://github.com/zrbrown/friendly-ssl) for more configuration options. Note that the manual
certificate renewal and TOS agreement endpoints are disabled by default, so these  operations (particularly agreeing
to TOS) will require server access.

### CSS

Mindecrire uses standard HTML tags, so CSS can be applied to standard elements. For blog post content, which is rendered
from Markdown, styles can be set for the following classes (the types of elements should be self-explanatory based on
the name, based on the [CommonMark](https://github.com/commonmark/commonmark-java) types):

- `mindecrire-md-block-quote`
- `mindecrire-md-custom-block`
- `mindecrire-md-document`
- `mindecrire-md-fenced-code-block`
- `mindecrire-md-heading`
- `mindecrire-md-html-block`
- `mindecrire-md-indented-code-block`
- `mindecrire-md-bullet-list`
- `mindecrire-md-ordered-list`
- `mindecrire-md-list-item`
- `mindecrire-md-paragraph`
- `mindecrire-md-thematic-break`
- `mindecrire-md-code`
- `mindecrire-md-custom-node`
- `mindecrire-md-emphasis`
- `mindecrire-md-hard-line-break`
- `mindecrire-md-html-inline`
- `mindecrire-md-image`
- `mindecrire-md-link`
- `mindecrire-md-soft-line-break`
- `mindecrire-md-strong-emphasis`
- `mindecrire-md-text`

Other CSS classes that can be customized for common site elements:

- `post-title`
- `content-container`
- `post-content`
- `post-author`
- `post-date`
- `content-title-edit-container`
- `content-title-edit`
- `post-update-container`
- `post-update-date`
- `post-action-container`
- `post-action`
- `tag-container`
- `tag-wrapper`
- `tag`
- `add-tag-text`
- `tag-remove`
- `file-input`
- `refresh-button-container`
- `refresh-button`
- `navigation-buttons`
- `previous-post`
- `next-post`
- `header`
- `headerLinkContainer`
- `headerNavContainer`
- `headerIconContainer`
- `headerIconLink`
- `headerIcon`
- `validation-text`
- `error-title`

### Common Variables

In `src/main/resources/templates/common_vars.ftlh`, assign common Freemarker variables to structure and style the site.
These are all required to be defined except for `headTitle`:

```injectedfreemarker
<#assign headTitle = "My Page">
<#assign customCss = "/css/my_css.css">
<#assign headerImage = "/images/logo.png">
<#assign navLinks = [
["/projects", "Projects"],
["/about", "About"]
]>
<#assign headerIcons = [
["//github.com/username", "Github", "/images/github-icon.svg"],
["//bsky.app/profile/username.bsky.social", "Bluesky", "/images/bluesky-icon.svg"],
["//linkedin.com/in/username", "LinkedIn", "/images/linkedin-icon.png"],
["//facebook.com/username", "Facebook", "/images/facebook-icon.png"]
]>
```

| Variable    | Description                                                                                                                                              |
|-------------|----------------------------------------------------------------------------------------------------------------------------------------------------------|
| headTitle   | The title that will be used if a given page doesn't set one.                                                                                             |
| customCss   | CSS file that contains overrides and any other custom CSS.                                                                                               |
| headerImage | Image displayed to the left of the page header.                                                                                                          |
| navLinks    | List of links in the navigation bar of the page header. Format: Relative link ("/example"), Display Name ("Example")                                     |
| headerIcons | Icons displayed on the right of the page header. Format: URL ("//github.com/username"), Alt Text ("Github"), Image location ("/images/bluesky-icon.svg") |

`headerIcons` can refer to any image. If SVG or PNG images are used, they can be uniformly colored by setting the
`filter` CSS property to the `headerIcon` class:

```css
.headerIcon {
    filter: brightness(0) saturate(100%) invert(37%) sepia(62%) saturate(346%) hue-rotate(186deg) brightness(92%) contrast(88%);
}
```

Filter values can be generated [here](https://isotropic.co/tool/hex-color-to-css-filter/) - but since this only
converts all-black images, if your images are not all-black, add `brightness(0) saturate(100%)` to the beginning of
the value to convert them to black first.

### Static Resources

Static resources can be put under `src/main/resources/static`, such as `src/main/resources/static/docs/doc.pdf` or
`src/main/resources/static/images/thing.jpg` and referenced from markdown or templates via a relative path, e.g.
`[Important Doc](docs/important_doc.pdf)` or `<#assign headerImage = "/images/thing.jpg">`.

### Application

A single simple application class is all the code that's required:

```java
package com.yoursite.site.Application;

import net.eightlives.mindecrire.annotation.MindecrireApp;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;

@Configuration
@MindecrireApp
@EnableAutoConfiguration
public class Application extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

### Infrastructure

Besides a server on which to run the application, an S3-compatible object storage service is needed to store uploaded
images and a database is needed to store everything else.

### Running

Since Mindecrire is just a Spring Boot application, it can be run any number of ways you can run a Spring Boot application.
See [Docker](#docker) for an example.

### Other Useful Things

#### Running Locally

To run locally, a `local.yml` and `local-secrets.yml` (add to `.gitignore`!) file is useful:

`local.yml`
```yaml
server:
  port: 8000
  ssl:
    enabled: false

spring:
  datasource.url: jdbc:postgresql://localhost:5432/databasename
  datasource.username: postgres
  datasource.password: postgres

objectStorage:
  imageBucket:
    name: images
    url: http://localhost:9000
    region: us-west-2
    accessKeyId: x
    secretAccessKey: x

site:
  url: http://localhost:8000

userAuthorization.userPermissions:
  bob: ADMIN, POST_ADMIN, POST_ADD, POST_EDIT, POST_UPDATE
```

`local-secrets.yml`
```yaml
spring:
  security.oauth2.client.registration.github.client-id: your-id
  security.oauth2.client.registration.github.client-secret: your-secret
```

Ensure a local PostgreSQL instance and S3 instance are running before starting the server (Requires Docker and [awslocal](https://github.com/localstack/awscli-local)):

```shell
docker run --detach --rm -p 4566:4566 --name locals3 localstack/localstack:s3-latest
awslocal s3api create-bucket --bucket images --region us-west-2 --create-bucket-configuration LocationConstraint=us-west-2
docker run --detach --rm -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -p 5432:5432 --name localdb postgres:10.11
```

Run with VM argument `-Dspring.config.name=mindecrire,application,local,local-secrets`.

#### Docker

For portability, using a `Dockerfile` is recommended:

```dockerfile
FROM maven:3.6.2-jdk-12
ARG BUILDSRC=/buildsrc
COPY ./ ${BUILDSRC}
WORKDIR ${BUILDSRC}
RUN mvn clean package

FROM openjdk:12-alpine
ARG DEPENDENCY=/buildsrc/target/dependency
COPY --from=0 ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=0 ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=0 ${DEPENDENCY}/BOOT-INF/classes /app
EXPOSE 80
EXPOSE 443
ENTRYPOINT ["java","-Dspring.config.name=mindecrire,application,production,user-authorization","-cp","app:app/lib/*","com.yoursite.site.Application"]
```

Running `docker build -t yoursite .` in the directory containing the `Dockerfile` will build the image `yoursite`.

The above is dependent on some volumes on the server being mounted. To run, a shell script is useful so you don't
have to memorize a long command:

```shell
#!/usr/bin/env bash

docker run \
-d \
--mount "type=bind,src=/etc/mindecrire/config,dst=/app/config" \
--mount "type=bind,src=${PWD}/src/main/resources/static,dst=/app/static" \
-v /etc/mindecrire/ssl:/etc/mindecrire/ssl \
-p 443:443 -p 80:80 yoursite
```

- Production config should go in `/etc/mindecrire/config` in this example.
- Mounting the `static` directory isn't necessary, but if done this way (by cloning your site's git repository
to your production environment) the static content can be updated on the fly without restarting the server.
- TLS information will be stored in `/etc/mindecrire/ssl` in this example.

#### Directory permissions

By default, `/etc/mindecrire/` is used to store configuration files. You may need to adjust directory permissions using
`chmod` so that the user running the server has write permission to this directory.
