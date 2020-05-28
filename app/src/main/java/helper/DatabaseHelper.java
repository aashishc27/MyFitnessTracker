package helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;


public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Document.db";
    public static final String TABLE_NAME = "selected_documents";
    public static final String COL_1 = "id";
    public static final String COL_2 = "category";
    public static final String COL_3 = "type";
    public static final String COL_4 = "path";
    public static final String COL_5 = "loan_code";
    public static final String COL_6 = "password";
    Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT,category TEXT,type TEXT,path TEXT UNIQUE,loan_code TEXT,password TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String loan_code, String category, String type, String path, int isPasswordProtected) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COL_2, category);
        contentValues.put(COL_3, type);
        contentValues.put(COL_4, path);
        contentValues.put(COL_5, loan_code);
        long result = db.insert(TABLE_NAME, null, contentValues);
        if (result == -1) {
            Util.showToast("Document already selected", context);
            return false;
        } else
            return true;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        return res;
    }

    public boolean updateData(String path, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_6, password);
        db.update(TABLE_NAME, contentValues, "path = ?", new String[]{path});
        return true;
    }

    public Integer deleteData(String path) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "path = ?", new String[]{path});
    }

    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_NAME);
       // Util.showToast("all documents deleted",context);
    }

    public String getTableAsString() {

        SQLiteDatabase db = this.getWritableDatabase();
        String tableString = String.format("Table %s:\n", TABLE_NAME);
        Cursor allRows = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        if (allRows.moveToFirst()) {
            String[] columnNames = allRows.getColumnNames();
            do {
                for (String name : columnNames) {
                    tableString += String.format("%s: %s\n", name,
                            allRows.getString(allRows.getColumnIndex(name)));
                }
                tableString += "\n";

            } while (allRows.moveToNext());
        }

        return tableString;
    }

}
