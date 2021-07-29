package tech.yiren.ystart.codegen.entity;

import lombok.Data;
import lombok.ToString;

import java.util.HashMap;
import java.util.List;

@Data
@ToString
public class MainData {
    public String name;
    public List<HashMap> options;
    public String _id;
}
