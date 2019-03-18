package jmt.com.myapplication.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import jmt.com.myapplication.models.ToDoList;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "GroupWorkHelper.db";
    private static final int DB_VER = 1;
    private static final String DB_TABLE = "Task";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERID = "UserID";
    private static final String COLUMN_NAME = "Name";
    private static final String COLUMN_DESCRIPTION = "Description";
    private static final String COLUMN_DATE = "Date";
    private static final String COLUMN_STATUS = "Status";

    public DBHelper(Context context,
                    String name,
                    SQLiteDatabase.CursorFactory factory,
                    int version) {
        super(context, DB_NAME, factory, DB_VER);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = " CREATE TABLE " + DB_TABLE + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERID + " TEXT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_STATUS + " INTEGER " +
                ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS " + DB_TABLE);
        onCreate(db);
    }

    public Long addTask(ToDoList toDoList) {

        int status;
        if (toDoList.isStatus()) {
            status = 1;
        } else {
            status = 0;
        }
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERID, toDoList.getUserID());
        values.put(COLUMN_NAME, toDoList.getName());
        values.put(COLUMN_DESCRIPTION, toDoList.getDesciption());
        values.put(COLUMN_DATE, toDoList.getDate());
        values.put(COLUMN_STATUS, status);
        SQLiteDatabase db = getWritableDatabase();
        Long result = db.insert(DB_TABLE, null, values);
        db.close();
        return result;
    }

    public int updateTask(ToDoList toDoList){
        int status;
        if (toDoList.isStatus()) {
            status = 1;
        } else {
            status = 0;
        }
        int id = toDoList.getId();
        String user = toDoList.getUserID();
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STATUS, status);

        // updating row
        int result = db.update(DB_TABLE, values, COLUMN_USERID + " = ? AND " + COLUMN_ID+" = ? ",
                new String[]{String.valueOf(toDoList.getUserID()), String.valueOf(toDoList.getId())});
        db.close();
        return result;
    }

    public void deleteTask(int id){
        String whereClause = "_id=?";
        String [] whereArgs = new String []{id+""};
        SQLiteDatabase db = getWritableDatabase();
        db.delete(DB_TABLE,whereClause,whereArgs);
        db.close();
    }

    public ArrayList<ToDoList> getTaskList(String userID){
        ArrayList<ToDoList> taskList = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String selectQuery = "Select * from "+ DB_TABLE + " where UserID=" + "\"" + userID + "\"";
        Cursor cursor = db.rawQuery(selectQuery,null);
        while(cursor.moveToNext()){
//
            boolean status = true;
            if(Integer.parseInt(cursor.getString(5)) == 0){
                status = false;
            }

            ToDoList todoLists= new ToDoList();
            todoLists.setId(Integer.parseInt(cursor.getString(0)));
            todoLists.setUserID(cursor.getString(1));
            todoLists.setName(cursor.getString(2));
            todoLists.setDesciption(cursor.getString(3));
            todoLists.setDate(cursor.getString(4));
            todoLists.setStatus(status);
            taskList.add(todoLists);
        }
        cursor.close();
        db.close();
        return taskList;
    }
}
