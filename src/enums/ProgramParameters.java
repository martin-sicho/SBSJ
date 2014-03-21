package enums;

/**
 * <br/>
 * Created by Martin Sicho on 21.3.14.
 */
public enum ProgramParameters {
    PROGRAM_NAME("sbsj")
    , INPUT("input")
    , INPUT_METAVAR("SOURCE")
    , OUTPUT("output")
    , OUTPUT_METAVAR("DESTINATION")
    , NAME("name")
    , NAME_METAVAR("BACKUP_NAME")
    , SHALLOW("shallow")
    , LIST_BACKUPS("list_backups")
    ;

    private String value;

    private ProgramParameters(String value) {
        this.value = value;
    }

    public String get() {
        return this.value;
    }
}
