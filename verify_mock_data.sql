-- ============================================
-- Verify Mock Data in moveo-db Database
-- ============================================

-- Connect to the database
USE `moveo-db`;

-- ============================================
-- 1. Check Users
-- ============================================
SELECT '=== USERS ===' as '';
SELECT * FROM users;

SELECT '' as '';
SELECT CONCAT('Total Users: ', COUNT(*)) as Summary FROM users;

-- ============================================
-- 2. Check Projects
-- ============================================
SELECT '' as '';
SELECT '=== PROJECTS ===' as '';
SELECT
    p.id,
    p.name,
    p.owner_id,
    p.description,
    (SELECT COUNT(*) FROM tasks WHERE project_id = p.id) as task_count
FROM projects p
ORDER BY p.id;

SELECT '' as '';
SELECT CONCAT('Total Projects: ', COUNT(*)) as Summary FROM projects;

-- ============================================
-- 3. Check Tasks
-- ============================================
SELECT '' as '';
SELECT '=== TASKS ===' as '';
SELECT
    t.id,
    t.title,
    t.status,
    t.assigned_to,
    p.name as project_name
FROM tasks t
JOIN projects p ON t.project_id = p.id
ORDER BY p.id, t.id;

SELECT '' as '';
SELECT CONCAT('Total Tasks: ', COUNT(*)) as Summary FROM tasks;

-- ============================================
-- 4. Task Status Distribution
-- ============================================
SELECT '' as '';
SELECT '=== TASK STATUS DISTRIBUTION ===' as '';
SELECT
    status,
    COUNT(*) as count,
    CONCAT(ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM tasks), 1), '%') as percentage
FROM tasks
GROUP BY status
ORDER BY count DESC;

-- ============================================
-- 5. Projects by Owner with Task Summary
-- ============================================
SELECT '' as '';
SELECT '=== PROJECTS BY OWNER ===' as '';
SELECT
    p.owner_id,
    COUNT(DISTINCT p.id) as project_count,
    COUNT(t.id) as total_tasks,
    SUM(CASE WHEN t.status = 'DONE' THEN 1 ELSE 0 END) as completed_tasks,
    SUM(CASE WHEN t.status = 'IN_PROGRESS' THEN 1 ELSE 0 END) as in_progress_tasks,
    SUM(CASE WHEN t.status = 'TODO' THEN 1 ELSE 0 END) as todo_tasks
FROM projects p
LEFT JOIN tasks t ON p.id = t.project_id
GROUP BY p.owner_id
ORDER BY project_count DESC;

-- ============================================
-- 6. Detailed View - John's Projects
-- ============================================
SELECT '' as '';
SELECT '=== JOHN DOE PROJECTS (cognito-user-john-123) ===' as '';
SELECT
    p.id,
    p.name as project_name,
    p.description,
    COUNT(t.id) as total_tasks,
    SUM(CASE WHEN t.status = 'DONE' THEN 1 ELSE 0 END) as completed,
    SUM(CASE WHEN t.status = 'IN_PROGRESS' THEN 1 ELSE 0 END) as in_progress,
    SUM(CASE WHEN t.status = 'TODO' THEN 1 ELSE 0 END) as todo,
    CONCAT(ROUND(SUM(CASE WHEN t.status = 'DONE' THEN 1 ELSE 0 END) * 100.0 / COUNT(t.id), 1), '%') as completion_rate
FROM projects p
LEFT JOIN tasks t ON p.id = t.project_id
WHERE p.owner_id = 'cognito-user-john-123'
GROUP BY p.id, p.name, p.description
ORDER BY p.id;

-- ============================================
-- 7. Task Assignment Status
-- ============================================
SELECT '' as '';
SELECT '=== TASK ASSIGNMENT STATUS ===' as '';
SELECT
    CASE
        WHEN assigned_to IS NULL THEN 'Unassigned'
        WHEN assigned_to LIKE '%john%' THEN 'John Doe'
        WHEN assigned_to LIKE '%sarah%' THEN 'Sarah Cohen'
        WHEN assigned_to LIKE '%david%' THEN 'David Levi'
        ELSE assigned_to
    END as assigned_to,
    COUNT(*) as task_count
FROM tasks
GROUP BY
    CASE
        WHEN assigned_to IS NULL THEN 'Unassigned'
        WHEN assigned_to LIKE '%john%' THEN 'John Doe'
        WHEN assigned_to LIKE '%sarah%' THEN 'Sarah Cohen'
        WHEN assigned_to LIKE '%david%' THEN 'David Levi'
        ELSE assigned_to
    END
ORDER BY task_count DESC;

-- ============================================
-- 8. Project Statistics
-- ============================================
SELECT '' as '';
SELECT '=== PROJECT STATISTICS ===' as '';
SELECT
    'Largest Project (by tasks)' as metric,
    p.name as value,
    CONCAT(COUNT(t.id), ' tasks') as details
FROM projects p
LEFT JOIN tasks t ON p.id = t.project_id
GROUP BY p.id, p.name
ORDER BY COUNT(t.id) DESC
LIMIT 1;

SELECT
    'Most Active Project (most in-progress tasks)' as metric,
    p.name as value,
    CONCAT(SUM(CASE WHEN t.status = 'IN_PROGRESS' THEN 1 ELSE 0 END), ' tasks in progress') as details
FROM projects p
LEFT JOIN tasks t ON p.id = t.project_id
GROUP BY p.id, p.name
ORDER BY SUM(CASE WHEN t.status = 'IN_PROGRESS' THEN 1 ELSE 0 END) DESC
LIMIT 1;

-- ============================================
-- 9. Overall Summary
-- ============================================
SELECT '' as '';
SELECT '=== OVERALL SUMMARY ===' as '';
SELECT
    (SELECT COUNT(*) FROM users) as total_users,
    (SELECT COUNT(*) FROM projects) as total_projects,
    (SELECT COUNT(*) FROM tasks) as total_tasks,
    (SELECT COUNT(*) FROM tasks WHERE status = 'DONE') as completed_tasks,
    (SELECT COUNT(*) FROM tasks WHERE status = 'IN_PROGRESS') as in_progress_tasks,
    (SELECT COUNT(*) FROM tasks WHERE status = 'TODO') as todo_tasks,
    (SELECT COUNT(*) FROM tasks WHERE assigned_to IS NOT NULL) as assigned_tasks,
    (SELECT COUNT(*) FROM tasks WHERE assigned_to IS NULL) as unassigned_tasks;

-- ============================================
-- 10. Sample Data for Testing
-- ============================================
SELECT '' as '';
SELECT '=== SAMPLE IDS FOR TESTING ===' as '';
SELECT 'User IDs:' as type, GROUP_CONCAT(id ORDER BY id) as ids FROM users
UNION ALL
SELECT 'Project IDs:', GROUP_CONCAT(id ORDER BY id) FROM projects
UNION ALL
SELECT 'Task IDs:', GROUP_CONCAT(id ORDER BY id SEPARATOR ', ') FROM (SELECT id FROM tasks LIMIT 10) as t;

