package com.wb.util;

import org.springframework.web.servlet.view.freemarker.FreeMarkerView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 获取根路径
 * 通过重写exposeHelpers方法，在spring里配置自己的freemarker的视图解析器，在模板中就可以通过${base}获取
 */
public class MyFreeMarkerView extends FreeMarkerView {
    private static final String CONTEXT_PATH = "base";

    @Override
    protected void exposeHelpers(Map<String, Object> model, HttpServletRequest request) throws Exception {
        model.put(CONTEXT_PATH, request.getContextPath());
        super.exposeHelpers(model, request);
    }

}
