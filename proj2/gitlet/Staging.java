package gitlet;

import java.io.Serializable;
import java.util.HashMap;

/** Represents the gitlet staging area.
 *
 *  @LiZhu
 */
public class Staging implements Serializable {
    public HashMap<String, String> stagingAdd; // <filename, UID>
    public HashMap<String, String> stagingRemove;

    public Staging() {
        this.stagingAdd = new HashMap<>();
        this.stagingRemove = new HashMap<>();
    }

    public void clearStage(){
        stagingAdd.clear();
        stagingRemove.clear();
    }
}
