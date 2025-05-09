package chatchatback.service;

import chatchatback.pojo.vo.AuthorWordCloudVO;

import java.util.List;

public interface WordCloudService {

    /**
     * 获取作者词云
     */
    AuthorWordCloudVO getAuthorWordCloud(Long id);
}
