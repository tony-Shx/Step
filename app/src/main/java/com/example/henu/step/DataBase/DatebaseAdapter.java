package com.example.henu.step.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.henu.step.Bean.Run;

import java.util.ArrayList;

/**
 * Created by 宋浩祥 on 2017/3/7.
 */

public class DatebaseAdapter {

    private DatabaseHelper databaseHelper;
    public DatebaseAdapter(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }

    public void add(Run run){
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        //将数据按照键值对存入ContentValues
        ContentValues values = new ContentValues();
        values.put(RunMetaData.RunTable.TELEPHONE,run.getTelephone());
        values.put(RunMetaData.RunTable.START_TIME,run.getStart_time());
        values.put(RunMetaData.RunTable.END_TIME,run.getEnd_time());
        values.put(RunMetaData.RunTable.TOTAL_LENGTH,run.getLength());
        values.put(RunMetaData.RunTable.TOTAL_TIME,run.getDuration());
        values.put(RunMetaData.RunTable.CONSUME,run.getConsume());
        values.put(RunMetaData.RunTable.POINTS,run.getPoints());
        values.put(RunMetaData.RunTable.ISUPDATE,run.isUpdate());
        //执行插入操作
        db.insert(RunMetaData.RunTable.TABLE_NAME,RunMetaData.RunTable.TELEPHONE,values);
        db.close();
        System.out.println("插入数据成功！");
    }
    public void delete(int id){
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        String[] args = {String.valueOf(id)};
        db.delete(RunMetaData.RunTable.TABLE_NAME,"_id=?",args);
        db.close();
    }

    public void update(Run run){
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(RunMetaData.RunTable.ISUPDATE,run.isUpdate());
        String agre[] = {String.valueOf(run.getId())};
        db.update(RunMetaData.RunTable.TABLE_NAME,values,"_id=?",agre);

    }

    public Run findById(int id){
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String[] selectionArgs = {String.valueOf(id)};
        Cursor cursor = db.query(RunMetaData.RunTable.TABLE_NAME,null,"_id=?",selectionArgs,null,null,null);
        Run run = null;
        while (cursor.moveToNext()){
            run =  new Run();
            run.setId(cursor.getInt(cursor.getColumnIndex("_id")));
            run.setTelephone(cursor.getString(cursor.getColumnIndex("telephone")));
            run.setStart_time(cursor.getInt(cursor.getColumnIndex("start_time")));
            run.setEnd_time(cursor.getInt(cursor.getColumnIndex("end_time")));
            run.setLength(cursor.getFloat(cursor.getColumnIndex("length")));
            run.setDuration(cursor.getInt(cursor.getColumnIndex("duration")));
            run.setConsume(cursor.getFloat(cursor.getColumnIndex("consume")));
            run.setPoints(cursor.getString(cursor.getColumnIndex("points")));
            run.setUpdate(cursor.getInt(cursor.getColumnIndex("isUpdate")));
        }
        cursor.close();
        return run;
    }

    public ArrayList<Run> findAll(){
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query(RunMetaData.RunTable.TABLE_NAME,null,null,null,null,null,null);
        ArrayList<Run> runArrayList = new ArrayList<>();
        while (cursor.moveToNext()){
            Run run =  new Run();
            run.setId(cursor.getInt(cursor.getColumnIndex("_id")));
            run.setTelephone(cursor.getString(cursor.getColumnIndex("telephone")));
            run.setStart_time(cursor.getInt(cursor.getColumnIndex("start_time")));
            run.setEnd_time(cursor.getInt(cursor.getColumnIndex("end_time")));
            run.setLength(cursor.getFloat(cursor.getColumnIndex("length")));
            run.setDuration(cursor.getInt(cursor.getColumnIndex("duration")));
            run.setConsume(cursor.getFloat(cursor.getColumnIndex("consume")));
            run.setPoints(cursor.getString(cursor.getColumnIndex("points")));
            run.setUpdate(cursor.getInt(cursor.getColumnIndex("is_update")));
            runArrayList.add(run);
        }
        cursor.close();
        return runArrayList;
    }



}
