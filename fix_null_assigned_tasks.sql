v-- ============================================================================
-- Fix NULL values in tasks.assigned_to column
-- ============================================================================
-- This script updates all tasks with null assigned_to to be assigned to their project owner

-- Step 1: Show current tasks with NULL assigned_to
SELECT
    t.id,
    t.title,
    t.assigned_to,
    p.name as project_name,
    p.owner_id as project_owner
FROM tasks t
JOIN projects p ON t.project_id = p.id
WHERE t.assigned_to IS NULL;

-- Step 2: Update NULL assigned_to with project owner
UPDATE tasks t
JOIN projects p ON t.project_id = p.id
SET t.assigned_to = p.owner_id
WHERE t.assigned_to IS NULL;

-- Step 3: Verify - should return 0 rows
SELECT
    t.id,
    t.title,
    t.assigned_to,
    p.name as project_name,
    p.owner_id as project_owner
FROM tasks t
JOIN projects p ON t.project_id = p.id
WHERE t.assigned_to IS NULL;

-- Step 4: Show all tasks with their assignments
SELECT
    t.id,
    t.title,
    t.status,
    t.assigned_to,
    p.name as project_name,
    p.owner_id as project_owner,
    CASE
        WHEN t.assigned_to = p.owner_id THEN '✅ Owner'
        ELSE '👤 Other'
    END as assignment_type
FROM tasks t
JOIN projects p ON t.project_id = p.id
ORDER BY p.id, t.id;

-- Expected result: ALL tasks now have assigned_to value
-- Tasks that had NULL are now assigned to their project owner

