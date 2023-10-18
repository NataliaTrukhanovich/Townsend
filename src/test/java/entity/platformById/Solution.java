package entity.platformById;

import lombok.Data;

@Data
public class Solution {
        public String id;
        public String name;
        public String description;
        public int priceMin;
        public boolean isFavorites;
}
