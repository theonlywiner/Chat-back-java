package chatchatback.service;

import chatchatback.pojo.vo.AuthorProfileVO;
import chatchatback.pojo.vo.AuthorVO;

import java.util.List;

public interface AuthorService {

    /**
     * 获取所有诗人
     * @return
     */
    List<AuthorVO> getAllAuthor();

    /**
     * 获取诗人信息常用词汇展示信息
     */
    AuthorProfileVO getAuthorInfo(Long id);
}
