package com.mikropresente.app.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.mikropresente.app.db.dao.ParticipantDao;
import com.mikropresente.app.db.entity.Participant;

@Database(entities = {Participant.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ParticipantDao participanDao();
}
