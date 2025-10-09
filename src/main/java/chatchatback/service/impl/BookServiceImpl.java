package chatchatback.service.impl;

import chatchatback.mapper.BookMapper;
import chatchatback.pojo.dto.*;
import chatchatback.pojo.entity.ClassicPoemInfo;
import chatchatback.pojo.entity.Paragraphs;
import chatchatback.pojo.vo.NavTreeDataVO;
import chatchatback.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.javassist.NotFoundException;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private RestHighLevelClient esClient;

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
    private List<NavTreeDataVO> buildSeriesTree(Long  seriesId) {
        // 1. 获取系列信息
        Map<String, Object> series = bookMapper.selectSeriesById(seriesId);

        // 2. 获取所有书籍及章节（批量查询优化）
        List<Map<String, Object>> books = bookMapper.selectBooksBySeries(seriesId);
        List<NavTreeDataVO> bookNodes = new ArrayList<>();

        for (Map<String, Object> book : books) {
            List<Map<String, Object>> chapters = bookMapper.selectChaptersByBook((Long) book.get("id"));
            List<NavTreeDataVO> chapterNodes = chapters.stream()
                    .map(c -> new NavTreeDataVO(c.get("id").toString(), (String)c.get("name"), null))
                    .collect(Collectors.toList());

            bookNodes.add(new NavTreeDataVO(
                    (String) book.get("name"),
                    (String) book.get("name"),
                    chapterNodes));
        }

        return Collections.singletonList(
                new NavTreeDataVO((String) series.get("name"), (String) series.get("name"), bookNodes));
    }

    // 构建存在book的导航树
    private List<NavTreeDataVO> buildBookTree(Long  bookId) {
        Map<String, Object> book = bookMapper.selectBookById(bookId);
        List<Map<String, Object>> chapters = bookMapper.selectChaptersByBook(bookId);

        List<NavTreeDataVO> chapterNodes = chapters.stream()
                .map(c -> new NavTreeDataVO(c.get("id").toString(), (String)c.get("name"), null))
                .collect(Collectors.toList());

        return Collections.singletonList(
                new NavTreeDataVO((String) book.get("name"), (String) book.get("name"), chapterNodes));
    }

    /**
     *  3.书籍详情页导航树获取
     * */
    @Cacheable(value = "navTree", key = "#chapterId")
    public List<NavTreeDataVO> getNavTree(Long chapterId) {
        // 1. 获取章节关联信息
        Map<String, Object> relation = bookMapper.selectChapterRelations(chapterId);

        // 2. 根据关联类型构建树
        if (relation.get("series_id") != null) {
            return buildSeriesTree((Long) relation.get("series_id"));
        } else if (relation.get("book_id") != null) {
            return buildBookTree((Long) relation.get("book_id"));
        } else {
            return Collections.singletonList(
                    new NavTreeDataVO(chapterId.toString(), (String) relation.get("chapter_name"), null));
        }
    }

    /**
     *  2.1 查询所有chapter数据，搜索列表展示
     * */
    @Override
    public PageResult<ClassicPoemInfo> page(SearchQueryParamDTO param) {
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
    public PageResult<ClassicPoemInfo> searchTitle(SearchQueryParamDTO param) {
        // 执行查询
        List<ClassicPoemInfo> list = bookMapper.list(param);
        return new PageResult<>((long) list.size(), list);
    }

    /**
     *  6.根据es快速匹配古文内容（paragraphs）
     * */
    public List<Paragraphs> searchAncientTextByKeyword(String keyword) {
        SearchRequest searchRequest = new SearchRequest("paragraphs");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchQuery("ancientText", keyword).analyzer("ik_smart"));
        sourceBuilder.size(5); // 只返回前5个
        searchRequest.source(sourceBuilder);

        List<Paragraphs> resultList = new ArrayList<>();
        try {
            SearchResponse response = esClient.search(searchRequest, RequestOptions.DEFAULT);
            for (SearchHit hit : response.getHits().getHits()) {
                Map<String, Object> map = hit.getSourceAsMap();
                Paragraphs p = new Paragraphs();
                p.setId(Long.valueOf(map.get("id").toString()));
                p.setChapterId(map.get("chapterId") == null ? null : Long.valueOf(map.get("chapterId").toString()));
                p.setAncientText((String) map.get("ancientText"));
                p.setModernText((String) map.get("modernText"));
                resultList.add(p);
            }
        } catch (Exception e) {
            throw new RuntimeException("ES搜索失败: " + e.getMessage());
        }
        return resultList;
    }

    
    @Override
    public List<Paragraphs> searchAncientTextByKeywordMysql(String keyword) {
        // 只查前113588条，模糊匹配ancient_text字段，返回前5条
        List<Paragraphs> allMatched = bookMapper.searchAncientTextByKeywordMysqlAll(keyword);
        return allMatched.stream().limit(5).toList();
    }
}
