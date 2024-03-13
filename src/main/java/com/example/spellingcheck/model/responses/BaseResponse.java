package com.example.spellingcheck.model.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseResponse {
    private LocalDateTime timestamp;
    private String message;
    private Object data;
    public static BaseResponse of(Object data){
        return new BaseResponse(LocalDateTime.now(), "success", data);
    }
}