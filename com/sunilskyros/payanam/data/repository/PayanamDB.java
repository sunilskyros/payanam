package com.sunilskyros.payanam.data.repository;

import com.sunilskyros.payanam.data.dto.Bus;
import com.sunilskyros.payanam.data.dto.Passenger;
import com.sunilskyros.payanam.data.dto.Stop;
import com.sunilskyros.payanam.data.dto.Ticket;
import com.sunilskyros.payanam.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PayanamDB {
    private static PayanamDB payanamDB = null;

    private PayanamDB() {
        setupTables();
    }

    public static PayanamDB getInstance() {
        if (payanamDB == null) {
            payanamDB = new PayanamDB();
        }
        return payanamDB;
    }

    private void setupTables() {
        String createPassengers = "CREATE TABLE IF NOT EXISTS passengers (" +
                "phone_number VARCHAR(15) PRIMARY KEY, " +
                "name VARCHAR(50), " +
                "password VARCHAR(50), " +
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
                e.printStackTrace();
            }
        }
    }

    public Passenger addPassenger(Passenger passenger) {
        if (passenger == null || passenger.getPhoneNumber() == null) return null;

        if (passenger.getStatus() == null) {
            passenger.setStatus(Passenger.Status.ACTIVE);
        }
        if (passenger.getRole() == null) {
            passenger.setRole(Passenger.Role.PASSENGER);
        }

        String sql = "INSERT INTO passengers (phone_number, name, password, role, status) VALUES (?, ?, ?, ?, ?)";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return null;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, passenger.getPhoneNumber());
            pstmt.setString(2, passenger.getName());
            pstmt.setString(3, passenger.getPassword());
            pstmt.setString(4, passenger.getRole().name());
            pstmt.setString(5, passenger.getStatus().name());
            pstmt.executeUpdate();
            return passenger;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Passenger authenticatePassenger(String phoneNumber, String password) {
        Passenger passenger = getPassengerByPhone(phoneNumber);
        if (passenger == null) return null;
        if (password == null || !password.equals(passenger.getPassword())) return null;
        return passenger;
    }

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
            e.printStackTrace();
        }
        return null;
    }

    public void addBus(Bus bus) {
        String sql = "INSERT INTO buses (id, name) VALUES (?, ?)";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bus.getId());
            pstmt.setString(2, bus.getName());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateBusStops(Bus bus) {
        if (bus == null) return;
        Connection conn = DBConnection.getConnection();
        if (conn == null) return;
            
        String deleteStops = "DELETE FROM stops WHERE bus_id = ?";
        try (PreparedStatement deleteStmt = conn.prepareStatement(deleteStops)) {
            deleteStmt.setInt(1, bus.getId());
            deleteStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
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
                e.printStackTrace();
            }
        }
    }

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
            e.printStackTrace();
        }
    }

    public void removeBus(int busId) {
        String sql = "DELETE FROM buses WHERE id = ?";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, busId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<Integer, Bus> getBusList() {
        Map<Integer, Bus> busList = new HashMap<>();
        String sqlBuses = "SELECT * FROM buses";
        String sqlStops = "SELECT * FROM stops ORDER BY bus_id, stop_id_seq";

        Connection conn = DBConnection.getConnection();
        if (conn == null) return busList;

        try (Statement stmtBuses = conn.createStatement();
             Statement stmtStops = conn.createStatement()) {

            ResultSet rsBuses = stmtBuses.executeQuery(sqlBuses);
            while (rsBuses.next()) {
                Bus bus = new Bus();
                bus.setId(rsBuses.getInt("id"));
                bus.setName(rsBuses.getString("name"));
                bus.setStop(new ArrayList<>());
                busList.put(bus.getId(), bus);
            }

            ResultSet rsStops = stmtStops.executeQuery(sqlStops);
            while (rsStops.next()) {
                int busId = rsStops.getInt("bus_id");
                if (busList.containsKey(busId)) {
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
                    busList.get(busId).getStops().add(stop);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return busList;
    }

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
                bus.setStop(new ArrayList<>());
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
            e.printStackTrace();
        }
        return bus;
    }

    public Ticket addTicket(Ticket ticket) {
        if (ticket == null || ticket.getPassengerPhoneNumber() == null) return null;
        
        String sql = "INSERT INTO tickets (passenger_phone_number, bus_id, bus_name, source_stop, destination_stop, price, bought_time, valid_until) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return null;

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, ticket.getPassengerPhoneNumber());
            pstmt.setInt(2, ticket.getBusId());
            pstmt.setString(3, ticket.getBusName());
            pstmt.setString(4, ticket.getSourceStop());
            pstmt.setString(5, ticket.getDestinationStop());
            pstmt.setInt(6, ticket.getPrice());
            pstmt.setTimestamp(7, ticket.getBoughtTime() != null ? Timestamp.valueOf(ticket.getBoughtTime()) : null);
            pstmt.setTimestamp(8, ticket.getValidUntil() != null ? Timestamp.valueOf(ticket.getValidUntil()) : null);
            
            pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                ticket.setTicketId(rs.getInt(1));
            }
            return ticket;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

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
                Timestamp bt = rs.getTimestamp("bought_time");
                if (bt != null) ticket.setBoughtTime(bt.toLocalDateTime());
                Timestamp vu = rs.getTimestamp("valid_until");
                if (vu != null) ticket.setValidUntil(vu.toLocalDateTime());
                tickets.add(ticket);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tickets;
    }

    public Ticket getTicketById(int ticketId) {
        String sql = "SELECT * FROM tickets WHERE ticket_id = ?";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return null;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, ticketId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Ticket ticket = new Ticket();
                ticket.setTicketId(rs.getInt("ticket_id"));
                ticket.setPassengerPhoneNumber(rs.getString("passenger_phone_number"));
                ticket.setBusId(rs.getInt("bus_id"));
                ticket.setBusName(rs.getString("bus_name"));
                ticket.setSourceStop(rs.getString("source_stop"));
                ticket.setDestinationStop(rs.getString("destination_stop"));
                ticket.setPrice(rs.getInt("price"));
                Timestamp bt = rs.getTimestamp("bought_time");
                if (bt != null) ticket.setBoughtTime(bt.toLocalDateTime());
                Timestamp vu = rs.getTimestamp("valid_until");
                if (vu != null) ticket.setValidUntil(vu.toLocalDateTime());
                return ticket;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
