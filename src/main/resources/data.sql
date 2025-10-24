-- Script di inizializzazione database Desk Booking System
-- Basato sulle piantine Piano 1 e Piano 3

-- ========================================
-- INSERIMENTO PIANI
-- ========================================

-- Piano 1
INSERT INTO floors (name, floor_number, code, square_meters, total_desks, total_lockers, description, map_image_url, active, created_at, updated_at)
VALUES ('PRIMO PIANO', 1, 'MQ 673', 673, 74, 76, 'Primo piano con aree Trading, IT App, IT e Trading Editor/Marketing', 
        '/images/floors/piano_1.jpg', true, NOW(), NOW());

-- Piano 3
INSERT INTO floors (name, floor_number, code, square_meters, total_desks, total_lockers, description, map_image_url, active, created_at, updated_at)
VALUES ('TERZO PIANO', 3, 'MQ 673', 673, 80, 100, 'Terzo piano con aree IT Analytics, IT Infrastructure, DBA e Development', 
        '/images/floors/piano_3.jpg', true, NOW(), NOW());

-- ========================================
-- INSERIMENTO DIPARTIMENTI - PIANO 1
-- ========================================

INSERT INTO departments (name, code, total_desks, description, floor_id, active, created_at, updated_at)
VALUES 
    ('TRADING', 'TRAD', 28, 'Dipartimento Trading', 
     (SELECT id FROM floors WHERE floor_number = 1), true, NOW(), NOW()),
    
    ('IT APP', 'IT-APP', 10, 'Dipartimento IT Applicazioni', 
     (SELECT id FROM floors WHERE floor_number = 1), true, NOW(), NOW()),
    
    ('IT', 'IT', 14, 'Dipartimento IT Generale', 
     (SELECT id FROM floors WHERE floor_number = 1), true, NOW(), NOW()),
    
    ('TRAD. EDITOR - MKTG', 'TRAD-MKT', 22, 'Dipartimento Trading Editor e Marketing', 
     (SELECT id FROM floors WHERE floor_number = 1), true, NOW(), NOW());

-- ========================================
-- INSERIMENTO DIPARTIMENTI - PIANO 3
-- ========================================

INSERT INTO departments (name, code, total_desks, description, floor_id, active, created_at, updated_at)
VALUES 
    ('IT ANALYTICS - DEMAND', 'IT-ANA', 40, 'Dipartimento IT Analytics e Demand', 
     (SELECT id FROM floors WHERE floor_number = 3), true, NOW(), NOW()),
    
    ('IT INFRA - MID - DBA - DEV', 'IT-INF', 40, 'Dipartimento IT Infrastructure, Middleware, DBA e Development', 
     (SELECT id FROM floors WHERE floor_number = 3), true, NOW(), NOW());

-- ========================================
-- INSERIMENTO POSTAZIONI - PIANO 1
-- ========================================

-- Trading (postazioni 55-74, 1-4, 63-72)
-- Lato sinistro
INSERT INTO desks (desk_number, floor_id, department_id, type, status, active, created_at, updated_at)
SELECT 
    CAST(n AS VARCHAR),
    (SELECT id FROM floors WHERE floor_number = 1),
    (SELECT id FROM departments WHERE code = 'TRAD'),
    'STANDARD',
    'AVAILABLE',
    true,
    NOW(),
    NOW()
FROM generate_series(55, 74) AS n
WHERE n NOT IN (61, 62); -- Escludi numeri che potrebbero essere corridoi

-- Trading zona centrale-sinistra
INSERT INTO desks (desk_number, floor_id, department_id, type, status, active, created_at, updated_at)
SELECT 
    CAST(n AS VARCHAR),
    (SELECT id FROM floors WHERE floor_number = 1),
    (SELECT id FROM departments WHERE code = 'TRAD'),
    'STANDARD',
    'AVAILABLE',
    true,
    NOW(),
    NOW()
FROM generate_series(63, 72) AS n;

-- IT & IT APP (postazioni 17-40)
INSERT INTO desks (desk_number, floor_id, department_id, type, status, active, created_at, updated_at)
SELECT 
    CAST(n AS VARCHAR),
    (SELECT id FROM floors WHERE floor_number = 1),
    (SELECT id FROM departments WHERE code = CASE 
        WHEN n BETWEEN 17 AND 32 THEN 'IT-APP'
        ELSE 'IT'
    END),
    'STANDARD',
    'AVAILABLE',
    true,
    NOW(),
    NOW()
FROM generate_series(17, 40) AS n;

-- Trading Editor & MKTG (postazioni 1-16, parte superiore)
INSERT INTO desks (desk_number, floor_id, department_id, type, status, active, created_at, updated_at)
SELECT 
    CAST(n AS VARCHAR),
    (SELECT id FROM floors WHERE floor_number = 1),
    (SELECT id FROM departments WHERE code = 'TRAD-MKT'),
    'STANDARD',
    'AVAILABLE',
    true,
    NOW(),
    NOW()
FROM generate_series(1, 16) AS n;

-- ========================================
-- INSERIMENTO POSTAZIONI - PIANO 3
-- ========================================

