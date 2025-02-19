import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

enum VehicleType { BIKE, CAR, TRUCK }

class ParkingSpot {
    private final int floor;
    private final List<Integer> spots;

    public ParkingSpot(int floor, List<Integer> spots) {
        this.floor = floor;
        this.spots = spots;
    }

    public int getFloor() { return floor; }
    public List<Integer> getSpots() { return spots; }
}

class Floor {
    private final boolean[] spots;
    private final Lock lock = new ReentrantLock();

    public Floor(int capacity) {
        spots = new boolean[capacity];
        Arrays.fill(spots, true);
    }

    public List<Integer> parkVehicle(VehicleType type) {
        lock.lock();
        try {
            if (type == VehicleType.TRUCK) {
                for (int i = 0; i < spots.length - 1; i++) {
                    if (spots[i] && spots[i+1]) {
                        spots[i] = spots[i+1] = false;
                        return Arrays.asList(i, i+1);
                    }
                }
                return null;
            }
            
            for (int i = 0; i < spots.length; i++) {
                if (spots[i]) {
                    spots[i] = false;
                    return Collections.singletonList(i);
                }
            }
            return null;
        } finally {
            lock.unlock();
        }
    }

    public void freeSpots(List<Integer> spotsToFree) {
        lock.lock();
        try {
            for (int spot : spotsToFree) {
                if (spot >= 0 && spot < spots.length) {
                    spots[spot] = true;
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public int availableSpots() {
        lock.lock();
        try {
            int count = 0;
            for (boolean available : spots) {
                if (available) count++;
            }
            return count;
        } finally {
            lock.unlock();
        }
    }

    public int consecutiveSpotsAvailable() {
        lock.lock();
        try {
            int count = 0;
            for (int i = 0; i < spots.length - 1; i++) {
                if (spots[i] && spots[i+1]) count++;
            }
            return count;
        } finally {
            lock.unlock();
        }
    }
}

class ParkingLot {
    private final List<Floor> floors;
    private final Map<String, ParkingSpot> vehicleMap = new ConcurrentHashMap<>();

    public ParkingLot(int numFloors, int spotsPerFloor) {
        floors = new ArrayList<>(numFloors);
        for (int i = 0; i < numFloors; i++) {
            floors.add(new Floor(spotsPerFloor));
        }
    }

    public ParkingSpot parkVehicle(String licensePlate, VehicleType type) {
        for (int floorNum = 0; floorNum < floors.size(); floorNum++) {
            Floor floor = floors.get(floorNum);
            List<Integer> spots = floor.parkVehicle(type);
            if (spots != null) {
                ParkingSpot spot = new ParkingSpot(floorNum, spots);
                vehicleMap.put(licensePlate, spot);
                return spot;
            }
        }
        return null;
    }

    public boolean leaveVehicle(String licensePlate) {
        ParkingSpot spot = vehicleMap.remove(licensePlate);
        if (spot == null) return false;
        floors.get(spot.getFloor()).freeSpots(spot.getSpots());
        return true;
    }

    public int getAvailableSpots(int floor) {
        if (floor < 0 || floor >= floors.size()) return 0;
        return floors.get(floor).availableSpots();
    }

    public int getTotalAvailableSpots() {
        return floors.stream().mapToInt(Floor::availableSpots).sum();
    }

    public boolean isFull() {
        return floors.stream().allMatch(f -> f.availableSpots() == 0);
    }

    public ParkingSpot locateVehicle(String licensePlate) {
        return vehicleMap.get(licensePlate);
    }

    public int getTruckSpacesAvailable() {
        return floors.stream().mapToInt(Floor::consecutiveSpotsAvailable).sum();
    }
}

public class ParkingSystem {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ParkingLot parkingLot = null;
        
        while (true) {
            System.out.print("> ");
            String[] input = scanner.nextLine().split(" ");
            
            try {
                switch (input[0].toLowerCase()) {
                    case "create_parking_lot":
                        parkingLot = new ParkingLot(
                            Integer.parseInt(input[1]),
                            Integer.parseInt(input[2])
                        );
                        System.out.println("Parking lot created");
                        break;
                        
                    case "park":
                        ParkingSpot spot = parkingLot.parkVehicle(
                            input[1],
                            VehicleType.valueOf(input[2].toUpperCase())
                        );
                        System.out.println(spot != null ? 
                            "Parked at floor " + spot.getFloor() + ", spots " + spot.getSpots() :
                            "Parking failed");
                        break;
                        
                    case "leave":
                        System.out.println(parkingLot.leaveVehicle(input[1]) ?
                            "Vehicle removed" : "Vehicle not found");
                        break;
                        
                    case "availability":
                        if (input.length > 1) {
                            System.out.println("Available spots: " + 
                                parkingLot.getAvailableSpots(Integer.parseInt(input[1])));
                        } else {
                            System.out.println("Total available: " + 
                                parkingLot.getTotalAvailableSpots());
                        }
                        break;
                        
                    case "locate":
                        ParkingSpot location = parkingLot.locateVehicle(input[1]);
                        System.out.println(location != null ?
                            "Located at floor " + location.getFloor() + ", spots " + location.getSpots() :
                            "Vehicle not found");
                        break;
                        
                    case "is_full":
                        System.out.println(parkingLot.isFull() ? 
                            "Parking lot is full" : "Spaces available");
                        break;
                        
                    case "exit":
                        scanner.close();
                        return;
                        
                    default:
                        System.out.println("Invalid command");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}