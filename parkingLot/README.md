# Parking Lot System Design

## Problem Statement

Design an object-oriented Parking Lot system that can manage parking operations for a multi-floor building. The system should be extensible and maintainable, utilizing appropriate design patterns.

## Requirements

### Core Features
1.  **Parking Structure**:
    *   The parking lot should consist of multiple **floors**.
    *   Each floor should contain multiple **parking spots**.
    *   The system should support multiple entry and exit points (**EntrancePanel**, **ExitPanel**).

2.  **Parking Spots**:
    *   Support different types of parking spots to accommodate various vehicles:
        *   **Compact**: For standard cars.
        *   **Large**: For trucks and large vehicles.
        *   **Handicapped**: Reserved for handicapped vehicles.
        *   **Motorbike**: For motorcycles.
        *   **Electric**: With charging stations for electric vehicles.

3.  **Vehicles**:
    *   Support different vehicle types: **Car**, **Truck**, **ElectricCar**, **Motorbike**.
    *   Vehicles should only be allowed to park in suitable spots (e.g., a Truck cannot park in a Compact spot).

4.  **Parking Operations**:
    *   **Entry**:
        *   Upon entry, the system should check for available spots for the vehicle type.
        *   If a spot is available, a **ParkingTicket** is issued.
        *   If no spot is available, the system should indicate that the parking is full.
    *   **Exit**:
        *   The customer presents the ticket at the exit.
        *   The system calculates the parking fee based on the duration and vehicle type.
        *   Payment is processed, and the ticket is marked as paid.

5.  **Pricing & Payment**:
    *   **Pricing Strategy**: The system should support flexible pricing models (e.g., **Per Hour**, **Peak Hour**, **Electric Charging**).
    *   **Payment Methods**: Support multiple payment options (e.g., **Cash**, **Credit Card**).

6.  **Real-time Updates**:
    *   A **Display Board** should show the current status of parking spots.
    *   The display should update automatically when a spot is occupied or freed.

### Technical Requirements
*   **Design Patterns**: Demonstrate the use of standard design patterns where applicable:
    *   **Singleton**: For the main `ParkingLot` instance.
    *   **Factory**: For creating vehicles and parking spots.
    *   **Strategy**: For pricing algorithms and payment methods.
    *   **Observer**: For updating display boards when spot availability changes.
    *   **Command**: For handling parking operations (e.g., processing tickets).
    *   **State**: For managing ticket status (Active, Paid).
*   **Exception Handling**: Handle edge cases like "Parking Full" gracefully.
*   **Concurrency**: Ensure the system can handle concurrent requests (e.g., multiple vehicles entering/exiting simultaneously).

## Project Structure

*   `src/main/java/org/example/model`: Core domain entities (ParkingLot, ParkingFloor, ParkingSpot, Vehicle, etc.).
*   `src/main/java/org/example/services`: Service logic (ParkingLot management).
*   `src/main/java/org/example/strategy`: Implementation of various strategies (Pricing, Payment, Observer, Command).
*   `src/main/java/org/example/factory`: Factory classes for object creation.
*   `src/main/java/org/example/enums`: Enumerations for types and statuses.
*   `src/main/java/org/example/exceptions`: Custom exceptions.

## Running the Application

The `Main.java` class provides a simulation of the parking lot operations:
1.  Initializes the parking lot with floors and spots.
2.  Creates a vehicle and issues a ticket.
3.  Processes the ticket via an attendant.
4.  Calculates the fee and processes payment.
5.  Validates exit.
