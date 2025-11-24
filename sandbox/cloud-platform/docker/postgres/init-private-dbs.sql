-- Create databases for private tenants
CREATE DATABASE private_tenant_001;
CREATE DATABASE private_tenant_002;

-- Grant permissions
GRANT ALL PRIVILEGES ON DATABASE private_tenant_001 TO postgres;
GRANT ALL PRIVILEGES ON DATABASE private_tenant_002 TO postgres;

-- Switch to each database and create extensions
\c private_tenant_001
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

\c private_tenant_002
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
