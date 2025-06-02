package chatchatback.mapper;

import chatchatback.pojo.dto.WordSearchDTO;
import chatchatback.pojo.entity.ExamplesPoems;
import chatchatback.pojo.entity.MeaningsPoems;
import chatchatback.pojo.entity.PhoneticInitials;
import chatchatback.pojo.entity.WordsPoems;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface WordMapper {

    // 根据搜索词搜索词
    List<WordsPoems> searchWord(WordSearchDTO wordSearchDTO);

    // 根据id搜索词
    List<MeaningsPoems> searchMeanings(Long wordId);

    // 根据id搜索例句
    List<ExamplesPoems> searchBatchExamples(List<Long> ids);

    // 搜索词的分类
    @Select("select initial,display_name  from phonetic_initials")
    List<PhoneticInitials> searchWordClass();
}