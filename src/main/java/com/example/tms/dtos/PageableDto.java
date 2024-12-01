package com.example.tms.dtos;

import com.example.tms.repository.entities.Task;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PageableDto {
    private String filter;
    private int page;
    private int size;
    private List<String> objectList;
    public PageableDto(List<String> objectList)
    {
        this.objectList = objectList;
    }
    public PageableDto(){}

    @Override
    public String toString()
    {
        return String.format("Filter: %s, page: %d, size: %d, Tasks: %s\n", filter, page, size, objectList.toString());
    }

}
