package tech.yiren.ystart.codegen.entity;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@ToString
public class Relative {
    public Long createUserId;
    public String createUserName;
    public LocalDate createtime;
    public LocalDate updatetime;
    public String dateFxied;
    public String dateTimeFxied;
    public String dataSource;
    public String helpTip;
    public String fieldCode;
    public String fieldName;
    public String fieldType;
    public Integer isNull;
    public Integer maxLength;
    public Integer minLength;
    public Integer precision;
    public BigDecimal maxValue;
    public BigDecimal minValue;
    public String model_id;
    public String modelId;
    public String modelCode;
    public Integer defalutValBoolean;
    public String defalutValDate;
    public String dateOffset;
    public String relObjFieldName;
    public String relObjDeleteRule;
    public String selectStyle;
    public String selectMultipleStyle;
    public String booleanYes;
    public String booleanNo;
    public String fieldDesc;
    public String relMainData;
    public String defalutVal;
    public String relObj;
    public String relObjField;
    public String _id;
    public Boolean showColumn;
}
