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
public final class CommandLineViewer implements BackupViewer {
    private String mBackupName;
    private int mWidth = 120;
    private int[] mTableRatios = {7,4,4,12,3};
    private int[] mDataMaxLengths = new int[5];
    private int mRowHeight = 1;
    private DateFormat mDateFormatter = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.DEFAULT, new Locale("en", "GB"));

    public CommandLineViewer() {
        // no action
    }

    /**
     * This method just prints the header for the output information table.
     */
    public void printHeader() {
        //printDashedLine(mWidth);
        System.out.println();
        String format_string = "%-" + mWidth / mTableRatios[0] + "s %-"
                + mWidth / mTableRatios[1]  + "s %-" + mWidth / mTableRatios[2]
                + "s %-" + mWidth / mTableRatios[3] + "s %-" + mWidth / mTableRatios[4] + "s%n";
        System.out.printf(format_string, "Name", "Original Directory", "Backup Directory" , "Shallow", "Last Synchronized");
        printDashedLine(mWidth);
    }

    /**
     * This mathod takes information about a backup from a
     * {@link backupmanagment.BackupManager BackupManager} instance.
     * The information is then listed in the form of a table.
     *  @param name backup name
     * @param shallow whether the backup was scheduled as shallow
     * @param date date of last synchronization
     */
    @Override
    public void showBackupInfo(String name, String original, String backup, boolean shallow, Date date) {
        String[] data = new String[5];
        data[0] = name;
        data[4] = mDateFormatter.format(date);
        data[3] = shallow ? "Yes." : "No.";
        data[1] = original;
        data[2] = backup;

        mDataMaxLengths[0] = name.length();
        mDataMaxLengths[4] = mDateFormatter.format(date).length();
        mDataMaxLengths[3] = data[3].length();
        mDataMaxLengths[1] = original.length();
        mDataMaxLengths[2] = backup.length();

        mRowHeight = computeRowHeight();

        for (String i : retrieveFormattedRows(data)) {
            System.out.print(i);
        }
    }

    // getters

    @Override
    public String getBackupName() {
        return mBackupName;
    }

    // setters

    @Override
    public void setBackupName(String name) {
        mBackupName = name;
    }

    // internal private methods

    private void printDashedLine(int width) {
        for (int i = 0; i < width - 1; i++) {
            System.out.print("-");
        }
        System.out.println();
    }

    private int computeRowHeight() {
        int max = 0;
        for (int i = 0; i < mDataMaxLengths.length; i++) {
            int current = mDataMaxLengths[i] / (mWidth / mTableRatios[i]);
            mDataMaxLengths[i] = mWidth / mTableRatios[i];
            if (current + 1 > max) {
                max = current + 1;
            }
        }
        return max;
    }

    private String[] retrieveFormattedRows(String[] data) {
        String[] data_temp = data.clone();
        String[] rows = new String[mRowHeight];
        for (int row_idx = 0; row_idx < rows.length; row_idx++) {
            String row = "%-";
            for (int data_idx = 0; data_idx < data.length; data_idx++) {
                if (data[data_idx].length() > mDataMaxLengths[data_idx]) {
                    row = row + mDataMaxLengths[data_idx] + "s %-";
                    data_temp[data_idx] = data[data_idx].substring(0, mDataMaxLengths[data_idx]);
                    data[data_idx] = data[data_idx].substring(mDataMaxLengths[data_idx]);
                } else {
                    row = row + mDataMaxLengths[data_idx] + "s %-";
                    data_temp[data_idx] = data[data_idx];
                    data[data_idx] = "";
                }
            }
            row = row.substring(0, row.length() - ("s %-".length() - 1));
            row = String.format(row + "%n", data_temp[0], data_temp[1], data_temp[2], data_temp[3], data_temp[4]);
            rows[row_idx] = row;
        }
        return rows;
    }
}
