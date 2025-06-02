package chatchatback.service;

import chatchatback.pojo.dto.Result;
import chatchatback.pojo.dto.WordSearchDTO;
import chatchatback.pojo.entity.PhoneticInitials;
import chatchatback.pojo.entity.WordsPoems;
import chatchatback.pojo.vo.WordsPoemsVO;

import java.util.List;

public interface WordService {

    /**
     * 搜索常用词
     */
    List<WordsPoemsVO> searchWord(WordSearchDTO wordSearchDTO);


    /**
     * 搜索常用词分类
     */
    List<PhoneticInitials> searchWordClass();
}
