CREATE DATABASE IF NOT EXISTS distwebuser;
CREATE DATABASE IF NOT EXISTS distmission;
CREATE DATABASE IF NOT EXISTS distevent;
CREATE DATABASE IF NOT EXISTS distwebcompetence;

-- Optional: Add user privileges for each database
GRANT ALL PRIVILEGES ON distwebuser.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON distmission.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON distevent.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON distwebcompetence.* TO 'root'@'%';