package syos.dao;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import com.syos.infrastructure.database.ConnectionPool;

import syos.models.Product;

/**
 * DAO implementation for Product entity
 */
public class ProductDAO extends AbstractDAO<Product, Integer> {
    
    public ProductDAO(ConnectionPool connectionPool) {
        super(connectionPool, "products");
    }
    
    @Override
    protected String getInsertSQL() {
        return "INSERT INTO products (product_code, product_name, description, category_id, " +
               "brand, manufacturer, unit_price, reorder_level, barcode, unit_of_measure, " +
               "is_active, is_taxable, tax_rate, created_at, updated_at) " +
               "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
    }
    
    @Override
    protected String getSelectAllSQL() {
        return "SELECT id, product_code, product_name, description, category_id, brand, " +
               "manufacturer, unit_price, reorder_level, barcode, unit_of_measure, " +
               "is_active, is_taxable, tax_rate, created_at, updated_at FROM products";
    }
    
    @Override
    protected String getSelectByIdSQL() {
        return getSelectAllSQL() + " WHERE id = ?";
    }
    
    @Override
    protected String getUpdateSQL() {
        return "UPDATE products SET product_code = ?, product_name = ?, description = ?, " +
               "category_id = ?, brand = ?, manufacturer = ?, unit_price = ?, " +
               "reorder_level = ?, barcode = ?, unit_of_measure = ?, is_active = ?, " +
               "is_taxable = ?, tax_rate = ?, updated_at = NOW() WHERE id = ?";
    }
    
    @Override
    protected String getDeleteByIdSQL() {
        return "DELETE FROM products WHERE id = ?";
    }
    
    @Override
    protected void setInsertParameters(PreparedStatement statement, Product product) throws SQLException {
        statement.setString(1, product.getProductCode());
        statement.setString(2, product.getProductName());
        statement.setString(3, product.getDescription());
        statement.setObject(4, product.getCategoryId());
        statement.setString(5, product.getBrand());
        statement.setString(6, product.getManufacturer());
        statement.setBigDecimal(7, product.getUnitPrice());
        statement.setObject(8, product.getReorderLevel());
        statement.setString(9, product.getBarcode());
        statement.setString(10, product.getUnitOfMeasure());
        statement.setBoolean(11, product.isActive());
        statement.setBoolean(12, product.isTaxable());
        statement.setBigDecimal(13, product.getTaxRate());
    }
    
    @Override
    protected void setUpdateParameters(PreparedStatement statement, Product product) throws SQLException {
        statement.setString(1, product.getProductCode());
        statement.setString(2, product.getProductName());
        statement.setString(3, product.getDescription());
        statement.setObject(4, product.getCategoryId());
        statement.setString(5, product.getBrand());
        statement.setString(6, product.getManufacturer());
        statement.setBigDecimal(7, product.getUnitPrice());
        statement.setObject(8, product.getReorderLevel());
        statement.setString(9, product.getBarcode());
        statement.setString(10, product.getUnitOfMeasure());
        statement.setBoolean(11, product.isActive());
        statement.setBoolean(12, product.isTaxable());
        statement.setBigDecimal(13, product.getTaxRate());
        statement.setInt(14, product.getId());
    }
    
