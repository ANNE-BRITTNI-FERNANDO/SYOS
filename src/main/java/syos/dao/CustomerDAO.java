package syos.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.syos.infrastructure.database.ConnectionPool;

import syos.models.Customer;

/**
 * DAO implementation for Customer entity (stub)
 */
public class CustomerDAO extends AbstractDAO<Customer, Integer> {
    
    public CustomerDAO(ConnectionPool connectionPool) {
        super(connectionPool, "customers");
    }
    
    @Override
    protected String getInsertSQL() {
        return "INSERT INTO customers (customer_code, first_name, last_name, email, phone_number) VALUES (?, ?, ?, ?, ?)";
    }
    
    @Override
    protected String getSelectAllSQL() {
        return "SELECT id, customer_code, first_name, last_name, email, phone_number FROM customers";
    }
    
    @Override
    protected String getSelectByIdSQL() {
        return getSelectAllSQL() + " WHERE id = ?";
    }
    
    @Override
    protected String getUpdateSQL() {
        return "UPDATE customers SET customer_code = ?, first_name = ?, last_name = ?, email = ?, phone_number = ? WHERE id = ?";
    }
    
    @Override
    protected String getDeleteByIdSQL() {
        return "DELETE FROM customers WHERE id = ?";
    }
    
    @Override
    protected void setInsertParameters(PreparedStatement statement, Customer customer) throws SQLException {
        statement.setString(1, customer.getCustomerCode());
        statement.setString(2, customer.getFirstName());
        statement.setString(3, customer.getLastName());
        statement.setString(4, customer.getEmail());
        statement.setString(5, customer.getPhoneNumber());
    }
    
    @Override
    protected void setUpdateParameters(PreparedStatement statement, Customer customer) throws SQLException {
        statement.setString(1, customer.getCustomerCode());
        statement.setString(2, customer.getFirstName());
        statement.setString(3, customer.getLastName());
        statement.setString(4, customer.getEmail());
        statement.setString(5, customer.getPhoneNumber());
        statement.setInt(6, customer.getId());
    }
    
    @Override
    protected Customer mapResultSetToEntity(ResultSet resultSet) throws SQLException {
        Customer customer = new Customer();
        customer.setId(resultSet.getInt("id"));
        customer.setCustomerCode(resultSet.getString("customer_code"));
        customer.setFirstName(resultSet.getString("first_name"));
        customer.setLastName(resultSet.getString("last_name"));
        customer.setEmail(resultSet.getString("email"));
        customer.setPhoneNumber(resultSet.getString("phone_number"));
        return customer;
    }
    
    @Override
    protected Integer getEntityId(Customer customer) {
        return customer.getId();
    }
    
    @Override
    protected void setEntityId(Customer customer, Integer id) {
        customer.setId(id);
    }
}