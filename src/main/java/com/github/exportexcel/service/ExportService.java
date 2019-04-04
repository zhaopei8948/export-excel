package com.github.exportexcel.service;

import com.alibaba.fastjson.JSONObject;
import com.github.exportexcel.dao.ExportDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
public class ExportService {

    @Autowired
    private ExportDao exportDao;

    public List<JSONObject> getListBySql(String sql) {
        return this.exportDao.getListBySql(sql);
    }
}
