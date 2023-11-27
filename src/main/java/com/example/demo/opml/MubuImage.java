package com.example.demo.opml;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 幕布图片类
 *
 * @author weng
 * @date 2023-11-27 16:03
 * @since demo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MubuImage {

    private String id;

    private Integer oh;

    private Integer ow;

    private String uri;

    private Integer w;

}
