package entity.platformById;

import lombok.Data;

import java.util.ArrayList;
@Data
public class FiltersForPlatform {
    public String id;
    public String filterName;
    public ArrayList<FilterItemsForPlatform> filterItemsForPlatforms;
}
