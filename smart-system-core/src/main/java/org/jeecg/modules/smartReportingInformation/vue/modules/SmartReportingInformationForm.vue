<template>
  <a-spin :spinning="confirmLoading">
    <j-form-container :disabled="formDisabled">
      <!-- 主表单区域 -->
      <a-form-model ref="form" :model="model" :rules="validatorRules" slot="detail">
        <a-row>
          <a-col :span="24" >
            <a-form-model-item label="被反映人信息" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="reflectedInformation">
              <a-input v-model="model.reflectedInformation" placeholder="请输入被反映人信息" ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24" >
            <a-form-model-item label="被反映人单位" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="reflectedDocumentid">
              <a-input v-model="model.reflectedDocumentid" placeholder="请输入被反映人单位" ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24" >
            <a-form-model-item label="主要问题" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="majorProblem">
              <a-input v-model="model.majorProblem" placeholder="请输入主要问题" ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24" >
            <a-form-model-item label="照片" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="photoString">
              <j-image-upload isMultiple  v-model="model.photo" ></j-image-upload>
            </a-form-model-item>
          </a-col>
          <a-col :span="24" >
            <a-form-model-item label="附件" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="description">
              <j-upload v-model="model.description"  ></j-upload>
            </a-form-model-item>
          </a-col>
          <a-col :span="24" >
            <a-form-model-item label="举报时间" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="reportingTime">
              <j-date placeholder="请选择举报时间" v-model="model.reportingTime" :show-time="true" date-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24" >
            <a-form-model-item label="处理状态" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="processingResult">
              <j-dict-select-tag type="list" v-model="model.processingResult" dictCode="processing_result" placeholder="请选择处理状态" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24" >
            <a-form-model-item label="举报人姓名" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="reportingName">
              <a-input v-model="model.reportingName" placeholder="请输入举报人姓名" ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24" >
            <a-form-model-item label="联系电话" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="contactNumber">
              <a-input v-model="model.contactNumber" placeholder="请输入联系电话" ></a-input>
            </a-form-model-item>
          </a-col>
        </a-row>
      </a-form-model>
    </j-form-container>
      <!-- 子表单区域 -->
    <a-tabs v-model="activeKey" @change="handleChangeTabs">
      <a-tab-pane tab="举报调查表" :key="refKeys[0]" :forceRender="true">
        <j-editable-table
          :ref="refKeys[0]"
          :loading="smartReportingSurveyTable.loading"
          :columns="smartReportingSurveyTable.columns"
          :dataSource="smartReportingSurveyTable.dataSource"
          :maxHeight="300"
          :disabled="formDisabled"
          :rowNumber="true"
          :rowSelection="true"
          :actionButton="true"/>
      </a-tab-pane>
      <a-tab-pane tab="举报附件表" :key="refKeys[1]" :forceRender="true">
        <j-editable-table
          :ref="refKeys[1]"
          :loading="smartReportingDescriptionTable.loading"
          :columns="smartReportingDescriptionTable.columns"
          :dataSource="smartReportingDescriptionTable.dataSource"
          :maxHeight="300"
          :disabled="formDisabled"
          :rowNumber="true"
          :rowSelection="true"
          :actionButton="true"/>
      </a-tab-pane>
    </a-tabs>
  </a-spin>
</template>

