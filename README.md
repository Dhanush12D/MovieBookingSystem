# Movie Booking System

A desktop-based Movie Booking System developed using Java Swing, JDBC, and MySQL. The application allows users to log in, select movies, book seats, and view booking confirmations.

## Technologies Used

* Java
* Java Swing
* JDBC
* MySQL

## Features

* User Login Authentication
* Movie Selection
* Seat Booking
* Booking Confirmation
* Dynamic Movie Posters and Backgrounds
* MySQL Database Integration
* User-friendly Swing Interface

## Project Highlights

This project helped me gain practical experience in Java Swing, JDBC, MySQL database connectivity, GUI development, event handling, and debugging.

During development, I worked through real-world issues such as database connection errors, classpath problems, image resource loading, UI design issues, and page navigation bugs.

## How to Run

1. Clone the repository.
2. Open the project in IntelliJ IDEA or VS Code.
3. Create the required MySQL database.
4. Update the database username, password, and connection details.
5. Add the MySQL JDBC driver.
6. Run the main Java class.

## Future Improvements

* Booking history
* Ticket cancellation
* Admin dashboard
* Movie search and filtering
* Online payment integration
* Improved UI and animations

  ## System Flow

```mermaid
flowchart TD
    A[User] --> B[Java Swing UI]

    B --> C[Login]
    C --> D[JDBC]
    D --> E[(MySQL Database)]

    E --> D
    D --> C

    C --> F[Movie Selection]
    F --> G[Select Movie]
    G --> H[Seat Selection]

    H --> I[Check Seat Availability]
    I --> D
    D --> E

    I --> J[Confirm Booking]
    J --> D
    D --> E

    E --> K[Booking Details]
    K --> L[Booking Confirmation]
    L --> B
```

<img width="903" height="567" alt="Screenshot 2026-05-31 170350" src="https://github.com/user-attachments/assets/4b2e311f-7ed3-4c97-9b26-130cb88e1478" />
<img width="616" height="678" alt="Screenshot 2026-05-27 000435" src="https://github.com/user-attachments/assets/056de867-1f89-43d2-a2a2-061ba1e08773" />
<img width="1122" height="692" alt="Screenshot 2026-05-27 000417" src="https://github.com/user-attachments/assets/0df7be5c-fdb7-41a5-97ca-298794850e49" />
<img width="1113" height="784" alt="Screenshot 2026-05-27 000322" src="https://github.com/user-attachments/assets/496b6010-ad27-467c-a2be-da16a19de7d9" />
<img width="1409" height="730" alt="Screenshot 2026-05-27 000233" src="https://github.com/user-attachments/assets/a19398e4-ed66-4b3b-adf9-55777331a793" />
<img width="1722" height="973" alt="Screenshot 2026-05-24 181029" src="https://github.com/user-attachments/assets/89f2ef5a-ab2c-4ce6-a0f4-4eb79c8f0ca5" />


## Author

Dhanush Nayak
