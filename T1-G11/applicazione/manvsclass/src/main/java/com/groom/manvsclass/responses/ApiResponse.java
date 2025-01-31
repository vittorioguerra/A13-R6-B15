package com.groom.manvsclass.responses;

import java.util.ArrayList;
import java.util.List;

public class ApiResponse extends Response {

    private List<String> data;

    public ApiResponse() {
        this.data = new ArrayList<>();
    }

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }

    public void setData(String data) {
        this.data.add(data);
    }
}