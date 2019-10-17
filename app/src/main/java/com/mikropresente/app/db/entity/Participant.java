package com.mikropresente.app.db.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Participant {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    public int uid;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "position")
    public String position;

    @ColumnInfo(name = "email")
    public String email;

    @ColumnInfo(name = "code")
    public String code;

}
