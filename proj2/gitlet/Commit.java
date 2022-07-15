package gitlet;

// TODO: any imports you need here

import java.io.Serializable;
import java.util.Date; // TODO: You'll likely use this in this class
import java.util.HashMap;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @LiZhu TODO
 */
public class Commit implements Serializable, Dumpable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private Date date;
    // Something that keeps track of what file this commit is tracking
    private String commitMessage;
    private String parentID;
    private HashMap<String, String> files;

    public Commit(String commitMessage, String parentID) {
        this.date = new Date(System.currentTimeMillis());
        this.commitMessage = commitMessage;
        this.parentID = parentID;
        this.files = new HashMap<>();
    }

    public Commit() {
        this.date = new Date(System.currentTimeMillis());
        this.commitMessage = "initial commit";
        this.parentID = null;
        this.files = null;
    }


    @Override
    public void dump() {
        System.out.printf("date: %s%nmessage: %s%n", date , commitMessage);
    }
}
