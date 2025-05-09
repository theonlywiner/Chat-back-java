package chatchatback.service.impl;

import chatchatback.mapper.WordCloudMapper;
import chatchatback.pojo.entity.WordCloudItem;
import chatchatback.pojo.vo.AuthorWordCloudVO;
import chatchatback.service.WordCloudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class WordCloudServiceImpl implements WordCloudService {

    @Autowired
    private WordCloudMapper wordCloudMapper;

    /**
     * 获取作者词云
     * @param id
     * @return
     */
    @Override
    public AuthorWordCloudVO getAuthorWordCloud(Long id) {
        //模拟数据
        if (id == 1000) {
            List<WordCloudItem> wordCloudItemList = Arrays.asList(
                    new WordCloudItem("江海", 25, "象征漂泊、自由或隐逸，寄托超脱世俗的志向。"),
                    new WordCloudItem("清风", 22, "喻高洁品格、闲适心境，或与自然共鸣的意象。"),
                    new WordCloudItem("故人", 19, "追忆友情、感怀离散，或暗含世事沧桑。"),
                    new WordCloudItem("孤鸿", 12, "象征孤独、清高或人生漂泊无依。"),
                    new WordCloudItem("烟雨", 18, "描绘朦胧景致，隐喻人生困境或超然心境。"),
                    new WordCloudItem("白发", 15, "感慨年华老去，或自嘲豁达的人生态度。"),
                    new WordCloudItem("天涯", 17, "表达漂泊羁旅、人生辽阔或孤独疏离。"),
                    new WordCloudItem("醉", 28, "抒发放达不羁、借酒忘忧，或暗含对现实的疏离。"),
                    new WordCloudItem("明月", 20, "寄托思乡怀人、澄明心境或哲理性追问。"),
                    new WordCloudItem("扁舟", 12, "象征退隐江湖、自由漂泊或超脱世俗。"),
                    new WordCloudItem("功名", 14, "反思仕途沉浮，表达淡泊或自嘲。"),
                    new WordCloudItem("梦", 19, "暗喻人生虚幻、往事如烟，或理想与现实的矛盾。"),
                    new WordCloudItem("斜阳", 14, "渲染苍凉意境，或暗喻时光流逝、盛衰变迁。"),
                    new WordCloudItem("长恨", 9, "感慨人生遗憾、世事无常或理想未竟。"),
                    new WordCloudItem("一笑", 15, "体现豁达自嘲、超然物外或对苦难的释然。"),
                    new WordCloudItem("千古", 10, "抒发历史兴亡之感，或追求超越时空的价值。"),
                    new WordCloudItem("人间", 32, "常指现实世界、世俗纷扰，或与自然、理想之境对比。"),
                    new WordCloudItem("江南", 18, "象征自然风物之美、闲适生活，或寄托思乡之情。"),
                    new WordCloudItem("平生", 28, "感慨人生际遇、总结心志，或表达对过往的反思。"),
                    new WordCloudItem("斜月", 8, "描绘深夜孤寂之景，或寄托渺远思绪。"),
                    new WordCloudItem("世事", 16, "指人间纷扰、无常变化，常与超脱之心对比。"),
                    new WordCloudItem("浮生", 13, "慨叹人生短暂虚幻，或倡导及时行乐。"),
                    new WordCloudItem("西风", 11, "渲染萧瑟秋意，或隐喻人生逆境。"),
                    new WordCloudItem("青山", 16, "象征自然永恒、归隐之志或豁达胸襟。"),
                    new WordCloudItem("浊酒", 10, "借酒消愁或表达简朴生活的态度。"),
                    new WordCloudItem("归去", 24, "表达对隐逸生活的向往、超脱世俗的心境。")
            );

            return AuthorWordCloudVO.builder()
                    .author("苏轼")
                    .dynasty("北宋")
                    .wordCloudItemList(wordCloudItemList)
                    .build();
        }
        return null;
    }
}
