package com.dismai.service.es;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dismai.core.SpringUtil;
import com.dismai.dto.EsDataQueryDto;
import com.dismai.dto.ProgramListDto;
import com.dismai.dto.ProgramPageListDto;
import com.dismai.dto.ProgramRecommendListDto;
import com.dismai.dto.ProgramSearchDto;
import com.dismai.enums.BusinessStatus;
import com.dismai.page.PageUtil;
import com.dismai.page.PageVo;
import com.dismai.service.init.ProgramDocumentParamName;
import com.dismai.service.tool.ProgramPageOrder;
import com.dismai.util.BusinessEsHandle;
import com.dismai.util.StringUtil;
import com.dismai.vo.ProgramHomeVo;
import com.dismai.vo.ProgramListVo;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.ScriptSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.io.IOException;

import com.dismai.entity.TicketCategoryAggregate;
import com.dismai.service.ProgramService;
import com.dismai.vo.ProgramVo;

@Slf4j
@Component
public class ProgramEs {
    
    @Autowired
    private BusinessEsHandle businessEsHandle;

    @Lazy
    @Autowired
    private ProgramService programService;

    public List<ProgramHomeVo> selectHomeList(ProgramListDto programListDto) {
        List<ProgramHomeVo> programHomeVoList = new ArrayList<>();

        try {
            String indexName = SpringUtil.getPrefixDistinctionName() + "-" + 
                    ProgramDocumentParamName.INDEX_NAME;

            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
            boolQuery.must(QueryBuilders.termQuery(
                    ProgramDocumentParamName.PROGRAM_STATUS, BusinessStatus.YES.getCode()));
            if (Objects.nonNull(programListDto.getAreaId())) {
                boolQuery.must(QueryBuilders.termQuery(
                        ProgramDocumentParamName.AREA_ID, programListDto.getAreaId()));
            } else {
                boolQuery.must(QueryBuilders.termQuery(
                        ProgramDocumentParamName.PRIME, BusinessStatus.YES.getCode()));
            }

            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.query(boolQuery);
            sourceBuilder.size(0);
            sourceBuilder.trackTotalHits(false);

            // terms aggregation on parentProgramCategoryId with top_hits sub-aggregation
            sourceBuilder.aggregation(AggregationBuilders
                    .terms("by_category")
                    .field(ProgramDocumentParamName.PARENT_PROGRAM_CATEGORY_ID)
                    .size(programListDto.getParentProgramCategoryIds().size())
                    .subAggregation(AggregationBuilders
                            .topHits("top_programs")
                            .size(7)
                            .sort(ProgramDocumentParamName.HIGH_HEAT, SortOrder.DESC)));

            JSONObject result = businessEsHandle.executeQueryRaw(indexName, sourceBuilder);
            JSONObject aggs = result.getJSONObject("aggregations");
            if (aggs == null) return programHomeVoList;

            JSONArray buckets = aggs.getJSONObject("by_category").getJSONArray("buckets");
            if (buckets == null || buckets.isEmpty()) return programHomeVoList;

            for (int i = 0; i < buckets.size(); i++) {
                JSONObject bucket = buckets.getJSONObject(i);
                JSONObject topHits = bucket.getJSONObject("top_programs");
                if (topHits == null) continue;

                JSONObject hitsObj = topHits.getJSONObject("hits");
                if (hitsObj == null) continue;

                JSONArray hitsArr = hitsObj.getJSONArray("hits");
                if (hitsArr == null || hitsArr.isEmpty()) continue;

                List<ProgramListVo> voList = new ArrayList<>();
                for (int j = 0; j < hitsArr.size(); j++) {
                    JSONObject hit = hitsArr.getJSONObject(j);
                    JSONObject source = hit.getJSONObject("_source");
                    if (source != null) {
                        ProgramListVo vo = source.toJavaObject(ProgramListVo.class);
                        voList.add(vo);
                    }
                }
                if (!voList.isEmpty()) {
                    ProgramHomeVo homeVo = new ProgramHomeVo();
                    homeVo.setCategoryName(voList.get(0).getParentProgramCategoryName());
                    homeVo.setCategoryId(voList.get(0).getParentProgramCategoryId());
                    homeVo.setProgramListVoList(voList);
                    programHomeVoList.add(homeVo);
                }
            }
        } catch (Exception e) {
            log.error("selectHomeList error", e);
        }
        return programHomeVoList;
    }
    
