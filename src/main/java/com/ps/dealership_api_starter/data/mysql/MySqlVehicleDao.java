package com.ps.dealership_api_starter.data.mysql;

import com.ps.dealership_api_starter.data.VehicleDao;
import com.ps.dealership_api_starter.models.Vehicle;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlVehicleDao extends MySqlDaoBase implements VehicleDao {
    public MySqlVehicleDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public List<Vehicle> search(Double minPrice, Double maxPrice, String make, String model, Integer minYear, Integer maxYear, String color, Integer minMiles, Integer maxMiles, String type) {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT * FROM vehicles WHERE " +
                "(? IS NULL OR price >= ?) AND " +
                "(? IS NULL OR price <= ?) AND " +
                "(? IS NULL OR make LIKE ?) AND " +
                "(? IS NULL OR model LIKE ?) AND " +
                "(? IS NULL OR year >= ?) AND " +
                "(? IS NULL OR year <= ?) AND " +
                "(? IS NULL OR color LIKE ?) AND " +
                "(? IS NULL OR odometer >= ?) AND " +
                "(? IS NULL OR odometer <= ?) AND " +
                "(? IS NULL OR vehicle_type LIKE ?)";

        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setObject(1, minPrice);
            statement.setObject(2, minPrice);
            statement.setObject(3, maxPrice);
            statement.setObject(4, maxPrice);
            statement.setObject(5, make);
            statement.setString(6, make == null ? null : "%" + make + "%");
            statement.setObject(7, model);
            statement.setString(8, model == null ? null : "%" + model + "%");
            statement.setObject(9, minYear);
            statement.setObject(10, minYear);
            statement.setObject(11, maxYear);
            statement.setObject(12, maxYear);
            statement.setObject(13, color);
            statement.setString(14, color == null ? null : "%" + color + "%");
            statement.setObject(15, minMiles);
            statement.setObject(16, minMiles);
            statement.setObject(17, maxMiles);
            statement.setObject(18, maxMiles);
            statement.setObject(19, type);
            statement.setString(20, type == null ? null : "%" + type + "%");

            ResultSet row = statement.executeQuery();

            while (row.next()) {
                Vehicle vehicle = mapRow(row);
                vehicles.add(vehicle);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return vehicles;
    }

    @Override
    public Vehicle getById(int vin) {
        String sql = "SELECT * FROM vehicles WHERE vin = ?";
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, vin);

            ResultSet row = statement.executeQuery();

            if (row.next()) {
                return mapRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public Vehicle create(Vehicle vehicle) {
        String sql = "INSERT INTO vehicles(vin, year, make, model, vehicle_type, color, odometer, price, sold) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, vehicle.getVin());
            statement.setInt(2, vehicle.getYear());
            statement.setString(3, vehicle.getMake());
            statement.setString(4, vehicle.getModel());
            statement.setString(5, vehicle.getVehicleType());
            statement.setString(6, vehicle.getColor());
            statement.setInt(7, vehicle.getOdometer());
            statement.setDouble(8, vehicle.getPrice());
            statement.setBoolean(9, vehicle.isSold());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                return getById(vehicle.getVin());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public void update(int vin, Vehicle vehicle) {
        String sql = "UPDATE vehicles SET " +
                "year = ?, " +
                "make = ?, " +
                "model = ?, " +
                "vehicle_type = ?, " +
                "color = ?, " +
                "odometer = ?, " +
                "price = ?, " +
                "sold = ? " +
                "WHERE vin = ?";

        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, vehicle.getYear());
            statement.setString(2, vehicle.getMake());
            statement.setString(3, vehicle.getModel());
            statement.setString(4, vehicle.getVehicleType());
            statement.setString(5, vehicle.getColor());
            statement.setInt(6, vehicle.getOdometer());
            statement.setDouble(7, vehicle.getPrice());
            statement.setBoolean(8, vehicle.isSold());
            statement.setInt(9, vin);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int vin) {
        String sql = "DELETE FROM vehicles WHERE vin = ?";

        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, vin);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    protected static Vehicle mapRow(ResultSet row) throws SQLException {
        int vin = row.getInt("vin");
        int year = row.getInt("year");
        String make = row.getString("make");
        String model = row.getString("model");
        String vehicleType = row.getString("vehicle_type");
        String color = row.getString("color");
        int odometer = row.getInt("odometer");
        double price = row.getDouble("price");
        boolean sold = row.getBoolean("sold");

        return new Vehicle(vin, year, make, model, vehicleType, color, odometer, price, sold);
    }
}

