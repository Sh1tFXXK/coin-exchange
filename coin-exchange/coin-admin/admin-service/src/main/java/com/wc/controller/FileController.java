package com.wc.controller;

import cn.hutool.core.date.DateUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.wc.model.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 文件上传
 */
@RestController
@Api(tags ="文件上传")
public class FileController {


    @Autowired
    private OSS ossClient;

    @Value("${spring.cloud.alicoud.access-key}")
    private String accessId;

    @Value("${oss.bucket.name:exchange-imgs}")
    private String bucketName;

    @Value("${spring.cloud.alicloud.oss.endpoint}")
    private String endPoint;

    @Value("${oss.callback.url:sj.frp.one}")
    private String callBackUrl;

    @ApiOperation(value ="文件上传")
    @PostMapping("/image/AliYunImgUpload")
    @ApiImplicitParams({
            @ApiImplicitParam(name ="file",value ="你要上传的文件")
    })
    public R<String> fileUpload(@RequestParam("file") MultipartFile file) throws IOException {
        /**
         * 3个参数：
         * 1 bucketName
         * 2 FileName
         * 3文件的路径
         */
        String fileName= DateUtil.today().replaceAll("-","/")+"/"+file.getOriginalFilename() ;
        ossClient.putObject(bucketName,fileName, file.getInputStream());
        //文件上传
        //上传成功后，路径为https://exchange-imgs.oss-cn-beijing.aliyuncs.com/2020/10/10/1588586643_meitu_1.jpg
        return R.ok("https://"+bucketName+"." +endPoint+"/"+fileName) ;
    }

    @GetMapping("/image/pre/upload")
    @ApiOperation(value ="获取一个上传的票据")
    public R<Map<String,String>> asyncUpload() {
        String dir = DateUtil.today().replaceAll("-","/");
        Map<String,String>uploadPolicy= getUploadPolicy(30L,3*1024*1024L,dir,callBackUrl);
        return R.ok(uploadPolicy);
    }

    /**
     *
     获取上传的票据
     *
     * @paramexpireTime票据的过期时间
     * @paramdir文件上传的文件夹
     * @parammaxFileSize上传文件的最大体积
     * @paramcallbackUrl前端把文件上传成功后的回调地址
     * @return票据
     */
    private Map<String, String> getUploadPolicy(Long expireTime,Long maxFileSize, String dir,String callbackUrl) {
        //返回给前端的值
        Map<String,String> respMap= new LinkedHashMap<>();
        long expireEndTime=System.currentTimeMillis() + expireTime *1000;
        Date expiration=new Date(expireEndTime);//过期时间
        //PostObject请求最大可 支 持 的 文 件大 小为5GB，即CONTENT_LENGTH_RANGE为5*1024*1024*1024。
        PolicyConditions policyConds = new PolicyConditions();
        //设置上传的大小,最大为3M
        policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE,0,maxFileSize);
        //上传的到那个文件夹
        policyConds.addConditionItem(MatchMode.StartWith,PolicyConditions.COND_KEY, dir);
        try{
            //生成一个票据
            String postPolicy=ossClient.generatePostPolicy(expiration,policyConds);
            //对票据加密
            byte[] binaryData = new byte[0];
            binaryData =postPolicy.getBytes("utf-8");
            String encodedPolicy= BinaryUtil.toBase64String(binaryData);
            String postSignature=ossClient.calculatePostSignature(postPolicy);
            respMap.put("accessid",accessId);
            respMap.put("policy",encodedPolicy);
            respMap.put("signature",postSignature);
            respMap.put("dir",dir);
            respMap.put("host","https://"+ bucketName + "."+endPoint);
            // host的格式为https://bucketname.endpoint
            respMap.put("expire",String.valueOf(expireEndTime/1000));
            respMap.put("callbackUrl", callbackUrl);//前端上传成功了,可以使用该地址来通知后端.
        }catch(UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return respMap;
    }
}
