package com.cjree.core.basic.config;

import feign.RequestTemplate;
import feign.codec.EncodeException;
import feign.codec.Encoder;
import feign.form.ContentType;
import feign.form.FormEncoder;
import feign.form.MultipartFormContentProcessor;
import feign.form.spring.SpringManyMultipartFilesWriter;
import feign.form.spring.SpringSingleMultipartFileWriter;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;

public class SpringFormEncoder extends FormEncoder
{

    public SpringFormEncoder()
    {
        this(((Encoder) (new Default())));
    }

    public SpringFormEncoder(Encoder delegate)
    {
        super(delegate);//调用父类的构造方法
        MultipartFormContentProcessor processor = (MultipartFormContentProcessor)getContentProcessor(ContentType.MULTIPART);
        processor.addWriter(new SpringSingleMultipartFileWriter());
        processor.addWriter(new SpringManyMultipartFilesWriter());
    }

    public void encode(Object object, Type bodyType, RequestTemplate template)
            throws EncodeException
    {
        if(!bodyType.equals(MultipartFile.class))
        {
            super.encode(object, bodyType, template);//调用FormEncoder对应方法
        } else
        {
            MultipartFile file = (MultipartFile)object;
            Map data = Collections.singletonMap(file.getName(), object);
            super.encode(data, MAP_STRING_WILDCARD, template);
            return;
        }
    }
}