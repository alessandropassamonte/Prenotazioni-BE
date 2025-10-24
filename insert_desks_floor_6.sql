-- Script di inserimento postazioni per il Piano 6
-- Inserisce desk dal numero 1 al 80

INSERT INTO desks (desk_number, floor_id, department_id, type, status, active, created_at, updated_at)
SELECT
    CAST(n AS VARCHAR),
    6,
    NULL,
    'STANDARD',
    'AVAILABLE',
    true,
    NOW(),
    NOW()
FROM generate_series(1, 80) AS n;

-- Verifica inserimento
SELECT COUNT(*) as total_desks_inserted
FROM desks
WHERE floor_id = 6;
-- Script di inserimento postazioni per il Piano 5
-- Inserisce desk dal numero 1 al 54

INSERT INTO desks (desk_number, floor_id, department_id, type, status, active, created_at, updated_at)
SELECT
    CAST(n AS VARCHAR),
    5,
    NULL,
    'STANDARD',
    'AVAILABLE',
    true,
    NOW(),
    NOW()
FROM generate_series(1, 54) AS n;

-- Verifica inserimento
SELECT COUNT(*) as total_desks_inserted
FROM desks
WHERE floor_id = 5;