    public List<ProgramListVo> recommendList(ProgramRecommendListDto programRecommendListDto) {
        List<ProgramListVo> programListVoList = new ArrayList<>();
        try {
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
            boolQuery.must(QueryBuilders.termQuery(
                    ProgramDocumentParamName.PROGRAM_STATUS, BusinessStatus.YES.getCode()));
            if (Objects.nonNull(programRecommendListDto.getAreaId())) {
                QueryBuilder builds = QueryBuilders.termQuery(ProgramDocumentParamName.AREA_ID, 
                        programRecommendListDto.getAreaId());
                boolQuery.must(builds);
            }
            if (Objects.nonNull(programRecommendListDto.getParentProgramCategoryId())) {
                QueryBuilder builds = QueryBuilders.termQuery(ProgramDocumentParamName.PARENT_PROGRAM_CATEGORY_ID, 
                        programRecommendListDto.getParentProgramCategoryId());
                boolQuery.must(builds);
            }
            if (Objects.nonNull(programRecommendListDto.getProgramId())) {
                QueryBuilder builds = QueryBuilders.termQuery(ProgramDocumentParamName.ID,
                        programRecommendListDto.getProgramId());
                boolQuery.mustNot(builds);
            }
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(boolQuery);
            searchSourceBuilder.trackTotalHits(true);
            searchSourceBuilder.from(1);
            searchSourceBuilder.size(10);
           
            Script script = new Script("Math.random()");
            ScriptSortBuilder scriptSortBuilder = new ScriptSortBuilder(script, ScriptSortBuilder.ScriptSortType.NUMBER);
            scriptSortBuilder.order(SortOrder.ASC);
            
            searchSourceBuilder.sort(scriptSortBuilder);
            
            businessEsHandle.executeQuery(
                    SpringUtil.getPrefixDistinctionName() + "-" + ProgramDocumentParamName.INDEX_NAME,
                    ProgramDocumentParamName.INDEX_TYPE,programListVoList,null,ProgramListVo.class,
                    searchSourceBuilder,null);
        }catch (Exception e) {
            log.error("recommendList error",e);
        }
        return programListVoList;
    }
    
    
    public PageVo<ProgramListVo> selectPage(ProgramPageListDto programPageListDto) {
        PageVo<ProgramListVo> pageVo = new PageVo<>();
        try {
            List<EsDataQueryDto> esDataQueryDtoList = new ArrayList<>();
            EsDataQueryDto statusDto = new EsDataQueryDto();
            statusDto.setParamName(ProgramDocumentParamName.PROGRAM_STATUS);
            statusDto.setParamValue(BusinessStatus.YES.getCode());
            esDataQueryDtoList.add(statusDto);
            if (Objects.nonNull(programPageListDto.getAreaId())) {
                EsDataQueryDto areaIdQueryDto = new EsDataQueryDto();
                areaIdQueryDto.setParamName(ProgramDocumentParamName.AREA_ID);
                areaIdQueryDto.setParamValue(programPageListDto.getAreaId());
                esDataQueryDtoList.add(areaIdQueryDto);
            }else {
                EsDataQueryDto primeQueryDto = new EsDataQueryDto();
                primeQueryDto.setParamName(ProgramDocumentParamName.PRIME);
                primeQueryDto.setParamValue(BusinessStatus.YES.getCode());
                esDataQueryDtoList.add(primeQueryDto);
            }
            if (Objects.nonNull(programPageListDto.getParentProgramCategoryId())) {
                EsDataQueryDto parentProgramCategoryIdQueryDto = new EsDataQueryDto();
                parentProgramCategoryIdQueryDto.setParamName(ProgramDocumentParamName.PARENT_PROGRAM_CATEGORY_ID);
                parentProgramCategoryIdQueryDto.setParamValue(programPageListDto.getParentProgramCategoryId());
                esDataQueryDtoList.add(parentProgramCategoryIdQueryDto);
            }
            if (Objects.nonNull(programPageListDto.getProgramCategoryId())) {
                EsDataQueryDto programCategoryIdQueryDto = new EsDataQueryDto();
                programCategoryIdQueryDto.setParamName(ProgramDocumentParamName.PROGRAM_CATEGORY_ID);
                programCategoryIdQueryDto.setParamValue(programPageListDto.getProgramCategoryId());
                esDataQueryDtoList.add(programCategoryIdQueryDto);
            }
            if (Objects.nonNull(programPageListDto.getStartDateTime()) && 
                    Objects.nonNull(programPageListDto.getEndDateTime())) {
                EsDataQueryDto showDayTimeQueryDto = new EsDataQueryDto();
                showDayTimeQueryDto.setParamName(ProgramDocumentParamName.SHOW_DAY_TIME);
                showDayTimeQueryDto.setStartTime(programPageListDto.getStartDateTime());
                showDayTimeQueryDto.setEndTime(programPageListDto.getEndDateTime());
                esDataQueryDtoList.add(showDayTimeQueryDto);
            }
            
            ProgramPageOrder programPageOrder = getProgramPageOrder(programPageListDto);
            
            PageInfo<ProgramListVo> programListVoPageInfo = businessEsHandle.queryPage(
                    SpringUtil.getPrefixDistinctionName() + "-" + ProgramDocumentParamName.INDEX_NAME,
                    ProgramDocumentParamName.INDEX_TYPE, esDataQueryDtoList, programPageOrder.sortParam, 
                    programPageOrder.sortOrder, programPageListDto.getPageNumber(), programPageListDto.getPageSize(), 
                    ProgramListVo.class);
            pageVo = PageUtil.convertPage(programListVoPageInfo, programListVo -> programListVo);
        }catch (Exception e) {
            log.error("selectPage error",e);
        }
        return pageVo;
    }
    
