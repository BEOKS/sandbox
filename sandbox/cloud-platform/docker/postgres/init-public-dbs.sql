-- Create databases for public tenants
CREATE DATABASE public_tenant_gov_001;
CREATE DATABASE public_tenant_gov_002;

-- Grant permissions
GRANT ALL PRIVILEGES ON DATABASE public_tenant_gov_001 TO postgres;
GRANT ALL PRIVILEGES ON DATABASE public_tenant_gov_002 TO postgres;

-- Switch to each database and create extensions
\c public_tenant_gov_001
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

\c public_tenant_gov_002
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
