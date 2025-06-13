-- Einfügen von Benutzerdaten
INSERT INTO "user"
(first_name, last_name, nickname, user_role, email_address, password_hash, password_salt, hash_method, has_admin_verified,
 is_email_verified,
 country,
 city,
 postal_code,
 street,
 house_number,
 created_at)
VALUES ('John', 'Doe', 'john', 'USER', 'foeckerspe@fim.uni-passau.de', 'AVFHOicSIX9imsya3+1F7PNK7sNpKTyCqRTinyCw+DQ=', 'z5dlU2O9a/+AqqwoK74WfA==',
        'PBKDF2WithHmacSHA512', TRUE, TRUE, 'DE',
        'Musterstadt', '12345', 'Musterstraße', '17', NOW());


-- Einfügen von Anzeigen
INSERT INTO ad
(creator_id, title, free_text, cost_in_talent_points, date_of_publication, country, city, postal_code, email_address)
VALUES (1, 'Gitarrenunterricht', 'Biete professionellen Gitarrenunterricht an.', 10, NOW()::DATE, 'Germany', 'Berlin', '10115', 'admin@example.com'),
       (2, 'Mathenachhilfe', 'Biete Nachhilfe in Mathematik für Schüler.', 8, NOW()::DATE, 'Germany', 'Munich', '80331', 'john.doe@example.com'),
       (1, 'Yoga Kurse', 'Yoga Kurse für Anfänger und Fortgeschrittene.', 15, NOW()::DATE, 'Germany', 'Cologne', '50667', 'admin@example.com'),
       (2, 'Kochkurs', 'Italienischer Kochkurs für Pasta und Pizza.', 12, NOW()::DATE, 'Germany', 'Hamburg', '20099', 'john.doe@example.com'),
       (2, 'Sprachunterricht', 'Englisch und Französisch Unterricht verfügbar.', 9, NOW()::DATE, 'Germany', 'Frankfurt', '60313',
        'john.doe@example.com'),
       (2, 'Fotografie Workshop', 'Lerne die Grundlagen der Fotografie.', 20, NOW()::DATE, 'Germany', 'Dresden', '01067', 'john.doe@example.com'),
       (2, 'Hausaufgabenhilfe', 'Unterstützung bei Hausaufgaben aller Art für Grundschüler.', 5, NOW()::DATE, 'Germany', 'Stuttgart', '70173',
        'john.doe@example.com'),
       (2, 'Computerkurse', 'Einsteigerkurse für Senioren zum Umgang mit Computern.', 7, NOW()::DATE, 'Germany', 'Leipzig', '04109',
        'john.doe@example.com'),
       (2, 'Gartenpflege', 'Biete Dienste für Gartenarbeit und Landschaftsgestaltung.', 11, NOW()::DATE, 'Germany', 'Nuremberg', '90403',
        'john.doe@example.com'),
       (1, 'Autoreparatur', 'Reparaturdienste für Autos, spezialisiert auf ältere Modelle.', 16, NOW()::DATE, 'Germany', 'Bremen', '28195',
        'admin@example.com'),
       (1, 'Musikproduktion', 'Biete Musikproduktionskurse an, inklusive Aufnahmetechniken.', 18, NOW()::DATE, 'Germany', 'Hanover', '30159',
        'admin@example.com'),
       (1, 'Tanzkurse', 'Modern Dance und klassisches Ballett für Kinder und Erwachsene.', 14, NOW()::DATE, 'Germany', 'Dortmund', '44135',
        'admin@example.com'),
       (1, 'Haustierbetreuung', 'Betreuung und Gassi gehen für Hunde und Katzen.', 6, NOW()::DATE, 'Germany', 'Essen', '45127', 'admin@example.com'),
       (1, 'Künstlerworkshops', 'Workshops in verschiedenen Kunstformen, von Malerei bis Skulptur.', 13, NOW()::DATE, 'Germany', 'Düsseldorf',
        '40210', 'admin@example.com'),
       (1, 'Webdesign', 'Erstellung und Design von Websites, einschließlich SEO Optimierung.', 17, NOW()::DATE, 'Germany', 'Mainz', '55116',
        'admin@example.com');
