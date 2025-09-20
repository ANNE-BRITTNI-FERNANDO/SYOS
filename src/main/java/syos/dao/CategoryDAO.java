package syos.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.syos.infrastructure.database.ConnectionPool;

import syos.models.Category;

/**
 * DAO implementation for Category entity (stub)
 */
public class CategoryDAO extends AbstractDAO<Category, Integer> {
    
    public CategoryDAO(ConnectionPool connectionPool) {
        super(connectionPool, "categories");
    }
    
    @Override
    protected String getInsertSQL() {
        return "INSERT INTO categories (category_code, category_name, description, parent_category_id, is_active) VALUES (?, ?, ?, ?, ?)";
    }
    
    @Override
    protected String getSelectAllSQL() {
        return "SELECT id, category_code, category_name, description, parent_category_id, is_active FROM categories";
    }
    
    @Override
    protected String getSelectByIdSQL() {
        return getSelectAllSQL() + " WHERE id = ?";
    }
    
    @Override
    protected String getUpdateSQL() {
        return "UPDATE categories SET category_code = ?, category_name = ?, description = ?, parent_category_id = ?, is_active = ? WHERE id = ?";
    }
    
    @Override
    protected String getDeleteByIdSQL() {
        return "DELETE FROM categories WHERE id = ?";
    }
    
    @Override
    protected void setInsertParameters(PreparedStatement statement, Category category) throws SQLException {
        statement.setString(1, category.getCategoryCode());
        statement.setString(2, category.getCategoryName());
        statement.setString(3, category.getDescription());
        statement.setObject(4, category.getParentCategoryId());
        statement.setBoolean(5, category.isActive());
    }
    
    @Override
    protected void setUpdateParameters(PreparedStatement statement, Category category) throws SQLException {
        statement.setString(1, category.getCategoryCode());
        statement.setString(2, category.getCategoryName());
        statement.setString(3, category.getDescription());
        statement.setObject(4, category.getParentCategoryId());
        statement.setBoolean(5, category.isActive());
        statement.setInt(6, category.getId());
    }
    
    @Override
    protected Category mapResultSetToEntity(ResultSet resultSet) throws SQLException {
        Category category = new Category();
        category.setId(resultSet.getInt("id"));
        category.setCategoryCode(resultSet.getString("category_code"));
        category.setCategoryName(resultSet.getString("category_name"));
        category.setDescription(resultSet.getString("description"));
        Integer parentId = resultSet.getObject("parent_category_id", Integer.class);
        category.setParentCategoryId(parentId);
        category.setActive(resultSet.getBoolean("is_active"));
        return category;
    }
    
    @Override
    protected Integer getEntityId(Category category) {
        return category.getId();
    }
    
    @Override
    protected void setEntityId(Category category, Integer id) {
        category.setId(id);
    }
}