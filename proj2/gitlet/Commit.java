package gitlet;


import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

import static gitlet.Utils.sha1;


/** Represents a gitlet commit object.
 *
 *  @LiZhu
 */
public class Commit implements Serializable, Dumpable {
    private String UID;
    private String shortUID = this.UID.substring(0, 5);
    private Date date;
    private String commitMessage;
    private String parentID;
    private HashMap<String, String> fileMap; // <filename, UID>

    public Commit(String commitMessage, String parentID, HashMap<String, String> fileMap) {
        this.date = new Date(System.currentTimeMillis());
        this.commitMessage = commitMessage;
        this.parentID = parentID;
        this.fileMap = fileMap;
        this.UID = sha1(this.date + this.commitMessage + this.parentID);
    }

    public Commit() {
        this.date = new Date(0);
        this.commitMessage = "initial commit";
        this.parentID = null;
        this.fileMap = null;
        this.UID = sha1(this.date + this.commitMessage);
    }

    public String getUID() {
        return UID;
    }

    public String getshortUID() {
        return shortUID;
    }

    public Date getDate() {
        return date;
    }

    public String getCommitMessage() {
        return commitMessage;
    }

    public String getParentID() {
        return parentID;
    }

    public HashMap<String, String> getFileMap() {
        return fileMap;
    }

    // For testing purpose
    @Override
    public void dump() {
        System.out.printf("date: %s%nmessage: %s%n", date , commitMessage);
    }

}
