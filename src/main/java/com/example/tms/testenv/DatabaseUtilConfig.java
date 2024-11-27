package com.example.tms.testenv;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@Component
public class DatabaseUtilConfig implements CommandLineRunner {

    private final DataSource dataSource;
    public DatabaseUtilConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(String... args) throws Exception {
        try(Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement())
        {
            statement.execute("CREATE TABLE IF NOT EXISTS users\n" +
                    "(\n" +
                    "    id SERIAL PRIMARY KEY,\n" +
                    "    login CHARACTER(30),\n" +
                    "    nickname CHARACTER(30),\n" +
                    "    password CHARACTER(60)\n" +
                    ");\n" +
                    "\n" +
                    "CREATE TABLE IF NOT EXISTS roles\n" +
                    "(\n" +
                    "    id SERIAL PRIMARY KEY ,\n" +
                    "    name varchar(30)\n" +
                    ");\n" +
                    "\n" +
                    "CREATE TABLE IF NOT EXISTS tasks\n" +
                    "(\n" +
                    "    id SERIAL PRIMARY KEY,\n" +
                    "    header varchar(60),\n" +
                    "    description varchar(100),\n" +
                    "    status varchar(30),\n" +
                    "    priority varchar(30),\n" +
                    "    author_id bigint not null,\n"+
                    "    comment_id bigint not null,"+
                    "    FOREIGN KEY (author_id) references users(id)\n" +
                    ");\n" +
                    "\n" +
                    "CREATE TABLE IF NOT EXISTS comments\n" +
                    "(\n" +
                    "    id SERIAL PRIMARY KEY,\n" +
                    "    text varchar(100),\n" +
                    "    date date,\n" +
                    "    author_id bigint not null,\n" +
                    "    FOREIGN KEY(author_id) references users(id)"+
                    ");\n" +
                    "\n" +
                    "CREATE TABLE IF NOT EXISTS roles_x_users\n" +
                    "(\n" +
                    "    user_id bigint not null,\n" +
                    "    role_id bigint not null,\n" +
                    "    PRIMARY KEY(user_id, role_id),\n" +
                    "    FOREIGN KEY(user_id) references users(id),\n" +
                    "    FOREIGN KEY(role_id) references roles(id)\n" +
                    ");\n" +
                    "\n" +
                    "CREATE TABLE IF NOT EXISTS tasks_x_users\n" +
                    "(\n" +
                    "    task_id bigint not null,\n" +
                    "    executor_id bigint not null,\n" +
                    "    PRIMARY KEY(task_id, executor_id)," +
                    "    FOREIGN KEY(task_id) references tasks(id),"+
                    "    FOREIGN KEY(executor_id) references users(id)\n" +
                    ");\n" +
                    "\n" +
                    "CREATE TABLE IF NOT EXISTS tasks_comments"+
                    "(\n"+
                    "task_id bigint not null,\n"+
                    "comment_id bigint not null,\n"+
                    "PRIMARY KEY(task_id, comment_id),\n"+
                    "FOREIGN KEY(task_id) references tasks(id),\n"+
                    "FOREIGN KEY(comment_id) references comments(id)\n"+
                    ");\n" +
                    "\n" +
                    "CREATE TABLE IF NOT EXISTS comments_x_authors\n" +
                    "(\n" +
                    "    comment_id bigint not null,\n" +
                    "    author_id bigint not null,\n" +
                    "    PRIMARY KEY(author_id),\n" +
                    "    FOREIGN KEY(author_id) references users(id)\n" +
                    ");");
            statement.execute("INSERT INTO roles VALUES ('1', 'ROLE_ADMIN'),\n" +
                    "                         ('2', 'ROLE_USER');");


        }
    }

}
