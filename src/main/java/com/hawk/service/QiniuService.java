package com.hawk.service;

import com.alibaba.fastjson.JSONObject;
import com.hawk.data.constant.ResultEnum;
import com.hawk.exception.MyException;
import com.hawk.util.PasswordUtil;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
public class QiniuService {

    private static ExecutorService executor = Executors.newCachedThreadPool();

    // 账号密钥，可在个人中心-密钥管理中查看
    private static final String ACCESS_KEY = "TnIOszZVvneKT9xI9ySSiXpbpCsJeBGoFCUu6jTl";

    private static final String SECRET_KEY = "1Mf5ksCyGwIzxTJoZ2zSUS65tS034t48G9nMQJV_";

    // 要上传的空间
    private static final String BUCKET_NAME = "hawk97";

    // 七牛默认外链域名
    private static final String QINIU_IMAGE_DOMAIN = "http://p44lruo4o.bkt.clouddn.com/";

    // 密钥配置
    private Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);

    // 创建配置对象
    private Configuration cfg = new Configuration(Zone.huadong());

    // 创建上传对象
    private UploadManager uploadManager = new UploadManager(cfg);

    private static String[] IMAGE_FILE_EXT = new String[] {"bmp", "jpg", "jpeg", "png", "gif"};

    private boolean isImageAllowed(String imageExt) {
        for (String ext : IMAGE_FILE_EXT) {
            if (ext.equals(imageExt)) {
                return true;
            }
        }
        return false;
    }

    // 获得简单上传的凭证
    public String getUpToken() {
        return auth.uploadToken(BUCKET_NAME);
    }

    public String upload(MultipartFile image) {
        int dotPos = image.getOriginalFilename().lastIndexOf(".");
        if (dotPos < 0) {
            throw new MyException(ResultEnum.IMAGE_FORMAT_ERROR);
        }
        String imageExt = image.getOriginalFilename().substring(dotPos + 1).toLowerCase();
        if (!isImageAllowed(imageExt)) {
            throw new MyException(ResultEnum.IMAGE_FORMAT_ERROR);
        }
        String imageName = PasswordUtil.get10UUID() + "." + imageExt;
        executor.execute(() -> {
            try {
                Response response = uploadManager.put(image.getBytes(), imageName, getUpToken());
                if (response.isOK() && response.isJson()) {
                    log.info("七牛云上传图片成功：" + response.bodyString());
                }
                log.error("七牛云上传图片失败：" + response.bodyString());
            } catch (QiniuException e) {
                log.error("七牛异常：" + e.getMessage());
            } catch (IOException e) {
                log.error("IO异常：" + e.getMessage());
            }
        });
        return QINIU_IMAGE_DOMAIN + imageName;
    }
}
