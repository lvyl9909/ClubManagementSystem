# We are Team Y!

**Team Member:**

| Name         | Student ID | Email                          |
|--------------|------------|-------------------------------|
| Yang Xu      | 1491561    | xuyx12@student.unimelb.edu.au |
| Yun Chen     | 1412174    | yunchen6@student.unimelb.edu.au |
| Yuyuan Zhang | 1435712    | yuyuzhang1@student.unimelb.edu.au |
| Yilin Lyu    | 1429919    | yillyu1@student.unimelb.edu.au |

**Our repository structure:**

```
├── docs
│   ├── meeting-notes
│   ├── part1
│   ├── part2
│   ├── part3
│   └── part4
├── ClubManagement
│   ├── .idea
│   ├── backend
│   │   ├── src
│   │   │   ├── main
│   │   │   │   ├── java
│   │   │   │   │   ├── org.teamy.backend
│   │   │   │   │   │   ├── config
│   │   │   │   │   │   ├── controller
│   │   │   │   │   │   ├── dao
│   │   │   │   │   │   ├── model
│   │   │   │   │   │   ├── service
│   │   │   │   │   │   └── HelloServlet.java
│   │   │   │   ├── resources
│   │   │   │   │   ├── META-INF
│   │   │   │   │   │   ├── beans.xml
│   │   │   │   │   │   └── persistence.xml
│   │   │   │   └── webapp
│   │   │   │   │   ├── index.jsp
│   │   │   │   │   ├── WEB-INF
│   │   │   │   └── └── └── web.xml
│   │   │   └── test
│   │   │   │   ├── java
│   │   │   └── └── resources
│   │   ├── target
│   │   ├── database_dump.sql
│   │   ├── mvnw.cmd
│   │   ├── pom.xml
│   │   └── README.md
│   └── frontend
│   │   ├── src
│   │   ├── public
│   │   ├── node_modules
│   │   ├── package.json
│   │   ├── package-lock.json
│   │   └── README.md
└── README.md
```
**Changelog:**

## Data Sample

### Introduction
This system is designed to manage student clubs, events, and funding applications. The data is stored in a relational database, and each table represents a different part of the system, including users, clubs, events, and applications.

### Data Structure

### User Credentials
To test the system, you can use the following pre-defined usernames and passwords. These users have already been created in the sample data:

| Username   | Password   | Note         |
|------------|------------|--------------|
| username1  | password1  | Club admin   |
| username2  | password2  | Club admin   |
| username3  | password3  | Club admin   |
| username4  | password4  | Club admin   |
| username5  | password5  | Club admin   |
| username6  | password6  | Club admin   |
| username7  | password7  | Club admin   |
| username8  | password8  | Club admin   |
| username9  | password9  | Club admin   |
| username10 | password10 | Faculty admin|

Simply log in using any of these credentials through the `/login` page or API to access the system's features.

### Viewing Personal Information on Home Page
After successfully logging in, you will be redirected to the **Home** page (`/home`). On this page, you will be able to view your personal information, which includes the following details:

- **Name**: Your full name.
- **Username**: Your unique username.
- **Email**: The email address associated with your account.

### Viewing and Managing Club Information
After logging in, you can view the clubs you manage by clicking on the **Club** section in the navigation menu.

#### Steps to View and Manage Clubs:
1. From the **Home** page, click on the **Club** section in the navigation menu.
2. This will display a list of clubs you are managing. For each club, you can view the following information:
    - **Club Name**: The name of the club.
    - **Description**: A brief description of the club.

3. Next to each club, there is a **Manage** button. Clicking this button will allow you to perform the following actions:
    - **Add/Remove Members**: Manage the list of members in the club.
    - **View Club Events**: See the events associated with this club.
    - **View Club Funding**: See the funding created by this club.

#### Steps to View Events:
1. Click on the **Event** section in the sidebar.
2. This will display two tabs:
    - **My Events**: This tab shows events that you are personally managing or are a part of.
    - **All Events**: This tab displays a list of all the events available in the system, regardless of your role in them.

For each event, you can see details such as:
- **Event Title**: The name of the event.
- **Date and Time**: The scheduled date and time of the event.
- **Venue**: The location where the event will take place.
- **Club**: The club that is organizing the event.
- **Description**: A brief description of the event.

#### Steps to Get a Ticket:
1. From the **Event** section (either **My Events** or **All Events**), locate the event you are interested in.
2. Click the **Get Ticket** button to attempt to secure a ticket for the event.

### Logging Out of the System
To end your session, you can easily log out by clicking the **Logout** button in the navigation bar.


## Example: User "yunchen6"

To better understand how to use the system, here’s a concrete example:

### User Credentials for Example
In this scenario, the user will log in with the following credentials:

| Username   | Password        |
|------------|-----------------|
| yunchen6   | passwordyunchen  |

### Club Details
This user, **yunchen6**, is a part of one club. The club has two administrators and contains the following information:

- **Club Name**: Formula 1 Club
- **Description**: A club for F1 enthusiasts
- **Administrators**:
   1. **yunchen6**
   2. **adminuser2** (Alice Johnson)

### Event Details
The **Tech Innovators** club has two upcoming events. These events can be viewed by logging in and navigating to the **Event** section:

1. **Event 1: Singapore Grand Prix**
   - **Date**: 2024-09-22
   - **Venue**: Garden by the Bay
   - **Capacity**: 100
   - **Description**: Formula 1 race in Singapore

2. **Event 2: Australia Grand Prix**
   - **Date**: 2025-03-16
   - **Venue**: Albert Park
   - **Capacity**: 10
   - **Description**: Formula 1 race in Melbounre

### Ticket Information
**yunchen6** has already secured one ticket for the **Singapore Grand Prix** event. This ticket can be viewed under the **My Event** section.

- **Ticket Details**:
   - **Event**: Singapore Grand Prix
   - **Status**: Confirmed

### Funding Application
This club has submitted one funding application to support the upcoming hackathon event. The details are as follows:

- **Description**: 2025 Shanghai Lenovo Grand Prix
- **Amount Requested**: 10000
- **Status**: Submitted

### How to Use the Example:
1. **Login**: Navigate to the login page and use the provided credentials (**username: yunchen6**, **password: passwordyunchen**).
2. **View Personal Information**: After logging in, you will be redirected to the home page where you can view the personal information for **yunchen6**.
3. **View and Manage Club**: Click on the **Club** section to see the **Tech Innovators** club details. You can also manage the club if you have admin privileges.
4. **View Events**: Go to the **Event** section in the sidebar to view the two events listed for the club.
5. **Get Ticket**: You can attempt to get additional tickets for the events if there are available spots.
6. **Manage Funding Application**: If you are an administrator, you can manage the submitted funding application, track its status, or submit additional details if necessary.
7. **Logout**: Once you have completed your tasks, click the **Logout** button to securely log out of the system.


## CheckList
    -View all the clubs and events
    -Create a new club and events
    -View the homepage

## Accessing Our Application

You can access our application through the following link:

[ClubManagement](https://clubmanagement-frontend.onrender.com)


## Contributing

## License

## Contact
For any questions or issues, please contact:


