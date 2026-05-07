package com.sunilskyros.payanam.data.repository;

import com.sunilskyros.payanam.data.dto.Bus;
import com.sunilskyros.payanam.data.dto.Passenger;
import com.sunilskyros.payanam.data.dto.Stop;
import com.sunilskyros.payanam.data.dto.Ticket;
import com.sunilskyros.payanam.util.DBConnection;
import com.sunilskyros.payanam.util.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Singleton class handling all direct database interactions using JDBC.
 * Acts as the centralized Repository for the entire application.
 */
public class PayanamDB {
    private static PayanamDB payanamDB = null;

    private PayanamDB() {
        setupTables();
    }

    /**
     * Gets the singleton instance of the PayanamDB repository.
     * @return The active PayanamDB instance.
     */
    public static PayanamDB getInstance() {
        if (payanamDB == null) {
            payanamDB = new PayanamDB();
        }
        return payanamDB;
    }

    /**
     * Initializes the database schema on first boot.
     * Creates passengers, buses, stops, and tickets tables if they do not exist.
     */
    private void setupTables() {
        String createPassengers = "CREATE TABLE IF NOT EXISTS passengers (" +
                "phone_number VARCHAR(15) PRIMARY KEY, " +
                "name VARCHAR(50), " +
                "password VARCHAR(60), " +
                "role VARCHAR(20), " +
                "status VARCHAR(20))";

        String createBuses = "CREATE TABLE IF NOT EXISTS buses (" +
                "id INT PRIMARY KEY, " +
                "name VARCHAR(50))";

        String createStops = "CREATE TABLE IF NOT EXISTS stops (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "bus_id INT, " +
                "stop_id_seq INT, " +
                "stop_name VARCHAR(50), " +
                "updated_time TIME, " +
                "current_stop BOOLEAN, " +
                "FOREIGN KEY (bus_id) REFERENCES buses(id) ON DELETE CASCADE)";

        String createTickets = "CREATE TABLE IF NOT EXISTS tickets (" +
                "ticket_id INT PRIMARY KEY AUTO_INCREMENT, " +
                "passenger_phone_number VARCHAR(15), " +
                "bus_id INT, " +
                "bus_name VARCHAR(50), " +
                "source_stop VARCHAR(50), " +
                "destination_stop VARCHAR(50), " +
                "price INT, " +
                "is_valid BOOLEAN DEFAULT true," +
                "bought_time DATETIME, " +
                "valid_until DATETIME, " +
                "FOREIGN KEY (passenger_phone_number) REFERENCES passengers(phone_number) ON DELETE CASCADE, " +
                "FOREIGN KEY (bus_id) REFERENCES buses(id) ON DELETE CASCADE)";

        Connection conn = DBConnection.getConnection();
        if (conn != null) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(createPassengers);
                stmt.execute(createBuses);
                stmt.execute(createStops);
                stmt.execute(createTickets);
            } catch (SQLException e) {
                System.err.println("Database error during table setup: " + e.getMessage());
            }
        }
    }

    /**
     * Inserts a new passenger into the database.
     * @param passenger The passenger object to save.
     * @return The saved passenger, or null if insertion failed.
     */
    public Passenger addPassenger(Passenger passenger) {
        if (passenger == null || passenger.getPhoneNumber() == null)
            return null;

        if (passenger.getStatus() == null) {
            passenger.setStatus(Passenger.Status.ACTIVE);
        }
        if (passenger.getRole() == null) {
            passenger.setRole(Passenger.Role.PASSENGER);
        }

        String sql = "INSERT INTO passengers (phone_number, name, password, role, status) VALUES (?, ?, ?, ?, ?)";
        Connection conn = DBConnection.getConnection();
        if (conn == null)
            return null;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, passenger.getPhoneNumber());
            pstmt.setString(2, passenger.getName());
            pstmt.setString(3, passenger.getPassword());
            pstmt.setString(4, passenger.getRole().name());
            pstmt.setString(5, passenger.getStatus().name());
            pstmt.executeUpdate();
            return passenger;
        } catch (SQLException e) {
            return null;
        }
    }

    /**
     * Deletes a passenger from the database.
     * @param passenger The passenger to remove.
     * @return A status message indicating success or failure.
     */
    public String removeUser(Passenger passenger) {
        String sql = "DELETE FROM passengers WHERE phone_number = ?";
        Connection conn = DBConnection.getConnection();
        if (conn == null)
            return "Error: Cannot connect to database";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, passenger.getPhoneNumber());
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                return "Passenger not found";
            }
            return "Deleted successfully";
        } catch (SQLException e) {
            System.err.println("Database error during user removal: " + e.getMessage());
            return "Error deleting the passenger";
        }
    }

    /**
     * Authenticates a user by validating their phone number and hashing their password.
     * @param phoneNumber The user's phone number.
     * @param password The plain text password to verify.
     * @return The authenticated passenger, or null if credentials are invalid.
     */
    public Passenger authenticatePassenger(String phoneNumber, String password) {
        Passenger passenger = getPassengerByPhone(phoneNumber);
        if (passenger == null) return null;
        if (!PasswordUtil.verify(password, passenger.getPassword())) return null;
        return passenger;
    }

    /**
     * Retrieves a passenger by their phone number.
     * @param phoneNumber The phone number to query.
     * @return The Passenger object if found, otherwise null.
     */
    public Passenger getPassengerByPhone(String phoneNumber) {
        if (phoneNumber == null) return null;
        String sql = "SELECT * FROM passengers WHERE phone_number = ?";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return null;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, phoneNumber);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Passenger passenger = new Passenger();
                passenger.setPhoneNumber(rs.getString("phone_number"));
                passenger.setName(rs.getString("name"));
                passenger.setPassword(rs.getString("password"));
                passenger.setRole(Passenger.Role.valueOf(rs.getString("role")));
                passenger.setStatus(Passenger.Status.valueOf(rs.getString("status")));
                return passenger;
            }
        } catch (SQLException e) {
            System.err.println("Database error fetching passenger: " + e.getMessage());
        }
        return null;
    }

    /**
     * Inserts a new bus record into the database.
     * @param bus The Bus object to add.
     */
    public void addBus(Bus bus) {
        String sql = "INSERT INTO buses (id, name) VALUES (?, ?)";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bus.getId());
            pstmt.setString(2, bus.getName());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Database error adding bus: " + e.getMessage());
        }
    }

    /**
     * Completely replaces the route (stops) for a specific bus.
     * Deletes existing stops and inserts the new sequence.
     * @param bus The bus object containing the new list of stops.
     */
    public void updateBusStops(Bus bus) {
        if (bus == null) return;
        Connection conn = DBConnection.getConnection();
        if (conn == null) return;

        String deleteStops = "DELETE FROM stops WHERE bus_id = ?";
        try (PreparedStatement deleteStmt = conn.prepareStatement(deleteStops)) {
            deleteStmt.setInt(1, bus.getId());
            deleteStmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Database error clearing previous stops: " + e.getMessage());
        }

        if (bus.getStops() != null && !bus.getStops().isEmpty()) {
            String insertStop = "INSERT INTO stops (bus_id, stop_id_seq, stop_name, updated_time, current_stop) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertStop)) {
                for (Stop stop : bus.getStops()) {
                    insertStmt.setInt(1, bus.getId());
                    insertStmt.setInt(2, stop.getId());
                    insertStmt.setString(3, stop.getStopName());
                    insertStmt.setTime(4, stop.getUpdatedTime() != null ? Time.valueOf(stop.getUpdatedTime()) : null);
                    if (stop.getCurrentStop() == null) {
                        insertStmt.setNull(5, Types.BOOLEAN);
                    } else {
                        insertStmt.setBoolean(5, stop.getCurrentStop());
                    }
                    insertStmt.addBatch();
                }
                insertStmt.executeBatch();
            } catch (SQLException e) {
                System.err.println("Database error inserting new stops: " + e.getMessage());
            }
        }
    }

    /**
     * Updates the status and timing for a list of existing stops.
     * Typically used for tracking a bus's real-time progression.
     * @param stops The list of modified stops.
     */
    public void updateStops(List<Stop> stops) {
        if (stops == null || stops.isEmpty()) return;
        String sql = "UPDATE stops SET updated_time = ?, current_stop = ? WHERE bus_id = ? AND stop_id_seq = ?";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (Stop stop : stops) {
                pstmt.setTime(1, stop.getUpdatedTime() != null ? Time.valueOf(stop.getUpdatedTime()) : null);
                if (stop.getCurrentStop() == null) {
                    pstmt.setNull(2, Types.BOOLEAN);
                } else {
                    pstmt.setBoolean(2, stop.getCurrentStop());
                }
                pstmt.setInt(3, stop.getBusId());
                pstmt.setInt(4, stop.getId());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        } catch (SQLException e) {
            System.err.println("Database error updating stops: " + e.getMessage());
        }
    }

    /**
     * Removes a bus and cascades to its associated stops and tickets.
     * @param busId The ID of the bus to remove.
     */
    public void removeBus(int busId) {
        String sql = "DELETE FROM buses WHERE id = ?";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, busId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Database error removing bus: " + e.getMessage());
        }
    }

    /**
     * Retrieves all available buses in the system.
     * @return A map mapping bus IDs to Bus objects (without their stops loaded).
     */
    public Map<Integer, Bus> getBusList() {
        Map<Integer, Bus> busList = new HashMap<>();
        String sqlBuses = "SELECT * FROM buses";

        Connection conn = DBConnection.getConnection();
        if (conn == null) return busList;

        try (Statement stmtBuses = conn.createStatement()) {
            ResultSet rsBuses = stmtBuses.executeQuery(sqlBuses);
            while (rsBuses.next()) {
                Bus bus = new Bus();
                bus.setId(rsBuses.getInt("id"));
                bus.setName(rsBuses.getString("name"));
                bus.setStops(new ArrayList<>());
                busList.put(bus.getId(), bus);
            }
        } catch (SQLException e) {
            System.err.println("Database error fetching bus list: " + e.getMessage());
        }
        return busList;
    }

    /**
     * Performs a substring search to find all buses passing through a specific stop.
     * @param stopName The partial or full name of the stop.
     * @return A list of buses servicing the given stop name.
     */
    public List<Bus> searchBusesByStop(String stopName) {
        List<Bus> result = new ArrayList<>();
        if (stopName == null || stopName.trim().isEmpty()) {
            return result;
        }

        String sql = "SELECT DISTINCT b.id, b.name FROM buses b JOIN stops s ON b.id = s.bus_id WHERE LOWER(s.stop_name) LIKE ?";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return result;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + stopName.trim().toLowerCase() + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Bus bus = new Bus();
                bus.setId(rs.getInt("id"));
                bus.setName(rs.getString("name"));
                result.add(bus);
            }
        } catch (SQLException e) {
            System.err.println("Database error searching buses by stop: " + e.getMessage());
        }
        return result;
    }

    /**
     * Retrieves a specific bus along with its fully ordered list of stops.
     * @param busId The ID of the bus.
     * @return The heavily populated Bus object, or null if not found.
     */
    public Bus getBusById(int busId) {
        Bus bus = null;
        String sqlBus = "SELECT * FROM buses WHERE id = ?";
        String sqlStops = "SELECT * FROM stops WHERE bus_id = ? ORDER BY stop_id_seq";

        Connection conn = DBConnection.getConnection();
        if (conn == null) return null;

        try (PreparedStatement pstmtBus = conn.prepareStatement(sqlBus);
             PreparedStatement pstmtStops = conn.prepareStatement(sqlStops)) {

            pstmtBus.setInt(1, busId);
            ResultSet rsBus = pstmtBus.executeQuery();
            if (rsBus.next()) {
                bus = new Bus();
                bus.setId(rsBus.getInt("id"));
                bus.setName(rsBus.getString("name"));
                bus.setStops(new ArrayList<>());
            }

            if (bus != null) {
                pstmtStops.setInt(1, busId);
                ResultSet rsStops = pstmtStops.executeQuery();
                while (rsStops.next()) {
                    Stop stop = new Stop();
                    stop.setId(rsStops.getInt("stop_id_seq"));
                    stop.setBusId(busId);
                    stop.setStopName(rsStops.getString("stop_name"));
                    Time t = rsStops.getTime("updated_time");
                    if (t != null) stop.setUpdatedTime(t.toLocalTime());

                    boolean hasCurrentStop = rsStops.getObject("current_stop") != null;
                    if (hasCurrentStop) {
                        stop.setCurrentStop(rsStops.getBoolean("current_stop"));
                    } else {
                        stop.setCurrentStop(null);
                    }
                    bus.getStops().add(stop);
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error fetching bus by ID: " + e.getMessage());
        }
        return bus;
    }

    /**
     * Persists a newly purchased ticket into the database.
     * Captures the auto-generated ticket ID back into the Ticket object.
     * @param ticket The ticket object containing route and pricing details.
     * @return The completed ticket with its assigned ID, or null on failure.
     */
    public Ticket addTicket(Ticket ticket) {
        if (ticket == null || ticket.getPassengerPhoneNumber() == null) return null;

        String sql = "INSERT INTO tickets (passenger_phone_number, bus_id, bus_name, source_stop, destination_stop, price, is_valid, bought_time, valid_until) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return null;

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, ticket.getPassengerPhoneNumber());
            pstmt.setInt(2, ticket.getBusId());
            pstmt.setString(3, ticket.getBusName());
            pstmt.setString(4, ticket.getSourceStop());
            pstmt.setString(5, ticket.getDestinationStop());
            pstmt.setInt(6, ticket.getPrice());
            pstmt.setBoolean(7, true);
            pstmt.setTimestamp(8, ticket.getBoughtTime() != null ? Timestamp.valueOf(ticket.getBoughtTime()) : null);
            pstmt.setTimestamp(9, ticket.getValidUntil() != null ? Timestamp.valueOf(ticket.getValidUntil()) : null);

            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                ticket.setTicketId(rs.getInt(1));
            }
            return ticket;
        } catch (SQLException e) {
            System.err.println("Database error adding ticket: " + e.getMessage());
            return null;
        }
    }

    /**
     * Retrieves all tickets associated with a specific passenger's phone number.
     * @param phoneNumber The phone number linked to the tickets.
     * @return A list of tickets booked by the user.
     */
    public List<Ticket> getTicketsByPassenger(String phoneNumber) {
        List<Ticket> tickets = new ArrayList<>();
        if (phoneNumber == null) return tickets;

        String sql = "SELECT * FROM tickets WHERE passenger_phone_number = ?";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return tickets;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, phoneNumber);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Ticket ticket = new Ticket();
                ticket.setTicketId(rs.getInt("ticket_id"));
                ticket.setPassengerPhoneNumber(rs.getString("passenger_phone_number"));
                ticket.setBusId(rs.getInt("bus_id"));
                ticket.setBusName(rs.getString("bus_name"));
                ticket.setSourceStop(rs.getString("source_stop"));
                ticket.setDestinationStop(rs.getString("destination_stop"));
                ticket.setPrice(rs.getInt("price"));
                ticket.setIsValid(rs.getBoolean("is_valid"));
                Timestamp bt = rs.getTimestamp("bought_time");
                if (bt != null) ticket.setBoughtTime(bt.toLocalDateTime());
                Timestamp vu = rs.getTimestamp("valid_until");
                if (vu != null) ticket.setValidUntil(vu.toLocalDateTime());
                tickets.add(ticket);
            }
        } catch (SQLException e) {
            System.err.println("Database error fetching tickets by passenger: " + e.getMessage());
        }
        return tickets;
    }

    /**
     * Retrieves a single ticket by its unique ID.
     * Optimized by first finding the owner's phone number, then pulling their tickets.
     * @param ticketId The integer ID of the ticket.
     * @return The Ticket object, or null if not found.
     */
    public Ticket getTicketById(int ticketId) {
        String sql = "SELECT passenger_phone_number FROM tickets WHERE ticket_id = ?";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return null;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, ticketId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String phoneNumber = rs.getString("passenger_phone_number");
                List<Ticket> tickets = getTicketsByPassenger(phoneNumber);
                for (Ticket ticket : tickets) {
                    if (ticket.getTicketId() == ticketId) {
                        return ticket;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error fetching ticket by ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Updates the validity status of an existing ticket in the database.
     * @param ticket The ticket containing the new validity state.
     * @return true if the update affected a row, false otherwise.
     */
    public Boolean updateTicket(Ticket ticket) {
        String sql = "UPDATE tickets SET is_valid = ? WHERE ticket_id = ?";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, ticket.getIsValid());
            ps.setInt(2, ticket.getTicketId());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Database error updating ticket: " + e.getMessage());
            return false;
        }
    }
}
