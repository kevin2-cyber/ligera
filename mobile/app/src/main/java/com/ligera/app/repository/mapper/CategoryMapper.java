package com.ligera.app.repository.mapper;

import com.ligera.app.db.entity.CategoryEntity;
import com.ligera.app.network.model.response.CategoryResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for Category models
 */
public class CategoryMapper {

    /**
     * Map CategoryResponse to CategoryEntity
     *
     * @param response CategoryResponse from network
     * @return CategoryEntity for database
     */
    public static CategoryEntity mapResponseToEntity(CategoryResponse response) {
        if (response == null) {
            return null;
        }
        
        CategoryEntity entity = new CategoryEntity();
        entity.setId(response.getId());
        entity.setName(response.getName());
        entity.setDescription(response.getDescription());
        entity.setImage(response.getImage());
        entity.setParentId(response.getParentId());
        entity.setProductCount(response.getProductCount());
        entity.setLastRefreshed(System.currentTimeMillis());
        
        return entity;
    }
    
    /**
     * Map list of CategoryResponse to list of CategoryEntity
     *
     * @param responses List of CategoryResponse from network
     * @return List of CategoryEntity for database
     */
    public static List<CategoryEntity> mapResponseToEntity(List<CategoryResponse> responses) {
        if (responses == null) {
            return new ArrayList<>();
        }
        
        return responses.stream()
                .map(CategoryMapper::mapResponseToEntity)
                .collect(Collectors.toList());
    }
}

