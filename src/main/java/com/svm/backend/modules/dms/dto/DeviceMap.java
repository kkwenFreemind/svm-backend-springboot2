package com.svm.backend.modules.dms.dto;

import lombok.Data;

/**
 * @author kevinchang
 */
@Data
public class DeviceMap {

    private Long id;
    private String name;
    private Object local;


//    {id: 1, name: "北投活動中心#一號機", local: [25.1255096, 121.488043]},
//    {id: 2, name: "士林活動中心#一號機", local: [25.0893594, 121.5193471]},
//    {id: 3, name: "內湖活動中心#一號機", local: [25.0772151, 121.5735998]},
//    {id: 4, name: "天母公園#一號機", local: [25.1136872, 121.5203246]}

}
