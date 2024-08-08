
### Prerequisites

Ensure you have the following software installed on your system:

- [JDK 17](https://www.oracle.com/java/technologies/javase-jdk17-downloads.html)
- [Maven](https://maven.apache.org/install.html)
- [MySQL](https://dev.mysql.com/downloads/mysql/)
- [Tomcat 10.1.26](https://tomcat.apache.org/download-10.cgi)

## Tech Stack

- **JDK 17**: Used for compiling and running Java code.
- **Jakarta EE 9.1**: Framework for building enterprise Java applications.
- **Maven**: Project build and dependency management tool.
- **MySQL**: Relational database management system.
- **Tomcat 10.1.26**: Application server for deploying and running web applications.
- **Servlet**: Java programs used for handling HTTP requests and responses.

## Usage
### Database
You can find a database_dump.sql file in the backend folder, which is our test database file. You need to run the following command in the directory’s command line to create the corresponding database.

`pg_dump -U username -h hostname -p port -d databasename -F p -f /path/to/your/backup.sql ` 

After modifying the database, you can also run the following command to save the new database script file.

`psql -U username -h hostname -p port -f /path/to/your/backup.sql`

And for now, our test database user and password are hardcoded.

**username**: root

**password**: 123456

### Tomcat
