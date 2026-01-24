package chatchatback.controller;

import chatchatback.pojo.entity.ClassicPoemInfo;
import chatchatback.pojo.dto.Result;
import chatchatback.pojo.entity.PhoneticInitials;
import chatchatback.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *  首页基本信息管理
 * */

@Slf4j
@RestController
public class HomeBookController {

    @Autowired
    private BookService bookService;

    @GetMapping("/getbooks")
    public Result getBooks(@RequestParam String initial) {
        log.info("获取首页书籍信息");
        try {
            List<ClassicPoemInfo> books = bookService.getAllBooks(initial);
            return Result.success(books);
        } catch (Exception e) {
            return Result.error("数据获取失败: " + e.getMessage());
        }
    }

    // 获取首字母列表
    @GetMapping("/getInitial")
    public Result getInitial() {
        log.info("获取首字母列表..");
        try {
            //添加默认选项
            List<PhoneticInitials> phoneticInitials = bookService.getInitial();
            return Result.success(phoneticInitials);
        } catch (Exception e) {
            return Result.error("数据获取失败: " + e.getMessage());
        }
    }
}
