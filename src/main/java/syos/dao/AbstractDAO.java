package syos.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.syos.infrastructure.database.ConnectionPool;

/**
 * Abstract base DAO implementation providing common database operations
 * @param <T> The entity type
 * @param <ID> The entity ID type
 */
public abstract class AbstractDAO<T, ID> implements BaseDAO<T, ID> {
    
    protected final ConnectionPool connectionPool;
    protected final String tableName;
    
    protected AbstractDAO(ConnectionPool connectionPool, String tableName) {
        this.connectionPool = connectionPool;
        this.tableName = tableName;
    }
    
    /**
     * Get the SQL for inserting a new entity
     * @return SQL insert statement
     */
    protected abstract String getInsertSQL();
    
    /**
     * Get the SQL for selecting all entities
     * @return SQL select statement
     */
    protected abstract String getSelectAllSQL();
    
    /**
     * Get the SQL for selecting by ID
     * @return SQL select statement with ID parameter
     */
    protected abstract String getSelectByIdSQL();
    
    /**
     * Get the SQL for updating an entity
     * @return SQL update statement
     */
    protected abstract String getUpdateSQL();
    
    /**
     * Get the SQL for deleting by ID
     * @return SQL delete statement with ID parameter
     */
    protected abstract String getDeleteByIdSQL();
    
    /**
     * Set parameters for insert statement
     * @param statement The prepared statement
     * @param entity The entity to insert
     * @throws SQLException if parameter setting fails
     */
    protected abstract void setInsertParameters(PreparedStatement statement, T entity) throws SQLException;
    
    /**
     * Set parameters for update statement
     * @param statement The prepared statement
     * @param entity The entity to update
     * @throws SQLException if parameter setting fails
     */
    protected abstract void setUpdateParameters(PreparedStatement statement, T entity) throws SQLException;
    
    /**
     * Map a result set row to an entity
     * @param resultSet The result set positioned at a row
     * @return The mapped entity
     * @throws SQLException if mapping fails
     */
    protected abstract T mapResultSetToEntity(ResultSet resultSet) throws SQLException;
    
    /**
     * Get the ID from an entity
     * @param entity The entity
     * @return The entity ID
     */
    protected abstract ID getEntityId(T entity);
    
    /**
     * Set the ID on an entity (used after insert)
     * @param entity The entity
     * @param id The generated ID
     */
    protected abstract void setEntityId(T entity, ID id);
    
    @Override
    public T create(T entity) throws DatabaseException {
        if (entity == null) {
            throw new DatabaseException("Entity cannot be null", "NULL_ENTITY", "CREATE");
        }
        
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(getInsertSQL(), Statement.RETURN_GENERATED_KEYS)) {
            
            setInsertParameters(statement, entity);
            
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new DatabaseException("Creating entity failed, no rows affected", "NO_ROWS_AFFECTED", "CREATE");
            }
            
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    // Handle the case where MySQL returns BigInteger for auto-generated IDs
                    Object generatedKeyObj = generatedKeys.getObject(1);
                    ID generatedId;
                    
                    if (generatedKeyObj instanceof Number) {
                        @SuppressWarnings("unchecked")
                        ID id = (ID) Integer.valueOf(((Number) generatedKeyObj).intValue());
                        generatedId = id;
                    } else {
                        @SuppressWarnings("unchecked")
                        ID id = (ID) generatedKeyObj;
                        generatedId = id;
                    }
                    
