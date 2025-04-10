package net.diaowen.common.plugs.es;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsAggregate;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import com.alibaba.fastjson.JSON;
import net.diaowen.common.plugs.page.Page;
import net.diaowen.dwsurvey.common.AggregationResult;
import net.diaowen.dwsurvey.common.AggregationResultItem;
import net.diaowen.dwsurvey.entity.es.answer.DwEsSurveyAnswer;
import net.diaowen.dwsurvey.entity.es.answer.DwEsSurveyAnswerAnOption;
import net.diaowen.dwsurvey.entity.es.answer.extend.EsAnIp;
import net.diaowen.dwsurvey.entity.es.answer.extend.EsAnState;
import net.diaowen.dwsurvey.entity.es.answer.extend.EsAnTime;
import net.diaowen.dwsurvey.entity.es.answer.question.EsAnQuestion;
import net.diaowen.dwsurvey.entity.es.answer.question.option.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 用来在启动时创建数据库索引
 * 如果没有对应的索引则会自动创建，如果创建失败给出提示
 */
@Service
public class DwEsInitService {
    private Logger logger = LoggerFactory.getLogger(getClass());
    //答卷索引名称
    @Resource
    private ESClientService esClientService;
    @Resource ESService esService;

    //创建索引方法
    @PostConstruct
    public void createIndex(){
        try {
            esService.initIndexName();
            String ANSWER_INDEX_NAME = ESService.getAnswerIndexName();
            logger.debug("ANSWER_INDEX_NAME {}", ANSWER_INDEX_NAME);
            if (!esClientService.indexExists(ANSWER_INDEX_NAME)) {
                boolean isSuccess = esClientService.createIndex(ANSWER_INDEX_NAME,"conf/es/dwsurvey-answer-index/dwsurvey_answer_index.json");
                if (isSuccess) {
                    logger.info("ES索引 {} 初始化成功", ANSWER_INDEX_NAME );
                } else {
                    logger.error("ES索引 {} 初始化失败，请手工创建", ANSWER_INDEX_NAME);
                }
            }
            String ANSWER_INDEX_NAME_AGG = ESService.getAnswerIndexNameAgg();
            if (!esClientService.indexExists(ANSWER_INDEX_NAME_AGG)) {
                boolean isSuccess = esClientService.createIndex(ANSWER_INDEX_NAME_AGG,"conf/es/dwsurvey-answer-index-aggs/dwsurvey_answer_index_aggs.json");
                if (isSuccess) {
                    logger.info("ES索引 {} 初始化成功", ANSWER_INDEX_NAME_AGG );
                } else {
                    logger.error("ES索引 {} 初始化失败，请手工创建", ANSWER_INDEX_NAME_AGG);
                }
            }
            String ANSWER_CONTACT_RELATE = ESService.getAnswerContactRelate();
            if (!esClientService.indexExists(ANSWER_CONTACT_RELATE)) {
                boolean isSuccess = esClientService.createIndex(ANSWER_CONTACT_RELATE,"conf/es/dwsurvey-contact-relate-index/dwsurvey_contact_relate_index.json");
                if (isSuccess) {
                    logger.info("ES索引 {} 初始化成功", ANSWER_CONTACT_RELATE );
                } else {
                    logger.error("ES索引 {} 初始化失败，请手工创建", ANSWER_CONTACT_RELATE);
                }
            }
            String ANSWER_CONTACT = ESService.getAnswerContact();
            if (!esClientService.indexExists(ANSWER_CONTACT)) {
                boolean isSuccess = esClientService.createIndex(ANSWER_CONTACT,"conf/es/dwsurvey-contacts-index/dwsurvey_contacts_index.json");
                if (isSuccess) {
                    logger.info("ES索引 {} 初始化成功", ANSWER_CONTACT );
                } else {
                    logger.error("ES索引 {} 初始化失败，请手工创建", ANSWER_CONTACT);
                }
            }
            // 对新增索引字段的初始化
            try {
               boolean putMappingResult = esClientService.putMapping(ANSWER_INDEX_NAME, "conf/es/dwsurvey-answer-index/dwsurvey_answer_index_append250211.json");
               logger.info("append250211.json putMappingResult {} {}", ANSWER_INDEX_NAME, putMappingResult);
            }catch (Exception e){
                e.printStackTrace();
                logger.warn("dwsurvey_answer_index_append250211.json 字段已经存在 {}", e.getMessage());
            }
            try {
                boolean putMappingResult = esClientService.putMapping(ANSWER_INDEX_NAME_AGG, "conf/es/dwsurvey-answer-index-aggs/dwsurvey_answer_index_aggs_append250211.json");
                logger.info("append250211.json putMappingResult {} {}", ANSWER_INDEX_NAME_AGG, putMappingResult);
            }catch (Exception e){
                e.printStackTrace();
                logger.warn("dwsurvey_answer_index_aggs_append250211 字段已经存在 {}", e.getMessage());
            }
            // 对新增索引字段的初始化
            try {
                boolean putMappingResult = esClientService.putMapping(ANSWER_INDEX_NAME, "conf/es/dwsurvey-answer-index/dwsurvey_answer_index_append250215.json");
                logger.info("append250215.json putMappingResult {} {}", ANSWER_INDEX_NAME, putMappingResult);
            }catch (Exception e){
                e.printStackTrace();
                logger.warn("dwsurvey_answer_index_append250215.json 字段已经存在 {}", e.getMessage());
            }
            try {
                boolean putMappingResult = esClientService.putMapping(ANSWER_INDEX_NAME_AGG, "conf/es/dwsurvey-answer-index-aggs/dwsurvey_answer_index_aggs_append250215.json");
                logger.info("append250215.json putMappingResult {} {}", ANSWER_INDEX_NAME_AGG, putMappingResult);
            }catch (Exception e){
                e.printStackTrace();
                logger.warn("dwsurvey_answer_index_aggs_append250215 字段已经存在 {}", e.getMessage());
            }
        } catch (IOException e) {
            logger.error("索引初始化创建异常，请确认ES服务是否安装，同时注意是否安装IK分词插件 {}", e.getMessage());
        }
    }

}
