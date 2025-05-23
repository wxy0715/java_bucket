package com.wxy.shardingjdbc.model;

import java.util.ArrayList;
import java.util.List;

public class TestExample {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table test
     *
     * @mbg.generated Thu Jan 05 10:52:28 CST 2023
     */
    protected String orderByClause;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table test
     *
     * @mbg.generated Thu Jan 05 10:52:28 CST 2023
     */
    protected boolean distinct;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table test
     *
     * @mbg.generated Thu Jan 05 10:52:28 CST 2023
     */
    protected List<Criteria> oredCriteria;

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table test
     *
     * @mbg.generated Thu Jan 05 10:52:28 CST 2023
     */
    public TestExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table test
     *
     * @mbg.generated Thu Jan 05 10:52:28 CST 2023
     */
    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table test
     *
     * @mbg.generated Thu Jan 05 10:52:28 CST 2023
     */
    public String getOrderByClause() {
        return orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table test
     *
     * @mbg.generated Thu Jan 05 10:52:28 CST 2023
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table test
     *
     * @mbg.generated Thu Jan 05 10:52:28 CST 2023
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table test
     *
     * @mbg.generated Thu Jan 05 10:52:28 CST 2023
     */
    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table test
     *
     * @mbg.generated Thu Jan 05 10:52:28 CST 2023
     */
    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table test
     *
     * @mbg.generated Thu Jan 05 10:52:28 CST 2023
     */
    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table test
     *
     * @mbg.generated Thu Jan 05 10:52:28 CST 2023
     */
    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table test
     *
     * @mbg.generated Thu Jan 05 10:52:28 CST 2023
     */
    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table test
     *
     * @mbg.generated Thu Jan 05 10:52:28 CST 2023
     */
    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table test
     *
     * @mbg.generated Thu Jan 05 10:52:28 CST 2023
     */
    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Integer value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Integer value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Integer value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Integer value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Integer value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Integer> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Integer> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Integer value1, Integer value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Integer value1, Integer value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andNameIsNull() {
            addCriterion("name is null");
            return (Criteria) this;
        }

        public Criteria andNameIsNotNull() {
            addCriterion("name is not null");
            return (Criteria) this;
        }

        public Criteria andNameEqualTo(String value) {
            addCriterion("name =", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotEqualTo(String value) {
            addCriterion("name <>", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameGreaterThan(String value) {
            addCriterion("name >", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameGreaterThanOrEqualTo(String value) {
            addCriterion("name >=", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameLessThan(String value) {
            addCriterion("name <", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameLessThanOrEqualTo(String value) {
            addCriterion("name <=", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameLike(String value) {
            addCriterion("name like", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotLike(String value) {
            addCriterion("name not like", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameIn(List<String> values) {
            addCriterion("name in", values, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotIn(List<String> values) {
            addCriterion("name not in", values, "name");
            return (Criteria) this;
        }

        public Criteria andNameBetween(String value1, String value2) {
            addCriterion("name between", value1, value2, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotBetween(String value1, String value2) {
            addCriterion("name not between", value1, value2, "name");
            return (Criteria) this;
        }

        public Criteria andNamePlainIsNull() {
            addCriterion("name_plain is null");
            return (Criteria) this;
        }

        public Criteria andNamePlainIsNotNull() {
            addCriterion("name_plain is not null");
            return (Criteria) this;
        }

        public Criteria andNamePlainEqualTo(String value) {
            addCriterion("name_plain =", value, "namePlain");
            return (Criteria) this;
        }

        public Criteria andNamePlainNotEqualTo(String value) {
            addCriterion("name_plain <>", value, "namePlain");
            return (Criteria) this;
        }

        public Criteria andNamePlainGreaterThan(String value) {
            addCriterion("name_plain >", value, "namePlain");
            return (Criteria) this;
        }

        public Criteria andNamePlainGreaterThanOrEqualTo(String value) {
            addCriterion("name_plain >=", value, "namePlain");
            return (Criteria) this;
        }

        public Criteria andNamePlainLessThan(String value) {
            addCriterion("name_plain <", value, "namePlain");
            return (Criteria) this;
        }

        public Criteria andNamePlainLessThanOrEqualTo(String value) {
            addCriterion("name_plain <=", value, "namePlain");
            return (Criteria) this;
        }

        public Criteria andNamePlainLike(String value) {
            addCriterion("name_plain like", value, "namePlain");
            return (Criteria) this;
        }

        public Criteria andNamePlainNotLike(String value) {
            addCriterion("name_plain not like", value, "namePlain");
            return (Criteria) this;
        }

        public Criteria andNamePlainIn(List<String> values) {
            addCriterion("name_plain in", values, "namePlain");
            return (Criteria) this;
        }

        public Criteria andNamePlainNotIn(List<String> values) {
            addCriterion("name_plain not in", values, "namePlain");
            return (Criteria) this;
        }

        public Criteria andNamePlainBetween(String value1, String value2) {
            addCriterion("name_plain between", value1, value2, "namePlain");
            return (Criteria) this;
        }

        public Criteria andNamePlainNotBetween(String value1, String value2) {
            addCriterion("name_plain not between", value1, value2, "namePlain");
            return (Criteria) this;
        }

        public Criteria andNameEncryptIsNull() {
            addCriterion("name_encrypt is null");
            return (Criteria) this;
        }

        public Criteria andNameEncryptIsNotNull() {
            addCriterion("name_encrypt is not null");
            return (Criteria) this;
        }

        public Criteria andNameEncryptEqualTo(String value) {
            addCriterion("name_encrypt =", value, "nameEncrypt");
            return (Criteria) this;
        }

        public Criteria andNameEncryptNotEqualTo(String value) {
            addCriterion("name_encrypt <>", value, "nameEncrypt");
            return (Criteria) this;
        }

        public Criteria andNameEncryptGreaterThan(String value) {
            addCriterion("name_encrypt >", value, "nameEncrypt");
            return (Criteria) this;
        }

        public Criteria andNameEncryptGreaterThanOrEqualTo(String value) {
            addCriterion("name_encrypt >=", value, "nameEncrypt");
            return (Criteria) this;
        }

        public Criteria andNameEncryptLessThan(String value) {
            addCriterion("name_encrypt <", value, "nameEncrypt");
            return (Criteria) this;
        }

        public Criteria andNameEncryptLessThanOrEqualTo(String value) {
            addCriterion("name_encrypt <=", value, "nameEncrypt");
            return (Criteria) this;
        }

        public Criteria andNameEncryptLike(String value) {
            addCriterion("name_encrypt like", value, "nameEncrypt");
            return (Criteria) this;
        }

        public Criteria andNameEncryptNotLike(String value) {
            addCriterion("name_encrypt not like", value, "nameEncrypt");
            return (Criteria) this;
        }

        public Criteria andNameEncryptIn(List<String> values) {
            addCriterion("name_encrypt in", values, "nameEncrypt");
            return (Criteria) this;
        }

        public Criteria andNameEncryptNotIn(List<String> values) {
            addCriterion("name_encrypt not in", values, "nameEncrypt");
            return (Criteria) this;
        }

        public Criteria andNameEncryptBetween(String value1, String value2) {
            addCriterion("name_encrypt between", value1, value2, "nameEncrypt");
            return (Criteria) this;
        }

        public Criteria andNameEncryptNotBetween(String value1, String value2) {
            addCriterion("name_encrypt not between", value1, value2, "nameEncrypt");
            return (Criteria) this;
        }

        public Criteria andShadowIsNull() {
            addCriterion("shadow is null");
            return (Criteria) this;
        }

        public Criteria andShadowIsNotNull() {
            addCriterion("shadow is not null");
            return (Criteria) this;
        }

        public Criteria andShadowEqualTo(Integer value) {
            addCriterion("shadow =", value, "shadow");
            return (Criteria) this;
        }

        public Criteria andShadowNotEqualTo(Byte value) {
            addCriterion("shadow <>", value, "shadow");
            return (Criteria) this;
        }

        public Criteria andShadowGreaterThan(Byte value) {
            addCriterion("shadow >", value, "shadow");
            return (Criteria) this;
        }

        public Criteria andShadowGreaterThanOrEqualTo(Byte value) {
            addCriterion("shadow >=", value, "shadow");
            return (Criteria) this;
        }

        public Criteria andShadowLessThan(Byte value) {
            addCriterion("shadow <", value, "shadow");
            return (Criteria) this;
        }

        public Criteria andShadowLessThanOrEqualTo(Byte value) {
            addCriterion("shadow <=", value, "shadow");
            return (Criteria) this;
        }

        public Criteria andShadowIn(List<Byte> values) {
            addCriterion("shadow in", values, "shadow");
            return (Criteria) this;
        }

        public Criteria andShadowNotIn(List<Byte> values) {
            addCriterion("shadow not in", values, "shadow");
            return (Criteria) this;
        }

        public Criteria andShadowBetween(Byte value1, Byte value2) {
            addCriterion("shadow between", value1, value2, "shadow");
            return (Criteria) this;
        }

        public Criteria andShadowNotBetween(Byte value1, Byte value2) {
            addCriterion("shadow not between", value1, value2, "shadow");
            return (Criteria) this;
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table test
     *
     * @mbg.generated do_not_delete_during_merge Thu Jan 05 10:52:28 CST 2023
     */
    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table test
     *
     * @mbg.generated Thu Jan 05 10:52:28 CST 2023
     */
    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}