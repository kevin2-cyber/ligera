package com.ligera.app.repository.mapper;

import com.ligera.app.db.entity.ProductEntity;
import com.ligera.app.network.model.response.ProductResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for Product models
 */
public class ProductMapper {

    /**
     * Map ProductResponse to ProductEntity
     *
     * @param response ProductResponse from network
     * @return ProductEntity for database
     */
    public static ProductEntity mapResponseToEntity(ProductResponse response) {
        if (response == null) {
            return null;
        }
        
        ProductEntity entity = new ProductEntity();
        entity.setId(response.getId());
        entity.setName(response.getName());
        entity.setDescription(response.getDescription());
        entity.setPrice(response.getPrice());
        entity.setImage(response.getImage());
        entity.setImages(response.getImages());
        entity.setCategoryId(response.getCategoryId());
        entity.setCategoryName(response.getCategoryName());
        entity.setRating(response.getRating());
        entity.setReviewCount(response.getReviewCount());
        entity.setInStock(response.isInStock());
        entity.setQuantity(response.getQuantity());
        entity.setBrand(response.getBrand());
        entity.setSize(response.getSize());
        entity.setColor(response.getColor());
        entity.setFeatured(response.isFeatured());
        entity.setPopular(response.isPopular());
        entity.setDiscountPercentage(response.getDiscountPercentage());
        entity.setTags(response.getTags());
        entity.setCreatedAt(response.getCreatedAt());
        entity.setUpdatedAt(response.getUpdatedAt());
        entity.setLastRefreshed(System.currentTimeMillis());
        
        return entity;
    }
    
    /**
     * Map list of ProductResponse to list of ProductEntity
     *
     * @param responses List of ProductResponse from network
     * @return List of ProductEntity for database
     */
    public static List<ProductEntity> mapResponseToEntity(List<ProductResponse> responses) {
        if (responses == null) {
            return new ArrayList<>();
        }
        
        return responses.stream()
                .map(ProductMapper::mapResponseToEntity)
                .collect(Collectors.toList());
    }
}

