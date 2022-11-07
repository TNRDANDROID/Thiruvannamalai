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

    public PublicTax insertPMAY(PublicTax pmgsySurvey) {

        ContentValues values = new ContentValues();
        values.put(AppConstant.PV_CODE, pmgsySurvey.getPvCode());
        values.put(AppConstant.HAB_CODE, pmgsySurvey.getHabCode());
        values.put(AppConstant.BENEFICIARY_NAME, pmgsySurvey.getBeneficiaryName());
        values.put(AppConstant.SECC_ID, pmgsySurvey.getSeccId());
        values.put(AppConstant.HABITATION_NAME, pmgsySurvey.getHabitationName());
        values.put(AppConstant.PV_NAME, pmgsySurvey.getPvName());
        values.put(AppConstant.PERSON_ALIVE, pmgsySurvey.getPersonAlive());
        values.put(AppConstant.LEGAL_HEIR_AVAILABLE, pmgsySurvey.getIsLegel());
        values.put(AppConstant.PERSON_MIGRATED, pmgsySurvey.getIsMigrated());

        long id = db.insert(DBHelper.PMAY_LIST_TABLE_NAME,null,values);
        Log.d("Inserted_id_PMAY_LIST", String.valueOf(id));

        return pmgsySurvey;
    }

    public ArrayList<PublicTax> getAll_PMAYList(String pvcode, String habcode) {

        ArrayList<PublicTax> cards = new ArrayList<>();
        Cursor cursor = null;

        String condition = "";

        if (habcode != "") {
            condition = " where pvcode = '" + pvcode+"' and habcode = '" + habcode+"'" ;
        }else {
            condition = "";
        }

        try {
            cursor = db.rawQuery("select * from "+DBHelper.PMAY_LIST_TABLE_NAME + condition,null);
            // cursor = db.query(CardsDBHelper.TABLE_CARDS,
            //       COLUMNS, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    PublicTax card = new PublicTax();
                    card.setPvCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PV_CODE)));
                    card.setHabCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.HAB_CODE)));
                    card.setBeneficiaryName(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.BENEFICIARY_NAME)));
                    card.setSeccId(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.SECC_ID)));
                    card.setHabitationName(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.HABITATION_NAME)));
                    card.setPvName(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PV_NAME)));
                    card.setPersonAlive(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PERSON_ALIVE)));
                    card.setIsLegel(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.LEGAL_HEIR_AVAILABLE)));
                    card.setIsMigrated(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PERSON_MIGRATED)));

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

    public ArrayList<PublicTax> getSavedPMAYDetails() {

        ArrayList<PublicTax> cards = new ArrayList<>();
        Cursor cursor = null;
        String selection = null;
        String[] selectionArgs = null;


        try {
//            cursor = db.query(DBHelper.SAVE_PMAY_DETAILS,
//                    new String[]{"*"}, selection, selectionArgs, null, null, null);
            cursor = db.rawQuery("select * from "+DBHelper.SAVE_PMAY_DETAILS+" where id in (select pmay_id from "+DBHelper.SAVE_PMAY_IMAGES+")",null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {

                    PublicTax card = new PublicTax();


                    card.setPmayId(cursor.getString(cursor
                            .getColumnIndexOrThrow("id")));
                    card.setDistictCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.DISTRICT_CODE)));
                    card.setBlockCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.BLOCK_CODE)));
                    card.setPvCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PV_CODE)));
                    card.setHabCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.HAB_CODE)));
                    card.setPvName(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PV_NAME)));
                    card.setHabitationName(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.HABITATION_NAME)));
                    card.setSeccId(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.SECC_ID)));
                    card.setBeneficiaryName(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.BENEFICIARY_NAME)));
                    card.setFatherName(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.BENEFICIARY_FATHER_NAME)));
                    card.setPersonAlive(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PERSON_ALIVE)));
                    card.setIsLegel(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.LEGAL_HEIR_AVAILABLE)));
                    card.setIsMigrated(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PERSON_MIGRATED)));
                    card.setButtonText(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.BUTTON_TEXT)));


                    cards.add(card);
                }
            }
        } catch (Exception e) {
            //   Log.d(DEBUG_TAG, "Exception raised with a value of " + e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        try {
//            cursor = db.query(DBHelper.SAVE_PMAY_DETAILS,
//                    new String[]{"*"}, selection, selectionArgs, null, null, null);
            cursor = db.rawQuery("select * from "+DBHelper.SAVE_PMAY_DETAILS+" where button_text = 'Save details'",null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {

                    PublicTax card = new PublicTax();

                    card.setPmayId(cursor.getString(cursor
                            .getColumnIndexOrThrow("id")));
                    card.setDistictCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.DISTRICT_CODE)));
                    card.setBlockCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.BLOCK_CODE)));
                    card.setPvCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PV_CODE)));
                    card.setHabCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.HAB_CODE)));
                    card.setPvName(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PV_NAME)));
                    card.setHabitationName(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.HABITATION_NAME)));
                    card.setSeccId(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.SECC_ID)));
                    card.setBeneficiaryName(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.BENEFICIARY_NAME)));
                    card.setFatherName(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.BENEFICIARY_FATHER_NAME)));
                    card.setPersonAlive(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PERSON_ALIVE)));
                    card.setIsLegel(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.LEGAL_HEIR_AVAILABLE)));
                    card.setIsMigrated(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PERSON_MIGRATED)));
                    card.setButtonText(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.BUTTON_TEXT)));


                    cards.add(card);
                }
            }
        } catch (Exception e) {
            //   Log.d(DEBUG_TAG, "Exception raised with a value of " + e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return cards;
    }


    public ArrayList<PublicTax> getSavedPMAYImages(String pmay_id, String type_of_photo) {

        ArrayList<PublicTax> cards = new ArrayList<>();
        Cursor cursor = null;
        String selection = null;
        String[] selectionArgs = null;

        if(!type_of_photo.isEmpty()){
            selection = "pmay_id = ? and type_of_photo = ? ";
            selectionArgs = new String[]{pmay_id,type_of_photo};
        }
        else if(type_of_photo.isEmpty()) {
            selection = "pmay_id = ? ";
            selectionArgs = new String[]{pmay_id};
        }


        try {
            cursor = db.query(DBHelper.SAVE_PMAY_IMAGES,
                    new String[]{"*"}, selection, selectionArgs, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {

                    byte[] photo = cursor.getBlob(cursor.getColumnIndexOrThrow(AppConstant.KEY_IMAGE));
                    byte[] decodedString = Base64.decode(photo, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                    PublicTax card = new PublicTax();


                    card.setPmayId(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PMAY_ID)));
                    card.setTypeOfPhoto(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.TYPE_OF_PHOTO)));
                    card.setLatitude(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.KEY_LATITUDE)));
                    card.setLongitude(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.KEY_LONGITUDE)));

                    card.setImage(decodedByte);

                    cards.add(card);
                }
            }
        } catch (Exception e) {
            //   Log.d(DEBUG_TAG, "Exception raised with a value of " + e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return cards;
    }


    /////New ******************** Task****************
    public void insert_Master_Fin_Year(PublicTax pmgsySurvey) {
        ContentValues values = new ContentValues();
        values.put("fin_year", pmgsySurvey.getFin_year());
        long id = db.insert(DBHelper.MASTER_FIN_YEAR_TABLE,null,values);
        Log.d("Inserted_id_fin_year", String.valueOf(id));
    }
    public void insert_Master_Work_Type(PublicTax pmgsySurvey) {
        ContentValues values = new ContentValues();
        values.put("work_code", pmgsySurvey.getWork_code());
        values.put("work_name", pmgsySurvey.getWork_name());
        long id = db.insert(DBHelper.MASTER_WORK_TYPE_TABLE,null,values);
        Log.d("Inserted_id_work_type", String.valueOf(id));
    }
    public void insert_Master_Self_Help_Group(PublicTax pmgsySurvey) {
        ContentValues values = new ContentValues();
        values.put("shg_code", pmgsySurvey.getShg_code());
        values.put("shg_name", pmgsySurvey.getShg_name());
        long id = db.insert(DBHelper.MASTER_SELF_HELP_GROUP_TABLE,null,values);
        Log.d("Inserted_id_s_g_t", String.valueOf(id));
    }
    public void insert_Master_Self_Help_Group_Member(PublicTax pmgsySurvey) {
        ContentValues values = new ContentValues();
        values.put("shg_code", pmgsySurvey.getShg_code());
        values.put("shg_member_code", pmgsySurvey.getShg_member_code());
        values.put("member_name", pmgsySurvey.getMember_name());
        long id = db.insert(DBHelper.MASTER_SELF_HELP_GROUP_MEMBER_TABLE,null,values);
        Log.d("Inserted_id_s_g_m_t", String.valueOf(id));
    }

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
    public ArrayList<PublicTax> getAll_Master_Work_Type() {
        ArrayList<PublicTax> cards = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("select * from "+DBHelper.MASTER_WORK_TYPE_TABLE,null);
            // cursor = db.query(CardsDBHelper.TABLE_CARDS,
            //       COLUMNS, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    PublicTax card = new PublicTax();
                    card.setWork_code(cursor.getInt(cursor
                            .getColumnIndexOrThrow("work_code")));
                    card.setWork_name(cursor.getString(cursor
                            .getColumnIndexOrThrow("work_name")));
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
    public ArrayList<PublicTax> getAll_Master_Self_Help_Group() {
        ArrayList<PublicTax> cards = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("select * from "+DBHelper.MASTER_SELF_HELP_GROUP_TABLE,null);
            // cursor = db.query(CardsDBHelper.TABLE_CARDS,
            //       COLUMNS, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    PublicTax card = new PublicTax();
                    card.setShg_code(cursor.getInt(cursor
                            .getColumnIndexOrThrow("shg_code")));
                    card.setShg_name(cursor.getString(cursor
                            .getColumnIndexOrThrow("shg_name")));
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
    public ArrayList<PublicTax> getAll_Master_Self_Help_Group_Member(int shg_code) {
        ArrayList<PublicTax> cards = new ArrayList<>();
        Cursor cursor = null;
        String selection;
        String[] selectionArgs;
        try {
            //cursor = db.rawQuery("select * from "+DBHelper.MASTER_SELF_HELP_GROUP_MEMBER_TABLE,null);
            selection = "shg_code = ? ";
            selectionArgs = new String[]{String.valueOf(shg_code)};
            cursor = db.query(DBHelper.MASTER_SELF_HELP_GROUP_MEMBER_TABLE,
                    new String[]{"*"}, selection, selectionArgs, null, null, null);
            // cursor = db.query(CardsDBHelper.TABLE_CARDS,
            //       COLUMNS, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    PublicTax card = new PublicTax();
                    card.setShg_code(cursor.getInt(cursor
                            .getColumnIndexOrThrow("shg_code")));
                    card.setShg_member_code(cursor.getInt(cursor
                            .getColumnIndexOrThrow("shg_member_code")));
                    card.setMember_name(cursor.getString(cursor
                            .getColumnIndexOrThrow("member_name")));
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
    public ArrayList<PublicTax> getParticular_Before_Save_Tree_Image_Table(int shg_code, int shg_member_code) {
        ArrayList<PublicTax> cards = new ArrayList<>();
        Cursor cursor = null;
        String selection;
        String[] selectionArgs;
        try {
            //cursor = db.rawQuery("select * from "+DBHelper.MASTER_SELF_HELP_GROUP_MEMBER_TABLE,null);
            selection = "shg_code = ? and shg_member_code = ? ";
            selectionArgs = new String[]{String.valueOf(shg_code),String.valueOf(shg_member_code)};
            cursor = db.query(DBHelper.SAVE_BEFORE_TREE_IMAGE_TABLE,
                    new String[]{"*"}, selection, selectionArgs, null, null, null);

            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    PublicTax card = new PublicTax();

                    card.setShg_code(cursor.getInt(cursor
                            .getColumnIndexOrThrow("shg_code")));
                    card.setShg_member_code(cursor.getInt(cursor
                            .getColumnIndexOrThrow("shg_member_code")));
                    card.setMember_name(cursor.getString(cursor
                            .getColumnIndexOrThrow("member_name")));
                    card.setBefore_photo_lat(cursor.getString(cursor
                            .getColumnIndexOrThrow("before_photo_lat")));
                    card.setBefore_photo_long(cursor.getString(cursor
                            .getColumnIndexOrThrow("before_photo_long")));
                    byte[] before_photo = cursor.getBlob(cursor.getColumnIndexOrThrow("before_photo"));
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    Bitmap bitmap = BitmapFactory.decodeByteArray(before_photo, 0, before_photo.length, options);
                    card.setBefore_photo((bitmap));

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
    public ArrayList<PublicTax> getParticular_After_Save_Tree_Image_Table(int shg_code, int shg_member_code) {
        ArrayList<PublicTax> cards = new ArrayList<>();
        Cursor cursor = null;
        String selection;
        String[] selectionArgs;
        try {
            //cursor = db.rawQuery("select * from "+DBHelper.MASTER_SELF_HELP_GROUP_MEMBER_TABLE,null);
            selection = "shg_code = ? and shg_member_code = ? ";
            selectionArgs = new String[]{String.valueOf(shg_code),String.valueOf(shg_member_code)};
            cursor = db.query(DBHelper.SAVE_AFTER_TREE_IMAGE_TABLE,
                    new String[]{"*"}, selection, selectionArgs, null, null, null);
            // cursor = db.query(CardsDBHelper.TABLE_CARDS,
            //       COLUMNS, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    PublicTax card = new PublicTax();

                    card.setShg_code(cursor.getInt(cursor
                            .getColumnIndexOrThrow("shg_code")));
                    card.setShg_member_code(cursor.getInt(cursor
                            .getColumnIndexOrThrow("shg_member_code")));
                    card.setMember_name(cursor.getString(cursor
                            .getColumnIndexOrThrow("member_name")));
                    card.setAfter_photo_lat(cursor.getString(cursor
                            .getColumnIndexOrThrow("after_photo_lat")));
                    card.setAfter_photo_long(cursor.getString(cursor
                            .getColumnIndexOrThrow("after_photo_long")));
                    byte[] after_photo = cursor.getBlob(cursor.getColumnIndexOrThrow("after_photo"));
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    Bitmap bitmap = BitmapFactory.decodeByteArray(after_photo, 0, after_photo.length, options);
                    card.setAfter_photo((bitmap));
                    cards.add(card);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
            //   Log.d(DEBUG_TAG, "Exception raised with a value of " + e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return cards;
    }
    public ArrayList<PublicTax> getAllTreeImages(){
        ArrayList<PublicTax> cards = new ArrayList<>();
        Cursor cursor = null;
        String selection;
        String[] selectionArgs;
        try {

            cursor = db.rawQuery(AppConstant.SQL_QUERY,null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    PublicTax card = new PublicTax();

                    card.setShg_code(cursor.getInt(cursor
                            .getColumnIndexOrThrow("shg_code")));
                    card.setShg_member_code(cursor.getInt(cursor
                            .getColumnIndexOrThrow("shg_member_code")));
                    card.setWork_code(cursor.getInt(cursor
                            .getColumnIndexOrThrow("work_code")));
                    card.setShg_name(cursor.getString(cursor
                            .getColumnIndexOrThrow("shg_name")));
                    card.setMember_name(cursor.getString(cursor
                            .getColumnIndexOrThrow("member_name")));
                    card.setWork_name(cursor.getString(cursor
                            .getColumnIndexOrThrow("work_name")));
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

    public void deletePMAYTable() {
        db.execSQL("delete from " + DBHelper.PMAY_LIST_TABLE_NAME);
    }

    public void deletePMAYDetails() { db.execSQL("delete from " + DBHelper.SAVE_PMAY_DETAILS); }

    public void deletePMAYImages() { db.execSQL("delete from " + DBHelper.SAVE_PMAY_IMAGES);}

    public void deletefin_year() { db.execSQL("delete from " + DBHelper.MASTER_FIN_YEAR_TABLE);}
    public void deletework_type() { db.execSQL("delete from " + DBHelper.MASTER_WORK_TYPE_TABLE);}
    public void deleteself_help_group() { db.execSQL("delete from " + DBHelper.MASTER_SELF_HELP_GROUP_TABLE);}
    public void deleteself_help_group_member() { db.execSQL("delete from " + DBHelper.MASTER_SELF_HELP_GROUP_MEMBER_TABLE);}
    public void deleteSAVE_BEFORE_TREE_IMAGE_TABLE() { db.execSQL("delete from " + DBHelper.SAVE_BEFORE_TREE_IMAGE_TABLE);}
    public void deleteSAVE_AFTER_TREE_IMAGE_TABLE() { db.execSQL("delete from " + DBHelper.SAVE_AFTER_TREE_IMAGE_TABLE);}
    public void deleteSAVE_TREE_IMAGE_TABLE() { db.execSQL("delete from " + DBHelper.SAVE_TREE_IMAGE_TABLE);}




    public void deleteAll() {

        deleteVillageTable();
        deletePMAYTable();
        deletePMAYDetails();
        deletePMAYImages();

        deletefin_year();
        deletework_type();
        deleteself_help_group();
        deleteself_help_group_member();

        deleteSAVE_BEFORE_TREE_IMAGE_TABLE();
        deleteSAVE_AFTER_TREE_IMAGE_TABLE();
        deleteSAVE_TREE_IMAGE_TABLE();
    }



}
