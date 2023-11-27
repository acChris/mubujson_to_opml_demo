package com.example.demo.opml;


import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 幕布 json 转为 opml 文件
 *
 * @author weng
 * @date 2023-11-27 14:18
 * @since demo
 */
public class OpmlDemo {

    /**
     * ------------------------------ 以下为 OPML 参数 ------------------------------
     */
    /**
     * 开头
     */
    private static String opmlHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<opml version=\"2.0\">\n" +
            "  <head>\n" +
            "    <title>{}</title>\n" +
            "  </head>\n";

    /**
     * body
     */
    private static String opmlBody = "<body>{}</body>\n";

    /**
     * 结尾
     */
    private static String opmlEnd = "\n</opml>";

    /**
     * outline 模板（mubuText，mubuNote，mubuImages 都要 url编码；例如 %5B%7B%22id%22%3A）
     */
    private static String outlineTemplate = "<outline text=\"{}\" _mubu_text=\"{}\" _note=\"{}\" _mubu_note=\"{}\" _mubu_images=\"{}\">\n";

    private static String outlineEnd = "\n</outline>\n";

    /**
     * 所有 outline 内容值
     */
    private static StringBuilder content = new StringBuilder();

    /**
     * 幕布 json => opml
     *
     * @param json {"code":0,"data":{"role":roleId,"baseVersion":versionId,"author":{"level":levelXXX,"name":"nameVal"},"name":"nameVal","definition":"{\"nodes\":[{\"id\":\"idXXX\",\"modified\":1692332288320,\"text\":\"this is a text!\"},{\"children\":[{\"heading\":2,\"id\":\"id22\",\"modified\":1692247564320,\"text\":\"text 222 !\"}],\"id\":\"id2222!\",\"modified\":1692225569820,\"text\":\"text!!!!\"}]}","directory":[{"name":"dirName","id":"dirId"}]}}
     * @return
     */
    public static boolean transJson2Opml(String json, File filePath) {

        // 1. 获取 json 的 data.name、data.definition、data.directory
        List<String> targetAttributes = Arrays.asList("data.name", "data.definition", "data.directory.id", "data.directory.name");
        List<String> targetVal = new ArrayList<>(targetAttributes.size());
        JSONObject jsonObject = JSONUtil.parseObj(json);
        targetAttributes.forEach(attr -> targetVal.add(String.valueOf(jsonObject.getByPath(attr))));

        // 2. 获取具体文件内容值：文件名，文件值，文件id，文件目录
        String fileName = targetVal.get(0);
        String definition = targetVal.get(1);
        String fileDirId = targetVal.get(2), fileDirName = targetVal.get(3);
        List<MubuNode> mubuNodes = new ArrayList<>();
        if (StrUtil.isNotBlank(definition)) {
            JSONArray nodes = JSONUtil.parseObj(definition).getJSONArray("nodes");
            mubuNodes = JSONUtil.toList(nodes, MubuNode.class);
            mubuNodes.forEach(System.out::println);
        }

        // 3. 遍历每一个节点，生成相应的 <outline> 标签
        getContent(mubuNodes, 1);

        // 4. 拼接所有 OPML 内容，并生成文件
        String header = StrUtil.format(opmlHeader, fileName);
        String body = StrUtil.format(opmlBody, content.toString());
        String allOpml = header + body + opmlEnd;
        FileUtil.touch(filePath);
        FileUtil.writeUtf8String(allOpml, filePath);
        return true;
    }

    private static void getContent(List<MubuNode> mubuNodes, int level) {
        if (mubuNodes == null || mubuNodes.size() == 0) {
            if (level >= 2) content.append(outlineEnd);
            return ;
        }
        mubuNodes.forEach(node -> {
            // 插入当前节点的值
            insertNodeVal(node);

            // 遍历所有子节点
            List<MubuNode> children = node.getChildren();
            if (children != null) {
                children.forEach(chi -> {
                    insertNodeVal(chi);
                    getContent(chi.getChildren(), level + 1);
                });
            }

            // 结尾
            content.append(outlineEnd);
        });
    }

    private static void insertNodeVal(MubuNode node) {
        String outlineText = node.getText(),
                mubuText = URLUtil.encode(outlineText),
                note = node.getNote(),
                mubuNote = URLUtil.encode(note),
                mubuImages = URLUtil.encode(JSONUtil.toJsonStr(node.getImages()));
        content.append(StrUtil.format(outlineTemplate, outlineText, mubuText, note, mubuNote, mubuImages));
    }

    public static void main(String[] args) {
        String json = "{\"code\":0,\"data\":{\"role\":roleId,\"baseVersion\":versionId,\"author\":{\"level\":levelXXX,\"name\":\"nameVal\"},\"name\":\"nameVal\",\"definition\":\"{\\\"nodes\\\":[{\\\"id\\\":\\\"idXXX\\\",\\\"modified\\\":1692332288320,\\\"text\\\":\\\"this is a text!\\\"},{\\\"children\\\":[{\\\"heading\\\":2,\\\"id\\\":\\\"id22\\\",\\\"modified\\\":1692247564320,\\\"text\\\":\\\"text 222 !\\\"}],\\\"id\\\":\\\"id2222!\\\",\\\"modified\\\":1692225569820,\\\"text\\\":\\\"text!!!!\\\"}]}\",\"directory\":[{\"name\":\"dirName\",\"id\":\"dirId\"}]}}";
        transJson2Opml(json, new File("./test.opml"));
    }
}
