package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.TreeMap;

import static gitlet.Repository.*;
import static gitlet.Utils.*;

public class Gitlet implements Serializable {
    private TreeMap<String, Commit> commitTree; // Key=commit ID; value = commitMessage? Not sure...

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
        this.commitTree.put(initCommit.getUID(), initCommit);
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

        // TODO Maybe stagingRemove doesn't have to be HashMap, HashSet instead

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
        Staging stagingArea = readObject(INDEX, Staging.class);
        if (stagingArea.stagingAdd.isEmpty() && stagingArea.stagingRemove.isEmpty()) {
            throw error("No changes added to the commit.");
        }
        if (message.isEmpty()) {
            throw error("Please enter a commit message.");
        }
        Commit currCommit = getCurrentCommit();
        Commit newCommit = new Commit(message, currCommit.getUID(), currCommit.getFileMap());

        /** Update fileMap in the current Commit */
        for (String a : stagingArea.stagingAdd.keySet()) {
            newCommit.getFileMap().put(a, stagingArea.stagingAdd.get(a));
        }

        for (String a : stagingArea.stagingRemove.keySet()) {
            newCommit.getFileMap().remove(a);
        }

        /** Get current branch */
        String currBranch = new File(readContentsAsString(HEAD)).getName();

        saveCommit(newCommit, currBranch);
        this.commitTree.put(newCommit.getUID(), newCommit);
        stagingArea.clearStage();
        writeObject(INDEX, stagingArea);
    }

    public void log() {
        // TODO not taken into consideration branching
        for (String s : this.commitTree.keySet()) {
            System.out.println("===");
            System.out.println("commit " + s);
            System.out.println("Date: " + this.commitTree.get(s).getDate());
            System.out.println(this.commitTree.get(s).getCommitMessage());
            System.out.println();
        }
    }

    /** Helper methods */

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
