package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import static gitlet.Utils.join;

public class Repository {

    public static final File CWD = new File(System.getProperty("user.dir"));
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File COMMIT_DIR = join(CWD, "commits");
    public static final File BLOB_DIR = join(CWD, "blobs");
    public static final File INDEX = join(CWD, "INDEX");
    public static final File HEAD = join(CWD, "HEAD");
    public static final File BRANCH = join(CWD, "BRANCH");


    static void createRepository() {
        GITLET_DIR.mkdir();
        COMMIT_DIR.mkdir();
        BLOB_DIR.mkdir();
        try {
            INDEX.createNewFile();
            HEAD.createNewFile();
            BRANCH.createNewFile();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static void saveObject(Object o) {
        if (o instanceof Commit) {
            Commit commit = (Commit) o;
            File file = join(COMMIT_DIR, commit.getshortUID());
            try {
                file.createNewFile();
                Utils.writeObject(file, commit);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if (o instanceof Blob) {
            Blob blob = (Blob) o;
            File file = join(BLOB_DIR, blob.getshortUID());
            try {
                file.createNewFile();
                Utils.writeObject(file, blob);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }






}