    public ProgramPageOrder getProgramPageOrder(ProgramPageListDto programPageListDto){
        ProgramPageOrder programPageOrder = new ProgramPageOrder();
        switch (programPageListDto.getType()) {
            //推荐排序
            case 2:
                programPageOrder.sortParam = ProgramDocumentParamName.HIGH_HEAT;
                programPageOrder.sortOrder = SortOrder.DESC;
                break;
            //最近开场    
            case 3:
                programPageOrder.sortParam = ProgramDocumentParamName.SHOW_TIME;
                programPageOrder.sortOrder = SortOrder.ASC;
                break;
            //最新上架    
            case 4:
                programPageOrder.sortParam = ProgramDocumentParamName.ISSUE_TIME;
                programPageOrder.sortOrder = SortOrder.ASC;
                break;
            //相关度排序    
            default:
                programPageOrder.sortParam = null;
                programPageOrder.sortOrder = null;
        }
        return programPageOrder;
    }
    
    public PageVo<ProgramListVo> search(ProgramSearchDto programSearchDto) throws IOException {
        PageVo<ProgramListVo> pageVo = new PageVo<>();
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.must(QueryBuilders.termQuery(
                ProgramDocumentParamName.PROGRAM_STATUS, BusinessStatus.YES.getCode()));
        if (Objects.nonNull(programSearchDto.getAreaId())) {
            QueryBuilder builds = QueryBuilders.termQuery(ProgramDocumentParamName.AREA_ID, programSearchDto.getAreaId());
            boolQuery.must(builds);
        }
        if (Objects.nonNull(programSearchDto.getParentProgramCategoryId())) {
            QueryBuilder builds = QueryBuilders.termQuery(ProgramDocumentParamName.PARENT_PROGRAM_CATEGORY_ID, programSearchDto.getParentProgramCategoryId());
            boolQuery.must(builds);
        }
        if (Objects.nonNull(programSearchDto.getStartDateTime()) &&
                Objects.nonNull(programSearchDto.getEndDateTime())) {
            QueryBuilder builds = QueryBuilders.rangeQuery(ProgramDocumentParamName.SHOW_DAY_TIME)
                    .from(programSearchDto.getStartDateTime()).to(programSearchDto.getEndDateTime()).includeLower(true);
            boolQuery.must(builds);
        }
        if (StringUtil.isNotEmpty(programSearchDto.getContent())) {
            BoolQueryBuilder innerBoolQuery = QueryBuilders.boolQuery();
            innerBoolQuery.should(QueryBuilders.matchQuery(ProgramDocumentParamName.TITLE, programSearchDto.getContent()));
            innerBoolQuery.should(QueryBuilders.matchQuery(ProgramDocumentParamName.ACTOR, programSearchDto.getContent()));
            innerBoolQuery.minimumShouldMatch(1);
            boolQuery.must(innerBoolQuery);
        }

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        ProgramPageOrder programPageOrder = getProgramPageOrder(programSearchDto);
        if (Objects.nonNull(programPageOrder.sortParam) && Objects.nonNull(programPageOrder.sortOrder)) {
            FieldSortBuilder sort = SortBuilders.fieldSort(programPageOrder.sortParam);
            sort.order(programPageOrder.sortOrder);
            searchSourceBuilder.sort(sort);
        }
        searchSourceBuilder.query(boolQuery);
        searchSourceBuilder.trackTotalHits(true);
        searchSourceBuilder.from((programSearchDto.getPageNumber() - 1) * programSearchDto.getPageSize());
        searchSourceBuilder.size(programSearchDto.getPageSize());
        searchSourceBuilder.highlighter(getHighlightBuilder(Arrays.asList(ProgramDocumentParamName.TITLE,
                ProgramDocumentParamName.ACTOR)));
        List<ProgramListVo> list = new ArrayList<>();
        PageInfo<ProgramListVo> pageInfo = new PageInfo<>(list);
        pageInfo.setPageNum(programSearchDto.getPageNumber());
        pageInfo.setPageSize(programSearchDto.getPageSize());
        businessEsHandle.executeQuery(SpringUtil.getPrefixDistinctionName() + "-" + ProgramDocumentParamName.INDEX_NAME,
                ProgramDocumentParamName.INDEX_TYPE,list,pageInfo,ProgramListVo.class,
                searchSourceBuilder,Arrays.asList(ProgramDocumentParamName.TITLE,ProgramDocumentParamName.ACTOR));
        pageVo = PageUtil.convertPage(pageInfo,programListVo -> programListVo);
        return pageVo;
    }
    
