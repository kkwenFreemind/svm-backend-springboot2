package com.svm.backend.modules.ums.dto;


import com.svm.backend.modules.ums.model.UmsMenu;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 後台菜單節點封裝
 *
 * @author : kevin Chang
 */
@Getter
@Setter
public class UmsMenuNode extends UmsMenu {

    private List<UmsMenuNode> children;
}
