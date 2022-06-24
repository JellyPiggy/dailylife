package com.itheima.web;

import com.alibaba.fastjson.JSON;
import com.itheima.pojo.Brand;
import com.itheima.service.BrandService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * @author 晓蝈
 * @version 1.0
 */
@WebServlet("/addServlet")
public class AddServlet extends HttpServlet {
    private BrandService brandService = new BrandService();
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //1.获取请求体数据
        BufferedReader br = request.getReader();
        String params = br.readLine();
        //2.将JSON字符串转化为Java对象
        Brand brand = JSON.parseObject(params, Brand.class);
        System.out.println(brand);
        //3.添加
        brandService.add(brand);
        //4.响应
        response.getWriter().write("success");
    }
}
