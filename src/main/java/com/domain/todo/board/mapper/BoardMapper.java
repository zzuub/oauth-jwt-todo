package com.domain.todo.board.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface BoardMapper {
    List<Map<String, Object>> getTodoList(Map<String, Object> param);

    int getTodoListCnt(Map<String, Object> param);

    Map<String, Object> getTodoDetail(@Param("todoId") int todoId,
                                      @Param("userId") String userId);

    int addTodo(Map<String, Object> param);

    int updateTodo(Map<String, Object> param);

    int updateTodoStatus(Map<String, Object> param);

    int deleteTodo(Map<String, Object> param);
}
