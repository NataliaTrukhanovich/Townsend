package entity.filters;

import lombok.Data;

import java.util.ArrayList;
@Data
public class FilterGroup {
    String id;
    String filterGroupName;
    ArrayList<Filter> filtersForPlatforms;
}
