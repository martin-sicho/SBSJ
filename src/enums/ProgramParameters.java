package enums;

/**
 * <br/>
 * Created by Martin Sicho on 21.3.14.
 */
public enum ProgramParameters {
    PROGRAM_NAME("sbsj")
    , ORIGINAL("input")
    , ORIGINAL_METAVAR("ORIGINAL_PATH")
    , BACKUP("output")
    , BACKUP_METAVAR("BACKUP_DESTINATION")
    , NAME("name")
    , NAME_METAVAR("BACKUP_NAME")
    , SHALLOW("shallow")
    , LIST_BACKUPS("list_backups")
    , SYNCHRONIZE("synchronize")
    , KEEP_ALL("keep_all")
    ;

    private final String value;

    private ProgramParameters(String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }
}
