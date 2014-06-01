package views;

import javax.swing.table.DefaultTableCellRenderer;
import java.text.DateFormat;
import java.util.Locale;

/**
 * This class renders all dates in the table using the {@link java.text.DateFormat#FULL} format.
 *
 * <br />
 * Created by Martin Sicho on 30.5.2014.
 */
class BackupTableDateRenderer extends DefaultTableCellRenderer {
    private DateFormat mDateFormatter = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.DEFAULT, new Locale("en", "GB"));

    /**
     * Sets the <code>String</code> object for the cell being rendered to
     * <code>value</code>.
     *
     * @param value the string value for this cell; if value is
     *              <code>null</code> it sets the text value to an empty string
     * @see javax.swing.JLabel#setText
     */
    @Override
    protected void setValue(Object value) {
        setText((value == null) ? "" : mDateFormatter.format(value));
    }
}