    public HighlightBuilder getHighlightBuilder(List<String> fieldNameList){
        // 创建一个HighlightBuilder
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        if (fieldNameList == null || fieldNameList.isEmpty()) {
            return highlightBuilder;
        }
        for (String fieldName : fieldNameList) {
            if (StringUtil.isEmpty(fieldName)) {
                continue;
            }
            // 为特定字段添加高亮设置
            HighlightBuilder.Field highlightTitle = new HighlightBuilder.Field(fieldName);
            highlightTitle.preTags("<em>");
            highlightTitle.postTags("</em>");
            highlightBuilder.field(highlightTitle);
        }
        return highlightBuilder;
    }
    
    public void deleteByProgramId(Long programId){
        try {
            List<EsDataQueryDto> esDataQueryDtoList = new ArrayList<>();
            EsDataQueryDto programIdDto = new EsDataQueryDto();
            programIdDto.setParamName(ProgramDocumentParamName.ID);
            programIdDto.setParamValue(programId);
            esDataQueryDtoList.add(programIdDto);
            
            List<ProgramListVo> programListVos = 
                    businessEsHandle.query(
                            SpringUtil.getPrefixDistinctionName() + "-" + ProgramDocumentParamName.INDEX_NAME, 
                            ProgramDocumentParamName.INDEX_TYPE, 
                            esDataQueryDtoList, 
                            ProgramListVo.class);
            if (CollectionUtil.isNotEmpty(programListVos)) {
                for (ProgramListVo programListVo : programListVos) {
                    String indexName = SpringUtil.getPrefixDistinctionName() + "-" + ProgramDocumentParamName.INDEX_NAME;
                    Map<String, Object> updateFields = new HashMap<>();
                    updateFields.put(ProgramDocumentParamName.PROGRAM_STATUS, 0);
                    businessEsHandle.updateDocumentFields(indexName, programListVo.getEsId(), updateFields);
                }
            }
        }catch (Exception e) {
            log.error("deleteByProgramId error",e);
        }
    }

