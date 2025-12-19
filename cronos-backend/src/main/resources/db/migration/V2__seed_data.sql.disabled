-- Cronos Platform - Seed Data for Development
-- Version: 2
-- Description: Sample data for testing and development
-- NOTE: This migration should only run in development/local environments

-- ============================================================================
-- CONDITIONAL EXECUTION (PostgreSQL)
-- Only run if we're in a development environment
-- ============================================================================

DO $$
BEGIN
    -- Check if we should run seed data (you can set this via environment variable)
    -- For now, we'll check if there are no tenants (fresh database)
    IF NOT EXISTS (SELECT 1 FROM tenants LIMIT 1) THEN
        
        -- ========================================================================
        -- SAMPLE TENANT
        -- ========================================================================
        INSERT INTO tenants (id, name, slug, status, primary_color, logo_url, work_day_start, work_day_end, created_at, updated_at)
        VALUES (
            'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11',
            'Demo Salon & Spa',
            'demo-salon',
            'ACTIVE',
            '#6366f1',
            'https://example.com/logo.png',
            '09:00:00',
            '18:00:00',
            NOW(),
            NOW()
        );

        -- ========================================================================
        -- SAMPLE SUBSCRIPTION
        -- ========================================================================
        INSERT INTO subscriptions (id, tenant_id, plan, status, start_date, end_date, max_staff, max_services, max_appointments_per_month, features, created_at, updated_at)
        VALUES (
            'b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11',
            'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11',
            'PRO',
            'ACTIVE',
            CURRENT_DATE,
            CURRENT_DATE + INTERVAL '1 year',
            10,
            50,
            1000,
            '{"online_booking": true, "email_notifications": true, "sms_notifications": false, "custom_branding": true}',
            NOW(),
            NOW()
        );

        -- ========================================================================
        -- SAMPLE USERS
        -- ========================================================================
        -- Admin user (password: Admin@123)
        INSERT INTO users (id, tenant_id, email, password, first_name, last_name, role, created_at, updated_at)
        VALUES (
            'c1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11',
            'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11',
            'admin@demosalon.com',
            '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', -- Admin@123
            'Admin',
            'User',
            'ADMIN',
            NOW(),
            NOW()
        );

        -- Staff user (password: Staff@123)
        INSERT INTO users (id, tenant_id, email, password, first_name, last_name, role, created_at, updated_at)
        VALUES (
            'd1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11',
            'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11',
            'sarah@demosalon.com',
            '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', -- Staff@123
            'Sarah',
            'Johnson',
            'STAFF',
            NOW(),
            NOW()
        );

        -- ========================================================================
        -- SAMPLE SERVICE CATEGORIES
        -- ========================================================================
        INSERT INTO service_categories (id, tenant_id, name, description, display_order, created_at, updated_at)
        VALUES 
            ('e1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'Hair Services', 'Professional hair cutting, styling, and coloring', 1, NOW(), NOW()),
            ('e2eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'Spa Services', 'Relaxing spa treatments and massages', 2, NOW(), NOW()),
            ('e3eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'Nail Services', 'Manicures, pedicures, and nail art', 3, NOW(), NOW());

        -- ========================================================================
        -- SAMPLE SERVICES
        -- ========================================================================
        INSERT INTO services (id, tenant_id, category_id, name, description, duration, price, is_active, created_at, updated_at)
        VALUES 
            ('f1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'e1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'Haircut', 'Professional haircut and styling', 45, 35.00, true, NOW(), NOW()),
            ('f2eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'e1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'Hair Coloring', 'Full hair coloring service', 120, 85.00, true, NOW(), NOW()),
            ('f3eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'e2eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'Swedish Massage', '60-minute relaxing massage', 60, 75.00, true, NOW(), NOW()),
            ('f4eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'e3eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'Manicure', 'Classic manicure', 30, 25.00, true, NOW(), NOW()),
            ('f5eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'e3eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'Pedicure', 'Relaxing pedicure', 45, 35.00, true, NOW(), NOW());

        -- ========================================================================
        -- SAMPLE STAFF
        -- ========================================================================
        INSERT INTO staff (id, tenant_id, user_id, position, bio, photo_url, is_active, created_at, updated_at)
        VALUES (
            'g1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11',
            'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11',
            'd1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11',
            'Senior Stylist',
            'Experienced hair stylist with 10+ years in the industry',
            'https://example.com/sarah.jpg',
            true,
            NOW(),
            NOW()
        );

        -- ========================================================================
        -- STAFF-SERVICE ASSIGNMENTS
        -- ========================================================================
        INSERT INTO staff_services (staff_id, service_id)
        VALUES 
            ('g1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'f1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),
            ('g1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'f2eebc99-9c0b-4ef8-bb6d-6bb9bd380a11');

        -- ========================================================================
        -- SAMPLE CLIENTS
        -- ========================================================================
        INSERT INTO clients (id, tenant_id, user_id, first_name, last_name, email, phone, notes, preferences, created_at, updated_at)
        VALUES 
            (
                'h1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11',
                'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11',
                NULL,
                'John',
                'Doe',
                'john.doe@example.com',
                '+1-555-0100',
                'Regular customer, prefers morning appointments',
                '{"communicationChannel": "EMAIL", "reminderEnabled": true, "reminderHours": 24, "language": "en", "timezone": "America/New_York"}',
                NOW(),
                NOW()
            ),
            (
                'h2eebc99-9c0b-4ef8-bb6d-6bb9bd380a11',
                'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11',
                NULL,
                'Jane',
                'Smith',
                'jane.smith@example.com',
                '+1-555-0101',
                'VIP client',
                '{"communicationChannel": "BOTH", "reminderEnabled": true, "reminderHours": 48, "language": "en", "timezone": "America/New_York"}',
                NOW(),
                NOW()
            );

        -- ========================================================================
        -- SAMPLE BUSINESS HOURS
        -- ========================================================================
        INSERT INTO business_hours (id, tenant_id, day_of_week, open_time, close_time, is_open, is_closed)
        VALUES 
            (gen_random_uuid(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'MONDAY', '09:00:00', '18:00:00', true, false),
            (gen_random_uuid(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'TUESDAY', '09:00:00', '18:00:00', true, false),
            (gen_random_uuid(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'WEDNESDAY', '09:00:00', '18:00:00', true, false),
            (gen_random_uuid(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'THURSDAY', '09:00:00', '18:00:00', true, false),
            (gen_random_uuid(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'FRIDAY', '09:00:00', '18:00:00', true, false),
            (gen_random_uuid(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'SATURDAY', '10:00:00', '16:00:00', true, false),
            (gen_random_uuid(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'SUNDAY', NULL, NULL, false, false);

        -- ========================================================================
        -- SAMPLE APPOINTMENTS
        -- ========================================================================
        INSERT INTO appointments (id, tenant_id, user_id, service_id, staff_id, client_id, start_time, end_time, status, created_at, updated_at)
        VALUES (
            'i1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11',
            'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11',
            NULL,
            'f1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11',
            'g1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11',
            'h1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11',
            NOW() + INTERVAL '2 days' + INTERVAL '10 hours',
            NOW() + INTERVAL '2 days' + INTERVAL '10 hours 45 minutes',
            'CONFIRMED',
            NOW(),
            NOW()
        );

        RAISE NOTICE 'Seed data inserted successfully for development environment';
    ELSE
        RAISE NOTICE 'Tenants already exist - skipping seed data insertion';
    END IF;
END $$;
