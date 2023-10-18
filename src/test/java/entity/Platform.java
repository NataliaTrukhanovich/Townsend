package entity;

import lombok.Data;

@Data
public class Platform {
    String id;
    Boolean isActive;
    String name;
    String description;
    String urlWebsite;
    Integer priceMin;
    Integer priceMax;
    Integer numberOfSolution;
    String fileUrl;
    String adminId;
    Boolean isFavorites;
}
