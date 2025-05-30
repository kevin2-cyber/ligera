/*
 * Test data for Ligera Backend
 * 
 * This script inserts test users and data for development and testing purposes.
 * The passwords are BCrypt encoded with the following original values:
 * - Admin: password123
 * - Regular: password123
 * - Test: password123
 *
 * DO NOT USE THESE CREDENTIALS IN PRODUCTION!
 */

-- Test Users
-- Note: BCrypt encoded passwords are used for security
INSERT INTO users (name, email, password, role, account_status, created_at, updated_at)
VALUES
    -- Admin User
    ('Admin User', 'admin@ligera.com', 
     '$2a$10$FnkNozkIQoWTbVxCBjZJQ.MDhJVXZhQUx9/vGW/FG5uJbXLnH6XuC', -- password123
     'ADMIN', 'ACTIVE', NOW(), NOW()),
     
    -- Regular User
    ('Regular User', 'user@ligera.com', 
     '$2a$10$FnkNozkIQoWTbVxCBjZJQ.MDhJVXZhQUx9/vGW/FG5uJbXLnH6XuC', -- password123
     'USER', 'ACTIVE', NOW(), NOW()),
     
    -- Test User
    ('Test User', 'test@ligera.com', 
     '$2a$10$FnkNozkIQoWTbVxCBjZJQ.MDhJVXZhQUx9/vGW/FG5uJbXLnH6XuC', -- password123
     'USER', 'ACTIVE', NOW(), NOW())
ON CONFLICT (email) DO NOTHING;

-- Assign roles to users
-- Get the role IDs
DO $$
DECLARE
    admin_role_id BIGINT;
    user_role_id BIGINT;
    admin_user_id BIGINT;
    regular_user_id BIGINT;
    test_user_id BIGINT;
BEGIN
    -- Get role IDs
    SELECT id INTO admin_role_id FROM roles WHERE name = 'ADMIN';
    SELECT id INTO user_role_id FROM roles WHERE name = 'USER';
    
    -- Get user IDs
    SELECT id INTO admin_user_id FROM users WHERE email = 'admin@ligera.com';
    SELECT id INTO regular_user_id FROM users WHERE email = 'user@ligera.com';
    SELECT id INTO test_user_id FROM users WHERE email = 'test@ligera.com';
    
    -- Assign admin role to admin user
    IF admin_role_id IS NOT NULL AND admin_user_id IS NOT NULL THEN
        INSERT INTO user_roles (user_id, role_id)
        VALUES (admin_user_id, admin_role_id)
        ON CONFLICT (user_id, role_id) DO NOTHING;
        
        -- Also give admin the user role
        INSERT INTO user_roles (user_id, role_id)
        VALUES (admin_user_id, user_role_id)
        ON CONFLICT (user_id, role_id) DO NOTHING;
    END IF;
    
    -- Assign user role to regular user
    IF user_role_id IS NOT NULL AND regular_user_id IS NOT NULL THEN
        INSERT INTO user_roles (user_id, role_id)
        VALUES (regular_user_id, user_role_id)
        ON CONFLICT (user_id, role_id) DO NOTHING;
    END IF;
    
    -- Assign user role to test user
    IF user_role_id IS NOT NULL AND test_user_id IS NOT NULL THEN
        INSERT INTO user_roles (user_id, role_id)
        VALUES (test_user_id, user_role_id)
        ON CONFLICT (user_id, role_id) DO NOTHING;
    END IF;
END $$;

/*
 * Development Usage:
 * 
 * 1. Admin Login:
 *    - Email: admin@ligera.com
 *    - Password: password123
 * 
 * 2. Regular User Login:
 *    - Email: user@ligera.com
 *    - Password: password123
 * 
 * 3. Test User Login:
 *    - Email: test@ligera.com
 *    - Password: password123
 * 
 * These accounts can be used for testing different access levels and permissions
 * in the application during development.
 */

