<%--suppress XmlDuplicatedId --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<style type="text/css">
    #page-content-wrapper {
        font-weight: normal;
    }
    table td {
        padding:10px;
    }

    .trips li{
        border-radius: 40px;
        background-color: #e4e7f7;
        height: 36px;
        padding: 10px
    }
</style>

<h4 style="color:#337ab7;font-weight:bold">Добавить путешествия</h4>

<form:form modelAttribute="tripForm" method="post">
    <table>
        <tr>
            <td>Страна путешествия</td>
            <td>
                <form:select path="country" cssClass="form-control" htmlEscape="true" cssErrorClass="form-control error">
                    <form:option value="">не указано</form:option>
                    <c:forEach items="${countries}" var="country">
                        <form:option value="${country.code}">${country.name}</form:option>
                    </c:forEach>
                </form:select>
            </td>
            <td><form:errors cssClass="text-danger" path="country"/></td>
        </tr>
        <tr>
            <td>Город / Поселение</td>
            <td><form:input path="city" cssClass="form-control" cssErrorClass="form-control error"/></td>
            <td><form:errors cssClass="text-danger" path="city"/> </td>
        </tr>
        <tr>
            <td>Год путешествия</td>
            <td>
                <div style="width:224px" class="input-group date form_date_trip col-md-5" data-date="" data-date-format="mm/yyyy" data-link-format="yyyy-mm">
                    <form:input style="background-color:white" type="text" path="year" cssErrorClass="form-control error" class="form-control" name="m" size="16" readonly="true"/>
                    <span class="input-group-addon"><span class="glyphicon glyphicon-remove"></span></span>
                    <span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>
                </div>
            <td><form:errors cssClass="text-danger" path="year"/></td>
        </tr>
        <tr>
            <td colspan="=2">
                <a class="std-button btn btn-default" onclick="$('#tripForm').submit()">Сохранить</a>
            </td>
            <td></td>
        </tr>
    </table>
</form:form>
<br/><br/>
<h4 style="color:#337ab7;font-weight:bold">Мои путешествия</h4>


<div class="trips">
    <ul class="list-inline">
        <c:forEach items="${trips}" var="trip">
            <li><span onclick="document.location='<c:url value="/settings/trips/remove?trip=${trip.id}"/>';"><span class="glyphicon glyphicon-remove-circle"></span></span>&nbsp;<a href="#">${trip.country}'${trip.year}</a></li>
        </c:forEach>
    </ul>
</div>

<script type="text/javascript">
    $('.form_date_trip').datetimepicker({
        language:  'ru',
        weekStart: 1,
        todayBtn:  1,
        autoclose: 1,
        todayHighlight: 1,
        startView: 4,
        minView: 3,
        forceParse: 0
    });
</script>