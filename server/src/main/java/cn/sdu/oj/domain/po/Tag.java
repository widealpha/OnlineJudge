package cn.sdu.oj.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Tag {
    private Integer id;
    private Integer parent;
    private String name;
    private Integer level;
    private boolean hasChild;
}
