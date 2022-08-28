package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.TreeMap;

import static gitlet.Repository.*;
import static gitlet.Utils.*;

public class Gitlet implements Serializable {

    public void init() {
        if (GITLET_DIR.exists()) {
            exitWithError("A Gitlet version-control system already exists in the current directory.");
        }
        createRepository();
        Commit initCommit = new Commit();
        /** Set the head pointer to master branch */
        writeContents(HEAD, join("refs", "heads", "master").getPath());
        saveCommit(initCommit, "master");

        Staging stagingArea = new Staging();
        writeObject(INDEX, stagingArea);
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
        /** Get staging area */
        Staging stagingArea = readObject(INDEX, Staging.class);
        /** Get current commit */
        Commit currCommit = getCurrentCommit();
        Blob blob = new Blob(filename, file);

        if (stagingArea.stagingAdd.containsKey(filename)) {
            stagingArea.stagingAdd.remove(filename);
        } else if (currCommit.getFileMap().containsKey(filename)) {
            stagingArea.stagingRemove.put(filename, blob.getUID());
            restrictedDelete(filename);
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
        stagingArea.clearStage();
        writeObject(INDEX, stagingArea);
    }

    public void log() {
        // TODO not taken into consideration branching
        Commit c = getCurrentCommit();
        while (c.getParentID() != null) {
            c.print();
            c = readObject(join(COMMIT_DIR, c.getParentID()), Commit.class);
        }
        c.print();

    }

    public void globalLog() {
        List<String> commits = plainFilenamesIn(COMMIT_DIR);
        for (String s : commits) {
            Commit c = readObject(join(COMMIT_DIR, s), Commit.class);
            c.print();
        }
    }

    public void find(String message) {
        List<String> commits = plainFilenamesIn(COMMIT_DIR);
        int count = 0;
        for (String s : commits) {
            Commit c = readObject(join(COMMIT_DIR, s), Commit.class);
            if (c.getCommitMessage().equals(message)) {
                System.out.println(c.getUID());
                count++;
            }
        }
        if (count == 0) {
            error("Found no commit with that message.");
        }
    }

    public void status() {
        /** Branches */
        System.out.println("=== Branches ===");
        String currBranch = new File(readContentsAsString(HEAD)).getName();
        System.out.println("*" + currBranch);
        List<String> heads = plainFilenamesIn(HEADS_DIR);
        for (String s : heads) {
            if (!s.equals(currBranch)) {
                System.out.println(s);
            }
        }
        System.out.println();

        /** Staged Files */
        Staging stagingArea = readObject(INDEX, Staging.class);
        System.out.println("=== Staged Files ===");
        for (String s : stagingArea.stagingAdd.keySet()) {
            System.out.println(s);
        }
        System.out.println();

        /** Removed Files */
        System.out.println("=== Removed Files ===");
        for (String s : stagingArea.stagingRemove.keySet()) {
            System.out.println(s);
        }
        System.out.println();

        /** Modifications not staged for commit */
        System.out.println("=== Modifications Not Staged For Commit ===");
        Commit currCommit = getCurrentCommit();
        // Tracked in the current commit, changed in the working directory, but not staged;
        for (String s : currCommit.getFileMap().keySet()) {
            File file = join(CWD, s);
            Blob blob = new Blob(s, file);
            if (!currCommit.getFileMap().get(s).equals(blob.getUID()) && !stagingArea.stagingAdd.containsKey(s)) {
                System.out.println(s + " (modified)");
            }
        }
        // Staged for addition, but with different contents than in the working directory
        for (String s : stagingArea.stagingAdd.keySet()) {
            File file = join(CWD, s);
            Blob blob = new Blob(s, file);
            if (!stagingArea.stagingAdd.get(s).equals(blob.getUID())) {
                System.out.println(s + " (modified)");
            }
        }
        // Staged for addition, but deleted in the working directory
        for (String s : stagingArea.stagingAdd.keySet()) {
            File file = join(CWD, s);
            if (!file.exists()) {
                System.out.println(s + " (deleted)");
            }
        }
        // Not staged for removal, but tracked in the current commit and deleted from the working directory
        for (String s : currCommit.getFileMap().keySet()) {
            File file = join(CWD, s);
            if (!stagingArea.stagingRemove.containsKey(s) && !file.exists()) {
                System.out.println(s + " (deleted)");
            }
        }
        System.out.println();

        /** Untracked files */
        System.out.println("=== Untracked Files ===");
        List<String> files = plainFilenamesIn(CWD);
        for (String s : files) {
            if (!stagingArea.stagingAdd.containsKey(s) && !currCommit.getFileMap().containsKey(s)) {
                System.out.println(s);
            }
        }
        System.out.println();
    }

    public void checkoutFile(String filename) {
        Commit currCommit = getCurrentCommit();
        if (!currCommit.getFileMap().containsKey(filename)) {
            exitWithError("File does not exist in that commit.");
        }
        String blobUID = currCommit.getFileMap().get(filename);
        copyFile(blobUID);
    }

    public void checkoutFileWithCommit(String commitID, String filename) {
        List<String> commits = plainFilenamesIn(COMMIT_DIR);
        if (!commits.contains(commitID)) {
            exitWithError("No commit with that id exists.");
        }
        Commit commit = readObject(join(COMMIT_DIR, commitID), Commit.class);
        if (!commit.getFileMap().containsKey(filename)) {
            exitWithError("File does not exist in that commit.");
        }
        String blobUID = commit.getFileMap().get(filename);
        copyFile(blobUID);
    }

    public void checkoutBranch(String branchName) {
        List<String> branches = plainFilenamesIn(HEADS_DIR);
        if (!branches.contains(branchName)) {
            exitWithError("No such branch exists.");
        }
        String currBranch = new File(readContentsAsString(HEAD)).getName();
        if (currBranch.equals(branchName)) {
            exitWithError("No need to checkout the current branch.");
        }
        Commit currCommit = getCurrentCommit();
        writeContents(HEAD, join("refs", "heads", branchName).getPath());
        Commit checkout = getCurrentCommit();
        for (String s : plainFilenamesIn(CWD)) {
            if (!currCommit.getFileMap().containsKey(s)) {
                if (checkout.getFileMap().containsKey(s)) {
                    exitWithError("There is an untracked file in the way; delete it, or add and commit it first.");
                }
            }
        }

        for (String s : checkout.getFileMap().values()) {
            copyFile(s);
        }
        for (String s : currCommit.getFileMap().keySet()) {
            if (!checkout.getFileMap().containsKey(s)) {
                restrictedDelete(join(CWD, s));
            }
        }
        Staging stagingArea = readObject(INDEX, Staging.class);
        stagingArea.clearStage();
        writeObject(INDEX, stagingArea);
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
        writeContents(branchname, commit.getUID());
        if (branchname.exists()) {
            writeContents(branchname, commit.getUID());
        }
        try {
            branchname.createNewFile();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private Commit getCurrentCommit() {
        String path = readContentsAsString(HEAD);
        String currCommitID = readContentsAsString(join(GITLET_DIR, path));
        return readObject(join(COMMIT_DIR, currCommitID), Commit.class);
    }

    private void copyFile(String blobUID) {
        File file = join(BLOB_DIR, blobUID);
        Blob blob = readObject(file, Blob.class);

        File newFile = join(CWD, blob.getFilename());
        if (newFile.exists()) {
            writeContents(newFile, blob.getContent());
        }

        try {
            newFile.createNewFile();
            writeContents(newFile, blob.getContent());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
