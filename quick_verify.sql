-- Quick verification of mock data
USE `moveo-db`;

SELECT '=== USER COUNT ===' as '';
SELECT COUNT(*) as user_count FROM users;

SELECT '=== PROJECT COUNT ===' as '';
SELECT COUNT(*) as project_count FROM projects;

SELECT '=== TASK COUNT ===' as '';
SELECT COUNT(*) as task_count FROM tasks;

SELECT '=== USERS ===' as '';
SELECT * FROM users;

SELECT '=== PROJECTS ===' as '';
SELECT id, name, owner_id FROM projects;

SELECT '=== SAMPLE TASKS ===' as '';
SELECT t.id, t.title, t.status, p.name as project_name
FROM tasks t
JOIN projects p ON t.project_id = p.id
LIMIT 10;