    public void addDocument(Long programId) {
        try {
            ProgramVo programVo = programService.getDetailFromDb(programId);
            List<Long> ids = java.util.Collections.singletonList(programId);
            Map<Long, TicketCategoryAggregate> ticketCategorieMap = programService.selectTicketCategorieMap(ids);

            Map<String,Object> map = new HashMap<>(32);
            map.put(ProgramDocumentParamName.ID,programVo.getId());
            map.put(ProgramDocumentParamName.PROGRAM_GROUP_ID,programVo.getProgramGroupId());
            map.put(ProgramDocumentParamName.PRIME,programVo.getPrime());
            map.put(ProgramDocumentParamName.TITLE,programVo.getTitle());
            map.put(ProgramDocumentParamName.ACTOR,programVo.getActor());
            map.put(ProgramDocumentParamName.PLACE,programVo.getPlace());
            map.put(ProgramDocumentParamName.ITEM_PICTURE,programVo.getItemPicture());
            map.put(ProgramDocumentParamName.AREA_ID,programVo.getAreaId());
            map.put(ProgramDocumentParamName.AREA_NAME,programVo.getAreaName());
            map.put(ProgramDocumentParamName.PROGRAM_CATEGORY_ID,programVo.getProgramCategoryId());
            map.put(ProgramDocumentParamName.PROGRAM_CATEGORY_NAME,programVo.getProgramCategoryName());
            map.put(ProgramDocumentParamName.PARENT_PROGRAM_CATEGORY_ID,programVo.getParentProgramCategoryId());
            map.put(ProgramDocumentParamName.PARENT_PROGRAM_CATEGORY_NAME,programVo.getParentProgramCategoryName());
            map.put(ProgramDocumentParamName.HIGH_HEAT,programVo.getHighHeat());
            map.put(ProgramDocumentParamName.ISSUE_TIME,programVo.getIssueTime());
            map.put(ProgramDocumentParamName.SHOW_TIME, programVo.getShowTime());
            map.put(ProgramDocumentParamName.SHOW_DAY_TIME,programVo.getShowDayTime());
            map.put(ProgramDocumentParamName.SHOW_WEEK_TIME,programVo.getShowWeekTime());
            map.put(ProgramDocumentParamName.MIN_PRICE,
                    Optional.ofNullable(ticketCategorieMap.get(programVo.getId()))
                            .map(TicketCategoryAggregate::getMinPrice).orElse(null));
            map.put(ProgramDocumentParamName.MAX_PRICE,
                    Optional.ofNullable(ticketCategorieMap.get(programVo.getId()))
                            .map(TicketCategoryAggregate::getMaxPrice).orElse(null));
            map.put(ProgramDocumentParamName.PROGRAM_STATUS, BusinessStatus.YES.getCode());

            String indexName = SpringUtil.getPrefixDistinctionName() + "-" + ProgramDocumentParamName.INDEX_NAME;
            businessEsHandle.add(indexName, ProgramDocumentParamName.INDEX_TYPE, map);
            log.info("ES addDocument success, programId: {}", programId);
        } catch (Exception e) {
            log.error("ES addDocument error, programId: {}", programId, e);
        }
    }
}
