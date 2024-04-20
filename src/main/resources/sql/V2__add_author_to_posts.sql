SET SEARCH_PATH TO application;

CREATE TABLE IF NOT EXISTS author_details
(
    author       TEXT PRIMARY KEY,
    display_name TEXT
);

ALTER TABLE application.posts
    ADD COLUMN author TEXT,
    ADD FOREIGN KEY (author) REFERENCES author_details (author);

INSERT INTO application.author_details(author, display_name) VALUES ('unknown', 'Unknown');

UPDATE application.posts
SET author='unknown'
WHERE posts.author IS NULL;

ALTER TABLE application.posts
    ALTER COLUMN author SET NOT NULL;
