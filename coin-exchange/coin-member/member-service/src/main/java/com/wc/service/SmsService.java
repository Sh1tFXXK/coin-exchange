package com.wc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wc.domain.Sms;

public interface SmsService extends IService<Sms> {
/**
 *
 短信的发现
 * @paramsms
 *
短信
 * @return
 *
是否发送成功
 */
    boolean sendSms(Sms sms);
}