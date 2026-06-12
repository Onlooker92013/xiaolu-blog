package com.xiaolu.blog.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.xiaolu.blog.common.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class FileService {

    @Value("${blog.upload.path}")
    private String uploadPath;

    private static final String[] ALLOWED_EXT = {".jpg", ".jpeg", ".png", ".gif", ".webp", ".svg"};

    public Map<String, String> uploadImage(MultipartFile file) throws IOException {
        if (file.isEmpty()) throw new BusinessException("文件不能为空");

        String ext = FileUtil.extName(file.getOriginalFilename()).toLowerCase();
        boolean allowed = false;
        for (String e : ALLOWED_EXT) {
            if (("." + ext).equals(e)) { allowed = true; break; }
        }
        if (!allowed) throw new BusinessException("不支持的文件格式");

        String fileName = IdUtil.fastSimpleUUID() + "." + ext;
        String dateDir = java.time.LocalDate.now().toString().replace("-", "/");

        // Resolve absolute path
        File baseDir = new File(uploadPath);
        if (!baseDir.isAbsolute()) {
            baseDir = new File(System.getProperty("user.home"), uploadPath.replace("./", ""));
        }
        File dir = new File(baseDir, dateDir);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new BusinessException("无法创建上传目录");
        }

        File dest = new File(dir, fileName);
        file.transferTo(dest);

        String url = "/uploads/" + dateDir + "/" + fileName;
        Map<String, String> result = new HashMap<>();
        result.put("url", url);
        result.put("filename", file.getOriginalFilename());
        return result;
    }
}
