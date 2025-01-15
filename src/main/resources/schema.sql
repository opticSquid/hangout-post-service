-- Installing the required extensions
CREATE EXTENSION IF NOT EXISTS postgis;

--Creating the tables
create table
    if not exists comment (
        commentid uuid not null,
        createdat timestamp(6),
        replies integer,
        text varchar(500),
        toplevel boolean,
        userid uuid,
        postid uuid,
        primary key (commentid)
    );

create table
    if not exists hierarchy_keeper (
        keeperid integer not null,
        childcommentid uuid,
        parentcommentid uuid,
        primary key (keeperid)
    );

create table
    if not exists media (
        filename varchar(513) not null,
        content_type varchar(255),
        process_status varchar(255) check (
            process_status in ('IN_QUEUE', 'PROCESSING', 'SUCCESS', 'FAIL')
        ),
        primary key (filename)
    );

create table
    if not exists post (
        post_id uuid not null,
        comments integer,
        created_at timestamp(6)
        with
            time zone,
            hearts integer,
            interactions integer,
            location geography (Point, 4326),
            owner_id numeric(38, 0),
            post_description varchar(500),
            publish boolean,
            filename varchar(513),
            primary key (post_id)
    );

create sequence if not exists hierarchy_keeper_seq start
with
    1 increment by 50;

-- Adding a QuadTree GiST index on the geometry colum for faster search
CREATE INDEX CONCURRENTLY IF NOT EXISTS locationIndex ON post USING GIST (location);

-- Forcing postgres to update information about added index on geolocation to use in further queries
ANALYZE post (location);