package com.legacies.legacies.Tool;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;


public class SqliteSetting extends SQLiteOpenHelper {

    private Context context;

    public static String namaTabel ="Setting";
    public static String key_sys = "sysID";
    public static String key_id ="ID";
    public static String key_value ="Value";

    public SqliteSetting(Context context) {
        super(context, "Legacies_db", null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }

    public boolean simpan (String id, String value){

        SQLiteDatabase db = this.getWritableDatabase();
        if(!isTableExists(namaTabel,db)){
            BuatTabelJadwal();
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(key_id, id);
        contentValues.put(key_value, value);

        long result;
        if(!DataSudahAda(namaTabel,key_id,id,db)) {
            result = db.insert(namaTabel,null,contentValues);
            Log.d("ACTION", "NEW");
        }else{
            result = db.update(namaTabel,contentValues,key_id + " = ?", new String[] {id});
            Log.d("ACTION", "UPDATE");
        }

        Log.d("Result", "" + result);

        if (result == -1) {
            Log.d("Result", "GAGAL");
            return false;
        } else {
            Log.d("Result", "SIMPAN");
            return true;
        }
    }

    public ArrayList<String> ambil () {

        ArrayList<String> result = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + namaTabel;

        SQLiteDatabase db = this.getWritableDatabase();
        Log.d("Path",db.getPath());
        if(isTableExists(namaTabel,db)) {

            Cursor cursor = db.rawQuery(selectQuery, null);

            while (cursor.moveToNext()) {
                String hasil = "";
                for (int i = 0 ; i < cursor.getColumnCount();i++){
                    hasil = hasil + Character.toString((char) 240) + cursor.getString(i);
                }
                hasil = hasil.substring(1);
                Log.d("HASIL",hasil);
                result.add(hasil);
            }
            cursor.close();
            return result;
        }
        else{
            return null;
        }
    }

    public String ambil1 (String id) {
        String result = null;
        String selectQuery = "SELECT "+ key_value +" FROM " + namaTabel + " WHERE " + key_id + " ='" + id + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        if(isTableExists(namaTabel,db)) {

            Cursor cursor = db.rawQuery(selectQuery, null);

            while (cursor.moveToNext()) {

                for (int i = 0 ; i < cursor.getColumnCount();i++){
                    result = cursor.getString(i);
                }
            }
            if(result!=null) {
                Log.d("RESULT", result);
            }
            cursor.close();
            return result;
        }
        else{
            return null;
        }
    }


    public void hapus(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        if(DataSudahAda(namaTabel,key_id,id,db)){
            long result = db.delete(namaTabel,key_id + " = ?", new String[] {id});
            Log.d("Result", "" + result);

            if (result == -1) {
                Toast.makeText(context, "", Toast.LENGTH_SHORT).show();    //TODO
            } else {
                Toast.makeText(context, "", Toast.LENGTH_SHORT).show();    //TODO
            }
        }
        else{
            Toast.makeText(context, "", Toast.LENGTH_SHORT).show();        //TODO
        }
    }

    public boolean isTableExists(String tableName, SQLiteDatabase sqLiteDatabase) {

        if(sqLiteDatabase == null || !sqLiteDatabase.isOpen()) {
            sqLiteDatabase = getReadableDatabase();
        }

        if(!sqLiteDatabase.isReadOnly()) {
            sqLiteDatabase = getReadableDatabase();
        }

        Cursor cursor = sqLiteDatabase.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"+tableName+"'", null);
        if(cursor!=null) {
            if(cursor.getCount()>0) {
                return true;
            }
        }

        return false;
    }

    public void BuatTabelJadwal(){
        SQLiteDatabase ourDatabase = this.getWritableDatabase();
        String buatTabelBaru = "create table if not exists "+ namaTabel +" ("+ key_sys + " integer primary key autoincrement, " + key_id + " text, " + key_value + " text)";
        ourDatabase.execSQL(buatTabelBaru);
    }


    public boolean DataSudahAda (String NamaTabel, String KeyFile , String isi , SQLiteDatabase db){
        String query = "select * from " + NamaTabel + " where " + KeyFile + " = '" + isi +"'";
        Cursor data = db.rawQuery(query,null);

        if(data!=null) {
            if(data.getCount()>0) {
                return true;
            }
        }
        if (data != null) {
            data.close();
        }
        return false;
    }

}