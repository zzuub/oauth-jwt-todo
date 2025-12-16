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
        //result.put("todoCnt", boardMapper.getTodoListCnt(param));
        return result;
    }

    public Map<String, Object> getTodoDetail(int todoId) {
        Map<String, Object> result = boardMapper.getTodoDetail(todoId);
        //map이 완전히 비어 있는지 확인.
        if (result == null || result.isEmpty()) {
            //throw: 실패 이유 명확(직관성) / 서비스는 예외만 throw
            throw new ApiException(ExceptionCode.TODO_NOT_FOUND);
        }
        return result;
    }

    public Map<String, Object> addTodo(Map<String, Object> param) {
        validateTodoParam(param);

        //쿼리 실행 후 영향을 받은 행 수 반환
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

        int result = boardMapper.updateTodo(param);
        //mapper의 insert,update,delete가 영향을 받은 행 수가 0일때 실패로 간주하여 예외 발생
        //데이터 무결성을 위해
        if (result == 0) {
            throw new ApiException(ExceptionCode.UPDATE_FAILED);
        }
        return param;
    }

    public Map<String, Object> updateTodoStatus(Map<String, Object> param) {
        //컨트롤러에서 map으로 받은 파라미터는 object타입이라 int 사용을 위해(integer로 캐스팅,
        //mybatis가 자동으로 java integer 0/1로 변환
        int todoId = (Integer) param.get("todoId");
        int status = (Integer) param.get("status");

        getTodoOrThrow(todoId);

        Map<String, Object> updateParam = new HashMap<>();
        //새로운 빈 HashMap을 만들고 순차적으로 put해야 하는 상황이기 때문에 map.of X
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

    //TodoId 확인,예외처리
    private Map<String, Object> getTodoOrThrow(int todoId) {
        Map<String, Object> todo = boardMapper.getTodoDetail(todoId);
        if (todo == null || todo.isEmpty()) {
            throw new ApiException(ExceptionCode.TODO_NOT_FOUND);
        }
        return todo;
    }

    //현재는 핵심 필드인 content의 null값과 길이만 검증.
    //title등 다른 필드는 선택사항으로 설계. 추가 검증 가능
    private void validateTodoParam(Map<String, Object> param) {
        String content = (String) param.get("content");
        //.trim()-> 문자열 앞뒤 공백을 제거. UI에서 발생하는 공백 입력 실수 방지
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
