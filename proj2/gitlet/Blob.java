package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.Arrays;

import static gitlet.Utils.*;


public class Blob implements Serializable {

    private String UID;
    private String shortUID = this.UID.substring(0, 5);
    private File directory;
    private String filename;
    private byte[] content;

    public Blob(String filename, File dir) {
        this.filename = filename;
        this.directory = join(dir, filename);
        if (!this.directory.exists()) {
            this.content = null;
        }
        this.content = readContents(this.directory);
        this.UID = sha1(this.filename + this.content);
    }

    public String getUID() {
        return UID;
    }

    public String getshortUID() {
        return shortUID;
    }

    public File getDirectory() {
        return directory;
    }

    public String getFilename() {
        return filename;
    }

    public byte[] getContent() {
        return content;
    }


}
