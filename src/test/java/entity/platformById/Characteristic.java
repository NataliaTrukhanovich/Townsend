package entity.platformById;

import lombok.Data;

import java.util.ArrayList;
@Data
public class Characteristic {
    public String id;
    public String filterGroupName;
    public ArrayList<FiltersForPlatform> filtersForPlatforms;
}
