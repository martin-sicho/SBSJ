package enums;

/**
 * <br/>
 * Created by Martin Sicho on 21.3.14.
 */
public enum ProgramPaths {
    BACKUPS_DIR("backups/");

    private String value;

    private ProgramPaths(String value) {
        this.value = value;
    }

    public String get() {
        return this.value;
    }
}
