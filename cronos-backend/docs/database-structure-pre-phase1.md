# Current Database Structure - Pre-Phase 1

**Date:** 2025-12-06  
**Branch:** feature/phase-1-data-model  
**Purpose:** Document existing schema before Phase 1 migrations

---

## Existing Tables

### 1. users
**Columns:**
- `id` (UUID, PK)
- `first_name` (VARCHAR)
- `last_name` (VARCHAR)
- `email` (VARCHAR, UNIQUE)
- `password` (VARCHAR - bcrypt hashed)
- `role` (VARCHAR - e.g., "USER", "ADMIN")
- `tenant_id` (UUID, FK → tenants)
- `created_at` (TIMESTAMP)
- `updated_at` (TIMESTAMP)

**Relationships:**
- ManyToOne → tenants

---

### 2. tenants
**Columns:**
- `id` (UUID, PK)
- `name` (VARCHAR)
- `slug` (VARCHAR, UNIQUE)
- `created_at` (TIMESTAMP)
- `updated_at` (TIMESTAMP)

**Relationships:**
- OneToMany → users
- OneToMany → services
- OneToMany → appointments

---

### 3. services
**Columns:**
- `id` (UUID, PK)
- `tenant_id` (UUID, FK → tenants)
- `name` (VARCHAR)
- `description` (TEXT)
- `duration` (INTEGER - minutes)
- `price` (DECIMAL)
- `is_active` (BOOLEAN)
- `created_at` (TIMESTAMP)
- `updated_at` (TIMESTAMP)

**Relationships:**
- ManyToOne → tenants

---

### 4. appointments
**Columns:**
- `id` (UUID, PK)
- `tenant_id` (UUID, FK → tenants)
- `service_id` (UUID, FK → services)
- `start_time` (TIMESTAMP)
- `end_time` (TIMESTAMP)
- `status` (VARCHAR)
- `notes` (TEXT)
- `created_at` (TIMESTAMP)
- `updated_at` (TIMESTAMP)

**Relationships:**
- ManyToOne → tenants
- ManyToOne → services

---

## Schema Managed By

Currently using **Hibernate DDL Auto** (`spring.jpa.hibernate.ddl-auto=update`)

**Changes in Phase 1:**
- Switch to **Flyway** for migrations
- Change to `spring.jpa.hibernate.ddl-auto=validate`

---

## Data to Preserve

### Production Data (if any)
- Users table
- Tenants table
- Services table
- Appointments table

### Migration Strategy
1. Flyway will baseline on existing schema
2. Use `spring.flyway.baseline-on-migrate=true`
3. First migration (V1) will create new tables only
4. Existing tables will be altered in subsequent migrations if needed

---

## Notes

- No foreign key constraints currently enforced at DB level (managed by Hibernate)
- No explicit indices beyond primary keys
- Tenant isolation enforced at application level, not DB level

**Phase 1 will add:**
- Explicit foreign key constraints
- Composite indices on (tenant_id, created_at)
- Check constraints for business rules
- 9 new tables
