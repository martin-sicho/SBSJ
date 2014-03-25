package views;

import java.util.Date;
import java.util.List;

/**
 * <br/>
 * Created by Martin Sicho on 25.3.14.
 */
public interface BackupViewer {
    void printHeader();
    void listBackup(String name, Date date, boolean shallow);
    String getName();
    void setName(String name);
}
