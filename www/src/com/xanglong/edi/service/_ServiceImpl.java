package com.xanglong.edi.service;

import com.xanglong.edi.api._Service;
import com.xanglong.frame.mvc.MyService;

/**框架内部实现类*/
@MyService
public class _ServiceImpl implements _Service {

	@Override
	public String test() {
		return "666";
	}

}