-- ============================================================================
-- Mallcom: Role-Based Authentication - SQL changes
-- ============================================================================
-- Hibernate (spring.jpa.hibernate.ddl-auto=update) will automatically:
--   - create the `role` column on `users` when the app starts, because the
--     User entity now declares it (see entity/User.java)
--   - create the `products` table if it doesn't already exist (Product entity
--     already existed in the project)
--
-- Run this script by hand ONLY if you prefer explicit control, or if you have
-- existing rows in `users` from before this change (ddl-auto=update adds the
-- column but does NOT backfill a role for rows that predate it, and the
-- column is NOT NULL, so old rows without a role will fail to load).
-- ============================================================================

-- 1. Add the role column (skip if Hibernate already added it on startup)
ALTER TABLE users
  ADD COLUMN IF NOT EXISTS role VARCHAR(20) NOT NULL DEFAULT 'USER';

-- 2. Backfill any pre-existing rows that don't have a role yet
UPDATE users SET role = 'USER' WHERE role IS NULL OR role = '';

-- 3. Create your ADMIN account by hand (never through /api/auth/register).
--    The password below is BCrypt-hashed for the plaintext "Admin@123" —
--    change the email/name, and generate your own hash for a real deployment
--    (e.g. via `passlib`/`bcrypt` in Python, or any BCrypt generator, at
--    strength/cost factor 10 to match Spring's default BCryptPasswordEncoder).
INSERT INTO users (full_name, email, password, role)
VALUES (
  'Admin User',
  'admin@mallcom.com',
  '$2b$10$iBXhtAcZf0Q1IJUc1A.e2OpW.Ci8BxDfHSL6nWbl4TSqUYn73QKqu', -- Admin@123
  'ADMIN'
);

-- 4. (Optional) Products table reference — Hibernate creates this
--    automatically from entity/Product.java, shown here for documentation:
-- CREATE TABLE IF NOT EXISTS products (
--   id BIGINT AUTO_INCREMENT PRIMARY KEY,
--   name VARCHAR(255) NOT NULL,
--   description VARCHAR(1000),
--   price DECIMAL(19,2) NOT NULL,
--   category VARCHAR(255) NOT NULL,
--   image VARCHAR(255)
-- );
