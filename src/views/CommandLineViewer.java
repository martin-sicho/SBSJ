package views;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Implementation of the {@link views.BackupViewer} interface.
 * Delivers information to the user via command line.
 *
 * <br/>
 * Created by Martin Sicho on 25.3.14.
 */
public class CommandLineViewer implements BackupViewer {
    private String mName;
    private int mWidth = 80;
    private DateFormat mDateFormatter = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.DEFAULT, new Locale("en", "GB")); 

    public CommandLineViewer() {
        this.mName = null;
    }

    /**
     * This method just prints the header for the output information table.
     */
    @Override
    public void printHeader() {
        printLine(mWidth);
        String format_string = "%-" + mWidth / 4 + "s %-" + mWidth / 2  + "s %-" + mWidth / 4 + "s%n";
        System.out.printf(format_string, "Name", "Last Synchronized", "Shallow");
        printLine(mWidth);
    }

    /**
     * This mathod takes information about a backup from a
     * {@link backupmanagment.BackupManager BackupManager} instance.
     * The information is then listed in the form of a table.
     *
     * @param name backup name
     * @param date date of last synchronization
     * @param shallow whether the backup was scheduled as shallow
     */
    @Override
    public void listBackup(String name, Date date, boolean shallow) {
        String format_string = "%-" + mWidth / 4 + "s %-" + mWidth / 2  + "s %-" + mWidth / 4 + "s%n";
        System.out.printf(format_string, name, mDateFormatter.format(date), shallow);
    }

    // getters

    @Override
    public String getName() {
        return mName;
    }

    // setters

    @Override
    public void setName(String name) {
        this.mName = name;
    }

    // internal private methods

    private void printLine(int width) {
        for (int i = 0; i < width; i++) {
            System.out.print("-");
        }
        System.out.println();
    }
}
