package com.gamehub.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gamehub.backend.pojo.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
// 用Mybatis-Plus自动生成常见的SQL语句
public interface UserMapper extends BaseMapper<User> {
}
