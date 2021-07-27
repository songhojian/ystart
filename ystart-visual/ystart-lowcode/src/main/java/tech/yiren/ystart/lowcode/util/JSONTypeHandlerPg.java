package tech.yiren.ystart.lowcode.util;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.postgresql.util.PGobject;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes(Object.class)
public class JSONTypeHandlerPg extends BaseTypeHandler<Object> {

    private static final PGobject jsonObject = new PGobject();

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
        jsonObject.setType("json");
//        jsonObject.setValue(parameter.toString());
        jsonObject.setValue(JsonUtil.stringify(parameter));
        ps.setObject(i, jsonObject);
    }

    @Override
    public Object getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return JsonUtil.parse(rs.getString(columnIndex), Object.class);
//        return rs.getString(columnIndex);
    }

    @Override
    public Object getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return JsonUtil.parse(cs.getString(columnIndex), Object.class);
    }

    @Override
    public Object getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return JsonUtil.parse(rs.getString(columnName), Object.class);
    }

}