# Car Rental System

## Problem Statement
The goal of this project is to design and implement a **Car Rental System** that allows users to browse and book vehicles from various branches. The system supports multiple vehicle types, dynamic pricing, and flexible booking strategies while ensuring data consistency and handling concurrent booking requests efficiently.

## Requirements

### Functional Requirements
1.  **Branch Management**: The system supports multiple branches (e.g., Bangalore, Delhi), each managing its own fleet of vehicles.
2.  **Vehicle Inventory**: Support for different types of vehicles (e.g., Sedan, SUV) with specific attributes like license plate, hourly rate, and per-km rate.
3.  **Booking System**:
    *   Users can search for vehicles by type and branch.
    *   Users can book a vehicle for a specific duration.
    *   The system must prevent double booking of the same vehicle (Concurrency Handling).
4.  **Pricing Engine**: Calculate rental costs dynamically based on:
    *   **Hourly Pricing**: Charged based on the duration of the rental.
    *   **Distance-based Pricing**: Charged based on the distance traveled (if applicable).
5.  **Payment Processing**: Support for multiple payment methods (e.g., Credit Card, Wallet).
6.  **Booking Strategy**: The system can intelligently select a vehicle based on defined strategies:
    *   **Least Booked**: Prioritizes vehicles that have been booked less often to ensure even usage.
    *   **Cheapest**: Selects the most affordable vehicle available.

### Non-Functional Requirements
*   **Thread Safety**: Ensure that multiple users attempting to book the same vehicle simultaneously do not result in data inconsistency.
*   **Extensibility**: The system should be easily extensible to add new vehicle types, pricing strategies, or payment methods without modifying existing code.

## Design Patterns Used

This project implements several standard design patterns to ensure clean, maintainable, and scalable code:

1.  **Singleton Pattern**:
    *   **Usage**: `BookingService`
    *   **Reason**: Ensures that a single instance of the service handles all booking operations, centralizing logic and state management where necessary.

2.  **Factory Pattern**:
    *   **Usage**: `VehicleFactory`
    *   **Reason**: Centralizes the creation logic for different `Vehicle` types (`Sedan`, `Suv`), decoupling the client code from specific class instantiations.

3.  **Strategy Pattern**:
    *   **Pricing Strategy**: (`HourlyPricingStrategy`, `DistanceBasedPricingStartegy`) - Allows the pricing algorithm to be selected or switched at runtime.
    *   **Booking Strategy**: (`LeastBookedVehicleStrategy`, `CheapestBookingStrategy`) - Encapsulates the logic for selecting a vehicle from the available pool.
    *   **Payment Strategy**: (`CreditCardPayment`, `WalletPayment`) - Defines a family of payment algorithms, making it easy to add new payment methods.

4.  **Repository Pattern**:
    *   **Usage**: `BookingRepository`, `BranchRepository`
    *   **Reason**: Abstracts the data layer, separating business logic from data access and storage implementation (in-memory in this prototype).

5.  **Builder Pattern**:
    *   **Usage**: `Booking.builder()`
    *   **Reason**: Provides a clear and readable way to construct complex `Booking` objects with multiple parameters.

6.  **Concurrency Control (Optimistic Locking)**:
    *   **Usage**: `AtomicBoolean` in `Vehicle` class.
    *   **Reason**: Used to handle race conditions when multiple threads try to book the same vehicle simultaneously, ensuring data integrity without heavy locking mechanisms.
