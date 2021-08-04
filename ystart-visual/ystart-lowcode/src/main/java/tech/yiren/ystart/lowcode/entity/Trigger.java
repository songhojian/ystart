package tech.yiren.ystart.lowcode.entity;

import lombok.Data;
import lombok.ToString;

import java.sql.Date;
import java.util.ArrayList;

@Data
@ToString
public class Trigger {
	public String _id;
	public String modelId;
	public String triggerName;
	public String triggerWay;//0 自动触发 1 手动提交
	public String triggerAction;//0 新建 1 编辑
	public String triggerCondition;//0 总是触发 1满足条件触发
	public String triggerStatus;//0 启动 1禁用
	public String triggerDescription;
	public String pushButtonName;
	public String pushButtonPosition;
	public ArrayList<Integer> associate;
	public String funString;
/*
	public String creatName;
	public Date creatTime;
	public String reviseName;
	public Date reviseTime;*/


}
