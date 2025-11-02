package com.ligera.app.repository.mapper;

import android.util.Log;
import com.ligera.app.model.entity.Product;
import com.ligera.app.network.model.response.ProductResponse;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for Product models
 */
public class ProductMapper {

    private static final String TAG = "ProductMapper";

    /**
     * Map ProductResponse to Product
     *
     * @param response ProductResponse from network
     * @return Product for database
     */
    public static Product mapResponseToEntity(ProductResponse response) {
        if (response == null) {
            return null;
        }
        
        Product entity = new Product();
        entity.setId(response.getId());
        entity.setName(response.getName());
        entity.setDescription(response.getDescription());
        entity.setPrice(response.getPrice());
        entity.setImageUrl(response.getImage());
        entity.setImageUrls(response.getImages());
        entity.setCategoryId(response.getCategoryId());
        entity.setRating(response.getRating());
        entity.setQuantity(response.getQuantity());
        entity.setBrand(response.getBrand());
        entity.setSize(response.getSize());
        entity.setFeatured(response.isFeatured());
        entity.setPopular(response.isPopular());
        entity.setTags(response.getTags());

        // Handle date conversion from String to long
        if (response.getCreatedAt() != null && !response.getCreatedAt().isEmpty()) {
            try {
                entity.setCreatedAt(Instant.parse(response.getCreatedAt()).toEpochMilli());
            } catch (DateTimeParseException e) {
                Log.e(TAG, "Could not parse createdAt date: " + response.getCreatedAt(), e);
                entity.setCreatedAt(System.currentTimeMillis()); // Fallback
            }
        }

        if (response.getUpdatedAt() != null && !response.getUpdatedAt().isEmpty()) {
            try {
                entity.setLastUpdated(Instant.parse(response.getUpdatedAt()).toEpochMilli());
            } catch (DateTimeParseException e) {
                Log.e(TAG, "Could not parse updatedAt date: " + response.getUpdatedAt(), e);
                entity.setLastUpdated(System.currentTimeMillis()); // Fallback
            }
        }
        
        entity.setLastRefreshed(System.currentTimeMillis());
        
        return entity;
    }
    
    /**
     * Map list of ProductResponse to list of Product
     *
     * @param responses List of ProductResponse from network
     * @return List of Product for database
     */
    public static List<Product> mapResponseToEntity(List<ProductResponse> responses) {
        if (responses == null) {
            return new ArrayList<>();
        }
        
        return responses.stream()
                .map(ProductMapper::mapResponseToEntity)
                .collect(Collectors.toList());
    }
}
