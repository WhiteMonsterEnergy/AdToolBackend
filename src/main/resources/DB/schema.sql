-- Drop + recreate database
DROP DATABASE IF EXISTS adtool;
CREATE DATABASE adtool
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_0900_ai_ci;

USE adtool;

-- -----------------------
-- USERS (ikke kald den "user" i MySQL)
-- -----------------------
CREATE TABLE users (
                       user_id        INT AUTO_INCREMENT PRIMARY KEY,
                       email          VARCHAR(255) NOT NULL UNIQUE,
                       password_hash  VARCHAR(255) NOT NULL,
                       name           VARCHAR(255) NOT NULL,
                       created_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- -----------------------
-- AD CAMPAIGN
-- -----------------------
CREATE TABLE ad_campaign (
                             campaign_id   INT AUTO_INCREMENT PRIMARY KEY,
                             user_id       INT NOT NULL,
                             title         VARCHAR(255) NOT NULL,
                             primary_text  TEXT,
                             status        VARCHAR(50) NOT NULL DEFAULT 'draft',
                             platform      VARCHAR(50) NOT NULL DEFAULT 'unknown',
                             created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             updated_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                             CONSTRAINT fk_campaign_user
                                 FOREIGN KEY (user_id) REFERENCES users(user_id)
                                     ON DELETE CASCADE
);

CREATE INDEX idx_campaign_user_id ON ad_campaign(user_id);

-- -----------------------
-- AUDIENCE SEGMENT
-- -----------------------
CREATE TABLE audience_segment (
                                  segment_id    INT AUTO_INCREMENT PRIMARY KEY,
                                  name          VARCHAR(255) NOT NULL,
                                  description   TEXT,
                                  age_min       INT,
                                  age_max       INT,
                                  location      VARCHAR(255)
);

-- -----------------------
-- CAMPAIGN <-> AUDIENCE (junction)
-- -----------------------
CREATE TABLE campaign_audience (
                                   campaign_id  INT NOT NULL,
                                   segment_id   INT NOT NULL,

                                   PRIMARY KEY (campaign_id, segment_id),

                                   CONSTRAINT fk_ca_campaign
                                       FOREIGN KEY (campaign_id) REFERENCES ad_campaign(campaign_id)
                                           ON DELETE CASCADE,

                                   CONSTRAINT fk_ca_segment
                                       FOREIGN KEY (segment_id) REFERENCES audience_segment(segment_id)
                                           ON DELETE CASCADE
);

CREATE INDEX idx_ca_segment_id ON campaign_audience(segment_id);

-- -----------------------
-- AD PROMPT
-- -----------------------
CREATE TABLE ad_prompt (
                           prompt_id    INT AUTO_INCREMENT PRIMARY KEY,
                           campaign_id  INT NOT NULL,
                           prompt_text  TEXT NOT NULL,
                           created_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

                           CONSTRAINT fk_prompt_campaign
                               FOREIGN KEY (campaign_id) REFERENCES ad_campaign(campaign_id)
                                   ON DELETE CASCADE
);

CREATE INDEX idx_prompt_campaign_id ON ad_prompt(campaign_id);

-- -----------------------
-- AD SUGGESTION
-- -----------------------
CREATE TABLE ad_suggestion (
                               suggestion_id  INT AUTO_INCREMENT PRIMARY KEY,
                               prompt_id      INT NOT NULL,
                               generated_text TEXT NOT NULL,
                               created_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               is_selected    BOOLEAN NOT NULL DEFAULT FALSE,

                               CONSTRAINT fk_suggestion_prompt
                                   FOREIGN KEY (prompt_id) REFERENCES ad_prompt(prompt_id)
                                       ON DELETE CASCADE
);

CREATE INDEX idx_suggestion_prompt_id ON ad_suggestion(prompt_id);
CREATE INDEX idx_suggestion_is_selected ON ad_suggestion(is_selected);

-- -----------------------
-- MEDIA ASSET
-- -----------------------
CREATE TABLE media_asset (
                             media_id     INT AUTO_INCREMENT PRIMARY KEY,
                             campaign_id  INT NOT NULL,
                             file_name    VARCHAR(255),
                             file_url     VARCHAR(500),
                             file_type    VARCHAR(50),
                             uploaded_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

                             CONSTRAINT fk_media_campaign
                                 FOREIGN KEY (campaign_id) REFERENCES ad_campaign(campaign_id)
                                     ON DELETE CASCADE
);

CREATE INDEX idx_media_campaign_id ON media_asset(campaign_id);

-- =========================================================
-- TESTDATA
-- =========================================================

-- USERS
INSERT INTO users (email, password_hash, name)
VALUES
    ('mads@test.dk', 'hash123', 'Mads'),
    ('anna@test.dk', 'hash456', 'Anna');

-- CAMPAIGNS (tilhører Mads = user_id 1)
INSERT INTO ad_campaign (user_id, title, primary_text, status, platform)
VALUES
    (1, 'Marmalade Winter Sale', 'Lagersalg på udvalgte produkter – kun i dag!', 'draft', 'instagram'),
    (1, 'Boom Butik Drop', 'Nye drops hver uge – limited stock!', 'active', 'tiktok');

-- AUDIENCE SEGMENTS
INSERT INTO audience_segment (name, description, age_min, age_max, location)
VALUES
    ('Women 18-30 DK', 'Kvinder 18-30 i Danmark, fashion/interesse', 18, 30, 'Denmark'),
    ('Students Copenhagen', 'Studerende i København, budgetvenlige køb', 18, 28, 'Copenhagen');

-- CAMPAIGN <-> AUDIENCE
-- campaign 1 -> segment 1 og 2
INSERT INTO campaign_audience (campaign_id, segment_id)
VALUES
    (1, 1),
    (1, 2);

-- PROMPTS (til campaign 1)
INSERT INTO ad_prompt (campaign_id, prompt_text)
VALUES
    (1, 'Lav en moderne pink/rød fashion-reklame til kvinder 18-30, med 60% rabat og “Kun i dag!!”'),
    (1, 'Skriv 3 korte overskrifter til en lagersalgs-annonce, som passer til Instagram.');

-- SUGGESTIONS (til prompt 1 og 2)
INSERT INTO ad_suggestion (prompt_id, generated_text, is_selected)
VALUES
    (1, 'LAGERSALG PÅ UDVALGTE PRODUKTER\nKun i dag!!\n60% rabat', TRUE),
    (1, 'Kun i dag: 60% på favoritterne', FALSE),
    (2, 'Lagersalg i dag – grab dine favoritter', TRUE),
    (2, '60% på udvalgte styles – kun i dag', FALSE);

-- MEDIA ASSETS (til campaign 1)
INSERT INTO media_asset (campaign_id, file_name, file_url, file_type)
VALUES
    (1, 'model_female_1.png', '/uploads/model_female_1.png', 'image/png'),
    (1, 'logo_marmalade.svg', '/uploads/logo_marmalade.svg', 'image/svg+xml');
