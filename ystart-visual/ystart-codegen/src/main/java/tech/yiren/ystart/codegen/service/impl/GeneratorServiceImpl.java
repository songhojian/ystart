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

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import tech.yiren.ystart.codegen.entity.GenConfig;
import tech.yiren.ystart.codegen.entity.GenFormConf;
import tech.yiren.ystart.codegen.entity.Model;
import tech.yiren.ystart.codegen.mapper.GenFormConfMapper;
import tech.yiren.ystart.codegen.mapper.GeneratorMapper;
import tech.yiren.ystart.codegen.service.GeneratorService;
import tech.yiren.ystart.codegen.util.GenUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author ystart
 * @date 2018-07-30
 * <p>
 * ???????????????
 */
@Service
@AllArgsConstructor
public class GeneratorServiceImpl implements GeneratorService {

	private final GenFormConfMapper genFormConfMapper;
	@Resource
	private MongoTemplate mongoTemplate;

	/**
	 * ???????????????
	 * @param tableName ????????????
	 * @param dsName
	 * @return
	 */
	@Override
	public IPage<Map<String, Object>> getPage(Page page, String tableName, String dsName) {
		GeneratorMapper mapper = GenUtils.getMapper(dsName);
		// ?????????????????????
		DynamicDataSourceContextHolder.push(dsName);
		return mapper.queryTable(page, tableName);
	}

	@Override
	public Map<String, String> previewCode(GenConfig genConfig) {
		// ??????tableName ???????????????????????????
		List<GenFormConf> formConfList = genFormConfMapper.selectList(Wrappers.<GenFormConf>lambdaQuery()
				.eq(GenFormConf::getTableName, genConfig.getTableName()).orderByDesc(GenFormConf::getCreateTime));

		String tableNames = genConfig.getTableName();
		String dsName = genConfig.getDsName();
		GeneratorMapper mapper = GenUtils.getMapper(genConfig.getDsName());

		for (String tableName : StrUtil.split(tableNames, StrUtil.DASHED)) {
			// ???????????????
			Map<String, String> table = mapper.queryTable(tableName, dsName);
			// ???????????????
			List<Map<String, String>> columns = mapper.selectMapTableColumn(tableName, dsName);
			// ????????????
			Query queryRelative1 = new Query();
			queryRelative1.addCriteria(Criteria.where("code").is(genConfig.getTableName()));
			Model model = mongoTemplate.findOne(queryRelative1,Model.class,"model");
			if (CollUtil.isNotEmpty(formConfList)) {
				return GenUtils.generatorCode(genConfig, table, columns, null, formConfList.get(0),model);
			}
			else {
				return GenUtils.generatorCode(genConfig, table, columns, null, null,model);
			}
		}
		return MapUtil.empty();
	}

	/**
	 * ????????????
	 * @param genConfig ????????????
	 * @return
	 */
	@Override
	public byte[] generatorCode(GenConfig genConfig) {
		// ??????tableName ???????????????????????????
		List<GenFormConf> formConfList = genFormConfMapper.selectList(Wrappers.<GenFormConf>lambdaQuery()
				.eq(GenFormConf::getTableName, genConfig.getTableName()).orderByDesc(GenFormConf::getCreateTime));

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ZipOutputStream zip = new ZipOutputStream(outputStream);

		String tableNames = genConfig.getTableName();
		String dsName = genConfig.getDsName();
		GeneratorMapper mapper = GenUtils.getMapper(dsName);

		for (String tableName : StrUtil.split(tableNames, StrUtil.DASHED)) {
			// ???????????????
			Map<String, String> table = mapper.queryTable(tableName, dsName);
			// ???????????????
			List<Map<String, String>> columns = mapper.selectMapTableColumn(tableName, dsName);
			// ????????????
			Query queryRelative1 = new Query();
			queryRelative1.addCriteria(Criteria.where("code").is(genConfig.getTableName()));
			Model model = mongoTemplate.findOne(queryRelative1,Model.class,"model");

			if (CollUtil.isNotEmpty(formConfList)) {
				GenUtils.generatorCode(genConfig, table, columns, zip, formConfList.get(0), model);
			} else {
				GenUtils.generatorCode(genConfig, table, columns, zip, null, model);
			}
		}
		IoUtil.close(zip);
		return outputStream.toByteArray();
	}

}
