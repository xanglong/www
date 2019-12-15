package com.xanglong.edi.controller;

import com.xanglong.edi.api._Service;
import com.xanglong.frame.mvc.MyAutowired;
import com.xanglong.frame.mvc.MyController;
import com.xanglong.frame.mvc.MyRequestMapping;

/**框架内部控制类*/
@MyController
@MyRequestMapping("/edi/_")
public class _Controller {
	
	@MyAutowired
    private _Service _service;
	
	@MyRequestMapping("/test.do")
	public String test() {
		return _service.test();
	}

}