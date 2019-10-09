package com.mikropresente.app.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.mikropresente.app.db.entity.Participant;

import java.util.List;

@Dao
public interface ParticipantDao {
    @Query("SELECT * FROM participant")
    List<Participant> getAll();

    @Query("SELECT * FROM participant WHERE uid IN (:participantsIds)")
    List<Participant> loadAllByIds(int[] participantsIds);

    @Query("SELECT * FROM participant WHERE name LIKE :first LIMIT 1")
    Participant findByName(String first);

    @Query("SELECT * FROM participant WHERE code LIKE :first LIMIT 1")
    Participant findByCode(String first);

    @Insert
    void insertAll(Participant... participants);

    @Delete
    void delete(Participant participant);
}
