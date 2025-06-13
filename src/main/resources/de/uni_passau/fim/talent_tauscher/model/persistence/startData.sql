-- Insert Default Administrator
INSERT INTO "user"
(first_name,
 last_name,
 nickname,
 user_role,
 email_address,
 password_hash,
 password_salt,
 hash_method,
 has_admin_verified,
 is_email_verified,
 country,
 city,
 postal_code,
 created_at)
VALUES ('Admin',
        'User',
        'adminuser',
        'ADMIN',
        'admin@example.com',
        'AVFHOicSIX9imsya3+1F7PNK7sNpKTyCqRTinyCw+DQ=',
        'z5dlU2O9a/+AqqwoK74WfA==',
        'PBKDF2WithHmacSHA512',
        TRUE,
        TRUE,
        'Germany',
        'Berlin',
        '12345',
        NOW());

-- Insert Default System Settings
INSERT INTO system_settings
(css_name,
 logo,
 data_protection,
 max_pic_size,
 contact_info,
 is_admin_confirmation_needed_registration,
 imprint,
 sum_paginated_items)
VALUES ('styles.css',
        NULL,
        'Your data protection information here.',
        2048,
        'Contact us at support@example.com.',
        FALSE,
        'Imprint information here.',
        5);
