package gitlet;

import java.io.File;
import java.io.IOException;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @LiZhu TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** The directory to store all objects. */
    public static final File OBJECTS_DIR = join(GITLET_DIR, "objects");

    public static void init() throws IOException {
        if (Repository.GITLET_DIR.exists()) {
            Utils.exitWithError("A Gitlet version-control system already exists in the current directory.");
        }
        GITLET_DIR.mkdir();
        OBJECTS_DIR.mkdir();
        storeCommit(new Commit());

        // Get the current working directory
        // Create a new commit with no parent
        // Branches? HEAD?
        // UID?
    }

    private static void storeCommit (Commit a) throws IOException {
        String commitName = sha1(serialize(a));
        File dir = join(OBJECTS_DIR, commitName.substring(0, 2));
        dir.mkdir();
        File file = join(dir, commitName);
        file.createNewFile();
        writeObject(file, a);
    }

    public static void commit() {
        // Clone the HEAD commit
        // Modify its message and timestamp according to user input
        // Use the staging area in order to modify the files tracked by the new commit
        // Write back any new object made or any modified objects read earlier
    }

    public static void main(String[] args) {

    }


}
