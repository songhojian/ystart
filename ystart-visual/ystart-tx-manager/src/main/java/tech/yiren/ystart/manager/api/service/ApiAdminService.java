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

package tech.yiren.ystart.manager.api.service;

import com.lorne.core.framework.exception.ServiceException;
import tech.yiren.ystart.manager.compensate.model.TxModel;
import tech.yiren.ystart.manager.model.ModelName;
import tech.yiren.ystart.manager.model.TxState;

import java.util.List;
import java.util.Map;

/**
 * @author LCN on 2017/11/12
 * @author ystart
 */
public interface ApiAdminService {

	TxState getState();

	/**
	 * k/v 获取 值封装成map
	 * @return
	 */
	List<Map<String, Object>> getMapState();

	String loadNotifyJson();

	List<ModelName> modelList();

	List<String> modelTimes(String model);

	List<TxModel> modelInfos(String path);

	boolean compensate(String path) throws ServiceException;

	boolean hasCompensate();

	boolean delCompensate(String path);

}
