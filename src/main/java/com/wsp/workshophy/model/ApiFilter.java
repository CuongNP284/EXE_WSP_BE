package com.wsp.workshophy.model;


import com.wsp.workshophy.constant.Constants;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Log4j2
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ApiFilter<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private T criteria;

    private String search;

    private List<String> sortByMultiple = new ArrayList<>();
    private String sortBy;
    private String sortDirection;

    private Integer page;
    private Integer size;

    public void initialize() {
        if (Objects.isNull(page) || page < 0) {
            page = Constants.DEFAULT_PAGE;
        }
        if (Objects.isNull(size) || size < 0 || size > Constants.DEFAULT_PAGE_SIZE_MAX) {
            size = Constants.DEFAULT_PAGE_SIZE;
        }
        if (StringUtils.isBlank(sortDirection)) {
            sortDirection = Sort.Direction.ASC.name();
        }
        if (StringUtils.isBlank(sortBy)) {
            sortBy = "id";
        }
    }

    public Sort getSort() {
        initialize();
        if (CollectionUtils.isNotEmpty(sortByMultiple)) {
            String[] stockArr = new String[sortByMultiple.size()];
            for (int i = 0; i < sortByMultiple.size(); i++) {
                stockArr[i] = sortByMultiple.get(i);
            }
            return Sort.by(Sort.Direction.ASC.name().equalsIgnoreCase(sortDirection) ? Sort.Direction.ASC : Sort.Direction.DESC, stockArr);
        }
        return Sort.Direction.ASC.name().equalsIgnoreCase(sortDirection)
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
    }

    public Pageable getPageable() {
        return PageRequest.of((page == null || page <= 0) ? Constants.DEFAULT_PAGE : page, (size == null || size <= 0) ? Constants.DEFAULT_PAGE_SIZE : size, getSort());
    }

}
