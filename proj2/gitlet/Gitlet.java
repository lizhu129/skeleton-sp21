package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.TreeMap;

import static gitlet.Repository.*;
import static gitlet.Utils.*;

public class Gitlet implements Serializable {
    private TreeMap<String, String> commitTree; // Key=commit ID; value = commitMessage? Not sure...

    public Gitlet() {
        this.commitTree = new TreeMap<>();
    }

    public void init() {
        if (GITLET_DIR.exists()) {
            exitWithError("A Gitlet version-control system already exists in the current directory.");
        }
        createRepository();
        Commit initCommit = new Commit();
        // Set the head pointer to master branch
        writeContents(HEAD, join("refs", "heads", "master").getPath());
        saveCommit(initCommit, "master");

        Staging stagingArea = new Staging();
        writeObject(INDEX, stagingArea);
        // ?????????? not sure
        this.commitTree.put(initCommit.getUID(), initCommit.getCommitMessage());
    }

    public void add(String filename) {
        File file = join(CWD, filename);
        if (!file.exists()) {
            exitWithError("File does not exist.");
        }
        Blob blob = new Blob(filename, file);

        // get staging area
        Staging stagingArea = readObject(INDEX, Staging.class);

        // If the current working version of the file is identical to the version in the current commit,
        // do not stage it to be added, and remove it from the staging area if it is already there
        /** Get the current commit */
        Commit currCommit = getCurrentCommit();

        if (blob.getUID().equals(currCommit.getFileMap().get(filename))) {
            if (stagingArea.stagingAdd.containsKey(filename)) {
                stagingArea.stagingAdd.remove(filename);
            }
        } else {
            stagingArea.stagingAdd.put(filename, blob.getUID());
            blob.storeBlob();
        }

        writeObject(INDEX, stagingArea);

    }

    public void rm(String filename) {
        File file = join(CWD, filename);
        Staging stagingArea = readObject(INDEX, Staging.class);
        Commit currCommit = getCurrentCommit();
        Blob blob = new Blob(filename, file);

        if (stagingArea.stagingAdd.containsKey(filename)) {
            stagingArea.stagingAdd.remove(filename);
        } else if (currCommit.getFileMap().containsKey(filename)) {
            stagingArea.stagingRemove.put(filename, blob.getUID());
            restrictedDelete(file);
        } else {
            exitWithError("No reason to remove the file.");
        }

        writeObject(INDEX, stagingArea);

    }


    public void commit(String message) {

    }



    private void saveCommit(Commit commit, String branch) {
        /** Save commit as an object in commits folder */
        File file = join(COMMIT_DIR, commit.getUID());
        try {
            file.createNewFile();
            writeObject(file, commit);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        /** Store current branch head */
        File branchname = join(HEADS_DIR, branch);
        try {
            branchname.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        writeContents(branchname, commit.getUID());
    }


    private Commit getCurrentCommit() {
        String path = readContentsAsString(HEAD);
        String currCommitID = readContentsAsString(join(GITLET_DIR, path));
        return readObject(join(COMMIT_DIR, currCommitID), Commit.class);
    }


}
