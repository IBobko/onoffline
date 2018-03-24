<%--suppress XmlDuplicatedId --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<style type="text/css">
    td {
        padding:10px;
        font-weight: normal;
    }
</style>
<h3 style="color:#337ab7;font-weight:bold">Смена пароля</h3>

<c:if test="${passwordSaved != null}">
    <p class="bg-success" style="padding:10px">Пароль успешно изменен</p>
</c:if>

<form:form method="post" modelAttribute="changePasswordForm">
    <table>
        <tr>
            <td>
                Введите старый пароль
            </td>
            <td>
                <form:password cssClass="form-control" path="oldPassword"/>
            </td>
            <td>
                <form:errors path="oldPassword"/>
            </td>
        </tr>
        <tr>
            <td>
                Ваш новый пароль
            </td>
            <td>
                <form:password cssClass="form-control" path="newPassword"/>
            </td>
            <td>
                <form:errors path="newPassword"/>
            </td>
        </tr>
        <tr>
            <td>
                Подтвердите новый пароль
            </td>
            <td>
                <form:password cssClass="form-control" path="repeatedPassword"/>
            </td>
            <td>
                <form:errors path="repeatedPassword"/>
            </td>
        </tr>
        <tr>
            <td colspan="2">
                <input type="submit" value="Сменить пароль" class="std-button btn btn-default"/>
            </td><td></td>
        </tr>
    </table>
</form:form>





