package cn.aliyun.core.controller.upload;

import cn.aliyun.core.entity.Result;
import cn.aliyun.core.utils.fdfs.FastDFSClient;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("upload")
public class UploadController {
    @Value("${FILE_SERVER_URL}")
    private String FILE_SERVER_URL;
    @RequestMapping("uploadFile")
    public Result uploadFile(MultipartFile file){
        try {
            //使用工具类将附件上传
            String conf="classpath:fastDFS/fdfs_client.conf";
            FastDFSClient fastDFSClient = new FastDFSClient(conf);
            String filename = file.getOriginalFilename();
            String extension = FilenameUtils.getExtension(filename);
            String s = fastDFSClient.uploadFile(file.getBytes(),extension,null);
            s=FILE_SERVER_URL+s;
            return new Result(true,s);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"上传失败");
        }
    }
}
