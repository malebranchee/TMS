CREATE TABLE IF NOT EXISTS users
(
    id SERIAL PRIMARY KEY,
    login CHARACTER(30),
    nickname CHARACTER(30),
    password CHARACTER(60)
);

CREATE TABLE IF NOT EXISTS roles
(
    id SERIAL PRIMARY KEY ,
    name varchar(30)
);

CREATE TABLE IF NOT EXISTS tasks
(
    id SERIAL PRIMARY KEY,
    header varchar(60),
    description varchar(100),
    status varchar(30),
    priority varchar(30),
    comments varchar(100)
);

CREATE TABLE IF NOT EXISTS comments
(
    id SERIAL PRIMARY KEY,
    author varchar(30),
    text varchar(100),
    date date
);

CREATE TABLE IF NOT EXISTS roles_x_users
(
    user_id bigint not null,
    role_id bigint not null,
    PRIMARY KEY(user_id, role_id),
    FOREIGN KEY(user_id) references users(id),
    FOREIGN KEY(role_id) references roles(id)
);

CREATE TABLE IF NOT EXISTS tasks_x_users
(
    task_id bigint not null,
    executor_id bigint not null,
    author_id bigint not null,
    comment_id bigint not null,
    PRIMARY KEY(task_id),
    FOREIGN KEY(executor_id) references users(id),
    FOREIGN KEY(author_id) references users(id),
    FOREIGN KEY(comment_id) references comments(id)
);

CREATE TABLE IF NOT EXISTS comments_x_authors
(
    comment_id bigint not null,
    author_id bigint not null,
    PRIMARY KEY(comment_id),
    FOREIGN KEY(author_id) references users(id)
);
