package com.github.exportexcel.dao;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Repository
public class ExportDao {

    private static final Log logger = LogFactory.getLog(ExportDao.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<JSONObject> getListBySql(String sql) {
        return this.jdbcTemplate.query(sql, new RowMapper<JSONObject>() {
            @Override
            public JSONObject mapRow(ResultSet rs, int rowNum) throws SQLException {
                JSONObject jsonObject = new JSONObject();
                ResultSetMetaData resultSetMetaData = rs.getMetaData();
                jsonObject.put("count", resultSetMetaData.getColumnCount());
                int columnType = 0;
                String columnName = "";
                for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
                    columnName = resultSetMetaData.getColumnName(i);
                    jsonObject.put(i + "", columnName);
                    jsonObject.put(columnName, rs.getObject(i));
                }
                return jsonObject;
            }
        });
    }
}
