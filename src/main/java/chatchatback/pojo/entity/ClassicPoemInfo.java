package chatchatback.pojo.entity;

import chatchatback.pojo.dto.ContentPair;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 古诗信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassicPoemInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String title;
    private String initial;
    private String author;
    private String dynasty;
    private String description;
    private String chapterFirstId;
    private List<ContentPair> contentList;
    private String translation;
    private String content;
    private String notes;
}

