package com.hx.crm.settings.service.impl;

import com.hx.crm.settings.dao.DicTypeDao;
import com.hx.crm.settings.dao.DicValueDao;
import com.hx.crm.settings.domain.DicType;
import com.hx.crm.settings.domain.DicValue;
import com.hx.crm.settings.service.DicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DicServiceImpl implements DicService {
    @Autowired
    private DicTypeDao dicTypeDao;
    @Autowired
    private DicValueDao dicValueDao;

    @Override
    public Map<String, List<DicValue>> getAll() {
        Map<String,List<DicValue>> map=new HashMap<>();
//        将字典类型取出
        List<DicType> dtList=dicTypeDao.getTypeList();
//        将字典类型列表遍历
        for(DicType dt:dtList){
//            取得每一种类型的字典编码
            String code=dt.getCode();
//            根据每一个字典类型 来取得字典值的列表
            List<DicValue> dvList=dicValueDao.getListByCode(code);
            map.put(code+"List",dvList);
        }
        return map;
    }
}
