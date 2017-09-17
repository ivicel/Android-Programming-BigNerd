package info.ivicel.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import info.ivicel.criminalintent.CrimeDbSchema.CrimeTable;

/**
 * Created by sedny on 11/09/2017.
 */

public class CrimeLab {
    private static CrimeLab sCrimeLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;
    
    public static CrimeLab get(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }
    
    private CrimeLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new CrimeBaseHelper(context).getWritableDatabase();
    }
    
    public List<Crime> getCrimes() {
        List<Crime> crimes = new ArrayList<>();
        CrimeCursorWrapper wrapper = queryCrimes(null, null);
        try {
            wrapper.moveToFirst();
            while (!wrapper.isAfterLast()) {
                crimes.add(wrapper.getCrime());
                wrapper.moveToNext();
            }
        } finally {
            wrapper.close();
        }
        return crimes;
    }
    
    public Crime getCrime(UUID id) {
        CrimeCursorWrapper wrapper = queryCrimes(CrimeTable.Cols.UUID + " = ?",
                new String[]{id.toString()});
        try {
            if (wrapper.getCount() == 0) {
                return null;
            }
            wrapper.moveToFirst();
            return wrapper.getCrime();
        } finally {
            wrapper.close();
        }
    }
    
    public void updateCrime(Crime crime) {
        ContentValues values = getContentValues(crime);
        mDatabase.update(CrimeTable.NAME, values, CrimeTable.Cols.UUID + " = ?",
                new String[]{crime.getId().toString()});
    }
    
    public void addCrime(Crime c) {
        ContentValues values = getContentValues(c);
        mDatabase.insert(CrimeTable.NAME, null, values);
    }
    
    private static ContentValues getContentValues(Crime crime) {
        ContentValues values = new ContentValues();
        values.put(CrimeTable.Cols.UUID, crime.getId().toString());
        values.put(CrimeTable.Cols.TITLE, crime.getTitle());
        values.put(CrimeTable.Cols.DATE, crime.getDate().getTime());
        values.put(CrimeTable.Cols.SOLVED, crime.isSolved() ? 1 : 0);
        values.put(CrimeTable.Cols.SUSPECT, crime.getSuspect());
        return values;
    }
    
    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(CrimeTable.NAME, null, whereClause,
                whereArgs, null, null, null);
        return new CrimeCursorWrapper(cursor);
    }
    
    public File getPhotoFile(Crime crime) {
        File fileDir = mContext.getFilesDir();
        return new File(fileDir, crime.getPhotoFilename());
    }
}
