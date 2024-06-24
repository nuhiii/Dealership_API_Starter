package com.ps.dealership_api_starter.data.mysql;

import com.ps.dealership_api_starter.data.SalesContractDao;
import com.ps.dealership_api_starter.models.SalesContract;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MySqlSalesContractDao extends MySqlDaoBase implements SalesContractDao {

    public MySqlSalesContractDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public SalesContract getById(int contractId) {
        String sql = "SELECT * FROM sales_contracts WHERE contract_id = ?";
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
    public SalesContract create(SalesContract salesContract) {
        String sql = "INSERT INTO sales_contracts(contract_date, customer_name, customer_email, vin, sales_tax, recording_fee, processing_fee, total_price, finance_option, monthly_payment) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setString(1, salesContract.getContractDate());
            statement.setString(2, salesContract.getCustomerName());
            statement.setString(3, salesContract.getCustomerEmail());
            statement.setInt(4, salesContract.getVin());
            statement.setDouble(5, salesContract.getSalesTax());
            statement.setDouble(6, salesContract.getRecordingFee());
            statement.setDouble(7, salesContract.getProcessingFee());
            statement.setDouble(8, salesContract.getTotalPrice());
            statement.setString(9, salesContract.getFinanceOption());
            statement.setDouble(10, salesContract.getMonthlyPayment());

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

    private SalesContract mapRow(ResultSet row) throws SQLException {
        int contractId = row.getInt("contract_id");
        String contractDate = row.getString("contract_date");
        String customerName = row.getString("customer_name");
        String customerEmail = row.getString("customer_email");
        int vin = row.getInt("vin");
        double salesTax = row.getDouble("sales_tax");
        double recordingFee = row.getDouble("recording_fee");
        double processingFee = row.getDouble("processing_fee");
        double totalPrice = row.getDouble("total_price");
        String financeOption = row.getString("finance_option");
        double monthlyPayment = row.getDouble("monthly_payment");

        return new SalesContract(contractId, contractDate, customerName, customerEmail, vin, salesTax, recordingFee, processingFee, totalPrice, financeOption, monthlyPayment);
    }
}

