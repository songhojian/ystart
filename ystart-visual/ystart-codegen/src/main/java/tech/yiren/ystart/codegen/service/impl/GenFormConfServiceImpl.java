/*
 *    Copyright (c) 2018-2025, ystart All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * Neither the name of the pig4cloud.com developer nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * Author: ystart
 */
package tech.yiren.ystart.codegen.service.impl;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import tech.yiren.ystart.codegen.entity.ColumnEntity;
import tech.yiren.ystart.codegen.entity.GenFormConf;
import tech.yiren.ystart.codegen.mapper.GenFormConfMapper;
import tech.yiren.ystart.codegen.mapper.GeneratorMapper;
import tech.yiren.ystart.codegen.service.GenFormConfService;
import tech.yiren.ystart.codegen.util.GenUtils;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 表单管理
 *
 * @author ystart
 * @date 2019-08-12 15:55:35
 */
@Service
@AllArgsConstructor
public class GenFormConfServiceImpl extends ServiceImpl<GenFormConfMapper, GenFormConf> implements GenFormConfService {

	/**
	 * 1. 根据数据源、表名称，查询已配置表单信息 2. 不存在调用模板生成
	 * @param dsName 数据源ID
	 * @param tableName 表名称
	 * @return
	 */
	@Override
	@SneakyThrows
	public String getForm(String dsName, String tableName) {
		GenFormConf form = getOne(Wrappers.<GenFormConf>lambdaQuery().eq(GenFormConf::getTableName, tableName)
				.orderByDesc(GenFormConf::getCreateTime), false);

		if (form != null) {
			return form.getFormInfo();
		}

		GeneratorMapper mapper = GenUtils.getMapper(dsName);
		List<ColumnEntity> columns = mapper.selectTableColumn(tableName, dsName);
		// 设置velocity资源加载器
		Properties prop = new Properties();
		prop.put("file.resource.loader.class", ClasspathResourceLoader.class.getName());
		Velocity.init(prop);
		Template template = Velocity.getTemplate("template/avue/crud.js.vm", CharsetUtil.UTF_8);
		VelocityContext context = new VelocityContext();

		List<ColumnEntity> columnList = new ArrayList<>();
		for (ColumnEntity column : columns) {
			ColumnEntity columnEntity = new ColumnEntity();
			columnEntity.setLowerAttrName(StringUtils.uncapitalize(GenUtils.columnToJava(column.getColumnName())));

			// 判断注释是否为空
			if (StrUtil.isNotBlank(column.getComments())) {
				columnEntity.setComments(column.getComments());
			}
			else {
				columnEntity.setComments(columnEntity.getLowerAttrName());
			}
			columnList.add(columnEntity);
		}
		context.put("columns", columnList);
		StringWriter writer = new StringWriter();
		template.merge(context, writer);
		return StrUtil.trim(StrUtil.removePrefix(writer.toString(), GenUtils.CRUD_PREFIX));
	}

}
