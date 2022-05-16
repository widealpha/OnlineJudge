package cn.sdu.oj.domain.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Problem {
    protected Integer id;
    protected String name;
    protected String description;
    protected String example = "";
    protected Integer difficulty = 3;
    protected Integer isOpen = 0;
    protected String tip;
    protected Integer author;

}
