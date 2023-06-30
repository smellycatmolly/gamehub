package com.gamehub.backend.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data //@Data 注解会自动生成 getPassword() 方法，因为它会自动为每个字段生成相应的 getter 和 setter 方法。
@NoArgsConstructor  //注解生成一个无参构造函数，这在实例化对象时很有用，可以直接使用无参构造函数创建对象。
@AllArgsConstructor // 注解生成一个包含所有字段的构造函数，这在需要一次性设置所有属性的情况下很有用。

public class Bot {
    @TableId(type = IdType.AUTO) // 让id自增1
    private Integer id;   // 在pojo里要用Integer
    private Integer userId;   // 在pojo的驼峰命名法，对应了user_id
    private String title;
    private String description;
    private String content;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date createtime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date modifytime;
}