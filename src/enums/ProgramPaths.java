package enums;

import java.nio.file.Paths;

/**
 * This enum class stores any constant paths related to the program functionality.
 *
 * <br/>
 * Created by Martin Sicho on 21.3.14.
 */
public enum ProgramPaths {
    BACKUPS_DIR(Paths.get(System.getProperty("user.home"), "SBSJ_backups").toString() + "/");

    private final String value;

    private ProgramPaths(String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }
}
