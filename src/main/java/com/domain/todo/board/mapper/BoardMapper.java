package com.domain.todo.board.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface BoardMapper {
    List<Map<String, Object>> getTodoList(Map<String, Object> param);

    int getTodoListCnt(Map<String, Object> param);

    Map<String, Object> getTodoDetail(int todoId, String userId);

    int addTodo(Map<String, Object> param);

    int updateTodo(Map<String, Object> param);

    int updateTodoStatus(Map<String, Object> param);

    int deleteTodo(int todoId, String userId);
}
