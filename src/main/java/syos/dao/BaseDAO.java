package syos.dao;

import java.util.List;
import java.util.Optional;

/**
 * Base DAO interface providing common CRUD operations
 * @param <T> The entity type
 * @param <ID> The entity ID type
 */
public interface BaseDAO<T, ID> {
    
    /**
     * Create a new entity
     * @param entity The entity to create
     * @return The created entity with generated ID
     * @throws DatabaseException if creation fails
     */
    T create(T entity) throws DatabaseException;
    
    /**
     * Find an entity by its ID
     * @param id The entity ID
     * @return Optional containing the entity if found, empty otherwise
     * @throws DatabaseException if query fails
     */
    Optional<T> findById(ID id) throws DatabaseException;
    
    /**
     * Find all entities
     * @return List of all entities
     * @throws DatabaseException if query fails
     */
    List<T> findAll() throws DatabaseException;
    
    /**
     * Update an existing entity
     * @param entity The entity to update
     * @return The updated entity
     * @throws DatabaseException if update fails
     */
    T update(T entity) throws DatabaseException;
    
    /**
     * Delete an entity by its ID
     * @param id The entity ID
     * @return true if entity was deleted, false if not found
     * @throws DatabaseException if deletion fails
     */
    boolean deleteById(ID id) throws DatabaseException;
    
    /**
     * Delete an entity
     * @param entity The entity to delete
     * @return true if entity was deleted, false if not found
     * @throws DatabaseException if deletion fails
     */
    boolean delete(T entity) throws DatabaseException;
    
    /**
     * Check if an entity exists by its ID
     * @param id The entity ID
     * @return true if entity exists, false otherwise
     * @throws DatabaseException if query fails
     */
    boolean existsById(ID id) throws DatabaseException;
    
    /**
     * Count all entities
     * @return The total number of entities
     * @throws DatabaseException if query fails
     */
    long count() throws DatabaseException;
    
    /**
     * Find entities with pagination
     * @param offset The number of entities to skip
     * @param limit The maximum number of entities to return
     * @return List of entities for the specified page
     * @throws DatabaseException if query fails
     */
    List<T> findWithPagination(int offset, int limit) throws DatabaseException;
}