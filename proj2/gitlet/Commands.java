package gitlet;

import java.io.IOException;

import static gitlet.Repository.*;

public class Commands {

    public static void init() throws IOException {
        makeDirectory();
        Commit initialCommit = new Commit();
        storeCommit(initialCommit);
    }


    public static void commit() {
        // Clone the HEAD commit
        // Modify its message and timestamp according to user input
        // Use the staging area in order to modify the files tracked by the new commit
        // Write back any new object made or any modified objects read earlier
    }
}
