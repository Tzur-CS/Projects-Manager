-- ============================================================================
-- Add Role System and Moveo Admin User
-- ============================================================================

-- Step 1: Add role column to users table (if it doesn't exist)
ALTER TABLE users
ADD COLUMN IF NOT EXISTS role VARCHAR(10) NOT NULL DEFAULT 'USER';

-- Step 2: Update existing users with correct Cognito sub IDs and roles
-- Tzur - Regular user
UPDATE users
SET cognito_sub = '3265e404-d051-70fe-f26e-13b4afd5d058',
    username = 'tzur',
    role = 'USER'
WHERE email = 'tzur@example.com';

-- Eden - Regular user
UPDATE users
SET cognito_sub = 'e24574f4-a0f1-70e4-fcbd-11a0911f8d67',
    username = 'eden',
    role = 'USER'
WHERE email = 'eden@example.com';

-- Step 3: Insert or Update Moveo as admin user
INSERT INTO users (name, email, username, cognito_sub, role, created_at, updated_at)
VALUES (
    'Moveo',
    'moveo@example.com',
    'moveo',
    '4235a444-d0a1-70b2-f43e-0e4790f07064',
    'ADMIN',
    NOW(),
    NOW()
)
ON DUPLICATE KEY UPDATE
    cognito_sub = '4235a444-d0a1-70b2-f43e-0e4790f07064',
    username = 'moveo',
    role = 'ADMIN',
    updated_at = NOW();

-- Step 4: Verify the changes
SELECT
    id,
    name,
    email,
    username,
    cognito_sub,
    role,
    created_at
FROM users
ORDER BY role DESC, name;

-- Expected output:
-- +----+-------+--------------------+----------+------------------------------------------+-------+---------------------+
-- | id | name  | email              | username | cognito_sub                              | role  | created_at          |
-- +----+-------+--------------------+----------+------------------------------------------+-------+---------------------+
-- |  3 | Moveo | moveo@example.com  | moveo    | 4235a444-d0a1-70b2-f43e-0e4790f07064    | ADMIN | 2025-12-06 10:00:00 |
-- |  1 | Tzur  | tzur@example.com   | tzur     | 3265e404-d051-70fe-f26e-13b4afd5d058    | USER  | 2025-12-06 10:00:00 |
-- |  2 | Eden  | eden@example.com   | eden     | e24574f4-a0f1-70e4-fcbd-11a0911f8d67    | USER  | 2025-12-06 10:00:00 |
-- +----+-------+--------------------+----------+------------------------------------------+-------+---------------------+

