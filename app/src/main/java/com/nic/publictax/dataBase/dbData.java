package com.nic.publictax.dataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.nic.publictax.constant.AppConstant;
import com.nic.publictax.model.PublicTax;

import java.util.ArrayList;


public class dbData {
    private SQLiteDatabase db;
    private SQLiteOpenHelper dbHelper;
    private Context context;

    public dbData(Context context){
        this.dbHelper = new DBHelper(context);
        this.context = context;
    }

    public void open() {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        if(dbHelper != null) {
            dbHelper.close();
        }
    }

    /****** DISTRICT TABLE *****/


    /****** VILLAGE TABLE *****/
    public PublicTax insertVillage(PublicTax pmgsySurvey) {

        ContentValues values = new ContentValues();
        values.put(AppConstant.DISTRICT_CODE, pmgsySurvey.getDistictCode());
        values.put(AppConstant.BLOCK_CODE, pmgsySurvey.getBlockCode());
        values.put(AppConstant.PV_CODE, pmgsySurvey.getPvCode());
        values.put(AppConstant.PV_NAME, pmgsySurvey.getPvName());

        long id = db.insert(DBHelper.VILLAGE_TABLE_NAME,null,values);
        Log.d("Inserted_id_village", String.valueOf(id));

        return pmgsySurvey;
    }
    public ArrayList<PublicTax> getAll_Village(String dcode, String bcode) {

        ArrayList<PublicTax> cards = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("select * from "+DBHelper.VILLAGE_TABLE_NAME+" where dcode = "+dcode+" and bcode = "+bcode+" order by pvname asc",null);
            // cursor = db.query(CardsDBHelper.TABLE_CARDS,
            //       COLUMNS, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    PublicTax card = new PublicTax();
                    card.setDistictCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.DISTRICT_CODE)));
                    card.setBlockCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.BLOCK_CODE)));
                    card.setPvCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PV_CODE)));
                    card.setPvName(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PV_NAME)));

                    cards.add(card);
                }
            }
        } catch (Exception e){
            //   Log.d(DEBUG_TAG, "Exception raised with a value of " + e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return cards;
    }

    public PublicTax insertHabitation(PublicTax pmgsySurvey) {

        ContentValues values = new ContentValues();
        values.put(AppConstant.DISTRICT_CODE, pmgsySurvey.getDistictCode());
        values.put(AppConstant.BLOCK_CODE, pmgsySurvey.getBlockCode());
        values.put(AppConstant.PV_CODE, pmgsySurvey.getPvCode());
        values.put(AppConstant.HABB_CODE, pmgsySurvey.getHabCode());
        values.put(AppConstant.HABITATION_NAME, pmgsySurvey.getHabitationName());

        long id = db.insert(DBHelper.HABITATION_TABLE_NAME,null,values);
        Log.d("Inserted_id_habitation", String.valueOf(id));

        return pmgsySurvey;
    }
    public ArrayList<PublicTax> getAll_Habitation(String dcode, String bcode) {

        ArrayList<PublicTax> cards = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("select * from "+DBHelper.HABITATION_TABLE_NAME+" where dcode = "+dcode+" and bcode = "+bcode+" order by habitation_name asc",null);
            // cursor = db.query(CardsDBHelper.TABLE_CARDS,
            //       COLUMNS, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    PublicTax card = new PublicTax();
                    card.setDistictCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.DISTRICT_CODE)));
                    card.setBlockCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.BLOCK_CODE)));
                    card.setPvCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PV_CODE)));
                    card.setHabCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.HABB_CODE)));
                    card.setHabitationName(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.HABITATION_NAME)));

                    cards.add(card);
                }
            }
        } catch (Exception e){
            //   Log.d(DEBUG_TAG, "Exception raised with a value of " + e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return cards;
    }

    /////New ******************** Task****************
    public ArrayList<PublicTax> getAll_Master_Fin_Year() {
        ArrayList<PublicTax> cards = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("select * from "+DBHelper.MASTER_FIN_YEAR_TABLE,null);
            // cursor = db.query(CardsDBHelper.TABLE_CARDS,
            //       COLUMNS, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    PublicTax card = new PublicTax();
                    card.setFin_year(cursor.getString(cursor
                            .getColumnIndexOrThrow("fin_year")));
                    cards.add(card);
                }
            }
        } catch (Exception e){
            //   Log.d(DEBUG_TAG, "Exception raised with a value of " + e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return cards;
    }

    public Bitmap bytearrtoBitmap(byte[] photo){
        byte[] imgbytes = Base64.decode(photo, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imgbytes, 0,
                imgbytes.length);
        return bitmap;
    }

    //////////////////////*****************/////////////


    public void deleteVillageTable() {
        db.execSQL("delete from " + DBHelper.VILLAGE_TABLE_NAME);
    }

    public void deletefin_year() { db.execSQL("delete from " + DBHelper.MASTER_FIN_YEAR_TABLE);}
    public void deleteHabitation() { db.execSQL("delete from " + DBHelper.HABITATION_TABLE_NAME);}



    public void deleteAll() {

        deleteVillageTable();
        deleteHabitation();

        deletefin_year();

    }



}
