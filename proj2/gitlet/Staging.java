package gitlet;

import java.io.Serializable;
import java.util.HashMap;

public class Staging implements Serializable {
    private HashMap<String, Blob> stagingAdd;
    private HashMap<String, Blob> stagingDelete;

    public Staging() {
        this.stagingAdd = new HashMap<>();
        this.stagingDelete = new HashMap<>();
    }

    public void clear() {
        this.stagingAdd = new HashMap<>();
        this.stagingDelete = new HashMap<>();
    }
}
