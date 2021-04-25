package com.hx.crm.workbench.dao;

import com.hx.crm.workbench.domain.Clue;

public interface ClueDao {


    int save(Clue clue);

    Clue detail(String id);

    Clue getByid(String clueId);

    int delete(String clueId);
}
