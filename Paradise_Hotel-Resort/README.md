# Hotel Management System

This project is a hotel management system designed to facilitate the management of hotel operations, including room bookings, guest registrations, and additional services. The application features a user-friendly graphical interface that allows hotel staff to efficiently manage various aspects of hotel operations.

## Project Structure

The project is organized into the following directories and files:

- **src/**: Contains the source code for the application.
  - **Main.java**: The entry point of the application, initializing the GUI.
  - **gui/**: Contains the graphical user interface components.
    - **HotelGUI.java**: Defines the main window and tabbed interface for managing rooms, guests, reservations, and reports.
  - **model/**: Contains the data models used in the application.
    - **Habitacion.java**: Represents a hotel room with properties such as room number, type, capacity, and price.
    - **Huesped.java**: Represents a guest with properties such as ID, name, contact information, and reservation history.
    - **Reserva.java**: Represents a reservation with properties such as reservation code, dates, associated guest and room, and additional services.
    - **ServicioAdicional.java**: Represents additional services offered by the hotel.
    - **Factura.java**: Represents an invoice with properties such as invoice ID, total amount, and payment status.
    - **enums.java**: Contains enumerations used throughout the application.
  - **system/**: Contains the system management logic.
    - **SistemaHotel.java**: Manages the overall hotel system, including lists for rooms, guests, reservations, and services.

## Running the Application

To run the hotel management system, follow these steps:

1. Ensure you have Java Development Kit (JDK) installed on your machine.
2. Clone the repository or download the project files.
3. Navigate to the project directory in your terminal or command prompt.
4. Compile the source code using the following command:
   ```
   javac src/**/*.java
   ```
5. Run the application using the following command:
   ```
   java src/Main
   ```
PS: Download the .exe file and run it directly by the operative system (Windows).

## Features

- Manage hotel rooms, including availability and pricing.
- Register and manage guest information.
- Create and manage reservations.
- Generate reports on room demand and guest history.

## Future Enhancements

- Implement a database for persistent storage of guest and reservation data.
- Add user authentication for different roles (e.g., admin, staff).
- Enhance the reporting features with more detailed analytics.

## License

This project is open-source and available for modification and distribution under the MIT License.
