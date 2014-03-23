package enums;

/**
 * <br/>
 * Created by Martin Sicho on 21.3.14.
 */
public enum ProgramParameters {
    PROGRAM_NAME("sbsj")
    , ORIGINAL("input")
    , ORIGINAL_METAVAR("ORIGINAL")
    , BACKUP("output")
    , BACKUP_METAVAR("BACKUP_DESTINATION")
    , NAME("name")
    , NAME_METAVAR("BACKUP_NAME")
    , SHALLOW("shallow")
    , LIST_BACKUPS("list_backups")
    , SYNCHRONIZE("synchronize")
    ;

    private final String value;

    private ProgramParameters(String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }
}
