<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="单位">
              <a-input placeholder="请输入单位" v-model="queryParam.departId"></a-input>
            </a-form-item>
          </a-col>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="会议名称">
              <a-input placeholder="请输入会议名称" v-model="queryParam.meetingName"></a-input>
            </a-form-item>
          </a-col>
          <template v-if="toggleSearchStatus">
            <a-col :xl="10" :lg="11" :md="12" :sm="24">
              <a-form-item label="会议时间">
                <j-date :show-time="true" date-format="YYYY-MM-DD HH:mm:ss" placeholder="请选择开始时间" class="query-group-cust" v-model="queryParam.meetingTime_begin"></j-date>
                <span class="query-group-split-cust"></span>
                <j-date :show-time="true" date-format="YYYY-MM-DD HH:mm:ss" placeholder="请选择结束时间" class="query-group-cust" v-model="queryParam.meetingTime_end"></j-date>
              </a-form-item>
            </a-col>
          </template>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
              <a @click="handleToggleSearch" style="margin-left: 8px">
                {{ toggleSearchStatus ? '收起' : '展开' }}
                <a-icon :type="toggleSearchStatus ? 'up' : 'down'"/>
              </a>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <!-- 查询区域-END -->
    
    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <a-button @click="handleAdd" type="primary" icon="plus">新增</a-button>
      <a-button type="primary" icon="download" @click="handleExportXls('民主生活会表')">导出</a-button>
      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">
        <a-button type="primary" icon="import">导入</a-button>
      </a-upload>
      <!-- 高级查询区域 -->
      <j-super-query :fieldList="superFieldList" ref="superQueryModal" @handleSuperQuery="handleSuperQuery"></j-super-query>
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay">
          <a-menu-item key="1" @click="batchDel"><a-icon type="delete"/>删除</a-menu-item>
        </a-menu>
        <a-button style="margin-left: 8px"> 批量操作 <a-icon type="down" /></a-button>
      </a-dropdown>
    </div>

    <!-- table区域-begin -->
    <div>
      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
        <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a style="font-weight: 600">{{ selectedRowKeys.length }}</a>项
        <a style="margin-left: 24px" @click="onClearSelected">清空</a>
      </div>

      <a-table
        ref="table"
        size="middle"
        bordered
        rowKey="id"
        class="j-table-force-nowrap"
        :scroll="{x:true}"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
        @change="handleTableChange">

        <template slot="htmlSlot" slot-scope="text">
          <div v-html="text"></div>
        </template>
        <template slot="imgSlot" slot-scope="text">
          <span v-if="!text" style="font-size: 12px;font-style: italic;">无图片</span>
          <img v-else :src="getImgView(text)" height="25px" alt="" style="max-width:80px;font-size: 12px;font-style: italic;"/>
        </template>
        <template slot="fileSlot" slot-scope="text">
          <span v-if="!text" style="font-size: 12px;font-style: italic;">无文件</span>
          <a-button
            v-else
            :ghost="true"
            type="primary"
            icon="download"
            size="small"
            @click="downloadFile(text)">
            下载
          </a-button>
        </template>

        <span slot="action" slot-scope="text, record">
          <a @click="handleEdit(record)">编辑</a>

          <a-divider type="vertical" />
          <a-dropdown>
            <a class="ant-dropdown-link">更多 <a-icon type="down" /></a>
            <a-menu slot="overlay">
              <a-menu-item>
                <a @click="handleDetail(record)">详情</a>
              </a-menu-item>
              <a-menu-item>
                <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
                  <a>删除</a>
                </a-popconfirm>
              </a-menu-item>
            </a-menu>
          </a-dropdown>
        </span>

      </a-table>
    </div>

    <smart-democratic-life-meeting-modal ref="modalForm" @ok="modalFormOk"/>
  </a-card>
</template>

