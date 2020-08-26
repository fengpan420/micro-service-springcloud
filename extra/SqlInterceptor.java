package org.example;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;


@Intercepts({
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})}
)
public class SqlInterceptor implements Interceptor {
    private static ThreadLocal<SimpleDateFormat> dateTimeFormatter = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        Object result = null;
        //捕获掉异常，不要影响业务
        try {
            MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
            Object parameter = null;
            if (invocation.getArgs().length > 1) {
                parameter = invocation.getArgs()[1];
            }
            String sqlId = mappedStatement.getId();
            BoundSql boundSql = mappedStatement.getBoundSql(parameter);
            Configuration configuration = mappedStatement.getConfiguration();

            long startTime = System.currentTimeMillis();

            try {
                result = invocation.proceed();
            } finally {
                long endTime = System.currentTimeMillis();
                long sqlCostTime = endTime - startTime;
                String sql = this.getSql(configuration, boundSql);
                this.formatSqlLog(sqlId, sql, sqlCostTime, result);
            }
            return result;

        } catch (Exception e) {
            return result;
        }
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties properties) {
    }

    /**
     * 获取完整的sql语句
     *
     * @param configuration
     * @param boundSql
     * @return
     */
    private String getSql(Configuration configuration, BoundSql boundSql) {
        // 输入sql字符串空判断
        String sql = boundSql.getSql();
        if (StringUtils.isEmpty(sql)) {
            return "";
        }
        return formatSql(sql, configuration, boundSql);
    }

    /**
     * 将占位符替换成参数值
     *
     * @param sql
     * @param configuration
     * @param boundSql
     * @return
     */
    private String formatSql(String sql, Configuration configuration, BoundSql boundSql) {

        //美化sql
        sql = beautifySql(sql);

        //填充占位符, 目前基本不用mybatis存储过程调用,故此处不做考虑
        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();

        List<String> parameters = new ArrayList<>();
        if (parameterMappings != null) {
            MetaObject metaObject = parameterObject == null ? null : configuration.newMetaObject(parameterObject);
            for (int i = 0; i < parameterMappings.size(); i++) {
                ParameterMapping parameterMapping = parameterMappings.get(i);
                if (parameterMapping.getMode() != ParameterMode.OUT) {
                    //  参数值
                    Object value;
                    String propertyName = parameterMapping.getProperty();
                    //  获取参数名称
                    if (boundSql.hasAdditionalParameter(propertyName)) {
                        // 获取参数值
                        value = boundSql.getAdditionalParameter(propertyName);
                    } else if (parameterObject == null) {
                        value = null;
                    } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                        // 如果是单个值则直接赋值
                        value = parameterObject;
                    } else {
                        value = metaObject == null ? null : metaObject.getValue(propertyName);
                    }

                    if (value instanceof Number) {
                        parameters.add(String.valueOf(value));
                    } else {
                        StringBuilder builder = new StringBuilder();
                        builder.append("'");
                        if (value instanceof Date) {
                            builder.append(dateTimeFormatter.get().format((Date) value));
                        } else if (value instanceof String) {
                            builder.append(value);
                        }
                        builder.append("'");
                        parameters.add(builder.toString());
                    }
                }
            }
        }

        for (String value : parameters) {
            sql = sql.replaceFirst("\\?", value);
        }
        return sql;
    }


    /**
     * 格式化sql日志
     *
     * @param sqlId
     * @param sql
     * @param costTime
     * @return
     */
    private void formatSqlLog(String sqlId, String sql, long costTime, Object obj) {
        String sqlLog = "==> " + sql;
        StringBuffer result = new StringBuffer();
        if (obj instanceof List) {
            List list = (List) obj;
            int count = list.size();
            result.append("<==      Total: " + count);
        } else if (obj instanceof Integer) {
            result.append("<==      Total: " + obj);
        }

        result.append("      Spend Time ==> " + costTime + " ms");
//        Logger log = LoggerFactory.getLogger(sqlId);
//        log.info(sqlLog);
//        log.info(result.toString());
        System.out.println(sqlLog);
        System.out.println(result.toString());
    }


    public static String beautifySql(String sql) {
        sql = sql.replaceAll("[\\s\n ]+", " ");
        return sql;
    }

}
