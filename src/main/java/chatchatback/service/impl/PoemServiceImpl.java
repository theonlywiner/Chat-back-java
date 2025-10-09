package chatchatback.service.impl;

import chatchatback.mapper.GradeMapper;
import chatchatback.mapper.PoemMapper;
import chatchatback.pojo.dto.PageResult;
import chatchatback.pojo.dto.PoemPageQueryDTO;
import chatchatback.pojo.dto.PoemPageQueryGradeDTO;
import chatchatback.pojo.entity.Dynasty;
import chatchatback.pojo.entity.Grade;
import chatchatback.pojo.entity.Poem;
import chatchatback.pojo.vo.DailyPoemVO;
import chatchatback.pojo.vo.PoemListVO;
import chatchatback.service.PoemService;
import chatchatback.utils.CurrentHolder;
import chatchatback.utils.PageQueryUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static chatchatback.constant.Constant.MaxGradeId;

@Slf4j
@Service
public class PoemServiceImpl implements PoemService {

    @Autowired
    private PoemMapper poemMapper;
    @Autowired
    private GradeMapper gradeMapper;

    @Autowired
    private PageQueryUtils pageQueryUtils;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    private static final String DAILY_POEM_KEY = "chatchat:daily:poem";
    private static final long DAILY_POEM_TTL = 24 * 60 * 60; // 24小时过期

    /**
     * 获取年级列表
     */
    @Override
    public List<Grade> getGradeList() {
        List<Grade> gradeList = gradeMapper.getGradeList();
        Grade grade = new Grade(0L, "全部");
        gradeList.addFirst(grade);
        return gradeList;
    }

    /**
     * 根据年级id获取诗词列表
     */
    @Override
    public PageResult<PoemListVO> getPoemsByGradeId(PoemPageQueryGradeDTO poemPageQueryGradeDTO) {
        // 获取年级信息
        int userId = CurrentHolder.getCurrentId();
        Long gradeId = gradeMapper.getGradeIdById((long) userId);
        poemPageQueryGradeDTO.setGradeId(gradeId);

        if (gradeId == MaxGradeId) {
            PoemPageQueryDTO poemPageQueryDTO = new PoemPageQueryDTO();
            BeanUtils.copyProperties(poemPageQueryGradeDTO, poemPageQueryDTO);
            poemPageQueryDTO.setGradeId(0L);
            PageResult<PoemListVO> allPoems = getAllPoems(poemPageQueryDTO);
//            System.out.println(allPoems);
            return allPoems;
        }else {
            //设置分页信息
            PageHelper.startPage(poemPageQueryGradeDTO.getPage(), poemPageQueryGradeDTO.getPageSize());

            //执行查询
            Page<PoemListVO> poemList = (Page<PoemListVO>)poemMapper.getPoemsByGradeId(poemPageQueryGradeDTO);
            log.info("获取诗词列表,{}",poemList.getResult());
            return new PageResult<>(poemList.getTotal(), poemList.getResult());
        }
    }

    /**
     * 根据id获取诗词
     */
    @Override
    public Poem getPoemById(Long id) {
        return poemMapper.getPoemById(id);
    }

    /**
     * 获取所有诗词，分页
     */
    @Override
    public PageResult<PoemListVO> getAllPoems(PoemPageQueryDTO dto) {
        // 动态计算lastId
        pageQueryUtils.calculateLastId(dto);

        // 分页查询
        List<PoemListVO> list = poemMapper.getAllPoems(dto);

        // 总数查询（带条件）
        Long total = poemMapper.countAllPoems(dto);
        System.out.println(total);
        return new PageResult<>(total, list);
    }

    /**
     * 根据id列表获取诗词名称
     */
    @Override
    public List<PoemListVO> getPoemNameByIds(List<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            return poemMapper.getPoemNameByIds(ids);
        }
        return null;
    }

    /**
     * 获取朝代列表
     */
    @Override
    public List<Dynasty> getDynasties() {
        List<Dynasty> dynasties = poemMapper.getDynasties();
        dynasties.addFirst(new Dynasty(0L, "全部"));
        return dynasties;
    }

    /**
     * 获取每日随机诗词
     */
    @Override
    public DailyPoemVO getDailyPoem() {
        Poem poem = null;
        Long randomId = poemMapper.selectRandomPoemId();
        poem = poemMapper.selectPoemDetailsById(randomId);
        if (poem == null) {
            return null;
        }

        // 转换为前端需要的格式
        DailyPoemVO dailyPoemVO = new DailyPoemVO();
        dailyPoemVO.setTitle(poem.getName());
        dailyPoemVO.setAuthor(poem.getAuthor());
        dailyPoemVO.setDynasty(poem.getDynasty());
        dailyPoemVO.setBackground(poem.getBackground());

        // 将全文内容按行分割成列表
        if (poem.getFullAncientContent() != null) {
            dailyPoemVO.setContent(Arrays.asList(poem.getFullAncientContent().split("\\n")));
        }

        return dailyPoemVO;
    }

}
