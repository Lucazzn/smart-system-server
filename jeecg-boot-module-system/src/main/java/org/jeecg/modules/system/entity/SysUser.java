package org.jeecg.modules.system.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @Author scott
 * @since 2018-12-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 登录账号
     */
    //@Excel(name = "账号", width = 15)
    private String username;

    /**
     * 真实姓名
     */
    @Excel(name = "姓名", width = 15)
    private String realname;

    /**
     * 密码
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    /**
     * md5密码盐
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String salt;

    /**
     * 头像
     */
   /* @Excel(name = "头像", width = 15,type = 2)*/
    private String avatar;

    /**
     * 生日
     */
    @Excel(name = "出生日期", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthday;

    /**
     * 性别（1：男 2：女）
     */
    @Excel(name = "性别", width = 15,dicCode="sex")
    @Dict(dicCode = "sex")
    private Integer sex;

    /**
     * 电子邮件
     */
//    @Excel(name = "电子邮件", width = 15)
    private String email;
    /**
     * 身份证号
     */
    @Excel(name = "身份证号", width = 15)
    @ApiModelProperty(value = "身份证号")
    private java.lang.String idnumber;

    /**
     * 干部人员类别
     */
    @Excel(name = "人员类别", width = 15,dicCode="office_type")
    @ApiModelProperty(value = "人员类别")
    @Dict(dicCode = "office_type")
    private java.lang.String officeType;
    /**
     * 电话
     */
    @TableField(value = "phone",updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "手机号码", width = 15)
    private String phone;

    /**
     * 部门code(当前选择登录部门)
     */
    @Excel(name="单位",width = 15,dictTable ="sys_depart",dicText = "depart_name",dicCode = "org_code")
    @Dict(dictTable ="sys_depart",dicText = "depart_name",dicCode = "org_code")
    private String orgCode;
    //当前登录人单位ID
    @Excel(name = "所属村", width = 15,dictTable ="sys_depart",dicText = "depart_name",dicCode = "id")
    @Dict(dictTable ="sys_depart",dicText = "depart_name",dicCode = "id")
    @ApiModelProperty(value = "所属村")
    private String departId;
    @Excel(name = "所属镇", width = 15,dictTable ="sys_depart",dicText = "depart_name",dicCode = "id")
    @Dict(dictTable ="sys_depart",dicText = "depart_name",dicCode = "id")
    @ApiModelProperty(value = "所属镇")
    private String zhenId;
    /**部门名称--将不需要序列化的属性前添加关键字transient，序列化对象的时候，这个属性就不会被序列化*/
  /*  @Excel(name = "单位", width = 15)*/
    private transient String orgCodeTxt;

    /**
     * 状态(1：正常  2：冻结 ）
     */
//    @Excel(name = "状态", width = 15,dicCode="user_status")
//    @Dict(dicCode = "user_status")
    private Integer status;

    /**
     * 删除状态（0，正常，1已删除）
     */
   /* @Excel(name = "删除状态", width = 15,dicCode="del_flag")*/
    @TableLogic
    private Integer delFlag;

    /**
     * 工号，唯一键
     */
    private String workNo;

    /**
     * 职务，关联职务表
     */
    @Excel(name = "职务", width = 15,dictTable ="sys_position",dicText = "name",dicCode = "code")
    @Dict(dictTable ="sys_position",dicText = "name",dicCode = "code")
    private String post;

    /**
     * 座机号
     */

    private String telephone;

    /**
     * 创建人
     */

    private String createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 同步工作流引擎1同步0不同步
     */
    private Integer activitiSync;

    /**
     * 身份（1普通成员 2上级）
     */
    @Excel(name="是否单位负责人（1否2是）",width = 15)
    private Integer userIdentity;

    /**
     * 负责部门
     */
    //@Excel(name="负责部门",width = 15,dictTable ="sys_depart",dicText = "depart_name",dicCode = "id")
    @Dict(dictTable ="sys_depart",dicText = "depart_name",dicCode = "id")
    private String departIds;

    /**
     * 职级
     */
    @Excel(name = "职级", width = 15,dicCode="position_rank")
    @ApiModelProperty(value = "职级")
    @Dict(dicCode = "position_rank")
    private java.lang.String positionRank;

    /**
     * 民族
     */
    @Excel(name = "民族", width = 15,dicCode="ethnicity")
    @ApiModelProperty(value = "民族")
    @Dict(dicCode = "ethnicity")
    private java.lang.String ethnicity;

    /**
     * 政治面貌
     */
    @Excel(name = "政治面貌", width = 15,dicCode="political_status")
    @ApiModelProperty(value = "政治面貌")
    @Dict(dicCode = "political_status")
    private java.lang.String politicalStatus;

    /**
     * 入党日期
     */
    @Excel(name = "入党日期", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date joinPartyDate;

    /**
     * 人员类别
     */
    @ApiModelProperty(value = "人员类别")
    @Dict(dicCode = "people_type")
    private java.lang.String peopleType;

    /**
     * 多租户id配置，编辑用户的时候设置
     */
    private String relTenantIds;

    /**设备id uniapp推送用*/
    private String clientId;

    private List<String> roleId;

    /**角色*/
    //@Excel(name = "角色", width = 15)
    @ApiModelProperty(value = "角色")
    private String role;


    /**
     * 上次验证手机号码时间
     */
    private Date lastVerifyTime;

    /**户籍编号*/
    @Excel(name = "户籍编号", width = 15)
    @ApiModelProperty(value = "户籍编号")
    private String homeCode;

    @Excel(name = "是否是户主", width = 15,dicCode = "home_role")
    @ApiModelProperty(value = "是否是户主")
    @Dict(dicCode = "home_role")
    private Integer homeRole;

    private Integer relation;

}