    @Override
    protected Product mapResultSetToEntity(ResultSet resultSet) throws SQLException {
        Product product = new Product();
        product.setId(resultSet.getInt("id"));
        product.setProductCode(resultSet.getString("product_code"));
        product.setProductName(resultSet.getString("product_name"));
        product.setDescription(resultSet.getString("description"));
        
        Integer categoryId = resultSet.getObject("category_id", Integer.class);
        product.setCategoryId(categoryId);
        
        product.setBrand(resultSet.getString("brand"));
        product.setManufacturer(resultSet.getString("manufacturer"));
        product.setUnitPrice(resultSet.getBigDecimal("unit_price"));
        
        Integer reorderLevel = resultSet.getObject("reorder_level", Integer.class);
        product.setReorderLevel(reorderLevel);
        
        product.setBarcode(resultSet.getString("barcode"));
        product.setUnitOfMeasure(resultSet.getString("unit_of_measure"));
        product.setActive(resultSet.getBoolean("is_active"));
        product.setTaxable(resultSet.getBoolean("is_taxable"));
        product.setTaxRate(resultSet.getBigDecimal("tax_rate"));
        
        Timestamp createdAt = resultSet.getTimestamp("created_at");
        if (createdAt != null) {
            product.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = resultSet.getTimestamp("updated_at");
        if (updatedAt != null) {
            product.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return product;
    }
    
    @Override
    protected Integer getEntityId(Product product) {
        return product.getId();
    }
    
    @Override
    protected void setEntityId(Product product, Integer id) {
        product.setId(id);
    }
    
    // Custom query methods
    
    /**
     * Find product by product code
     * @param productCode The product code to search for
     * @return Optional containing the product if found
     * @throws DatabaseException if query fails
     */
    public Optional<Product> findByProductCode(String productCode) throws DatabaseException {
        if (productCode == null || productCode.trim().isEmpty()) {
            return Optional.empty();
        }
        
        String sql = getSelectAllSQL() + " WHERE product_code = ?";
        return executeQueryForSingleResult(sql, productCode.trim().toUpperCase());
    }
    
    /**
     * Find product by barcode
     * @param barcode The barcode to search for
     * @return Optional containing the product if found
     * @throws DatabaseException if query fails
     */
    public Optional<Product> findByBarcode(String barcode) throws DatabaseException {
        if (barcode == null || barcode.trim().isEmpty()) {
            return Optional.empty();
        }
        
        String sql = getSelectAllSQL() + " WHERE barcode = ?";
        return executeQueryForSingleResult(sql, barcode);
    }
    
    /**
     * Find products by category ID
     * @param categoryId The category ID to search for
     * @return List of products in the specified category
     * @throws DatabaseException if query fails
     */
    public List<Product> findByCategoryId(Integer categoryId) throws DatabaseException {
        if (categoryId == null) {
            return List.of();
        }
        
        String sql = getSelectAllSQL() + " WHERE category_id = ?";
        return executeQuery(sql, categoryId);
    }
    
    /**
     * Find all active products
     * @return List of active products
     * @throws DatabaseException if query fails
     */
    public List<Product> findActiveProducts() throws DatabaseException {
        String sql = getSelectAllSQL() + " WHERE is_active = true";
        return executeQuery(sql);
    }
    
    /**
     * Find products that need reordering
     * @return List of products below reorder level
     * @throws DatabaseException if query fails
     */
    public List<Product> findProductsNeedingReorder() throws DatabaseException {
        String sql = getSelectAllSQL() + " p INNER JOIN inventory i ON p.id = i.product_id " +
                    "WHERE p.reorder_level IS NOT NULL AND i.quantity_on_hand <= p.reorder_level";
        return executeQuery(sql);
    }
    
    /**
     * Find products by brand
     * @param brand The brand to search for
     * @return List of products from the specified brand
     * @throws DatabaseException if query fails
     */
    public List<Product> findByBrand(String brand) throws DatabaseException {
        if (brand == null || brand.trim().isEmpty()) {
            return List.of();
        }
        
        String sql = getSelectAllSQL() + " WHERE brand = ?";
        return executeQuery(sql, brand);
    }
    
    /**
     * Find taxable products
     * @return List of taxable products
     * @throws DatabaseException if query fails
     */
    public List<Product> findTaxableProducts() throws DatabaseException {
        String sql = getSelectAllSQL() + " WHERE is_taxable = true";
        return executeQuery(sql);
    }
    
    /**
     * Search products by name or description
     * @param searchTerm The search term
     * @return List of products matching the search
     * @throws DatabaseException if query fails
     */
    public List<Product> searchProducts(String searchTerm) throws DatabaseException {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return List.of();
        }
        
        String likePattern = "%" + searchTerm.trim() + "%";
        String sql = getSelectAllSQL() + " WHERE product_name LIKE ? OR description LIKE ? OR product_code LIKE ?";
        return executeQuery(sql, likePattern, likePattern, likePattern);
    }
    
    /**
     * Find products within price range
     * @param minPrice Minimum price (inclusive)
     * @param maxPrice Maximum price (inclusive)
     * @return List of products within the specified price range
     * @throws DatabaseException if query fails
     */
    public List<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) throws DatabaseException {
        if (minPrice == null && maxPrice == null) {
            return findAll();
        }
        
        StringBuilder sql = new StringBuilder(getSelectAllSQL() + " WHERE ");
        if (minPrice != null && maxPrice != null) {
            sql.append("unit_price BETWEEN ? AND ?");
            return executeQuery(sql.toString(), minPrice, maxPrice);
        } else if (minPrice != null) {
            sql.append("unit_price >= ?");
            return executeQuery(sql.toString(), minPrice);
        } else {
            sql.append("unit_price <= ?");
            return executeQuery(sql.toString(), maxPrice);
        }
    }
    
    /**
     * Check if product code already exists
     * @param productCode The product code to check
     * @return true if product code exists, false otherwise
     * @throws DatabaseException if query fails
     */
    public boolean productCodeExists(String productCode) throws DatabaseException {
        if (productCode == null || productCode.trim().isEmpty()) {
            return false;
        }
        
        String sql = "SELECT COUNT(*) FROM products WHERE product_code = ?";
        
        try (var connection = connectionPool.getConnection();
             var statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, productCode.trim().toUpperCase());
            
            try (var resultSet = statement.executeQuery()) {
                return resultSet.next() && resultSet.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            throw new DatabaseException("Failed to check product code existence: " + e.getMessage(), e, e.getSQLState(), "CHECK_PRODUCT_CODE");
        }
    }
    
    /**
     * Update product price
     * @param productId The product ID
     * @param newPrice The new price
     * @return true if price was updated, false if product not found
     * @throws DatabaseException if update fails
     */
    public boolean updatePrice(Integer productId, BigDecimal newPrice) throws DatabaseException {
        if (productId == null || newPrice == null || newPrice.compareTo(BigDecimal.ZERO) < 0) {
            return false;
        }
        
        String sql = "UPDATE products SET unit_price = ?, updated_at = NOW() WHERE id = ?";
        return executeUpdate(sql, newPrice, productId) > 0;
    }
    
    /**
     * Activate a product
     * @param productId The product ID to activate
     * @return true if product was activated, false if not found
     * @throws DatabaseException if update fails
     */
    public boolean activateProduct(Integer productId) throws DatabaseException {
        if (productId == null) {
            return false;
        }
        
        String sql = "UPDATE products SET is_active = true, updated_at = NOW() WHERE id = ?";
        return executeUpdate(sql, productId) > 0;
    }
    
    /**
     * Deactivate a product
     * @param productId The product ID to deactivate
     * @return true if product was deactivated, false if not found
     * @throws DatabaseException if update fails
     */
    public boolean deactivateProduct(Integer productId) throws DatabaseException {
        if (productId == null) {
            return false;
        }
        
        String sql = "UPDATE products SET is_active = false, updated_at = NOW() WHERE id = ?";
        return executeUpdate(sql, productId) > 0;
    }
}