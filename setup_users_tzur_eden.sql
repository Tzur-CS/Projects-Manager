-- ============================================================================
-- Setup Users: Tzur and Eden with their Cognito Sub IDs
-- ============================================================================

-- Clear existing data (optional - remove if you want to keep existing data)
-- DELETE FROM tasks WHERE 1=1;
-- DELETE FROM projects WHERE 1=1;
-- DELETE FROM users WHERE 1=1;

-- ============================================================================
-- CREATE USERS
-- ============================================================================

-- User 1: Tzur
INSERT INTO users (email, username, cognito_sub, created_at, updated_at)
VALUES (
    'tzur@example.com',
    'tzur',
    '3265e404-d051-70fe-f26e-13b4afd5d058',
    NOW(),
    NOW()
)
ON DUPLICATE KEY UPDATE
    email = 'tzur@example.com',
    username = 'tzur',
    cognito_sub = '3265e404-d051-70fe-f26e-13b4afd5d058',
    updated_at = NOW();

-- User 2: Eden
INSERT INTO users (email, username, cognito_sub, created_at, updated_at)
VALUES (
    'eden@example.com',
    'eden',
    'e24574f4-a0f1-70e4-fcbd-11a0911f8d67',
    NOW(),
    NOW()
)
ON DUPLICATE KEY UPDATE
    email = 'eden@example.com',
    username = 'eden',
    cognito_sub = 'e24574f4-a0f1-70e4-fcbd-11a0911f8d67',
    updated_at = NOW();

-- ============================================================================
-- CREATE SAMPLE PROJECTS FOR EACH USER
-- ============================================================================

-- Projects for Tzur
INSERT INTO projects (name, description, owner_id, created_at, updated_at)
VALUES
    ('Tzur Project 1', 'First project for Tzur', '3265e404-d051-70fe-f26e-13b4afd5d058', NOW(), NOW()),
    ('Tzur Project 2', 'Second project for Tzur', '3265e404-d051-70fe-f26e-13b4afd5d058', NOW(), NOW()),
    ('Tzur Website', 'Website development project', '3265e404-d051-70fe-f26e-13b4afd5d058', NOW(), NOW());

-- Projects for Eden
INSERT INTO projects (name, description, owner_id, created_at, updated_at)
VALUES
    ('Eden Project 1', 'First project for Eden', 'e24574f4-a0f1-70e4-fcbd-11a0911f8d67', NOW(), NOW()),
    ('Eden Project 2', 'Second project for Eden', 'e24574f4-a0f1-70e4-fcbd-11a0911f8d67', NOW(), NOW()),
    ('Eden Mobile App', 'Mobile app development', 'e24574f4-a0f1-70e4-fcbd-11a0911f8d67', NOW(), NOW());

-- ============================================================================
-- CREATE SAMPLE TASKS FOR EACH USER'S PROJECTS
-- ============================================================================

-- Tasks for Tzur's Projects
-- (Get project IDs first, these will be auto-incremented)
SET @tzur_project1_id = (SELECT id FROM projects WHERE name = 'Tzur Project 1' AND owner_id = '3265e404-d051-70fe-f26e-13b4afd5d058');
SET @tzur_project2_id = (SELECT id FROM projects WHERE name = 'Tzur Project 2' AND owner_id = '3265e404-d051-70fe-f26e-13b4afd5d058');

INSERT INTO tasks (title, description, status, project_id, assigned_user_id, due_date, created_at, updated_at)
VALUES
    ('Setup Database', 'Configure MySQL database', 'TODO', @tzur_project1_id, '3265e404-d051-70fe-f26e-13b4afd5d058', DATE_ADD(NOW(), INTERVAL 7 DAY), NOW(), NOW()),
    ('Create API', 'Build REST API endpoints', 'IN_PROGRESS', @tzur_project1_id, '3265e404-d051-70fe-f26e-13b4afd5d058', DATE_ADD(NOW(), INTERVAL 14 DAY), NOW(), NOW()),
    ('Design UI', 'Design user interface', 'TODO', @tzur_project2_id, '3265e404-d051-70fe-f26e-13b4afd5d058', DATE_ADD(NOW(), INTERVAL 10 DAY), NOW(), NOW());

-- Tasks for Eden's Projects
SET @eden_project1_id = (SELECT id FROM projects WHERE name = 'Eden Project 1' AND owner_id = 'e24574f4-a0f1-70e4-fcbd-11a0911f8d67');
SET @eden_project2_id = (SELECT id FROM projects WHERE name = 'Eden Project 2' AND owner_id = 'e24574f4-a0f1-70e4-fcbd-11a0911f8d67');

INSERT INTO tasks (title, description, status, project_id, assigned_user_id, due_date, created_at, updated_at)
VALUES
    ('Research Requirements', 'Gather project requirements', 'DONE', @eden_project1_id, 'e24574f4-a0f1-70e4-fcbd-11a0911f8d67', NOW(), NOW(), NOW()),
    ('Write Documentation', 'Create technical documentation', 'IN_PROGRESS', @eden_project1_id, 'e24574f4-a0f1-70e4-fcbd-11a0911f8d67', DATE_ADD(NOW(), INTERVAL 5 DAY), NOW(), NOW()),
    ('Test Features', 'Test all new features', 'TODO', @eden_project2_id, 'e24574f4-a0f1-70e4-fcbd-11a0911f8d67', DATE_ADD(NOW(), INTERVAL 12 DAY), NOW(), NOW());

-- ============================================================================
-- VERIFY DATA
-- ============================================================================

-- Show all users
SELECT 'USERS:' as '';
SELECT id, email, username, cognito_sub FROM users;

-- Show projects by user
SELECT '' as '';
SELECT 'TZUR PROJECTS:' as '';
SELECT id, name, description, owner_id FROM projects WHERE owner_id = '3265e404-d051-70fe-f26e-13b4afd5d058';

SELECT '' as '';
SELECT 'EDEN PROJECTS:' as '';
SELECT id, name, description, owner_id FROM projects WHERE owner_id = 'e24574f4-a0f1-70e4-fcbd-11a0911f8d67';

-- Show tasks by user
SELECT '' as '';
SELECT 'TZUR TASKS:' as '';
SELECT t.id, t.title, t.status, p.name as project_name
FROM tasks t
JOIN projects p ON t.project_id = p.id
WHERE t.assigned_user_id = '3265e404-d051-70fe-f26e-13b4afd5d058';

SELECT '' as '';
SELECT 'EDEN TASKS:' as '';
SELECT t.id, t.title, t.status, p.name as project_name
FROM tasks t
JOIN projects p ON t.project_id = p.id
WHERE t.assigned_user_id = 'e24574f4-a0f1-70e4-fcbd-11a0911f8d67';