                    setEntityId(entity, generatedId);
                    return entity;
                } else {
                    throw new DatabaseException("Creating entity failed, no ID obtained", "NO_ID_GENERATED", "CREATE");
                }
            }
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to create entity: " + e.getMessage(), e, e.getSQLState(), "CREATE");
        }
    }
    
    @Override
    public Optional<T> findById(ID id) throws DatabaseException {
        if (id == null) {
            return Optional.empty();
        }
        
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(getSelectByIdSQL())) {
            
            statement.setObject(1, id);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapResultSetToEntity(resultSet));
                }
                return Optional.empty();
            }
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find entity by ID: " + e.getMessage(), e, e.getSQLState(), "FIND_BY_ID");
        }
    }
    
    @Override
    public List<T> findAll() throws DatabaseException {
        List<T> entities = new ArrayList<>();
        
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(getSelectAllSQL());
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                entities.add(mapResultSetToEntity(resultSet));
            }
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find all entities: " + e.getMessage(), e, e.getSQLState(), "FIND_ALL");
        }
        
        return entities;
    }
    
    @Override
    public T update(T entity) throws DatabaseException {
        if (entity == null) {
            throw new DatabaseException("Entity cannot be null", "NULL_ENTITY", "UPDATE");
        }
        
        ID id = getEntityId(entity);
        if (id == null) {
            throw new DatabaseException("Entity ID cannot be null for update", "NULL_ID", "UPDATE");
        }
        
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(getUpdateSQL())) {
            
            setUpdateParameters(statement, entity);
            
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new DatabaseException("Entity not found for update", "ENTITY_NOT_FOUND", "UPDATE");
            }
            
            return entity;
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update entity: " + e.getMessage(), e, e.getSQLState(), "UPDATE");
        }
    }
    
    @Override
    public boolean deleteById(ID id) throws DatabaseException {
        if (id == null) {
            return false;
        }
        
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(getDeleteByIdSQL())) {
            
            statement.setObject(1, id);
            
            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete entity by ID: " + e.getMessage(), e, e.getSQLState(), "DELETE");
        }
    }
    
    @Override
    public boolean delete(T entity) throws DatabaseException {
        if (entity == null) {
            return false;
        }
        
        ID id = getEntityId(entity);
        return deleteById(id);
    }
    
    @Override
    public boolean existsById(ID id) throws DatabaseException {
        return findById(id).isPresent();
    }
    
    @Override
    public long count() throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM " + tableName;
        
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            
            if (resultSet.next()) {
                return resultSet.getLong(1);
            }
            return 0;
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count entities: " + e.getMessage(), e, e.getSQLState(), "COUNT");
        }
    }
    
    @Override
    public List<T> findWithPagination(int offset, int limit) throws DatabaseException {
        if (offset < 0 || limit <= 0) {
            throw new DatabaseException("Invalid pagination parameters", "INVALID_PAGINATION", "FIND_PAGINATED");
        }
        
        String sql = getSelectAllSQL() + " LIMIT ? OFFSET ?";
        List<T> entities = new ArrayList<>();
        
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, limit);
            statement.setInt(2, offset);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    entities.add(mapResultSetToEntity(resultSet));
                }
            }
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find entities with pagination: " + e.getMessage(), e, e.getSQLState(), "FIND_PAGINATED");
        }
        
        return entities;
    }
    
    /**
     * Execute a custom query and map results to entities
     * @param sql The SQL query
     * @param parameters The query parameters
     * @return List of entities
     * @throws DatabaseException if query fails
     */
    protected List<T> executeQuery(String sql, Object... parameters) throws DatabaseException {
        List<T> entities = new ArrayList<>();
        
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            for (int i = 0; i < parameters.length; i++) {
                statement.setObject(i + 1, parameters[i]);
            }
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    entities.add(mapResultSetToEntity(resultSet));
                }
            }
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to execute query: " + e.getMessage(), e, e.getSQLState(), "QUERY");
        }
        
        return entities;
    }
    
    /**
     * Execute a custom query and return a single result
     * @param sql The SQL query
     * @param parameters The query parameters
     * @return Optional containing the entity if found
     * @throws DatabaseException if query fails
     */
    protected Optional<T> executeQueryForSingleResult(String sql, Object... parameters) throws DatabaseException {
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            for (int i = 0; i < parameters.length; i++) {
                statement.setObject(i + 1, parameters[i]);
            }
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapResultSetToEntity(resultSet));
                }
                return Optional.empty();
            }
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to execute query for single result: " + e.getMessage(), e, e.getSQLState(), "QUERY_SINGLE");
        }
    }
    
    /**
     * Execute an update/delete query
     * @param sql The SQL statement
     * @param parameters The statement parameters
     * @return Number of affected rows
     * @throws DatabaseException if execution fails
     */
    protected int executeUpdate(String sql, Object... parameters) throws DatabaseException {
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            for (int i = 0; i < parameters.length; i++) {
                statement.setObject(i + 1, parameters[i]);
            }
            
            return statement.executeUpdate();
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to execute update: " + e.getMessage(), e, e.getSQLState(), "UPDATE_QUERY");
        }
    }
}