package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.TreeMap;

import static gitlet.Utils.*;

public class Gitlet implements Serializable {

    private String head;
    private String headBranch; // Active branch
    private HashMap<String, String> branches; //<Key=branch, value=commitID> split point???
    private TreeMap<String, String> commits; // Key=commit ID; value = commitMessage?
    private Staging stagingArea;

    public Gitlet() {
        this.head = null;
        this.headBranch = null;
        this.branches = new HashMap<>();
        this.commits = new TreeMap<>();
        this.stagingArea = new Staging();
    }

    static void init() {
        Repository.createRepository();
        Commit initCommit = new Commit();

    }




}
