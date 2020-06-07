package helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;


public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Profile.db";
    public static final String TABLE_NAME = "user_details";
    public static final String TABLE_NAME_2 = "selected_food";
    public static final String COL_1 = "id";
    public static final String COL_3 = "weight";
    public static final String COL_4 = "height";
    public static final String COL_5 = "age";
    public static final String COL_6 = "gender";
    public static final String COL_7 = "activity";
    public static final String COL_8 = "food";
    public static final String COL_9 = "goals";


    public static final String T2_COL_1 = "protein";
    public static final String T2_COL_2= "carbs";
    public static final String T2_COL_3 = "fats";

    Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.context = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT,weight TEXT,height TEXT,age TEXT,gender TEXT,activity TEXT,food TEXT,goals TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String user_code,String weight, String height, String age, String gender, String activity,String food,String goal) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();


        contentValues.put(COL_3, weight);
        contentValues.put(COL_4, height);
        contentValues.put(COL_5, age);
        contentValues.put(COL_6, gender);
        contentValues.put(COL_7, activity);
        contentValues.put(COL_8, food);
        contentValues.put(COL_9,goal);


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


    public boolean storeInDB(ArrayList<String> protein,ArrayList<String> carbs,ArrayList<String> fats) {

        JSONObject json = new JSONObject();
        try {
            json.put("protein", new JSONArray(protein));
            json.put("carbs", new JSONArray(carbs));
            json.put("fats", new JSONArray(fats));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String arrayList = json.toString();

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(T2_COL_2,arrayList);

        long result = db.insert(TABLE_NAME, null, contentValues);
        if (result == -1) {
            Util.showToast("Document already selected", context);
            return false;
        } else
            return true;
    }



}
