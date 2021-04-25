package com.hx.crm.workbench.service;

import com.hx.crm.workbench.domain.Tran;
import com.hx.crm.workbench.domain.TranHistory;

import java.util.List;

public interface TranService {
    boolean save(Tran t, String customerName);

    Tran detail(String id);

    List<TranHistory> getHistoryListByTranId(String TranId);

    boolean changeStage(Tran t);
}
