package tech.yiren.ystart.lowcode.entity;

import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;

@Data
@ToString
public class Model {
    public Long createUserId;
    public String createUserName;
    public LocalDate createtime;
    public LocalDate updatetime;
    public String code;
    public String desc;
    public String menuName;
    public String name;
    public Long parentMenu;
    public String type;
    public String source;
    public String _id;
    public Object listOption;
    public Object addOption;
    public Object editOption;
    public Object viewOption;
    public Object cardOption;
    public Object mobileOption;
    public Object showColumn;
    public Object sortColumn;
    public Object searchColumn;
    public Object searchSuperColumn;
    public String rel1;
    public String rel2;
}
