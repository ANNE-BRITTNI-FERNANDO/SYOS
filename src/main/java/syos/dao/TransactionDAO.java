package syos.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.syos.infrastructure.database.ConnectionPool;

import syos.models.Transaction;

/**
 * DAO implementation for Transaction entity (stub)
 */
public class TransactionDAO extends AbstractDAO<Transaction, Integer> {
    
    public TransactionDAO(ConnectionPool connectionPool) {
        super(connectionPool, "transactions");
    }
    
    @Override
    protected String getInsertSQL() {
        return "INSERT INTO transactions (transaction_number, transaction_type, amount, payment_method, user_id) VALUES (?, ?, ?, ?, ?)";
    }
    
    @Override
    protected String getSelectAllSQL() {
        return "SELECT id, transaction_number, transaction_type, amount, payment_method, user_id FROM transactions";
    }
    
    @Override
    protected String getSelectByIdSQL() {
        return getSelectAllSQL() + " WHERE id = ?";
    }
    
    @Override
    protected String getUpdateSQL() {
        return "UPDATE transactions SET transaction_number = ?, transaction_type = ?, amount = ?, payment_method = ?, user_id = ? WHERE id = ?";
    }
    
    @Override
    protected String getDeleteByIdSQL() {
        return "DELETE FROM transactions WHERE id = ?";
    }
    
    @Override
    protected void setInsertParameters(PreparedStatement statement, Transaction transaction) throws SQLException {
        statement.setString(1, transaction.getTransactionNumber());
        statement.setString(2, transaction.getTransactionType().name());
        statement.setBigDecimal(3, transaction.getAmount());
        statement.setString(4, transaction.getPaymentMethod().name());
        statement.setObject(5, transaction.getUserId());
    }
    
    @Override
    protected void setUpdateParameters(PreparedStatement statement, Transaction transaction) throws SQLException {
        statement.setString(1, transaction.getTransactionNumber());
        statement.setString(2, transaction.getTransactionType().name());
        statement.setBigDecimal(3, transaction.getAmount());
        statement.setString(4, transaction.getPaymentMethod().name());
        statement.setObject(5, transaction.getUserId());
        statement.setInt(6, transaction.getId());
    }
    
    @Override
    protected Transaction mapResultSetToEntity(ResultSet resultSet) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setId(resultSet.getInt("id"));
        transaction.setTransactionNumber(resultSet.getString("transaction_number"));
        transaction.setTransactionType(Transaction.TransactionType.valueOf(resultSet.getString("transaction_type")));
        transaction.setAmount(resultSet.getBigDecimal("amount"));
        transaction.setPaymentMethod(Transaction.PaymentMethod.valueOf(resultSet.getString("payment_method")));
        transaction.setUserId(resultSet.getInt("user_id"));
        return transaction;
    }
    
    @Override
    protected Integer getEntityId(Transaction transaction) {
        return transaction.getId();
    }
    
    @Override
    protected void setEntityId(Transaction transaction, Integer id) {
        transaction.setId(id);
    }
}