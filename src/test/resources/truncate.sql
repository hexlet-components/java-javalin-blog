TRUNCATE TABLE article RESTART IDENTITY;

INSERT INTO article
  (name, description, created_at, updated_at)
VALUES
  ('The Man Within', 'Every flight begins with a fall.', '2022-01-01 13:57:40', '2022-02-21 12:00:40'),
  ('Consider the Lilies', 'The things I do for love.', '2022-01-01 13:57:40', '2022-02-21 12:00:40');
