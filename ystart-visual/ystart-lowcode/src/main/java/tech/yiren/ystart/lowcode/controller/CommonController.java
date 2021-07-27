package tech.yiren.ystart.lowcode.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tech.yiren.ystart.common.core.util.R;
import tech.yiren.ystart.common.log.annotation.SysLog;
import tech.yiren.ystart.common.oss.service.OssTemplate;
import tech.yiren.ystart.lowcode.dto.FilterDto;
import tech.yiren.ystart.lowcode.dto.PageDto;
import tech.yiren.ystart.lowcode.service.CommonService;


import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * 通用数据
 *
 * @author pig code generator
 * @date 2021-06-10 15:52:35
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/common")
@Api(value = "common", tags = "通用数据管理")
public class CommonController {

    private final CommonService commonService;


    @Value("${oss.bucketName}")
    private String bucketName;

    @Value("${oss.fileDir}")
    private String fileDir;


    @Value("${oss.url}")
    private String ossUrl;



    @Autowired
    private OssTemplate template;

    @ApiOperation(value = "分页查询", notes = "分页查询")
    @GetMapping("/{model}/page")
//    @PreAuthorize("@pms.hasPermission('common_common_get')" )
    public R getDemoPage(@PathVariable("model") String model, Page page, FilterDto common) {
        System.out.println(page);
        PageDto pageDto = new PageDto();
        pageDto.setPageNo(page.getCurrent());
        pageDto.setPageSize((int) page.getSize());
        if(null != common){
            pageDto.setFilter(common.getFilter());
        }
        pageDto.setPageSize((int) page.getSize());
        return R.ok(commonService.listByPage(model, pageDto));
    }


    /**
     * 得到列表
     *
     * @param filterDto 过滤器dto
     * @return {@link R}
     */
    @ApiOperation(value = "列表查询", notes = "列表查询")
    @GetMapping("/{model}//list")
//    @PreAuthorize("@pms.hasPermission('object_object_list')")
    public R getList(@PathVariable("model") String model, FilterDto filterDto) {
        return R.ok(commonService.list(model,filterDto));
    }


    @ApiOperation(value = "通过id查询", notes = "通过id查询")
    @GetMapping("/{model}/getById/{id}")
//    @PreAuthorize("@pms.hasPermission('common_common_get')" )
    public R getById(@PathVariable("model") String model, @PathVariable("id") String id) {
        return R.ok(commonService.getById(model, id));
    }


    @ApiOperation(value = "新增通用数据", notes = "新增通用数据")
    @SysLog("新增通用数据")
    @PostMapping("/{model}/save")
//    @PreAuthorize("@pms.hasPermission('common_common_add')" )
    public R insert(@PathVariable("model") String model, @RequestBody Object object) {
        return R.ok(commonService.insert(model, object));
    }

    @ApiOperation(value = "修改通用数据", notes = "修改通用数据")
    @SysLog("修改通用数据")
    @PutMapping("/{model}/update")
//    @PreAuthorize("@pms.hasPermission('common_common_add')" )
    public R update(@PathVariable("model") String model, @RequestBody Object object) {
        return R.ok(commonService.update(model, object));
    }

    @ApiOperation(value = "修改通用数据", notes = "修改通用数据")
    @SysLog("修改通用数据")
    @PutMapping("/{model}/updateById/{id}")
//    @PreAuthorize("@pms.hasPermission('common_common_edit')" )
    public R updateById(@PathVariable("model") String model, @PathVariable String id, @RequestBody Object object) {
        return R.ok(commonService.updateById(model, id, object));
    }

    @ApiOperation(value = "通过id删除通用数据", notes = "通过id删除通用数据")
    @SysLog("通过id删除通用数据")
    @DeleteMapping("/{model}/deleteById/{id}")
//    @PreAuthorize("@pms.hasPermission('common_common_del')" )
    public R deleteById(@PathVariable("model") String model, @PathVariable String id) {
        return R.ok(commonService.deleteById(model, id));
    }



    /**
     * 上传文件
     * 文件名采用uuid,避免原始文件名中带"-"符号导致下载的时候解析出现异常
     *
     * @param file 资源
     * @return R(bucketName, filename)
     */
    @PostMapping("/upload")
    public R upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        try {
            String ossFileName = "";
            StringBuilder fileUrl = new StringBuilder();
            if (file.getOriginalFilename().lastIndexOf(".") == -1) {
                ossFileName = System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 18);
            } else {
                String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
                ossFileName = System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 18) + "." + suffix;
            }
            if (!fileDir.endsWith("/")) {
                fileDir = fileDir.concat("/");
            }

            fileUrl = fileUrl.append(fileDir + ossFileName);
            template.putObject(bucketName, fileUrl.toString(), file.getInputStream());

            fileUrl = fileUrl.insert(0, ossUrl);

            Map<String, Object> ossRet = new HashMap<>(16);
            ossRet.put("url",fileUrl.toString());
            ossRet.put("name",file.getOriginalFilename());
            ossRet.put("ossFileName",ossFileName);
            return R.ok(ossRet);
        } catch (Exception e) {
            e.printStackTrace();
            return R.ok();
        }
    }

}
