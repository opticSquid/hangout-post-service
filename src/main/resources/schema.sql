-- Installing the required extensions
CREATE EXTENSION IF NOT EXISTS postgis;

-- CREATE EXTENSION IF NOT EXISTS fuzzystrmatch;
-- CREATE EXTENSION IF NOT EXISTS postgis_tiger_geocoder;
-- CREATE EXTENSION IF NOT EXISTS postgis_topology;
--Creating the tables
-- Adding a QuadTree GiST index on the geometry colum for faster search
-- CREATE INDEX CONCURRENTLY locationIndex ON post USING GIST (location);
-- Forcing postgres to update information about added index on geolocation to use in further queries
-- VACUUM ANALYZE post (location);