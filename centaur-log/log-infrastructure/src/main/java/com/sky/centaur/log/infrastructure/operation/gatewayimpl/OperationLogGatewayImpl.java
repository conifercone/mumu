/*
 * Copyright (c) 2024-2024, kaiyu.shan@outlook.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sky.centaur.log.infrastructure.operation.gatewayimpl;

import static com.sky.centaur.basis.constants.CommonConstants.ES_QUERY_EN;
import static com.sky.centaur.basis.constants.CommonConstants.ES_QUERY_SP;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sky.centaur.basis.exception.DataConversionException;
import com.sky.centaur.basis.kotlin.tools.BeanUtil;
import com.sky.centaur.basis.kotlin.tools.CommonUtil;
import com.sky.centaur.log.domain.operation.OperationLog;
import com.sky.centaur.log.domain.operation.gateway.OperationLogGateway;
import com.sky.centaur.log.infrastructure.operation.convertor.OperationLogConvertor;
import com.sky.centaur.log.infrastructure.operation.gatewayimpl.elasticsearch.OperationLogEsRepository;
import com.sky.centaur.log.infrastructure.operation.gatewayimpl.elasticsearch.dataobject.OperationLogEsDo;
import com.sky.centaur.log.infrastructure.operation.gatewayimpl.kafka.OperationLogKafkaRepository;
import com.sky.centaur.unique.client.api.PrimaryKeyGrpcService;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Component;

/**
 * 操作日志领域网关实现
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
@Component
public class OperationLogGatewayImpl implements OperationLogGateway {

  private final OperationLogKafkaRepository operationLogKafkaRepository;
  private final OperationLogEsRepository operationLogEsRepository;
  private final ObjectMapper objectMapper;
  private final PrimaryKeyGrpcService primaryKeyGrpcService;
  private final ElasticsearchTemplate elasticsearchTemplate;

  @Autowired
  public OperationLogGatewayImpl(OperationLogKafkaRepository operationLogKafkaRepository,
      OperationLogEsRepository operationLogEsRepository, ObjectMapper objectMapper,
      PrimaryKeyGrpcService primaryKeyGrpcService, ElasticsearchTemplate elasticsearchTemplate) {
    this.operationLogKafkaRepository = operationLogKafkaRepository;
    this.operationLogEsRepository = operationLogEsRepository;
    this.objectMapper = objectMapper;
    this.primaryKeyGrpcService = primaryKeyGrpcService;
    this.elasticsearchTemplate = elasticsearchTemplate;
  }

  @Override
  public void submit(OperationLog operationLog) {
    OperationLogConvertor.toKafkaDataObject(operationLog).ifPresent(res -> {
      try {
        operationLogKafkaRepository.send("operation-log", objectMapper.writeValueAsString(
            res));
      } catch (JsonProcessingException e) {
        throw new DataConversionException();
      }
    });
  }

  @Override
  public void save(OperationLog operationLog) {
    OperationLogConvertor.toEsDataObject(operationLog).ifPresent(operationLogEsRepository::save);
  }

  @Override
  public Optional<OperationLog> findOperationLogById(String id) {
    Optional<OperationLog> optionalOperationLog = operationLogEsRepository.findById(
        id).flatMap(OperationLogConvertor::toEntity);
    OperationLog operationLog = new OperationLog();
    operationLog.setId(String.valueOf(primaryKeyGrpcService.snowflake()));
    operationLog.setBizNo(id);
    operationLog.setContent("根据日志ID获取操作日志");
    operationLog.setOperatingTime(LocalDateTime.now(ZoneId.of("UTC")));
    optionalOperationLog.ifPresentOrElse(op -> operationLog.setSuccess(op.toString()),
        () -> operationLog.setFail(String.format("未找到ID为%s的操作日志", id)));
    this.submit(operationLog);
    return optionalOperationLog;
  }

  @Override
  @SuppressWarnings("DuplicatedCode")
  public Page<OperationLog> findAll(OperationLog operationLog, int pageNo, int pageSize) {
    PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
    Criteria criteria = new Criteria();
    Optional.ofNullable(operationLog).ifPresent(optLog -> {
      Optional.ofNullable(optLog.getId())
          .ifPresent(id -> criteria.and(
              new Criteria(BeanUtil.getPropertyName(OperationLogEsDo::getId)).matches(id)));
      Optional.ofNullable(optLog.getContent())
          .ifPresent(content -> {
            String propertyName = BeanUtil.getPropertyName(OperationLogEsDo::getContent);
            criteria.and(
                new Criteria(propertyName).matches(content).or(propertyName.concat(ES_QUERY_EN))
                    .matches(content).or(propertyName.concat(ES_QUERY_SP))
                    .matches(content));
          });
      Optional.ofNullable(optLog.getOperator())
          .ifPresent(operator -> {
            String propertyName = BeanUtil.getPropertyName(OperationLogEsDo::getOperator);
            criteria.and(
                new Criteria(propertyName).matches(operator).or(propertyName.concat(ES_QUERY_EN))
                    .matches(operator).or(propertyName.concat(ES_QUERY_SP))
                    .matches(operator));
          });
      Optional.ofNullable(optLog.getBizNo())
          .ifPresent(bizNo -> criteria.and(
              new Criteria(BeanUtil.getPropertyName(OperationLogEsDo::getBizNo)).matches(bizNo)));
      Optional.ofNullable(optLog.getCategory())
          .ifPresent(category -> criteria.and(
              new Criteria(BeanUtil.getPropertyName(OperationLogEsDo::getCategory)).matches(
                  category)));
      Optional.ofNullable(optLog.getDetail())
          .ifPresent(detail -> {
            String propertyName = BeanUtil.getPropertyName(OperationLogEsDo::getDetail);
            criteria.and(
                new Criteria(propertyName).matches(detail).or(propertyName.concat(ES_QUERY_EN))
                    .matches(detail).or(propertyName.concat(ES_QUERY_SP))
                    .matches(detail));
          });
      Optional.ofNullable(optLog.getSuccess())
          .ifPresent(success -> {
            String propertyName = BeanUtil.getPropertyName(OperationLogEsDo::getSuccess);
            criteria.and(
                new Criteria(propertyName).matches(success).or(propertyName.concat(ES_QUERY_EN))
                    .matches(success).or(propertyName.concat(ES_QUERY_SP))
                    .matches(success));
          });
      Optional.ofNullable(optLog.getFail())
          .ifPresent(fail -> {
            String propertyName = BeanUtil.getPropertyName(OperationLogEsDo::getFail);
            criteria.and(
                new Criteria(propertyName).matches(fail).or(propertyName.concat(ES_QUERY_EN))
                    .matches(fail).or(propertyName.concat(ES_QUERY_SP))
                    .matches(fail));
          });

      Optional.ofNullable(optLog.getOperatingTime())
          .ifPresent(
              operatingTime -> criteria.and(new Criteria(
                  BeanUtil.getPropertyName(OperationLogEsDo::getOperatingTime)).matches(
                  CommonUtil.convertAccountZoneToUTC(operatingTime))));
      Optional.ofNullable(optLog.getOperatingStartTime())
          .ifPresent(
              operatingStartTime -> criteria.and(
                  new Criteria(
                      BeanUtil.getPropertyName(OperationLogEsDo::getOperatingTime)).greaterThan(
                      CommonUtil.convertAccountZoneToUTC(operatingStartTime))));
      Optional.ofNullable(optLog.getOperatingEndTime())
          .ifPresent(
              operatingEndTime -> criteria.and(
                  new Criteria(
                      BeanUtil.getPropertyName(OperationLogEsDo::getOperatingTime)).lessThan(
                      CommonUtil.convertAccountZoneToUTC(operatingEndTime))));
    });
    Query query = new CriteriaQuery(criteria).setPageable(pageRequest)
        .addSort(
            Sort.by(BeanUtil.getPropertyName(OperationLogEsDo::getOperatingTime)).descending());
    SearchHits<OperationLogEsDo> searchHits = elasticsearchTemplate.search(query,
        OperationLogEsDo.class);
    List<OperationLog> operationLogs = searchHits.getSearchHits().stream()
        .map(SearchHit::getContent).map(res -> OperationLogConvertor.toEntity(res).orElse(null))
        .filter(
            Objects::nonNull)
        .peek(operationLogDomain ->
            operationLogDomain.setOperatingTime(
                CommonUtil.convertUTCToAccountZone(operationLogDomain.getOperatingTime())))
        .toList();
    return new PageImpl<>(operationLogs, pageRequest, searchHits.getTotalHits());
  }
}
