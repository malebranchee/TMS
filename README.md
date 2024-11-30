
# Task Management System API

## **API for controlling lifecycle of tasks** 

## Tech Stack

**Client:** Spring Boot v3.3.5, Java 17, Spring Security 6.3.4

**Database:** PostgreSQL (driver 42.7.2, server 17)

**Server:** Apache Tomcat 10.1.31

## Requirements

**- linux distributive;**

**- installed and configured PostgreSQL server;**

## Deployment

### Initialization

- #### [Install and configure](https://www.baeldung.com/linux/postgresql-install-configure) PostgreSQL server;
 
- #### Clone repository:

    ```git clone https://github.com/malebranchee/TMS```

- #### Execute shell script inserting your database credentials

    ```sh ./start.sh {username} {password}```

    #### Server  will be available on 
    ```localhost:8081``` 
    #### To visit swagger-ui ```localhost:8081/swagger-ui/index.html``` 

### Shutting down

#### Execute ```sh ./shutdown.sh```
