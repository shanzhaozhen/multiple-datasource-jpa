package org.shanzhaozhen.multipledatasourcejpa.entity.view;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VOStaff {

    private Integer id;

    //胸卡
    private String staffCode;

    //姓名
    private String staffName;

    //部门id
    private Integer depId;

    //部门名称
    private String depName;

    //职任
    private String duty;

    //职位（工种）
    private String positionName;

    private String diploma;

}
