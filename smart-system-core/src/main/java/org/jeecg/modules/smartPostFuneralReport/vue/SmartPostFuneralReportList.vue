<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <a-form-item label="工作单位">
              <a-input placeholder="请输入工作单位" v-model="queryParam.departId"></a-input>
            </a-form-item>
          </a-col>
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
      <a-button type="primary" icon="download" @click="handleExportXls('丧事事后报备表')">导出</a-button>
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
        :scroll="{x:true}"
        bordered
        rowKey="id"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
        class="j-table-force-nowrap"
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

    <smart-post-funeral-report-modal ref="modalForm" @ok="modalFormOk"></smart-post-funeral-report-modal>
  </a-card>
</template>

<script>

  import '@/assets/less/TableExpand.less'
  import { mixinDevice } from '@/utils/mixin'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import SmartPostFuneralReportModal from './modules/SmartPostFuneralReportModal'

  export default {
    name: 'SmartPostFuneralReportList',
    mixins:[JeecgListMixin, mixinDevice],
    components: {
      SmartPostFuneralReportModal
    },
    data () {
      return {
        description: '丧事事后报备表管理页面',
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
            title:'报备人ID',
            align:"center",
            dataIndex: 'peopleId'
          },
          {
            title:'工作单位',
            align:"center",
            dataIndex: 'departId'
          },
          {
            title:'逝世人姓名',
            align:"center",
            dataIndex: 'theDeadName'
          },
          {
            title:'与本人关系',
            align:"center",
            dataIndex: 'relation'
          },
          {
            title:'葬礼时间',
            align:"center",
            dataIndex: 'funeralTime',
            customRender:function (text) {
              return !text?"":(text.length>10?text.substr(0,10):text)
            }
          },
          {
            title:'葬礼地点',
            align:"center",
            dataIndex: 'funeralAddress'
          },
          {
            title:'报备时间',
            align:"center",
            dataIndex: 'reportTime'
          },
          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            fixed:"right",
            width:147,
            scopedSlots: { customRender: 'action' }
          }
        ],
        url: {
          list: "/smartPostFuneralReport/smartPostFuneralReport/list",
          delete: "/smartPostFuneralReport/smartPostFuneralReport/delete",
          deleteBatch: "/smartPostFuneralReport/smartPostFuneralReport/deleteBatch",
          exportXlsUrl: "/smartPostFuneralReport/smartPostFuneralReport/exportXls",
          importExcelUrl: "smartPostFuneralReport/smartPostFuneralReport/importExcel",
          
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
      },
    },
    methods: {
      initDictConfig(){
      },
      getSuperFieldList(){
        let fieldList=[];
        fieldList.push({type:'string',value:'peopleId',text:'报备人ID',dictCode:''})
        fieldList.push({type:'string',value:'reportSex',text:'性别',dictCode:''})
        fieldList.push({type:'string',value:'reportAge',text:'年龄',dictCode:''})
        fieldList.push({type:'string',value:'politics',text:'政治面貌',dictCode:''})
        fieldList.push({type:'string',value:'departId',text:'工作单位',dictCode:''})
        fieldList.push({type:'string',value:'zhiwu',text:'职务',dictCode:''})
        fieldList.push({type:'string',value:'zhiji',text:'职级',dictCode:''})
        fieldList.push({type:'string',value:'phone',text:'联系电话',dictCode:''})
        fieldList.push({type:'string',value:'theDeadName',text:'逝世人姓名',dictCode:''})
        fieldList.push({type:'string',value:'relation',text:'与本人关系',dictCode:''})
        fieldList.push({type:'date',value:'funeralTime',text:'葬礼时间'})
        fieldList.push({type:'string',value:'funeralAddress',text:'葬礼地点',dictCode:''})
        fieldList.push({type:'string',value:'peopleAccount',text:'宴请人数',dictCode:''})
        fieldList.push({type:'string',value:'groundName',text:'宴请场所名称',dictCode:''})
        fieldList.push({type:'string',value:'address',text:'宴请场所地址',dictCode:''})
        fieldList.push({type:'string',value:'peopleType',text:'宴请人员范围',dictCode:''})
        fieldList.push({type:'string',value:'carAccount',text:'葬礼用车数量',dictCode:''})
        fieldList.push({type:'string',value:'publicCarAccount',text:'其中公车数量',dictCode:''})
        fieldList.push({type:'string',value:'unusualMoney',text:'不符合规定收受礼金（元）',dictCode:''})
        fieldList.push({type:'string',value:'unusualCount',text:'不符合规定收受礼品件数',dictCode:''})
        fieldList.push({type:'Text',value:'punishContent',text:'违规收受礼金礼品处置情况',dictCode:''})
        fieldList.push({type:'Text',value:'isElse',text:'有否其他违规情况',dictCode:''})
        fieldList.push({type:'Text',value:'elseState',text:'其他需要说明事项',dictCode:''})
        fieldList.push({type:'datetime',value:'reportTime',text:'报备时间'})
        this.superFieldList = fieldList
      }
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>