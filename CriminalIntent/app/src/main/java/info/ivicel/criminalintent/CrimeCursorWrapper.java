package info.ivicel.criminalintent;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.Date;
import java.util.UUID;

import info.ivicel.criminalintent.CrimeDbSchema.CrimeTable;
/**
 * Created by Ivicel on 15/09/2017.
 */

public class CrimeCursorWrapper extends CursorWrapper {
    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public CrimeCursorWrapper(Cursor cursor) {
        super(cursor);
    }
    
    public Crime getCrime() {
        String uuidString = getString(getColumnIndex(CrimeTable.Cols.UUID));
        String title = getString(getColumnIndex(CrimeTable.Cols.TITLE));
        long date = getLong(getColumnIndex(CrimeTable.Cols.DATE));
        int isSolved = getInt(getColumnIndex(CrimeTable.Cols.SOLVED));
        Crime crime = new Crime(UUID.fromString(uuidString));
        crime.setDate(new Date(date));
        crime.setTitle(title);
        crime.setSolved(isSolved != 0);
        return crime;
    }
}
