package mh.manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import mh.manager.models.ModelStatus;

/**
 * Created by man.ha on 7/27/2017.
 */

public class LoginDatabase extends SQLiteOpenHelper {
    private static String DBName = "sqllogin.sqlite";
    private static int versionDB = 1;
    private SQLiteDatabase _database;
    public LoginDatabase(Context context) {
        super(context, DBName, null, versionDB);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE  TABLE \"login\" (\"id\" VARCHAR PRIMARY KEY  NOT NULL , \"username\" VARCHAR, \"email\" VARCHAR, \"departments\" VARCHAR, \"token\" VARCHAR)");
        db.execSQL("CREATE  TABLE \"status\" (\"id\" VARCHAR PRIMARY KEY  NOT NULL , \"name\" VARCHAR, \"state\" VARCHAR)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean insertLoain(String id, String username, String email, String departments, String token){
        try{
            Log.i("lluu", id + username + email +departments );
            _database = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("id", id);
            values.put("username", username);
            values.put("email", email);
            values.put("departments", departments);
            values.put("token", token);
            _database.insert("login", null, values);

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            close();
        }
        return true;

    }

    public boolean insertStatus(String id, String name, String state){
        try{
            _database = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("id", id);
            values.put("name", name);
            values.put("state", state);
            _database.insert("status", null, values);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            close();
        }

        return true;
    }

    public ArrayList<ModelStatus> getInforStatus(){
        _database = this.getWritableDatabase();
        ArrayList<ModelStatus> listModelStatus = new ArrayList<>();
        try {
            Cursor cursor = _database.rawQuery("SELECT id, name, state from status", null);  //WHERE state='open'
            ModelStatus modelStatus ;
            while (cursor.moveToNext()){
                if(cursor.getCount() > 0) {
                    modelStatus = new ModelStatus(cursor.getString(0), cursor.getString(1), cursor.getString(2));
                    listModelStatus.add(modelStatus);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            close();
        }
        return  listModelStatus;
    }

    public List<String> getListStatus(){
        _database = this.getWritableDatabase();
        List<String> name = new ArrayList<>();
        try {
            Cursor cursor = _database.rawQuery("SELECT id, name, state from status", null);  //WHERE state='open'
            while (cursor.moveToNext()){
                if(cursor.getCount() > 0) {
                    name.add(cursor.getString(1));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            close();
        }
        return  name;
    }

    public String getIdStatus(String nameStatus){
        _database = this.getWritableDatabase();
        String strName = "";
        try {
            Cursor cursor = _database.rawQuery("SELECT * from status WHERE name ='"+nameStatus+"'", null);  //WHERE state='open'
            if(cursor.getCount() > 0) {
                cursor.moveToFirst();
                strName = cursor.getString(cursor.getColumnIndex("id"));
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            close();
        }
        Log.i("strName===>", strName);
        return  strName;
    }

    public String getId(){
        _database = this.getWritableDatabase();
        String strId = "";
        try {
            Cursor cursor = _database.rawQuery("SELECT * from login", null);  //WHERE state='open'
            if(cursor.getCount() > 0) {
                cursor.moveToFirst();
                strId = cursor.getString(cursor.getColumnIndex("id"))+"&token="+cursor.getString(cursor.getColumnIndex("token"));
                cursor.close();
            }else{
                strId = "0";
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            close();
        }
        Log.i("strId===>", strId);
        return  strId;
    }

    public String getLinkDetail(){
        _database = this.getWritableDatabase();
        String strLink = "";
        try {
            Cursor cursor = _database.rawQuery("SELECT * from login", null);  //WHERE state='open'
            if(cursor.getCount() > 0) {
                cursor.moveToFirst();
                strLink = "token="+cursor.getString(cursor.getColumnIndex("token"))+"&agentId="+cursor.getString(cursor.getColumnIndex("id"));
                cursor.close();
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            close();
        }
        Log.i("strLink===>", strLink);
        return  "get-list-ticket?"+strLink;
    }

    public Integer getCountData(){
        _database = this.getWritableDatabase();
        Integer intCount = null;
        try {
            Cursor cursor = _database.rawQuery("SELECT count(*) from login", null);  //WHERE state='open'
            if(cursor.getCount() > 0) {
                cursor.moveToFirst();
                intCount = cursor.getInt(0);
                cursor.close();
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            close();
        }
        Log.i("strId===>", intCount.toString());
        return  intCount;
    }


    public JSONArray getInforUser(){
        _database = this.getWritableDatabase();
        String id = null;
        String username = null;
        String email = null;
        String departments = null;
        String token = null;
        try {
            Cursor csPO = _database.rawQuery("SELECT * from login", null);
            if(csPO.getCount() > 0) {
                csPO.moveToFirst();
                id = csPO.getString(csPO.getColumnIndex("id"));
                username = csPO.getString(csPO.getColumnIndex("username"));
                email = csPO.getString(csPO.getColumnIndex("email"));
                departments = csPO.getString(csPO.getColumnIndex("departments"));
                token = csPO.getString(csPO.getColumnIndex("token"));
                csPO.close();
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            close();
        }

        JSONArray jsonArray = new JSONArray();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id",id);
            jsonObject.put("username",username);
            jsonObject.put("email",email);
            jsonObject.put("departments",departments);
            jsonObject.put("token", token);
            jsonArray.put(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        String data = "{data:[{"+"id:"+id+",username:"+username+",email:"+email+",departments:"+departments+"}]}";

        return  jsonArray;

    }

    public String getOneIdLogin(){
        _database = this.getWritableDatabase();
        String id =null;
        String token = null;
        try{
            Cursor cursor = _database.rawQuery("SELECT id, token FROM login", null);
            if(cursor.getCount() > 0){
                cursor.moveToFirst();
                id = cursor.getString(cursor.getColumnIndex("id"));
                token = cursor.getString(cursor.getColumnIndex("token"));
            }
        }catch (Exception e){

        }finally {
            close();
        }
        return id+"&token="+token+"&agentId="+id;
    }


    public void clearTable(String tableName) {
        _database = this.getWritableDatabase();
        _database.delete(tableName, null, null);
    }
}

