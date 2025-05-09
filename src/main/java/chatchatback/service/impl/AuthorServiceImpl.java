package chatchatback.service.impl;

import chatchatback.mapper.AuthorMapper;
import chatchatback.pojo.vo.AuthorProfileVO;
import chatchatback.pojo.vo.AuthorVO;
import chatchatback.service.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorServiceImpl implements AuthorService {

    @Autowired
    private AuthorMapper authorMapper;

    /**
     * 获取所有作者
     * @return
     */
    @Override
    public List<AuthorVO> getAllAuthor() {
        List<AuthorVO> authors = authorMapper.getAllAuthor();

        //模拟数据创建
        AuthorVO poem = AuthorVO.builder()
                .id(1000L)
                .name("苏轼")
                .dynasty("北宋")
                .build();

        authors.add(poem);
        return authors;
    }

    /**
     * 获取诗人信息常用词汇展示信息
     */
    @Override
    public AuthorProfileVO getAuthorInfo(Long id) {
        //模拟数据创建
        if (id == 1000) {
            return AuthorProfileVO.builder()
                    .totalWorks(9999)
                    .mostUsedWord("醉")
                    .dynasty("北宋")
                    .build();
        }
        return null;
    }
}
