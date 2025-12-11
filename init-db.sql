-- Create the authdb database
CREATE DATABASE authdb;
CREATE USER auth_user WITH PASSWORD 'auth_pass';
GRANT ALL PRIVILEGES ON DATABASE authdb TO auth_user;

-- Connect to authdb and grant schema privileges
\c authdb;
GRANT ALL ON SCHEMA public TO auth_user;

-- Create the resourcedb database
CREATE DATABASE resourcedb;
CREATE USER resource_user WITH PASSWORD 'resource_pass';
GRANT ALL PRIVILEGES ON DATABASE resourcedb TO resource_user;

-- Connect to resourcedb and grant schema privileges
\c resourcedb;
GRANT ALL ON SCHEMA public TO resource_user;
