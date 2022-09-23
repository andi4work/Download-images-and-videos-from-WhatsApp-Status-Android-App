package vid.img.download.data.model;

/**
 * Created by CMR Labs on 14/2/17.
 */

public class SampleModel {
    private String name;
    private String description;

    public SampleModel(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
