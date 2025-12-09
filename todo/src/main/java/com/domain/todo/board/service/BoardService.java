package com.domain.todo.board.service;

import com.domain.todo.board.mapper.BoardMapper;
import com.domain.todo.exception.ApiException;
import com.domain.todo.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardMapper boardMapper;

    public Map<String, Object> getTodoList(Map<String,Object> param) {
        Map<String,Object> result = new HashMap<>();
        result.put("data", boardMapper.getTodoList(param));
        result.put("todoCnt", boardMapper.getTodoListCnt(param));
        return result;
    }

    public Map<String, Object> getTodoDetail(int todoId) {
        Map<String, Object> result = boardMapper.getTodoDetail(todoId);
        if (result == null || result.isEmpty()) {
            throw new ApiException(ExceptionCode.TODO_NOT_FOUND);
        }
        return result;
    }

    public Map<String, Object> addTodo(Map<String, Object> param) {
        validateTodoParam(param);
        param.put("registDate", LocalDateTime.now());

        int result = boardMapper.addTodo(param);
        if (result == 0) {
            throw new ApiException(ExceptionCode.UPDATE_FAILED);
        }
        return param;
    }

    public Map<String, Object> updateTodo(Map<String, Object> param) {
        validateTodoParam(param);
        int todoId = (Integer) param.get("todoId");
        getTodoOrThrow(todoId);
        boardMapper.updateTodo(param);
        return param;
    }

    public Map<String, Object> updateTodoStatus(Map<String, Object> param) {
        int todoId = (Integer) param.get("todoId");
        int status = (Integer) param.get("status");

        getTodoOrThrow(todoId);

        Map<String, Object> updateParam = new HashMap<>();
        updateParam.put("todoId", todoId);
        updateParam.put("status", status);

        int result = boardMapper.updateTodoStatus(updateParam);
        if (result == 0) {
            throw new ApiException(ExceptionCode.UPDATE_FAILED);
        }

        return Map.of("todoId", todoId, "status", status);
    }

    public Map<String, Object> deleteTodo(int todoId) {
        getTodoOrThrow(todoId);

        int result = boardMapper.deleteTodo(todoId);
        if (result == 0) {
            throw new ApiException(ExceptionCode.DELETE_FAILED);
        }

        return Map.of("todoId", todoId);
    }

    private Map<String, Object> getTodoOrThrow(int todoId) {
        Map<String, Object> todo = boardMapper.getTodoDetail(todoId);
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

    public int getTotalCount() {
        return boardMapper.getTodoListCnt(Map.of());
    }

    public int getTotalPages(int pageSize) {
        return (int) Math.ceil((double) getTotalCount() / pageSize);
    }

    public int getTodoListCnt(Map<String, Object> param) {
        return boardMapper.getTodoListCnt(param);
    }

}
