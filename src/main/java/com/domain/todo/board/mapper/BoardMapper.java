package com.domain.todo.board.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
//인터페이스-> 서로 다른 클래스가 공통 기능을 제공해야할때, 다중 구현이 필요한 경우
public interface BoardMapper {
    List<Map<String, Object>> getTodoList(Map<String, Object> param);

    int getTodoListCnt(Map<String, Object> param);

    Map<String, Object> getTodoDetail(int todoId);

    int addTodo(Map<String, Object> param);

    int updateTodo(Map<String, Object> param);

    int updateTodoStatus(Map<String, Object> param);

    int deleteTodo(int todoId);
}
