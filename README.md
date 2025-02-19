# Parking Lot System

## Overview

The Parking Lot System is a Java-based command-line application that simulates the management of a parking lot with multiple floors. The system supports different vehicle types—Bike, Car, and Truck—with Trucks requiring two consecutive spots. It allows you to park vehicles, remove them when they leave, query available spots, locate vehicles by license plate, and check if the parking lot is full.

## Features

- **Multiple Floors:** Create a parking lot with any number of floors, each with a fixed number of spots.
- **Vehicle Types:** 
  - **Bike:** Occupies 1 spot.
  - **Car:** Occupies 1 spot.
  - **Truck:** Requires 2 consecutive spots.
- **Parking & Unparking:** 
  - Park a vehicle by automatically assigning the nearest available spot(s).
  - Remove a vehicle and free up the occupied spots.
- **Queries:** 
  - Check available spots per floor or overall.
  - Locate a parked vehicle by its license plate.
  - Verify whether the parking lot is completely full.
- **Concurrency:** Utilizes Java locks to handle multiple concurrent parking operations safely.

## Project Structure

- **ParkingSystem.java:** Contains the main method and implements a command-line interface for interacting with the parking lot.
- **Other Classes:**
  - `VehicleType`: An enum that defines the vehicle types (BIKE, CAR, TRUCK).
  - `ParkingSpot`: Represents a parking spot with a floor number and list of spot numbers.
  - `Floor`: Manages parking spots on a single floor. Provides methods to park vehicles, free spots, and count available spots.
  - `ParkingLot`: Manages multiple floors and maintains a mapping between vehicle license plates and their parked spots. Provides methods for parking, leaving, querying status, and locating vehicles.

## How It Works

1. **Creating the Parking Lot:**  
   The user creates a parking lot by specifying the number of floors and the number of spots per floor. This initializes multiple `Floor` objects, each containing a set of parking spots.

2. **Parking a Vehicle:**  
   When a user parks a vehicle, the system searches each floor for available spots:
   - For a Bike or Car, it finds a single available spot.
   - For a Truck, it searches for two consecutive free spots.
   If a suitable spot is found, the system marks the spot(s) as occupied and stores the parking information.

3. **Leaving the Parking Lot:**  
   When a vehicle leaves, the system frees up the associated spots and removes the vehicle from the internal mapping.

4. **Querying the System:**  
   The system can report the number of available spots on each floor or in the entire lot. It can also locate a vehicle by license plate and indicate if the lot is full.

## How to Compile and Run

1. **Compile the Code:**

   Open a terminal or command prompt, navigate to the directory containing your `.java` files, and compile the project using:
   ```bash
   javac ParkingSystem.java
