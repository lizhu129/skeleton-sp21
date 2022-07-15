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
    public static final File HEAD = join(GITLET_DIR, "HEAD");
    public static final File INDEX = join(GITLET_DIR, "index"); // Staging area
    public static final File REFS_HEADS = join(GITLET_DIR, "refs", "heads");
    public static final File LOGS = join(GITLET_DIR, "logs"); //???


    public static void makeDirectory() throws IOException {
        if (GITLET_DIR.exists()) {
            Utils.exitWithError("A Gitlet version-control system already exists in the current directory.");
        }
        GITLET_DIR.mkdir();
        OBJECTS_DIR.mkdir();
        REFS_HEADS.mkdir();
        HEAD.createNewFile();
        writeContents(HEAD, "master");
        // TODO: REFS_HEADS

    }
    public static void storeCommit (Commit a) throws IOException {
        String commitName = sha1(serialize(a));
        File dir = join(OBJECTS_DIR, commitName.substring(0, 2));
        dir.mkdir();
        File file = join(dir, commitName);
        file.createNewFile();
        writeObject(file, a);

        Dumpable obj = Utils.readObject(file, Dumpable.class);
        obj.dump();
        System.out.println("---");
    }



    public static void main(String[] args) {

    }


}
