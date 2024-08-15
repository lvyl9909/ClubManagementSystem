
### Prerequisites

Ensure you have the following software installed on your system:

- [JDK 17](https://www.oracle.com/java/technologies/javase-jdk17-downloads.html)
- [Maven](https://maven.apache.org/install.html)
- [PostgreSQL](https://www.postgresql.org/download/)
- [Tomcat 10.1.26](https://tomcat.apache.org/download-10.cgi)

## Tech Stack

- **JDK 17**: Used for compiling and running Java code.
- **Jakarta EE 9.1**: Framework for building enterprise Java applications.
- **Maven**: Project build and dependency management tool.
- **PostgreSQL**: Relational database management system.
- **Tomcat 10.1.26**: Application server for deploying and running web applications.
- **Servlet**: Java programs used for handling HTTP requests and responses.

## Usage
### Database
You can find a database_dump.sql file in the backend folder, which is our test database file. You need to run the following command in the directory’s command line to create the corresponding database.

`pg_dump -U username -h hostname -p port -d databasename -F p -f /path/to/your/backup.sql ` 

After modifying the database, you can also run the following command to save the new database script file.

`psql -U username -h hostname -p port -f /path/to/your/backup.sql`

you can also connect to our online database:

**host**:dpg-cqqa5sjv2p9s73b4fi2g-a.singapore-postgres.render.com

**username**: swen90007_teamy_owner

**password**: MmDETsMioPzOVdhSJoB4T3wwrxD1ElGH

**database**:swen90007_teamy


### Tomcat

To deploy the application using Tomcat 10.1.26:

1. **Download Tomcat**: If you haven't already, download Tomcat 10.1.26 from the [official website](https://tomcat.apache.org/download-10.cgi) and extract it to a preferred directory.

2. **Configure Tomcat in IDE**:
    - Go to your IDE's Run/Debug Configurations.
    - Select the downloaded Tomcat version as the server.
    - In the deployment section, add the `backend_war` artifact.


### Set Up Environment Variables

To run the project correctly, please follow these steps to add the necessary configurations in the virtual machine options:

Open the project configuration file.
In the virtual machine options section, add the following content:

   ```bash
   DATABASE_URL=postgresql://swen90007_teamy_owner:MmDETsMioPzOVdhSJoB4T3wwrxD1ElGH@dpg-cqqa5sjv2p9s73b4fi2g-a.singapore-postgres.render.com:5432/swen90007_teamy
   JDK_HOME=/path/to/your/jdk17
   MAVEN_HOME=/path/to/your/maven
   TOMCAT_HOME=/path/to/your/tomcat-10.1.26
   ```

### Starting the Back-End

To start the back-end server, follow these steps:

1. **Build the Project**:
    - Ensure you have Maven installed and configured.
    - Navigate to the root directory of the project.
    - Run the following command to clean and build the project:

      ```bash
      mvn clean install
      ```

2. **Start the Server**

   **Start Tomcat**:
   - Open a terminal and navigate to the `bin` directory of your Tomcat installation:

     ```bash
     cd $CATALINA_HOME/bin
     ```

   - Start Tomcat using the `startup.sh` script:

     ```bash
     ./startup.sh
     ```

   - On Windows, use the `startup.bat` script instead:

     ```bash
     startup.bat
     ```

   **Start Tomcat from the IDE**:
   - **Configure Tomcat**:
      - Open your IDE (such as IntelliJ IDEA or Eclipse).
      - Navigate to the Run/Debug Configurations.
      - Add a new configuration for Tomcat.
      - Select the Tomcat version (10.1.26) that you have downloaded and installed.
      - Set the deployment artifact to `backend_war` (the WAR file you built).

   - **Run Tomcat**:
      - Once configured, you can start Tomcat directly from the IDE.
      - Click on the Run button (often a green triangle) or the Debug button (a bug icon) to start Tomcat.
      - The IDE will handle starting Tomcat, deploying your application, and opening the appropriate browser tab.

   **Verify the Server**:
   - Once Tomcat is running, you can check the server logs to ensure there are no errors. Logs are typically found in the `logs` directory under your Tomcat installation.
   - Access the application by opening a web browser and navigating to:

     ```
     http://localhost:8080/ClubManagement
     ```
     
