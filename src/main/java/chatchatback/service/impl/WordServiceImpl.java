package chatchatback.service.impl;

import chatchatback.mapper.WordMapper;
import chatchatback.pojo.dto.Result;
import chatchatback.pojo.dto.WordSearchDTO;
import chatchatback.pojo.entity.ExamplesPoems;
import chatchatback.pojo.entity.MeaningsPoems;
import chatchatback.pojo.entity.PhoneticInitials;
import chatchatback.pojo.entity.WordsPoems;
import chatchatback.pojo.enums.PriorityStatus;
import chatchatback.pojo.vo.WordsPoemsVO;
import chatchatback.service.WordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WordServiceImpl implements WordService {

    private final WordMapper wordMapper;

    /**
     * 搜索常用词
     */
    @Override
    public List<WordsPoemsVO> searchWord(WordSearchDTO wordSearchDTO) {
        //1.查询词
        // 1.1 把前端传过来的value转换PriorityStatus
        wordSearchDTO.setStatus(PriorityStatus.fromPriority(wordSearchDTO.getPriority()));
        List<WordsPoems> wordsPoems = wordMapper.searchWord(wordSearchDTO);

        if (wordsPoems.isEmpty()) {
            return Collections.emptyList();
        }

        //2.查询词对应的所有意思
        //2.1 如果wordsPoems就一个元素，就传这个元素的id
        List<MeaningsPoems> meaningsPoems = null;
        if (wordsPoems.size() == 1) {
            meaningsPoems = wordMapper.searchMeanings(wordsPoems.getFirst().getId());
        }else {
            //2.2 并且不为空，就全查询
            meaningsPoems = wordMapper.searchMeanings(0L);
        }

        List<ExamplesPoems> examplesPoems = null;
        //3.查询词对应的所有例句
        if (wordsPoems.size() == 1) {
            List<Long> Ids = meaningsPoems.stream().map(MeaningsPoems::getId).toList();
            examplesPoems = wordMapper.searchBatchExamples(Ids);
        }else {
            examplesPoems = wordMapper.searchBatchExamples(null);
        }

        //4.转换为VO类型
        //4.1分类
        Map<Long, List<ExamplesPoems>> examplesMap = new HashMap<>();
        if (examplesPoems != null && !examplesPoems.isEmpty()) {
            examplesMap = examplesPoems.stream().collect(Collectors.groupingBy(ExamplesPoems::getMeaningId));
        }
        //4.2拼接List<ExamplesPoems>
        List<MeaningsPoems> list = new ArrayList<>();
        for (MeaningsPoems meaningsPoems1 : meaningsPoems) {
            meaningsPoems1.setExamplesPoemsList(examplesMap.get(meaningsPoems1.getId()));
            list.add(meaningsPoems1);
        }

        //5.返回结果
        //同理拼接List<WordsPoemsVO>
        //4.1分类
        Map<Long, List<MeaningsPoems>> meaningsMap = new HashMap<>();
        if (!list.isEmpty()) {
            meaningsMap = list.stream().collect(Collectors.groupingBy(MeaningsPoems::getWordId));
        }
        //4.2拼接List<ExamplesPoems>
        List<WordsPoemsVO> wordsPoemsVOList = new ArrayList<>();
        for (WordsPoems wordsPoems1 : wordsPoems) {
            WordsPoemsVO wordsPoemsVO = new WordsPoemsVO();
            BeanUtils.copyProperties(wordsPoems1, wordsPoemsVO);
            wordsPoemsVO.setMeaningsPoemsList(meaningsMap.get(wordsPoemsVO.getId()));
            wordsPoemsVOList.add(wordsPoemsVO);
        }

        return wordsPoemsVOList;
    }

    /**
     * 搜索常用词分类
     */
    @Override
    public List<PhoneticInitials> searchWordClass() {
        //添加默认选项
        List<PhoneticInitials> phoneticInitials = wordMapper.searchWordClass();
        //添加频率
//        phoneticInitials.addFirst(new PhoneticInitials(PriorityStatus.LOW_FREQUENCY.getPriority(), PriorityStatus.LOW_FREQUENCY.getPriority().substring(0, 2)));
        phoneticInitials.addFirst(new PhoneticInitials(PriorityStatus.MEDIUM_FREQUENCY.getPriority(), PriorityStatus.MEDIUM_FREQUENCY.getPriority().substring(0, 2)));
        phoneticInitials.addFirst(new PhoneticInitials(PriorityStatus.HIGH_FREQUENCY.getPriority(), PriorityStatus.HIGH_FREQUENCY.getPriority().substring(0, 2)));
        //添加全部
        phoneticInitials.addFirst(new PhoneticInitials(null, "全部"));
        return phoneticInitials;
    }
}
