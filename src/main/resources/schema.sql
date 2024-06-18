create TABLE IF NOT EXISTS users (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  email VARCHAR(255) NOT NULL,
  login VARCHAR(40) NOT NULL,
  name VARCHAR(40) NULL,
  birthday DATE NOT NULL
);

create TABLE IF NOT EXISTS mpa (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(40) NOT NULL
);

create TABLE IF NOT EXISTS genres (
   id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name varchar(50) NOT NULL
);

create TABLE IF NOT EXISTS films (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(40) NOT NULL,
    description  VARCHAR(255) NOT NULL,
    release_date DATE NOT NULL,
    duration INT NOT NULL,
    mpa_id BIGINT NOT NULL REFERENCES mpa (id)
);

create TABLE IF NOT EXISTS likes (
    user_id BIGINT NOT NULL REFERENCES users (id),
    film_id BIGINT NOT NULL REFERENCES films (id),
    PRIMARY KEY (user_id, film_id)
);

create TABLE IF NOT EXISTS friends (
    user_id BIGINT NOT NULL REFERENCES users (id),
    friend_id BIGINT NOT NULL REFERENCES users (id),
    status boolean NOT NULL DEFAULT FALSE,
    PRIMARY KEY (user_id, friend_id)
);

create TABLE IF NOT EXISTS films_genres (
    film_id  BIGINT NOT NULL REFERENCES films (id),
    genre_id BIGINT NOT NULL REFERENCES genres (id),
    PRIMARY KEY (film_id, genre_id)
);