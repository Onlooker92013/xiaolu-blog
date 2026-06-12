package com.xiaolu.blog.controller;

import com.xiaolu.blog.common.Result;
import com.xiaolu.blog.dto.ArticleVO;
import com.xiaolu.blog.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

@io.swagger.v3.oas.annotations.tags.Tag(name = "RSS")
@RestController
@RequestMapping("/api/rss")
@RequiredArgsConstructor
public class RssController {

    private final ArticleService articleService;

    @Value("${blog.rss.title}")
    private String blogTitle;

    @Value("${blog.rss.description}")
    private String blogDesc;

    @Value("${blog.rss.author}")
    private String blogAuthor;

    @Operation(summary = "RSS Feed")
    @GetMapping(produces = "application/xml;charset=UTF-8")
    public String rss() {
        var query = new com.xiaolu.blog.dto.ArticleQuery();
        query.setPage(1);
        query.setPageSize(20);
        var result = articleService.page(query);
        List<ArticleVO> articles = result.getList();

        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<rss version=\"2.0\" xmlns:atom=\"http://www.w3.org/2005/Atom\">\n");
        xml.append("<channel>\n");
        xml.append("<title>").append(blogTitle).append("</title>\n");
        xml.append("<link>https://xiaolu.college</link>\n");
        xml.append("<description>").append(blogDesc).append("</description>\n");
        xml.append("<language>zh-CN</language>\n");
        xml.append("<lastBuildDate>").append(formatRfc822(new Date())).append("</lastBuildDate>\n");

        for (ArticleVO article : articles) {
            xml.append("<item>\n");
            xml.append("<title>").append(article.getTitle()).append("</title>\n");
            xml.append("<link>https://xiaolu.college/article/").append(article.getId()).append("</link>\n");
            xml.append("<description>").append(article.getSummary() != null ? article.getSummary() : "").append("</description>\n");
            xml.append("<author>").append(blogAuthor).append("</author>\n");
            xml.append("<pubDate>").append(formatRfc822(article.getCreatedAt())).append("</pubDate>\n");
            xml.append("<guid>https://xiaolu.college/article/").append(article.getId()).append("</guid>\n");
            xml.append("</item>\n");
        }

        xml.append("</channel>\n</rss>");
        return xml.toString();
    }

    private String formatRfc822(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
            Date date = sdf.parse(dateStr.replace("T", "T"));
            SimpleDateFormat rfc822 = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
            rfc822.setTimeZone(TimeZone.getTimeZone("GMT"));
            return rfc822.format(date);
        } catch (Exception e) {
            return formatRfc822(new Date());
        }
    }

    private String formatRfc822(Date date) {
        SimpleDateFormat rfc822 = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
        rfc822.setTimeZone(TimeZone.getTimeZone("GMT"));
        return rfc822.format(date);
    }
}
