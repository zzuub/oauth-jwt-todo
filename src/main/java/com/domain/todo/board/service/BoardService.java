package com.domain.todo.board.service;

import com.domain.todo.board.mapper.BoardMapper;
import com.domain.todo.exception.ApiException;
import com.domain.todo.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardMapper boardMapper;

    public Map<String, Object> getTodoList(Map<String,Object> param) {
        Map<String,Object> result = new HashMap<>();
        result.put("data", boardMapper.getTodoList(param));
        //result.put("todoCnt", boardMapper.getTodoListCnt(param));
        return result;
    }

    public Map<String, Object> getTodoDetail(int todoId, String userId) {
        Map<String, Object> result = boardMapper.getTodoDetail(todoId, userId);
        if (result == null || result.isEmpty()) {
            throw new ApiException(ExceptionCode.TODO_NOT_FOUND);
        }
        return result;
    }

    public Map<String, Object> addTodo(Map<String, Object> param) {
        validateTodoParam(param);

        String userId = (String) param.get("userId");
        if (userId == null || userId.trim().isEmpty()) {
            throw new ApiException(ExceptionCode.AUTH_REQUIRED);
        }

        int result = boardMapper.addTodo(param);
        if (result == 0) {
            throw new ApiException(ExceptionCode.UPDATE_FAILED);
        }
        return param;
    }

    public Map<String, Object> updateTodo(Map<String, Object> param) {
        validateTodoParam(param);
        int todoId = (Integer) param.get("todoId");
        String userId = (String) param.get("userId");
        getTodoOrThrow(todoId,userId);

        int result = boardMapper.updateTodo(param);
        if (result == 0) {
            throw new ApiException(ExceptionCode.UPDATE_FAILED);
        }
        return param;
    }

    public Map<String, Object> updateTodoStatus(Map<String, Object> param) {
        int todoId = (Integer) param.get("todoId");
        int completed_yn = (Integer) param.get("completed_yn");
        String userId = (String) param.get("userId");

        getTodoOrThrow(todoId, userId);

        Map<String, Object> updateParam = new HashMap<>();
        updateParam.put("todoId", todoId);
        updateParam.put("completed_yn", completed_yn);
        updateParam.put("userId", userId);

        int result = boardMapper.updateTodoStatus(updateParam);
        if (result == 0) {
            throw new ApiException(ExceptionCode.UPDATE_FAILED);
        }

        return Map.of("todoId", todoId, "completed_yn", completed_yn);
    }

    public Map<String, Object> deleteTodo(int todoId, String userId) {
        getTodoOrThrow(todoId, userId);

        int result = boardMapper.deleteTodo(todoId, userId);
        if (result == 0) {
            throw new ApiException(ExceptionCode.DELETE_FAILED);
        }

        return Map.of("todoId", todoId);
    }

    //TodoId 확인,예외처리
    private Map<String, Object> getTodoOrThrow(int todoId, String userId) {
        Map<String, Object> todo = boardMapper.getTodoDetail(todoId, userId);
        if (todo == null || todo.isEmpty()) {
            throw new ApiException(ExceptionCode.TODO_NOT_FOUND);
        }
        return todo;
    }

    private void validateTodoParam(Map<String, Object> param) {
        String content = (String) param.get("content");
        if (content == null || content.trim().isEmpty()) {
            throw new ApiException(ExceptionCode.INVALID_CONTENT);
        }
        if (content.length() > 1000) {
            throw new ApiException(ExceptionCode.CONTENT_TOO_LONG);
        }
    }

    public int getTodoListCnt(Map<String, Object> param) {
        return boardMapper.getTodoListCnt(param);
    }

}
