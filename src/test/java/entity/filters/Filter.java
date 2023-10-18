package entity.filters;

import lombok.Data;

import java.util.ArrayList;
@Data
public class Filter {
    public String id;
    public String inputType;
    public String placeholderFrom;
    public String placeholderTo;
    public boolean topPriority;
    public String filterName;
    public String descriptionLong;
    public ArrayList<FilterItems> filterItemsForPlatforms;
}
