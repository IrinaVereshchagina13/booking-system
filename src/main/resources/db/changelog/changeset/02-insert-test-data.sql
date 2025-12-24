INSERT INTO cinemas (name, city) VALUES
     ('Cinema Park', 'Moscow'),
     ('Karo Film', 'Saint Petersburg'),
     ('Formula Kino', 'Novosibirsk');

INSERT INTO halls (name, cinema_id)
SELECT
    'Hall ' || hall_num || ' (' || color || ')',
    c.id
FROM cinemas c
CROSS JOIN (
    VALUES (1, 'Red'), (2, 'Blue'), (3, 'Green')
) AS t(hall_num, color);

INSERT INTO seats (hall_id, row_number, seat_number)
SELECT
    h.id,
    r,
    s
FROM halls h
CROSS JOIN generate_series(1, 8) r
CROSS JOIN generate_series(1, 12) s;

INSERT INTO movies (title, description, duration_minutes) VALUES
('Inception', 'A thief who steals corporate secrets through the use of dream-sharing technology.', 148),
('The Matrix', 'A computer hacker learns from mysterious rebels about the true nature of his reality.', 136),
('Interstellar', 'A team of explorers travel through a wormhole in space in an attempt to ensure humanity''s survival.', 169),
('The Dark Knight', 'When the menace known as the Joker wreaks havoc and chaos on the people of Gotham.', 152),
('Pulp Fiction', 'The lives of two mob hitmen, a boxer, a gangster and his wife intertwine.', 154),
('Forrest Gump', 'The presidencies of Kennedy and Johnson, the Vietnam War, the Watergate scandal and other historical events unfold.', 142),
('Fight Club', 'An insomniac office worker and a devil-may-care soap maker form an underground fight club.', 139),
('The Lion King', 'Lion prince Simba and his father are targeted by his bitter uncle, who wants to ascend the throne himself.', 88),
('Titanic', 'A seventeen-year-old aristocrat falls in love with a kind but poor artist aboard the luxurious, ill-fated R.M.S. Titanic.', 195),
('Avatar', 'A paraplegic Marine dispatched to the moon Pandora on a unique mission becomes torn between following his orders and protecting the world he feels is his home.', 162);


INSERT INTO sessions (hall_id, movie_id, start_time, price)
SELECT
    h.id,
    (SELECT id FROM movies ORDER BY random() LIMIT 1),
    NOW() + (random() * (INTERVAL '7 days')) + (random() * (INTERVAL '10 hours')),
    (ARRAY[300, 400, 500, 700])[floor(random() * 4 + 1)]
FROM halls h
    CROSS JOIN generate_series(1, 10);