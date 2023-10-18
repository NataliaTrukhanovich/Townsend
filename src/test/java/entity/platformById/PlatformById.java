package entity.platformById;

import entity.Platform;
import lombok.Data;

import java.util.ArrayList;

@Data
public class PlatformById {
    public Platform platform;
    public ArrayList<Characteristic> characteristics;
    public ArrayList<Solution> solutions;
    public ArrayList<Review> reviews;
}
