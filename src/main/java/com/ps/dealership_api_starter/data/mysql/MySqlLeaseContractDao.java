package com.ps.dealership_api_starter.data.mysql;

import com.ps.dealership_api_starter.data.LeaseContractDao;
import com.ps.dealership_api_starter.models.LeaseContract;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MySqlLeaseContractDao extends MySqlDaoBase implements LeaseContractDao {

    public MySqlLeaseContractDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public LeaseContract getById(int contractId) {
        String sql = "SELECT * FROM lease_contracts WHERE contract_id = ?";
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, contractId);

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
    public LeaseContract create(LeaseContract leaseContract) {
        String sql = "INSERT INTO lease_contracts(contract_date, customer_name, customer_email, vin, sales_tax, recording_fee, processing_fee, total_price, monthly_payment) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setString(1, leaseContract.getContractDate());
            statement.setString(2, leaseContract.getCustomerName());
            statement.setString(3, leaseContract.getCustomerEmail());
            statement.setInt(4, leaseContract.getVin());
            statement.setDouble(5, leaseContract.getSalesTax());
            statement.setDouble(6, leaseContract.getRecordingFee());
            statement.setDouble(7, leaseContract.getProcessingFee());
            statement.setDouble(8, leaseContract.getTotalPrice());
            statement.setDouble(9, leaseContract.getMonthlyPayment());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int contractId = generatedKeys.getInt(1);
                    return getById(contractId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return null;
    }

    private LeaseContract mapRow(ResultSet row) throws SQLException {
        int contractId = row.getInt("contract_id");
        String contractDate = row.getString("contract_date");
        String customerName = row.getString("customer_name");
        String customerEmail = row.getString("customer_email");
        int vin = row.getInt("vin");
        double salesTax = row.getDouble("sales_tax");
        double recordingFee = row.getDouble("recording_fee");
        double processingFee = row.getDouble("processing_fee");
        double totalPrice = row.getDouble("total_price");
        double monthlyPayment = row.getDouble("monthly_payment");

        return new LeaseContract(contractId, contractDate, customerName, customerEmail, vin, salesTax, recordingFee, processingFee, totalPrice, monthlyPayment);
    }
}