<script>

  import { getAction } from '@/api/manage'
  import { FormTypes,getRefPromise,VALIDATE_NO_PASSED } from '@/utils/JEditableTableUtil'
  import { JEditableTableModelMixin } from '@/mixins/JEditableTableModelMixin'
  import { validateDuplicateValue } from '@/utils/util'

  export default {
    name: 'SmartReportingInformationForm',
    mixins: [JEditableTableModelMixin],
    components: {
    },
    data() {
      return {
        labelCol: {
          xs: { span: 24 },
          sm: { span: 6 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },
        labelCol2: {
          xs: { span: 24 },
          sm: { span: 3 },
        },
        wrapperCol2: {
          xs: { span: 24 },
          sm: { span: 20 },
        },
        model:{
        },
        // 新增时子表默认添加几行空数据
        addDefaultRowNum: 1,
        validatorRules: {
           reportingTime: [
              { required: true, message: '请输入举报时间!'},
           ],
           processingResult: [
              { required: true, message: '请输入处理状态!'},
           ],
        },
        refKeys: ['smartReportingSurvey', 'smartReportingDescription', ],
        tableKeys:['smartReportingSurvey', 'smartReportingDescription', ],
        activeKey: 'smartReportingSurvey',
        // 举报调查表
        smartReportingSurveyTable: {
          loading: false,
          dataSource: [],
          columns: [
            {
              title: '调查附件名称',
              key: 'surveyDescription',
              type: FormTypes.input,
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue:'',
            },
            {
              title: '附件文件存放路径',
              key: 'descriptionPath',
              type: FormTypes.file,
              token:true,
              responseName:"message",
              width:"200px",
              placeholder: '请选择文件',
              defaultValue:'',
            },
          ]
        },
        // 举报附件表
        smartReportingDescriptionTable: {
          loading: false,
          dataSource: [],
          columns: [
            {
              title: '举报附件名称',
              key: 'reportingDescription',
              type: FormTypes.input,
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue:'',
            },
            {
              title: '附件文件存放路径',
              key: 'descriptionPath',
              type: FormTypes.file,
              token:true,
              responseName:"message",
              width:"200px",
              placeholder: '请选择文件',
              defaultValue:'',
            },
          ]
        },
        url: {
          add: "/smartreportingInformation/smartReportingInformation/add",
          edit: "/smartreportingInformation/smartReportingInformation/edit",
          queryById: "/smartreportingInformation/smartReportingInformation/queryById",
          smartReportingSurvey: {
            list: '/smartreportingInformation/smartReportingInformation/querySmartReportingSurveyByMainId'
          },
          smartReportingDescription: {
            list: '/smartreportingInformation/smartReportingInformation/querySmartReportingDescriptionByMainId'
          },
        }
      }
    },
    props: {
      //表单禁用
      disabled: {
        type: Boolean,
        default: false,
        required: false
      }
    },
    computed: {
      formDisabled(){
        return this.disabled
      },
    },
    created () {
    },
    methods: {
      addBefore(){
        this.smartReportingSurveyTable.dataSource=[]
        this.smartReportingDescriptionTable.dataSource=[]
      },
      getAllTable() {
        let values = this.tableKeys.map(key => getRefPromise(this, key))
        return Promise.all(values)
      },
      /** 调用完edit()方法之后会自动调用此方法 */
      editAfter() {
        this.$nextTick(() => {
        })
        // 加载子表数据
        if (this.model.id) {
          let params = { id: this.model.id }
          this.requestSubTableData(this.url.smartReportingSurvey.list, params, this.smartReportingSurveyTable)
          this.requestSubTableData(this.url.smartReportingDescription.list, params, this.smartReportingDescriptionTable)
        }
      },
      //校验所有一对一子表表单
      validateSubForm(allValues){
          return new Promise((resolve,reject)=>{
            Promise.all([
            ]).then(() => {
              resolve(allValues)
            }).catch(e => {
              if (e.error === VALIDATE_NO_PASSED) {
                // 如果有未通过表单验证的子表，就自动跳转到它所在的tab
                this.activeKey = e.index == null ? this.activeKey : this.refKeys[e.index]
              } else {
                console.error(e)
              }
            })
          })
      },
      /** 整理成formData */
      classifyIntoFormData(allValues) {
        let main = Object.assign(this.model, allValues.formValue)
        return {
          ...main, // 展开
          smartReportingSurveyList: allValues.tablesValue[0].values,
          smartReportingDescriptionList: allValues.tablesValue[1].values,
        }
      },
      validateError(msg){
        this.$message.error(msg)
      },

    }
  }
</script>

<style scoped>
</style>