-- IT Analytics - Demand (parte superiore destra)
INSERT INTO desks (desk_number, floor_id, department_id, type, status, active, created_at, updated_at)
SELECT 
    CAST(n AS VARCHAR),
    (SELECT id FROM floors WHERE floor_number = 3),
    (SELECT id FROM departments WHERE code = 'IT-ANA'),
    'STANDARD',
    'AVAILABLE',
    true,
    NOW(),
    NOW()
FROM generate_series(1, 22) AS n;

-- IT Infra - MID - DBA - DEV (parte destra e inferiore)
INSERT INTO desks (desk_number, floor_id, department_id, type, status, active, created_at, updated_at)
SELECT 
    CAST(n AS VARCHAR),
    (SELECT id FROM floors WHERE floor_number = 3),
    (SELECT id FROM departments WHERE code = 'IT-INF'),
    'STANDARD',
    'AVAILABLE',
    true,
    NOW(),
    NOW()
FROM generate_series(23, 42) AS n;

-- Postazioni lato sinistro Piano 3
INSERT INTO desks (desk_number, floor_id, department_id, type, status, active, created_at, updated_at)
SELECT 
    CAST(n AS VARCHAR),
    (SELECT id FROM floors WHERE floor_number = 3),
    (SELECT id FROM departments WHERE code = 'IT-INF'),
    'STANDARD',
    'AVAILABLE',
    true,
    NOW(),
    NOW()
FROM generate_series(63, 80) AS n;

-- Postazioni parte inferiore Piano 3
INSERT INTO desks (desk_number, floor_id, department_id, type, status, active, created_at, updated_at)
SELECT 
    CAST(n AS VARCHAR),
    (SELECT id FROM floors WHERE floor_number = 3),
    (SELECT id FROM departments WHERE code = CASE 
        WHEN n BETWEEN 43 AND 54 THEN 'IT-ANA'
        ELSE 'IT-INF'
    END),
    'STANDARD',
    'AVAILABLE',
    true,
    NOW(),
    NOW()
FROM generate_series(43, 62) AS n;

-- ========================================
-- INSERIMENTO LOCKER - PIANO 1
-- ========================================

-- Locker Turnisti Piano 1 (62 locker)
INSERT INTO lockers (locker_number, floor_id, type, status, active, created_at, updated_at)
SELECT 
    'T1-' || LPAD(CAST(n AS VARCHAR), 3, '0'),
    (SELECT id FROM floors WHERE floor_number = 1),
    'TURNISTI',
    'FREE',
    true,
    NOW(),
    NOW()
FROM generate_series(1, 62) AS n;

-- Locker Free Piano 1 (14 locker)
INSERT INTO lockers (locker_number, floor_id, type, status, active, created_at, updated_at)
SELECT 
    'F1-' || LPAD(CAST(n AS VARCHAR), 3, '0'),
    (SELECT id FROM floors WHERE floor_number = 1),
    'FREE',
    'FREE',
    true,
    NOW(),
    NOW()
FROM generate_series(1, 14) AS n;

-- ========================================
-- INSERIMENTO LOCKER - PIANO 3
-- ========================================

-- Locker Turnisti Piano 3 (6 locker)
INSERT INTO lockers (locker_number, floor_id, type, status, active, created_at, updated_at)
SELECT 
    'T3-' || LPAD(CAST(n AS VARCHAR), 3, '0'),
    (SELECT id FROM floors WHERE floor_number = 3),
    'TURNISTI',
    'FREE',
    true,
    NOW(),
    NOW()
FROM generate_series(1, 6) AS n;

-- Locker Free Piano 3 (94 locker)
INSERT INTO lockers (locker_number, floor_id, type, status, active, created_at, updated_at)
SELECT 
    'F3-' || LPAD(CAST(n AS VARCHAR), 3, '0'),
    (SELECT id FROM floors WHERE floor_number = 3),
    'FREE',
    'FREE',
    true,
    NOW(),
    NOW()
FROM generate_series(1, 94) AS n;

-- ========================================
-- UTENTI DI TEST
-- ========================================

-- Admin user
INSERT INTO users (email, first_name, last_name, employee_id, password, department_id, role, work_type, phone_number, active, email_verified, created_at, updated_at)
VALUES 
    ('admin@company.it', 'Admin', 'System', 'EMP001', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 
     (SELECT id FROM departments WHERE code = 'IT' LIMIT 1), 'ADMIN', 'STANDARD', '+39 333 1234567', true, true, NOW(), NOW()),

-- Manager user
    ('manager.trading@company.it', 'Marco', 'Rossi', 'EMP002', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 
     (SELECT id FROM departments WHERE code = 'TRAD'), 'MANAGER', 'STANDARD', '+39 333 2234567', true, true, NOW(), NOW()),

-- Regular users
    ('user.it@company.it', 'Luigi', 'Bianchi', 'EMP003', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 
     (SELECT id FROM departments WHERE code = 'IT-APP'), 'USER', 'HYBRID', '+39 333 3234567', true, true, NOW(), NOW()),

    ('turnista@company.it', 'Giuseppe', 'Verdi', 'EMP004', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 
     (SELECT id FROM departments WHERE code = 'TRAD'), 'USER', 'TURNISTA', '+39 333 4234567', true, true, NOW(), NOW());

-- Password per tutti gli utenti di test: "password123"

COMMIT;
