package com.domain.todo.auth.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface AuthMapper {
    Map<String,Object> findByProviderAndId(@Param("provider") String provider,
                                           @Param("providerId") String providerId);

    int insertUser(Map<String,Object> param);
}
