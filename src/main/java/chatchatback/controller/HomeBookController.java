package chatchatback.controller;

import chatchatback.pojo.ClassicPoemInfo;
import chatchatback.pojo.Result;
import chatchatback.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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
    public Result getBooks() {
        log.info("获取首页书籍信息");
        try {
            List<ClassicPoemInfo> books = bookService.getAllBooks();
            return Result.success(books);
        } catch (Exception e) {
            return Result.error("数据获取失败: " + e.getMessage());
        }
    }
}
