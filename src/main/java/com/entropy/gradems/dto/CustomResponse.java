package com.entropy.gradems.dto;

import com.entropy.gradems.util.HttpStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomResponse<T> {
    private HttpStatus status;
    private String message;
    private T data;

    public static CustomResponse<Integer> getIntegerCustomResponse(int result) {
        CustomResponse<Integer> response = new CustomResponse<>();
        if (result != 0) {
            response.setStatus(HttpStatus.OK);
            response.setMessage("success");
            response.setData(result);
        } else {
            response.setStatus(HttpStatus.BAD_REQUEST);
            response.setMessage("fail");
            response.setData(result);
        }
        return response;
    }
}