<script>

  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import SmartDemocraticLifeMeetingModal from './modules/SmartDemocraticLifeMeetingModal'
  import {filterMultiDictText} from '@/components/dict/JDictSelectUtil'
  import '@/assets/less/TableExpand.less'

  export default {
    name: "SmartDemocraticLifeMeetingList",
    mixins:[JeecgListMixin],
    components: {
      SmartDemocraticLifeMeetingModal
    },
    data () {
      return {
        description: '民主生活会表管理页面',
        // 表头
        columns: [
          {
            title: '#',
            dataIndex: '',
            key:'rowIndex',
            width:60,
            align:"center",
            customRender:function (t,r,index) {
              return parseInt(index)+1;
            }
          },
          {
            title:'单位',
            align:"center",
            dataIndex: 'departId'
          },
          {
            title:'会议名称',
            align:"center",
            dataIndex: 'meetingName'
          },
          {
            title:'会议地点',
            align:"center",
            dataIndex: 'address'
          },
          {
            title:'会议时间',
            align:"center",
            dataIndex: 'meetingTime'
          },
          {
            title:'上报时间',
            align:"center",
            dataIndex: 'reportingTime'
          },
          {
            title:'主持人姓名',
            align:"center",
            dataIndex: 'hostName'
          },
          {
            title:'会议记录人姓名',
            align:"center",
            dataIndex: 'recorderName'
          },
          {
            title:'会议内容摘要',
            align:"center",
            dataIndex: 'summary'
          },
          {
            title:'会议记录',
            align:"center",
            dataIndex: 'record'
          },
          {
            title:'创建人',
            align:"center",
            dataIndex: 'createBy'
          },
          {
            title:'创建时间',
            align:"center",
            dataIndex: 'createTime'
          },
          {
            title:'审核状态',
            align:"center",
            dataIndex: 'verifyStatus'
          },
          {
            title:'附件说明',
            align:"center",
            dataIndex: 'explanation'
          },
          {
            title:'附件文件',
            align:"center",
            dataIndex: 'path',
            scopedSlots: {customRender: 'fileSlot'}
          },
          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            fixed:"right",
            width:147,
            scopedSlots: { customRender: 'action' },
          }
        ],
        url: {
          list: "/smartDemocraticLifeMeeting/smartDemocraticLifeMeeting/list",
          delete: "/smartDemocraticLifeMeeting/smartDemocraticLifeMeeting/delete",
          deleteBatch: "/smartDemocraticLifeMeeting/smartDemocraticLifeMeeting/deleteBatch",
          exportXlsUrl: "/smartDemocraticLifeMeeting/smartDemocraticLifeMeeting/exportXls",
          importExcelUrl: "smartDemocraticLifeMeeting/smartDemocraticLifeMeeting/importExcel",
          
        },
        dictOptions:{},
        superFieldList:[],
      }
    },
    created() {
      this.getSuperFieldList();
    },
    computed: {
      importExcelUrl: function(){
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
      }
    },
    methods: {
      initDictConfig(){
      },
      getSuperFieldList(){
        let fieldList=[];
        fieldList.push({type:'string',value:'departId',text:'单位',dictCode:'sys_depart,depart_name,id'})
        fieldList.push({type:'string',value:'meetingName',text:'会议名称',dictCode:''})
        fieldList.push({type:'string',value:'address',text:'会议地点',dictCode:''})
        fieldList.push({type:'datetime',value:'meetingTime',text:'会议时间'})
        fieldList.push({type:'datetime',value:'reportingTime',text:'上报时间'})
        fieldList.push({type:'string',value:'hostName',text:'主持人姓名',dictCode:''})
        fieldList.push({type:'string',value:'recorderName',text:'会议记录人姓名',dictCode:''})
        fieldList.push({type:'Text',value:'summary',text:'会议内容摘要',dictCode:''})
        fieldList.push({type:'Text',value:'record',text:'会议记录',dictCode:''})
        fieldList.push({type:'string',value:'createBy',text:'创建人',dictCode:''})
        fieldList.push({type:'datetime',value:'createTime',text:'创建时间'})
        fieldList.push({type:'string',value:'verifyStatus',text:'审核状态',dictCode:''})
        fieldList.push({type:'Text',value:'explanation',text:'附件说明',dictCode:''})
        fieldList.push({type:'Text',value:'path',text:'附件文件',dictCode:''})
        this.superFieldList = fieldList
      }
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>