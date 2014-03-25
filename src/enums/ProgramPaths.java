package enums;

/**
 * This enum class stores any constant paths related to the program functionality.
 *
 * <br/>
 * Created by Martin Sicho on 21.3.14.
 */
public enum ProgramPaths {
    BACKUPS_DIR("backups/");

    private final String value;

    private ProgramPaths(String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }
}
