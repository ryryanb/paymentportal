package com.moadbus.web.biller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccreditedBillerUser {

    private Long id;
    private String userName;
    private String billerShortName;

}
