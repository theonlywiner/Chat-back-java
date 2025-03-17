package chatchatback.service.impl;

import chatchatback.mapper.BookMapper;
import chatchatback.pojo.*;
import chatchatback.service.BookService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookMapper bookMapper;

    /**
     *  1.首页书籍信息获取
     * */
    @Override
    public List<ClassicPoemInfo> getAllBooks() {
        try {
            return bookMapper.selectAllBooksOptimized();
        } catch (Exception e) {
            throw new RuntimeException("数据获取失败: " + e.getMessage());
        }
    }

    /**
     *  2.书籍详情基本信息获取
     * */
    @Transactional(readOnly = true)
    public ClassicPoemInfo getClassicPoemInfo(Long  chapterId) throws NotFoundException {
        ClassicPoemInfo poemInfo = bookMapper.selectPoemDetailById(chapterId);
        if (poemInfo == null) {
            throw new NotFoundException("章节不存在");
        }

        // 获取段落内容（单独查询）
        List<ContentPair> contentList = bookMapper.selectContentPairsById(chapterId);
        poemInfo.setContentList(contentList);

        return poemInfo;
    }

    // 构建存在service的导航树
    private List<NavTreeData> buildSeriesTree(Long  seriesId) {
        // 1. 获取系列信息
        Map<String, Object> series = bookMapper.selectSeriesById(seriesId);

        // 2. 获取所有书籍及章节（批量查询优化）
        List<Map<String, Object>> books = bookMapper.selectBooksBySeries(seriesId);
        List<NavTreeData> bookNodes = new ArrayList<>();

        for (Map<String, Object> book : books) {
            List<Map<String, Object>> chapters = bookMapper.selectChaptersByBook((Long) book.get("id"));
            List<NavTreeData> chapterNodes = chapters.stream()
                    .map(c -> new NavTreeData(c.get("id").toString(), (String)c.get("name"), null))
                    .collect(Collectors.toList());

            bookNodes.add(new NavTreeData(
                    (String) book.get("name"),
                    (String) book.get("name"),
                    chapterNodes));
        }

        return Collections.singletonList(
                new NavTreeData((String) series.get("name"), (String) series.get("name"), bookNodes));
    }

    // 构建存在book的导航树
    private List<NavTreeData> buildBookTree(Long  bookId) {
        Map<String, Object> book = bookMapper.selectBookById(bookId);
        List<Map<String, Object>> chapters = bookMapper.selectChaptersByBook(bookId);

        List<NavTreeData> chapterNodes = chapters.stream()
                .map(c -> new NavTreeData(c.get("id").toString(), (String)c.get("name"), null))
                .collect(Collectors.toList());

        return Collections.singletonList(
                new NavTreeData((String) book.get("name"), (String) book.get("name"), chapterNodes));
    }

    /**
     *  3.书籍详情页导航树获取
     * */
    @Cacheable(value = "navTree", key = "#chapterId")
    public List<NavTreeData> getNavTree(Long chapterId) {
        // 1. 获取章节关联信息
        Map<String, Object> relation = bookMapper.selectChapterRelations(chapterId);

        // 2. 根据关联类型构建树
        if (relation.get("series_id") != null) {
            return buildSeriesTree((Long) relation.get("series_id"));
        } else if (relation.get("book_id") != null) {
            return buildBookTree((Long) relation.get("book_id"));
        } else {
            return Collections.singletonList(
                    new NavTreeData(chapterId.toString(), (String) relation.get("chapter_name"), null));
        }
    }

    /**
     *  2.1 查询所有chapter数据，搜索列表展示
     * */
    @Override
    public PageResult<ClassicPoemInfo> page(SearchQueryParam param) {
        param.setStartIndex(param.getStartIndex() + 1);
        // 执行查询
        List<ClassicPoemInfo> list = bookMapper.list(param);

        // 处理分页结果
        return new PageResult<>(bookMapper.countTotal(), list);
    }

    /**
     *  2.2 携带title查询
     * */
    @Override
    public PageResult<ClassicPoemInfo> searchTitle(SearchQueryParam param) {
        // 执行查询
        List<ClassicPoemInfo> list = bookMapper.list(param);
        return new PageResult<>((long) list.size(), list);
    }

}
