package com.example.demo.opml;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 幕布 node
 *
 * @author weng
 * @date 2023-11-27 15:16
 * @since demo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MubuNode {

    /**
     * 随机 10 位大小写字母和数字，如 1DFjwwio3B
     */
    private String id;

    /**
     * 图片集
     */
    private List<MubuImage> images;

    /**
     * 修改时间。时间戳形式显示，如 1694791226461
     */
    private Long modified;

    /**
     * 标题内容
     */
    private String text = "";

    /**
     * 标题注释
     */
    private String note = "";

    /**
     * 扩展（暂时用不到）
     */
    private Boolean collapsed;

    /**
     * 子节点，可为 null
     */
    private List<MubuNode> children;


}
