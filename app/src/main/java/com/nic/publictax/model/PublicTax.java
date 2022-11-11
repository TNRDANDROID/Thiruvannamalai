package com.nic.publictax.model;

import android.graphics.Bitmap;

/**
 * Created by Kavitha on 10-11-2022.
 */

public class PublicTax {

    private String distictCode;
    private String districtName;

    private String blockCode;

    public String getHabCode() {
        return HabCode;
    }

    public void setHabCode(String habCode) {
        HabCode = habCode;
    }

    private String HabCode;

    private String Description;
    private String Latitude;
    private String HabitationName;
    private String pvCode;
    private String pvName;

    /////New Variable
    private String fin_year;
    private String taxtypeid;
    private String taxtypedesc_en;
    private String blockName;
    private String transactionName;
    private String transactionDate;
    private String transactionStatus;


    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public String getTransactionName() {
        return transactionName;
    }

    public void setTransactionName(String transactionName) {
        this.transactionName = transactionName;
    }

    public String getTaxtypeid() {
        return taxtypeid;
    }

    public void setTaxtypeid(String taxtypeid) {
        this.taxtypeid = taxtypeid;
    }

    public String getTaxtypedesc_en() {
        return taxtypedesc_en;
    }

    public void setTaxtypedesc_en(String taxtypedesc_en) {
        this.taxtypedesc_en = taxtypedesc_en;
    }

    public String getBlockName() {
        return blockName;
    }

    public void setBlockName(String blockName) {
        this.blockName = blockName;
    }

    public String getPvCode() {
        return pvCode;
    }

    public void setPvCode(String pvCode) {
        this.pvCode = pvCode;
    }

    public String getPvName() {
        return pvName;
    }

    public void setPvName(String pvName) {
        this.pvName = pvName;
    }

    public String getDistictCode() {
        return distictCode;
    }

    public void setDistictCode(String distictCode) {
        this.distictCode = distictCode;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public String getBlockCode() {
        return blockCode;
    }

    public void setBlockCode(String blockCode) {
        this.blockCode = blockCode;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getHabitationName() {
        return HabitationName;
    }

    public void setHabitationName(String habitationName) {
        HabitationName = habitationName;
    }

    public String getFin_year() {
        return fin_year;
    }

    public void setFin_year(String fin_year) {
        this.fin_year = fin_year;
    }
}